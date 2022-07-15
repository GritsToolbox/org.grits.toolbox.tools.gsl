package org.grits.toolbox.tools.gsl.util;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.grits.toolbox.core.utilShare.ResourceLocatorUtils;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.utils.GlycanStructureDatabase;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.utils.GlycanStructureDatabaseIndex;
import org.grits.toolbox.tools.gsl.database.LipidDatabaseIndex;
import org.grits.toolbox.tools.gsl.database.LipidDatabaseList;

/**
 * Class for using Glycan and Lipid databases which contain predefined structures as default.
 * @see org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.utils.DatabaseUtils
 * @author Masaaki Matsubara
 *
 */
public class DatabaseUtilsForGSL {
	private static final String DATABASE_FOLDER = "/databases";
	private static final String GLYCAN_INDEX_FILE = "GlycanDatabases.index";
	private static final String LIPID_LIST_FILE = "LipidDatabases.index";
	private static String DATABASE_PATH = null;

	/**
	 * Gets GlycanStructureDatabaseIndex object defined in GlycanDatabase.index
	 * @return GlycanStructureDatabaseIndex for GSL
	 * @throws IOException
	 * @throws JAXBException
	 */
	public static GlycanStructureDatabaseIndex getGelatoDatabases() throws IOException, JAXBException {
		String t_path = DatabaseUtilsForGSL.getDatabasePath();
		JAXBContext jaxbContext = JAXBContext.newInstance(GlycanStructureDatabaseIndex.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		GlycanStructureDatabaseIndex t_index = (GlycanStructureDatabaseIndex) jaxbUnmarshaller
				.unmarshal(new File(t_path + File.separator + GLYCAN_INDEX_FILE));
		// now we need to add the path
		for ( GlycanStructureDatabase t_database : t_index.getDatabase() ) {
			t_database.setPath(t_path + File.separator + t_database.getFileName());
		}
		return t_index;
	}

	/**
	 * Gets LipidDatabaseList object defined in LipidDatabase.index
	 * @return LipidDatabaseList for GSL
	 * @throws IOException
	 * @throws JAXBException
	 */
	public static LipidDatabaseList getLipidDatabases() throws IOException, JAXBException {
		String t_path = DatabaseUtilsForGSL.getDatabasePath();
		JAXBContext jaxbContext = JAXBContext.newInstance(LipidDatabaseList.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		LipidDatabaseList t_list = (LipidDatabaseList) jaxbUnmarshaller
				.unmarshal(new File(t_path + File.separator + LIPID_LIST_FILE));
		// now we need to add the path
		for ( LipidDatabaseIndex t_index : t_list.getIndices() ) {
			t_index.setPath(t_path + File.separator + t_index.getFileName());
		}
		return t_list;
	}

	/**
	 * Gets database path in org.grits.toolbox.tools.gsl plug-in.
	 * @return String of database path
	 * @throws IOException
	 */
	public static String getDatabasePath() throws IOException {
		if ( DATABASE_PATH == null ) {
			DATABASE_PATH = ResourceLocatorUtils.getLegalPathOfResource(new DatabaseUtilsForGSL(),
					DATABASE_FOLDER);
		}
		return DATABASE_PATH;
	}
}
