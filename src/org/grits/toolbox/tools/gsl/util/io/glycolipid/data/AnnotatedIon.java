package org.grits.toolbox.tools.gsl.util.io.glycolipid.data;

public class AnnotatedIon {

	private Structure m_structure;
	private String m_strIon;
	private String m_strMass;
	private String m_strDeviation;

	public void setStructure(Structure a_structure) {
		this.m_structure = a_structure;
	}

	public Structure getAnnotatedStructure() {
		return m_structure;
	}

	public void setIon(String a_strIon) {
		this.m_strIon = a_strIon;
	}

	public String getIon() {
		return m_strIon;
	}

	public void setMass(String a_strMass) {
		this.m_strMass = a_strMass;
	}

	public String getMass() {
		return m_strMass;
	}

	public void setDeviation(String a_strDeviation) {
		this.m_strDeviation = a_strDeviation;
	}

	public String getDeviation() {
		return m_strDeviation;
	}

	@Override
	public boolean equals(Object o) {
		if ( !( o instanceof AnnotatedIon ) )
			return false;
		AnnotatedIon t_o = (AnnotatedIon)o;
		if ( !this.m_structure.equals(t_o.m_structure) )
			return false;
		if ( !this.m_strMass.equals(t_o.m_strMass) )
			return false;
		if ( !this.m_strIon.equals(t_o.m_strIon) )
			return false;
		if ( !this.m_strDeviation.equals(t_o.m_strDeviation) )
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.m_structure.toString()+", Ion: "+this.m_strIon+", Mass: "+this.m_strMass+", Deviation: "+this.m_strDeviation;
	}
}
