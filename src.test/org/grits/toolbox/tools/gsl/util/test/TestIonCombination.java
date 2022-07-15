package org.grits.toolbox.tools.gsl.util.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.grits.toolbox.ms.om.data.Method;
import org.grits.toolbox.tools.gsl.dango.IonCombinationGenerator;
import org.grits.toolbox.tools.gsl.structure.IonCombination;
import org.grits.toolbox.tools.gsl.util.io.om.MSDataModelFileHandlar;

public class TestIonCombination {

	public static void main(String[] args) {
		String t_strMethodFilename = TestResourcePath.METHOD_PATH;

		Method t_method = MSDataModelFileHandlar.readMethodXML(t_strMethodFilename);
		IonCombinationGenerator t_genIonComb = new IonCombinationGenerator(t_method);
		t_genIonComb.generate();
		try {
			FileWriter t_fo = new FileWriter(new File( TestResourcePath.RESOURCE_DIR+"testIon.txt" ));
			for ( IonCombination t_ionComb : t_genIonComb.getPossibleIonCombinations() ) {
				t_fo.write(t_ionComb.toString()+": "+t_ionComb.calculateCharge()+": "+t_ionComb.calculateMass()+"\n");
			}
			t_fo.write("Total: "+t_genIonComb.getPossibleIonCombinations().size());
			t_fo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
