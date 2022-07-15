package org.grits.toolbox.tools.gsl.annotation.datamodel.preference;

import org.apache.log4j.Logger;
import org.grits.toolbox.core.datamodel.UnsupportedVersionException;
import org.grits.toolbox.core.preference.share.PreferenceEntity;
import org.grits.toolbox.datamodel.ms.tablemodel.FillTypes;
import org.grits.toolbox.display.control.table.preference.TableViewerPreference;

/**
 * Class for loading preference of MSGlycolipidAnnotationSummaryViewer.
 * Preferences are loaded by MSGlycolipidAnnotationSummaryViewerPreference.
 * @see org.grits.toolbox.tools.gsl.annotation.datamodel.preference.MSGlycolipidAnnotationSummaryViewerPreference
 * @author Masaaki Matsubara
 *
 */
public class MSGlycolipidAnnotationSummaryViewerPreferenceLoader {
	private static final Logger logger = Logger.getLogger(MSGlycolipidAnnotationSummaryViewerPreferenceLoader.class);

	/**
	 * Get MSGlycolipidAnnotationSummaryViewerPreference for the given MS level and fill types of the MSGlycolipidAnnotationSummary GRITS table.
	 * @param _iMSLevel MS level for the preference of the GRITS tabel
	 * @param fillType FillTypes of the preference of the GRITS table
	 * @return MSGlycolipidAnnotationSummaryViewerPreference for the given MS level and fill types
	 */
	public static MSGlycolipidAnnotationSummaryViewerPreference getTableViewerPreference(int _iMSLevel, FillTypes fillType )  {
		MSGlycolipidAnnotationSummaryViewerPreference preferences = null;
		try {
			PreferenceEntity preferenceEntity = MSGlycolipidAnnotationSummaryViewerPreference.getPreferenceEntity(_iMSLevel, fillType); 
			if( preferenceEntity != null ) {
				preferences = (MSGlycolipidAnnotationSummaryViewerPreference) TableViewerPreference.getTableViewerPreference(
						preferenceEntity, MSGlycolipidAnnotationSummaryViewerPreference.class);
			}
		} catch (UnsupportedVersionException ex) {
			logger.error(ex.getMessage(), ex);
			
		} catch( Exception ex ) {
			logger.error(ex.getMessage(), ex);
		}		
		if( preferences == null ) {
			preferences = new MSGlycolipidAnnotationSummaryViewerPreference();
			preferences.setFillType(fillType);
			preferences.setMSLevel(_iMSLevel);
			preferences.setColumnSettings("");
		}
		return preferences;
	}

}
