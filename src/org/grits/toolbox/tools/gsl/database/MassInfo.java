package org.grits.toolbox.tools.gsl.database;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class for mass information of a lipid.
 * Currently, this is not used.
 * @author Masaaki Matsubara
 *
 */
@XmlRootElement(name = "mass")
public class MassInfo {

	private IonizationType m_enumIon;
	private boolean m_bIsPermethylated;
	private String m_strComposition;
	private String m_strValue;

	public MassInfo() {
		this.m_enumIon = IonizationType.NOION;
		this.m_bIsPermethylated = false;
		this.m_strComposition = "";
		this.m_strValue = "0.0";
	}

	@XmlAttribute
	public IonizationType getIonizationType() {
		return m_enumIon;
	}

	@XmlAttribute
	public boolean getIsPermethylated() {
		return m_bIsPermethylated;
	}

	@XmlAttribute
	public String getComposition() {
		return m_strComposition;
	}

	@XmlElement(name = "monoisotopic")
	public String getValue() {
		return m_strValue;
	}

	public void setIonizationType(IonizationType m_enumIon) {
		this.m_enumIon = m_enumIon;
	}

	public void setIsPermethylated(boolean m_bIsPermethylated) {
		this.m_bIsPermethylated = m_bIsPermethylated;
	}

	public void setComposition(String m_strComposition) {
		this.m_strComposition = m_strComposition;
	}

	public void setValue(String m_strValue) {
		this.m_strValue = m_strValue;
	}
}
