package org.grits.toolbox.tools.gsl.structure;

import java.util.HashMap;
import java.util.Map;

import org.grits.toolbox.tools.gsl.structure.lipid.ILipid;

/**
 * Class for storing lipid fragment information.
 * @author Masaaki Matsubara
 *
 */
public class LipidFragmentInfo {

	public static String Z = "Z";
	public static String Y = "Y";
	public static String W = "W";
	public static String U = "U";

	private ILipid m_lipFragment;
	private Map<String, Integer> m_mapFragmentTypeToNum;

	public LipidFragmentInfo(ILipid a_lipFragment) {
		this.m_lipFragment = a_lipFragment;
		this.m_mapFragmentTypeToNum = new HashMap<>();
	}

	/**
	 * Adds the given fragment type with the given number
	 * @param a_strFragmentType String of fragment type
	 * @param a_nFragmentType number of the given fragment type
	 */
	public void addFragmentType(String a_strFragmentType, int a_nFragmentType) {
		if ( a_nFragmentType == 0 )
			return;

		int t_nFragType = a_nFragmentType;
		if ( this.m_mapFragmentTypeToNum.containsKey(a_strFragmentType) ) {
			t_nFragType += this.m_mapFragmentTypeToNum.get(a_strFragmentType);
			// Remove the fragment type when the number is zero
			if ( t_nFragType == 0 ) {
				this.m_mapFragmentTypeToNum.remove(a_strFragmentType);
				return;
			}
		}
		this.m_mapFragmentTypeToNum.put(a_strFragmentType, t_nFragType);
	}

	/**
	 * Add fragment types from a String concatenated with hyphen
	 * @param a_strFragmentType String of fragment type
	 */
	public void addFragmentType(String a_strFragmentType) {
		if ( a_strFragmentType == null )
			return;
		// Split the string by "-"
		for ( String t_strFragmentType : a_strFragmentType.split("-") ) {
			String t_strFragmentType0 = t_strFragmentType;
			int t_nFragmentType = 1;
			if ( t_strFragmentType.isEmpty() )
				continue;
			// Parse number at the head
			for ( int i=1; i<t_strFragmentType.length(); i++ ) {
				String t_strSub = t_strFragmentType.substring(0, i);
				try {
					t_nFragmentType = Integer.parseInt(t_strSub);
					t_strFragmentType0 = t_strFragmentType.substring(i);
				} catch (NumberFormatException e) {
					break;
				}
			}
			this.addFragmentType(t_strFragmentType0, t_nFragmentType);
		}
	}

	public ILipid getFragment() {
		return m_lipFragment;
	}

	public Map<String, Integer> getFragmentTypeToNumber() {
		return this.m_mapFragmentTypeToNum;
	}

	/**
	 * Gets fragment type as a String.
	 * @return String of fragment type
	 */
	public String getFragmentType() {
		String t_strFragment = "";
		String t_strH2OLoss = "";
		for ( String t_strFragType : this.m_mapFragmentTypeToNum.keySet() ) {
			Integer t_nFrag = this.m_mapFragmentTypeToNum.get(t_strFragType);
			if ( t_nFrag == null || t_nFrag == 0)
				continue;
			if ( t_strFragType.contains("H2O") ) {
				t_strH2OLoss += "-";
				if ( t_nFrag > 1 )
					t_strH2OLoss += t_nFrag;
				t_strH2OLoss += "H2O";
				continue;
			}
			t_strFragment += "-";
			if ( t_nFrag > 1 )
				t_strFragment += t_nFrag;
			t_strFragment += t_strFragType;
		}
		t_strFragment += t_strH2OLoss;

		return t_strFragment;
	}

}
