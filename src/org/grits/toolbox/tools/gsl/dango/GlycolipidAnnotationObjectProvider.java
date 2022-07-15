package org.grits.toolbox.tools.gsl.dango;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.grits.toolbox.ms.om.data.AnalyteSettings;
import org.grits.toolbox.ms.om.data.GlycanAnnotation;
import org.grits.toolbox.ms.om.data.GlycolipidAnnotation;
import org.grits.toolbox.ms.om.data.LipidAnnotation;
import org.grits.toolbox.ms.om.data.Method;

/**
 * Class for providing Glycolipid (Glycan and Lipid) annotation objects.
 * Each objects are generated from AnnalyteSettings containing GlycanSettings and/or LipidSettings.
 * If AnnalyteSettings has both GlycanSettings and LipidSettings, the AnnalyteSettings will be a source of glycolipids.
 * If it has only GlycanSettings or LipidSettings, it will be a source of glycans or lipids.
 * @author Masaaki Matsubara
 *
 */
public class GlycolipidAnnotationObjectProvider {

	private int m_nAnnotations;
	private List<GlycolipidAnnotation> m_lGLAnnotations;
	private List<GlycanAnnotation> m_lGAnnotations;
	private List<LipidAnnotation> m_lLAnnotations;

	public void clear() {
		this.m_nAnnotations = 0;
		this.m_lGLAnnotations = new ArrayList<>();
		this.m_lGAnnotations = new ArrayList<>();
		this.m_lLAnnotations = new ArrayList<>();
	}

	public List<GlycolipidAnnotation> getGlycolipidAnnotations() {
		return this.m_lGLAnnotations;
	}

	public List<GlycanAnnotation> getGlycanAnnotations() {
		return this.m_lGAnnotations;
	}

	public List<LipidAnnotation> getLipidAnnotations() {
		return this.m_lLAnnotations;
	}

	/**
	 * Creates Annotation objects from the given Method.
	 * @param a_method Method to be used for the creation of Annotation objects
	 */
	public void createAnnotations(Method a_method) {
		this.clear();

		Map<String, List<GlycanAnnotation>> t_mapURIToGAnnots = new HashMap<>();
		Map<String, List<LipidAnnotation>> t_mapURIToLAnnots = new HashMap<>();
		List<String[]> t_lDBPairs = new ArrayList<>();

		// Get glycan sequences and lipid names and create the annotations
		for ( AnalyteSettings t_lAnalytes : a_method.getAnalyteSettings() ) {

			String t_strGDBURI = null;
			if ( t_lAnalytes.getGlycanSettings() != null ) {
				t_strGDBURI = t_lAnalytes.getGlycanSettings().getFilter().getDatabase();
				if ( !t_mapURIToGAnnots.containsKey(t_strGDBURI) ) {
					t_mapURIToGAnnots.put(t_strGDBURI, new ArrayList<>());
//				if ( !AnnotationSetProvider.isExistGlycanDatabase(t_strGDBURI) ) {
					String t_strPerDerivType = t_lAnalytes.getGlycanSettings().getPerDerivatisationType();
					
					// Load 0:GWBSequence, 1:GlycanID and 2:Database name
					List<String[]> t_lGSeqIDs = GlycanAnnotationUtils.getGWBSequencesAndGlycanID(t_lAnalytes.getGlycanSettings());
					if ( !t_lGSeqIDs.isEmpty() ) {
						for ( String[] t_GSeqID : t_lGSeqIDs ) {
							GlycanAnnotation t_gAnnot = this.getGlycanAnnotation(t_GSeqID[0], t_GSeqID[1], t_strPerDerivType);
							// Modify glycan string IDs if a GlycanAnnotation in the other DB has the same ID
							this.modifyGlycanStringID(t_mapURIToGAnnots, t_strGDBURI, t_gAnnot);
							t_mapURIToGAnnots.get(t_strGDBURI).add(t_gAnnot);
//							AnnotationSetProvider.putGlycanAnnotation(t_strGDBURI, t_gAnnot);
						}
					}
				}
			}

			String t_strLDBURI = null;
			if ( t_lAnalytes.getLipidSettings() != null ) {
				t_strLDBURI = t_lAnalytes.getLipidSettings().getDatabase().getURI();
//				List<LipidAnnotation> t_lLAnnots = new ArrayList<>();
				if ( !t_mapURIToLAnnots.containsKey(t_strLDBURI) ) {
					t_mapURIToLAnnots.put(t_strLDBURI, new ArrayList<>());
//				if ( !AnnotationSetProvider.isExistLipidDatabase(t_strLDBURI) ) {
					String t_strPerDerivType = t_lAnalytes.getLipidSettings().getPerDerivatisationType();
					List<String> t_lLNames = LipidAnnotationUtils.getLipidNames( t_lAnalytes.getLipidSettings() );
					if ( !t_lLNames.isEmpty() ) {
						for ( String t_strLName : t_lLNames ) {
							LipidAnnotation t_lAnnot = this.getLipidAnnotation(t_strLName, t_strPerDerivType);
							t_mapURIToLAnnots.get(t_strLDBURI).add(t_lAnnot);
//							AnnotationSetProvider.putLipidAnnotation(t_strLDBURI, t_lAnnot);
						}
					}
				}
			}

			// Set pair of glycan DB and lipid DB
			String[] t_strDBPair = {t_strGDBURI, t_strLDBURI};
			t_lDBPairs.add(t_strDBPair);
		}

		for ( String[] t_strDBPair : t_lDBPairs ) {
			if ( t_strDBPair[0] != null && t_strDBPair[1] != null ) {
				// Create GlycolipidAnnotations
				/// For each glycan annotation
				for ( GlycanAnnotation t_gAnnot : t_mapURIToGAnnots.get(t_strDBPair[0]) ) {
					/// For each lipid annotation
					for ( LipidAnnotation t_lAnnot : t_mapURIToLAnnots.get(t_strDBPair[1]) ) {
						this.m_lGLAnnotations.add( this.getGlycolipidAnnotation(t_gAnnot, t_lAnnot) );
					}
				}
				continue;
			}
//			if ( t_strDBPair[0] != null && t_strDBPair[1] == null ) {
			if ( t_strDBPair[0] != null ) {
				this.m_lGAnnotations.addAll(t_mapURIToGAnnots.get(t_strDBPair[0]));
				continue;
			}
//			if ( t_strDBPair[0] == null && t_strDBPair[1] != null ) {
			if ( t_strDBPair[1] != null ) {
				this.m_lLAnnotations.addAll(t_mapURIToLAnnots.get(t_strDBPair[1]));
				continue;
			}
		}


	}

	private GlycolipidAnnotation getGlycolipidAnnotation(GlycanAnnotation a_gAnnot, LipidAnnotation a_lAnnot) {
		// Create new one
		GlycolipidAnnotation t_glAnnot = new GlycolipidAnnotation();
		t_glAnnot.setId( ++this.m_nAnnotations );
		// TODO: Check how to store the parameters
		// Set glycolipid sequence as combined glycan and lipid
		String t_strGLSeq = a_gAnnot.getSequence().replace(",freeEnd", ","+a_lAnnot.getSequence()+"=0.0000u");
		t_glAnnot.setSequence(t_strGLSeq);
//		t_glAnnot.setType();
//		t_glAnnot.setScores();
//		t_glAnnot.setSelected();
		// TODO: Determine how to make StringId
		t_glAnnot.setStringId( a_gAnnot.getStringId()+"--"+a_lAnnot.getStringId() );
		t_glAnnot.setGlycanAnnotation(a_gAnnot);
		t_glAnnot.setLipidAnnotation(a_lAnnot);
		t_glAnnot.setPerDerivatisationType( a_gAnnot.getPerDerivatisationType() ); // Use glycan's one
		return t_glAnnot;
	}

	private GlycanAnnotation getGlycanAnnotation(String a_strGSeq, String a_strGID, String a_strPerDeriv) {
		GlycanAnnotation t_gAnnot = new GlycanAnnotation();
		t_gAnnot.setId( ++this.m_nAnnotations );
//		t_gAnnot.setSequenceFormat();
//		t_gAnnot.setGlycanId( a_strGID );
		t_gAnnot.setStringId(a_strGID);
		// TODO: Don't make composition at this because to be too slow
//		String t_strComposition = GlycanAnnotationUtils.generateGlycanComposition(a_strGSeq);
//		if ( !t_strComposition.isEmpty() )
//			t_gAnnot.setComposition(t_strComposition);
		t_gAnnot.setSequence(a_strGSeq); // GWB sequence
		t_gAnnot.setSequenceGWB(a_strGSeq);
		t_gAnnot.setPerDerivatisationType( a_strPerDeriv );
		return t_gAnnot;
	}

	private LipidAnnotation getLipidAnnotation(String a_strLName, String a_strPerDeriv) {
		LipidAnnotation t_lAnnot = new LipidAnnotation();
		t_lAnnot.setId( ++this.m_nAnnotations );
		String t_strComposition = LipidAnnotationUtils.getLipidComposition(a_strLName);
		if ( !t_strComposition.isEmpty() )
			t_lAnnot.setComposition(t_strComposition);
		t_lAnnot.setSequence(a_strLName);
		t_lAnnot.setStringId(a_strLName); // Set Lipid name
		t_lAnnot.setPerDerivatisationType( a_strPerDeriv );
		return t_lAnnot;
	}

	private void modifyGlycanStringID(Map<String, List<GlycanAnnotation>> a_mapURIToGAnnots, String a_strURI, GlycanAnnotation a_gAnnot) {
		// Check duplicated ID between DBs
		// Add the DB file name to ID if the same IDs are used between glycans in the different DBs
		boolean t_bDuplicated = false;
		for ( String t_strURI : a_mapURIToGAnnots.keySet() ) {
			if ( t_strURI.equals(a_strURI) )
				continue;
			String t_strDBName = (new File(t_strURI)).getName().replace(".xml", "");
			t_strDBName = "(DB:"+t_strDBName+")";
			for ( GlycanAnnotation t_gAnnot : a_mapURIToGAnnots.get(t_strURI) ) {
				String t_strGStringID = t_gAnnot.getStringId();
				// Remove DB name if the string id already contains it
				if ( t_strGStringID.contains(t_strDBName) )
					t_strGStringID = t_strGStringID.replace(t_strDBName, "");
				if ( t_strGStringID.equals( a_gAnnot.getStringId() ) ) {
					t_gAnnot.setStringId( t_strDBName+t_strGStringID );
					t_bDuplicated = true;
				}
			}
		}
		if ( t_bDuplicated ) {
			String t_strDBName = (new File(a_strURI)).getName().replace(".xml", "");
			t_strDBName = "(DB:"+t_strDBName+")";
			a_gAnnot.setStringId( t_strDBName+a_gAnnot.getStringId() );
		}
	}

}
