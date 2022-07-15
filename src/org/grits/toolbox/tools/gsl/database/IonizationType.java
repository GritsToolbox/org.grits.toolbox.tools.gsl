package org.grits.toolbox.tools.gsl.database;

import javax.xml.bind.annotation.XmlEnumValue;

/**
 * Definition of typical ionization types.
 * @author Masaaki Matsubara
 *
 */
public enum IonizationType {

	@XmlEnumValue("M")          NOION("M", 0),
	@XmlEnumValue("M-H2O")      NOIONMH2O("M-H2O", 0),
	@XmlEnumValue("[M+H]+")     PH("[M+H]+", 1),
	@XmlEnumValue("[M-H]-")     MH("[M-H]-", -1),
	@XmlEnumValue("[M+Na]+")    PNA("[M+Na]+", 1),
	@XmlEnumValue("[M+H-H2O]+") PHMH2O("[M+H-H2O]+", 1);

	private String m_strSymbol;
	private int m_iCharge;

	private IonizationType(String a_strSymbol, int a_iCharge) {
		this.m_strSymbol = a_strSymbol;
		this.m_iCharge = a_iCharge;
	}

	public String getSymbol() {
		return this.m_strSymbol;
	}

	public int getCharge() {
		return this.m_iCharge;
	}
}
