package org.grits.toolbox.tools.gsl.util.generator;

import org.grits.toolbox.tools.gsl.database.lipid.LipidClass;
import org.grits.toolbox.tools.gsl.database.lipid.LipidParameters;
import org.grits.toolbox.tools.gsl.structure.lipid.ILipid;
import org.grits.toolbox.tools.gsl.structure.lipid.Lipid;
import org.grits.toolbox.tools.gsl.util.generator.structure.LipidsGeneratorAbstract;
import org.grits.toolbox.tools.gsl.util.generator.structure.SphingosinesGenerator;
import org.grits.toolbox.tools.gsl.util.mass.ChemicalComposition;
import org.grits.toolbox.tools.gsl.util.mass.LipidChemicalComposition;

/**
 * Class for generating sphingosine database.
 * @author Masaaki Matsubara
 *
 */
public class SphingosineDatabaseGenerator extends LipidDatabaseGeneratorAbstract {

	private LipidParametersIO m_paramsImporter;

	public SphingosineDatabaseGenerator() {
		super();
		this.setLipidClass(LipidClass.SPHINGOSINE);
		this.m_paramsImporter = new LipidParametersIO();
		this.m_paramsImporter.setLipidClass(LipidClass.SPHINGOSINE);
	}

	/**
	 * Sets carbon lengths.
	 * @param a_strParams String of numbers and/or ranges separated by commas
	 * @return pass/fail
	 * @see LipidParametersIO#setCarbonLengths(String)
	 */
	public boolean setCarbonLengths(String a_strParams) {
		return this.m_paramsImporter.setCarbonLengths(a_strParams);
	}

	/**
	 * Sets number of hydroxyl groups.
	 * @param a_strParams String of numbers and/or ranges separated by commas
	 * @return pass/fail
	 * @see LipidParametersIO#setNumbersOfHydroxylGroups(String)
	 */
	public boolean setNumbersOfHydroxylGroups(String a_strParams) {
		return this.m_paramsImporter.setNumbersOfHydroxylGroups(a_strParams);
	}

	/**
	 * Sets number of O-acetyl groups.
	 * @param a_strParams String of numbers and/or ranges separated by commas
	 * @return pass/fail
	 * @see LipidParametersIO#setNumbersOfOAcetylGroups(String)
	 */
	public boolean setNumbersOfOAcetylGroups(String a_strParams) {
		return this.m_paramsImporter.setNumbersOfOAcetylGroups(a_strParams);
	}

	/**
	 * Sets number of double bonds.
	 * @param a_strParams String of numbers and/or ranges separated by commas
	 * @return pass/fail
	 * @see LipidParametersIO#setNumbersOfDoubleBonds(String)
	 */
	public boolean setNumbersOfDoubleBonds(String a_strParams) {
		return this.m_paramsImporter.setNumbersOfDoubleBonds(a_strParams);
	}

	/**
	 * Sets flag for allowing only even numbered carbon length.
	 * @param a_bOnlyEven boolean indicating only even numbered carbon length is allowed
	 * @see LipidParametersIO#setOnlyEvenNumberForCarbonLength(boolean)
	 */
	public void allowOnlyEvenNumberedCarbonLength(boolean a_bOnlyEven) {
		this.m_paramsImporter.allowOnlyEvenNumberedCarbonLength(a_bOnlyEven);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.tools.gsl.util.generator.LipidDatabaseGeneratorAbstract#generate()
	 */
	@Override
	public void generate() {
		// Set origins for generating lipids
		LipidParameters t_params = new LipidParameters();
		t_params.setCarbonLengths( this.m_paramsImporter.printCarbonLengths() );
		t_params.setNumberOfHydroxylGroups( this.m_paramsImporter.printNumbersOfHydroxylGroups() );
		t_params.setNumberOfOAcetylGroups( this.m_paramsImporter.printNumbersOfOAcetylGroups() );
		t_params.setNumberOfDoubleBonds( this.m_paramsImporter.printNumbersOfDoubleBonds() );
		t_params.setIsAllowedOnlyEvenNumbers( this.m_paramsImporter.isOnlyEvenNumberForCarbonLength() );
		this.getLipidGenerationSettings().setSphingosine(t_params);

		super.generate();
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.tools.gsl.util.generator.LipidDatabaseGeneratorAbstract#getLipidsGenerator()
	 */
	@Override
	protected LipidsGeneratorAbstract getLipidsGenerator() {
		SphingosinesGenerator t_genSph = new SphingosinesGenerator();
		t_genSph.setParameters(this.m_paramsImporter);
		return t_genSph;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.tools.gsl.util.generator.LipidDatabaseGeneratorAbstract#getChemicalComposition(org.grits.toolbox.tools.gsl.structure.lipid.ILipid)
	 */
	@Override
	protected ChemicalComposition getChemicalComposition(ILipid a_ILip) {
		return new LipidChemicalComposition((Lipid)a_ILip);
	}

}
