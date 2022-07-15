package org.grits.toolbox.tools.gsl.annotation.preference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.grits.toolbox.core.datamodel.UnsupportedVersionException;
import org.grits.toolbox.core.preference.share.PreferenceEntity;
import org.grits.toolbox.core.preference.share.PreferenceReader;
import org.grits.toolbox.core.preference.share.PreferenceWriter;
import org.grits.toolbox.entry.ms.annotation.glycan.preference.MSGlycanAnnotationPreference;
import org.grits.toolbox.entry.ms.annotation.glycan.preference.MSGlycanAnnotationSettingsPreference;
import org.grits.toolbox.entry.ms.annotation.glycan.util.PreferenceUtils;
import org.grits.toolbox.tools.gsl.Activator;

/**
 * Extends MSGlycanAnnotationSettingsPreference for preference of DANGO annotation settings.
 * @author Masaaki Matsubara
 *
 */
@XmlRootElement(name="msGlycolipidAnnotationSettings")
public class MSGlycolipidAnnotationSettingsPreference extends MSGlycanAnnotationSettingsPreference {
	private static final Logger logger = Logger.getLogger(MSGlycolipidAnnotationSettingsPreference.class);
	private static final String PREFERENCE_NAME_ALL = "org.grits.toolbox.tools.gsl.annotation.settings";
	private static final String CURRENT_VERSION = "1.0";

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.preference.MSGlycanAnnotationSettingsPreference#saveValues()
	 */
	@Override
	public boolean saveValues() {
		PreferenceEntity preferenceEntity = new PreferenceEntity(PREFERENCE_NAME_ALL);
		preferenceEntity.setVersion(CURRENT_VERSION);
		try {
			preferenceEntity.setValue(PreferenceUtils.marshalXML(this));
			return PreferenceWriter.savePreference(preferenceEntity);
		} catch (JAXBException e) {
			logger.error("Could not serialize the preference to the preferences file", e);
		}
		return false;

	}

	/**
	 * Get PreferenceEntity for DANGO annotation.
	 * @return PreferenceEntity for DANTO annotation
	 * @throws UnsupportedVersionException if the version was null for the elementor if the element with the given name was not serializableas the current version PreferenceEntity object
	 */
	public static PreferenceEntity getPreferenceEntity() throws UnsupportedVersionException {
		PreferenceEntity preferenceEntity = PreferenceReader.getPreferenceByName(PREFERENCE_NAME_ALL);
		return preferenceEntity;
	}

	/**
	 * Loads the XML, creates a preference object, and adds it to the preference list
	 *
	 * @param file a XML file
	 */
	private void processPreferenceFile( File file ) {
		BufferedReader bufferedReader;
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
			String line;
			String xmlString = "";
			while ((line = bufferedReader.readLine()) != null) {
				xmlString += line + "\n";
			}
			bufferedReader.close();
			MSGlycanAnnotationPreference pref = (MSGlycanAnnotationPreference) PreferenceUtils
					.unmarshallFromXML(xmlString, MSGlycanAnnotationPreference.class);
			this.getPreferenceList().add(pref);
		} catch (JAXBException e) {
			logger.warn(file.getName() + " is not a valid preference file");
		} catch (FileNotFoundException e1) {
			logger.warn(file.getName() + " is not a valid preference file");
		} catch (IOException e) {
			logger.warn(file.getName() + " is not a valid preference file");
		}
	}

	/**
	 * Loads the default DANGO preferences from the default files
	 */
	@Override
	public void loadDefaults() {
		// load all files in preference folder
		URL resourceURL;
		try {
			resourceURL = FileLocator.toFileURL(
					Platform.getBundle(Activator.PLUGIN_ID).getResource("preference"));
			File preferenceDir= new File(resourceURL.getPath());
			if (preferenceDir.exists() && preferenceDir.isDirectory()) {
				File[] prefSubDirs = preferenceDir.listFiles();
				for (File subDir : prefSubDirs) {
					if( subDir.isDirectory() && subDir.getName().equals("annotation") ) {
						File[] files = subDir.listFiles();
						for (File file : files) {
							if (file.getName().endsWith(".xml")) {
								processPreferenceFile(file);
							}
						}
					}
				}
			}
		} catch (IOException e) {
			logger.error("Could not load default preference files", e);
		} 
	}
}
