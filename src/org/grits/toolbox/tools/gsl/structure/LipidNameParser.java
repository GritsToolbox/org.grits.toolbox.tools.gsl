package org.grits.toolbox.tools.gsl.structure;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.grits.toolbox.tools.gsl.database.lipid.LipidClass;
import org.grits.toolbox.tools.gsl.structure.lipid.Ceramide;
import org.grits.toolbox.tools.gsl.structure.lipid.FattyAcid;
import org.grits.toolbox.tools.gsl.structure.lipid.ILipid;
import org.grits.toolbox.tools.gsl.structure.lipid.Lipid;
import org.grits.toolbox.tools.gsl.structure.lipid.Sphingomyelin;
import org.grits.toolbox.tools.gsl.structure.lipid.Sphingosine;

/**
 * Class for parsing lipid name. Type of lipids are defined in LipidClass.
 * @author Masaaki Matsubara
 * @see org.grits.toolbox.tools.gsl.database.lipid.LipidClass
 *
 */
public class LipidNameParser {

	/**
	 * Parses lipid name into a lipid.
	 * @param a_strLipid String of lipid name
	 * @return ILipid, a lipid parsed from the name
	 */
	public static ILipid parseLipidName(String a_strLipid) {
		String t_strExp = "(.*?)\\((.*?)\\)(.*)";
		Matcher t_match = Pattern.compile(t_strExp).matcher(a_strLipid);
		if ( !t_match.find() )
			return null;

		String t_strClass  = t_match.group(1);
		String t_strParams = t_match.group(2);
		String t_strSubsts = t_match.group(3);

		LipidClass t_lc = LipidClass.forAbbreviation(t_strClass);
		if ( t_lc == null )
			return null;

		ILipid t_lip = null;
		switch ( t_lc ) {
		case SPHINGOMYELIN:
			// Split parameters by "/"
			if ( !t_strParams.contains("/") )
				return null;
			String[] t_sepParams = t_strParams.split("/");
			// Generate sphingosine and fatty acid
			List<Integer> t_listPsSp = parseLipidParameters(t_sepParams[0]);
			List<Integer> t_listPsFA = parseLipidParameters(t_sepParams[1]);
			if ( t_listPsSp == null || t_listPsFA == null )
				return null;
			Sphingosine t_sp = new Sphingosine(t_listPsSp.get(0), t_listPsSp.get(1), 0, t_listPsSp.get(2));
			FattyAcid t_fa   = new FattyAcid(t_listPsFA.get(0), t_listPsFA.get(1), 0, t_listPsFA.get(2));
			t_lip = new Sphingomyelin(t_sp, t_fa);
			break;
		case CERAMIDE:
			// Split parameters by "/"
			if ( !t_strParams.contains("/") )
				return null;
			t_sepParams = t_strParams.split("/");
			// Generate sphingosine and fatty acid
			t_listPsSp = parseLipidParameters(t_sepParams[0]);
			t_listPsFA = parseLipidParameters(t_sepParams[1]);
			if ( t_listPsSp == null || t_listPsFA == null )
				return null;
			t_sp = new Sphingosine(t_listPsSp.get(0), t_listPsSp.get(1), 0, t_listPsSp.get(2));
			t_fa = new FattyAcid(t_listPsFA.get(0), t_listPsFA.get(1), 0, t_listPsFA.get(2));
			t_lip = new Ceramide(t_sp, t_fa);
			break;
		case SPHINGOSINE:
			t_listPsSp = parseLipidParameters(t_strParams);
			if ( t_listPsSp == null )
				return null;
			t_lip = new Sphingosine(t_listPsSp.get(0), t_listPsSp.get(1), 0, t_listPsSp.get(2));
			break;
		case FATTY_ACID:
			t_listPsFA = parseLipidParameters(t_strParams);
			if ( t_listPsFA == null )
				return null;
			t_lip = new FattyAcid(t_listPsFA.get(0), t_listPsFA.get(1), 0, t_listPsFA.get(2));
			break;
		case FRAGMENT:
			List<Integer> t_listPs = parseLipidParameters(t_strParams);
			if ( t_listPs == null )
				return null;
			// Extract substituents and its numbers
			Map<String, Integer> t_mapSubstToNum = parseSubstituents(t_strSubsts);
			if ( t_mapSubstToNum == null )
				return null;
			int t_nNH2 = 0;
			int t_nCOOH = 0;
			int t_nCONH2 = 0;
			for ( String t_strSubstName : t_mapSubstToNum.keySet() ) {
				if ( t_strSubstName.equals("NH2") )
					t_nNH2 = t_mapSubstToNum.get(t_strSubstName);
				else if ( t_strSubstName.equals("COOH") )
					t_nCOOH = t_mapSubstToNum.get(t_strSubstName);
				else if ( t_strSubstName.equals("CONH2") )
					t_nCONH2 = t_mapSubstToNum.get(t_strSubstName);
				else
					return null;
			}
			t_lip = new Lipid(t_nNH2, t_nCOOH, t_nCONH2, t_listPs.get(0), t_listPs.get(1), 0, t_listPs.get(2));
			break;
		default:
			break;
		}
		
		return t_lip;
	}

	/**
	 * Parses String of lipid parameters which contain # of hydroxyl groups, carbon length and # of double bonds
	 * @param a_strParam String of lipid parameters
	 * @return List of the numbers (carbon length, # of hydroxyl groups and # of double bonds)
	 */
	public static List<Integer> parseLipidParameters(String a_strParam) {
		String t_strExp = "^(h|d|t|tet)?([0-9]+):([0-9]+)$";
		Matcher t_match = Pattern.compile(t_strExp).matcher(a_strParam);
		if ( !t_match.find() )
			return null;

		int t_nOH = 0;
		int t_nC = 0;
		int t_nDB = 0;

		String t_strOH = t_match.group(1);
		String t_strC  = t_match.group(2);
		String t_strDB = t_match.group(3);

		// Exchange # of hydroxyl groups
		if ( t_strOH != null ) {
			t_nOH = (t_strOH.equals(""))? 0 :
					(t_strOH.equals("h"))? 1 :
					(t_strOH.equals("d"))? 2 :
					(t_strOH.equals("t"))? 3 :
					(t_strOH.equals("tet"))? 4 :
					-1;
			if ( t_nOH < 0 )
				return null;
		}

		// Exchange carbon length
		try {
			t_nC = Integer.valueOf(t_strC);
		} catch (NumberFormatException e) {
			return null;
		}

		// Exchange # of double bonds
		try {
			t_nDB = Integer.valueOf(t_strDB);
		} catch (NumberFormatException e) {
			return null;
		}

		return Arrays.asList(t_nC, t_nOH, t_nDB);
	}

	/**
	 * Parses String containing substituent names and the number.
	 * @param a_strSubsts String containing substituent names and the number
	 * @return Map of the parsed substituent name to the number
	 */
	public static Map<String, Integer> parseSubstituents(String a_strSubsts) {
		Map<String, Integer> t_mapSubstToNum = new HashMap<>();
		if ( a_strSubsts == null || a_strSubsts.isEmpty() )
			return t_mapSubstToNum;

		// Error if no "("
		if ( !a_strSubsts.contains("(") )
			return null;

		String[] t_splitSubsts = a_strSubsts.split("\\(", -1);
		// Error if there is a string before first "("
		if ( !t_splitSubsts[0].equals("") )
			return null;

		for ( int i=1; i<t_splitSubsts.length; i++ ) {
			// Error if no ")"
			if ( !t_splitSubsts[i].contains(")") )
				return null;
			String[] t_splitSubstNum = t_splitSubsts[i].split("\\)", -1);
			// Error if two or more ")" are contained
			if ( t_splitSubstNum.length != 2 )
				return null;
			String t_strName = t_splitSubstNum[0];
			String t_strNum  = t_splitSubstNum[1];
			// Error if name of substituent is empty
			if ( t_strName.isEmpty() )
				return null;
			// Error if there are two or more same names
			if ( t_mapSubstToNum.containsKey(t_strName) )
				return null;

			if ( t_strNum.isEmpty() ) {
				t_mapSubstToNum.put(t_strName, 1);
				continue;
			}

			int t_nSubst = 0;
			// Error if # of the substituent is not integer
			try {
				t_nSubst = Integer.valueOf(t_strNum);
			} catch (NumberFormatException e) {
				return null;
			}
			t_mapSubstToNum.put(t_strName, t_nSubst);
		}
		return t_mapSubstToNum;
	}


}
