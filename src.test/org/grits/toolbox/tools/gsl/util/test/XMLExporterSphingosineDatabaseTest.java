package org.grits.toolbox.tools.gsl.util.test;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.grits.toolbox.tools.gsl.database.lipid.LipidDatabase;
import org.grits.toolbox.tools.gsl.util.generator.SphingosineDatabaseGenerator;

public class XMLExporterSphingosineDatabaseTest {

	public static void main(String[] args) {
		// Set parameters
		SphingosineDatabaseGenerator t_genLDB = new SphingosineDatabaseGenerator();
		t_genLDB.setLipidDatabaseName("Possible sphingosine database");
		t_genLDB.setVersion("1.0");
		t_genLDB.setCreatorName("Masaaki Matsubara");
		t_genLDB.setCreatorInstitution("Complex Carbohydrate Research Center");
		t_genLDB.setDescription("This database is generated for a test.\nAll possible sphingosine patterns are included.");

		t_genLDB.setGeneratedBy(XMLExporterSphingosineDatabaseTest.class.getName());

		t_genLDB.setCarbonLengths("16-30");
		t_genLDB.allowOnlyEvenNumberedCarbonLength(true);
		t_genLDB.setNumbersOfHydroxylGroups("1-4");
		t_genLDB.setNumbersOfDoubleBonds("0-3");

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
			m.marshal(t_ld, new File("./resource.test/Sph.xml"));
			
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

}
