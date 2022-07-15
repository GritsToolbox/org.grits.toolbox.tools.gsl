package org.grits.toolbox.tools.gsl.util.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.grits.toolbox.ms.annotation.structure.GlycanPreDefinedOptions;
import org.grits.toolbox.ms.om.data.Fragment;
import org.grits.toolbox.tools.gsl.dango.GlycanAnnotationUtils;
import org.grits.toolbox.tools.gsl.util.mass.ChemicalComposition;

public class TestGlycanFragmentGeneration {

	public static void main(String[] args) {
		List<String> t_lTypes = new ArrayList<>();
		t_lTypes.add(Fragment.TYPE_A);
		t_lTypes.add(Fragment.TYPE_B);
		t_lTypes.add(Fragment.TYPE_C);
		t_lTypes.add(Fragment.TYPE_X);
		t_lTypes.add(Fragment.TYPE_Y);
		t_lTypes.add(Fragment.TYPE_Z);

		List<Fragment> t_lFragments = new ArrayList<>();
		for ( String t_strType : t_lTypes ) {
			Fragment t_frag = new Fragment();
			t_frag.setType(t_strType);
			t_frag.setNumber("-1");
			t_lFragments.add(t_frag);
		}

		

		String t_strGWBSeq = "";
		// aGal
//		t_strGWBSeq = "freeEnd--??1D-Gal,p$MONO,perMe,0,0,freeEnd";
		// GD3
//		t_strGWBSeq = "freeEnd--?b1D-Glc,p--4b1D-Gal,p--3a2D-NeuAc,p--8a2D-NeuAc,p$MONO,perMe,0,0,freeEnd";
		// GD1a
//		t_strGWBSeq = "freeEnd/#bcleavage--?b1D-Glc,p--4b1D-Gal,p(--4b1D-GalNAc,p--3b1D-Gal,p--3a2D-NeuAc,p)--3a2D-NeuAc,p$MONO,perMe,0,0,freeEnd";
		// GD1b
//		t_strGWBSeq = "freeEnd--??1D-Glc,p--??1D-Gal,p(--??1D-GalNAc,p--??1D-Gal,p)--??2D-NeuAc,p--??2D-NeuAc,p$MONO,perMe,0,0,freeEnd";
		// GD1c
//		t_strGWBSeq = "freeEnd--??1D-Glc,p--??1D-Gal,p--??1D-GalNAc,p--??1D-Gal,p--??2D-NeuAc,p--??2D-NeuAc,p$MONO,perMe,0,0,freeEnd";
		// GM1b_GalNAc
//		t_strGWBSeq = "freeEnd--??1D-Glc,p--??1D-Gal,p--??1D-GalNAc,p--??1D-Gal,p--??2D-NeuAc,p$MONO,perMe,0,0,freeEnd";
		// GM1b_GlcNAc
//		t_strGWBSeq = "freeEnd--??1D-Glc,p--??1D-Gal,p--??1D-GlcNAc,p--??1D-Gal,p--??2D-NeuAc,p$MONO,perMe,0,0,freeEnd";
		// Composition
		t_strGWBSeq = "freeEnd}(((((--??1Hex,p)--??1Hex,p)--??1Hex,p)--??1HexNAc,p)--??1HexNAc,p)--??1HexNAc,p$MONO,perMe,Na,0,freeEnd";

		String t_strPerDeriv = GlycanPreDefinedOptions.DERIVITIZATION_NO_DERIVATIZATION;
//		String t_strPerDeriv = GlycanPreDefinedOptions.DERIVITIZATION_PERMETHYLATED;
		List<String[]> t_lGlycanFragSeq
		= GlycanAnnotationUtils.generateGlycanFragments(t_strGWBSeq, true, t_strPerDeriv, t_lFragments, 2, 1);

		BigDecimal bdMassCH2 = new BigDecimal( ChemicalComposition.parseFormula("CH2").getMonoisotopicMass() ).setScale(10, BigDecimal.ROUND_HALF_UP);
		BigDecimal bdH = new BigDecimal( ChemicalComposition.parseFormula("H").getMonoisotopicMass() ).setScale(10, BigDecimal.ROUND_HALF_UP);
		BigDecimal bdNa = new BigDecimal( ChemicalComposition.parseFormula("Na").getMonoisotopicMass() ).setScale(10, BigDecimal.ROUND_HALF_UP);

		File file = new File(TestResourcePath.RESOURCE_DIR+"gfrags.txt");
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			String t_strChemForm = GlycanAnnotationUtils.calculateGlycanChemicalComposition(t_strGWBSeq, true, t_strPerDeriv).getFormula();
			double t_dMass = GlycanAnnotationUtils.calculateGlycanMass(t_strGWBSeq, true, t_strPerDeriv);
			pw.println("Original: ");
			pw.println(t_strGWBSeq+"\n"+t_strChemForm+":M:"+t_dMass);
			pw.println("Fragments:");
			for ( String[] t_strFrag : t_lGlycanFragSeq ) {
				t_strChemForm = GlycanAnnotationUtils.calculateGlycanChemicalComposition(t_strFrag[0], true, t_strPerDeriv).getFormula();
				t_dMass = GlycanAnnotationUtils.calculateGlycanMass(t_strFrag[0], true, t_strPerDeriv);
				// Change OMe to OH at freeEnd
				t_dMass -= bdMassCH2.doubleValue();
				double t_dMassH = t_dMass + bdH.doubleValue();
				double t_dMassNa = t_dMass + bdNa.doubleValue();
				double t_dMassNa2 = ( t_dMassNa + bdNa.doubleValue() )/2;
				pw.println(t_strFrag[0]+"\n"+t_strChemForm+":Fragment:"+t_strFrag[1]+":M - CH2:"+t_dMassH+":+H:"+t_dMassH+":+Na:"+t_dMassNa+":+2Na:"+t_dMassNa2);
			}
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
