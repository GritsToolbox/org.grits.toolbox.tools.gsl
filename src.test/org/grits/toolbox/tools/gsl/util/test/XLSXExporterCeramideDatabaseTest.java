package org.grits.toolbox.tools.gsl.util.test;

import java.io.IOException;

import org.grits.toolbox.tools.gsl.util.generator.CeramideDatabaseGenerator;
import org.grits.toolbox.tools.gsl.util.io.lipid.LipidDatabaseExporterXLSX;

public class XLSXExporterCeramideDatabaseTest {

	public static void main(String[] args) {
		String t_strOutput = TestResourcePath.RESOURCE_DIR+"Cer.xlsx";

		// Set parameters
		CeramideDatabaseGenerator t_genLDB = new CeramideDatabaseGenerator();
		t_genLDB.setLipidDatabaseName("Possible ceramide database");
		t_genLDB.setVersion("1.0");
		t_genLDB.setCreatorName("Masaaki Matsubara");
		t_genLDB.setCreatorInstitution("Complex Carbohydrate Research Center");
		t_genLDB.setDescription("This database is generated for a test.\nAll possible ceramide patterns are included.");

		t_genLDB.setCarbonLengthesForSphingosine("18");
		t_genLDB.allowOnlyEvenNumberedCarbonLengthOfSphingosine(false);
		t_genLDB.setNumbersOfHydrxylGroupsForSphingosine("2-3");
		t_genLDB.setNumbersOfUnsaturationsForSphingosine("0-1");

		t_genLDB.setCarbonLengthesForFattyAcid("16-26");
		t_genLDB.allowOnlyEvenNumberedCarbonLengthOfFattyAcid(false);
		t_genLDB.setNumbersOfHydrxylGroupsForFattyAcid("0-1");
		t_genLDB.setNumbersOfUnsaturationsForFattyAcid("0-1");

		// Generate lipid database
		t_genLDB.generate();

		// Export .xlsx file
		try {
			LipidDatabaseExporterXLSX t_oLDB2XSLX = new LipidDatabaseExporterXLSX(t_genLDB.getLipidDatabase(), t_strOutput);
			t_oLDB2XSLX.createBook();
			t_oLDB2XSLX.write();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
