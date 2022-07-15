package org.grits.toolbox.tools.gsl.structure.lipid;

/**
 * An object model for sphingomyelin structure as subclass of Ceramide.
 * @author Masaaki Matsubara
 *
 */
public class Sphingomyelin extends Ceramide {

	public Sphingomyelin(Sphingosine a_oSph, FattyAcid a_oFA) {
		super(a_oSph, a_oFA);
	}

	@Override
	public String getName() {
		return super.getName().replace("Cer", "SM");
	}

	@Override
	public String getCompositionName() {
		return super.getCompositionName().replace("Cer", "SM");
	}

}
