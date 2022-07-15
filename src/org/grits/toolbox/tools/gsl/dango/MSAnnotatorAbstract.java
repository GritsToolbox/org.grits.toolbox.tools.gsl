package org.grits.toolbox.tools.gsl.dango;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.grits.toolbox.ms.om.data.Annotation;
import org.grits.toolbox.ms.om.data.DerivitizedAnnotation;
import org.grits.toolbox.ms.om.data.Feature;
import org.grits.toolbox.ms.om.data.FeatureSelection;
import org.grits.toolbox.ms.om.data.Peak;
import org.grits.toolbox.tools.gsl.structure.IonCombination;
import org.grits.toolbox.widgets.progress.IProgressListener;
import org.grits.toolbox.widgets.progress.IProgressListener.ProgressType;
import org.grits.toolbox.widgets.tools.INotifyingProcess;
import org.grits.toolbox.widgets.tools.NotifyingProcessUtil;

/**
 * Abstract class for annotate structures to MS Scans
 * @author Masaaki Matsubara
 *
 */
public abstract class MSAnnotatorAbstract implements INotifyingProcess {
	private static final Logger logger = Logger.getLogger(MSAnnotatorAbstract.class);

	private static final String COUNTING_SCORE = "counting_score";
	private static final String INTENSITY_SCORE = "intensity_score";
	private static final String ROW_COUNTING_SCORE = "row_counting_score";
	private static final String ROW_INTENSITY_SCORE = "row_intensity_score";

	private boolean m_bFilter;

	private boolean m_bIsTrustMzCharge;
	protected boolean m_bIsMonoIsotopic;
	private List<Annotation> m_lCandidateAnnots;
	private Map<BigDecimal, List<Annotation>> m_mapMassToCandidateAnnotations;
	protected Map<Integer, Annotation> m_mapIDToAnnotation;
	private List<IonCombination> m_lIonCombinationsForPrecursor;
	private List<IonCombination> m_lIonCombinationsForFragments;
	private Map<Integer, List<IonCombination>> m_mapMSLevelToIonCombos;

	private List<Peak> m_lAnnotatedPrecursorPeaks;

	private List<IProgressListener> lProgressListeners;
	private boolean bCancel;

	public MSAnnotatorAbstract(boolean a_bTrustMzCharge, boolean a_bIsMonoIsotopic) {
		this.m_bFilter = false;

		this.m_bIsTrustMzCharge = a_bTrustMzCharge;
		this.m_bIsMonoIsotopic = a_bIsMonoIsotopic;
		this.m_lCandidateAnnots = new ArrayList<>();
		this.m_mapMassToCandidateAnnotations = new TreeMap<>();
		this.m_mapIDToAnnotation = new HashMap<>();
		this.m_lIonCombinationsForPrecursor = new ArrayList<>();
		this.m_lIonCombinationsForFragments = new ArrayList<>();

		this.m_lAnnotatedPrecursorPeaks = new ArrayList<>();
	}

	public boolean isTrustMzCharge() {
		return this.m_bIsTrustMzCharge;
	}

	public boolean isMonoIsotopic() {
		return this.m_bIsMonoIsotopic;
	}

	/**
	 * Adds annotation as candidate structures. They are grouped as a list with their calculated mass.
	 * @param a_annot Annotation to be stored as candidate structure
	 */
	public void addCandidateAnnotation(Annotation a_annot) {
		this.m_lCandidateAnnots.add(a_annot);
		this.m_mapIDToAnnotation.put(a_annot.getId(), a_annot);

		// Create Feature from Annotation
		Feature t_feature = this.getAnnotationFeature(a_annot);
		if ( t_feature == null ) {
			logger.warn("Feature cannot create from "+a_annot);
			return;
		}

		// Get per derivatization type
		String t_strPerDeriv = ((DerivitizedAnnotation)a_annot).getPerDerivatisationType();

		// Calculate mass
		BigDecimal t_bdMass = this.calculateMass(t_feature, t_strPerDeriv);
		if ( t_bdMass == null ) {
			logger.warn("Mass cannot calculate from "+a_annot);
			return;
		}

		if ( !this.m_mapMassToCandidateAnnotations.containsKey(t_bdMass) )
			this.m_mapMassToCandidateAnnotations.put(t_bdMass, new ArrayList<>());
		this.m_mapMassToCandidateAnnotations.get(t_bdMass).add(a_annot);
	}

	public List<Annotation> getCandidateAnnotations() {
		return this.m_lCandidateAnnots;
	}

	public void setFilterByKeyFeature(boolean a_bDoFilter) {
		this.m_bFilter = a_bDoFilter;
	}

	public void setIonCombinationsPerMSLevel(int a_iMSLevel, List<IonCombination> a_lIonCombo) {
		this.m_mapMSLevelToIonCombos.put(a_iMSLevel, a_lIonCombo);
	}

	public void setIonCombinationsForPrecursor(List<IonCombination> a_lIonCombo) {
		this.m_lIonCombinationsForPrecursor = a_lIonCombo;
	}

	public void setIonCombinationsForFragments(List<IonCombination> a_lIonCombo) {
		this.m_lIonCombinationsForFragments = a_lIonCombo;
	}

	public List<Feature> annotateMSProfile(ScanReader a_scanR) {
		List<Feature> t_lMatchedFeatures = new ArrayList<>();

		return t_lMatchedFeatures;
	}

	/**
	 * Annotates MS1 structures to precursor m/z of MS2 scan and annotate fragment ions of the matched ions.
	 * The count/intensity scores are also calculated.
	 * @param a_scanRMS2 ScanReader to be annotated
	 * @return List of Features matched to m/z of the precursor ion (empty array if no match)
	 */
	public List<Feature> annotateMS1Structures(ScanReader a_scanRMS2) {
		List<Feature> t_lMatchedFeatures = new ArrayList<>();

		// Return null if scan has no precursor peak
		Peak t_peakPrecursor = a_scanRMS2.getScan().getPrecursor();
		if ( t_peakPrecursor == null )
			return t_lMatchedFeatures;

		Set<Peak> t_setAllMatchedPeaks = new HashSet<>();
		for ( IonCombination t_ionCombo : this.m_lIonCombinationsForPrecursor ) {
			// Skip if ion charge is not equal to the precursor charge when trust precursor charge is true
			if ( this.m_bIsTrustMzCharge ) {
//				Integer t_iPreCharge = t_peakPrecursor.getCharge();
				Integer t_iPreCharge = t_peakPrecursor.getPrecursorCharge();
				// For negative ion mode
				if ( !a_scanRMS2.getScan().getPolarity() )
					t_iPreCharge *= -1;
				if ( t_iPreCharge != null && t_iPreCharge != t_ionCombo.calculateCharge() )
					continue;
			}

			// Loop for annotation masses
			for ( BigDecimal t_bdMass : this.m_mapMassToCandidateAnnotations.keySet() ) {
				// Calculate m/z with ion combination
				BigDecimal t_bdMz = this.calculateIonizedMz(t_bdMass, t_ionCombo);

				if ( !a_scanRMS2.isMatchedToPrecursor(t_bdMz) )
					continue;

				for ( Annotation t_annot : this.m_mapMassToCandidateAnnotations.get(t_bdMass) ) {
					// Do filter whether there are key features
					if ( this.m_bFilter && !this.findKeyFragments(a_scanRMS2, t_annot, t_ionCombo) )
						continue;

					// Create Feature from Annotation
					Feature t_feature = this.getAnnotationFeature(t_annot);
					t_lMatchedFeatures.add(t_feature);

					// Set parameters other than sequence to the feature
					this.setParametersToFeature(
							t_feature, t_peakPrecursor.getId(), a_scanRMS2.getScan().getScanNo(),
							t_bdMz, a_scanRMS2.calculateDeviation(t_peakPrecursor, t_bdMz), t_ionCombo
						);

					// Annotate fragment ions and calculate score
					Set<Peak> t_setMatchedPeaks = this.annotateFragments(a_scanRMS2, t_feature);
					// Add matched peaks
					t_setAllMatchedPeaks.addAll(t_setMatchedPeaks);

					// Calculate and set scores
					double[] t_dScores = this.caluclateScores(a_scanRMS2, t_setMatchedPeaks);

					t_feature.addDoubleProp(COUNTING_SCORE, t_dScores[0]); // Counting score
					t_feature.addDoubleProp(INTENSITY_SCORE, t_dScores[1]); // Intensity score
					t_feature.addDoubleProp(ROW_COUNTING_SCORE, t_dScores[2]); // Row counting score
					t_feature.addDoubleProp(ROW_INTENSITY_SCORE, t_dScores[3]); // Row intensity score
				}
			}
		}

		// Set # of annotated peaks
		a_scanRMS2.getScan().setNumAnnotatedPeaks( t_setAllMatchedPeaks.size() );

		return t_lMatchedFeatures;
	}

	/**
	 * Annotates sub scan using parent scan having the precursor peak of sub scan.
	 * Returns empty array if the precursor peak of sub scan isn't in the parent scan or doesn't match to any features.
	 * @param a_scanRSub ScanReader of sub scan
	 * @return List of Features annotated to the precursor peak of sub scan
	 */
	public List<Feature> annotateSubScan(ScanReader a_scanRSub) {
		List<Feature> t_lPrecursorFeatures = new ArrayList<>();
		
		// Precursor peak of subscan has been associated to the corresponding peak of the parent scan.
		Peak t_peakPrecursor = a_scanRSub.getScan().getPrecursor();
		// Return empty array if no matched peak or the peak has no features
		if ( t_peakPrecursor == null || t_peakPrecursor.getFeatures().isEmpty() )
			return t_lPrecursorFeatures;

		// Copy the Features assigned to the peak if the precursor peak already used in the other Scan
		// (For the case that there are two or more different scans having the same precursor peak)
		List<Integer> t_lFeatureIds = t_peakPrecursor.getFeatures();
		if ( this.m_lAnnotatedPrecursorPeaks.contains(t_peakPrecursor) ) {
			t_lFeatureIds = new ArrayList<>();
			for ( Integer t_iPrecursorFeature : t_peakPrecursor.getFeatures() ) {
				Feature t_featureOriginal = FeatureProvider.getFeatures().get(t_iPrecursorFeature-1);
				// Skip if the original feature is copied one to avoid duplicated annotation
				if ( FeatureProvider.isCopiedFeature(t_featureOriginal) )
					continue;
				Feature t_featureCopied = this.copyFeature(t_featureOriginal);
				t_lFeatureIds.add(Integer.valueOf( t_featureCopied.getId() ));
			}
			if ( !t_lFeatureIds.isEmpty() )
				t_peakPrecursor.getFeatures().addAll(t_lFeatureIds);
		}
		else
			// Add the precursor peak to the list of annotated peaks
			this.m_lAnnotatedPrecursorPeaks.add(t_peakPrecursor);

		// Annotate fragments of parent Features
		Set<Peak> t_setAllMatchedPeaks = new HashSet<>();
		for ( Integer t_iPrecursorFeature : t_lFeatureIds ) {
			Feature t_featurePrecursor = FeatureProvider.getFeatures().get(t_iPrecursorFeature-1);

			// Replace FeatureSelection to have a ScanNo in the RowId
			t_featurePrecursor.getFeatureSelections().clear();
			FeatureSelection t_fs = new FeatureSelection();
			t_fs.setRowId( Feature.getRowId(t_peakPrecursor.getId(), a_scanRSub.getScan().getScanNo(), true) );
			t_featurePrecursor.getFeatureSelections().add(t_fs);

			// Store Features annotated to precursor peak
			t_lPrecursorFeatures.add(t_featurePrecursor);

			// Annotate fragments of features
			Set<Peak> t_setMatchedPeaks = this.annotateFragments(a_scanRSub, t_featurePrecursor);

			// Calculate scores for sub scans
			double[] t_dScores = this.caluclateScores(a_scanRSub, t_setMatchedPeaks);
			t_featurePrecursor.addDoubleProp(COUNTING_SCORE, t_dScores[0]);
			t_featurePrecursor.addDoubleProp(INTENSITY_SCORE, t_dScores[1]);
			t_featurePrecursor.addDoubleProp(ROW_COUNTING_SCORE, t_dScores[2]);
			t_featurePrecursor.addDoubleProp(ROW_INTENSITY_SCORE, t_dScores[3]);

			// Add matched peaks
			t_setAllMatchedPeaks.addAll(t_setMatchedPeaks);
		}

		// Set # of annotated peaks
		a_scanRSub.getScan().setNumAnnotatedPeaks(t_setAllMatchedPeaks.size());

		return t_lPrecursorFeatures;
	}

	/**
	 * Annotates fragment ions of the given Feature to the specified Scan.
	 * Annotated Peaks have Feature numbers of the matched fragments and are returned as a Set.
	 * @param a_scanR ScanReader having the Scan to be matched fragments
	 * @param a_featureParent Feature to be fragmented
	 * @return Set of Peaks matched to the fragments
	 */
	public Set<Peak> annotateFragments(ScanReader a_scanR, Feature a_featureParent) {

		Set<Peak> t_setMatchedPeaks = new HashSet<>();
		List<Feature> t_lFragFeatures = new ArrayList<>();

		// Get per derivatization type
		Annotation t_annot = this.m_mapIDToAnnotation.get( a_featureParent.getAnnotationId() );
		String t_strPerDeriv = ((DerivitizedAnnotation)t_annot).getPerDerivatisationType();

		// Generate a list of fragment names and its types from parent Feature
		List<String[]> t_lFragments = this.getFragments(a_featureParent, t_strPerDeriv, a_scanR.getScan().getMsLevel(), a_scanR.getScan().getActivationMethode());
		if ( t_lFragments == null )
			return t_setMatchedPeaks;

		for ( String[] t_strFrag : t_lFragments ) {
			// Create Feature from fragment information

			for ( IonCombination t_ionCombo : this.getIonCombinationsForFragments(t_strFrag) ) {
				if ( a_scanR.getScan().getPolarity() != (t_ionCombo.calculateCharge() > 0) )
					continue;
				if ( Math.abs( t_ionCombo.calculateCharge() ) > Math.abs( a_featureParent.getCharge() ) )
					continue;

				Feature t_featureFrag = this.getFragmentFeature(a_featureParent.getAnnotationId(), t_strFrag);
				if ( t_featureFrag == null )
					continue;

				// Calculate mass
				BigDecimal t_bdMass = this.calculateMass(t_featureFrag, t_strPerDeriv);
				if ( t_bdMass == null )
					continue;
				// Calculate m/z with ion combination
				BigDecimal t_bdFragMz = this.calculateIonizedMz(t_bdMass, t_ionCombo);

				// Get matched peak (null if no match)
				Peak t_peakMatched = a_scanR.getMatchedPeak(t_bdFragMz);
				// Continue if no match
				if ( t_peakMatched == null )
					continue;

				// Add unique peaks
				t_setMatchedPeaks.add(t_peakMatched);

				// Set parameters for the feature
				this.setParametersToFeature(
						t_featureFrag, t_peakMatched.getId(), null,
						t_bdFragMz, a_scanR.calculateDeviation(t_peakMatched, t_bdFragMz), t_ionCombo
					);
				// Set parent Feature ID
				t_featureFrag.setParentId(a_featureParent.getId());
				// Set feature id to matched peak TODO: Confirm the correct way to set the parameter
				t_peakMatched.getFeatures().add(Integer.valueOf( t_featureFrag.getId() ));

				t_lFragFeatures.add(t_featureFrag);
			}
		}

		// Set fragment features to parent feature
		this.setFragmentFeatures(a_featureParent, t_lFragFeatures);

		return t_setMatchedPeaks;
	}

	/**
	 * Gets IonCombinations for fragment ions. This will be override when the different fragment parts are contained.
	 * @param a_strFrag a Feature for using IonCombination
	 * @return List of IonCombinations
	 */
	protected List<IonCombination> getIonCombinationsForFragments(String[] a_strFrag) {
		return this.m_lIonCombinationsForFragments;
	}

	/**
	 * Calculates counting and intensity scores.
	 * @param a_scanR ScanReader for using calculation of scores
	 * @param a_setMatchedPeaks Set of matched Peaks
	 * @return Array of double values of scores (0: Counting score, 1: Intensity score, 2: Row counting score, 3: Row intensity score)
	 */
	private double[] caluclateScores(ScanReader a_scanR, Set<Peak> a_setMatchedPeaks) {
		// Calculate count score
		double t_dCountScore = (double) a_setMatchedPeaks.size()/a_scanR.getFilteredPeaks().size();
		double t_dRowCountScore = (double) a_setMatchedPeaks.size()/a_scanR.getScan().getTotalNumPeaks();

		// Calculate intensity score
		double t_dSumTotalIntensity = 0.0d;
		for ( Peak t_peak : a_scanR.getFilteredPeaks() )
			t_dSumTotalIntensity += t_peak.getIntensity();
		double t_dSumMatchedIntensity = 0.0d;
		for ( Peak t_peak : a_setMatchedPeaks )
			t_dSumMatchedIntensity += t_peak.getIntensity();
		double t_dIntensityScore = t_dSumMatchedIntensity/t_dSumTotalIntensity;
		double t_dRowIntensityScore = t_dSumMatchedIntensity/a_scanR.getScan().getTotalIntensity();

		double[] t_dScores = {t_dCountScore, t_dIntensityScore, t_dRowCountScore, t_dRowIntensityScore};
		return t_dScores;
	}

	/**
	 * Sets parameters to the given Feature except for the sequence and/or name and annotation id.
	 * The sequence and/or name and annotation id must be already set at {@link #getAnnotationFeature(Annotation)} or {@link #getFragments(Feature, String)}.
	 * @param a_feature Feature to be set the specified parameters
	 * @param a_iPeakId Integer of annotated peak id
	 * @param a_iScanNo Integer of scan no when the annotated peak is precursor of the scan (null if the peak is not precursor peak of the scan)
	 * @param a_bdMz BigDecimal of calculated m/z value
	 * @param a_bdDeviation BigDecimal of deviation (PPM) between the m/z values of matched peak and calculated one
	 * @param a_iAnnot Integer of Annotation id of the annotated structure
	 * @param a_ionCombo IonCombination of ions
	 */
	private void setParametersToFeature(Feature a_feature, Integer a_iPeakId, Integer a_iScanNo, BigDecimal a_bdMz, BigDecimal a_bdDeviation, IonCombination a_ionCombo) {
		FeatureProvider.addNewFeature(a_feature);

		// Set a FeatureSelection with peak id to feature
		FeatureSelection t_fSelection = new FeatureSelection();
//		t_fSelection.setRowId( Integer.toString(a_iPeakId) );
		t_fSelection.setRowId( Feature.getRowId(a_iPeakId, a_iScanNo, true) );
		a_feature.getFeatureSelections().add(t_fSelection);

		// TODO: Check how to store the parameters
		a_feature.setMz( a_bdMz.doubleValue() );
		a_feature.setDeviation( a_bdDeviation.doubleValue() );
		a_feature.setIons( a_ionCombo.getIonAdducts() );
		a_feature.setNeutralexchange( a_ionCombo.getIonExchanges() );
		a_feature.setNeutralLoss( a_ionCombo.getNeutralLosses() );
		a_feature.setCharge( a_ionCombo.calculateCharge() );
		
	}

	/**
	 * Copies the feature. It will have new Id.
	 * @param t_feature Feature to be copied
	 * @return Feature copied from the given feature
	 */
	private Feature copyFeature(Feature a_feature) {
		String[] t_lFragInfo = this.getFeatureFragmentInfo(a_feature);
		Feature t_featureCopied = this.getFragmentFeature(a_feature.getAnnotationId(), t_lFragInfo);

		// Add feature to the list of copied Feature
		FeatureProvider.addCopiedFeature(t_featureCopied);

		// Copy the information without FeatureSelection and props
		t_featureCopied.setMz( a_feature.getMz() );
		t_featureCopied.setDeviation( a_feature.getDeviation() );
		t_featureCopied.setIons( a_feature.getIons() );
		t_featureCopied.setNeutralexchange( a_feature.getNeutralexchange() );
		t_featureCopied.setNeutralLoss( a_feature.getNeutralLoss() );
		t_featureCopied.setCharge( a_feature.getCharge() );

		return t_featureCopied;
	}

	/**
	 * Gets the fragment info from the given Feature.
	 * This must be overrided if there is different information.
	 * @param a_feature Feature to be extracted fragment info
	 * @return List of Strings containing fragment info
	 */
	protected String[] getFeatureFragmentInfo(Feature a_feature) {
		String[] t_lFragInfo = {a_feature.getSequence(), a_feature.getType()};
		return t_lFragInfo;
	}

	protected BigDecimal calculateIonizedMz(BigDecimal a_bdAnnalyteMass, IonCombination a_ionCombo) {
		BigDecimal t_bdMzGlycolipid = a_bdAnnalyteMass.add( new BigDecimal( a_ionCombo.calculateMass() ) );
		return t_bdMzGlycolipid.divide( new BigDecimal( Math.abs( a_ionCombo.calculateCharge() ) ), 10, BigDecimal.ROUND_HALF_UP );
	}

	/**
	 * Gets perderivatization type from Annotation.
	 * TODO: Annotation must have per derivatization type directly.
	 * @param a_annot Annotation having per derivatization type
	 * @return String of per derivatization type
	 */
//	protected abstract String getPerDerivatizationType(Annotation a_annot);
	/**
	 * Calculates a m/z value for the combination of the given feature, ions and perderivative type.
	 * Feature must have at least the sequence or name of structures.
	 * @param a_feature Feature to be calculated m/z
	 * @param a_ionCombo IonCombination of ions
	 * @param a_strPerDeriv String of perderivative type
	 * @return BigDecimal of the calculated m/z value
	 */
	protected abstract BigDecimal calculateMass(Feature a_feature, String a_strPerDeriv);
	/**
	 * Returns true if key fragments of annotated structure are found from the given Scan (ScanReader).
	 * The key fragments are coming from the given Annotation and IonCombination.
	 * @param a_scanR Scan for finding key fragments
	 * @param a_annot Annotation for Precursor peak of the Scan
	 * @param a_ionCombo IonCombination for Precursor peak of the Scan
	 * @return True if key fragments are found
	 */
	protected abstract boolean findKeyFragments(ScanReader a_scanR, Annotation a_annot, IonCombination a_ionCombo);
	/**
	 * Gets Feature having only the sequence and/or name of the given Annotation.
	 * The Feature will be set parameters using {@link #setParametersToFeature(Feature, Peak, BigDecimal, BigDecimal, Integer, IonCombination)}
	 * @param a_annot Annotation for the Feature
	 * @return Feature having only the sequence and/or name of specified Annotation
	 */
	protected abstract Feature getAnnotationFeature(Annotation a_annot);
	/**
	 * Get fragment sequence and/or name and the cleavage type generated from the given Feature.
	 * Each Feature has only sequence or name of its structure.
	 * @param a_featureParent Feature to be fragmented
	 * @param a_strPerDeriv String of perderivative type
	 * @param a_iMSLevel Number of MS level
	 * @param a_strActivationMethod String of activation method
	 * @return List of String array containing fragment sequence and the cleavage type
	 */
	protected abstract List<String[]> getFragments(Feature a_featureParent, String a_strPerDeriv, int a_iMSLevel, String a_strActivationMethod);
	/**
	 * Gets fragment feature generated from the given information.
	 * @param a_iAnnotID Integer of Annotation id for the Feature
	 * @param a_strFragInfo String array containing fragment sequence and the cleavage type
	 * @return Feature of fragment ({@code null} if the fragment generation is failed)
	 */
	protected abstract Feature getFragmentFeature(Integer a_iAnnotID, String[] a_strFragInfo);
	/**
	 * Sets fragment features to parent feature.
	 * @param a_featureParent Feature to be parent
	 * @param a_lFragFeatures List of Features to be fragment
	 */
	protected abstract void setFragmentFeatures(Feature a_featureParent, List<Feature> a_lFragFeatures);

	/**
	 * Private static class for providing features with unique number.
	 * @author Masaaki Matsubara
	 *
	 */
	private static class FeatureProvider {
		private static final List<Feature> lFeatures = new ArrayList<Feature>();
		private static final List<Feature> lCopiedFeature = new ArrayList<Feature>();

		/**
		 * Adds a new feature to common list to manage its number
		 * @param a_feature Feature to be stored
		 */
		public static void addNewFeature(Feature a_feature) {
			lFeatures.add(a_feature);
			a_feature.setId( ""+lFeatures.size() );
		}

		/**
		 * Adds a copied feature.
		 * @param a_feature Feature to be stored as a copy
		 */
		public static void addCopiedFeature(Feature a_feature) {
			addNewFeature(a_feature);
			lCopiedFeature.add(a_feature);
		}

		/**
		 * Checks the given feature is contained in the copied feature list
		 * @param a_feature Feature to be check whether it's copied
		 * @return true if the feature is copied
		 */
		public static boolean isCopiedFeature(Feature a_feature) {
			return lCopiedFeature.contains(a_feature);
		}

		public static List<Feature> getFeatures() {
			return lFeatures;
		}

		/**
		 * Gets Feature with the given ID number.
		 * TODO: Confirm whether this should be used or not
		 * @param a_iFeatureID Integer of ID number
		 * @return Feature with the given ID number
		 */
		public static Feature getFeatureByID(Integer a_iFeatureID) {
			for ( Feature t_feature : lFeatures ) {
				if ( !t_feature.getId().equals( a_iFeatureID.toString() ) )
					continue;
				return t_feature;
			}
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.grits.toolbox.widgets.tools.INotifyingProcess#setProgressListeners(java.util.List)
	 */
	@Override
	public void setProgressListeners(List<IProgressListener> lProgressListeners) {
		this.lProgressListeners = lProgressListeners;
	}

	/* (non-Javadoc)
	 * @see org.grits.toolbox.widgets.tools.INotifyingProcess#addProgressListeners(org.grits.toolbox.widgets.progress.IProgressListener)
	 */
	@Override
	public void addProgressListeners(IProgressListener lProgressListener) {
		this.lProgressListeners.add(lProgressListener);	
	}

	/* (non-Javadoc)
	 * @see org.grits.toolbox.widgets.tools.INotifyingProcess#getProgressListeners()
	 */
	@Override
	public List<IProgressListener> getProgressListeners() {
		return lProgressListeners;
	}

	/* (non-Javadoc)
	 * @see org.grits.toolbox.widgets.tools.INotifyingProcess#isCanceled()
	 */
	@Override
	public boolean isCanceled() {
		return this.bCancel;
	}

	/* (non-Javadoc)
	 * @see org.grits.toolbox.widgets.tools.INotifyingProcess#setCanceled(boolean)
	 */
	@Override
	public void setCanceled(boolean bCancel) {
		this.bCancel = bCancel;	
	}

	/* (non-Javadoc)
	 * @see org.grits.toolbox.widgets.tools.INotifyingProcess#updateListeners(java.lang.String, int)
	 */
	@Override
	public void updateListeners( String _sMsg, int _iVal ) {
		NotifyingProcessUtil.updateListeners(getProgressListeners(), _sMsg, _iVal);
	}

	/* (non-Javadoc)
	 * @see org.grits.toolbox.widgets.tools.INotifyingProcess#updateErrorListener(java.lang.String)
	 */
	@Override
	public void updateErrorListener(String _sMsg) {
		NotifyingProcessUtil.updateErrorListener(getProgressListeners(), _sMsg);
	}

	/* (non-Javadoc)
	 * @see org.grits.toolbox.widgets.tools.INotifyingProcess#updateErrorListener(java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void updateErrorListener(String _sMsg, Throwable t) {
		NotifyingProcessUtil.updateErrorListener(getProgressListeners(), _sMsg, t);
	}

	/* (non-Javadoc)
	 * @see org.grits.toolbox.widgets.tools.INotifyingProcess#setMaxValue(int)
	 */
	@Override
	public void setMaxValue(int _iVal) {
		NotifyingProcessUtil.setMaxValue(getProgressListeners(), _iVal);		
	}

	/* (non-Javadoc)
	 * @see org.grits.toolbox.widgets.tools.INotifyingProcess#setProgressType(org.grits.toolbox.widgets.progress.IProgressListener.ProgressType)
	 */
	@Override
	public void setProgressType(ProgressType progressType) {
		NotifyingProcessUtil.setProgressType(getProgressListeners(), progressType);

	}

}
