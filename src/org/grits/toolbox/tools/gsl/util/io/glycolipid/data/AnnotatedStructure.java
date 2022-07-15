package org.grits.toolbox.tools.gsl.util.io.glycolipid.data;

public class AnnotatedStructure {

	private int m_iID;
	private Structure m_structure;
	private String m_strCountScore;
	private String m_strIntensityScore;

	public void setID(int a_iID) {
		this.m_iID = a_iID;
	}

	public int getID() {
		return m_iID;
	}

	public void setStructure(Structure a_structure) {
		this.m_structure = a_structure;
	}

	public Structure getStructure() {
		return this.m_structure;
	}

	public void setCountScore(String a_strCountScore) {
		this.m_strCountScore = a_strCountScore;
	}

	public String getCountScore() {
		return m_strCountScore;
	}

	public void setIntensityScore(String a_strIntensityScore) {
		this.m_strIntensityScore = a_strIntensityScore;
	}

	public String getIntensityScore() {
		return m_strIntensityScore;
	}

	@Override
	public boolean equals(Object o) {
		if ( !( o instanceof AnnotatedStructure ) )
			return false;
		AnnotatedStructure t_o = (AnnotatedStructure)o;
		if ( !this.m_structure.equals(t_o.m_structure) )
			return false;
		if ( this.m_iID != t_o.m_iID )
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ID: "+this.m_iID+", "+this.m_structure.toString();
	}
}
