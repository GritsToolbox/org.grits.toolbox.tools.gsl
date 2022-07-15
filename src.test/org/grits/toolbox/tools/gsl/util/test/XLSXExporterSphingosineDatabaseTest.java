package org.grits.toolbox.tools.gsl.util.test;

import java.io.IOException;

import org.grits.toolbox.tools.gsl.util.generator.SphingosineDatabaseGenerator;
import org.grits.toolbox.tools.gsl.util.io.lipid.LipidDatabaseExporterXLSX;

public class XLSXExporterSphingosineDatabaseTest {

	public static void main(String[] args) {
		String t_strOutput = "./resource.test/Sph.xlsx";

		// Set parameters
		SphingosineDatabaseGenerator t_genLDB = new SphingosineDatabaseGenerator();
		t_genLDB.setLipidDatabaseName("Possible sphingosine database");
		t_genLDB.setVersion("1.0");
		t_genLDB.setCreatorName("Masaaki Matsubara");
		t_genLDB.setCreatorInstitution("Complex Carbohydrate Research Center");
		t_genLDB.setDescription("This database is generated for a test.\nAll possible sphingosine patterns are included.");

		t_genLDB.setCarbonLengths("14-30");
		t_genLDB.allowOnlyEvenNumberedCarbonLength(true);
		t_genLDB.setNumbersOfHydroxylGroups("1-4");
		t_genLDB.setNumbersOfDoubleBonds("0-3");

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
