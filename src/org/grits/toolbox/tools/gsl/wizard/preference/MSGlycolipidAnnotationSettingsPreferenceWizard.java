package org.grits.toolbox.tools.gsl.wizard.preference;

import org.grits.toolbox.entry.ms.annotation.glycan.preference.MSGlycanAnnotationPreference;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.preference.MSGlycanAnnotationSettingsPreferenceWizard;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.AddAdductsForm;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.AddIonExchangeForm;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.AddNeutralLossForm;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.FragmentSettingsForm;
import org.grits.toolbox.tools.gsl.wizard.annotation.GlycolipidSettingsForm;
import org.grits.toolbox.tools.gsl.wizard.annotation.LipidFragmentSettingsForm;

/**
 * Wizard class for creating setting preferences for MS glycolipid annotation (DANGO).
 * Extends MSGlycanAnnotationSettingsPreferenceWizard.
 * @author Masaaki Matsubara
 *
 */
public class MSGlycolipidAnnotationSettingsPreferenceWizard extends MSGlycanAnnotationSettingsPreferenceWizard {
	//log4J Logger
//	private static final Logger logger = Logger.getLogger(MSGlycolipidAnnotationSettingsPreferenceWizard.class);

	protected GlycolipidSettingsForm glycolipidSettingsForm = null;
	protected LipidFragmentSettingsForm lipidFragmentSettingsForm = null;

	/**
	 * Constructor with preference
	 * @param preference MSGlycanANnotationPreference
	 */
	public MSGlycolipidAnnotationSettingsPreferenceWizard(MSGlycanAnnotationPreference preference) {
		super(preference);
	}

	/**
	 * Constructor with preference name
	 * @param preferenceName String of preference name
	 */
	public MSGlycolipidAnnotationSettingsPreferenceWizard(String preferenceName) {
		super(preferenceName);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.preference.MSGlycanAnnotationSettingsPreferenceWizard#addPages()
	 */
	@Override
	public void addPages() {
//		glycanSettingsForm = new GlycolipidSettingsForm(method,this,filterLibrary);
		glycolipidSettingsForm = new GlycolipidSettingsForm(method,this,filterLibrary);
		fragmentSettingsForm = new FragmentSettingsForm(method);
//		lipidFragmentSettingsForm = new LipidFragmentSettingsForm(method);
		ionSettingsForm = new AddAdductsForm(method);
		ionExchangeForm = new AddIonExchangeForm(method);
		neutralLossForm = new AddNeutralLossForm(method);
//		addPage(glycanSettingsForm);
		addPage(glycolipidSettingsForm);
		addPage(fragmentSettingsForm);
//		addPage(lipidFragmentSettingsForm);
		addPage(ionSettingsForm);
		addPage(ionExchangeForm);
		addPage(neutralLossForm);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.preference.MSGlycanAnnotationSettingsPreferenceWizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
/*		if(glycanSettingsForm.canFinish())
			glycanSettingsForm.save();
		else 
			return false;
*/		if(glycolipidSettingsForm.canFinish())
			glycolipidSettingsForm.save();
		else 
			return false;
		if(fragmentSettingsForm.canFinish())
			fragmentSettingsForm.save();
		else 
			return false;
/*		if(this.lipidFragmentSettingsForm.canFlipToNextPage())
			this.lipidFragmentSettingsForm.save();
		else 
			return false;
*/		if(ionSettingsForm.canFlipToNextPage())
			ionSettingsForm.save();
		else 
			return false;
		if(ionExchangeForm.canFlipToNextPage())
			ionExchangeForm.save();
		else 
			return false;
		if(neutralLossForm.isPageComplete())
			neutralLossForm.save();
		else 
			return false;
		
		return ionSettingsForm.isPageComplete() &&
//			   glycanSettingsForm.canFlipToNextPage() &&
			   glycolipidSettingsForm.canFlipToNextPage() &&
			   fragmentSettingsForm.canFinish() &&
			   ionExchangeForm.isPageComplete() &&
			   neutralLossForm.isPageComplete();
	}

}
