package org.grits.toolbox.tools.gsl.dango;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.grits.toolbox.ms.om.data.Annotation;
import org.grits.toolbox.ms.om.data.Feature;
import org.grits.toolbox.ms.om.data.LipidFeature;
import org.grits.toolbox.tools.gsl.structure.IonCombination;

/**
 * Class for annotating MS data with lipids.
 * @author Masaaki Matsubara
 *
 */
public class LipidMSAnnotator extends MSAnnotatorAbstract {

	private Map<String, List<String[]>> m_mapLipidToFragments;
	private Map<String, BigDecimal> m_mapLipidNameToMass;

	public LipidMSAnnotator(boolean a_bTrustMzCharge, boolean a_bIsMonoIsotopic) {
		super(a_bTrustMzCharge, a_bIsMonoIsotopic);

		this.m_mapLipidToFragments = new HashMap<>();
		this.m_mapLipidNameToMass = new HashMap<>();
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.tools.gsl.dango.MSAnnotatorAbstract#calculateMass(org.grits.toolbox.ms.om.data.Feature, java.lang.String)
	 */
	@Override
	protected BigDecimal calculateMass(Feature a_feature, String a_strPerDeriv) {
		if ( !(a_feature instanceof LipidFeature) )
			return null;
		LipidFeature t_feature = (LipidFeature)a_feature;
		String t_strLName = t_feature.getLipidName();
		if ( !this.m_mapLipidNameToMass.containsKey(t_strLName) ) {
			BigDecimal t_bdMass = LipidAnnotationUtils.calculateLipidMass(t_strLName, a_strPerDeriv);
			this.m_mapLipidNameToMass.put(t_strLName, t_bdMass);
		}
		return this.m_mapLipidNameToMass.get(t_strLName);
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
	protected LipidFeature getAnnotationFeature(Annotation a_annot) {
		String[] t_strFragInfo = {a_annot.getSequence(), ""};
		return this.getFragmentFeature(a_annot.getId(), t_strFragInfo);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.tools.gsl.dango.MSAnnotatorAbstract#getFragments(org.grits.toolbox.ms.om.data.Feature, java.lang.String, int, java.lang.String)
	 */
	@Override
	protected List<String[]> getFragments(Feature a_featureParent, String a_strPerDeriv, int a_iMSLevel, String a_strActivationMethod) {
		if ( !(a_featureParent instanceof LipidFeature) )
			return null;
		LipidFeature t_feature = (LipidFeature)a_featureParent;
		String t_strLName = t_feature.getLipidName();
		// Return list if already generated fragments
		if ( this.m_mapLipidToFragments.containsKey(t_strLName) )
			return this.m_mapLipidToFragments.get(t_strLName);

		// Generate fragments
		String t_strParentFragmentType = a_featureParent.getFragmentType();
		List<String[]> t_lFragLipNames = LipidAnnotationUtils.getLipidFragments(t_strLName, t_strParentFragmentType);

		this.m_mapLipidToFragments.put(t_strLName, t_lFragLipNames);
		return t_lFragLipNames;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.tools.gsl.dango.MSAnnotatorAbstract#getFragmentFeature(java.lang.Integer, java.lang.String[])
	 */
	@Override
	protected LipidFeature getFragmentFeature(Integer a_iAnnotID, String[] a_strFragInfo) {
		LipidFeature t_feature = new LipidFeature();
		t_feature.setAnnotationId(a_iAnnotID);
		t_feature.setSequence(a_strFragInfo[0]);
		t_feature.setLipidName(a_strFragInfo[0]);
		t_feature.setFragmentType(a_strFragInfo[1]);
		t_feature.setLipidFragments(new ArrayList<>());
		return t_feature;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.tools.gsl.dango.MSAnnotatorAbstract#setFragmentFeatures(org.grits.toolbox.ms.om.data.Feature, java.util.List)
	 */
	@Override
	protected void setFragmentFeatures(Feature a_featureParent, List<Feature> a_lFragFeatures) {
		if ( !(a_featureParent instanceof LipidFeature) )
			return;
		LipidFeature t_featureParent = (LipidFeature)a_featureParent;

		for ( Feature t_featureFrag : a_lFragFeatures ) {
			if ( !(t_featureFrag instanceof LipidFeature) )
				continue;
			t_featureParent.getLipidFragments().add((LipidFeature)t_featureFrag);
		}
	}

}
