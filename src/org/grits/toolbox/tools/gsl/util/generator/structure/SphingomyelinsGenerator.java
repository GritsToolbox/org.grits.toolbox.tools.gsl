package org.grits.toolbox.tools.gsl.util.generator.structure;

import org.grits.toolbox.tools.gsl.structure.lipid.FattyAcid;
import org.grits.toolbox.tools.gsl.structure.lipid.Sphingomyelin;
import org.grits.toolbox.tools.gsl.structure.lipid.Sphingosine;

/**
 * Class for generating list of possible sphingomyelins.
 * Extends CeramideGenerator.
 * @author Masaaki Matsubara
 * @see Sphingomyelin
 *
 */
public class SphingomyelinsGenerator extends CeramidesGenerator {

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.tools.gsl.util.generator.structure.CeramidesGenerator#getNewLipid(org.grits.toolbox.tools.gsl.structure.lipid.Sphingosine, org.grits.toolbox.tools.gsl.structure.lipid.FattyAcid)
	 */
	@Override
	protected Sphingomyelin getNewLipid(Sphingosine a_oSph, FattyAcid a_oFA) {
		return new Sphingomyelin( a_oSph, a_oFA );
	}
}
