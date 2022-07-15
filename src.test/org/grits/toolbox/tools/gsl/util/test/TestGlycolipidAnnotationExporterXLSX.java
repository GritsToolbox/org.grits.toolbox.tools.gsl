package org.grits.toolbox.tools.gsl.util.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.grits.toolbox.ms.file.reader.impl.MzXmlReader;
import org.grits.toolbox.ms.om.data.Annotation;
import org.grits.toolbox.ms.om.data.Feature;
import org.grits.toolbox.ms.om.data.GlycanAnnotation;
import org.grits.toolbox.ms.om.data.GlycanFeature;
import org.grits.toolbox.ms.om.data.GlycolipidAnnotation;
import org.grits.toolbox.ms.om.data.GlycolipidFeature;
import org.grits.toolbox.ms.om.data.LipidFeature;
import org.grits.toolbox.ms.om.data.Method;
import org.grits.toolbox.ms.om.data.Scan;
import org.grits.toolbox.tools.gsl.dango.GlycolipidAnnotationObjectProvider;
import org.grits.toolbox.tools.gsl.dango.GlycolipidMSAnnotator;
import org.grits.toolbox.tools.gsl.dango.IonCombinationGenerator;
import org.grits.toolbox.tools.gsl.dango.ScanReader;
import org.grits.toolbox.tools.gsl.structure.IonCombination;
import org.grits.toolbox.tools.gsl.util.io.glycolipid.GlycolipidAnnotationExporterXLSX;
import org.grits.toolbox.tools.gsl.util.io.glycolipid.data.AnnotationReport;
import org.grits.toolbox.tools.gsl.util.io.glycolipid.data.GlycolipidAnnotationReport;
import org.grits.toolbox.tools.gsl.util.io.glycolipid.data.GlycolipidAnnotationReportGenerator;
import org.grits.toolbox.tools.gsl.util.io.om.MSDataModelFileHandlar;

public class TestGlycolipidAnnotationExporterXLSX {

	private static Map<Integer, Scan> mapNumToScan = new HashMap<>();

	private static boolean bFilterByKeyFragment = true;
//	private static boolean bFilterBySubScan = false;
	private static boolean bExoprtCartoon = false;
	private static boolean bContainsIntactGlycan = true;
	private static int nAnnotationPerSheet = -1;
	private static int nMS2ScanPerBook = 4;

	public static void main(String[] args) {

		long t_timeStartTotal = System.currentTimeMillis();

		// For Method data
//		String t_strMethodFilepath = TestResourcePath.METHOD_PATH;
		String t_strMethodFilepath = TestResourcePath.METHOD_GGMIX_PATH;
//		String t_strMethodFilepath = TestResourcePath.METHOD_GGMIX_HIGHACC_PATH;
//		String t_strMethodFilepath = TestResourcePath.METHOD_GGMIX_LOWACC_PATH;
		// For Scan data
//		String t_strScanFilename = TestResourcePath.MZXML2;
//		String t_strScanFilename = TestResourcePath.MZXML_GGMIX_CID;
//		String t_strScanFilename = TestResourcePath.MZXML_GGMIX_HCD;
//		String t_strScanFilename = TestResourcePath.MZXML_SERUMGSL;
//		String t_strScanFilename = "Serum_GSL_700_2000.mzXML";
		String t_strScanFilename = "AN_HPL_ms2_1371_peak file.mzXML";
		String t_strScanFilepath = TestResourcePath.getResourcePath(t_strScanFilename);
		// For result data
		String t_strXLSXFilepath = TestResourcePath.getResourcePath("GlycolipidAnnotation_"+t_strScanFilename);

//		List<Scan> t_lScans = MSDataModelFileHandlar.readNumberedScansFromMzXML(t_strScanFilepath);
		List<Scan> t_lScans = (new MzXmlReader()).readMzXmlFileForDirectInfusion(t_strScanFilepath);
		// Map scan to ID
		for ( Scan t_scan : t_lScans )
			mapNumToScan.put(t_scan.getScanNo(), t_scan);
//		List<Scan> t_lScans = MSDataModelFileHandlar.readAllScansFromMzXML(t_strScanFilename);
		Method t_method = MSDataModelFileHandlar.readMethodXML(t_strMethodFilepath);

		long t_longAnnotTime = 0;
		long t_longGenReportTime = 0;
		long t_longExportTime = 0;

		if ( bFilterByKeyFragment )
			t_strXLSXFilepath += "-filtered";
		try {
			// Generate candidate structure for annotations
			GlycolipidAnnotationObjectProvider t_glAnnotProv = new GlycolipidAnnotationObjectProvider();
			t_glAnnotProv.createAnnotations(t_method);
			List<GlycolipidAnnotation> t_lGLAnnotations = t_glAnnotProv.getGlycolipidAnnotations();
			List<GlycanAnnotation> t_lGAnnotations = t_glAnnotProv.getGlycanAnnotations();
			// Map ID to GlycolipidAnnotation
			Map<Integer, GlycolipidAnnotation> t_mapIDToGLAnnotation = new HashMap<>();
			for ( GlycolipidAnnotation t_annot : t_lGLAnnotations )
				t_mapIDToGLAnnotation.put(t_annot.getId(), t_annot);

			// Generate ion combinations
			IonCombinationGenerator t_genIonCombo = new IonCombinationGenerator(t_method);
			t_genIonCombo.generate();
			List<IonCombination> t_lIonCombinations = t_genIonCombo.getPossibleIonCombinations();

			// Construct GlycolipidMSAnnotator
			GlycolipidMSAnnotator t_glAnnotor = new GlycolipidMSAnnotator(
					t_method.getTrustMzCharge(),
					t_method.getMonoisotopic(),
					t_method.getAnalyteSettings().get(0).getGlycanSettings()
				);
			// For filtering by key feature
			t_glAnnotor.setFilterByKeyFeature(bFilterByKeyFragment);
			// Set GlycolipidAnnotations as candidate Annotations
			for ( Annotation t_annotCandidate : t_lGLAnnotations )
				t_glAnnotor.addCandidateAnnotation(t_annotCandidate);
			if ( bContainsIntactGlycan )
				// Set GlycanAnnotations as candidate Annotations
				for ( Annotation t_annotCandidate : t_lGAnnotations )
					t_glAnnotor.addCandidateAnnotation(t_annotCandidate);
			// Set ion combos for precursor and fragment ions
			t_glAnnotor.setIonCombinationsForPrecursor(t_lIonCombinations);
			t_glAnnotor.setIonCombinationsForFragments(t_lIonCombinations);

			List<Scan> t_lMS2Scans = new ArrayList<>();

			int t_nMS2Scan = 0;
			for ( Scan t_scan : t_lScans ) {
				if ( t_scan.getMsLevel() != 2 )
					continue;
				t_nMS2Scan++;

				t_lMS2Scans.add(t_scan);
				// Output each three scans
				if ( t_lMS2Scans.size() < nMS2ScanPerBook )
					continue;

				// Annotate scans
				long[] t_longTimes = annotateGlycolipidToScans(t_strXLSXFilepath, t_lMS2Scans, t_method, t_glAnnotor);
				// Add times
				t_longAnnotTime += t_longTimes[0];
				t_longGenReportTime += t_longTimes[1];
				t_longExportTime += t_longTimes[2];

				t_lMS2Scans.clear();
			}

			if ( !t_lMS2Scans.isEmpty() ) {
				// Annotate scans
				long[] t_longTimes = annotateGlycolipidToScans(t_strXLSXFilepath, t_lMS2Scans, t_method, t_glAnnotor);
				// Add times
				t_longAnnotTime += t_longTimes[0];
				t_longGenReportTime += t_longTimes[1];
				t_longExportTime += t_longTimes[2];

				t_lMS2Scans.clear();
			}

			System.out.println( "\nTotal number of MS2 scans: ");
		} catch (IOException e) {
			e.printStackTrace();
		}

		long t_timeEndTotal = System.currentTimeMillis();

		System.out.println( "\nTotal annotation time: "+t_longAnnotTime );
		System.out.println( "Total excel export time: "+t_longExportTime );
		System.out.println( "Total report generation time: "+t_longGenReportTime );
		System.out.println( "\nTotal calculation time: "+(t_timeEndTotal-t_timeStartTotal) );
	}

	private static long[] annotateGlycolipidToScans(String a_strXLSXFilename, List<Scan> a_lScans, Method a_method, GlycolipidMSAnnotator a_glAnnotor) throws IOException {
		String t_strScanNo = "";
		for ( Scan t_scan : a_lScans ) {
			if ( !t_strScanNo.isEmpty() )
				t_strScanNo += ",";
			t_strScanNo += t_scan.getScanNo();
			for ( Integer t_iSubNo : t_scan.getSubScans() )
				t_strScanNo += ","+t_iSubNo;
		}
		GlycolipidAnnotationExporterXLSX t_export
		= new GlycolipidAnnotationExporterXLSX(a_strXLSXFilename+"-Scan#"+t_strScanNo+".xlsx");
		// Set flag for whether glycan cartoon will be exported
		t_export.setExportCartoon(bExoprtCartoon);
		t_export.setNumberOfAnnotationPerSheet(nAnnotationPerSheet);

		long t_longAnnotTime = 0;
		long t_longGenReportTime = 0;

		for ( Scan t_scan : a_lScans ) {
			
			System.out.println( "For scan no. "+t_scan.getScanNo() );

			ScanReader t_scanR = wrapScan(t_scan, a_method);

			// Start annotation
			System.out.println( "Start annotation..." );
			long t_startTime = System.currentTimeMillis();

			List<Feature> t_lFeatures = a_glAnnotor.annotateMS1Structures(t_scanR);

			long t_endTime = System.currentTimeMillis();
			System.out.println( "Matched structures: "+t_lFeatures.size() );
			int t_nFragments = countFragments(t_lFeatures);
			System.out.println( "Matched fragments: "+t_nFragments );
			System.out.println( "Annotation time: "+(t_endTime-t_startTime) );
			t_longAnnotTime += t_endTime-t_startTime;

			// Start exchange
			System.out.println( "Start exchange data..." );
			t_startTime = System.currentTimeMillis();

			GlycolipidAnnotationReportGenerator t_reportGen = new GlycolipidAnnotationReportGenerator(a_glAnnotor.getCandidateAnnotations());
			GlycolipidAnnotationReport t_report = t_reportGen.generateGlycolipidReport(t_scanR, t_lFeatures);

			t_endTime = System.currentTimeMillis();
			System.out.println( "\nReport generation time: "+(t_endTime-t_startTime) );
			t_longGenReportTime += t_endTime-t_startTime;

			// Add report
			t_export.addReport(t_report);

			/// For sub scans
			// Annotate subscans and add calculation times
			long[] t_longSubTimes = annotateSubScans(t_scan, a_method, a_glAnnotor, t_reportGen, t_report);
			t_longAnnotTime += t_longSubTimes[0];
			t_longGenReportTime += t_longSubTimes[1];
/*
			if ( t_scanR.getScan().getSubScans() == null || t_scanR.getScan().getSubScans().isEmpty() )
				continue;

			for ( Integer t_iScanNo : t_scanR.getScan().getSubScans() ) {
				System.out.println( "For sub scan no. "+t_iScanNo );

				ScanReader t_scanRSub = wrapScan( mapNumToScan.get(t_iScanNo), a_method );

				// Annotation
				System.out.println( "Start annotation..." );
				t_startTime = System.currentTimeMillis();

//				List<Feature> t_lAnnotatedFeatures = a_glAnnotor.annotateSubScan(t_scanR, t_scanRSub);
				List<Feature> t_lAnnotatedFeatures = a_glAnnotor.annotateSubScan(t_scanRSub);

				t_endTime = System.currentTimeMillis();
				System.out.println( "Matched structures: "+t_lAnnotatedFeatures.size() );
				t_nFragments = countFragments(t_lAnnotatedFeatures);
				System.out.println( "Matched fragments: "+t_nFragments );
				System.out.println( "Annotation time: "+(t_endTime-t_startTime) );
				t_longAnnotTime += t_endTime-t_startTime;

				// Exchange
				System.out.println( "Start exchange data..." );
				t_startTime = System.currentTimeMillis();

				AnnotationReport t_reportSub = t_reportGen.generateSubScanReport(t_scanRSub, t_lAnnotatedFeatures);

				t_endTime = System.currentTimeMillis();
				System.out.println( "\nReport generation time: "+(t_endTime-t_startTime) );
				t_longGenReportTime += t_endTime-t_startTime;

				t_report.addSubScanReport(t_reportSub);
			}
*/
		}
		// Export excel file
		long t_longExportTime = export(t_export);
		System.out.println( "\nExport time: "+(t_longExportTime)+"\n\n" );

		long[] t_longTimes = {t_longAnnotTime, t_longGenReportTime, t_longExportTime};
		return t_longTimes;
	}

	private static long[] annotateSubScans(Scan a_scanParent, Method a_method, GlycolipidMSAnnotator a_glAnnotor,
			GlycolipidAnnotationReportGenerator a_reportGen, GlycolipidAnnotationReport a_report) {

		// Return if no subscan
		if ( a_scanParent.getSubScans() == null || a_scanParent.getSubScans().isEmpty() )
			return new long[]{0,0};

		long t_longAnnotTime = 0;
		long t_longGenReportTime = 0;

		for ( Integer t_iScanNo : a_scanParent.getSubScans() ) {
			System.out.println( "For sub scan no. "+t_iScanNo );

			ScanReader t_scanRSub = wrapScan( mapNumToScan.get(t_iScanNo), a_method );

			// Annotation
			System.out.println( "Start annotation..." );
			long t_startTime = System.currentTimeMillis();

//			List<Feature> t_lAnnotatedFeatures = a_glAnnotor.annotateSubScan(t_scanR, t_scanRSub);
			List<Feature> t_lAnnotatedFeatures = a_glAnnotor.annotateSubScan(t_scanRSub);

			long t_endTime = System.currentTimeMillis();
			System.out.println( "Matched structures: "+t_lAnnotatedFeatures.size() );
			int t_nFragments = countFragments(t_lAnnotatedFeatures);
			System.out.println( "Matched fragments: "+t_nFragments );
			System.out.println( "Annotation time: "+(t_endTime-t_startTime) );
			t_longAnnotTime += t_endTime-t_startTime;

			// Exchange
			System.out.println( "Start exchange data..." );
			t_startTime = System.currentTimeMillis();

			AnnotationReport t_reportSub = a_reportGen.generateSubScanReport(t_scanRSub, t_lAnnotatedFeatures);

			t_endTime = System.currentTimeMillis();
			System.out.println( "\nReport generation time: "+(t_endTime-t_startTime) );
			t_longGenReportTime += t_endTime-t_startTime;

			a_report.addSubScanReport(t_reportSub);

			// Annotate subscans and add calculation times
			long[] t_longSubTimes = annotateSubScans(t_scanRSub.getScan(), a_method, a_glAnnotor, a_reportGen, a_report);
			t_longAnnotTime += t_longSubTimes[0];
			t_longGenReportTime += t_longSubTimes[1];
		}

		return new long[]{t_longAnnotTime, t_longGenReportTime};
	}

	private static ScanReader wrapScan(Scan a_scan, Method a_method) {
		// Create ScanReader and set parameters
		ScanReader t_scanR = new ScanReader(a_scan);
//		ScanReader t_scanR = ScanReader.getInstanceWithSubScanInfo(t_strScanFilename, t_scan.getScanNo());
//		if ( t_scanR == null )
//			return null;
		t_scanR.setShift(a_method.getShift());
		t_scanR.setAccuracy(a_method.getAccuracy(), a_method.getAccuracyPpm());
		t_scanR.setFragmentAccuracy(a_method.getFragAccuracy(), a_method.getFragAccuracyPpm());
		t_scanR.setIntencityCutoff(a_method.getIntensityCutoff(), a_method.getIntensityCutoffType());
		return t_scanR;
	}

	private static int countFragments(List<Feature> a_lParentFeatures) {
		int t_nFragments = 0;
		for ( Feature t_feature : a_lParentFeatures ) {
			if ( t_feature instanceof GlycolipidFeature) {
				t_nFragments += ((GlycolipidFeature)t_feature).getGlycolipidFragments().size();
				t_nFragments += ((GlycolipidFeature)t_feature).getGlycanFragments().size();
				t_nFragments += ((GlycolipidFeature)t_feature).getLipidFragments().size();
			}
			if ( t_feature instanceof GlycanFeature)
				t_nFragments += ((GlycanFeature)t_feature).getGlycanFragment().size();
			if ( t_feature instanceof LipidFeature)
				t_nFragments += ((LipidFeature)t_feature).getLipidFragments().size();
		}
		return t_nFragments;
	}

	private static long export(GlycolipidAnnotationExporterXLSX a_export) throws FileNotFoundException, IOException {
		System.out.println( "Start excel export..." );
		long t_startTime = System.currentTimeMillis();
		a_export.createBook();
		a_export.write();
		a_export.closeBook();
		long t_endTime = System.currentTimeMillis();
		return t_endTime-t_startTime;
	}
}
