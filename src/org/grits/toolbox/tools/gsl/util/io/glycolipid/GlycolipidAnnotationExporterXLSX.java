package org.grits.toolbox.tools.gsl.util.io.glycolipid;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.grits.toolbox.ms.om.data.Annotation;
import org.grits.toolbox.ms.om.data.GlycanAnnotation;
import org.grits.toolbox.ms.om.data.GlycolipidAnnotation;
import org.grits.toolbox.ms.om.data.LipidAnnotation;
import org.grits.toolbox.ms.om.data.Peak;
import org.grits.toolbox.tools.gsl.util.io.excel.ExporterXLSXAbstract;
import org.grits.toolbox.tools.gsl.util.io.glycolipid.data.AnnotatedIon;
import org.grits.toolbox.tools.gsl.util.io.glycolipid.data.AnnotatedStructure;
import org.grits.toolbox.tools.gsl.util.io.glycolipid.data.AnnotationInformationUnit;
import org.grits.toolbox.tools.gsl.util.io.glycolipid.data.AnnotationReport;
import org.grits.toolbox.tools.gsl.util.io.glycolipid.data.GlycolipidAnnotationReport;
import org.grits.toolbox.tools.gsl.util.io.glycolipid.data.GlycolipidStructure;
import org.grits.toolbox.utils.io.ExcelWriterHelper;

/**
 * Class for exporting glycolipid annotation results as XLSX file.
 * <i>Currently, we don't use this class because we are using only Grits table view and it also has excel exporter.
 * This was temporally used.</i>
 * @author Masaaki Matsubara
 *
 */
public class GlycolipidAnnotationExporterXLSX extends ExporterXLSXAbstract {

	private static final Logger logger = Logger.getLogger(GlycolipidAnnotationExporterXLSX.class);

	private List<GlycolipidAnnotationReport> m_lReports;
	private boolean m_bExportCartoon;
	private int m_nAnnotPerSheet;

	private ExcelWriterHelper m_helper = new ExcelWriterHelper();
	private Map<String, BufferedImage> m_mapGWBSequenceToImage;
	private List<Picture> m_lPictures;

	private static List<String> lBlankAnnot = Arrays.asList("", "", "", "", "");
	private static List<String> lBlankHead = Arrays.asList("", "", "", "");


	public GlycolipidAnnotationExporterXLSX(String a_strOutputFile) throws IOException {
		super(a_strOutputFile);

		this.m_lReports = new ArrayList<>();
		this.m_bExportCartoon = false;
		this.m_nAnnotPerSheet = -1;

		this.m_mapGWBSequenceToImage = new HashMap<>();
		this.m_lPictures = new ArrayList<>();
	}

	public void addReport(GlycolipidAnnotationReport a_report) {
		this.m_lReports.add(a_report);
	}

	public void setExportCartoon(boolean a_bExport) {
		this.m_bExportCartoon = a_bExport;
	}

	public void setNumberOfAnnotationPerSheet(int a_nAnnot) {
		this.m_nAnnotPerSheet = a_nAnnot;
	}

	@Override
	public boolean createBook() {
		for ( GlycolipidAnnotationReport t_report : this.m_lReports ) {

			// Summary
			if ( !this.createSheetForSummary(t_report) )
				return false;

			// Detailed summary
			if ( !this.createSheetForDetailedSummary(t_report) )
				return false;

			// Compositions
			if ( !this.createSheetForComposition(t_report) )
				return false;

			// Annotation result
			if ( !this.createSheetForScan(t_report) )
				return false;

			if ( t_report.getSubScanReports().isEmpty() )
				continue;

			// For subscans
			for ( AnnotationReport t_reportSub : t_report.getSubScanReports() ) {

				// Summary
				if ( !this.createSheetForSubScanSummary(t_reportSub) )
					return false;

				// Annotation result
				if ( !this.createSheetForScan(t_reportSub) )
					return false;
			}
		}

		return true;
	}

	protected boolean createSheetForSummary(GlycolipidAnnotationReport a_report) {
		// Create scan sheet naming as scan number and precurser m/z
		this.m_sheetCurent = this.createSheet(a_report.getReportName()+"-Summary");

		logger.debug("Create sheet: "+this.m_sheetCurent.getSheetName());

		// Title
		this.createRow(Arrays.asList( "", "Annotated structures", "", "Ions", "m/z", "deltaM (ppm)", "Count Score", "Intensity Score" ));
		// Precursor
//		String t_strCharge = a_report.getPrecursorPeak().getCharge().toString();
		String t_strCharge = a_report.getPrecursorPeak().getPrecursorCharge().toString();
		if ( !t_strCharge.startsWith("-") )
			t_strCharge = "+"+t_strCharge;
//		String t_strMz = a_report.getPrecursorPeak().getMz().toString();
		String t_strMz = a_report.getPrecursorPeak().getPrecursorMz().toString();
		this.createRow(Arrays.asList( "Precursor", "", "", t_strCharge, t_strMz, "--", "--", "--" ));
		// Annotated structures
		int i=0;
		for ( AnnotatedStructure t_as : this.getSortedAnnotatedStructures(a_report, true) ) {
			i++;
			GlycolipidStructure t_gl = (GlycolipidStructure)t_as.getStructure();
			AnnotationInformationUnit t_unit = a_report.getPrecursorAnnotationUnit(t_as.getID());
			AnnotatedIon t_info = this.sortInformationByDeviation(t_unit).get(0);
			this.createRow(Arrays.asList( ""+i,
					t_gl.getGlycanSequence(), t_gl.getLipidName(),
					t_info.getIon(), t_info.getMass(), t_info.getDeviation(),
					t_as.getCountScore(), t_as.getIntensityScore()
				));
		}
		return true;
	}

	protected boolean createSheetForDetailedSummary(final GlycolipidAnnotationReport a_report) {
		// Create scan sheet naming as scan number and precursor m/z
		this.m_sheetCurent = this.createSheet(a_report.getReportName()+"-Detail");

		logger.debug("Create sheet: "+this.m_sheetCurent.getSheetName());

		// Title
		this.createRow(Arrays.asList( "", "Annotated structures", "", "Ions", "m/z", "deltaM (ppm)", "Count Score", "Intensity Score" ));
		// Precursor
//		String t_strCharge = a_report.getPrecursorPeak().getCharge().toString();
		String t_strCharge = a_report.getPrecursorPeak().getPrecursorCharge().toString();
		if ( !t_strCharge.startsWith("-") )
			t_strCharge = "+"+t_strCharge;
//		String t_strMz = a_report.getPrecursorPeak().getMz().toString();
		String t_strMz = a_report.getPrecursorPeak().getPrecursorMz().toString();
		this.createRow(Arrays.asList( "Precursor", "Glycan", "Lipid", t_strCharge, t_strMz, "--", "--", "--" ));
		// Annotated structures
		int i=0;
		for ( AnnotatedStructure t_as : this.getSortedAnnotatedStructures(a_report, true) ) {
			i++;
			// Create composition row
			GlycolipidStructure t_gl = (GlycolipidStructure)t_as.getStructure();
			AnnotationInformationUnit t_unit = a_report.getPrecursorAnnotationUnit(t_as.getID());
			AnnotatedIon t_info = this.sortInformationByDeviation(t_unit).get(0);
			this.createRow(Arrays.asList( ""+i, t_gl.getGlycanSequence(), t_gl.getLipidName() ));

			// Sort glycolipid annotations by higher score order
			List<Annotation> t_lAnnots = a_report.getAnnotations(t_as);
			Collections.sort(t_lAnnots, new Comparator<Annotation>() {
				@Override
				public int compare(Annotation o1, Annotation o2) {
					Double t_dIScore1 = a_report.getIntensityScore(o1);
					Double t_dIScore2 = a_report.getIntensityScore(o2);
					if ( t_dIScore1 < t_dIScore2 ) return -1;
					if ( t_dIScore1 > t_dIScore2 ) return 1;
					Double t_dCScore1 = a_report.getCountingScore(o1);
					Double t_dCScore2 = a_report.getCountingScore(o2);
					if ( t_dCScore1 < t_dCScore2 ) return -1;
					if ( t_dCScore1 > t_dCScore2 ) return 1;
					return 0;
				}
			});
			Collections.reverse(t_lAnnots);
			// Create exact structures row
			for ( Annotation t_annot : t_lAnnots ) {
				Double t_dCScore = a_report.getCountingScore(t_annot);
				Double t_dIScore = a_report.getIntensityScore(t_annot);
				String t_strGSeq = "";
				String t_strLName = "";
				if ( t_annot instanceof GlycolipidAnnotation ) {
					GlycolipidAnnotation t_glAnnot = (GlycolipidAnnotation)t_annot;
					t_strGSeq = t_glAnnot.getGlycanAnnotation().getSequenceGWB();
					t_strLName = t_glAnnot.getLipidAnnotation().getSequence();
				}
				if ( t_annot instanceof GlycanAnnotation )
					t_strGSeq = ((GlycanAnnotation)t_annot).getSequenceGWB();
				if ( t_annot instanceof LipidAnnotation )
					t_strLName = ((LipidAnnotation)t_annot).getSequence();

				this.createRow(Arrays.asList( "", t_strGSeq, t_strLName,
						t_info.getIon(), t_info.getMass(), t_info.getDeviation(),
						t_dCScore.toString(), t_dIScore.toString()
					));
				this.convertGWBSequenceToImage(this.m_sheetCurent, this.m_nRowCurrent-1, 1);
			}
		}
		return true;
	}

	protected boolean createSheetForComposition(GlycolipidAnnotationReport a_report) {
		// Create scan sheet naming as scan number and precursor m/z
		this.m_sheetCurent = this.createSheet(a_report.getReportName()+"-Composition");

		logger.debug("Create sheet: "+this.m_sheetCurent.getSheetName());

		// List glycan compositions and corresponding sequence
		this.createRow(Arrays.asList( "Glycan composition", "Glycan strcutures" ));
		for ( String t_strGCompo : a_report.getGlycanCompositions() ) {
			List<String> t_lGCmpRow = new ArrayList<>();
			List<String> t_lGImgRow = new ArrayList<>();
			t_lGCmpRow.add(t_strGCompo);
			t_lGImgRow.add("");
			for ( GlycanAnnotation t_gAnnot : a_report.getGlycanAnnotations(t_strGCompo) ) {
				t_lGCmpRow.add( t_gAnnot.getStringId() );
				t_lGImgRow.add( t_gAnnot.getSequenceGWB() );
			}
			this.createRow(t_lGCmpRow);
			this.createRow(t_lGImgRow);
			// Create images
			for ( int i=1; i<t_lGImgRow.size(); i++ )
				this.convertGWBSequenceToImage(this.m_sheetCurent, this.m_nRowCurrent-1, i);
		}

		// List lipid compositions to corresponding name
		this.createRow(Arrays.asList("Lipid composition", "Lipid structure"));
		for ( String t_strLCompo : a_report.getLipidCompositions() ) {
			List<String> t_lLCmpRow = new ArrayList<>();
			t_lLCmpRow.add(t_strLCompo);
			for ( LipidAnnotation t_lAnnot : a_report.getLipidAnnotations(t_strLCompo) )
				t_lLCmpRow.add( t_lAnnot.getSequence() );
			this.createRow(t_lLCmpRow);
		}

		return true;
	}

	protected boolean createSheetForScan(AnnotationReport a_report) {
		// If no separation
		if ( this.m_nAnnotPerSheet == -1 ) {
			if ( !this.createSheetForScan(a_report, this.getSortedAnnotatedStructures(a_report, true), 1, 1) )
				return false;
			return true;
		}

		// Calculate number of sheets for the scan
		int t_nAnnotation = a_report.getAnnotatedStructures().size();
		int t_nSheet = t_nAnnotation/this.m_nAnnotPerSheet;
		if ( t_nAnnotation % this.m_nAnnotPerSheet > 0 )
			t_nSheet++;

		// Separate sheet by the specified number of AnnotatedStructures
		List<AnnotatedStructure> t_lAS = new ArrayList<>();
		// Sort annotated structures by deviation value (smaller comes first)
		int t_iSheet = 1;
		for ( AnnotatedStructure t_as : this.getSortedAnnotatedStructures(a_report, true) ) {
			t_lAS.add(t_as);
			if ( this.m_nAnnotPerSheet == -1 || t_lAS.size() < this.m_nAnnotPerSheet )
				continue;
			if ( !this.createSheetForScan(a_report, t_lAS, t_iSheet++, t_nSheet) )
				return false;
			t_lAS.clear();
		}
		if ( !t_lAS.isEmpty() && !this.createSheetForScan(a_report, t_lAS, t_iSheet++, t_nSheet) )
			return false;
		return false;
	}

	protected boolean createSheetForScan(AnnotationReport a_report, List<AnnotatedStructure> a_lAS, int t_iSheet, int t_nSheet) {
		// Create scan sheet naming as scan number and precursor m/z
		String t_strSheetName = a_report.getReportName();
		if ( t_nSheet != 1 )
			t_strSheetName += "-"+t_iSheet+"/"+t_nSheet;
		this.m_sheetCurent = this.createSheet(t_strSheetName);

		logger.debug("Create sheet: "+this.m_sheetCurent.getSheetName());

		// For headers
		List<String> t_lFirstRow = new ArrayList<>();
		List<String> t_lSecondRow = new ArrayList<>();
		t_lFirstRow.addAll( Arrays.asList( "No.", "m/z", "Intensity", "" ) );
		t_lSecondRow.addAll( Arrays.asList( "", "", "Absolute", "Relative" ) );
		List<String> t_lFirstAnnotRow = Arrays.asList( "AnnotatedStructure", "", "Ions", "m/z", "deltaM (ppm)" );
		List<String> t_lSecondAnnotRow = Arrays.asList( "Glycan", "Lipid", "", "", "" );
		for ( int i=0; i<a_lAS.size(); i++ ) {
			t_lFirstRow.addAll( t_lFirstAnnotRow );
			t_lSecondRow.addAll( t_lSecondAnnotRow );
		}
		this.createRow(t_lFirstRow);
		this.createRow(t_lSecondRow);
		// Merge "Intensity" cells
		this.m_sheetCurent.addMergedRegion( new CellRangeAddress(0,0,2,3) );
		// Merge "AnnotatedStructure" cells
		for (int i=0; i<a_lAS.size(); i++)
			this.m_sheetCurent.addMergedRegion( new CellRangeAddress(0,0,4+i*5,5+i*5) );

/*
		// For composition row
		List<String> t_lCompositionRow = new ArrayList<>();
		for ( AnnotatedStructure t_as : t_lAS ) {
			GlycolipidStructure t_gls = (GlycolipidStructure)t_as.getStructure();
			t_lCompositionRow.addAll( Arrays.asList( t_gls.getGlycanSequence(), t_gls.getLipidName(), "", "", "" ) );
		}
		this.createRow(t_lCompositionRow);
*/
		// For precursor
		Peak t_peakPre = a_report.getPrecursorPeak();
		List<String> t_lPreRow = new ArrayList<>();
		t_lPreRow.addAll( this.getPeakArray("Precursor", t_peakPre) );
		for ( AnnotatedStructure t_as : a_lAS ) {
			List<String> t_lPreArray = new ArrayList<>();
			GlycolipidStructure t_glStructure = (GlycolipidStructure)t_as.getStructure();
			t_lPreArray.add(t_glStructure.getGlycanSequence());
			t_lPreArray.add(t_glStructure.getLipidName());
			AnnotationInformationUnit t_annotUnit = a_report.getPrecursorAnnotationUnit( t_as.getID() );
			AnnotatedIon t_info = t_annotUnit.getAnnotationInfo().get(0);
			t_lPreArray.add(t_info.getIon());
			t_lPreArray.add(t_info.getMass());
			t_lPreArray.add(t_info.getDeviation());
			t_lPreRow.addAll(t_lPreArray);
		}
		this.createRow( t_lPreRow );
		// Convert sequence to image
		for ( int j=0; j<a_lAS.size(); j++ )
			this.convertGWBSequenceToImage(this.m_sheetCurent, this.m_nRowCurrent-1, 4+j*5);

		for ( Peak t_peak : a_report.getPeaks() ) {

			if ( !this.createRowsForPeak(t_peak, a_report, a_lAS) )
				return false;
		}

		// Auto size for all column
//		this.autoSizeAllColumn(this.m_sheetCurent);

		// Freeze panes
		this.m_sheetCurent.createFreezePane(4, 3);

		return true;
	}

//	protected boolean createRowsForPeak(Peak t_peak, List<GlycolipidAnnotation> t_lSortedAnnots) {
	protected boolean createRowsForPeak(Peak a_peak, AnnotationReport a_report, List<AnnotatedStructure> a_lAS) {
		logger.info(a_peak.toString());
		// If peak has no matched feature
		if ( a_peak.getFeatures().isEmpty() ) {
			// Output peak information
			List<String> t_lRow = new ArrayList<>();
			t_lRow.addAll( this.getPeakArray(a_peak.getId().toString(), a_peak) );
			this.createRow(t_lRow);
			return true;
		}

		// Collect annotation units with same peak and merge same informations
		int t_nMaxInfoCount = 0;
		List<List<List<String>>> t_lUnitRows = new ArrayList<>();
		for ( AnnotatedStructure t_as : a_lAS ) {
			List<List<String>> t_lInfoRows = new ArrayList<>();
			AnnotationInformationUnit t_unit = a_report.getAnnotationInformationUnit(a_peak.getId(), t_as.getID());
			if ( t_unit == null ) {
				t_lUnitRows.add(t_lInfoRows);
				continue;
			}
			// Convert annotated structure information to array list after sort by deviation
			for ( AnnotatedIon t_info : this.sortInformationByDeviation(t_unit) ) {
				List<String> t_lInfoRow = new ArrayList<>();
				GlycolipidStructure t_glSt = (GlycolipidStructure)t_info.getAnnotatedStructure();
				t_lInfoRow.add( t_glSt.getGlycanSequence() );
				t_lInfoRow.add( t_glSt.getLipidName() );
				t_lInfoRow.add( t_info.getIon() );
				t_lInfoRow.add( t_info.getMass() );
				t_lInfoRow.add( t_info.getDeviation() );

				// Merge if old information has same ion, mass and deviation
				boolean t_bIsMarged = false;
				for ( List<String> t_lInfoRow0 : t_lInfoRows ) {
					if ( !t_lInfoRow0.get(2).equals(t_lInfoRow.get(2)) ) continue; // Ion
					if ( !t_lInfoRow0.get(3).equals(t_lInfoRow.get(3)) ) continue; // Mass
					if ( !t_lInfoRow0.get(4).equals(t_lInfoRow.get(4)) ) continue; // Deviation
					// Merge glycan
					if ( t_lInfoRow0.get(1).isEmpty() && t_lInfoRow.get(1).isEmpty() ) {
						t_lInfoRow0.set(0, t_lInfoRow0.get(0)+"\n"+t_lInfoRow.get(0) );
						t_bIsMarged = true;
						break;
					}
					// Merge lipid
					if ( t_lInfoRow0.get(0).isEmpty() && t_lInfoRow.get(0).isEmpty() ) {
						t_lInfoRow0.set(1, t_lInfoRow0.get(1)+"\n"+t_lInfoRow.get(1) );
						t_bIsMarged = true;
						break;
					}
					// Merge glycolipid
					if ( !t_lInfoRow0.get(0).isEmpty() && !t_lInfoRow.get(0).isEmpty()
					  && !t_lInfoRow0.get(1).isEmpty() && !t_lInfoRow.get(1).isEmpty() ) {
						// Merge glycan part if there is the same lipid
						if (  t_lInfoRow0.get(1).contains(t_lInfoRow.get(1))
						  && !t_lInfoRow0.get(0).contains(t_lInfoRow.get(0)) ) {
							t_lInfoRow0.set(0, t_lInfoRow0.get(0)+"\n"+t_lInfoRow.get(0) );
							t_bIsMarged = true;
							break;
						}
						// Merge lipid side if there is the same glycan
						if (  t_lInfoRow0.get(0).contains(t_lInfoRow.get(0))
						  && !t_lInfoRow0.get(1).contains(t_lInfoRow.get(1)) ) {
							t_lInfoRow0.set(1, t_lInfoRow0.get(1)+"\n"+t_lInfoRow.get(1) );
							t_bIsMarged = true;
							break;
						}
						// Do nothing if there are the same glycan and lipid
						if (  t_lInfoRow0.get(0).contains(t_lInfoRow.get(0))
						  &&  t_lInfoRow0.get(1).contains(t_lInfoRow.get(1)) ) {
							t_bIsMarged = true;
							break;
						}
					}
				}
				if ( !t_bIsMarged )
					t_lInfoRows.add(t_lInfoRow);
			}
			t_lUnitRows.add(t_lInfoRows);
			if ( t_nMaxInfoCount < t_lInfoRows.size() )
				t_nMaxInfoCount = t_lInfoRows.size();
		}
		// Create rows
		for ( int i=0; i<t_nMaxInfoCount; i++ ) {
			List<String> t_lPeakRow = new ArrayList<>();
			if ( i==0 )
				t_lPeakRow.addAll( this.getPeakArray(a_peak.getId().toString(), a_peak) );
			else
				t_lPeakRow.addAll( lBlankHead );
			for ( List<List<String>> t_lUnitRow : t_lUnitRows ) {
				if ( t_lUnitRow.size() < i+1 )
					t_lPeakRow.addAll(lBlankAnnot);
				else
					t_lPeakRow.addAll(t_lUnitRow.get(i));
			}
			this.createRow(t_lPeakRow);

			// Convert sequence to image
			if ( !this.m_bExportCartoon )
				continue;
			for ( int j=0; j<t_lPeakRow.size(); j++ )
				this.convertGWBSequenceToImage(this.m_sheetCurent, this.m_nRowCurrent-1, 4+j*5);
		}

		return true;
	}

	protected boolean createSheetForSubScanSummary(final AnnotationReport a_reportSub) {
		// Create scan sheet naming as scan number and precursor m/z
		this.m_sheetCurent = this.createSheet(a_reportSub.getReportName()+"-Summary");

		logger.debug("Create sheet: "+this.m_sheetCurent.getSheetName());

		// Title
		this.createRow(Arrays.asList( "", "Annotated Fragments", "", "Ions", "m/z", "deltaM (ppm)", "Count Score", "Intensity Score" ));
		// Precursor
		String t_strCharge = "--";
		Integer t_iCharge = a_reportSub.getPrecursorPeak().getCharge();
		if ( t_iCharge != null && t_iCharge > 0 )
			t_strCharge = "+"+t_iCharge.toString();
		this.createRow(Arrays.asList( "Precursor", "Glycan", "Lipid", t_strCharge, a_reportSub.getPrecursorPeak().getMz().toString(), "--", "--", "--" ));
		// Annotated structures
		int i=0;
		for ( AnnotatedStructure t_as : this.getSortedAnnotatedStructures(a_reportSub, false) ) {
			i++;
			// Create fragment row
			GlycolipidStructure t_gl = (GlycolipidStructure)t_as.getStructure();
			AnnotationInformationUnit t_unit = a_reportSub.getPrecursorAnnotationUnit(t_as.getID());
			AnnotatedIon t_info = this.sortInformationByDeviation(t_unit).get(0);
			this.createRow(Arrays.asList( ""+i,
				t_gl.getGlycanSequence(), t_gl.getLipidName(),
				t_info.getIon(), t_info.getMass(), t_info.getDeviation(),
				t_as.getCountScore(), t_as.getIntensityScore()
			));
			this.convertGWBSequenceToImage(this.m_sheetCurent, this.m_nRowCurrent-1, 1);

			// Show the original structures
/*
			// Get root report
			AnnotationReport t_report = a_reportSub;
			while( t_report.getParentReport() != null )
				t_report = t_report.getParentReport();
			final AnnotationReport t_reportRoot = t_report;

			// Sort glycolipid annotations by higher score order
			List<Annotation> t_lAnnots = new ArrayList<>();
			for ( Feature t_feature : a_reportSub.getPrecursorFeatures(t_as) ) {
				Annotation t_annot = a_reportSub.getAnnotationFromFeature(t_feature);
				t_lAnnots.add(t_annot);
			}
			Collections.sort(t_lAnnots, new Comparator<Annotation>() {
				@Override
				public int compare(Annotation o1, Annotation o2) {
					Double t_dIScore1 = o1.getScores().get(t_reportRoot.getScanNumber()+"int");
					Double t_dIScore2 = o2.getScores().get(t_reportRoot.getScanNumber()+"int");
					if ( t_dIScore1 < t_dIScore2 ) return -1;
					if ( t_dIScore1 > t_dIScore2 ) return 1;
					Double t_dCScore1 = o1.getScores().get(t_reportRoot.getScanNumber()+"cnt");
					Double t_dCScore2 = o2.getScores().get(t_reportRoot.getScanNumber()+"cnt");
					if ( t_dCScore1 < t_dCScore2 ) return -1;
					if ( t_dCScore1 > t_dCScore2 ) return 1;
					return 0;
				}
			});
			Collections.reverse(t_lAnnots);
			// Create exact structures row
			for ( Annotation t_annot : t_lAnnots ) {
				Double t_dCScore = t_annot.getScores().get(t_reportRoot.getScanNumber()+"cnt");
				Double t_dIScore = t_annot.getScores().get(t_reportRoot.getScanNumber()+"int");
				String t_strGSeq = "";
				String t_strLName = "";
				if ( t_annot instanceof GlycolipidAnnotation ) {
					GlycolipidAnnotation t_glAnnot = (GlycolipidAnnotation)t_annot;
					t_strGSeq = t_glAnnot.getGlycanAnnotation().getSequenceGWB();
					t_strLName = t_glAnnot.getLipidAnnotation().getSequence();
				}
				if ( t_annot instanceof GlycanAnnotation )
					t_strGSeq = ((GlycanAnnotation)t_annot).getSequenceGWB();
				if ( t_annot instanceof LipidAnnotation )
					t_strLName = ((LipidAnnotation)t_annot).getSequence();

				this.createRow(Arrays.asList( "", t_strGSeq, t_strLName,
						t_info.getIon(), t_info.getMass(), t_info.getDeviation(),
						t_dCScore.toString(), t_dIScore.toString()
					));
				this.convertGWBSequenceToImage(this.m_sheetCurent, this.m_nRowCurrent-1, 1);
			}
*/
		}
		return true;
	}


	private List<AnnotatedStructure> getSortedAnnotatedStructures(AnnotationReport a_report, boolean a_bPriorDeviation) {
		List<AnnotatedStructure> t_lAS = a_report.getAnnotatedStructures();
		final Map<AnnotatedStructure, String> t_mapASToDev = new HashMap<>();
		for ( AnnotatedStructure t_as : t_lAS ) {
			AnnotationInformationUnit t_annotUnit = a_report.getPrecursorAnnotationUnit( t_as.getID() );
			List<AnnotatedIon> t_lInfo = this.sortInformationByDeviation(t_annotUnit);
			AnnotatedIon t_info = t_lInfo.get(0);
			t_mapASToDev.put(t_as, t_info.getDeviation());
		}
		// Sort by deviation and scores:
		// (Prior deviation = true) deviation -> intensity score -> count score
		// (Prior deviation = false) intensity score -> count score -> deviation
		Collections.sort(t_lAS, new Comparator<AnnotatedStructure>(){
			@Override
			public int compare(AnnotatedStructure o1, AnnotatedStructure o2) {
				if ( a_bPriorDeviation ) {
					// Smaller deviation comes first
					BigDecimal t_dDev1 = new BigDecimal( t_mapASToDev.get(o1) );
					BigDecimal t_dDev2 = new BigDecimal( t_mapASToDev.get(o2) );
					if ( t_dDev1.compareTo(t_dDev2) < 0 ) return -1;
					if ( t_dDev1.compareTo(t_dDev2) > 0 ) return 1;
				}

				// Bigger intensity score comes first
				Double t_dIScore1 = Double.valueOf( o1.getIntensityScore() );
				Double t_dIScore2 = Double.valueOf( o2.getIntensityScore() );
				if ( t_dIScore1 > t_dIScore2 ) return -1;
				if ( t_dIScore1 < t_dIScore2 ) return 1;
				// Bigger count score comes first
				Double t_dCScore1 = Double.valueOf( o1.getCountScore() );
				Double t_dCScore2 = Double.valueOf( o2.getCountScore() );
				if ( t_dCScore1 > t_dCScore2 ) return -1;
				if ( t_dCScore1 < t_dCScore2 ) return 1;

				if ( !a_bPriorDeviation ) {
					// Smaller deviation comes first
					BigDecimal t_dDev1 = new BigDecimal( t_mapASToDev.get(o1) );
					BigDecimal t_dDev2 = new BigDecimal( t_mapASToDev.get(o2) );
					if ( t_dDev1.compareTo(t_dDev2) < 0 ) return -1;
					if ( t_dDev1.compareTo(t_dDev2) > 0 ) return 1;
				}

				return 0;
			}
		});
		return t_lAS;
	}

	private List<AnnotatedIon> sortInformationByDeviation(AnnotationInformationUnit a_unit) {
		List<AnnotatedIon> t_lInfo = a_unit.getAnnotationInfo();
		Collections.sort(t_lInfo, new Comparator<AnnotatedIon>(){
			@Override
			public int compare(AnnotatedIon o1, AnnotatedIon o2) {
				BigDecimal t_dDev1 = new BigDecimal(o1.getDeviation());
				BigDecimal t_dDev2 = new BigDecimal(o2.getDeviation());
				if ( t_dDev1.compareTo(t_dDev2) < 0 ) return -1;
				if ( t_dDev1.compareTo(t_dDev2) > 0 ) return 1;
				return 0;
			}
		});
		return t_lInfo;
	}

	private List<String> getPeakArray(String t_strHead, Peak a_peak) {
		return Arrays.asList(
				t_strHead,
				this.roundDouble(a_peak.getMz(), 5),
				this.roundDouble(a_peak.getIntensity(), 2),
				(a_peak.getRelativeIntensity() == null)? "--" :
					this.roundDouble(a_peak.getRelativeIntensity() * 100, 2 )
			);
	}

	private String roundDouble(double value, int places) {
		return new BigDecimal(value).setScale(places, BigDecimal.ROUND_HALF_UP).toPlainString();
	}

	private void convertGWBSequenceToImage(Sheet a_sheet, int a_iRow, int a_iCol) {
		Cell t_cell = a_sheet.getRow(a_iRow).getCell(a_iCol);
		if ( t_cell == null || t_cell.getStringCellValue().isEmpty() )
			return;
		// Remove text after the image generation
		if ( this.setGWBSequenceImage(a_sheet, a_iRow, a_iCol, t_cell.getStringCellValue()) )
			t_cell.setCellValue("");
	}

	private boolean setGWBSequenceImage(Sheet a_sheet, int a_iRow, int a_iCol, String a_strGWBSeq) {
		try {
			System.out.println("Generate image of "+a_strGWBSeq);
			BufferedImage t_img = this.getGWBSequenceImage(a_strGWBSeq);
			if ( t_img == null )
				return false;
//			System.out.println("Done");
			System.out.println("Add image No."+this.m_lPictures.size()+" to "+a_iRow+","+a_iCol+" in "+a_sheet.getSheetName());
			this.m_helper.writeCellImage(this.m_book, a_sheet, a_iRow, a_iCol, t_img, this.m_lPictures);
//			System.out.println("Done");
		} catch (Exception e) {
			logger.error("An error in converting GWB sequence in the cell to image", e);
			return false;
		}
		return true;
	}

	private BufferedImage getGWBSequenceImage(String a_strGWBSeq) throws Exception {
		if ( this.m_mapGWBSequenceToImage.containsKey(a_strGWBSeq) )
			return this.m_mapGWBSequenceToImage.get(a_strGWBSeq);

		List<String> t_lGWBSeqs = new ArrayList<>();
		if ( a_strGWBSeq.contains("\n") ) {
			for ( String t_strGWBSeq : a_strGWBSeq.split("\n") ) {
				if ( t_strGWBSeq.isEmpty() )
					return null;
				t_lGWBSeqs.add(t_strGWBSeq);
			}
		}
		BufferedImage t_img = this.m_helper.createGlycanImage(t_lGWBSeqs, null, false, true, 0.5d);
		this.m_mapGWBSequenceToImage.put(a_strGWBSeq, t_img);
		return t_img;
	}
}
