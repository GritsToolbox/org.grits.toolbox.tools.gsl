package org.grits.toolbox.tools.gsl.util.mass;

/**
 * Enum class for atomic masses<br>
 * Atomic weight of principal isotpes are retrieved from the CIAAW web site.
 * {@link http://www.ciaaw.org/atomic-masses.htm}
 * @author Masaaki Matsubara
 *
 */
public enum AtomicMass implements IMass {

	H   ( "H"     , "1.0078250322", "2.0141017781", "0.01150" ,           null,      null ),
	D   ( "D"     , "2.0141017781",           null,       null,           null,      null ),
	Li  ( "Li"    , "7.01600344",   "6.01512289",   "5.09721" ,           null,      null ),
	C   ( "C"     , "12.000000000", "13.003354835", "1.08157" ,           null,      null ),
	C13 ( "(C^13)", "13.003354835",           null,       null,           null,      null ),
	N   ( "N"     , "14.003074004", "15.000108899", "0.36936" ,           null,      null ),
	O   ( "O"     , "15.994914620", "16.999131757", "0.03809" , "17.999159613", "0.20550" ),
	F   ( "F"     , "18.998403163",           null,       null,           null,      null ),
	Na  ( "Na"    , "22.989769282",           null,       null,           null,      null ),
	P   ( "P"     , "30.973761998",           null,       null,           null,      null ),
	S   ( "S"     , "31.972071174", "32.971458910", "0.00804" , "33.9678670"  , "0.0460"  ),
	K   ( "K"     , "38.96370649",  "40.96182526",  "7.21675" , "39.9639982"  , "0.01255"  );

	private String m_strSymbol;
	private String m_strMass0;
	private String m_strMass1;
	private String m_strRelativeIntencity1;
	private String m_strMass2;
	private String m_strRelativeIntencity2;

	private AtomicMass( String a_strSymbol, String a_strMass0, String a_strMass1, String a_strRelInt1, String a_strMass2, String a_strRelInt2 ) {
		this.m_strSymbol = a_strSymbol;
		this.m_strMass0 = a_strMass0;
		this.m_strMass1 = a_strMass1;
		this.m_strRelativeIntencity1 = a_strRelInt1;
		this.m_strMass2 = a_strMass2;
		this.m_strRelativeIntencity2 = a_strRelInt2;
	}

	public String getSymbol() {
		return this.m_strSymbol;
	}

	public String getExactMass() {
		return this.m_strMass0;
	}

	public String getMassPlus1() {
		return this.m_strMass1;
	}

	public String getRelativeIntensityPlus1() {
		return this.m_strRelativeIntencity1;
	}

	public String getMassPlus2() {
		return this.m_strMass2;
	}

	public String getRelativeIntensityPlus2() {
		return this.m_strRelativeIntencity2;
	}

	/**
	 * Gets AtomicMass having the given symbol.
	 * @param a_strSymbol String of atom symbol
	 * @return AtomicMass having the given symbol (null if no match was found)
	 */
	public static AtomicMass forSymbol( String a_strSymbol ) {
		for ( AtomicMass t_enumAM : AtomicMass.values() ) {
			if ( !t_enumAM.m_strSymbol.equals(a_strSymbol) ) continue;
			return t_enumAM;
		}
		return null;
	}
}
