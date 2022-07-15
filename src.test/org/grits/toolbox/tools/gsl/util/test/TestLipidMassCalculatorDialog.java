package org.grits.toolbox.tools.gsl.util.test;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.grits.toolbox.tools.gsl.dialog.LipidCalculatorDialog;

public class TestLipidMassCalculatorDialog {

	public static void main(String[] args) {
		Display t_display = new Display();
		Shell t_shell = new Shell(t_display);
		LipidCalculatorDialog t_dialog = new LipidCalculatorDialog(t_shell);
		t_dialog.open();

	}

}
