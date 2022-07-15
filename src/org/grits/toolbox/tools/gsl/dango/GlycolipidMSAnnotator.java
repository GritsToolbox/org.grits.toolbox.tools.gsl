package org.grits.toolbox.tools.gsl.dango;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.grits.toolbox.ms.annotation.structure.GlycanPreDefinedOptions;
import org.grits.toolbox.ms.om.data.Annotation;
import org.grits.toolbox.ms.om.data.Feature;
import org.grits.toolbox.ms.om.data.Fragment;
import org.grits.toolbox.ms.om.data.GlycanAnnotation;
import org.grits.toolbox.ms.om.data.GlycanFeature;
import org.grits.toolbox.ms.om.data.GlycanSettings;
import org.grits.toolbox.ms.om.data.GlycolipidAnnotation;
import org.grits.toolbox.ms.om.data.GlycolipidFeature;
import org.grits.toolbox.ms.om.data.LipidAnnotation;
import org.grits.toolbox.ms.om.data.LipidFeature;
import org.grits.toolbox.ms.om.data.Peak;
import org.grits.toolbox.tools.gsl.structure.IonCombination;
import org.grits.toolbox.tools.gsl.util.mass.ChemicalComposition;

/**
 * Class for annotating MS data with glycolipid.
 * @author Masaaki Matsubara
 *
 */
public class GlycolipidMSAnnotator extends MSAnnotatorAbstract {
	private static final Logger logger = Logger.getLogger(GlycolipidMSAnnotator.class);

	private static String GLYCAN = "Glycan";
	private static String LIPID = "Lipid";
	private static String GLYCOLIPID = "Glycolipid";

	// Calculate water mass for calculating glycosidic linkage
	private static final BigDecimal bdMassH2O = new BigDecimal( ChemicalComposition.parseFormula("H2O").getMonoisotopicMass() );
	// Calculate CH2 mass for permethylation
	private static final BigDecimal bdMassCH2 = new BigDecimal( ChemicalComposition.parseFormula("CH2").getMonoisotopicMass() );

	private GlycanMSAnnotator m_gAnnotor;
	private LipidMSAnnotator m_lAnnotor;

	/**
	 * 
	 * @param a_bTrustMzCharge whether or not to trust charge of a peak from input data
	 * @param a_bIsMonoIsotopic whether or not monoisotopic mass is calculated
	 * @param a_gSet GlycanSettings to be used for glycan annotation
	 */
	public GlycolipidMSAnnotator(boolean a_bTrustMzCharge, boolean a_bIsMonoIsotopic, GlycanSettings a_gSet) {
		super(a_bTrustMzCharge, a_bIsMonoIsotopic);

		this.m_gAnnotor = new GlycanMSAnnotator(a_bTrustMzCharge, a_bIsMonoIsotopic, a_gSet);
		this.m_lAnnotor = new LipidMSAnnotator(a_bTrustMzCharge, a_bIsMonoIsotopic);
	}

	public void setIonCombinationsForGlycanFragments(List<IonCombination> a_lIonCombo) {
		this.m_gAnnotor.setIonCombinationsForFragments(a_lIonCombo);
	}

	public void setIonCombinationsForLipidFragments(List<IonCombination> a_lIonCombo) {
		this.m_lAnnotor.setIonCombinationsForFragments(a_lIonCombo);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.tools.gsl.dango.MSAnnotatorAbstract#getIonCombinationsForFragments(java.lang.String[])
	 */
	@Override
	protected List<IonCombination> getIonCombinationsForFragments(String[] a_strFragInfo) {
		if ( a_strFragInfo[0].equals(GLYCAN) )
			return this.m_gAnnotor.getIonCombinationsForFragments(a_strFragInfo);
		if ( a_strFragInfo[0].equals(LIPID) )
			return this.m_lAnnotor.getIonCombinationsForFragments(a_strFragInfo);
		if ( a_strFragInfo[0].equals(GLYCOLIPID) )
			return super.getIonCombinationsForFragments(a_strFragInfo);
		return new ArrayList<>();
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.tools.gsl.dango.MSAnnotatorAbstract#calculateMass(org.grits.toolbox.ms.om.data.Feature, java.lang.String)
	 */
	@Override
	protected BigDecimal calculateMass(Feature a_feature, String a_strPerDeriv) {
		if ( a_feature instanceof GlycanFeature )
			return this.calculateGlycanMass((GlycanFeature)a_feature, a_strPerDeriv);

		if ( a_feature instanceof LipidFeature )
			return this.calculateLipidMass((LipidFeature)a_feature, a_strPerDeriv);

		if ( a_feature instanceof GlycolipidFeature ) {
			GlycolipidFeature t_feature = (GlycolipidFeature)a_feature;

			// Create a glycan feature only for the calculation of glycan mass
			GlycanFeature t_gFeature = new GlycanFeature();
			t_gFeature.setSequence(t_feature.getSequence());
			t_gFeature.setAnnotationId(a_feature.getAnnotationId());
			BigDecimal t_bdGMass = this.calculateGlycanMass(t_gFeature, a_strPerDeriv);
			if ( GlycanAnnotationUtils.isPermethylated(a_strPerDeriv) )
				t_bdGMass = t_bdGMass.subtract(bdMassCH2);

			// Create a lipid feature only for the calculation of lipid mass as Z cleavage
			LipidFeature t_lFeature = new LipidFeature();
			t_lFeature.setLipidName(t_feature.getLipidName());
			t_lFeature.setAnnotationId(a_feature.getAnnotationId());
			t_lFeature.setFragmentType(Fragment.TYPE_Z);
			BigDecimal t_bdLMass = this.calculateLipidMass(t_lFeature, a_strPerDeriv);

			return t_bdGMass.add(t_bdLMass);
		}
		logger.error("Undefined Feature is specified in calculateMass");
		return null;
	}

	/**
	 * Calculates glycan mass of the given GlycanFeature with the given perderivatization
	 * @param a_feature GlycanFeature to be calculated mass
	 * @param a_strPerDeriv String of prederivatization type
	 * @return BigDecimal of the given glycan mass
	 */
	private BigDecimal calculateGlycanMass(GlycanFeature a_feature, String a_strPerDeriv) {
		BigDecimal t_bdMass = this.m_gAnnotor.calculateMass(a_feature, a_strPerDeriv);
		if ( t_bdMass == null ) {
			logger.warn("Glycan mass cannot be calculated: "+a_feature.getSequence());
			return BigDecimal.ZERO;
		}

		/*
		// For fragment of glycolipid
		Annotation t_annotOrig = this.m_mapIDToAnnotation.get(a_feature.getAnnotationId());
		if ( t_annotOrig != null && t_annotOrig instanceof GlycolipidAnnotation ) {
			if ( !GlycanAnnotationUtils.canConnectReducingEnd(a_feature.getSequence()) )
				return t_bdMass;
			// Remove CH2 for the glycan free end if permethylated
			if ( GlycanAnnotationUtils.isPermethylated(a_strPerDeriv) )
				t_bdMass = t_bdMass.subtract(bdMassCH2);
		}
		*/
		return t_bdMass;
	}

	/**
	 * Calculates glycan mass of the given LipidFeature with the given perderivatization
	 * @param a_feature LipidFeature to be calculated mass
	 * @param a_strPerDeriv String of prederivatization type
	 * @return BigDecimal of the given lipid mass
	 */
	private BigDecimal calculateLipidMass(LipidFeature a_feature, String a_strPerDeriv) {
		BigDecimal t_bdMass = this.m_lAnnotor.calculateMass(a_feature, a_strPerDeriv);
		if ( t_bdMass == null ) {
			logger.warn("Lipid mass cannot be calculated: "+a_feature.getLipidName());
			return BigDecimal.ZERO;
		}

		// For glycolipid fragment
		Annotation t_annotOrig = this.m_mapIDToAnnotation.get(a_feature.getAnnotationId());
		if ( t_annotOrig != null && t_annotOrig instanceof GlycolipidAnnotation ) {
			// Remove H2O/MeOH for glycosidic linkage on lipid
			if ( !LipidAnnotationUtils.canConnectToGlycan(a_feature.getLipidName()) )
				return t_bdMass;
			if ( a_feature.getFragmentType().startsWith("Z") )
				t_bdMass = t_bdMass.subtract(bdMassH2O);
			if ( GlycanAnnotationUtils.isPermethylated(a_strPerDeriv) )
				t_bdMass = t_bdMass.subtract(bdMassCH2);
		}
		return t_bdMass;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.tools.gsl.dango.MSAnnotatorAbstract#findKeyFragments(org.grits.toolbox.tools.gsl.dango.ScanReader, org.grits.toolbox.ms.om.data.Annotation, org.grits.toolbox.tools.gsl.structure.IonCombination)
	 */
	@Override
	protected boolean findKeyFragments(ScanReader a_scanR, Annotation a_annot, IonCombination a_ionCombo) {
		if ( a_annot instanceof GlycanAnnotation )
			return this.m_gAnnotor.findKeyFragments(a_scanR, a_annot, a_ionCombo);

		if ( a_annot instanceof LipidAnnotation )
			return this.m_lAnnotor.findKeyFragments(a_scanR, a_annot, a_ionCombo);

		if ( !(a_annot instanceof GlycolipidAnnotation) )
			return false;

		GlycolipidAnnotation t_annot = (GlycolipidAnnotation)a_annot;
		// This filter is only for permethylated structures
		if ( !t_annot.getPerDerivatisationType().equals(GlycanPreDefinedOptions.DERIVITIZATION_PERMETHYLATED) )
			return true;

		// For glycan key fragment masses
		String[] t_strFragInfo = {"Glycan", t_annot.getGlycanAnnotation().getSequence(), "C"};
		GlycanFeature t_gFeature = (GlycanFeature) this.getFragmentFeature(t_annot.getId(), t_strFragInfo);
		BigDecimal t_bdGMass = this.calculateMass(t_gFeature, t_annot.getPerDerivatisationType());

		// Calculate with possible ion combinations
		IonCombinationGenerator t_ionComboGen = new IonCombinationGenerator(a_ionCombo);
		t_ionComboGen.generate();
		for ( IonCombination t_ionCombo : t_ionComboGen.getPossibleIonCombinations() ) {
			BigDecimal t_bdMz = this.calculateIonizedMz(t_bdGMass, t_ionCombo);
			Peak t_peakMatchedG = a_scanR.getMatchedPeakForKeyFragment(t_bdMz);
			if ( t_peakMatchedG != null ) {
				return true;
			}
		}

		// For lipid key fragment masses
		String[] t_strFragInfoL = {"Lipid", t_annot.getLipidAnnotation().getSequence(), "Z"};
		LipidFeature t_lFeature = (LipidFeature) this.getFragmentFeature(t_annot.getId(), t_strFragInfoL);
		BigDecimal t_bdLMass = this.calculateMass(t_lFeature, t_annot.getPerDerivatisationType());

		// Calculate with proton
		IonCombination t_ionComboH = IonCombinationGenerator.getHydrogenIon();
		t_bdLMass = this.calculateIonizedMz(t_bdLMass, t_ionComboH);
		Peak t_peakMatchedL = a_scanR.getMatchedPeakForKeyFragment(t_bdLMass);
		if ( t_peakMatchedL != null ) {
			return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.tools.gsl.dango.MSAnnotatorAbstract#getAnnotationFeature(org.grits.toolbox.ms.om.data.Annotation)
	 */
	@Override
	protected Feature getAnnotationFeature(Annotation a_annot) {
		if ( a_annot instanceof GlycanAnnotation )
			return this.m_gAnnotor.getAnnotationFeature(a_annot);

		if ( a_annot instanceof LipidAnnotation )
			return this.m_lAnnotor.getAnnotationFeature(a_annot);

		if ( !(a_annot instanceof GlycolipidAnnotation) )
			return null;

		GlycolipidAnnotation t_annot = (GlycolipidAnnotation)a_annot;
		String[] t_strFragInfo = {GLYCOLIPID, t_annot.getGlycanAnnotation().getSequence(), t_annot.getLipidAnnotation().getSequence(), ""};
		return this.getFragmentFeature(a_annot.getId(), t_strFragInfo);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.tools.gsl.dango.MSAnnotatorAbstract#getFragments(org.grits.toolbox.ms.om.data.Feature, java.lang.String, int, java.lang.String)
	 */
	@Override
	protected List<String[]> getFragments(Feature a_featureParent, String a_strPerDeriv, int a_iMSLevel, String a_strActivationMethod) {
		if ( a_featureParent instanceof GlycanFeature )
			return this.getGlycanFragments((GlycanFeature)a_featureParent, a_strPerDeriv, a_iMSLevel, a_strActivationMethod);

		if ( a_featureParent instanceof LipidFeature )
			return this.getLipidFragments((LipidFeature)a_featureParent, a_strPerDeriv, a_iMSLevel, a_strActivationMethod);

		if ( !(a_featureParent instanceof GlycolipidFeature) ) 
			return null;
		GlycolipidFeature t_featureParent = (GlycolipidFeature)a_featureParent;

		List<String[]> t_lFragInfo = new ArrayList<>();

		// For glycan fragment
		GlycanFeature t_gFeature = new GlycanFeature();
		t_gFeature.setSequence(t_featureParent.getSequence());
		List<String[]> t_lGFragInfo = this.getGlycanFragments(t_gFeature, a_strPerDeriv, a_iMSLevel, a_strActivationMethod);

		// For lipid fragment
		LipidFeature t_lFeature = new LipidFeature();
		t_lFeature.setLipidName(t_featureParent.getLipidName());
		List<String[]> t_lLFragInfo = this.getLipidFragments(t_lFeature, a_strPerDeriv, a_iMSLevel, a_strActivationMethod);
		// Filter if lipid cannot connect to glycan
		List<String[]> t_lFiltered = new ArrayList<>();
		for ( String[] t_LFragInfo : t_lLFragInfo ) {
			if ( LipidAnnotationUtils.canConnectToGlycan(t_LFragInfo[1]) )
				t_lFiltered.add(t_LFragInfo);
		}
		t_lLFragInfo = t_lFiltered;

		// Add the fragments of glycan and lipid part if glycan cleavage type "B" and/or "C" are specified
		if ( this.m_gAnnotor.hasFragmentType(Fragment.TYPE_B, a_iMSLevel, a_strActivationMethod)
		  || this.m_gAnnotor.hasFragmentType(Fragment.TYPE_C, a_iMSLevel, a_strActivationMethod) ) {
			t_lFragInfo.addAll(t_lGFragInfo);
			t_lFragInfo.addAll(t_lLFragInfo);
		}

		// For glycolipid fragment
		List<String> t_lUsedGSeqs = new ArrayList<>();
		for ( String[] t_strGFragInfo0 : t_lGFragInfo ) {
			// Only for glycans having redusing end
			if ( !GlycanAnnotationUtils.canConnectReducingEnd(t_strGFragInfo0[1]) )
				continue;
			if ( t_lUsedGSeqs.contains(t_strGFragInfo0[1]) )
				continue;
			String t_strGSeq = t_strGFragInfo0[1];
			t_lUsedGSeqs.add(t_strGSeq);
			// Remove last "C" or "B"
			String t_strGFragType = t_strGFragInfo0[2].substring(0, t_strGFragInfo0[2].length()-1);

			List<String> t_lUsedLNames = new ArrayList<>();
			for ( String[] t_strLFragInfo0 : t_lLFragInfo ) {
				// Only for lipids having at least one hydroxy group on the sphingosine (side)
				if ( !LipidAnnotationUtils.canConnectToGlycan(t_strLFragInfo0[1]) )
					continue;
				if ( t_lUsedLNames.contains(t_strLFragInfo0[1]) )
					continue;
				String t_strLName = t_strLFragInfo0[1];
				t_lUsedLNames.add(t_strLName);
				// Remove first "Z" or "Y"
				String t_strLFragType = t_strLFragInfo0[2].substring(1, t_strLFragInfo0[2].length());

				// Combine fragment type
				String t_strGLFragType = t_strGFragType+t_strLFragType;

				String[] t_strGLFragInfo = {GLYCOLIPID, t_strGSeq, t_strLName, t_strGLFragType};
				t_lFragInfo.add(t_strGLFragInfo);
			}
		}

		return t_lFragInfo;
	}

	/**
	 * Gets list of glycan fragment information.
	 * @param a_featureParent GlycanFeature having information of the structure to be fragmented
	 * @param a_strPerDeriv String of permethylation type
	 * @param a_iMSLevel number of MS level
	 * @param a_strActivationMethod String of activation method
	 * @return List of String[] which listed [0] type of structure, [1] sequence of fragment, and [2] type of fragment
	 */
	private List<String[]> getGlycanFragments(GlycanFeature a_featureParent, String a_strPerDeriv, int a_iMSLevel, String a_strActivationMethod) {
		Annotation t_annot = this.m_mapIDToAnnotation.get(a_featureParent.getAnnotationId());
		List<String[]> t_lFragInfo = new ArrayList<>();
		for ( String[] t_strGFragInfo0 : this.m_gAnnotor.getFragments(a_featureParent, a_strPerDeriv, a_iMSLevel, a_strActivationMethod) ) {
			// Add fragment information for glycan
			String t_strFragSeq = t_strGFragInfo0[0];
			String t_strFragType = t_strGFragInfo0[1];
			if ( !GlycanAnnotationUtils.canConnectReducingEnd(t_strFragSeq) ) {
				String[] t_strGFragInfo = {GLYCAN, t_strFragSeq, t_strFragType};
				t_lFragInfo.add(t_strGFragInfo);
				continue;
			}
			if ( t_annot instanceof GlycanAnnotation )
				continue;
			// Add cleavage type "B" and "C" for reducing end for glycolipid fragment
			if ( this.m_gAnnotor.hasFragmentType(Fragment.TYPE_C, a_iMSLevel, a_strActivationMethod) ) {
				String t_strFragTypeC = t_strFragType + "C";
				String[] t_strGFragInfoC = {GLYCAN, t_strFragSeq, t_strFragTypeC};
				t_lFragInfo.add(t_strGFragInfoC);
			}
			if ( this.m_gAnnotor.hasFragmentType(Fragment.TYPE_B, a_iMSLevel, a_strActivationMethod) ) {
				String t_strFragTypeB = t_strFragType + "B";
				String[] t_strGFragInfoB = {GLYCAN, t_strFragSeq, t_strFragTypeB};
				t_lFragInfo.add(t_strGFragInfoB);
			}
		}
		return t_lFragInfo;
	}

	/**
	 * Gets list of lipid fragment information.
	 * @param a_featureParent LipidFeature having information of the structure to be fragmented
	 * @param a_strPerDeriv String of permethylation type
	 * @param a_iMSLevel number of MS level
	 * @param a_strActivationMethod String of activation method
	 * @return List of String[] which listed [0] type of structure, [1] sequence of fragment, and [2] type of fragment
	 */
	private List<String[]> getLipidFragments(LipidFeature a_featureParent, String a_strPerDeriv, int a_iMSLevel, String a_strActivationMethod) {
		Annotation t_annot = this.m_mapIDToAnnotation.get(a_featureParent.getAnnotationId());
		List<String[]> t_lFragInfo = new ArrayList<>();
		for ( String[] t_strLFragInfo0 : this.m_lAnnotor.getFragments(a_featureParent, a_strPerDeriv, a_iMSLevel, a_strActivationMethod) ) {
			// Add fragment information for lipid
			String t_strFragSeq = t_strLFragInfo0[0];
			String t_strFragType = t_strLFragInfo0[1];
			// Add cleavage type "Z" for glycosidic linkage
			if ( !LipidAnnotationUtils.canConnectToGlycan(t_strFragSeq) ) {
				String[] t_strLFragInfo = {LIPID, t_strFragSeq, t_strFragType};
				t_lFragInfo.add(t_strLFragInfo);
				continue;
			}
			if ( t_annot instanceof LipidAnnotation )
				continue;
			// Add cleavage type "Y" and "Z" for reducing end for glycolipid fragment
			if ( this.m_gAnnotor.hasFragmentType(Fragment.TYPE_C, a_iMSLevel, a_strActivationMethod) ) {
				String t_strFragTypeZ = "Z" + t_strFragType;
				String[] t_strLFragInfoZ = {LIPID, t_strFragSeq, t_strFragTypeZ};
				t_lFragInfo.add(t_strLFragInfoZ);
			}
			if ( this.m_gAnnotor.hasFragmentType(Fragment.TYPE_B, a_iMSLevel, a_strActivationMethod) ) {
				String t_strFragTypeY = "Y" + t_strFragType;
				String[] t_strLFragInfoY = {LIPID, t_strFragSeq, t_strFragTypeY};
				t_lFragInfo.add(t_strLFragInfoY);
			}
		}
		return t_lFragInfo;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.tools.gsl.dango.MSAnnotatorAbstract#getFragmentFeature(java.lang.Integer, java.lang.String[])
	 */
	@Override
	protected Feature getFragmentFeature(Integer a_iAnnotID, String[] a_strFragInfo) {
		Annotation t_annot = this.m_mapIDToAnnotation.get(a_iAnnotID);
		Feature t_feature = null;
		if ( a_strFragInfo[0].equals(GLYCAN) ) {
			String[] t_strFragInfo = {a_strFragInfo[1], a_strFragInfo[2]};
			t_feature = this.m_gAnnotor.getFragmentFeature(a_iAnnotID, t_strFragInfo);
			// Add cleavage info into sequence
			if ( t_annot instanceof GlycolipidAnnotation ) {
				String t_strGSeq = a_strFragInfo[1];
				if ( a_strFragInfo[2].endsWith("C") )
					t_strGSeq = t_strGSeq.replace("freeEnd--", "freeEnd/#ccleavage--");
				if ( a_strFragInfo[2].endsWith("B") )
					t_strGSeq = t_strGSeq.replace("freeEnd--", "freeEnd/#bcleavage--");
				t_feature.setSequence(t_strGSeq);
			}
		}
		if ( a_strFragInfo[0].equals(LIPID) ) {
			String[] t_strFragInfo = {a_strFragInfo[1], a_strFragInfo[2]};
			t_feature = this.m_lAnnotor.getFragmentFeature(a_iAnnotID, t_strFragInfo);
			if ( t_annot instanceof GlycolipidAnnotation ) {
				// Create a cleaved glycan sequence with lipid name
				GlycolipidAnnotation t_glAnnot = (GlycolipidAnnotation) t_annot;
				String t_strLName = a_strFragInfo[1];
				// "freeEnd--?b1D-Glc,p--4b1D-Gal,p$MONO,perMe,0,0,freeEnd"
				// -> [0]:"freeEnd--?b1D-Glc,p--4b1D-Gal,p", [1]:"MONO,perMe,0,0,freeEnd"
				String[] t_strGSeqSep = t_glAnnot.getGlycanAnnotation().getSequence().split("\\$");
				// "freeEnd--?b1D-Glc,p--4b1D-Gal,p"
				// -> [0]:"freeEnd", [1]:"?b1D-Glc,p", [2]:"4b1D-Gal,p"
				String[] t_strGSeqSep2 = t_strGSeqSep[0].split("--");
				// "MONO,perMe,0,0,freeEnd"
				// -> "MONO,perMe,0,0,Cer(d18:1/16:0)=0.0000u"
				String t_strLSeq = t_strGSeqSep[1].replace(",freeEnd", ","+t_strLName+"=0.0000u");
				String t_strCleavage = "";
				if ( a_strFragInfo[2].startsWith("Z") )
					t_strCleavage = "/#zcleavage";
				if ( a_strFragInfo[2].startsWith("Y") )
					t_strCleavage = "/#ycleavage";
				// "freeEnd--?b1D-Glc,p/#zcleavage$MONO,perMe,0,0,Cer(d18:1/16:0)=0.0000u"
				t_strLSeq = t_strGSeqSep2[0]+"--"+t_strGSeqSep2[1]+t_strCleavage+"$"+t_strLSeq;
				t_feature.setSequence(t_strLSeq);
			}
		}

		if ( a_strFragInfo[0].equals(GLYCOLIPID) ) {
			GlycolipidFeature t_glFeature = new GlycolipidFeature();
//			t_glFeature.setSequence(a_strFragInfo[1]);
			String t_strGLSeq = a_strFragInfo[1].replace(",freeEnd", ","+a_strFragInfo[2]+"=0.0000u");
			t_glFeature.setSequence(t_strGLSeq);
			t_glFeature.setLipidName(a_strFragInfo[2]);
			t_glFeature.setFragmentType(a_strFragInfo[3]);
			t_glFeature.setGlycanFragments(new ArrayList<>());
			t_glFeature.setLipidFragments(new ArrayList<>());
			t_glFeature.setGlycolipidFragments(new ArrayList<>());
			t_glFeature.setAnnotationId(a_iAnnotID);
			t_feature = t_glFeature;
		}
		return t_feature;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.tools.gsl.dango.MSAnnotatorAbstract#setFragmentFeatures(org.grits.toolbox.ms.om.data.Feature, java.util.List)
	 */
	@Override
	protected void setFragmentFeatures(Feature a_featureParent, List<Feature> a_lFragFeatures) {
		if ( a_featureParent instanceof GlycanFeature )
			this.m_gAnnotor.setFragmentFeatures(a_featureParent, a_lFragFeatures);

		if ( a_featureParent instanceof LipidFeature )
			this.m_lAnnotor.setFragmentFeatures(a_featureParent, a_lFragFeatures);

		if ( !(a_featureParent instanceof GlycolipidFeature) )
			return;
		GlycolipidFeature t_featureParent = (GlycolipidFeature)a_featureParent;

		for ( Feature t_featureFrag : a_lFragFeatures ) {
			if ( t_featureFrag instanceof GlycanFeature )
				t_featureParent.getGlycanFragments().add((GlycanFeature)t_featureFrag);
			if ( t_featureFrag instanceof LipidFeature )
				t_featureParent.getLipidFragments().add((LipidFeature)t_featureFrag);
			if ( t_featureFrag instanceof GlycolipidFeature )
				t_featureParent.getGlycolipidFragments().add((GlycolipidFeature)t_featureFrag);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.tools.gsl.dango.MSAnnotatorAbstract#getFeatureFragmentInfo(org.grits.toolbox.ms.om.data.Feature)
	 */
	@Override
	protected String[] getFeatureFragmentInfo(Feature a_feature) {
		if ( a_feature instanceof GlycanFeature || a_feature instanceof LipidFeature ) {
			String t_sStructure = ( a_feature instanceof GlycanFeature )? GLYCAN : LIPID;
			String[] t_lFragInfo = { t_sStructure, a_feature.getSequence(), a_feature.getType() };
			return t_lFragInfo;
		}
		if ( a_feature instanceof GlycolipidFeature ) {
			GlycolipidFeature t_feature = (GlycolipidFeature)a_feature;
			String[] t_lFragInfo = { GLYCOLIPID, t_feature.getSequence(), t_feature.getLipidName(), t_feature.getType() };
			return t_lFragInfo;
		}
		return null;
	}

}
