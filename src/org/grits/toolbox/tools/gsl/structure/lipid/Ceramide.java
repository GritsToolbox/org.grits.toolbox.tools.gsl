package org.grits.toolbox.tools.gsl.structure.lipid;

import java.util.ArrayList;
import java.util.List;

/**
 * An object model for Ceramide structure implementing ILipid.
 * Sphingosine and FattyAcid are components of this structure.
 * @author Masaaki Matsubara
 *
 */
public class Ceramide implements ILipid {

	private Sphingosine m_oSphingosine;
	private FattyAcid m_oFattyAcid;

	public Ceramide( Sphingosine a_oSph, FattyAcid a_oFA ) {
		this.m_oSphingosine = a_oSph;
		this.m_oFattyAcid = a_oFA;
	}

	public Sphingosine getSphingosine() {
		return this.m_oSphingosine;
	}

	public FattyAcid getFattyAcid() {
		return this.m_oFattyAcid;
	}

	@Override
	public int getCarbonLength() {
		return this.m_oSphingosine.getCarbonLength()+this.m_oFattyAcid.getCarbonLength();
	}

	@Override
	public int getNumberOfHydroxylGroups() {
		return this.m_oSphingosine.getNumberOfHydroxylGroups()+this.m_oFattyAcid.getNumberOfHydroxylGroups();
	}

	@Override
	public int getNumberOfOAcetylGroups() {
		return this.m_oSphingosine.getNumberOfOAcetylGroups()+this.m_oFattyAcid.getNumberOfOAcetylGroups();
	}

	@Override
	public int getNumberOfDoubleBonds() {
		return this.m_oSphingosine.getNumberOfDoubleBonds()+this.m_oFattyAcid.getNumberOfDoubleBonds();
	}

	@Override
	public String getCoreName() {
		return this.m_oSphingosine.getCoreName()+"/"+this.m_oFattyAcid.getCoreName();
	}

	@Override
	public String getName() {
		return "Cer("+this.getCoreName()+")";
	}

	@Override
	public String getCompositionName() {
		String t_strName = this.getCarbonLength()+":"+this.getNumberOfDoubleBonds();
		if ( this.getNumberOfHydroxylGroups() > 0 ) {
			String t_strOH = (this.getNumberOfHydroxylGroups() == 1)? "h" :
							(this.getNumberOfHydroxylGroups() == 2)? "d" :
							(this.getNumberOfHydroxylGroups() == 3)? "t" :
							(this.getNumberOfHydroxylGroups() == 4)? "tet" :
							"h"+this.getNumberOfHydroxylGroups()+"-";
			t_strName = t_strOH+t_strName;
		}
		return "Cer("+t_strName+")";
	}

	@Override
	public boolean hasSubstructure() {
		return true;
	}

	@Override
	public List<String> getSubstructureNames() {
		List<String> t_listSubstNames = new ArrayList<>();
		t_listSubstNames.add(this.m_oSphingosine.getName());
		t_listSubstNames.add(this.m_oFattyAcid.getName());
		return t_listSubstNames;
	}
}
