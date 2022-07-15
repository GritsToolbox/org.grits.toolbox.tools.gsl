package org.grits.toolbox.tools.gsl.util.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.grits.toolbox.ms.annotation.structure.GlycanPreDefinedOptions;
import org.grits.toolbox.tools.gsl.dango.GlycanAnnotationUtils;
import org.grits.toolbox.tools.gsl.util.mass.ChemicalComposition;

public class TestGlycanMassCalculation {

	public static void main(String[] args) {

		List<String> t_lGWBSeqs = new ArrayList<String>();
		// aGal
		t_lGWBSeqs.add("freeEnd--??1D-Gal,p$MONO,perMe,0,0,freeEnd");
		t_lGWBSeqs.add("freeEnd/#ccleavage--??1D-Gal,p$MONO,perMe,0,0,freeEnd");
		t_lGWBSeqs.add("freeEnd/#bcleavage--??1D-Gal,p$MONO,perMe,0,0,freeEnd");
		// GD3
		t_lGWBSeqs.add("freeEnd--?b1D-Glc,p--4b1D-Gal,p--3a2D-NeuAc,p--8a2D-NeuAc,p$MONO,perMe,0,0,freeEnd");
		// GD1a
		t_lGWBSeqs.add("freeEnd/#bcleavage--?b1D-Glc,p--4b1D-Gal,p(--4b1D-GalNAc,p--3b1D-Gal,p--3a2D-NeuAc,p)--3a2D-NeuAc,p$MONO,perMe,0,0,freeEnd");
		// GD1b
//		t_strGWBSeq = "freeEnd--??1D-Glc,p--??1D-Gal,p(--??1D-GalNAc,p--??1D-Gal,p)--??2D-NeuAc,p--??2D-NeuAc,p$MONO,perMe,0,0,freeEnd";
		// GD1c
//		t_strGWBSeq = "freeEnd--??1D-Glc,p--??1D-Gal,p--??1D-GalNAc,p--??1D-Gal,p--??2D-NeuAc,p--??2D-NeuAc,p$MONO,perMe,0,0,freeEnd";
		// GM1b_GalNAc
//		t_strGWBSeq = "freeEnd--??1D-Glc,p--??1D-Gal,p--??1D-GalNAc,p--??1D-Gal,p--??2D-NeuAc,p$MONO,perMe,0,0,freeEnd";
		// GM1b_GlcNAc
//		t_strGWBSeq = "freeEnd--??1D-Glc,p--??1D-Gal,p--??1D-GlcNAc,p--??1D-Gal,p--??2D-NeuAc,p$MONO,perMe,0,0,freeEnd";

		System.out.println("None:");
		String t_strPerDeriv = GlycanPreDefinedOptions.DERIVITIZATION_NO_DERIVATIZATION;
		calcMass(t_lGWBSeqs, t_strPerDeriv);
		System.out.println("");
		System.out.println("Permethylated:");
		t_strPerDeriv = GlycanPreDefinedOptions.DERIVITIZATION_PERMETHYLATED;
		calcMass(t_lGWBSeqs, t_strPerDeriv);

	}

	public static void calcMass(List<String> a_lGWBSeqs, String a_strPerDeriv) {
		for ( String t_strGWBSeq : a_lGWBSeqs ) {
			double t_dMass = GlycanAnnotationUtils.calculateGlycanMass(t_strGWBSeq, true, a_strPerDeriv);
			System.out.println("Seq: "+t_strGWBSeq);
			System.out.println("Mass: "+t_dMass);
		}
	}
}
