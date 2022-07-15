package org.grits.toolbox.tools.gsl.util.generator.structure;

import java.util.ArrayList;
import java.util.List;

import org.grits.toolbox.tools.gsl.structure.lipid.FattyAcid;
import org.grits.toolbox.tools.gsl.structure.lipid.ILipid;
import org.grits.toolbox.tools.gsl.structure.lipid.Lipid;

/**
 * Class for generator of fatty acids.
 * @author Masaaki Matsubara
 *
 */
public class FattyAcidsGenerator extends LipidsGeneratorAbstract {

	/**
	 * Gets FattyAcids.
	 * @return List of generated FattyAcids
	 */
	public List<FattyAcid> getFattyAcids() {
		// Cast LipidAbstract to FattyAcid
		List<FattyAcid> t_alFAs = new ArrayList<>();
		for ( ILipid t_oFA : this.getLipids() )
			t_alFAs.add( (FattyAcid)t_oFA );
		return t_alFAs;
	}

	@Override
	protected Lipid generateLipid(int a_nLength, int a_nOH, int a_nOAc, int a_nDouble) {
		return new FattyAcid( a_nLength, a_nOH, a_nOAc, a_nDouble );
	}

}
