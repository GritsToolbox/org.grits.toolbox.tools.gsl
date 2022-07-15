package org.grits.toolbox.tools.gsl.util.generator.structure;

import java.util.ArrayList;
import java.util.List;

import org.grits.toolbox.tools.gsl.structure.lipid.ILipid;
import org.grits.toolbox.tools.gsl.structure.lipid.Lipid;
import org.grits.toolbox.tools.gsl.structure.lipid.Sphingosine;

/**
 * Class for generating list of sphingosines.
 * @author Masaaki Matsubara
 * @see Sphingosine
 *
 */
public class SphingosinesGenerator extends LipidsGeneratorAbstract {

	public SphingosinesGenerator() {
		super();
	}

	/**
	 * Get list of generated Sphingosines.
	 * @return List of generated Sphingosines
	 */
	public List<Sphingosine> getSphingosines() {
		// Cast ILipid to Sphingosine
		List<Sphingosine> t_alSphs = new ArrayList<>();
		for ( ILipid t_oSph : this.getLipids() )
			t_alSphs.add( (Sphingosine)t_oSph );
		return t_alSphs;
	}

	@Override
	protected Lipid generateLipid(int a_nLength, int a_nOH, int a_nOAc, int a_nDouble) {
		return new Sphingosine(a_nLength, a_nOH, a_nOAc, a_nDouble);
	}

}
