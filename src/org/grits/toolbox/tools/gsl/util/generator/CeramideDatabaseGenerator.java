package org.grits.toolbox.tools.gsl.util.generator;

import org.grits.toolbox.tools.gsl.database.lipid.LipidClass;
import org.grits.toolbox.tools.gsl.database.lipid.LipidParameters;
import org.grits.toolbox.tools.gsl.structure.lipid.Ceramide;
import org.grits.toolbox.tools.gsl.structure.lipid.ILipid;
import org.grits.toolbox.tools.gsl.util.generator.structure.CeramidesGenerator;
import org.grits.toolbox.tools.gsl.util.generator.structure.LipidsGeneratorAbstract;
import org.grits.toolbox.tools.gsl.util.mass.CeramideChemicalComposition;
import org.grits.toolbox.tools.gsl.util.mass.ChemicalComposition;

/**
 * Class for generating ceramide database
 * @author Masaaki Matsubara
 * @see CeramideGenerator
 *
 */
public class CeramideDatabaseGenerator extends LipidDatabaseGeneratorAbstract {

	private LipidParametersIO m_paramsImporterSphingosine;
	private LipidParametersIO m_paramsImporterFattyAcid;

	public CeramideDatabaseGenerator() {
		super();
		this.setLipidClass(LipidClass.CERAMIDE);
		this.m_paramsImporterSphingosine = new LipidParametersIO();
		this.m_paramsImporterSphingosine.setLipidClass(LipidClass.SPHINGOSINE);
		this.m_paramsImporterFattyAcid = new LipidParametersIO();
		this.m_paramsImporterFattyAcid.setLipidClass(LipidClass.FATTY_ACID);
	}

	public boolean setCarbonLengthesForSphingosine(String a_strParams) {
		return this.m_paramsImporterSphingosine.setCarbonLengths(a_strParams);
	}

	public boolean setNumbersOfHydrxylGroupsForSphingosine(String a_strParams) {
		return this.m_paramsImporterSphingosine.setNumbersOfHydroxylGroups(a_strParams);
	}

	public boolean setNumbersOfOAcetylGroupsForSphingosine(String a_strParams) {
		return this.m_paramsImporterSphingosine.setNumbersOfOAcetylGroups(a_strParams);
	}

	public boolean setNumbersOfUnsaturationsForSphingosine(String a_strParams) {
		return this.m_paramsImporterSphingosine.setNumbersOfDoubleBonds(a_strParams);
	}

	public void allowOnlyEvenNumberedCarbonLengthOfSphingosine(boolean a_bOnlyEven) {
		this.m_paramsImporterSphingosine.allowOnlyEvenNumberedCarbonLength(a_bOnlyEven);
	}

	public boolean setCarbonLengthesForFattyAcid(String a_strParams) {
		return this.m_paramsImporterFattyAcid.setCarbonLengths(a_strParams);
	}

	public boolean setNumbersOfHydrxylGroupsForFattyAcid(String a_strParams) {
		return this.m_paramsImporterFattyAcid.setNumbersOfHydroxylGroups(a_strParams);
	}

	public boolean setNumbersOfOAcetylGroupsForFattyAcid(String a_strParams) {
		return this.m_paramsImporterFattyAcid.setNumbersOfOAcetylGroups(a_strParams);
	}

	public boolean setNumbersOfUnsaturationsForFattyAcid(String a_strParams) {
		return this.m_paramsImporterFattyAcid.setNumbersOfDoubleBonds(a_strParams);
	}

	public void allowOnlyEvenNumberedCarbonLengthOfFattyAcid(boolean a_bOnlyEven) {
		this.m_paramsImporterFattyAcid.allowOnlyEvenNumberedCarbonLength(a_bOnlyEven);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.tools.gsl.util.generator.LipidDatabaseGeneratorAbstract#generate()
	 */
	@Override
	public void generate() {
		// Set origins for generating lipids
		this.getLipidGenerationSettings().setSphingosine(
				this.getLipidParameters(this.m_paramsImporterSphingosine)
			);

		this.getLipidGenerationSettings().setFattyAcid(
				this.getLipidParameters(this.m_paramsImporterFattyAcid)
			);

		super.generate();
	}

	private LipidParameters getLipidParameters(LipidParametersIO a_lipParamImporter) {
		LipidParameters t_params = new LipidParameters();
		t_params.setCarbonLengths( a_lipParamImporter.printCarbonLengths() );
		t_params.setNumberOfHydroxylGroups( a_lipParamImporter.printNumbersOfHydroxylGroups() );
		t_params.setNumberOfOAcetylGroups( a_lipParamImporter.printNumbersOfOAcetylGroups() );
		t_params.setNumberOfDoubleBonds( a_lipParamImporter.printNumbersOfDoubleBonds() );
		t_params.setIsAllowedOnlyEvenNumbers( a_lipParamImporter.isOnlyEvenNumberForCarbonLength() );
		return t_params;
	}

	protected LipidsGeneratorAbstract getLipidsGenerator() {
		CeramidesGenerator t_genCers = new CeramidesGenerator();
		t_genCers.setParametersForSphingosine(this.m_paramsImporterSphingosine);
		t_genCers.setParametersForFattyAcid(this.m_paramsImporterFattyAcid);
		return t_genCers;
	}

	protected ChemicalComposition getChemicalComposition(ILipid a_ILip) {
		return new CeramideChemicalComposition((Ceramide)a_ILip);
	}

}
