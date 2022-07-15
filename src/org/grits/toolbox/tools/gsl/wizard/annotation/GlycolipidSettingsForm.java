package org.grits.toolbox.tools.gsl.wizard.annotation;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.GlycanSettingsForm;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.GlycanSettingsTableWithActions;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.MSGlycanAnnotationWizard;
import org.grits.toolbox.ms.om.data.Method;
import org.grits.toolbox.util.structure.glycan.filter.om.FiltersLibrary;

/**
 * Extends GlycanSettingsForm to use GlycolipidSettingsTableWithActions.
 * @author Masaaki Matsubara
 * @see GlycolipidSettingsTableWithActions
 *
 */
public class GlycolipidSettingsForm extends GlycanSettingsForm {

	/**
	 * 
	 * @param method Method object to be filled
	 * @param wizard MSGlycolipidAnnotationWizard containing this page
	 * @param filterLibrary FiltersLibrary having filter information for glycan database
	 */
	public GlycolipidSettingsForm(Method method, MSGlycanAnnotationWizard wizard, FiltersLibrary filterLibrary) {
		super("glycolipidSettings", method, wizard, filterLibrary);
		setTitle("Glycolipid Settings");
		setDescription("Describe general glycolipid settings");
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.GlycanSettingsForm#getSettingsTable(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected GlycanSettingsTableWithActions getSettingsTable (Composite container) {
		return new GlycolipidSettingsTableWithActions(container, SWT.NONE, this);
	}

}
