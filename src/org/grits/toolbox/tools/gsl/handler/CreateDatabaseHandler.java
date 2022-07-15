 
package org.grits.toolbox.tools.gsl.handler;

import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.grits.toolbox.tools.gsl.wizard.lipidgeneration.LipidDatabaseGenerationWizard;

/**
 * Handler class for creating lipid database.
 * @author Masaaki Matsubara
 * @see LipidDatabaseGenerationWizard
 *
 */
public class CreateDatabaseHandler {

	private static final Logger logger = Logger.getLogger(CreateDatabaseHandler.class);
	

	@Execute
	public void execute(@Named (IServiceConstants.ACTIVE_SHELL) Shell shell) {
		logger.debug("BEGIN CreateDatabaseHandler");

		LipidDatabaseGenerationWizard wizard = new LipidDatabaseGenerationWizard();
		WizardDialog wizardDialog = new WizardDialog(shell, wizard);
		wizardDialog.open();

		logger.debug("END CreateDatabaseHandler");
	}

//	private Entry createCreateDatabaseDialog(Shell shell) {
//		CreateDatabaseTitleAreaDialog dialog = new CreateDatabaseTitleAreaDialog(shell);
//		return dialog;
//	}
}