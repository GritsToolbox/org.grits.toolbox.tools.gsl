package org.grits.toolbox.tools.gsl.util.io.glycolipid.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.grits.toolbox.ms.om.data.Feature;
import org.grits.toolbox.ms.om.data.GlycanFeature;
import org.grits.toolbox.ms.om.data.GlycolipidFeature;
import org.grits.toolbox.ms.om.data.LipidFeature;
import org.grits.toolbox.tools.gsl.structure.IonCombination;

/**
 * Class for providing objects related to AnnotationReport
 * @author Masaaki Matsubara
 *
 */
public class AnnotatedStructureProvider {

	private List<Structure> m_lStructures;
	private List<AnnotatedStructure> m_lAnnotStructures;
	private List<AnnotatedIon> m_lASInfo;
	private List<AnnotationInformationUnit> m_lASUnit;

	public AnnotatedStructureProvider() {
		this.m_lStructures = new ArrayList<>();
		this.m_lAnnotStructures = new ArrayList<>();
		this.m_lASInfo = new ArrayList<>();
		this.m_lASUnit = new ArrayList<>();
	}

	public List<AnnotatedStructure> getAnnotatedStructures() {
		return this.m_lAnnotStructures;
	}

	/**
	 * Create an AnnotatedStructure from glycan sequence and lipid name.
	 * If the same one is created previously, do not create new one and return old one.
	 * @param a_strGSeq - String of glycan sequence
	 * @param a_strLName - String of lipid name
	 * @return AnnotatedStructure having glycan sequence and lipid name
	 */
	public AnnotatedStructure createAnnotatedGlycolipidStructure(String a_strGSeq, String a_strLName) {
		// Create AnnotatedStructure for composition
		AnnotatedStructure t_as = new AnnotatedStructure();
		GlycolipidStructure t_gl = this.getGlycolipidStructure(a_strGSeq, a_strLName);
		t_as.setStructure(t_gl);

		// Get old one if the same one was already created
		boolean t_bIsNew = true;
		for ( AnnotatedStructure t_as0 : this.m_lAnnotStructures ) {
			if ( !t_as0.getStructure().equals(t_as.getStructure()) ) continue;
			t_bIsNew = false;
			t_as = t_as0;
			break;
		}
		if ( t_bIsNew ) {
			this.m_lAnnotStructures.add(t_as);
			t_as.setID( this.m_lAnnotStructures.size() );
		}
		return t_as;
	}

	/**
	 * Create an AnnotatedIon from a Feature.
	 * If the same one is created previously, do not create new one and return old one.
	 * @param a_feature - Feature to be used for creating AnnotatedIon
	 * @return AnnotatedIon having the structure and ion information of the specified Feature
	 */
	public AnnotatedIon createAnnotatedStructureInformation(Feature a_feature) {
		AnnotatedIon t_asInfo = new AnnotatedIon();

		GlycolipidStructure t_glStructure = new GlycolipidStructure();
		if ( a_feature instanceof GlycanFeature ) {
			t_glStructure.setGlycanSequence( ((GlycanFeature)a_feature).getSequence() );
			t_glStructure.setLipidName("");
		}
		if ( a_feature instanceof LipidFeature ) {
			t_glStructure.setGlycanSequence("");
			t_glStructure.setLipidName( ((LipidFeature)a_feature).getSequence() );
		}
		if ( a_feature instanceof GlycolipidFeature ) {
			t_glStructure.setGlycanSequence( ((GlycolipidFeature)a_feature).getSequence() );
			t_glStructure.setLipidName( ((GlycolipidFeature)a_feature).getLipidName() );
		}
		t_asInfo.setStructure(t_glStructure);
		t_asInfo.setIon( new IonCombination(a_feature).toString() );
		t_asInfo.setMass( this.roundDouble(a_feature.getMz(), 10) );
		t_asInfo.setDeviation( this.roundDouble(a_feature.getDeviation(), 10) );

		// Get old one if the same one was already created
		boolean t_bIsNew = true;
		for ( AnnotatedIon t_asInfo0 : this.m_lASInfo ) {
			if ( !t_asInfo0.equals(t_asInfo) ) continue;
			t_bIsNew = false;
			t_asInfo = t_asInfo0;
			break;
		}
		if ( t_bIsNew ) {
			this.m_lASInfo.add(t_asInfo);
		}
		return t_asInfo;
		
	}

	/**
	 * Create an AnnotationInformationUnit from peak ID, annotation ID and a Feature.
	 * If the AnnotationInformationUnit having the same peak and annotation IDs, a new AnnotatedIon created from the specified Feature is added.
	 * @param a_iPeak - peak ID of AnnotationInformationUnit
	 * @param a_iAnnot - annotation ID of AnnotationInforamtionUnit
	 * @param a_feature - Feature to be used for crating AnnotatedIon
	 * @return AnnotationInformationUnit having the specified peak and annotation IDs
	 */
	public AnnotationInformationUnit createAnnotatedStructureUnit(int a_iPeak, int a_iAnnot, Feature a_feature) {
		AnnotationInformationUnit t_asUnit = new AnnotationInformationUnit();
		t_asUnit.setPeakID(a_iPeak);
		t_asUnit.setAnnotationID(a_iAnnot);
		boolean t_bIsNew = true;
		for ( AnnotationInformationUnit t_asUnit0 : this.m_lASUnit ) {
			if ( a_iPeak != t_asUnit0.getPeakID() ) continue;
			if ( a_iAnnot != t_asUnit0.getAnnotationID() ) continue;
			t_bIsNew = false;
			t_asUnit = t_asUnit0;
			break;
		}
		if ( t_bIsNew )
			this.m_lASUnit.add(t_asUnit);

		AnnotatedIon t_asInfo = this.createAnnotatedStructureInformation(a_feature);
		if ( !t_asUnit.contains(t_asInfo) )
			t_asUnit.addStructureInformation(t_asInfo);
		return t_asUnit;
	}

	private GlycolipidStructure getGlycolipidStructure(String a_strGSeq, String a_strLName) {
		GlycolipidStructure t_gl = new GlycolipidStructure();
		t_gl.setGlycanSequence(a_strGSeq);
		t_gl.setLipidName(a_strLName);
		boolean t_bIsNew = true;
		for ( Structure t_gl0 : this.m_lStructures ) {
			if ( !t_gl.equals(t_gl0) ) continue;
			t_bIsNew = false;
			t_gl = (GlycolipidStructure)t_gl0;
			break;
		}
		if ( t_bIsNew )
			this.m_lStructures.add(t_gl);
		return t_gl;
	}

	private String roundDouble(double value, int places) {
		return new BigDecimal(value).setScale(places, BigDecimal.ROUND_HALF_UP).toPlainString();
	}

}
