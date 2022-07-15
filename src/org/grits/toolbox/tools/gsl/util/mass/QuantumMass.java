package org.grits.toolbox.tools.gsl.util.mass;

/**
 * Enum class for quantum masses.
 * Contains proton, electron, and neutron.
 * @author Masaaki Matsubara
 *
 */
public enum QuantumMass implements IMass {

	p ( "p", "1.007276"    ,  1 ),
	e ( "e", "0.0005485799", -1 ),
	n ( "n", "1.008665"    ,  0 );

	private String m_strSymbol;
	private String m_strMass;
	private int m_iCharge;

	private QuantumMass( String a_strSymbol, String a_strMass, int a_iCharge ) {
		this.m_strSymbol = a_strSymbol;
		this.m_strMass = a_strMass;
		this.m_iCharge = a_iCharge;
	}

	public String getSymbol() {
		return this.m_strSymbol;
	}

	public String getExactMass() {
		return this.m_strMass;
	}

	public int getCharge() {
		return this.m_iCharge;
	}
}
