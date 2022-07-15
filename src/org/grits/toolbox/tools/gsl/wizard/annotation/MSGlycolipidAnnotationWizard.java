package org.grits.toolbox.tools.gsl.wizard.annotation;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.grits.toolbox.core.datamodel.UnsupportedVersionException;
import org.grits.toolbox.entry.ms.annotation.glycan.preference.MSGlycanAnnotationPreference;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.AddAdductsForm;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.AddIonExchangeForm;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.AddNeutralLossForm;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.FragmentSettingsForm;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.GeneralInformationMulti;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.MSGlycanAnnotationWizard;
import org.grits.toolbox.ms.om.data.Method;
import org.grits.toolbox.tools.gsl.annotation.preference.MSGlycolipidAnnotationSettingsPreference;
import org.grits.toolbox.util.structure.glycan.util.FilterUtils;

/**
 * Wizard for collecting the settings for the glycolipid annotation process.
 * @author Masaaki Matsubara
 *
 */

public class MSGlycolipidAnnotationWizard extends MSGlycanAnnotationWizard {
	//log4J Logger
	private static final Logger logger = Logger.getLogger(MSGlycolipidAnnotationWizard.class);
	
	protected GlycolipidSettingsForm glycolipidSettingsForm = null;
	protected LipidFragmentSettingsForm lipidFragmentSettingsForm = null;

	public MSGlycolipidAnnotationWizard(){
		setWindowTitle("MS Annotation");
		method = new Method();
		try {
			filterLibrary = FilterUtils.readFilters(getDefaultFiltersPath());
		} catch (UnsupportedEncodingException e) {
			logger.error("Error getting the filters", e);
		} catch (FileNotFoundException e) {
			logger.error("Error getting the filters", e);
		} catch (JAXBException e) {
			logger.error("Error getting the filters", e);
		}
	}

	public GlycolipidSettingsForm getGlycolipidSettingsForm() {
		return this.glycolipidSettingsForm;
	}

	public void setGlycolipidSettingsForm(GlycolipidSettingsForm one) {
		this.glycolipidSettingsForm = one;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.MSGlycanAnnotationWizard#addPages()
	 */
	@Override
	public void addPages() {
		// Construct GeneralInformationMulti with DANGO annotation description
		this.initial = new GeneralInformationMulti(this.msEntries, "New MS Glycolipid Annotation", "Identify Glycolipids using DANGO.", "DANGO");
		// Try to read preference from MSGlycolipidAnnotationSettingPreference
		try {
			initial.loadPreferences(MSGlycolipidAnnotationSettingsPreference.class, MSGlycolipidAnnotationSettingsPreference.getPreferenceEntity());
		} catch (UnsupportedVersionException e) {
			logger.error("Could not load preferences", e);
		}
		this.glycolipidSettingsForm = new GlycolipidSettingsForm(this.method,this,this.filterLibrary);
		this.fragmentSettingsForm = new FragmentSettingsForm(this.method);
//		this.lipidFragmentSettingsForm = new LipidFragmentSettingsForm(this.method);
		this.ionSettingsForm = new AddAdductsForm(this.method);
		this.ionExchangeForm = new AddIonExchangeForm(this.method);
		this.neutralLossForm = new AddNeutralLossForm(this.method);
		addPage(this.initial);
		addPage(this.glycolipidSettingsForm);
		addPage(this.fragmentSettingsForm);
//		addPage(this.lipidFragmentSettingsForm);
		addPage(this.ionSettingsForm);
		addPage(this.ionExchangeForm);
		addPage(this.neutralLossForm);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.MSGlycanAnnotationWizard#setPreferences(org.grits.toolbox.entry.ms.annotation.glycan.preference.MSGlycanAnnotationPreference)
	 */
	@Override
	public void setPreferences(MSGlycanAnnotationPreference preferences) {
		this.preferences = preferences;
		// update all the pages
		//TODO
		glycolipidSettingsForm.updateControlsFromPreferences();
		fragmentSettingsForm.updateControlsFromPreferences();
		ionSettingsForm.updateControlsFromPreferences();
		ionExchangeForm.updateControlsFromPreferences();
		neutralLossForm.updateControlsFromPreferences();
		//TODO
		//lipidFragmentSettingsForm.updateControlsFromPreferences();
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.MSGlycanAnnotationWizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		if(this.glycolipidSettingsForm.canFlipToNextPage())
			this.glycolipidSettingsForm.save();
		else 
			return false;
		if(this.fragmentSettingsForm.canFlipToNextPage())
			this.fragmentSettingsForm.save();
		else 
			return false;
/*		if(this.lipidFragmentSettingsForm.canFlipToNextPage())
			this.lipidFragmentSettingsForm.save();
		else 
			return false;
*/		if(this.ionSettingsForm.canFlipToNextPage())
			this.ionSettingsForm.save();
		else 
			return false;
		if(this.ionExchangeForm.canFlipToNextPage())
			this.ionExchangeForm.save();
		else 
			return false;
		if(this.neutralLossForm.isPageComplete())
			this.neutralLossForm.save();
		else 
			return false;
		
		return this.ionSettingsForm.isPageComplete() &&
			   this.initial.canFlipToNextPage() &&
			   this.glycolipidSettingsForm.canFlipToNextPage() &&
			   this.fragmentSettingsForm.canFinish() &&
//			   this.lipidFragmentSettingsForm.canFinish() &&
			   this.ionExchangeForm.isPageComplete() &&
			   this.neutralLossForm.isPageComplete();
	}
}

