package org.grits.toolbox.tools.gsl.database.lipid;

import javax.xml.bind.annotation.XmlEnumValue;

/**
 * Definition of lipid classes.
 * @author Masaaki Matsubara
 *
 */
public enum LipidClass {

	@XmlEnumValue("SM")  SPHINGOMYELIN("Sphingomyelin", "SM"),
	@XmlEnumValue("Cer") CERAMIDE("Ceramide", "Cer"),
	@XmlEnumValue("Sp")  SPHINGOSINE("Sphingosine", "Sp"),
	@XmlEnumValue("FA")  FATTY_ACID("Fatty Acid", "FA"),
	@XmlEnumValue("Fr")  FRAGMENT("Fragment", "Fr");

	private String m_strName;
	private String m_strAbbr;

	private LipidClass(String a_strName, String a_strAbbr) {
		this.m_strName = a_strName;
		this.m_strAbbr = a_strAbbr;
	}

	public String getName() {
		return this.m_strName;
	}

	public String getAbbreviation() {
		return this.m_strAbbr;
	}

	/**
	 * Get LipidClass having the given name.
	 * @param a_strName String of the name of LipidClass
	 * @return LipidClass having the given name (null if no match was found)
	 */
	public static LipidClass forName(String a_strName) {
		for ( LipidClass t_lc : LipidClass.values() ) {
			if ( !t_lc.m_strName.equals(a_strName) ) continue;
			return t_lc;
		}
		return null;
	}

	/**
	 * Get LipidClass having the given abbreviation.
	 * @param a_strAbbr String of the abbreviation of LipidClass name
	 * @return LipidClass having the given abbreviation (null if no match was found)
	 */
	public static LipidClass forAbbreviation(String a_strAbbr) {
		for ( LipidClass t_lc : LipidClass.values() ) {
			if ( !t_lc.m_strAbbr.equals(a_strAbbr) ) continue;
			return t_lc;
		}
		return null;
	}
}
