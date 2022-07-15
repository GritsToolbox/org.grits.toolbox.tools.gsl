package org.grits.toolbox.tools.gsl.util.test;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TestMessageDialog {

	public static void main(String[] args) {
		Display t_display = new Display();
		Shell t_shell = new Shell(t_display);
		MessageDialog t_msgDialog = new MessageDialog(t_shell, "Select Database Type", null,
				"Please choose the database type", MessageDialog.QUESTION, new String[] {"Glycan", "Lipid"}, 0);
		int t_iResult = t_msgDialog.open();
		System.out.println(t_iResult);

	}

}
