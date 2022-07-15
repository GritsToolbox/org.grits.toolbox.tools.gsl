package org.grits.toolbox.tools.gsl.structure.lipid;

/**
 * An object model for sphingosine structure as subclass of Lipid.
 * @author MasaakiMatsubara
 *
 */
public class Sphingosine extends Lipid {

	/**
	 * Constructor of Sphingosine
	 * @param a_nCarbon number of carbons
	 * @param a_nOH number of hydoroxyl group
	 * @param a_nOAc number of O-acetyl group
	 * @param a_nDouble number of unsaturated
	 */
	public Sphingosine(int a_nCarbon, int a_nOH, int a_nOAc, int a_nDouble) {
		super(0, 1, 0, a_nCarbon, a_nOH, a_nOAc, a_nDouble);
	}

	@Override
	public String getName() {
		return "Sp("+this.getCoreName()+")";
	}
}
