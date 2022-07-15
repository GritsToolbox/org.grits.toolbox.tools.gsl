 
package org.grits.toolbox.tools.gsl.handler;

import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;
import org.grits.toolbox.tools.gsl.dialog.LipidCalculatorDialog;

/**
 * Handler class for lipid mass calculator.
 * @author Masaaki Matsubara
 *
 */
public class LipidMassCalculatorHandler {

	private static final Logger logger = Logger.getLogger(LipidMassCalculatorHandler.class);

	@Execute
	public void execute(@Named (IServiceConstants.ACTIVE_SHELL) Shell shell) {
		logger.debug("BEGIN LipidMassCalculatorHandler");

		// Create a new shell
		
		LipidCalculatorDialog t_dialog = new LipidCalculatorDialog(shell);
		t_dialog.open();

		logger.debug("END LipidMassCalculatorHandler");
	}
}