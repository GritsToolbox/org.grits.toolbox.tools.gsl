package org.grits.toolbox.tools.gsl.dango;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.grits.toolbox.ms.file.MSFile;
import org.grits.toolbox.ms.file.reader.IMSAnnotationFileReader;
import org.grits.toolbox.ms.om.data.Annotation;
import org.grits.toolbox.ms.om.data.CustomExtraData;
import org.grits.toolbox.ms.om.data.Data;
import org.grits.toolbox.ms.om.data.DataHeader;
import org.grits.toolbox.ms.om.data.Feature;
import org.grits.toolbox.ms.om.data.GlycanAnnotation;
import org.grits.toolbox.ms.om.data.GlycanFeature;
import org.grits.toolbox.ms.om.data.GlycolipidAnnotation;
import org.grits.toolbox.ms.om.data.GlycolipidFeature;
import org.grits.toolbox.ms.om.data.LipidFeature;
import org.grits.toolbox.ms.om.data.Method;
import org.grits.toolbox.ms.om.data.Peak;
import org.grits.toolbox.ms.om.data.Scan;
import org.grits.toolbox.ms.om.data.ScanFeatures;
import org.grits.toolbox.ms.om.io.xml.AnnotationWriter;
import org.grits.toolbox.widgets.progress.IProgressListener;
import org.grits.toolbox.widgets.progress.IProgressListener.ProgressType;
import org.grits.toolbox.widgets.tools.GRITSProcessStatus;
import org.grits.toolbox.widgets.tools.INotifyingProcess;
import org.grits.toolbox.widgets.tools.NotifyingProcessUtil;


/**
 * Class for DANGO annotation.
 * @author Masaaki Matsubara
 *
 */
public class DANGOAnnotation implements INotifyingProcess {
	private static final Logger logger = Logger.getLogger(DANGOAnnotation.class);

//	private String m_strScanFilename;
//	private String m_strMethodFilename;
	private String m_strZipFilepath;

	private Data m_data;

	private final static CustomExtraData countingScoreData = new CustomExtraData( "counting_score", "Counting Score", 
			"Generic Method", CustomExtraData.Type.Double, "0.00" );
	private final static CustomExtraData intensityScoreData = new CustomExtraData( "intensity_score", "Intensity Score", 
			"Second Method", CustomExtraData.Type.Double, "0.00" );

	private final static CustomExtraData rawCountingScoreData = new CustomExtraData( "raw_counting_score", "Raw Counting Score", 
			"Generic Method", CustomExtraData.Type.Double, "0.0000" );
	private final static CustomExtraData rawIntensityScoreData = new CustomExtraData( "raw_intensity_score", "Raw Intensity Score", 
			"Second Method", CustomExtraData.Type.Double, "0.0000" );

	private MSFile m_msFile;

	private boolean m_bFilterByKeyFragment;
//	private boolean m_bFilterBySubScan = false;
	private boolean m_bIntactGlycanAnnotation;

	private int m_nTotalSize;
//	private int m_iProgressValue;

	private List<IProgressListener> m_lMyProgressListeners;
	private boolean m_bCancel;

	/**
	 * 
	 * @param a_data Data having Method to be used for annotations
	 * @param a_msFile MSFile object having the full path to the mzXML/mzML file for this project
	 * @param a_strZipFilepath String of file path for storing Data and related objects
	 */
	public DANGOAnnotation(Data a_data, MSFile a_msFile, String a_strZipFilepath) {
		this.m_data = a_data;
		this.m_msFile = a_msFile;
		this.m_strZipFilepath = a_strZipFilepath;

		this.m_bFilterByKeyFragment = false;
		this.m_bIntactGlycanAnnotation = true;

		this.m_nTotalSize = -1;

		// Add feature CustomExtraData to DataHeader for the scores
		addFeatureCustomExtraData( this.m_data.getDataHeader() );
	}

	public void setFilterByKeyFragment(boolean a_bFilter) {
		this.m_bFilterByKeyFragment = a_bFilter;
	}

	public void setIntactGlycanAnnotation(boolean a_bGlycanAnnot) {
		this.m_bIntactGlycanAnnotation = a_bGlycanAnnot;
	}

	public Data getData() {
		return this.m_data;
	}

	/**
	 * Initializes objects for annotation. Add Annotations, Scans and empty ScanFeatures for each scan to Data.
	 */
	public void initialize() {
		setProgressType(ProgressType.Indeterminant);
		// Store Annotations created with Method
		this.m_data.setAnnotation( this.populateAnnotations(this.m_data.getDataHeader().getMethod()) );
		// Store Scans read from MSFile
		this.m_data.setScans( this.getScans(this.m_msFile) );
		// Set total # of scans
		this.m_nTotalSize = this.m_data.getScans().size();

		// Store empty ScanFeatures for each scan
//		ScanFeatures.usesComplexRowID = true;
		for ( Integer t_iScanNum : this.m_data.getScans().keySet() ) {
			ScanFeatures t_scanF = this.createScanFeatures( this.m_data.getScans().get(t_iScanNum), new ArrayList<>() );
			this.m_data.getScanFeatures().put(t_iScanNum, t_scanF);
		}
		setProgressType(ProgressType.Determinant);
	}

	/**
	 * Gets HashMap of scan ID to Scan from the given MSFile information.
	 * @param a_msFile MSFile having the information of scan file and the Scan reader
	 * @return HashMap of scan ID to Scan object
	 */
	protected HashMap<Integer, Scan> getScans(MSFile a_msFile) {
		if ( !(a_msFile.getReader() instanceof IMSAnnotationFileReader) )
			return null;

		HashMap<Integer, Scan> t_mapIDToScan = new HashMap<Integer, Scan>();
		IMSAnnotationFileReader t_msFileReader = (IMSAnnotationFileReader) a_msFile.getReader();
		List<Scan> t_lScans = t_msFileReader.readMSFile(a_msFile);
		for ( Scan t_scan : t_lScans )
			t_mapIDToScan.put(t_scan.getScanNo(), t_scan);
		return t_mapIDToScan;
	}

	/**
	 * Adds the DANGO custom extra data into DataHeader appropriate for "Features". <br>
	 * Currently, we have the "counting score" and the "intensity score".
	 */
	protected void addFeatureCustomExtraData(DataHeader a_dHeader) {
		a_dHeader.getFeatureCustomExtraData().add(countingScoreData);
		a_dHeader.getFeatureCustomExtraData().add(intensityScoreData);
		a_dHeader.getFeatureCustomExtraData().add(rawCountingScoreData);
		a_dHeader.getFeatureCustomExtraData().add(rawIntensityScoreData);
	}

	/**
	 * Creates a List of Annotations, including GlycolipidAnnotations generated by GlycolipidAnnotationObjectProvider
	 * (and GlycanAnnotations if {@link #m_bIntactGlycanAnnotation} is true).
	 * @param a_method Method
	 * @return List of Annotation
	 */
	public List<Annotation> populateAnnotations(Method a_method) {
		setMaxValue(1);
		updateListeners("Populate annotations", 0);

		List<Annotation> t_lAnnots = new ArrayList<>();

		// Generate candidate structure for annotations
		GlycolipidAnnotationObjectProvider t_glAnnotProv = new GlycolipidAnnotationObjectProvider();
		t_glAnnotProv.createAnnotations(a_method);
		// Add GlycolipidAnnotations
		for ( GlycolipidAnnotation t_glAnnot : t_glAnnotProv.getGlycolipidAnnotations() )
			t_lAnnots.add(t_glAnnot);

		// Add GlycanAnnotations if the flag is set
		if ( this.m_bIntactGlycanAnnotation )
			for ( GlycanAnnotation t_gAnnot : t_glAnnotProv.getGlycanAnnotations() )
				t_lAnnots.add(t_gAnnot);

		updateListeners("Done!", 1);
		return t_lAnnots;
	}

	/**
	 * Processes annotation. Create MSAnnotator, set list of IonCombinations created by IonCombinationGenerator and start annotation using MSAnnotator.
	 * @return GRITSProcessStatus.OK/ERROR/CANCEL
	 */
	public int processAnnotation() {
		// Create GlycolipidMSAnnotator
		GlycolipidMSAnnotator t_glAnnotator = new GlycolipidMSAnnotator(
				this.m_data.getDataHeader().getMethod().getTrustMzCharge(),
				this.m_data.getDataHeader().getMethod().getMonoisotopic(),
				this.m_data.getDataHeader().getMethod().getAnalyteSettings().get(0).getGlycanSettings()
			);
		// Set candidate glycolipid structures
//		int n = this.m_data.getAnnotation().size();
//		setMaxValue(n);
//		int i=0;
		for ( Annotation t_annot : this.m_data.getAnnotation() ) {
//			i++;
//			if ( i % 100 == 0 )
//				updateListeners("Adding candidate structures: "+i+"/"+n, i);
			t_glAnnotator.addCandidateAnnotation(t_annot);
		}
		

		// Generate all possible IonCombinations from Method
		IonCombinationGenerator t_ionComboGen = new IonCombinationGenerator(this.m_data.getDataHeader().getMethod());
		t_ionComboGen.generate();
		// Set the IonCombinations
		// TODO: Ion combo should be changed between precursor and fragments
		t_glAnnotator.setIonCombinationsForPrecursor(t_ionComboGen.getPossibleIonCombinations());
		t_glAnnotator.setIonCombinationsForFragments(t_ionComboGen.getPossibleIonCombinations());
		// TODO: Add selection of IonCombinations for each fragment part type
		t_glAnnotator.setIonCombinationsForGlycanFragments(t_ionComboGen.getPossibleIonCombinations());
		t_glAnnotator.setIonCombinationsForLipidFragments(t_ionComboGen.getPossibleIonCombinations());

		// Set flags
		t_glAnnotator.setFilterByKeyFeature(this.m_bFilterByKeyFragment);

		if( isCanceled() )
			return GRITSProcessStatus.CANCEL;

		// Set progress information
		setMaxValue(this.m_nTotalSize);
		updateListeners("Processing", 0);
//		this.m_iProgressValue = 0;

		// Start annotation
		int t_iRes = 0;
//		if ( this.m_msFile.getExperimentType().equals(Method.MS_TYPE_INFUSION) )
			t_iRes = this.annotateDirectInfusion(t_glAnnotator);

		updateListeners("Done!", this.m_nTotalSize);
		return t_iRes;
	}

	/**
	 * Annotates the scans of direct infusion using GlycolipidMSAnnotator
	 * @param a_glAnnotator GlycolipidAnnotator to be used for the annotation
	 * @return GRITSProcessStatus.OK/CANCEL/ERROR
	 */
	private int annotateDirectInfusion(GlycolipidMSAnnotator a_glAnnotator) {
		int t_iAnnotatedMS2 = 0;
		List<Integer> t_lMS1Scans = new ArrayList<>();
		List<Feature> t_lMS1Features = new ArrayList<>();
		List<Integer> t_lScanNums = new ArrayList<>();
		t_lScanNums.addAll( this.m_data.getScans().keySet() );
		Collections.sort(t_lScanNums);
		for ( Integer t_iScanNum : t_lScanNums ) {
			if( isCanceled() )
				return GRITSProcessStatus.CANCEL;
			Scan t_scan = this.m_data.getScans().get(t_iScanNum);

			if ( t_scan.getMsLevel() != 1 )
				continue;
			logger.debug("Processing scan #: " + t_iScanNum);
			updateListeners("Processing scan # "+t_iScanNum, t_iScanNum);

			t_lMS1Scans.add(t_iScanNum);
			// For sub scans
			boolean bRes = this.annotateSubScans(t_scan, a_glAnnotator);
			if ( !bRes ) {
				if ( isCanceled() )
					return GRITSProcessStatus.CANCEL;
				return GRITSProcessStatus.ERROR;
			}
			t_iAnnotatedMS2 += t_scan.getNumAnnotatedPeaks();
		}

		if ( t_lMS1Scans.size() > 1 ) {
			// Set # of annotated peaks for MS1
			for ( Integer t_iMS1ScanNum : t_lMS1Scans ) {
				Scan t_scan = this.m_data.getScans().get(t_iMS1ScanNum);
				t_scan.setNumAnnotatedPeaks(t_iAnnotatedMS2);
				// Collect precursor Features for MS1 ScanFeatures
				t_lMS1Features.addAll( this.m_data.getScanFeatures().get(t_iMS1ScanNum).getFeatures() );
			}

			// Set MS1 features to first MS1 ScanFeatures
			Integer t_iFirstMS1ScanNum = t_lMS1Scans.get(0);
			this.m_data.getScanFeatures().get(t_iFirstMS1ScanNum).setFeatures(t_lMS1Features);
		}

		return GRITSProcessStatus.OK;
	}

	/**
	 * Annotates sub scans of the specified Scan using the specified GlycolipidMSAnnotator.
	 * @param a_scanParent Scan to be used as parent scan
	 * @param a_glAnnotator GlycolipidMSAnnotator for annotating the sub scans
	 * @return false if thread is canceled
	 */
	private boolean annotateSubScans(Scan a_scanParent, GlycolipidMSAnnotator a_glAnnotator) {
		if ( isCanceled() )
			return false;
		// Return if no sub scan
		if ( a_scanParent.getSubScans() == null || a_scanParent.getSubScans().isEmpty() )
			return true;

		int t_nAnnotatedSubscan = 0;
		for ( Integer t_iSubScanNo : a_scanParent.getSubScans() ) {
			logger.debug("Processing scan #: " + t_iSubScanNo);
			updateListeners("Processing scan # "+t_iSubScanNo, t_iSubScanNo);
			if ( isCanceled() )
				return false;

			Scan t_scanSub = this.m_data.getScans().get(t_iSubScanNo);
			ScanReader t_scanRSub = this.wrapScan(
					t_scanSub, this.m_data.getDataHeader().getMethod()
				);

			// Annotate sub scans and get Features assigned to precursor peak
			List<Feature> t_lPrecursorFeatures;
			// Use intact structure for MS1 peak ions (MS2 precursor ions)
			if ( a_scanParent.getMsLevel() == 1 ) {
				t_lPrecursorFeatures = a_glAnnotator.annotateMS1Structures(t_scanRSub);
				// Add precursor Features to parent MS1 ScanFeatures
				this.m_data.getScanFeatures().get(a_scanParent.getScanNo()).getFeatures().addAll(t_lPrecursorFeatures);
			}
			else
				t_lPrecursorFeatures = a_glAnnotator.annotateSubScan(t_scanRSub);

			// Store fragment features to ScanFeatures
			this.m_data.getScanFeatures().get(t_iSubScanNo).setFeatures( this.getFragmentFeatures(t_lPrecursorFeatures) );

			// Skip sub scan annotation if no matched ion is assigned to precursor
			if ( t_lPrecursorFeatures.isEmpty() )
				continue;

			t_nAnnotatedSubscan++;
			if ( !this.annotateSubScans(t_scanSub, a_glAnnotator) )
				return false;
		}
		a_scanParent.setNumAnnotatedPeaks(t_nAnnotatedSubscan);

		return true;
	}

	/**
	 * Creates a new ScanFeatures.
	 * @param a_scan Scan to be stored
	 * @param a_lFeatures List of Features to be stored
	 * @return new ScanFeatures having the given Scan and List of Features
	 */
	private ScanFeatures createScanFeatures(Scan a_scan, List<Feature> a_lFeatures) {
		ScanFeatures t_scanF = new ScanFeatures();
		t_scanF.setScanId(a_scan.getScanNo());
		// Set fragment peaks
		Set<Peak> t_lScanPeaks = new HashSet<>();
		t_lScanPeaks.addAll(a_scan.getPeaklist());
		t_scanF.setScanPeaks(t_lScanPeaks);
		// Set assigned features
		t_scanF.setFeatures(a_lFeatures);
		// Set true complexRowID
		t_scanF.setUsesComplexRowId(true);
		return t_scanF;
	}

	/**
	 * Gets list of fragment Features of specified precursor Features
	 * @param a_lPrecursorFeatures List of precursor Features
	 * @return List of fragment Features
	 */
	private List<Feature> getFragmentFeatures(List<Feature> a_lPrecursorFeatures) {
		List<Feature> t_lFragmentFeatures = new ArrayList<>();
		for ( Feature t_fPre : a_lPrecursorFeatures ) {
			if ( t_fPre instanceof GlycolipidFeature ) {
				GlycolipidFeature t_glfPre = (GlycolipidFeature)t_fPre;
				t_lFragmentFeatures.addAll( t_glfPre.getGlycanFragments() );
				t_lFragmentFeatures.addAll( t_glfPre.getLipidFragments() );
				t_lFragmentFeatures.addAll( t_glfPre.getGlycolipidFragments() );
			}
			if ( t_fPre instanceof GlycanFeature ) {
				GlycanFeature t_gfPre = (GlycanFeature)t_fPre;
				t_lFragmentFeatures.addAll( t_gfPre.getGlycanFragment() );
			}
			if ( t_fPre instanceof LipidFeature ) {
				LipidFeature t_lfPre = (LipidFeature)t_fPre;
				t_lFragmentFeatures.addAll( t_lfPre.getLipidFragments() );
			}
		}
		return t_lFragmentFeatures;
	}

	/**
	 * Gets ScanReader wrapping the given Scan. Some parameters such as accuracies and intensity cutoff are also set.
	 * @param a_scan Scan to be wrapped
	 * @param a_method Method having parameters for reading Scan data
	 * @return ScanReader wrapping the specified Scan
	 */
	private ScanReader wrapScan(Scan a_scan, Method a_method) {
		// TODO:
//		System.out.println(a_scan);
		// Create ScanReader and set parameters
		ScanReader t_scanR = new ScanReader(a_scan);
		t_scanR.setShift(a_method.getShift());
		t_scanR.setAccuracy(a_method.getAccuracy(), a_method.getAccuracyPpm());
		t_scanR.setFragmentAccuracy(a_method.getFragAccuracy(), a_method.getFragAccuracyPpm());
		// Intensity cutoff for precursor (MS1)
		if ( a_scan.getMsLevel() == 1 )
			t_scanR.setIntencityCutoff(a_method.getPrecursorIntensityCutoff(), a_method.getPrecursorIntensityCutoffType());
		// Intensity cutoff for fragment
		else
			t_scanR.setIntencityCutoff(a_method.getIntensityCutoff(), a_method.getIntensityCutoffType());
		return t_scanR;
	}

	/**
	 * @see #archiveData(Data, String)
	 * @return pass/fail
	 */
	public boolean archiveData() {
		return this.archiveData(this.m_data, this.m_strZipFilepath);
	}

	/**
	 * Archives Data containing results of annotation using org.grits.toolbox.ms.om.io.xml.AnnotationWriter
	 * @param a_data Data to be archived
	 * @param a_strZipPath String of .zip file path for archive
	 * @return pass/fail
	 */
	public boolean archiveData(Data a_data, String a_strZipPath) {
		setMaxValue(1);
		updateListeners("Processing", 0);
		// Clear fragments from every features
//		System.out.println("# of parent, # of features");
		for ( ScanFeatures t_sf : a_data.getScanFeatures().values() ) {
//			Set<String> t_setParentID = new HashSet<>();
			for ( Feature t_f : t_sf.getFeatures() ) {
//				if ( t_f.getParentId() != null )
//					t_setParentID.add(t_f.getParentId());
				if ( t_f instanceof GlycanFeature )
					((GlycanFeature)t_f).getGlycanFragment().clear();
				if ( t_f instanceof LipidFeature )
					((LipidFeature)t_f).getLipidFragments().clear();
				if ( t_f instanceof GlycolipidFeature ) {
					((GlycolipidFeature)t_f).getGlycanFragments().clear();
					((GlycolipidFeature)t_f).getLipidFragments().clear();
					((GlycolipidFeature)t_f).getGlycolipidFragments().clear();
				}
			}
//			System.out.println("Scan "+t_sf.getScanId()+" "+t_setParentID.size()+" "+t_sf.getFeatures().size());
		}
		try {
			AnnotationWriter t_annWriter = new AnnotationWriter();
			t_annWriter.generateScansAnnotationFiles(null, a_data, a_strZipPath, true, true, true, true);
			updateListeners("Done!", 1);
			return true;
		} catch (Exception e) {
			logger.error("An error in archive Data objects as zip file.", e);
			updateListeners("Failed.", 1);
		}
		return false;
	}

	/**
	 * For INotifyingProcess
	 */
	
	/* (non-Javadoc)
	 * @see org.grits.toolbox.widgets.tools.INotifyingProcess#addProgressListeners(org.grits.toolbox.widgets.progress.IProgressListener)
	 */
	@Override
	public void addProgressListeners(IProgressListener lProgressListener) {
		this.m_lMyProgressListeners.add(lProgressListener);	
	}

	/* (non-Javadoc)
	 * @see org.grits.toolbox.widgets.tools.INotifyingProcess#getProgressListeners()
	 */
	@Override
	public List<IProgressListener> getProgressListeners() {
		return this.m_lMyProgressListeners;
	}

	/* (non-Javadoc)
	 * @see org.grits.toolbox.widgets.tools.INotifyingProcess#isCanceled()
	 */
	public boolean isCanceled() {
		return this.m_bCancel;
	}

	/* (non-Javadoc)
	 * @see org.grits.toolbox.widgets.tools.INotifyingProcess#setCanceled(boolean)
	 */
	@Override
	public void setCanceled(boolean bCancel) {
		this.m_bCancel = bCancel;
		// TODO: set cancel to my annotation class
//		if( bCancel && this.curGlycanMatcher != null ) {
//			this.curGlycanMatcher.setCanceled(true);
//		}		
	}	

	/* (non-Javadoc)
	 * @see org.grits.toolbox.widgets.tools.INotifyingProcess#setMaxValue(int)
	 */
	@Override
	public void setMaxValue(int _iVal) {
		NotifyingProcessUtil.setMaxValue(getProgressListeners(), _iVal);
	}

	/* (non-Javadoc)
	 * @see org.grits.toolbox.widgets.tools.INotifyingProcess#setProgressListeners(java.util.List)
	 */
	@Override
	public void setProgressListeners(List<IProgressListener> lProgressListeners) {
		this.m_lMyProgressListeners = lProgressListeners;
	}

	/* (non-Javadoc)
	 * @see org.grits.toolbox.widgets.tools.INotifyingProcess#setProgressType(org.grits.toolbox.widgets.progress.IProgressListener.ProgressType)
	 */
	@Override
	public void setProgressType(ProgressType progressType) {
		NotifyingProcessUtil.setProgressType(getProgressListeners(), progressType);

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
	 * @see org.grits.toolbox.widgets.tools.INotifyingProcess#updateListeners(java.lang.String, int)
	 */
	@Override
	public void updateListeners( String _sMsg, int _iVal ) {
		NotifyingProcessUtil.updateListeners(getProgressListeners(), _sMsg, _iVal);
	}


}
