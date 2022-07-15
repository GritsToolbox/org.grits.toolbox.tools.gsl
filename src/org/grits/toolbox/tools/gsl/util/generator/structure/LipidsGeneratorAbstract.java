package org.grits.toolbox.tools.gsl.util.generator.structure;

import java.util.ArrayList;
import java.util.List;

import org.grits.toolbox.tools.gsl.structure.lipid.ILipid;
import org.grits.toolbox.tools.gsl.structure.lipid.Lipid;
import org.grits.toolbox.tools.gsl.util.generator.LipidParametersIO;

/**
 * Abstract class for generator of lipids.
 * generateLipid() must be implemented to return appropriate Lipid object for subclass.
 * @author Masaaki Matsubara
 *
 */
public abstract class LipidsGeneratorAbstract {

	private LipidParametersIO m_parameters;
	private List<ILipid> m_listGeneratedLipids;

	public LipidsGeneratorAbstract() {
		this.m_parameters = new LipidParametersIO();
		this.m_listGeneratedLipids = new ArrayList<>();
	}

	public void setParameters(LipidParametersIO a_params) {
		this.m_parameters = a_params;
	}

	public LipidParametersIO getParameters() {
		return this.m_parameters;
	}

	protected void addLipid(ILipid a_ILip) {
		this.m_listGeneratedLipids.add(a_ILip);
	}

	public List<ILipid> getLipids() {
		return this.m_listGeneratedLipids;
	}

	/**
	 * Generate list of possible Lipids based on specified parameters
	 */
	public void generate() {
		// Fill 0 to possible parameters if empty
		this.m_parameters.fill();

		// Loop for number of carbon
		for ( int t_nCarbons : this.m_parameters.getCarbonLengthes() ) {
			// Ignore if flag of even number for carbon length is true and the carbon length is odd number
			if ( this.m_parameters.isOnlyEvenNumberForCarbonLength() && t_nCarbons % 2 == 1 )
				continue;
			// Loop for hydroxyl group
			for ( int t_nOH : this.m_parameters.getNumbersOfHydroxylGroups() ) {
				// Loop for O-acetyl group
				for ( int t_nOAc : this.m_parameters.getNumbersOfOAcetylGroups() ) {
					// Loop for unsaturated
					for ( int t_nDB : this.m_parameters.getNumbersOfDoubleBonds() ) {
						Lipid t_oLipid = this.generateLipid( t_nCarbons, t_nOH, t_nOAc, t_nDB );
						this.addLipid(t_oLipid);
					}
				}
			}
		}
	}

	/**
	 * Get settings for lipid generations.
	 * @return String of settings for lipid generations
	 */
	public String printParameters() {
		return this.m_parameters.printParameters();
	}

	/**
	 * Generate a Lipid object having given parameters.
	 * @param a_nC number of carbons
	 * @param a_nOH number of hydroxyl groups
	 * @param a_nOAc number of O-acetyl groups
	 * @param a_nDB number of double bonds
	 * @return new Lipid object
	 */
	protected abstract Lipid generateLipid(int a_nC, int a_nOH, int a_nOAc, int a_nDB);
}
