package org.grits.toolbox.tools.gsl.util.io.lipid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.grits.toolbox.tools.gsl.database.lipid.LipidDatabase;

/**
 * Class for serializing and deserializing LipidDatabase.
 * @author Masaaki Matsubara
 *
 */
public class LipidDatabaseFileHandler {

	public static final Logger logger = Logger.getLogger(LipidDatabaseFileHandler.class);

	/**
	 * Exports the given LipidDatabase to XML file with the given name
	 * @param a_strFilename XML file name
	 * @param a_database LipidDatabase to be exported
	 */
	public static void exportXML(String a_strFilename, LipidDatabase a_database) {
		try {
			// Create JAXB context and instantiate marshallar
			JAXBContext context = JAXBContext.newInstance(LipidDatabase.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			// Write to File
			m.marshal(a_database, new File(a_strFilename));
			
		} catch (JAXBException e) {
			logger.error("An error in export LipidDatabase to XML", e);
		}

	}

	/**
	 * Imports LipidDatabase from the given file name.
	 * @param a_strFileName String of XML file name
	 * @return LipidDatabase serialized from the given file name
	 */
	public static LipidDatabase importXML(String a_strFileName){
		try {
			JAXBContext context = JAXBContext.newInstance(LipidDatabase.class);
			Unmarshaller um = context.createUnmarshaller();
			return (LipidDatabase) um.unmarshal(new FileReader( a_strFileName ));
		} catch (JAXBException e) {
			logger.error("An error in importing LipidDatabase from XML.", e);
		} catch (FileNotFoundException e) {
			logger.error("LipidDatabase XML file not found.", e);
		}
		return null;
	}

}
