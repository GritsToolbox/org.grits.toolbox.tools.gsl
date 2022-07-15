package org.grits.toolbox.tools.gsl.util.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.grits.toolbox.ms.om.data.AnalyteSettings;
import org.grits.toolbox.ms.om.data.LipidDatabase;
import org.grits.toolbox.ms.om.data.LipidFragment;
import org.grits.toolbox.ms.om.data.LipidFragmentPerActivationMethod;
import org.grits.toolbox.ms.om.data.LipidFragmentPerMSLevel;
import org.grits.toolbox.ms.om.data.LipidSettings;
import org.grits.toolbox.ms.om.data.Method;
import org.grits.toolbox.util.structure.glycan.util.FilterUtils;

public class TestSerializeLipidSettings {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		LipidDatabase t_lipDB = new LipidDatabase();
		t_lipDB.setDatabase("lipid databse name");
		t_lipDB.setURI("lipid database uri");

		LipidSettings t_lipSett = new LipidSettings();
		t_lipSett.setDatabase(t_lipDB);
		t_lipSett.setLipidFragments(new ArrayList<LipidFragment>());
		t_lipSett.setLipidFragmentsPerActivationMethod(new ArrayList<LipidFragmentPerActivationMethod>());
		t_lipSett.setLipidFragmentsPerMSLevel(new ArrayList<LipidFragmentPerMSLevel>());
		t_lipSett.setMaxNumOfCleavages(2);
		t_lipSett.setPerDerivatisationType("perMe");

		AnalyteSettings t_analSett = new AnalyteSettings();
		t_analSett.setLipidSettings(t_lipSett);

		Method t_method = new Method();
		t_method.getAnalyteSettings().add(t_analSett);

		serialize(t_method);
	}

	private static void serialize(Method method) {
		// TODO: Serialize the Method to XML
		try {
			// Add context list for Filter classes
			List<Class> contextList = new ArrayList<>(Arrays.asList(FilterUtils.filterClassContext));
			contextList.add(Method.class);
			// Create JAXB context and instantiate marshallar
			JAXBContext context = JAXBContext.newInstance( contextList.toArray(new Class[contextList.size()]));
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			// Write to File
			m.marshal(method, new File("E:\\method.xml"));
			
		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}

}
