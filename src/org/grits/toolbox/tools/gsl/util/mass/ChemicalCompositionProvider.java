package org.grits.toolbox.tools.gsl.util.mass;

import org.grits.toolbox.tools.gsl.structure.lipid.Ceramide;
import org.grits.toolbox.tools.gsl.structure.lipid.ILipid;
import org.grits.toolbox.tools.gsl.structure.lipid.Lipid;
import org.grits.toolbox.tools.gsl.structure.lipid.Sphingomyelin;

/**
 * Class for providing lipid-specific ChemicalComposition.
 * @author Masaaki Matsubara
 *
 */
public class ChemicalCompositionProvider {

	/**
	 * Get lipid-specific ChemicalComposition.
	 * @param a_iLipid an instance of ILipid
	 * @return Subclass of ChemicalComposition specific to the given lipid (null if unsupported lipid is given)
	 */
	public static ChemicalComposition getChemicalComposition(ILipid a_iLipid) {
		if ( a_iLipid instanceof Sphingomyelin )
			return new SphingomyelinChemicalComposition( (Sphingomyelin)a_iLipid );
		if ( a_iLipid instanceof Ceramide )
			return new CeramideChemicalComposition( (Ceramide)a_iLipid );
		if ( a_iLipid instanceof Lipid )
			return new LipidChemicalComposition( (Lipid)a_iLipid );
		return null;
	}

}
