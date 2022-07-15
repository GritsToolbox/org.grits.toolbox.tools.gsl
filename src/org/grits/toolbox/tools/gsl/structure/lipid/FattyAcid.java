package org.grits.toolbox.tools.gsl.structure.lipid;

/**
 * An object model for a fatty acid structure as subclass of Lipid.
 * @author Masaaki Matsubara
 *
 */
public class FattyAcid extends Lipid {

	/**
	 * Constructor of fatty acid
	 * @param a_nCarbons number of carbons
	 * @param a_nOH number of hydoroxyl group
	 * @param a_nOAc number of O-acetyl group
	 * @param a_nDouble number of unsaturated
	 */
	public FattyAcid(int a_nCarbons, int a_nOH, int a_nOAc, int a_nDouble) {
		super(1, 0, 0, a_nCarbons, a_nOH, a_nOAc, a_nDouble);
	}

	@Override
	public String getName() {
		return "FA("+this.getCoreName()+")";
	}
}
