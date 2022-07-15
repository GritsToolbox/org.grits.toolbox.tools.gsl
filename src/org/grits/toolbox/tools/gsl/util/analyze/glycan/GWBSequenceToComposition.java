package org.grits.toolbox.tools.gsl.util.analyze.glycan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarImporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.application.glycanbuilder.Glycan;

public class GWBSequenceToComposition {

	private static final Logger logger = Logger.getLogger(GWBSequenceToComposition.class);

	public String getCompositionString(String a_strGWBSeq) {
		String t_strComposition = "";
		try {
			Glycan t_glycan = Glycan.fromString(a_strGWBSeq);
			String t_glycoCT = t_glycan.toGlycoCTCondensed();
			SugarImporterGlycoCTCondensed t_importer = new SugarImporterGlycoCTCondensed();
			Sugar t_sugar = t_importer.parse(t_glycoCT);
			// calculate composition
			GlycoVisitorComposition t_visitor = new GlycoVisitorComposition();
			t_visitor.start(t_sugar);
			HashMap<String, Integer> t_mapCompositonToCount = t_visitor.getComposition();
			t_strComposition = this.getCompositionString(t_mapCompositonToCount);
		} catch (SugarImporterException e) {
			logger.error(e);
		} catch (GlycoVisitorException e) {
			logger.error(e);
		}
		return t_strComposition;
	}

	private String getCompositionString(HashMap<String, Integer> a_composition) {
		String t_result = "";
		List<String> t_lComposition = new ArrayList<String>();
		for (String t_strComposition : a_composition.keySet())
			t_lComposition.add(t_strComposition);

		Collections.sort(t_lComposition);
		for (String t_strComposition : t_lComposition) {
			t_result += this.formatComposition(t_strComposition) + a_composition.get(t_strComposition).toString() + " ";
		}
		return t_result;
	}

	private String formatComposition(String a_string) {
		String t_string = a_string;
		String t_strSubst = "";
		if ( t_string.contains("-phosphate") ) {
			t_strSubst += "P";
			t_string = t_string.replace("-phosphate", "");
		}
		if ( t_string.contains("-sulfate") ) {
			t_strSubst += "S";
			t_string = t_string.replace("-sulfate", "");
		}
		String t_strComposition =
				(t_string.equals("hex"))?                   "Hex" :
				(t_string.equals("hexA"))?                  "HexA" :
				(t_string.equals("dhex"))?                  "dHex" :
				(t_string.equals("hex-n-acetyl"))?          "HexNAc" :
				(t_string.equals("dnonA-ulop-n-acetyl"))?   "NeuAc" :
				(t_string.equals("dnonA-ulop-n-glycolyl"))? "NeuAc" :
				(t_string.equals("dnonA-ulop"))?            "KDN"   :
				t_string;
		t_strComposition += t_strSubst;
		return t_strComposition;
	}

}
