package org.grits.toolbox.tools.gsl.util.test;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.grits.toolbox.tools.gsl.database.lipid.LipidDatabase;
import org.grits.toolbox.tools.gsl.util.generator.CeramideDatabaseGenerator;

public class XMLExporterCeramideDatabaseTest {

	public static void main(String[] args) {
		// Set parameters
		CeramideDatabaseGenerator t_genLDB = new CeramideDatabaseGenerator();
		t_genLDB.setLipidDatabaseName("Possible ceramide database");
		t_genLDB.setVersion("1.0");
		t_genLDB.setCreatorName("Masaaki Matsubara");
		t_genLDB.setCreatorInstitution("Complex Carbohydrate Research Center");
		t_genLDB.setDescription("Possible ceramide database suggested by Kazuhiro Aoki");

		t_genLDB.setGeneratedBy(XMLExporterCeramideDatabaseTest.class.getName());

		t_genLDB.setCarbonLengthesForSphingosine("14-30");
		t_genLDB.allowOnlyEvenNumberedCarbonLengthOfSphingosine(false);
		t_genLDB.setNumbersOfHydrxylGroupsForSphingosine("1-4");
		t_genLDB.setNumbersOfUnsaturationsForSphingosine("0-3");

		t_genLDB.setCarbonLengthesForFattyAcid("14-26");
		t_genLDB.allowOnlyEvenNumberedCarbonLengthOfFattyAcid(false);
		t_genLDB.setNumbersOfHydrxylGroupsForFattyAcid("0-2");
		t_genLDB.setNumbersOfUnsaturationsForFattyAcid("0-3");

		// Generate lipid database
		t_genLDB.generate();
		LipidDatabase t_ld = t_genLDB.getLipidDatabase();

		try {
			// Create JAXB context and instantiate marshallar
			JAXBContext context = JAXBContext.newInstance(LipidDatabase.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			// Write to System.out
//			m.marshal(t_ld, System.out);

			// Write to File
			m.marshal(t_ld, new File("./resource.test/Ceramide_all.xml"));
			
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

}
