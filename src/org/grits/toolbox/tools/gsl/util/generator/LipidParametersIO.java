package org.grits.toolbox.tools.gsl.util.generator;

import java.util.TreeSet;

import org.grits.toolbox.tools.gsl.database.lipid.LipidClass;

/**
 * Class for handling parameters for lipid generation settings.
 * The parameters can be specified as numbers and/or ranges separated by commas:
 * <pre>
 * 1-5 = 1, 2, 3, 4, 5
 * 1,3,5-7 = 1, 3, 5, 6, 7
 * </pre>
 * @author Masaaki Matsubara
 *
 */
public class LipidParametersIO {

	private LipidClass m_lipidClass;
	private TreeSet<Integer> m_setCarbonLength;
	private boolean m_bOnlyEvenCarbonLength;
	private TreeSet<Integer> m_setNumOfHydroxylGroups;
	private TreeSet<Integer> m_setNumOfOAcetylGroups;
	private TreeSet<Integer> m_setNumOfDoubleBonds;

	public LipidParametersIO() {
		this.m_lipidClass = LipidClass.FRAGMENT;
		this.m_setCarbonLength = new TreeSet<Integer>();
		this.m_bOnlyEvenCarbonLength = false;
		this.m_setNumOfHydroxylGroups = new TreeSet<Integer>();
		this.m_setNumOfOAcetylGroups = new TreeSet<Integer>();
		this.m_setNumOfDoubleBonds = new TreeSet<Integer>();
	}

	public LipidClass getLipidClass() {
		return this.m_lipidClass;
	}

	public TreeSet<Integer> getCarbonLengthes() {
		return this.m_setCarbonLength;
	}

	public boolean isOnlyEvenNumberForCarbonLength() {
		return this.m_bOnlyEvenCarbonLength;
	}

	public TreeSet<Integer> getNumbersOfHydroxylGroups() {
		return this.m_setNumOfHydroxylGroups;
	}

	public TreeSet<Integer> getNumbersOfOAcetylGroups() {
		return this.m_setNumOfOAcetylGroups;
	}

	public TreeSet<Integer> getNumbersOfDoubleBonds() {
		return this.m_setNumOfDoubleBonds;
	}

	public void setLipidClass(LipidClass a_class) {
		this.m_lipidClass = a_class;
	}

	public void addCarbonLength(int a_iCarbonLength) {
		this.m_setCarbonLength.add(a_iCarbonLength);
	}

	public void setOnlyEvenNumberForCarbonLength(boolean a_bOnlyEven) {
		this.m_bOnlyEvenCarbonLength = a_bOnlyEven;
	}

	public void addNumberOfHydroxylGroups(int a_nOH) {
		this.m_setNumOfHydroxylGroups.add(a_nOH);
	}

	public void addNumberOfOAcetylGroups(int a_nOAc) {
		this.m_setNumOfOAcetylGroups.add(a_nOAc);
	}

	public void addNumberOfDoubleBonds(int a_nUnsat) {
		this.m_setNumOfDoubleBonds.add(a_nUnsat);
	}

	/**
	 * Set value(s) of carbon lengths specified as numbers and/or ranges separated by commas.
	 * @param a_strParam String of numbers and/or ranges separated by commas
	 * @return true if the setting is done successfully
	 */
	public boolean setCarbonLengths(String a_strParam) {
		if ( a_strParam == null || a_strParam.isEmpty() )
			return false;
		// Reset parameters
		this.m_setCarbonLength.clear();
		for ( int t_iParm : LipidGeneratorUtils.parseValueRanges(a_strParam) )
			this.m_setCarbonLength.add(t_iParm);
		if ( this.m_setCarbonLength.isEmpty() )
			return false;
		return true;
	}

	/**
	 * Set number(s) of hydroxyl groups specified as numbers and/or ranges separated by commas.
	 * @param a_strParam String of numbers and/or ranges separated by commas
	 * @return true if the setting is done successfully
	 */
	public boolean setNumbersOfHydroxylGroups(String a_strParam) {
		if ( a_strParam == null || a_strParam.isEmpty() )
			return false;
		// Reset parameters
		this.m_setNumOfHydroxylGroups.clear();
		for ( int t_iParm : LipidGeneratorUtils.parseValueRanges(a_strParam) )
			this.m_setNumOfHydroxylGroups.add(t_iParm);
		if ( this.m_setNumOfHydroxylGroups.isEmpty() )
			return false;
		return true;
	}

	/**
	 * Set number(s) of O-acetyl groups specified as numbers and/or ranges separated by commas.
	 * @param a_strParam String of numbers and/or ranges separated by commas
	 * @return true if the setting is done successfully
	 */
	public boolean setNumbersOfOAcetylGroups(String a_strParam) {
		if ( a_strParam == null || a_strParam.isEmpty() )
			return false;
		// Reset parameters
		this.m_setNumOfOAcetylGroups.clear();
		for ( int t_iParm : LipidGeneratorUtils.parseValueRanges(a_strParam) )
			this.m_setNumOfOAcetylGroups.add(t_iParm);
		if ( this.m_setNumOfOAcetylGroups.isEmpty() )
			return false;
		return true;
	}

	/**
	 * Set number(s) of double bonds specified as numbers and/or ranges separated by commas.
	 * @param a_strParam String of numbers and/or ranges separated by commas
	 * @return true if the setting is done successfully
	 */
	public boolean setNumbersOfDoubleBonds(String a_strParam) {
		if ( a_strParam == null || a_strParam.isEmpty() )
			return false;
		// Reset parameters
		this.m_setNumOfDoubleBonds.clear();
		for ( int t_iParm : LipidGeneratorUtils.parseValueRanges(a_strParam) )
			this.m_setNumOfDoubleBonds.add(t_iParm);
		if ( this.m_setNumOfOAcetylGroups.isEmpty() )
			return false;
		return true;
	}

	/**
	 * Set flag for allowing only even numbered carbon length.
	 * @param a_bOnlyEven boolean indicating only even numbered carbon length is allowed
	 */
	public void allowOnlyEvenNumberedCarbonLength(boolean a_bOnlyEven) {
		this.m_bOnlyEvenCarbonLength = a_bOnlyEven;
	}

	public void fill() {
		if ( this.m_setCarbonLength.isEmpty() )
			this.m_setCarbonLength.add(0);
		if ( this.m_setNumOfHydroxylGroups.isEmpty() )
			this.m_setNumOfHydroxylGroups.add(0);
		if ( this.m_setNumOfOAcetylGroups.isEmpty() )
			this.m_setNumOfOAcetylGroups.add(0);
		if ( this.m_setNumOfDoubleBonds.isEmpty() )
			this.m_setNumOfDoubleBonds.add(0);

	}

	/**
	 * Return String of all parameters of lipid generation settings.
	 * @return String of settings parameter
	 */
	public String printParameters() {
		String t_strParams = "";
		t_strParams += this.m_lipidClass.getName()+":"+System.lineSeparator();
		t_strParams += "  Carbon length: "+this.printCarbonLengths()+System.lineSeparator();
		if ( !this.printNumbersOfHydroxylGroups().isEmpty() )
			t_strParams += "  Number of hydroxyl groups: "+this.printNumbersOfHydroxylGroups()+System.lineSeparator();
		if ( !this.printNumbersOfOAcetylGroups().isEmpty() )
			t_strParams += "  Number of O-acetyl groups: "+this.printNumbersOfOAcetylGroups()+System.lineSeparator();
		if ( !this.printNumbersOfDoubleBonds().isEmpty() )
			t_strParams += "  Number of double bonds: "+this.printNumbersOfDoubleBonds()+System.lineSeparator();
		if ( this.m_bOnlyEvenCarbonLength )
			t_strParams += "  Only allow even carbon length."+System.lineSeparator();
		return t_strParams;
	}

	public String printCarbonLengths() {
		return this.printParameterRanges(this.m_setCarbonLength);
	}

	public String printNumbersOfHydroxylGroups() {
		return this.printParameterRanges(this.m_setNumOfHydroxylGroups);
	}

	public String printNumbersOfOAcetylGroups() {
		return this.printParameterRanges(this.m_setNumOfOAcetylGroups);
	}

	public String printNumbersOfDoubleBonds() {
		return this.printParameterRanges(this.m_setNumOfDoubleBonds);
	}

	private String printParameterRanges( TreeSet<Integer> a_tsParams ) {
		if ( a_tsParams == null || a_tsParams.isEmpty() )
			return "";
		if ( a_tsParams.size() == 1 && a_tsParams.first().equals(0) )
			return "";

		String t_strParam = "";
		int t_nPre = a_tsParams.first();
		t_strParam += t_nPre;
		boolean t_bInRange = false;
		for ( int t_nParam : a_tsParams ) {
			if ( t_nPre == t_nParam ) continue;
			if ( t_nParam == t_nPre + 1 ) {
				t_bInRange = true;
			} else {
				t_strParam += ( t_bInRange )? "-" : ",";
				t_strParam += t_nParam;
				t_bInRange = false;
			}
			t_nPre = t_nParam;
		}
		if ( t_bInRange )
			t_strParam += "-"+a_tsParams.last();

		return t_strParam;
	}

	/**
	 * Calculate number of possible lipids from the setting parameters.
	 * @return Number of possible lipids
	 */
	public int calculateNumberOfCombination() {
		// Filter carbon length by flag for only even carbon length
		TreeSet<Integer> t_setCLengthes = new TreeSet<>();
		for ( Integer t_iCLength : this.m_setCarbonLength ) {
			if ( this.m_bOnlyEvenCarbonLength && t_iCLength%2 != 0 )
				continue;
			t_setCLengthes.add(t_iCLength);
		}

		// calculate combination
		int t_nCombo = t_setCLengthes.size();
		if ( !this.m_setNumOfHydroxylGroups.isEmpty() )
			t_nCombo *= this.m_setNumOfHydroxylGroups.size();
		if ( !this.m_setNumOfOAcetylGroups.isEmpty() )
			t_nCombo *= this.m_setNumOfOAcetylGroups.size();
		if ( !this.m_setNumOfDoubleBonds.isEmpty() )
			t_nCombo *= this.m_setNumOfDoubleBonds.size();

		return t_nCombo;
	}
}
