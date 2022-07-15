package org.grits.toolbox.tools.gsl.util.test;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.grits.toolbox.ms.file.reader.impl.MzXmlReader;
import org.grits.toolbox.ms.om.data.Peak;
import org.grits.toolbox.ms.om.data.Scan;

public class TestMzXmlReaderUtils {

	public static void main(String[] args) {
//		String t_strScanFilename = TestResourcePath.MZXML2_PATH;
		String t_strScanFilename = TestResourcePath.MZXML_GGMIX_HCD_PATH;

//		List<Scan> t_lScans = MSDataModelFileHandlar.readNumberedScansFromMzXML(t_strScanFilename);
//		List<Scan> t_lScans = MSDataModelFileHandlar.readAllScansFromMzXML(t_strScanFilename);
		MzXmlReader t_mzXMLParser = new MzXmlReader();
		List<Scan> t_lScans = t_mzXMLParser.readMzXmlFileForDirectInfusion(t_strScanFilename);

		Map<Integer, Integer> t_mapMSLevelToNum = new TreeMap<>();
		for ( Scan t_scan : t_lScans ) {
			int i=0;
			while (true) {
				i++;
				if ( i > 10 )
					break;
				if ( i != t_scan.getMsLevel() )
					continue;
				int t_nMSLevel = 0;
				if ( t_mapMSLevelToNum.containsKey(i) )
					t_nMSLevel = t_mapMSLevelToNum.get(i);
				t_mapMSLevelToNum.put(i, t_nMSLevel+1);
				break;
			}

			System.out.println("Scan#"+t_scan.getScanNo()+": MS"+t_scan.getMsLevel());
			System.out.println("Activation method: "+t_scan.getActivationMethode());
			System.out.println("#AnnotPeaks: "+t_scan.getNumAnnotatedPeaks());
			System.out.println("#PeaksTotal: "+t_scan.getTotalNumPeaks());
			System.out.println("#Peaks: "+t_scan.getPeaklist().size());
			if ( t_scan.getMsLevel() == 1 ) {
				for ( Peak t_peak : t_scan.getPeaklist() ) {
					System.out.println("  "+t_peak);
					System.out.println("  Original m/z: "+t_peak.getPrecursorMz()+" Int.: "+t_peak.getPrecursorIntensity());
				}
			}
			if ( t_scan.getPrecursor() != null ) {
				System.out.println("Precursor:");
				Peak t_peakPrecur = t_scan.getPrecursor();
				System.out.println("  "+t_peakPrecur);
				System.out.println("  Original m/z: "+t_peakPrecur.getPrecursorMz()+" Int.: "+t_peakPrecur.getPrecursorIntensity());
			}
			if ( t_scan.getSubScans().isEmpty() )
				continue;
			System.out.print("Subscan no: ");
			for ( Integer t_iSubNo : t_scan.getSubScans() )
				System.out.print(t_iSubNo+" ");
			System.out.println("");

			//			t_glAnnot.annotateMS2Scan(t_scan);
//			List<GlycolipidAnnotation> t_lMatchedGLAnnots = t_glAnnot.getMatchedGlycolipidAnnotations();
//			List<GlycolipidFeature> t_lMatechedGLFeatures = t_glAnnot.getMatchedFeatures();
		}
		for ( Integer t_iMSLevel : t_mapMSLevelToNum.keySet() )
			System.out.println("MS"+t_iMSLevel+":"+t_mapMSLevelToNum.get(t_iMSLevel));
	}

}
