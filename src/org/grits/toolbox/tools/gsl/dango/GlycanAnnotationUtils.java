package org.grits.toolbox.tools.gsl.dango;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.eurocarbdb.application.glycanbuilder.Atom;
import org.eurocarbdb.application.glycanbuilder.BuilderWorkspace;
import org.eurocarbdb.application.glycanbuilder.FragmentCollection;
import org.eurocarbdb.application.glycanbuilder.FragmentEntry;
import org.eurocarbdb.application.glycanbuilder.Fragmenter;
import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.eurocarbdb.application.glycanbuilder.GlycanRendererAWT;
import org.eurocarbdb.application.glycanbuilder.IonCloud;
import org.eurocarbdb.application.glycanbuilder.MassOptions;
import org.eurocarbdb.application.glycanbuilder.Molecule;
import org.eurocarbdb.application.glycanbuilder.ResidueDictionary;
import org.grits.toolbox.ms.annotation.structure.AnalyteStructure;
import org.grits.toolbox.ms.annotation.structure.GlycanDatabase;
import org.grits.toolbox.ms.annotation.structure.GlycanPreDefinedOptions;
import org.grits.toolbox.ms.annotation.structure.GlycanStructure;
import org.grits.toolbox.ms.om.data.Fragment;
import org.grits.toolbox.ms.om.data.GlycanSettings;
import org.grits.toolbox.tools.gsl.util.analyze.glycan.GWBSequenceToComposition;
import org.grits.toolbox.tools.gsl.util.io.glycan.GlycanDatabaseFileHandler;
import org.grits.toolbox.tools.gsl.util.mass.AtomicMass;
import org.grits.toolbox.tools.gsl.util.mass.ChemicalComposition;

/**
 * Utility class for mass calculation and fragmentation of glycan using GlycoWorkbench modules.
 * These calculation need GlycoWorkbench glycan sequence.
 * @author Masaaki Matsubara
 *
 */
public class GlycanAnnotationUtils {

	public static final Logger logger = Logger.getLogger(GlycanAnnotationUtils.class);

	private static Map<String, String> mapGWBSeqToComposition = new HashMap<>();

	/**
	 * Generates list of glycan fragment sequences
	 * @param a_strGSeq String of glycan sequence for fragmentation
	 * @param a_bMono True if monoisotopic mass
	 * @param a_strPerDeriv String of per derivatization type
	 * @param a_lFragments List of Fragments
	 * @param a_nMaxClvg int, max number of cleavages
	 * @param a_nMaxClvgCR int, max number of cross ring cleavages
	 * @return List of glycan fragment sequences
	 */
	public static List<String[]> generateGlycanFragments(
			String a_strGSeq, boolean a_bMono, String a_strPerDeriv,
			List<Fragment> a_lFragments, int a_nMaxClvg, int a_nMaxClvgCR
		) {

		List<String[]> t_lFragmentSeqs = new ArrayList<>();

		new BuilderWorkspace(new GlycanRendererAWT());

		// allow all types of fragments
		Fragmenter t_fragmenter = new Fragmenter();
		//initialize all of the fragment types false to avoid the default value which is true
		t_fragmenter.setComputeBFragments(false);
		t_fragmenter.setComputeYFragments(false);
		t_fragmenter.setComputeCFragments(false);
		t_fragmenter.setComputeZFragments(false);
		t_fragmenter.setComputeAFragments(false);
		t_fragmenter.setComputeXFragments(false);
		t_fragmenter.setIterate_ion_combinations(false);

		for ( Fragment fragment : a_lFragments ) {
			if(fragment.getType().equals(Fragment.TYPE_B)){
				t_fragmenter.setComputeBFragments(true);
			} else if(fragment.getType().equals(Fragment.TYPE_Y)){
				t_fragmenter.setComputeYFragments(true);
			} else if(fragment.getType().equals(Fragment.TYPE_C)){
				t_fragmenter.setComputeCFragments(true);
			} else if(fragment.getType().equals(Fragment.TYPE_Z)){
				t_fragmenter.setComputeZFragments(true);
			} else if(fragment.getType().equals(Fragment.TYPE_A)){
				t_fragmenter.setComputeAFragments(true);
			} else if(fragment.getType().equals(Fragment.TYPE_X)){
				t_fragmenter.setComputeXFragments(true);
			}
		}

		// set the number of allowed fragments
		t_fragmenter.setMaxNoCleavages(a_nMaxClvg);
		t_fragmenter.setMaxNoCrossRings(a_nMaxClvgCR);
		try{
			// load the glycan object and generate its fragments
			Glycan t_glycan = Glycan.fromString(a_strGSeq);
			setMassOptions(t_glycan, a_bMono, a_strPerDeriv );
			FragmentCollection fc = t_fragmenter.computeAllFragments(t_glycan);
			for ( FragmentEntry t_ent : (List<FragmentEntry>)fc.getFragments() ) {
				String[] t_strStructureAndFragType = new String[2];
				t_strStructureAndFragType[0] = t_ent.getStructure();
				t_strStructureAndFragType[1] = t_ent.name;
				t_lFragmentSeqs.add(t_strStructureAndFragType);
			}
		}catch(Exception e){
			logger.error("An error in glycan fragment generation", e);
		}
		return t_lFragmentSeqs;
	}

	/**
	 * Generates glycan composition string from the given GWBSequence.
	 * Cash will be used if the GWBSequence has already exchanged to composition.
	 * Returns empty ("") if this doesn't work.
	 * @param a_strGSeq String of GWBSequnce to be exchanged to glycan composition
	 * @return String of glycan composition exchanged from the specified GWBSequence ("" if failed)
	 */
	public static String generateGlycanComposition(String a_strGSeq) {
		if ( a_strGSeq == null || a_strGSeq.isEmpty() )
			return "";
		if ( !mapGWBSeqToComposition.containsKey(a_strGSeq) ) {
			GWBSequenceToComposition t_exchange = new GWBSequenceToComposition();
			mapGWBSeqToComposition.put(a_strGSeq, t_exchange.getCompositionString(a_strGSeq));
		}
		return mapGWBSeqToComposition.get(a_strGSeq);
	}

	/**
	 * Calculates the specified glycan mass with the specified parameter (is monoisotopic, perderivatization type)
	 * @param a_strGSeq String of GWB sequence
	 * @param a_bMono boolean of monoisotopic or not
	 * @param t_strPerDerivType String of perderivatization type
	 * @return double value of the glycan mass
	 */
	public static double calculateGlycanMass( String a_strGSeq, boolean a_bMono, String t_strPerDerivType ) {
		Glycan t_glycan = Glycan.fromString( a_strGSeq );
		setMassOptions(t_glycan, a_bMono, t_strPerDerivType);
		double t_dGMass = t_glycan.computeMass();
		// Recalculate the mass of the cleaved sulfate and phosphate (deviated by the mass of an water molecule H2O)
		String[] t_strCleavages = {"S/#lcleavage", "P/#lcleavage"};
		int t_nCleavages = 0;
		for ( String t_strCleavage : t_strCleavages ) {
			if ( !a_strGSeq.contains(t_strCleavage) )
				continue;
			// Count the number of cleavages
			int t_iSize = a_strGSeq.length();
			int t_iReplaced = a_strGSeq.replace(t_strCleavage, "").length();
			t_nCleavages += (t_iSize - t_iReplaced)/t_strCleavage.length();
		}
		// Subtract the mass of total deviation
		t_dGMass -= 18.0105646844 * t_nCleavages;
		return t_dGMass;
	}

	/**
	 * TBC
	 * @param a_strGSeq String of GWB sequence
	 * @param a_bMono is monoisotopic or not
	 * @param t_strPerDerivType String of perderivatization type
	 * @return ChemicalComposition ChemicalComposition of the given glycan
	 */
	public static ChemicalComposition calculateGlycanChemicalComposition( String a_strGSeq, boolean a_bMono, String t_strPerDerivType ) {
		Glycan t_glycan = Glycan.fromString( a_strGSeq );
		setMassOptions(t_glycan, a_bMono, t_strPerDerivType);
		try {
			ChemicalComposition t_cc = new ChemicalComposition();
			Molecule m = t_glycan.computeMolecule();
			for ( Entry<Atom, Integer> a : m.getAtoms() )
				t_cc.addNumberOfElements(AtomicMass.forSymbol(a.getKey().getSymbol()), a.getValue());
			return t_cc;
		} catch (Exception e) {
			logger.error("Cannot calculate chemical composition from Glycan.", e);
		}
		return null;
	}

	/**
	 * Gets pairs of GWB sequence and the glycan id from a GlycanDatabase in the given GlycanSettings.
	 * TODO: Switch to the GELATO system
	 * @param a_gSettings GlycanSettings having GlycanDatabase to be read GWB sequences and glycan ids
	 * @return List of String arrays having pairs of [0] GWB sequence and [1] the glycan id
	 */
	public static List<String[]> getGWBSequencesAndGlycanID(GlycanSettings a_gSettings) {
		List<String[]> t_lGWBSeqIDs = new ArrayList<>();
		GlycanDatabase t_gDB = GlycanDatabaseFileHandler.getGlycanDatabase( a_gSettings.getFilter().getDatabase() );
		if ( t_gDB != null ) {
			// Check duplicated ID
			boolean t_bHasDuplicated = false;
			Map<String, List<AnalyteStructure>> t_mapIdToStructures = new HashMap<>();
			for(AnalyteStructure t_gStructure : t_gDB.getStructures() ) {
				if ( !t_mapIdToStructures.containsKey(t_gStructure.getId()) )
					t_mapIdToStructures.put(t_gStructure.getId(), new ArrayList<>());
				t_mapIdToStructures.get(t_gStructure.getId()).add(t_gStructure);
				if ( !t_bHasDuplicated && t_mapIdToStructures.get(t_gStructure.getId()).size() > 1 )
					t_bHasDuplicated = true;
			}
			// Make the duplicated ids unique to add different number If existing.
			if ( t_bHasDuplicated ) {
				for ( String t_strId : t_mapIdToStructures.keySet() ) {
					List<AnalyteStructure> t_lStructures = t_mapIdToStructures.get(t_strId);
					if ( t_lStructures.size() == 1 )
						continue;
					int i = 1;
					for ( AnalyteStructure t_gStructure : t_lStructures )
						t_gStructure.setId( t_strId + "." + i++ );
				}
			}
			for ( AnalyteStructure t_gStructure : t_gDB.getStructures() ) {
				String[] t_GWBSeqID = new String[2];
				t_GWBSeqID[0] = ((GlycanStructure) t_gStructure).getGWBSequence();
				t_GWBSeqID[1] = t_gStructure.getId();
				t_lGWBSeqIDs.add(t_GWBSeqID);
			}
		}

		return t_lGWBSeqIDs;
	}

	public static boolean isPermethylated(String a_strPerDerivForGRITS) {
		return a_strPerDerivForGRITS.equals(GlycanPreDefinedOptions.DERIVITIZATION_PERMETHYLATED);
	}

	/**
	 * Checks a glycan having the specified sequence can connect to reducing end.
	 * Checks whether the reducing end of the sequence is freeEnd or not.
	 * @param a_strGSeq String of Glycoworkbench sequence
	 * @return true if the glycan can connect to reducing end
	 */
	public static boolean canConnectReducingEnd(String a_strGSeq) {
		// TODO: Check how to decide
		return a_strGSeq.startsWith("freeEnd");
	}

	/**
	 * Sets mass options including derivatization and mass type to Glycan. Doesn't set reducing end option.
	 * @param a_glycan Target glycan to be set mass options
	 * @param a_bIsMono boolean, whether mass is monoisotopic or not
	 * @param a_strPerDerivForGRITS String, perderivatization type
	 */
	public static void setMassOptions(Glycan a_glycan, boolean a_bIsMono, String a_strPerDerivForGRITS) {
		MassOptions t_massOptions = new MassOptions();
		//Map the derivitasiation type
		String t_strDerivTypeForMassOption =
			( a_strPerDerivForGRITS.equals(GlycanPreDefinedOptions.DERIVITIZATION_PERMETHYLATED) )?
					MassOptions.PERMETHYLATED :
			( a_strPerDerivForGRITS.equals(GlycanPreDefinedOptions.DERIVITIZATION_HEAVYPERMETHYLATION) )?
					MassOptions.HEAVYPERMETHYLATION :
			( a_strPerDerivForGRITS.equals(GlycanPreDefinedOptions.DERIVITIZATION_PERDMETHYLATED) )?
					MassOptions.PERDMETHYLATED :
			( a_strPerDerivForGRITS.equals(GlycanPreDefinedOptions.DERIVITIZATION_PERACETYLATED) )?
					MassOptions.PERACETYLATED :
			( a_strPerDerivForGRITS.equals(GlycanPreDefinedOptions.DERIVITIZATION_PERDACETYLATED) )?
					MassOptions.PERDACETYLATED :
					MassOptions.NO_DERIVATIZATION;
		t_massOptions.setDerivatization(t_strDerivTypeForMassOption);

		//Map the Monoisotopic
		if(a_bIsMono)
			t_massOptions.setIsotope(MassOptions.ISOTOPE_MONO);
		else
			t_massOptions.setIsotope(MassOptions.ISOTOPE_AVG);

		// Set IonCloud to remove default ions
		t_massOptions.ION_CLOUD = new IonCloud();
		t_massOptions.NEUTRAL_EXCHANGES = new IonCloud();

		// Set Reducing End as free end
		t_massOptions.setReducingEndType( ResidueDictionary.findResidueType("freeEnd") );

		a_glycan.setMassOptions(t_massOptions);
	}
}
