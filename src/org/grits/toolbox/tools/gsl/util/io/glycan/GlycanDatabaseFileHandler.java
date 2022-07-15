package org.grits.toolbox.tools.gsl.util.io.glycan;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.grits.toolbox.ms.annotation.structure.GlycanDatabase;
import org.grits.toolbox.tools.gsl.dango.GlycanAnnotationUtils;

/**
 * Class for handling deserialized GlycanDatabase.
 * @author Masaaki Matsubara
 *
 */
public class GlycanDatabaseFileHandler {

	private static String m_strGDB = "";
	private static GlycanDatabase m_gDB = null;

	/**
	 * Gets GlycanDatabase from the given file path using JAXB unmarshaller.
	 * @param a_strGDBFilepath String of database file name
	 * @return GlycanDatabase deserialized from the file path
	 */
	public static GlycanDatabase getGlycanDatabase(String a_strGDBFilepath) {
		if ( m_strGDB.equals(a_strGDBFilepath) )
			return m_gDB;
		m_strGDB = a_strGDBFilepath;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(GlycanDatabase.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			m_gDB = (GlycanDatabase) jaxbUnmarshaller.unmarshal( new FileInputStream(a_strGDBFilepath) );
			return m_gDB;
		} catch (JAXBException e) {
			GlycanAnnotationUtils.logger.error("An error during deserializing GlycanDatabase object.", e);
		} catch (FileNotFoundException e) {
			GlycanAnnotationUtils.logger.error("GlycanDatabase XML file not found.", e);
		}
		return null;
	}

}
