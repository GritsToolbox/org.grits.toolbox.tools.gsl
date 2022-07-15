package org.grits.toolbox.tools.gsl.wizard.lipidgeneration;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Button;

/**
 * WizardPage for lipid database export setting page of lipid database generator.
 * @author Masaaki Matsubara
 *
 */
public class LipidDatabaseExportSettingPage extends WizardPage {

	/**
	 * Create the wizard.
	 */
	public LipidDatabaseExportSettingPage() {
		super("wizardPage");
		setTitle("Wizard Page title");
		setDescription("Wizard Page description");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent parent Composite
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(1, false));
		
		Group grpIonizationType = new Group(container, SWT.NONE);
		grpIonizationType.setText("Ionization Type");
		grpIonizationType.setLayout(new GridLayout(1, false));
		
		Button m_btnShowPH = new Button(grpIonizationType, SWT.CHECK);
		m_btnShowPH.setText("[M + H]+");
		
		Button m_btnShowMH = new Button(grpIonizationType, SWT.CHECK);
		m_btnShowMH.setText("[M - H]-");
		
		Button m_btnShowPNa = new Button(grpIonizationType, SWT.CHECK);
		m_btnShowPNa.setText("[M + Na]+");
		
		Button m_btnShowPHMH2O = new Button(grpIonizationType, SWT.CHECK);
		m_btnShowPHMH2O.setText("[M + H - H2O]+");
	}

}
