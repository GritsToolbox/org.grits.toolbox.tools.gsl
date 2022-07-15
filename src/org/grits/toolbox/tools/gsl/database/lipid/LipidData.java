package org.grits.toolbox.tools.gsl.database.lipid;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class for storing data of a lipid.
 * An element of LipidDatabase.
 * @author Masaaki Matsubara
 * @see LipidDatabase
 *
 */
@XmlRootElement(name = "lipid")
public class LipidData {
	private LipidClass m_class;
	private String m_strCommonName;
	private List<String> m_listSubstructures;
//	private List<MassInfo> m_listMasses;
	private List<String> m_listFragments;

	public LipidData() {
		this.m_class = LipidClass.FRAGMENT;
		this.m_strCommonName = "";
		this.m_listSubstructures = new ArrayList<>();
//		this.m_listMasses = new ArrayList<>();
		this.m_listFragments = new ArrayList<>();
	}

	@XmlAttribute(name = "class")
	public void setLipidClass(LipidClass a_type) {
		this.m_class = a_type;
	}

	@XmlAttribute(name = "name")
	public void setCommonName(String a_strLipidName) {
		this.m_strCommonName = a_strLipidName;
	}

	public void addSubstructures(String a_strSubstructure) {
		this.m_listSubstructures.add(a_strSubstructure);
	}

//	@XmlElement(name = "mass")
//	public void setMasses(List<MassInfo> a_listMasses) {
//		this.m_listMasses = a_listMasses;
//	}

//	public boolean addMass(MassInfo a_mass) {
//		return this.m_listMasses.add(a_mass);
//	}

	public void addFragments(String a_strFragment) {
		this.m_listFragments.add(a_strFragment);
	}

	public LipidClass getLipidClass() {
		return this.m_class;
	}

	public String getCommonName() {
		return m_strCommonName;
	}

	public List<String> getSubstructures() {
		return this.m_listSubstructures;
	}

//	public List<MassInfo> getMasses() {
//		return this.m_listMasses;
//	}

	public List<String> getFragments() {
		return this.m_listFragments;
	}
}
