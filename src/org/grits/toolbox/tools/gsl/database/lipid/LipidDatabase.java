package org.grits.toolbox.tools.gsl.database.lipid;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class for storing a lipid database. This have LipidGenerationSettings and list of LipidData.
 * @author Masaaki Matsubara
 * @see LipidGenerationSettings
 * @see LipidData
 *
 */
@XmlRootElement(name = "lipid_database")
//@XmlType(propOrder = {"name", "version", "description", "creator", "institution", })
public class LipidDatabase {
	private String m_strName;
	private String m_strVersion;
	private String m_strDescription;
	private String m_strCreatorName;
	private String m_strCreatorInstitution;
	private LipidGenerationSettings m_stgOrigin;
	private List<LipidData> m_listLipidData;

	public LipidDatabase() {
		this.m_strName = "";
		this.m_strVersion = "1.0";
		this.m_strDescription = "";
		this.m_strCreatorName = "User name";
		this.m_strCreatorInstitution = "User institution";

		this.m_stgOrigin = new LipidGenerationSettings();
		this.m_listLipidData = new ArrayList<>();
	}

	@XmlAttribute
	public void setName(String a_strName) {
		this.m_strName = a_strName;
	}

	@XmlAttribute
	public void setVersion(String a_strVersion) {
		this.m_strVersion = a_strVersion;
	}

	public void setDescription(String a_strDescription) {
		this.m_strDescription = a_strDescription;
	}

	@XmlAttribute(name = "creator")
	public void setCreatorName(String a_strUserName) {
		this.m_strCreatorName = a_strUserName;
	}

	@XmlAttribute(name = "institution")
	public void setCreatorInstitution(String a_strUserInstitution) {
		this.m_strCreatorInstitution = a_strUserInstitution;
	}

	@XmlElement(name = "origin")
	public void setOrigin(LipidGenerationSettings a_stgOrigin) {
		this.m_stgOrigin = a_stgOrigin;
	}

	@XmlElement(name = "lipid")
	public void setLipidData(List<LipidData> a_listLipidData) {
		this.m_listLipidData = a_listLipidData;
	}

	public boolean addLipidData(LipidData a_lipidData) {
		return this.m_listLipidData.add(a_lipidData);
	}

	public String getName() {
		return this.m_strName;
	}

	public String getVersion() {
		return this.m_strVersion;
	}

	public String getDescription() {
		return this.m_strDescription;
	}

	public String getCreatorName() {
		return this.m_strCreatorName;
	}

	public String getCreatorInstitution() {
		return this.m_strCreatorInstitution;
	}

	public LipidGenerationSettings getOrigin() {
		return this.m_stgOrigin;
	}

	public List<LipidData> getLipidData() {
		return this.m_listLipidData;
	}
}
