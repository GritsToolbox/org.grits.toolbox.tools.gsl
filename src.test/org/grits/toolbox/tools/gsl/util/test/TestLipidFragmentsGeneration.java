package org.grits.toolbox.tools.gsl.util.test;

import java.util.Arrays;
import java.util.List;

import org.grits.toolbox.tools.gsl.structure.LipidFragmentInfo;
import org.grits.toolbox.tools.gsl.structure.LipidNameParser;
import org.grits.toolbox.tools.gsl.structure.lipid.Ceramide;
import org.grits.toolbox.tools.gsl.structure.lipid.ILipid;
import org.grits.toolbox.tools.gsl.structure.lipid.Lipid;
import org.grits.toolbox.tools.gsl.util.generator.structure.LipidFragmenter;
import org.grits.toolbox.tools.gsl.util.mass.CeramideChemicalComposition;
import org.grits.toolbox.tools.gsl.util.mass.ChemicalComposition;
import org.grits.toolbox.tools.gsl.util.mass.LipidChemicalComposition;

public class TestLipidFragmentsGeneration {

	public static void main(String[] args) {
		List<String> t_listLipids = Arrays.asList(
				"Cer(d18:1/16:0)",
				"Sp(d18:1)",
				"FA(16:0)",
				"Fr(h18:2)",
				"Fr(h18:2)(CONH2)",
				"FA(16:0",
				"Cer(d18:1)",
				"Sp(d18:1/16:0)",
				"Fr(h18:2)(Xxx)",
				"");
		for ( String t_strLipid : t_listLipids ) {
			ILipid t_lip = LipidNameParser.parseLipidName(t_strLipid);
			if ( t_lip == null )
				System.err.println("Invalid lipid name:"+t_strLipid);
		}

		String t_strLipid = "Cer(t18:0/26:0)";
		ILipid t_lip = LipidNameParser.parseLipidName(t_strLipid);
		if ( t_lip == null ) {
			System.err.println("Invalid lipid name:"+t_strLipid);
			System.exit(0);
		}
		printMass(t_lip);
		System.out.println();
		System.out.println("Fragments of "+t_lip.getName());

		LipidFragmenter t_genFrags = new LipidFragmenter();
		// Set limit for # of hydroxy group cleavages (neutral loss)
		t_genFrags.setWaterLossLimit(2);
		List<LipidFragmentInfo> t_frags = t_genFrags.fragment(t_lip);
		if ( t_frags == null ) {
			System.err.println("Fragmentation failed.");
			System.exit(0);
		}
		for ( LipidFragmentInfo t_fragInfo : t_frags ) {
			ILipid t_frag = t_fragInfo.getFragment();
			printMass(t_frag);
		}
	}

	private static void printMass(ILipid a_lip) {
		ChemicalComposition t_calcMass;
		if ( a_lip instanceof Ceramide )
			t_calcMass = new CeramideChemicalComposition((Ceramide)a_lip);
		else
			t_calcMass = new LipidChemicalComposition((Lipid)a_lip);
		System.out.println(a_lip.getName()+":");
		System.out.println("M\t\t"+t_calcMass.getFormula()+" "+t_calcMass.getMonoisotopicMass());
		t_calcMass.derivatize();
		System.out.println("M(PerMe)\t"+t_calcMass.getFormula()+" "+t_calcMass.getMonoisotopicMass());
	}
}
