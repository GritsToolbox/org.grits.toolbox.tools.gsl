package org.grits.toolbox.tools.gsl.util.io.glycolipid.data;

public class GlycolipidStructure extends Structure {

	private String m_strGlycanSequence;
	private String m_strLipidName;

	public void setGlycanSequence(String a_strGlycanSequence) {
		this.m_strGlycanSequence = a_strGlycanSequence;
	}

	public String getGlycanSequence() {
		return m_strGlycanSequence;
	}

	public void setLipidName(String a_strLipidName) {
		this.m_strLipidName = a_strLipidName;
	}

	public String getLipidName() {
		return m_strLipidName;
	}

	@Override
	public boolean equals(Object o) {
		if ( !( o instanceof GlycolipidStructure ) )
			return false;
		GlycolipidStructure t_as = (GlycolipidStructure)o;
		if ( this.m_strGlycanSequence == null && t_as.m_strGlycanSequence != null )
			return false;
		if ( this.m_strGlycanSequence != null && !this.m_strGlycanSequence.equals(t_as.m_strGlycanSequence) )
			return false;
		if ( this.m_strLipidName == null && t_as.m_strLipidName != null )
			return false;
		if ( this.m_strLipidName != null && !this.m_strLipidName.equals(t_as.m_strLipidName) )
			return false;
		return true;
	}

	@Override
	public String getSequence() {
		return "Glycan: "+this.m_strGlycanSequence+", Lipid: "+this.m_strLipidName;
	}

	@Override
	public String toString() {
		return "Glycan: "+this.m_strGlycanSequence+", Lipid: "+this.m_strLipidName;
	}
}
