package org.grits.toolbox.tools.gsl.util.io.glycolipid.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.grits.toolbox.ms.om.data.Annotation;
import org.grits.toolbox.ms.om.data.Feature;
import org.grits.toolbox.ms.om.data.GlycanAnnotation;
import org.grits.toolbox.ms.om.data.GlycanFeature;
import org.grits.toolbox.ms.om.data.GlycolipidAnnotation;
import org.grits.toolbox.ms.om.data.GlycolipidFeature;
import org.grits.toolbox.ms.om.data.LipidAnnotation;
import org.grits.toolbox.ms.om.data.LipidFeature;
import org.grits.toolbox.ms.om.data.Peak;
import org.grits.toolbox.tools.gsl.dango.GlycanAnnotationUtils;
import org.grits.toolbox.tools.gsl.dango.LipidAnnotationUtils;
import org.grits.toolbox.tools.gsl.dango.ScanReader;

/**
 * Class for generating GlycolipidAnntoationReport for a scan.
 * @author Masaaki Matsubara
 *
 */
public class GlycolipidAnnotationReportGenerator {

	private List<Annotation> m_lAnnotations;
//	private Map<Annotation, Feature> m_mapAnnotationToFeature;

	private AnnotatedStructureProvider m_annotProv;

	public GlycolipidAnnotationReportGenerator(List<Annotation> a_lCandidateAnnotations) {
		this.m_lAnnotations = a_lCandidateAnnotations;
//		this.m_mapAnnotationToFeature = new HashMap<>();

		this.m_annotProv = new AnnotatedStructureProvider();
	}
/*
	public void putAnnotationToFeature(Annotation a_annot, Feature a_feature) {
		this.m_mapAnnotationToFeature.put(a_annot, a_feature);
	}
*/
	/**
	 * Generate GlycolipidAnnotationReport from put GlycolipidAnnotation and GlycolipidFeature for a Scan
	 * @param a_scanR - ScanReader having the matched peaks
	 * @param a_lAnnotatedFeatures - List of Feature matched to precursor ion of the specified scan
	 * @return Generated GlycolipidAnnotationReport
	 */
	public GlycolipidAnnotationReport generateGlycolipidReport(ScanReader a_scanR, List<Feature> a_lAnnotatedFeatures) {
		GlycolipidAnnotationReport t_report = new GlycolipidAnnotationReport();
		t_report.setScanNumber(a_scanR.getScan().getScanNo());
		t_report.setPrecursorPeak(a_scanR.getScan().getPrecursor());
		t_report.setPeaks(a_scanR.getFilteredPeaks());

		// Map feature to annotation
		Map<Feature, Annotation> t_mapFeatureToAnnot = this.mapFeatureToAnnotation(a_lAnnotatedFeatures);
		for ( Feature t_feature : a_lAnnotatedFeatures ) {
			Annotation t_annot = t_mapFeatureToAnnot.get(t_feature);
			t_report.putAnnotationToScore(t_annot, t_feature.getDoubleProp().get("counting_score"), t_feature.getDoubleProp().get("intensity_score"));
			t_report.putFeatureToAnnotation(t_feature, t_annot);
		}

		List<Annotation> t_lSortedAnnots = this.getSortedAnnotations(a_lAnnotatedFeatures);

		// Create and set glycolipid composition information
		System.out.println("Create compositions...");
		for ( Annotation t_annot : t_lSortedAnnots ) {
			// Generate and set composition to GlycanAnnotation
			GlycanAnnotation t_gAnnot =
					( t_annot instanceof GlycanAnnotation )?     (GlycanAnnotation)t_annot :
					( t_annot instanceof GlycolipidAnnotation )? ((GlycolipidAnnotation)t_annot).getGlycanAnnotation() :
					null;
			String t_strGComposition = "";
			if ( t_gAnnot != null ) {
				if ( t_gAnnot.getComposition() == null || t_gAnnot.getComposition().isEmpty() ) {
					t_strGComposition = GlycanAnnotationUtils.generateGlycanComposition( t_gAnnot.getSequenceGWB() );
					t_gAnnot.setComposition(t_strGComposition);
				}
				// Add glycan Annotation to composition group
				t_report.addGlycanAnnotationToCompositionGroup(t_gAnnot.getComposition(), t_gAnnot);
				t_strGComposition = t_gAnnot.getComposition();
			}

			// Generate and set composition to LipidAnnotation
			LipidAnnotation t_lAnnot =
					( t_annot instanceof LipidAnnotation )?      (LipidAnnotation)t_annot :
					( t_annot instanceof GlycolipidAnnotation )? ((GlycolipidAnnotation)t_annot).getLipidAnnotation() :
					null;
			String t_strLComposition = "";
			if ( t_lAnnot != null ) {
				if ( t_lAnnot.getComposition() == null || t_lAnnot.getComposition().isEmpty() ) {
					t_strLComposition = LipidAnnotationUtils.getLipidComposition( t_lAnnot.getSequence() );
					t_lAnnot.setComposition(t_strLComposition);
				}
				// Add lipid Annotation to composition group
				t_report.addLipidAnnotationToCompositionGroup(t_lAnnot.getComposition(), t_lAnnot);
				t_strLComposition = t_lAnnot.getComposition();
			}

			if ( t_strGComposition.isEmpty() && t_strLComposition.isEmpty() )
				continue;

			// Construct AnnotatedStructure for composition
			AnnotatedStructure t_as = this.m_annotProv.createAnnotatedGlycolipidStructure(t_strGComposition, t_strLComposition);

			// Map composition object to GlycolipidAnnotation
			t_report.addAnnotationToComposition(t_as, t_annot);

			// Add highest scores from feature
			Feature t_feature = null;
			for ( Feature t_feature0 : a_lAnnotatedFeatures ) {
				if ( t_annot.getId() != t_feature0.getAnnotationId() )
					continue;
				t_feature = t_feature0;
				break;
			}
			if ( t_feature == null )
				continue;

			/// For count score
			if ( t_as.getCountScore() == null )
				t_as.setCountScore("0.0");
			Double t_dCScore = Double.valueOf(t_as.getCountScore());
//			if ( t_dCScore < t_annot.getScores().get(a_scanR.getScan().getScanNo()+"cnt") )
//				t_dCScore = t_annot.getScores().get(a_scanR.getScan().getScanNo()+"cnt");
			if ( t_dCScore < t_feature.getDoubleProp().get("counting_score") )
				t_dCScore = t_feature.getDoubleProp().get("counting_score");
			t_as.setCountScore( t_dCScore.toString() );
			/// For intensity score
			if ( t_as.getIntensityScore() == null )
				t_as.setIntensityScore("0.0");
			Double t_dIScore = Double.valueOf(t_as.getIntensityScore());
//			if ( t_dIScore < t_annot.getScores().get(a_scanR.getScan().getScanNo()+"int") )
//				t_dIScore = t_annot.getScores().get(a_scanR.getScan().getScanNo()+"int");
			if ( t_dIScore < t_feature.getDoubleProp().get("intensity_score") )
				t_dIScore = t_feature.getDoubleProp().get("intensity_score");
			t_as.setIntensityScore( t_dIScore.toString() );
		}
		System.out.println("Done");

		// Set glycolipid compositions as AnnotatedStructure
		for ( AnnotatedStructure t_as : t_report.getGlycolipidCompositions() ) {
			t_report.addAnnotatedStructure(t_as);
			for ( Feature t_feature : a_lAnnotatedFeatures ) {
				Annotation t_annot = t_mapFeatureToAnnot.get(t_feature);
				// Add precursor Feature to AnnotatedStructures
				if ( !t_report.getAnnotations(t_as).contains(t_annot) )
					continue;
				t_report.addPrecursorFeatureToAnnotatedStructure(t_as, t_feature);
			}
		}

		this.setFeatureInformation(t_report);
/*
		// Map ID to objects
		Map<Feature, Integer> t_mapFeatureToCompID = new HashMap<>();
//		Map<Integer, GlycolipidAnnotation> t_mapIDToGLAnnot = new HashMap<>();
		Map<Integer, Feature> t_mapIDToFeature = new HashMap<>();
		for ( AnnotatedStructure t_as : t_report.getAnnotatedStructures() ) {
			for ( Annotation t_annot : t_report.getAnnotations(t_as) ) {
				// Map ID to annotation
//				t_mapIDToGLAnnot.put(t_glAnnot.getId(), t_glAnnot);

				Feature t_feature = this.m_mapAnnotationToFeature.get(t_annot);
				t_mapIDToFeature.put(Integer.valueOf( t_feature.getId() ), t_feature);
				t_mapFeatureToCompID.put(t_feature, t_as.getID());

				List<GlycanFeature> t_gFrags = new ArrayList<>();
				List<LipidFeature> t_lFrags  = new ArrayList<>();
				if ( t_feature instanceof GlycolipidFeature ) {
					GlycolipidFeature t_glFeature = (GlycolipidFeature)t_feature;
					for ( GlycolipidFeature t_glFrag : t_glFeature.getGlycolipidFragments() ) {
						t_mapIDToFeature.put(Integer.valueOf( t_glFrag.getId() ), t_glFrag);
						t_mapFeatureToCompID.put(t_glFrag, t_as.getID());
					}
					t_gFrags = t_glFeature.getGlycanFragments();
					t_lFrags = t_glFeature.getLipidFragments();
				}
				if ( t_feature instanceof GlycanFeature )
					t_gFrags = ((GlycanFeature)t_feature).getGlycanFragment();
				if ( t_feature instanceof LipidFeature )
					t_lFrags = ((LipidFeature)t_feature).getLipidFragments();

				for ( GlycanFeature t_gFrag : t_gFrags ) {
					t_mapIDToFeature.put(Integer.valueOf( t_gFrag.getId() ), t_gFrag);
					t_mapFeatureToCompID.put(t_gFrag, t_as.getID());
				}
				for ( LipidFeature t_lFrag : t_lFrags ) {
					t_mapIDToFeature.put(Integer.valueOf( t_lFrag.getId() ), t_lFrag);
					t_mapFeatureToCompID.put(t_lFrag, t_as.getID());
				}

				// Create AnnotationInformationUnit for precursor ion
				AnnotationInformationUnit t_asUnitPre = this.m_annotProv.createAnnotatedStructureUnit(0, t_as.getID(), t_feature);
				t_report.addPrecursorAnnotationInformationUnit(t_asUnitPre);
			}
		}

		// Create AnnotationInformationUnit for fragment features
		Map<Peak, List<AnnotationInformationUnit>> t_mapPeakToASUnits = new HashMap<>();
		for ( Peak t_peak : this.m_scanR.getFilteredPeaks() ) {
			t_mapPeakToASUnits.put(t_peak, new ArrayList<>());
			if ( t_peak.getFeatures().isEmpty() )
				continue;
			int t_iPeak = t_peak.getId();
			for ( int t_iFeature : t_peak.getFeatures() ) {
				Feature t_feature = t_mapIDToFeature.get(t_iFeature);
				int t_iAnnot = t_mapFeatureToCompID.get(t_feature);
				AnnotationInformationUnit t_asUnit = this.m_annotProv.createAnnotatedStructureUnit(t_iPeak, t_iAnnot, t_feature);
				t_report.addAnnotationInformationUnit(t_asUnit);
			}
		}
*/
		return t_report;
	}

	public AnnotationReport generateSubScanReport(ScanReader a_scanR, List<Feature> a_lAnnotatedFeature) {
		AnnotationReport t_report = new AnnotationReport();
		t_report.setScanNumber(a_scanR.getScan().getScanNo());
		t_report.setPrecursorPeak(a_scanR.getScan().getPrecursor());
		t_report.setPeaks(a_scanR.getFilteredPeaks());

		
		List<Feature> t_lFeatures = new ArrayList<>();
//		t_lFeatures.addAll(a_lAnnotatedFeature);
		Map<Feature, Annotation> t_mapFeatureToAnnot = this.mapFeatureToAnnotation(a_lAnnotatedFeature);
		// Map feature to annotation
		for ( Feature t_feature : a_lAnnotatedFeature ) {
			Annotation t_annot = t_mapFeatureToAnnot.get(t_feature);
			t_report.putFeatureToAnnotation(t_feature, t_annot);
		}

		for ( Feature t_feature : a_lAnnotatedFeature ) {
			// Get glycan GWB sequence
			String t_strGSeq =
					( t_feature instanceof GlycanFeature )?     ((GlycanFeature)t_feature).getSequence() :
					( t_feature instanceof GlycolipidFeature )? ((GlycolipidFeature)t_feature).getSequence() :
					"";

			// Get lipid name
			String t_strLName =
					( t_feature instanceof LipidFeature )?      ((LipidFeature)t_feature).getLipidName() :
					( t_feature instanceof GlycolipidFeature )? ((GlycolipidFeature)t_feature).getLipidName() :
					"";

			// Get a unique AnnotatedStructure for the feature
			AnnotatedStructure t_as = this.m_annotProv.createAnnotatedGlycolipidStructure(t_strGSeq, t_strLName);

			// Set scores
//			double[] t_dScores = a_lAnnotatedFeature.get(t_feature);
			if ( t_as.getCountScore() == null )
//				t_as.setCountScore( Double.valueOf(t_dScores[0]).toString() );
				t_as.setCountScore( Double.valueOf(t_feature.getDoubleProp().get("counting_score")).toString() );
			if ( t_as.getIntensityScore() == null )
//				t_as.setIntensityScore( Double.valueOf(t_dScores[1]).toString() );
				t_as.setIntensityScore( Double.valueOf(t_feature.getDoubleProp().get("intensity_score")).toString() );

			t_report.addAnnotatedStructure(t_as);
			t_report.addPrecursorFeatureToAnnotatedStructure(t_as, t_feature);
		}

		this.setFeatureInformation(t_report);

		return t_report;
	}

	private void setFeatureInformation(AnnotationReport a_report) {

		// Map ID to objects
		Map<Feature, Integer> t_mapFeatureToCompID = new HashMap<>();
//		Map<Integer, GlycolipidAnnotation> t_mapIDToGLAnnot = new HashMap<>();
		Map<Integer, Feature> t_mapIDToFeature = new HashMap<>();
		for ( AnnotatedStructure t_as : a_report.getAnnotatedStructures() ) {
			for ( Feature t_feature : a_report.getPrecursorFeatures(t_as) ) {
				t_mapIDToFeature.put(Integer.valueOf( t_feature.getId() ), t_feature);
				t_mapFeatureToCompID.put(t_feature, t_as.getID());

				List<GlycanFeature> t_gFrags = new ArrayList<>();
				List<LipidFeature> t_lFrags  = new ArrayList<>();
				if ( t_feature instanceof GlycolipidFeature ) {
					GlycolipidFeature t_glFeature = (GlycolipidFeature)t_feature;
					for ( GlycolipidFeature t_glFrag : t_glFeature.getGlycolipidFragments() ) {
						t_mapIDToFeature.put(Integer.valueOf( t_glFrag.getId() ), t_glFrag);
						t_mapFeatureToCompID.put(t_glFrag, t_as.getID());
					}
					t_gFrags = t_glFeature.getGlycanFragments();
					t_lFrags = t_glFeature.getLipidFragments();
				}
				if ( t_feature instanceof GlycanFeature )
					t_gFrags = ((GlycanFeature)t_feature).getGlycanFragment();
				if ( t_feature instanceof LipidFeature )
					t_lFrags = ((LipidFeature)t_feature).getLipidFragments();

				for ( GlycanFeature t_gFrag : t_gFrags ) {
					t_mapIDToFeature.put(Integer.valueOf( t_gFrag.getId() ), t_gFrag);
					t_mapFeatureToCompID.put(t_gFrag, t_as.getID());
				}
				for ( LipidFeature t_lFrag : t_lFrags ) {
					t_mapIDToFeature.put(Integer.valueOf( t_lFrag.getId() ), t_lFrag);
					t_mapFeatureToCompID.put(t_lFrag, t_as.getID());
				}

				// Create AnnotationInformationUnit for precursor ion
				AnnotationInformationUnit t_asUnitPre = this.m_annotProv.createAnnotatedStructureUnit(0, t_as.getID(), t_feature);
				a_report.addPrecursorAnnotationInformationUnit(t_asUnitPre);
			}
		}

		// Create AnnotationInformationUnit for fragment features
		Map<Peak, List<AnnotationInformationUnit>> t_mapPeakToASUnits = new HashMap<>();
		for ( Peak t_peak : a_report.getPeaks() ) {
			t_mapPeakToASUnits.put(t_peak, new ArrayList<>());
			if ( t_peak.getFeatures().isEmpty() )
				continue;
			int t_iPeak = t_peak.getId();
			for ( int t_iFeature : t_peak.getFeatures() ) {
				if ( !t_mapIDToFeature.containsKey(t_iFeature) ) {
					System.out.println("No feature id "+t_iFeature+"in Peak "+t_iPeak);
					continue;
				}
				Feature t_feature = t_mapIDToFeature.get(t_iFeature);
				int t_iAnnot = t_mapFeatureToCompID.get(t_feature);
				AnnotationInformationUnit t_asUnit = this.m_annotProv.createAnnotatedStructureUnit(t_iPeak, t_iAnnot, t_feature);
				a_report.addAnnotationInformationUnit(t_asUnit);
			}
		}
	}

	private Map<Feature, Annotation> mapFeatureToAnnotation(List<Feature> a_lFeatures) {
		Map<Feature, Annotation> t_mapFeatureToAnnot = new HashMap<>();
		for ( Feature t_feature : a_lFeatures ) {
			for ( Annotation t_annot : this.m_lAnnotations ) {
				if ( t_feature.getAnnotationId().intValue() != t_annot.getId().intValue() )
					continue;
				t_mapFeatureToAnnot.put(t_feature, t_annot);
				break;
			}
		}
		return t_mapFeatureToAnnot;
	}

	private List<Annotation> getSortedAnnotations(List<Feature> a_lFeatures) {
		List<Annotation> t_lSortedAnnots = new ArrayList<>();
		Map<Feature, Annotation> t_mapFeatureToAnnot = this.mapFeatureToAnnotation(a_lFeatures);
		for ( Feature t_feature : a_lFeatures ) {
			Annotation t_annot = t_mapFeatureToAnnot.get(t_feature);
			if ( t_lSortedAnnots.contains(t_annot) )
				continue;
			t_lSortedAnnots.add(t_annot);
		}
		// Sort Annotaions by ID
		Collections.sort(t_lSortedAnnots, new Comparator<Annotation>(){
			@Override
			public int compare(Annotation o1, Annotation o2) {
				return o1.getId() - o2.getId();
			}
		});
		return t_lSortedAnnots;
	}

}
