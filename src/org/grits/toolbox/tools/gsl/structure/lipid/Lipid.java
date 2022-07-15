package org.grits.toolbox.tools.gsl.structure.lipid;

import java.util.List;

/**
 * An object model for generic lipid structure implementing ILipid.
 * @author Masaaki Matsubara
 *
 */
public class Lipid implements ILipid {
	private int m_nCarboxyl;
	private int m_nAmine;
	private int m_nCarbamoyl;
	private int m_nCarbonNumber;
	private int m_nHydroxylGroups;
	private int m_nOAcetylated;
	private int m_nDoubleBonds;

	/**
	 * Constructor
	 * @param a_nCOOH number of carboxyl groups
	 * @param a_nNH2 number of amine groups
	 * @param a_nCONH2 number of carbamoyl groups
	 * @param a_nLength carbon chain length
	 * @param a_nOH number of hydroxyl groups without OH on carboxyl group
	 * @param a_nOAc number of O-acetyle groups
	 * @param a_nDouble number of double bonds
	 */
	public Lipid( int a_nCOOH, int a_nNH2, int a_nCONH2, int a_nLength, int a_nOH, int a_nOAc, int a_nDouble ) {
		this.m_nCarboxyl = a_nCOOH;
		this.m_nAmine = a_nNH2;
		this.m_nCarbamoyl = a_nCONH2;
		this.m_nCarbonNumber = a_nLength;
		this.m_nHydroxylGroups = a_nOH;
		this.m_nOAcetylated = a_nOAc;
		this.m_nDoubleBonds = a_nDouble;
	}

	public int getNumberOfCarboxyl() {
		return this.m_nCarboxyl;
	}

	public int getNumberOfAmine() {
		return this.m_nAmine;
	}

	public int getNumberOfCarbamoyl() {
		return this.m_nCarbamoyl;
	}

	@Override
	public int getCarbonLength() {
		return this.m_nCarbonNumber;
	}

	@Override
	public int getNumberOfHydroxylGroups() {
		return this.m_nHydroxylGroups;
	}

	@Override
	public int getNumberOfOAcetylGroups() {
		return this.m_nOAcetylated;
	}

	@Override
	public int getNumberOfDoubleBonds() {
		return this.m_nDoubleBonds;
	}

	@Override
	public String getCoreName() {
		String t_strName = this.m_nCarbonNumber+":"+this.m_nDoubleBonds;
		if ( this.m_nHydroxylGroups > 0 ) {
			String t_strOH = (this.m_nHydroxylGroups == 1)? "h" :
							(this.m_nHydroxylGroups == 2)? "d" :
							(this.m_nHydroxylGroups == 3)? "t" :
							(this.m_nHydroxylGroups == 4)? "tet" :
							"h"+this.m_nHydroxylGroups;
			t_strName = t_strOH+t_strName;
		}
		return t_strName;
	}

	@Override
	public String getName() {
		String t_strName = "Fr("+this.getCoreName()+")";
		if ( this.m_nAmine > 0 ) {
			t_strName += "(NH2)";
			if ( this.m_nAmine > 1 )
				t_strName += this.m_nAmine;
		}
		if ( this.m_nCarboxyl > 0 ) {
			t_strName += "(COOH)";
			if ( this.m_nCarboxyl > 1 )
				t_strName += this.m_nCarboxyl;
		}
		if ( this.m_nCarbamoyl > 0 ) {
			t_strName += "(CONH2)";
			if ( this.m_nCarbamoyl > 1 )
				t_strName += this.m_nCarbamoyl;
		}
		return t_strName;
	}

	@Override
	public String getCompositionName() {
		// Same as name
		return this.getName();
	}

	@Override
	public boolean hasSubstructure() {
		return false;
	}

	@Override
	public List<String> getSubstructureNames() {
		return null;
	}
}
