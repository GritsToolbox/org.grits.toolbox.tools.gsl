package org.grits.toolbox.tools.gsl.annotation.datamodel.preference;

import org.apache.log4j.Logger;
import org.grits.toolbox.core.datamodel.UnsupportedVersionException;
import org.grits.toolbox.core.preference.share.PreferenceEntity;
import org.grits.toolbox.datamodel.ms.tablemodel.FillTypes;
import org.grits.toolbox.display.control.table.preference.TableViewerPreference;

/**
 * Class for loading preference of MSGlycolipidAnnotationViewer.
 * Preferences are loaded by MSGlycolipidAnnotationViewerPreference.
 * @see org.grits.toolbox.tools.gsl.annotation.datamodel.preference.MSGlycolipidAnnotationViewerPreference
 * @author Masaaki Matsubara
 *
 */
public class MSGlycolipidAnnotationViewerPreferenceLoader {
	private static final Logger logger = Logger.getLogger(MSGlycolipidAnnotationViewerPreferenceLoader.class);

	/**
	 * Get MSGlycolipidAnnotationViewerPreference for the given MS level and fill types of the MSGlycolipidAnnotation GRITS table.
	 * @param _iMSLevel MS level for the preference of the GRITS tabel
	 * @param fillType FillTypes of the preference of the GRITS table
	 * @return MSGlycolipidAnnotationViewerPreference for the given MS level and fill types
	 */
	public static MSGlycolipidAnnotationViewerPreference getTableViewerPreference(int _iMSLevel, FillTypes fillType )  {
		MSGlycolipidAnnotationViewerPreference preferences = null;
		try {
			PreferenceEntity preferenceEntity = MSGlycolipidAnnotationViewerPreference.getPreferenceEntity(_iMSLevel, fillType); 
			if( preferenceEntity != null ) {
				preferences = (MSGlycolipidAnnotationViewerPreference) TableViewerPreference.getTableViewerPreference(preferenceEntity, MSGlycolipidAnnotationViewerPreference.class);
			}
		} catch (UnsupportedVersionException ex) {
			logger.error(ex.getMessage(), ex);
			
		} catch( Exception ex ) {
			logger.error(ex.getMessage(), ex);
		}		
		if( preferences == null ) {
			preferences = new MSGlycolipidAnnotationViewerPreference();
			preferences.setFillType(fillType);
			preferences.setMSLevel(_iMSLevel);
			preferences.setColumnSettings("");
		}
		return preferences;
	}

}
