package org.grits.toolbox.tools.gsl.database.lipid;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class for storing the parameters for generation of candidate lipids.
 * This is a component of LipidDatabse.
 * TODO: Change class name better than "parameters"
 * @author Masaaki Matsubara
 * @see LipidDatabase
 *
 */
@XmlRootElement(name = "parameters")
public class LipidParameters {

	private String m_strCarbonLengths;
	private boolean m_bIsAllowedOnlyEvenNumbers;
	private String m_strNumOHs;
	private String m_strNumOAcs;
	private String m_strNumDBs;

	@XmlAttribute(name = "length", required = true)
	public void setCarbonLengths(String a_strCarbonLengths) {
		this.m_strCarbonLengths = a_strCarbonLengths;
	}

	@XmlAttribute(name = "only_even", required = false)
	public void setIsAllowedOnlyEvenNumbers(boolean m_bIsAllowedOnlyEvenNumbers) {
		this.m_bIsAllowedOnlyEvenNumbers = m_bIsAllowedOnlyEvenNumbers;
	}

	@XmlAttribute(name = "hydoroxy", required = false)
	public void setNumberOfHydroxylGroups(String a_strNumOHs) {
		this.m_strNumOHs = a_strNumOHs;
	}

	@XmlAttribute(name = "o_acetyl", required = false)
	public void setNumberOfOAcetylGroups(String a_strNumOAcs) {
		this.m_strNumOAcs = a_strNumOAcs;
	}

	@XmlAttribute(name = "double_bond", required = false)
	public void setNumberOfDoubleBonds(String a_strNumDBs) {
		this.m_strNumDBs = a_strNumDBs;
	}

	public String getCarbonLengths() {
		return this.m_strCarbonLengths;
	}

	public boolean getIsAllowedOnlyEvenNumbers() {
		return this.m_bIsAllowedOnlyEvenNumbers;
	}

	public String getNumberOfHydroxylGroups() {
		return this.m_strNumOHs;
	}

	public String getNumberOfOAcetylGroups() {
		return this.m_strNumOAcs;
	}

	public String getNumberOfDoubleBonds() {
		return this.m_strNumDBs;
	}

}
