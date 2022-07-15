package org.grits.toolbox.tools.gsl.dango;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.grits.toolbox.ms.annotation.structure.GlycanPreDefinedOptions;
import org.grits.toolbox.ms.om.data.LipidSettings;
import org.grits.toolbox.tools.gsl.database.lipid.LipidData;
import org.grits.toolbox.tools.gsl.database.lipid.LipidDatabase;
import org.grits.toolbox.tools.gsl.structure.LipidFragmentInfo;
import org.grits.toolbox.tools.gsl.structure.LipidNameParser;
import org.grits.toolbox.tools.gsl.structure.lipid.Ceramide;
import org.grits.toolbox.tools.gsl.structure.lipid.ILipid;
import org.grits.toolbox.tools.gsl.structure.lipid.Sphingosine;
import org.grits.toolbox.tools.gsl.util.generator.structure.LipidFragmenter;
import org.grits.toolbox.tools.gsl.util.io.lipid.LipidDatabaseFileHandler;
import org.grits.toolbox.tools.gsl.util.mass.ChemicalComposition;
import org.grits.toolbox.tools.gsl.util.mass.ChemicalCompositionProvider;

/**
 * Utility class for lipid annotation.
 * @author Masaaki Matsubara
 *
 */
public class LipidAnnotationUtils {

	/**
	 * Gets lipid composition of a lipid having the given name.
	 * @param a_strLipName String of the lipid name
	 * @return String of the name of lipid composition
	 */
	public static String getLipidComposition(String a_strLipName) {
		ILipid t_lip = LipidNameParser.parseLipidName( a_strLipName );
		if ( t_lip == null )
			return "";
		return t_lip.getCompositionName();
	}

	/**
	 * Gets list of lipid fragment names and the types
	 * @param a_strLipName String of lipid name to be fragmented
	 * @param a_strFragmentType String of fragment type
	 * @return List of String array (String[0]: name of lipid fragment, String[1]: type of lipid fragment)
	 */
	public static List<String[]> getLipidFragments(String a_strLipName, String a_strFragmentType) {
		List<String[]> t_lLipFrags = new ArrayList<>();
		ILipid t_lip = LipidNameParser.parseLipidName( a_strLipName );
		if ( t_lip == null )
			return t_lLipFrags;
		LipidFragmenter t_lipFrager = new LipidFragmenter();
		for ( LipidFragmentInfo t_fragInfo : t_lipFrager.fragment(t_lip) ) {
			if ( a_strFragmentType != null )
				t_fragInfo.addFragmentType(a_strFragmentType);
			String[] t_strLipFragInfo = new String[2];
			t_strLipFragInfo[0] = t_fragInfo.getFragment().getName();
			t_strLipFragInfo[1] = t_fragInfo.getFragmentType();
			t_lLipFrags.add(t_strLipFragInfo);
		}
		return t_lLipFrags;
	}

	/**
	 * @see #getLipidFragments(String, String)
	 * @param a_strLipName String of lipid name to be fragmented
	 * @return List of String[] contains lipid fragment information
	 */
	public static List<String[]> getLipidFragments(String a_strLipName) {
		return getLipidFragments(a_strLipName, null);
	}

	/**
	 * Calculates Lipid mass with per derivatization type
	 * @param a_strLipName String of lipid name
	 * @param t_strPerDerivType String of per derivatization type (specified in org.grits.toolbox.ms.annotation.structure.GlycanPreDefinedOptions)
	 * @return BigDecimal of calculated mass (null if lipid name cannot be parsed)
	 */
	public static BigDecimal calculateLipidMass( String a_strLipName, String t_strPerDerivType ) {
		ILipid t_lip = LipidNameParser.parseLipidName( a_strLipName );
		if ( t_lip == null )
			return null;
		ChemicalComposition t_ccLip = ChemicalCompositionProvider.getChemicalComposition(t_lip);
		if ( t_strPerDerivType.equals(GlycanPreDefinedOptions.DERIVITIZATION_PERMETHYLATED) )
			t_ccLip.derivatize();
		return new BigDecimal( t_ccLip.getMonoisotopicMass() );
	}

	/**
	 * Gets lipid names stored in lipid database in the given LipidSettings
	 * @param a_lSettings LipidSettings having the lipid database
	 * @return List of Strings of lipid names
	 */
	public static List<String> getLipidNames(LipidSettings a_lSettings) {
		List<String> t_lLipNames = new ArrayList<>();
		String t_strLDB = a_lSettings.getDatabase().getURI();
		LipidDatabase t_lDB = LipidDatabaseFileHandler.importXML(t_strLDB);
		if ( t_lDB != null )
			for ( LipidData t_ld : t_lDB.getLipidData() )
				t_lLipNames.add( t_ld.getCommonName() );
		return t_lLipNames;
	}

	/**
	 * Checks a lipid having the given name can connect to glycan.
	 * @param a_strLipName String of a lipid name
	 * @return true if the lipid can connect to glycan
	 */
	public static boolean canConnectToGlycan(String a_strLipName) {
		ILipid t_lip = LipidNameParser.parseLipidName(a_strLipName);
		if ( !(t_lip instanceof Ceramide) && !(t_lip instanceof Sphingosine) )
			return false;
		// Count number of hydroxyl group on sphingosine
		int t_nOH = 0;
		if ( t_lip instanceof Ceramide )
			t_nOH = ((Ceramide)t_lip).getSphingosine().getNumberOfHydroxylGroups();
		if ( t_lip instanceof Sphingosine )
			t_nOH = ((Sphingosine)t_lip).getNumberOfHydroxylGroups();
		if ( t_nOH < 1 )
			return false;
		return true;
	}
}
