package org.grits.toolbox.tools.gsl.util.io.glycolipid.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.grits.toolbox.ms.om.data.Annotation;
import org.grits.toolbox.ms.om.data.Feature;
import org.grits.toolbox.ms.om.data.Peak;

public class AnnotationReport {

	private int m_iScanNo;
	private Peak m_peakPrecursor;
	private List<Peak> m_lPeaks;
	private List<AnnotatedStructure> m_lASs;
	private Map<Feature, Annotation> m_mapFeatureToAnnotation;
	private Map<AnnotatedStructure, List<Feature>> m_mapStructureToFeatures;
	private List<AnnotationInformationUnit> m_lPreAnnotInfoUnits;
	private List<AnnotationInformationUnit> m_lAnnotInfoUnits;
	private AnnotationReport m_reportParentScan;
	private List<AnnotationReport> m_lSubScanReports;

	public AnnotationReport() {
		this.m_iScanNo = -1;
		this.m_peakPrecursor = null;
		this.m_lPeaks = new ArrayList<>();
		this.m_lASs = new ArrayList<>();
		this.m_mapFeatureToAnnotation = new HashMap<>();
		this.m_mapStructureToFeatures = new HashMap<>();
		this.m_lPreAnnotInfoUnits = new ArrayList<>();
		this.m_lAnnotInfoUnits = new ArrayList<>();
		this.m_reportParentScan = null;
		this.m_lSubScanReports = new ArrayList<>();
	}

	public void setScanNumber(int a_iScanNo) {
		this.m_iScanNo = a_iScanNo;
	}

	public int getScanNumber() {
		return this.m_iScanNo;
	}

	public void setPrecursorPeak(Peak a_peak) {
		this.m_peakPrecursor = a_peak;
	}

	public Peak getPrecursorPeak() {
		return this.m_peakPrecursor;
	}

	public void setPeaks(List<Peak> a_lPeaks) {
		this.m_lPeaks = a_lPeaks;
	}

	public List<Peak> getPeaks() {
		return this.m_lPeaks;
	}

	public void addAnnotatedStructure(AnnotatedStructure a_as) {
		// No duplicate
		if ( this.m_lASs.contains(a_as) )
			return;
		this.m_lASs.add(a_as);
	}

	public List<AnnotatedStructure> getAnnotatedStructures() {
		return this.m_lASs;
	}

	public void putFeatureToAnnotation(Feature a_feature, Annotation a_annotation) {
		// Return if annotation IDs are not same between Feature and Annotation
		if ( a_feature.getAnnotationId().intValue() != a_annotation.getId().intValue() )
			return;
		this.m_mapFeatureToAnnotation.put(a_feature, a_annotation);
	}

	public Annotation getAnnotationFromFeature(Feature a_feature) {
		return this.m_mapFeatureToAnnotation.get(a_feature);
	}

	/**
	 * Add the specified Feature to a group of Features having the same structure.
	 * The specified AnnotatedStructure must have the same structure as the specified Feature.
	 * @param a_as - AnnotatedStructure for adding the grouped feature
	 * @param a_feature - Feature of the precursor ion to be add the specified AnnotatedStructure group
	 */
	public void addPrecursorFeatureToAnnotatedStructure(AnnotatedStructure a_as, Feature a_feature) {
		// AnnotatedStructure must be added already
		if ( !this.m_lASs.contains(a_as) )
			return;

		if ( !this.m_mapStructureToFeatures.containsKey(a_as) )
			this.m_mapStructureToFeatures.put(a_as, new ArrayList<>());

		// No duplicate
		if ( this.m_mapStructureToFeatures.get(a_as).contains(a_feature) )
			return;
		this.m_mapStructureToFeatures.get(a_as).add(a_feature);
	}

	public List<Feature> getPrecursorFeatures(AnnotatedStructure a_as) {
		if ( this.m_mapStructureToFeatures.containsKey(a_as) )
			return this.m_mapStructureToFeatures.get(a_as);
		return new ArrayList<>();
	}

	public void addPrecursorAnnotationInformationUnit(AnnotationInformationUnit a_asUnit) {
		// No duplicate
		if ( this.m_lPreAnnotInfoUnits.contains(a_asUnit) )
			return;
		this.m_lPreAnnotInfoUnits.add(a_asUnit);
	}

	public AnnotationInformationUnit getPrecursorAnnotationUnit(int a_iAnnot) {
		for ( AnnotationInformationUnit t_unit : this.m_lPreAnnotInfoUnits ) {
			if ( t_unit.getAnnotationID() != a_iAnnot ) continue;
			return t_unit;
		}
		return null;
	}

	public void addAnnotationInformationUnit(AnnotationInformationUnit a_asUnit) {
		// No duplicate
		if ( this.m_lAnnotInfoUnits.contains(a_asUnit) )
			return;
		this.m_lAnnotInfoUnits.add(a_asUnit);
	}

	public List<AnnotationInformationUnit> getAnnotationInformationUnits() {
		return this.m_lAnnotInfoUnits;
	}

	public AnnotationInformationUnit getAnnotationInformationUnit(int a_iPeak, int a_iAnnot) {
		for ( AnnotationInformationUnit t_annotInfoUnit : this.m_lAnnotInfoUnits ) {
			if ( t_annotInfoUnit.getPeakID() != a_iPeak ) continue;
			if ( t_annotInfoUnit.getAnnotationID() != a_iAnnot ) continue;
			return t_annotInfoUnit;
		}
		return null;
	}

	public AnnotationReport getParentReport() {
		return this.m_reportParentScan;
	}

	public void addSubScanReport(AnnotationReport a_reportSub) {
		// Clear old parent relation from sub report
		if ( a_reportSub.m_reportParentScan != null ) {
			AnnotationReport t_reportOldParent = a_reportSub.m_reportParentScan;
			t_reportOldParent.m_lSubScanReports.remove(a_reportSub);
		}
		// No duplicate
		if ( this.m_lSubScanReports.contains(a_reportSub) )
			return;
		this.m_lSubScanReports.add(a_reportSub);
		a_reportSub.m_reportParentScan = this;
	}

	public List<AnnotationReport> getSubScanReports() {
		return this.m_lSubScanReports;
	}

	public String getReportName() {
		String t_strPreMz = "";
		AnnotationReport t_report = this;
		while ( t_report != null ) {
			String t_strPreMz0 = ( t_report.m_reportParentScan == null )? "" : "@";
//			double t_dPreMz = t_report.m_peakPrecursor.getMz();
			double t_dPreMz = t_report.m_peakPrecursor.getPrecursorMz();
			t_strPreMz0 += new BigDecimal(t_dPreMz).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
			t_strPreMz = t_strPreMz0 + t_strPreMz;
			t_report = t_report.m_reportParentScan;
		}
		return "Scan#"+this.m_iScanNo+"("+t_strPreMz+")";
	}
}
