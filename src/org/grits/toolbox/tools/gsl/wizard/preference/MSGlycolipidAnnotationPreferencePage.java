package org.grits.toolbox.tools.gsl.wizard.preference;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.grits.toolbox.entry.ms.annotation.glycan.preference.MSGlycanAnnotationPreference;
import org.grits.toolbox.entry.ms.annotation.glycan.util.PreferenceUtils;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.preference.MSGlycanAnnotationPreferencePage;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.preference.MSGlycanAnnotationSettingsPreferenceWizard;
import org.grits.toolbox.tools.gsl.annotation.preference.MSGlycolipidAnnotationSettingsPreference;

/**
 * Extends MSGlycanAnnotationPreferencePage to create preference for MSGlycolipidAnnotationSettingsWizard.
 * @author Masaaki Matsubara
 * @see MSGlycolipidAnnotationSettingsPreferenceWizard
 * @see MSGlycolipidAnnotationSettingsPreference
 *
 */
public class MSGlycolipidAnnotationPreferencePage extends MSGlycanAnnotationPreferencePage {

	//log4J Logger
	private static final Logger logger = Logger.getLogger(MSGlycolipidAnnotationPreferencePage.class);

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.preference.MSGlycanAnnotationPreferencePage#getMSAnnotationSettingsPreferenceWizard(org.grits.toolbox.entry.ms.annotation.glycan.preference.MSGlycanAnnotationPreference)
	 */
	@Override
	protected MSGlycanAnnotationSettingsPreferenceWizard getMSAnnotationSettingsPreferenceWizard(MSGlycanAnnotationPreference settings) {
		return new MSGlycolipidAnnotationSettingsPreferenceWizard(settings);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.preference.MSGlycanAnnotationPreferencePage#getMSAnnotationSettingsPreferenceWizard(java.lang.String)
	 */
	@Override
	protected MSGlycanAnnotationSettingsPreferenceWizard getMSAnnotationSettingsPreferenceWizard(String settingsName) {
		return new MSGlycolipidAnnotationSettingsPreferenceWizard(settingsName);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.preference.MSGlycanAnnotationPreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		boolean load = MessageDialog.openConfirm(getShell(), "Are you sure?", 
				"This will remove all the preferences you've created and load the default ones if any. Do you want to continue?");
		if (load) {
			preferences = new MSGlycolipidAnnotationSettingsPreference();
			preferences.loadDefaults();
			initStoredSettingList();
			setEditEnabled(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.preference.MSGlycanAnnotationPreferencePage#loadWorkspacePreferences()
	 */
	@Override
	protected boolean loadWorkspacePreferences() {
		try {
			preferences = PreferenceUtils.getMSGlycanAnnotationSettingsPreferences
					(MSGlycolipidAnnotationSettingsPreference.getPreferenceEntity(), MSGlycolipidAnnotationSettingsPreference.class);
		} catch (Exception ex) {
			logger.error("Error getting the mass spec preferences", ex);
		}
		return (preferences != null);
	}

}
