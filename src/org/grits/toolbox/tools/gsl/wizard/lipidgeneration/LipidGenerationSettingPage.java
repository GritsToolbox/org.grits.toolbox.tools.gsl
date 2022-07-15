package org.grits.toolbox.tools.gsl.wizard.lipidgeneration;

import org.apache.log4j.Logger;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.grits.toolbox.tools.gsl.util.generator.LipidGeneratorUtils;
import org.grits.toolbox.tools.gsl.util.generator.LipidParametersIO;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;

/**
 * WizardPage for lipid generation setting page of lipid database generator.
 * Users can choose type of lipid (Ceramide or sphingosine) and specify parameters of the lipid components at this page.
 * @author Masaaki Matsubara
 *
 */
public class LipidGenerationSettingPage extends WizardPage {
	private static final Logger logger = Logger.getLogger(LipidGenerationSettingPage.class);

	private Button m_radioCeramide;
	private Button m_radioSphingosine;
	private Text m_textCarbonLengthSp;
	private Text m_textHydroxylGroupsSp;
	private Text m_textNumUnsatSp;
	private Button m_btnOnlyEvenNumbersSp;
	private Text m_textCarbonLengthFA;
	private Text m_textHydroxylGroupsFA;
	private Text m_textNumUnsatFA;
	private Button m_btnOnlyEvenNumbersFA;
	private Label m_lblNumberOfLipids;

	public boolean isSelectedCeramide() {
		return this.m_radioCeramide.getSelection();
	}

	public boolean isSelectedSphingosine() {
		return this.m_radioSphingosine.getSelection();
	}

	public String getCarbonLengthForSphingosine() {
		return this.m_textCarbonLengthSp.getText().trim();
	}

	public String getNumberOfHydroxyGroupsForSphingosine() {
		return this.m_textHydroxylGroupsSp.getText().trim();
	}

	public String getNumberOfUnsaturationForSphingosine() {
		return this.m_textNumUnsatSp.getText().trim();
	}

	public boolean isSelectedOnlyEvenNumbersForSphingosine() {
		return this.m_btnOnlyEvenNumbersSp.getSelection();
	}

	public String getCarbonLengthForFattyAcid() {
		return this.m_textCarbonLengthFA.getText().trim();
	}

	public String getNumberOfHydroxyGroupsForFattyAcid() {
		return this.m_textHydroxylGroupsFA.getText().trim();
	}

	public String getNumberOfUnsaturationForFattyAcid() {
		return this.m_textNumUnsatFA.getText().trim();
	}

	public boolean isSelectedOnlyEvenNumbersForFattyAcid() {
		return this.m_btnOnlyEvenNumbersFA.getSelection();
	}

	public LipidGenerationSettingPage() {
		super("Lipid Database Name");
		setTitle("Lipid Parameters");
		setDescription("Choose lipid and enter the parameters for generating lipid.");
	}

	/**
	 * Create contents of the wizard page.
	 * @param parent parent Composite
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		GridLayout gl_area = new GridLayout(1, false);
		container.setLayout(gl_area);
		
		/*
		 * Choose lipid type (Ceramide or Sphingosine)
		 */
		// Group for choosing lipid type (default is ceramide)
		Group grpLipidType = new Group(container, SWT.NONE);
		grpLipidType.setText("Lipid class");
		grpLipidType.setLayout(new GridLayout(2, false));
		grpLipidType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		this.m_radioCeramide = new Button(grpLipidType, SWT.RADIO);
		this.m_radioCeramide.setText("Ceramide");
		this.m_radioCeramide.setSelection(true);
		
		this.m_radioSphingosine = new Button(grpLipidType, SWT.RADIO);
		this.m_radioSphingosine.setText("Sphingosine");

		// Group for Sphingosine's parameters
		Group grpSp = new Group(container, SWT.NONE);
		grpSp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpSp.setText("Sphingosine");
		grpSp.setLayout(new GridLayout(2, false));
		
		this.makeLabel(grpSp, "Carbon length");
		this.m_textCarbonLengthSp = this.makeParameterText(grpSp, "14-30");
		this.makeLabel(grpSp, "# of hydroxyl groups");
		this.m_textHydroxylGroupsSp = this.makeParameterText(grpSp, "1-4");
		this.makeLabel(grpSp, "# of unsaturation");
		this.m_textNumUnsatSp = this.makeParameterText(grpSp, "0-3");
		
		this.m_btnOnlyEvenNumbersSp
			= this.makeCheckButton(grpSp, "Only allow even number for carbon length");
		
		// Group for Fatty Acid's parameters
		Group grpFA = new Group(container, SWT.NONE);
		grpFA.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpFA.setText("Fatty Acid");
		grpFA.setLayout(new GridLayout(2, false));
		
		this.makeLabel(grpFA, "Carbon length");
		this.m_textCarbonLengthFA = this.makeParameterText(grpFA, "14-26");
		this.makeLabel(grpFA, "# of hydroxyl groups");
		this.m_textHydroxylGroupsFA = this.makeParameterText(grpFA, "0-2");
		this.makeLabel(grpFA, "# of unsaturation");
		this.m_textNumUnsatFA = this.makeParameterText(grpFA, "0-3");
		
		this.m_btnOnlyEvenNumbersFA
			= this.makeCheckButton(grpFA, "Only allow even number for carbon length");

		// Label for number of lipids user will get
		this.m_lblNumberOfLipids = new Label(container, SWT.NONE);
		this.m_lblNumberOfLipids.setAlignment(SWT.LEFT);

		// Control by SelectionListner in radio button for lipid selection
		this.m_radioCeramide.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.getSource();
				if (!button.getSelection()) return;
				grpFA.setVisible(true);

				setPageCompleteVerifyingTexts();
				writeNumberOfLipids();
			}
		});
		
		this.m_radioSphingosine.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.getSource();
				if (!button.getSelection()) return;
				grpFA.setVisible(false);

				setPageCompleteVerifyingTexts();
				writeNumberOfLipids();
			}
		});

		setPageCompleteVerifyingTexts();
		writeNumberOfLipids();
	}

	private Label makeLabel(Group grp, String strLabel) {
		Label lbl = new Label(grp, SWT.NONE);
		lbl.setAlignment(SWT.RIGHT);
		lbl.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lbl.setText(strLabel);
		return lbl;
	}

	private Text makeText(Group grp, String strInit) {
		Text txt = new Text(grp, SWT.BORDER);
		txt.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		txt.setText(strInit);
		return txt;
	}

	private Text makeParameterText(Group grp, String strInit) {
		Text txt = this.makeText(grp, strInit);
		// Set listner for verifying the format
		this.setListnerToParameterText(txt);
		return txt;
	}

	private Button makeCheckButton(Group grp, String strText) {
		Button btnChk = new Button(grp, SWT.CHECK);
		btnChk.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnChk.setText(strText);
		// Set listener
		btnChk.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				writeNumberOfLipids();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				writeNumberOfLipids();
			}
		});
		return btnChk;
	}

	private boolean verifyParameterText(Text a_txt) {
		String t_strParam = a_txt.getText().trim();
		try {
			if ( !LipidGeneratorUtils.validateValueRangesFormat(t_strParam) ) {
//				a_txt.setToolTipText("Values are required.");
				setErrorMessage("Values are required.");
				logger.debug("Values are requeired.");
				return false;
			}
		} catch (NumberFormatException nfe) {
//			a_txt.setToolTipText(nfe.getMessage());
			setErrorMessage(nfe.getMessage());
			logger.debug(nfe.getMessage());
			return false;
		}
//		a_txt.setToolTipText(null);
		setErrorMessage(null);
		return true;
	}

	/**
	 * Set page complete verifying texts. Make the text background yellow if text is not verified.
	 */
	private void setPageCompleteVerifyingTexts() {
		// Get default background color (white)
		Color t_colDef = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
		// Yellow for error background color
		Color t_colErr = Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);

		// Verify texts. Change background color if error.
//		boolean t_bErr = false;
		/// For Carbon length of sphingosine
		if ( !this.verifyParameterText(this.m_textCarbonLengthSp) ) {
			setPageComplete(false);
			this.m_textCarbonLengthSp.setBackground(t_colErr);
			return;
		} else {
			this.m_textCarbonLengthSp.setBackground(t_colDef);
		}
		/// For # of OH groups of sphingosine
		if ( !this.m_textHydroxylGroupsSp.getText().trim().isEmpty()
		  && !this.verifyParameterText(this.m_textHydroxylGroupsSp) ) {
			setPageComplete(false);
			this.m_textHydroxylGroupsSp.setBackground(t_colErr);
			return;
		} else {
			this.m_textHydroxylGroupsSp.setBackground(t_colDef);
		}
		/// For # of double bonds of sphingosine
		if ( !this.m_textNumUnsatSp.getText().trim().isEmpty()
		  && !this.verifyParameterText(this.m_textNumUnsatSp) ) {
			setPageComplete(false);
			this.m_textNumUnsatSp.setBackground(t_colErr);
			return;
		} else {
			this.m_textNumUnsatSp.setBackground(t_colDef);
		}

		// For fatty acid only when ceramide is selected
		if ( !m_radioCeramide.getSelection() ) {
			setPageComplete(true);
			return;
		}
		/// For Carbon length of sphingosine of fatty acid
		if ( !this.verifyParameterText(this.m_textCarbonLengthFA) ) {
			setPageComplete(false);
			this.m_textCarbonLengthFA.setBackground(t_colErr);
			return;
		} else {
			this.m_textCarbonLengthFA.setBackground(t_colDef);
		}
		/// For # of OH groups of fatty acid
		if ( !this.m_textHydroxylGroupsFA.getText().trim().isEmpty()
		  && !this.verifyParameterText(this.m_textHydroxylGroupsFA) ) {
			setPageComplete(false);
			this.m_textHydroxylGroupsFA.setBackground(t_colErr);
			return;
		} else {
			this.m_textHydroxylGroupsFA.setBackground(t_colDef);
		}
		/// For # of double bonds of fatty acid
		if ( !this.m_textNumUnsatFA.getText().trim().isEmpty()
		  && !this.verifyParameterText(this.m_textNumUnsatFA) ) {
			setPageComplete(false);
			this.m_textNumUnsatFA.setBackground(t_colErr);
			return;
		} else {
			this.m_textNumUnsatFA.setBackground(t_colDef);
		}

		// Set page complete if no error
		setPageComplete(true);
	}

	private int calculateNumberOfLipid() {
		int t_nLipids = 0;
		// For sphingosine
		LipidParametersIO t_lipParams = new LipidParametersIO();
		t_lipParams.setCarbonLengths( this.m_textCarbonLengthSp.getText().trim() );
		if ( !this.m_textHydroxylGroupsSp.getText().trim().isEmpty() )
			t_lipParams.setNumbersOfHydroxylGroups( this.m_textHydroxylGroupsSp.getText().trim() );
		if ( !this.m_textNumUnsatSp.getText().trim().isEmpty() )
			t_lipParams.setNumbersOfDoubleBonds( this.m_textNumUnsatSp.getText().trim() );
		t_lipParams.setOnlyEvenNumberForCarbonLength( this.m_btnOnlyEvenNumbersSp.getSelection() );
		t_nLipids = t_lipParams.calculateNumberOfCombination();

		if ( this.isSelectedSphingosine() )
			return t_nLipids;

		// For fatty acid
		t_lipParams = new LipidParametersIO();
		t_lipParams.setCarbonLengths( this.m_textCarbonLengthFA.getText().trim() );
		if ( !this.m_textHydroxylGroupsFA.getText().trim().isEmpty() )
			t_lipParams.setNumbersOfHydroxylGroups( this.m_textHydroxylGroupsFA.getText().trim() );
		if ( !this.m_textNumUnsatFA.getText().trim().isEmpty() )
			t_lipParams.setNumbersOfDoubleBonds( this.m_textNumUnsatFA.getText().trim() );
		t_lipParams.setOnlyEvenNumberForCarbonLength( this.m_btnOnlyEvenNumbersFA.getSelection() );
		t_nLipids *= t_lipParams.calculateNumberOfCombination();

		return t_nLipids;
	}

	private void writeNumberOfLipids() {
		if ( !isPageComplete() ) {
			this.m_lblNumberOfLipids.setText("");
			this.m_lblNumberOfLipids.getParent().layout();
			return;
		}
		int t_nLipids = this.calculateNumberOfLipid();
		this.m_lblNumberOfLipids.setText(t_nLipids+" lipids will be generated.");
		this.m_lblNumberOfLipids.getParent().layout();
	}

	private void setListnerToParameterText(Text a_txt) {
		a_txt.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setPageCompleteVerifyingTexts();
				writeNumberOfLipids();
			}
		});
		a_txt.addListener(SWT.Traverse, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setPageCompleteVerifyingTexts();
				writeNumberOfLipids();
			}
		});
	}
}
