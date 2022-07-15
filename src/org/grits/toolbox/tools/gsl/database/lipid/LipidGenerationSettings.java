package org.grits.toolbox.tools.gsl.database.lipid;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class for storing lipid generation settings containing origin of the lipid, its class and generation parameters.
 * There are two parameters for Sphingosine and Fatty Acid.
 * @author Masaaki Matsubara
 * @see LipidClass
 * @see LipidParameters
 *
 */
@XmlRootElement(name = "generation_settings")
public class LipidGenerationSettings {

	private String m_strOrigin;
	private LipidClass m_enumLipidClass;
	private LipidParameters m_paramsGenSphingosine;
	private LipidParameters m_paramsGenFattyAcid;

	@XmlAttribute(name = "generated_from")
	public void setOrigin(String a_strOrigin) {
		this.m_strOrigin = a_strOrigin;
	}

	@XmlAttribute(name = "lipid_class")
	public void setLipidClass(LipidClass a_enumLipidClass) {
		this.m_enumLipidClass = a_enumLipidClass;
	}

	@XmlElement(name = "sphingosine")
	public void setSphingosine(LipidParameters a_paramsSp) {
		this.m_paramsGenSphingosine = a_paramsSp;
	}

	@XmlElement(name = "fatty_acid")
	public void setFattyAcid(LipidParameters a_paramsFA) {
		this.m_paramsGenFattyAcid = a_paramsFA;
	}

	public String getOrigin() {
		return this.m_strOrigin;
	}

	public LipidClass getLipidClass() {
		return this.m_enumLipidClass;
	}

	public LipidParameters getSphingosine() {
		return this.m_paramsGenSphingosine;
	}

	public LipidParameters getFattyAcid() {
		return this.m_paramsGenFattyAcid;
	}


	
}
