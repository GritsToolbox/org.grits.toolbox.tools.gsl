package org.grits.toolbox.tools.gsl.dango;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.grits.toolbox.ms.om.data.Annotation;
import org.grits.toolbox.ms.om.data.Feature;
import org.grits.toolbox.ms.om.data.Fragment;
import org.grits.toolbox.ms.om.data.FragmentPerActivationMethod;
import org.grits.toolbox.ms.om.data.FragmentPerMsLevel;
import org.grits.toolbox.ms.om.data.GlycanFeature;
import org.grits.toolbox.ms.om.data.GlycanSettings;
import org.grits.toolbox.tools.gsl.structure.IonCombination;

/**
 * Class for annotating MS data with glycans.
 * @author Masaaki Matsubara
 *
 */
public class GlycanMSAnnotator extends MSAnnotatorAbstract {

	private GlycanSettings m_gSettings;
	private Map<String, BigDecimal> m_mapGWBSeqToMass;
	private Map<String, List<String[]>> m_mapGWBSeqToFrags;

	public GlycanMSAnnotator(boolean a_bTrustMzCharge, boolean a_bIsMonoIsotopic, GlycanSettings a_gSet) {
		super(a_bTrustMzCharge, a_bIsMonoIsotopic);

		this.m_gSettings = a_gSet;
		this.m_mapGWBSeqToMass = new HashMap<>();
		this.m_mapGWBSeqToFrags = new HashMap<>();
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.tools.gsl.dango.MSAnnotatorAbstract#calculateMass(org.grits.toolbox.ms.om.data.Feature, java.lang.String)
	 */
	@Override
	protected BigDecimal calculateMass(Feature a_feature, String a_strPerDeriv) {
		if ( !(a_feature instanceof GlycanFeature) )
			return null;
		GlycanFeature t_feature = (GlycanFeature)a_feature;
		String a_strGSeq = t_feature.getSequence();
		// Create a key sequence which is cut non structure information
		// e.g. "freeEnd--?b1D-Gal,p$MONO,perMe,0,0,freeEnd" -> "freeEnd--?b1D-Gal,p(perMe)"
		String a_strGSeqKey = a_strGSeq.substring(0, a_strGSeq.indexOf("$"))+"("+a_strPerDeriv+")";
		if ( !this.m_mapGWBSeqToMass.containsKey(a_strGSeqKey) ) {
			BigDecimal t_bdMass = new BigDecimal( GlycanAnnotationUtils.calculateGlycanMass(a_strGSeq, this.m_bIsMonoIsotopic, a_strPerDeriv) );
			t_bdMass = t_bdMass.setScale(10, RoundingMode.HALF_UP);
			this.m_mapGWBSeqToMass.put(a_strGSeqKey, t_bdMass);
		}
		return this.m_mapGWBSeqToMass.get(a_strGSeqKey);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.tools.gsl.dango.MSAnnotatorAbstract#findKeyFragments(org.grits.toolbox.tools.gsl.dango.ScanReader, org.grits.toolbox.ms.om.data.Annotation, org.grits.toolbox.tools.gsl.structure.IonCombination)
	 */
	@Override
	protected boolean findKeyFragments(ScanReader a_scanR, Annotation a_annot, IonCombination a_ionCombo) {
		// return true always
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.tools.gsl.dango.MSAnnotatorAbstract#getAnnotationFeature(org.grits.toolbox.ms.om.data.Annotation)
	 */
	@Override
	protected Feature getAnnotationFeature(Annotation a_annot) {
		String[] t_strFragInfo = {a_annot.getSequence(), ""};
		return this.getFragmentFeature(a_annot.getId(), t_strFragInfo);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.tools.gsl.dango.MSAnnotatorAbstract#getFragments(org.grits.toolbox.ms.om.data.Feature, java.lang.String, int, java.lang.String)
	 */
	@Override
	protected List<String[]> getFragments(Feature a_featureParent, String a_strPerDeriv, int a_iMSLevel, String a_strActivationMethod) {
		if ( !(a_featureParent instanceof GlycanFeature) )
			return null;
		GlycanFeature t_featureParent = (GlycanFeature)a_featureParent;
		String a_strGSeqParent = t_featureParent.getSequence();
		// Return list if fragments are already generated
		if ( this.m_mapGWBSeqToFrags.containsKey(a_strGSeqParent) )
			return this.m_mapGWBSeqToFrags.get(a_strGSeqParent);

		// Generate fragments
		List<String[]> t_lGFragGSeqs = null;
		// Apply fragments per activation method
		for ( FragmentPerActivationMethod t_fpa : this.m_gSettings.getPerActivation() ) {
			if ( !t_fpa.getActivationMethod().equals(a_strActivationMethod) )
				continue;
			t_lGFragGSeqs = GlycanAnnotationUtils.generateGlycanFragments(
					a_strGSeqParent, this.m_bIsMonoIsotopic, a_strPerDeriv,
					t_fpa.getFragments(),
					t_fpa.getMaxNumOfCleavages(),
					t_fpa.getMaxNumOfCrossRingCleavages()
				);
			return t_lGFragGSeqs;
		}
		// Apply fragments per MS level
		for ( FragmentPerMsLevel t_fpMSn : this.m_gSettings.getPerMsLevel() ) {
			if ( t_fpMSn.getMsLevel() != a_iMSLevel )
				continue;
			t_lGFragGSeqs = GlycanAnnotationUtils.generateGlycanFragments(
					a_strGSeqParent, this.m_bIsMonoIsotopic, a_strPerDeriv,
					t_fpMSn.getFragments(),
					t_fpMSn.getM_maxNumOfCleavages(),
					t_fpMSn.getM_maxNumOfCrossRingCleavages()
				);
			return t_lGFragGSeqs;
		}
		// if no perActivationMethod and perMSLevel
		t_lGFragGSeqs = GlycanAnnotationUtils.generateGlycanFragments(
				a_strGSeqParent, this.m_bIsMonoIsotopic, a_strPerDeriv,
				this.m_gSettings.getGlycanFragments(),
				this.m_gSettings.getMaxNumOfCleavages(),
				this.m_gSettings.getMaxNumOfCrossRingCleavages()
			);

		this.m_mapGWBSeqToFrags.put(a_strGSeqParent, t_lGFragGSeqs);
		return t_lGFragGSeqs;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.tools.gsl.dango.MSAnnotatorAbstract#getFragmentFeature(java.lang.Integer, java.lang.String[])
	 */
	@Override
	protected Feature getFragmentFeature(Integer a_iAnnotID, String[] a_strFragInfo) {
		GlycanFeature t_feature = new GlycanFeature();
		t_feature.setAnnotationId(a_iAnnotID);
		t_feature.setSequence(a_strFragInfo[0]);
		t_feature.setFragmentType(a_strFragInfo[1]);
		t_feature.setGlycanFragment(new ArrayList<>());
		return t_feature;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.tools.gsl.dango.MSAnnotatorAbstract#setFragmentFeatures(org.grits.toolbox.ms.om.data.Feature, java.util.List)
	 */
	@Override
	protected void setFragmentFeatures(Feature a_featureParent, List<Feature> a_lFragFeatures) {
		if ( !(a_featureParent instanceof GlycanFeature) )
			return;
		GlycanFeature t_featureParent = (GlycanFeature)a_featureParent;

		for ( Feature t_featureFrag : a_lFragFeatures ) {
			if ( !(t_featureFrag instanceof GlycanFeature) )
				continue;
			t_featureParent.getGlycanFragment().add((GlycanFeature)t_featureFrag);
		}
	}

	/**
	 * Returns true if the given fragment type is specified as fragment option of current annotation.
	 * @param a_strFragType String of glycan fragment type
	 * @param a_iMSLevel int of MS level for specific fragmentation
	 * @param a_strActivationMethod String of activation method for specific fragmentation
	 * @return true if the given fragment type is specified as fragment option of current annotation
	 */
	public boolean hasFragmentType(String a_strFragType, int a_iMSLevel, String a_strActivationMethod) {
		for ( FragmentPerActivationMethod t_fpa : this.m_gSettings.getPerActivation() ) {
			if ( !t_fpa.getActivationMethod().equals(a_strActivationMethod) )
				continue;
			for ( Fragment t_frag : t_fpa.getFragments() )
				if ( t_frag.getType().equals(a_strFragType) )
					return true;
		}

		for ( FragmentPerMsLevel t_fpMSn : this.m_gSettings.getPerMsLevel() ) {
			if ( t_fpMSn.getMsLevel() != a_iMSLevel )
				continue;
			for ( Fragment t_frag : t_fpMSn.getFragments() )
				if ( t_frag.getType().equals(a_strFragType) )
					return true;
		}

		for ( Fragment t_frag : this.m_gSettings.getGlycanFragments() )
			if ( t_frag.getType().equals(a_strFragType) )
				return true;

		return false;
	}
}
