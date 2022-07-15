package org.grits.toolbox.tools.gsl.util.test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.grits.toolbox.ms.om.data.Annotation;
import org.grits.toolbox.ms.om.data.GlycanAnnotation;
import org.grits.toolbox.ms.om.data.GlycanFeature;
import org.grits.toolbox.ms.om.data.GlycanSettings;
import org.grits.toolbox.ms.om.data.GlycolipidAnnotation;
import org.grits.toolbox.ms.om.data.GlycolipidFeature;
import org.grits.toolbox.ms.om.data.Method;
import org.grits.toolbox.ms.om.data.Scan;
import org.grits.toolbox.tools.gsl.dango.GlycolipidAnnotationObjectProvider;
import org.grits.toolbox.tools.gsl.dango.GlycolipidMSAnnotator;
import org.grits.toolbox.tools.gsl.dango.IonCombinationGenerator;
import org.grits.toolbox.tools.gsl.dango.ScanReader;
import org.grits.toolbox.tools.gsl.structure.IonCombination;
import org.grits.toolbox.tools.gsl.util.io.om.MSDataModelFileHandlar;

public class TestGlycolipidMSAnnotator extends GlycolipidMSAnnotator {

	public TestGlycolipidMSAnnotator(boolean a_bTrustMzCharge, boolean a_bIsMonoIsotopic, GlycanSettings a_gSet) {
		super(a_bTrustMzCharge, a_bIsMonoIsotopic, a_gSet);
	}

	public static void main(String[] args) {

		String t_strScanFilename = TestResourcePath.MZXML2_PATH;
		String t_strMethodFilename = TestResourcePath.RESOURCE_DIR+"method.xml";

		List<Scan> t_lScans = MSDataModelFileHandlar.readNumberedScansFromMzXML(t_strScanFilename);
		// Map scan to ID
		Scan t_scanMS2 = null;
		for ( Scan t_scan : t_lScans ) {
			if ( t_scan.getMsLevel() != 2 )
				continue;
			t_scanMS2 = t_scan;
			break;
		}

		Method t_method = MSDataModelFileHandlar.readMethodXML(t_strMethodFilename);

		// Create ScanReader and set parameters
		ScanReader t_scanR = new ScanReader(t_scanMS2);
//		ScanReader t_scanR = ScanReader.getInstanceWithSubScanInfo(t_strScanFilename, t_scan.getScanNo());
//		if ( t_scanR == null )
//			continue;
		t_scanR.setShift(t_method.getShift());
		t_scanR.setAccuracy(t_method.getAccuracy(), t_method.getAccuracyPpm());
		t_scanR.setFragmentAccuracy(t_method.getFragAccuracy(), t_method.getFragAccuracyPpm());
		t_scanR.setIntencityCutoff(t_method.getIntensityCutoff(), t_method.getIntensityCutoffType());

		boolean t_bFilterByKeyFragment = false;

		// Generate candidate annotations
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
	
		IonCombination t_ionCombo = null;
		for ( IonCombination t_ionCombo0 : t_lIonCombinations ) {
			if ( !t_ionCombo0.toString().equals("2Na") )
				continue;
			t_ionCombo = t_ionCombo0;
		}

		// Construct GlycolipidMSAnnotator
		TestGlycolipidMSAnnotator t_glAnnotator = new TestGlycolipidMSAnnotator(
				t_method.getTrustMzCharge(),
				t_method.getMonoisotopic(),
				t_method.getAnalyteSettings().get(0).getGlycanSettings()
			);
		// For filtering by key feature
		t_glAnnotator.setFilterByKeyFeature(t_bFilterByKeyFragment);
		// Set candidate GlycolipidAnnotations as Annotations
		for ( Annotation t_annotCandidate : t_lGLAnnotations )
			t_glAnnotator.addCandidateAnnotation(t_annotCandidate);
		// Set ion combos for precursor and fragment ions
		t_glAnnotator.setIonCombinationsForPrecursor(t_lIonCombinations);
		t_glAnnotator.setIonCombinationsForFragments(t_lIonCombinations);

		GlycolipidAnnotation t_annot = t_lGLAnnotations.get(0);
		GlycolipidFeature t_glFeature = new GlycolipidFeature();
		t_glFeature.setSequence("freeEnd--??1D-Glc,p--??1D-Gal,p(--??1D-GalNAc,p--??1D-Gal,p)--??2D-NeuAc,p--??2D-NeuAc,p$MONO,perMe,0,0,freeEnd");
		t_glFeature.setLipidName("");
		BigDecimal t_bdGLMass = t_glAnnotator.calculateMass(t_glFeature, t_annot.getPerDerivatisationType());
		System.out.println( t_glAnnotator.calculateIonizedMz(t_bdGLMass, t_ionCombo) );

		GlycanAnnotation t_gAnnot = t_lGAnnotations.get(0);
		GlycanFeature t_gFeature = new GlycanFeature();
		t_gFeature.setSequence("freeEnd--??1D-Glc,p--??1D-Gal,p(--??1D-GalNAc,p--??1D-Gal,p)--??2D-NeuAc,p--??2D-NeuAc,p$MONO,perMe,0,0,freeEnd");
		t_glFeature.setLipidName("");
		BigDecimal t_bdGMass = t_glAnnotator.calculateMass(t_gFeature, t_annot.getPerDerivatisationType());
		System.out.println( t_glAnnotator.calculateIonizedMz(t_bdGMass, t_ionCombo) );
		
}

}
