package org.grits.toolbox.tools.gsl.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.grits.toolbox.tools.gsl.database.IonizationType;
import org.grits.toolbox.tools.gsl.structure.LipidFragmentInfo;
import org.grits.toolbox.tools.gsl.structure.LipidNameParser;
import org.grits.toolbox.tools.gsl.structure.lipid.Ceramide;
import org.grits.toolbox.tools.gsl.structure.lipid.FattyAcid;
import org.grits.toolbox.tools.gsl.structure.lipid.ILipid;
import org.grits.toolbox.tools.gsl.structure.lipid.Lipid;
import org.grits.toolbox.tools.gsl.structure.lipid.Sphingosine;
import org.grits.toolbox.tools.gsl.util.generator.structure.LipidFragmenter;
import org.grits.toolbox.tools.gsl.util.mass.AtomicMass;
import org.grits.toolbox.tools.gsl.util.mass.ChemicalComposition;
import org.grits.toolbox.tools.gsl.util.mass.ChemicalCompositionProvider;

/**
 * TitleAreaDialog for showing lipid information and masses of an inputed lipid name
 * @author Masaaki Masaaki
 *
 */
public class LipidCalculatorDialog extends TitleAreaDialog {

//	private static final Logger logger = Logger.getLogger(LipidCalculatorDialog.class);

	private Text m_txtLipidName;
	private List<Label> m_lLblLipidNames;
	private List<Label> m_lLblNumC;
	private List<Label> m_lLblNumOHs;
	private List<Label> m_lLblNumDBs;

	private String m_strLipidName;
	private ILipid m_iLipid;
	private Text m_txtMassValue;
	private Text m_txtFormula;
	private Button m_btnPerMe;

	private Button m_btnM;
	private Button m_btnMmH2O;
	private Button m_btnMpH;
	private Button m_btnMmH;
	private Button m_btnMpNa;
	private Button m_btnMpHmH2O;
	private Button m_btnIonCustom;
	private Button m_btnIonCustomH2O;

	private Combo m_cmbCustomIonType;
	private Spinner m_spnCustomIonCount;
	private Button m_btnCustomIonNegative;

	private Table m_tblFragmentation;

	public LipidCalculatorDialog(Shell parentShell) {
		super(parentShell);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	@Override
	public void create() {
		super.create();
		setTitle("Lipid Mass Calculator");
		setMessage("Calculate lipid information and masses.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createLipidName(container);
		createLipidInformation(container);
		createMassInformation(container);
//		createFragmentation(container);

		return area;
	}

	private void createLipidName(Composite container) {
		Label lbtFirstName = new Label(container, SWT.NONE);
		lbtFirstName.setText("Lipid name");

		// Create lipid name text
		this.m_txtLipidName = new Text(container, SWT.BORDER);
		this.m_txtLipidName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		parseLipidName();
		// Add modify listner for extract lipid name
		this.m_txtLipidName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				m_iLipid = parseLipidName();
				if (m_iLipid == null) {
					initLipidInformations();
					initMassInformation();
//					initFragmentTable();
					return;
				}
				showStructureInformation(m_iLipid);
				showCalculatedMasses(m_iLipid);
//				updateFragmentTable(m_iLipid);
			}

		});
		new Label(container, SWT.NONE);
		Label lblExample = new Label(container, SWT.NONE);
		lblExample.setText("*Conforming to LIPID MAPS abbreviation.\n"
						+  " e.g. Cer(d18:1/16:0), Sp(d18:1), FA(16:0)");
	}


	private void createLipidInformation(Composite container) {
		Group grpInformation = new Group(container, SWT.NONE);
		grpInformation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		grpInformation.setText("Lipid information");
		grpInformation.setLayout(new GridLayout(4, true));

		// Blank
		new Label(grpInformation, SWT.NONE);

		// Lipid names
		this.m_lLblLipidNames = new ArrayList<>();
		for ( int i=0; i<3; i++ ) {
			Label lblName = new Label(grpInformation, SWT.NONE);
			lblName.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
			this.m_lLblLipidNames.add(lblName);
		}

		// Carbon length
		Label lblNumC = new Label(grpInformation, SWT.NONE);
		lblNumC.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblNumC.setText("Carbon chain length");

		this.m_lLblNumC = new ArrayList<>();
		for ( int i=0; i<3; i++ ) {
			Label lblNum = new Label(grpInformation, SWT.NONE);
			lblNum.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
			this.m_lLblNumC.add(lblNum);
		}

		// Hydroxyl groups
		Label lblNumOH = new Label(grpInformation, SWT.NONE);
		lblNumOH.setText("# of hydroxyl groups");

		this.m_lLblNumOHs = new ArrayList<>();
		for ( int i=0; i<3; i++ ) {
			Label lblNum = new Label(grpInformation, SWT.NONE);
			lblNum.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
			this.m_lLblNumOHs.add(lblNum);
		}

		// Double bonds
		Label lblNumDB = new Label(grpInformation, SWT.NONE);
		lblNumDB.setText("# of double bonds");

		this.m_lLblNumDBs = new ArrayList<>();
		for ( int i=0; i<3; i++ ) {
			Label lblNum = new Label(grpInformation, SWT.NONE);
			lblNum.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
			this.m_lLblNumDBs.add(lblNum);
		}

		this.initLipidInformations();
	}

	private void createMassInformation(Composite container) {
		// Create group
		Group grpMass = new Group(container, SWT.NONE);
		grpMass.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		grpMass.setText("Mass information");
		grpMass.setLayout(new GridLayout(4, false));

		// Show chemical formula
		Label lblComposition = new Label(grpMass, SWT.NONE);
		lblComposition.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblComposition.setText("Chemical Formula");
		
		this.m_txtFormula = new Text(grpMass, SWT.BORDER);
		this.m_txtFormula.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		this.m_txtFormula.setEditable(false);

		// Show mass value
		Label lblMassValue = new Label(grpMass, SWT.NONE);
		lblMassValue.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMassValue.setText("Monoisotopic Mass Value");
		
		this.m_txtMassValue = new Text(grpMass, SWT.BORDER);
		this.m_txtMassValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		this.m_txtMassValue.setEditable(false);

		// Selection listener for updating view
		SelectionListener listener = new SelectionListener(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLabels();
				if ( m_iLipid == null ) return;
				showCalculatedMasses(m_iLipid);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				updateLabels();
				if ( m_iLipid == null ) return;
				showCalculatedMasses(m_iLipid);
			}
		};
		Button btn;

		// Blank
		new Label(grpMass, SWT.NONE);
		
		// Permethylation check box
		btn = new Button(grpMass, SWT.CHECK);
		btn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		btn.setText("Permethylation");
		
		// Set SelectionLister to buttons
		btn.addSelectionListener(listener);
//		this.setSelectionListerToButton(this.m_btnPerMe);
		this.m_btnPerMe = btn;

		// Ionization buttons group
		Label lbl = new Label(grpMass, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lbl.setText("Ionization");
		
		this.m_btnM =     this.createRadioButton(grpMass, "M", listener);
		this.m_btnMmH2O = this.createRadioButton(grpMass, "M - H2O", listener);
		new Label(grpMass, SWT.NONE);

		// Blank
		new Label(grpMass, SWT.NONE);
		
		this.m_btnMpH = this.createRadioButton(grpMass, "[M + H]+", listener);
		this.m_btnMmH = this.createRadioButton(grpMass, "[M - H]-", listener);
		new Label(grpMass, SWT.NONE);

		// Blank
		new Label(grpMass, SWT.NONE);
		
		this.m_btnMpNa =    this.createRadioButton(grpMass, "[M + Na]+", listener);
		this.m_btnMpHmH2O = this.createRadioButton(grpMass, "[M + H - H2O]+", listener);
		new Label(grpMass, SWT.NONE);

		// Custom ion
		lbl = new Label(grpMass, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lbl.setText("Custom ion");

		SelectionListener listenerCustom = new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if ( ((Button)e.widget).getSelection() ) {
					m_spnCustomIonCount.setEnabled(true);
					m_cmbCustomIonType.setEnabled(true);
					m_btnCustomIonNegative.setEnabled(true);
				} else {
					m_spnCustomIonCount.setEnabled(false);
					m_cmbCustomIonType.setEnabled(false);
					m_btnCustomIonNegative.setEnabled(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if ( ((Button)e.widget).getSelection() ) {
					m_spnCustomIonCount.setEnabled(true);
					m_cmbCustomIonType.setEnabled(true);
					m_btnCustomIonNegative.setEnabled(true);
				} else {
					m_spnCustomIonCount.setEnabled(false);
					m_cmbCustomIonType.setEnabled(false);
					m_btnCustomIonNegative.setEnabled(false);
				}
			}
		};
		this.m_btnIonCustom = this.createRadioButton(grpMass, "[M + H]+", listener);
		this.m_btnIonCustom.addSelectionListener(listenerCustom);
		this.m_btnIonCustomH2O = this.createRadioButton(grpMass, "[M + H - H2O]+", listener);
		this.m_btnIonCustomH2O.addSelectionListener(listenerCustom);
		new Label(grpMass, SWT.NONE);

		// Customize ion
		new Label(grpMass, SWT.NONE);

		this.m_spnCustomIonCount = new Spinner(grpMass, SWT.BORDER);
		this.m_spnCustomIonCount.setMinimum(1);
		this.m_spnCustomIonCount.setMaximum(10);
		this.m_spnCustomIonCount.setSelection(1);
		this.m_spnCustomIonCount.addSelectionListener(listener);
		this.m_spnCustomIonCount.setEnabled(false);

		this.m_cmbCustomIonType = new Combo(grpMass, SWT.NONE);
		this.m_cmbCustomIonType.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		this.m_cmbCustomIonType.setItems(new String[] {"H", "Na", "Li", "K"});
		this.m_cmbCustomIonType.select(0);
		this.m_cmbCustomIonType.addSelectionListener(listener);
		this.m_cmbCustomIonType.setEnabled(false);

		this.m_btnCustomIonNegative = new Button(grpMass, SWT.CHECK);
		this.m_btnCustomIonNegative.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		this.m_btnCustomIonNegative.setText("Negative mode");
		this.m_btnCustomIonNegative.addSelectionListener(listener);
		this.m_btnCustomIonNegative.setEnabled(false);

		// Set default to M
		this.m_btnM.setSelection(true);
		new Label(grpMass, SWT.NONE);
		new Label(grpMass, SWT.NONE);
	}

	private Button createRadioButton(Group a_grp, String a_strLabel, SelectionListener a_listener) {
		Button t_btn = new Button(a_grp, SWT.RADIO);
		t_btn.setText(a_strLabel);
		t_btn.addSelectionListener(a_listener);
		return t_btn;
	}

	private void updateCustomIonLabel() {
		// Generate string
		String strLabel = "[M ";
		String strCharge = (this.m_btnCustomIonNegative.getSelection())? "-" : "+";
		strLabel += strCharge+" ";
		int n = this.m_spnCustomIonCount.getSelection();
		if ( n > 1 ) {
			strLabel += n;
			strCharge = n+strCharge;
		}
		strLabel += this.m_cmbCustomIonType.getText() + "]"+strCharge;
		String t_strOLoss = (this.m_btnPerMe.getSelection())? "MeOH" : "H2O";
		String strLabelH2O = strLabel.replace("]", " - "+t_strOLoss+"]");

		this.m_btnIonCustom.setText(strLabel);
		this.m_btnIonCustomH2O.setText(strLabelH2O);
	}

	private void createFragmentation(Composite container) {
		Group grpAbundanceTable = new Group(container, SWT.NONE);
		grpAbundanceTable.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		grpAbundanceTable.setText("Fragments");
		grpAbundanceTable.setLayout(new GridLayout(1, true));

		this.m_tblFragmentation = new Table(grpAbundanceTable, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		GridData gData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gData.heightHint = 200;
		this.m_tblFragmentation.setLayoutData(gData);
		this.m_tblFragmentation.setLinesVisible(true);
		this.m_tblFragmentation.setHeaderVisible(true);
		String[] t_aHeaders = {"Name", "Type", "Composition", "M", "[M + H]+"};
		for (int i=0; i<t_aHeaders.length; i++) {
			TableColumn t_col = new TableColumn(this.m_tblFragmentation, SWT.NONE);
			t_col.setText(t_aHeaders[i]);
//			t_col.setResizable(true);
			t_col.setMoveable(true);
			this.m_tblFragmentation.getColumn(i).pack();
		}
		this.updateFragmentTable(null);
		this.m_tblFragmentation.setSize(this.m_tblFragmentation.computeSize(SWT.DEFAULT, 200));

		grpAbundanceTable.pack();
	}

	private void initFragmentTable() {
		this.m_tblFragmentation.removeAll();
		
	}

	private void updateFragmentTable(ILipid a_lipid) {
		initFragmentTable();
		if ( a_lipid == null )
			return;
		LipidFragmenter t_lipFrag = new LipidFragmenter();
		List<LipidFragmentInfo> t_lFragInfo = t_lipFrag.fragment(a_lipid);

		for ( LipidFragmentInfo t_lipFragInfo : t_lFragInfo ) {
			TableItem t_item = new TableItem(this.m_tblFragmentation, SWT.NONE);
			t_item.setText(0, t_lipFragInfo.getFragment().getName() );
			t_item.setText(1, t_lipFragInfo.getFragmentType() );
			ChemicalComposition t_calcMass
				= ChemicalCompositionProvider.getChemicalComposition(t_lipFragInfo.getFragment());
			t_item.setText(2, t_calcMass.getFormula());
			t_item.setText(3, t_calcMass.getMonoisotopicMass());
			t_item.setText(4, t_calcMass.getMonoisotopicMass(IonizationType.PH));
//			t_item.setText(5, t_calcMass.getMonoisotopicMass(IonizationType.PNA));
		}
		for (int i=0; i < this.m_tblFragmentation.getColumnCount(); i++) {
			this.m_tblFragmentation.getColumn(i).pack();
		}
	}

	private ILipid parseLipidName() {
		// Parse lipid name
		String t_strLipid = this.m_txtLipidName.getText().trim();
		if ( t_strLipid.isEmpty() ) {
			setErrorMessage("Please enter a lipid name.");
			return null;
		}
		ILipid t_iLip = LipidNameParser.parseLipidName(t_strLipid);
		if ( t_iLip == null ) {
			setErrorMessage("Lipid name is not valid.");
			return null;
		}
		setErrorMessage(null);

		return t_iLip;
	}

	private void initLipidInformations() {
		// Initialize label
		for ( Label t_lbl : this.m_lLblLipidNames )
			t_lbl.setText("-");
		for ( Label t_lbl : this.m_lLblNumC )
			t_lbl.setText("-");
		for ( Label t_lbl : this.m_lLblNumOHs )
			t_lbl.setText("-");
		for ( Label t_lbl : this.m_lLblNumDBs )
			t_lbl.setText("-");
		this.m_lLblNumC.get(0).getParent().layout();
	}

	private void showStructureInformation(ILipid a_iLip) {
		this.initLipidInformations();
		// Extract lipid information and set label
		if ( a_iLip instanceof Ceramide ) {
			this.m_lLblLipidNames.get(0).setText("Sphingosine");
			this.m_lLblLipidNames.get(1).setText("Fatty Acid");
			Ceramide t_cer = (Ceramide)a_iLip;
			this.m_lLblNumC.get(0).setText(   ""+t_cer.getSphingosine().getCarbonLength()           );
			this.m_lLblNumC.get(1).setText(   ""+t_cer.getFattyAcid().getCarbonLength()             );
			this.m_lLblNumOHs.get(0).setText( ""+t_cer.getSphingosine().getNumberOfHydroxylGroups() );
			this.m_lLblNumOHs.get(1).setText( ""+t_cer.getFattyAcid().getNumberOfHydroxylGroups()   );
			this.m_lLblNumDBs.get(0).setText( ""+t_cer.getSphingosine().getNumberOfDoubleBonds()   );
			this.m_lLblNumDBs.get(1).setText( ""+t_cer.getFattyAcid().getNumberOfDoubleBonds()     );
		}
		if ( a_iLip instanceof Sphingosine ) {
			this.m_lLblLipidNames.get(0).setText("Sphingosine");
			Sphingosine t_sph = (Sphingosine)a_iLip;
			this.m_lLblNumC.get(0).setText(   ""+t_sph.getCarbonLength()           );
			this.m_lLblNumOHs.get(0).setText( ""+t_sph.getNumberOfHydroxylGroups() );
			this.m_lLblNumDBs.get(0).setText( ""+t_sph.getNumberOfDoubleBonds()   );
		}
		else if ( a_iLip instanceof FattyAcid ) {
			this.m_lLblLipidNames.get(0).setText("Fatty Acid");
			FattyAcid t_fa = (FattyAcid)a_iLip;
			this.m_lLblNumC.get(0).setText(   ""+t_fa.getCarbonLength()           );
			this.m_lLblNumOHs.get(0).setText( ""+t_fa.getNumberOfHydroxylGroups() );
			this.m_lLblNumDBs.get(0).setText( ""+t_fa.getNumberOfDoubleBonds()   );
		}
		else if ( a_iLip instanceof Lipid ) {
			setErrorMessage("Lipid name is not valid.");
			return;
		}
		this.m_lLblLipidNames.get(0).getParent().layout();
	}

	private void initMassInformation() {
		this.m_txtFormula.setText("");
		this.m_txtMassValue.setText("");
	}

	private void updateLabels() {
		String t_strOLoss = "H2O";
		if ( this.m_btnPerMe.getSelection() )
			t_strOLoss = "MeOH";
		this.m_btnMmH2O.setText("M - "+t_strOLoss);
		this.m_btnMpHmH2O.setText("[M + H - "+t_strOLoss+"]+");
		updateCustomIonLabel();
		this.m_btnMmH2O.getParent().layout();
	}

	private void showCalculatedMasses(ILipid a_iLip) {
		this.initMassInformation();

		ChemicalComposition t_calcMass = ChemicalCompositionProvider.getChemicalComposition(a_iLip);
		if ( this.m_btnPerMe.getSelection() )
			t_calcMass.derivatize();

		// Check ion type
		IonizationType t_ion = null;
		if ( this.m_btnM.getSelection() )
			t_ion = IonizationType.NOION;
		if ( this.m_btnMmH2O.getSelection() )
			t_ion = IonizationType.NOIONMH2O;
		if ( this.m_btnMpH.getSelection() )
			t_ion = IonizationType.PH;
		if ( this.m_btnMmH.getSelection() )
			t_ion = IonizationType.MH;
		if ( this.m_btnMpNa.getSelection() )
			t_ion = IonizationType.PNA;
		if ( this.m_btnMpHmH2O.getSelection() )
			t_ion = IonizationType.PHMH2O;

		// Set text
		if ( t_ion != null ) {
			this.m_txtMassValue.setText( t_calcMass.getMonoisotopicMass(t_ion) );
			this.m_txtFormula.setText( t_calcMass.getFormula(t_ion) );
		} else { // Custom ion
			int nIon = this.m_spnCustomIonCount.getSelection();
			if ( this.m_btnCustomIonNegative.getSelection() )
				nIon *= -1;
			t_calcMass = t_calcMass.getCustomIonizedComposition(
					AtomicMass.forSymbol(this.m_cmbCustomIonType.getText()),
					nIon,
					this.m_btnIonCustomH2O.getSelection()
				);
			this.m_txtMassValue.setText( t_calcMass.getMonoisotopicMass() );
			this.m_txtFormula.setText( t_calcMass.getFormula() );
		}
		
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	// save content of the Text fields because they get disposed
	// as soon as the Dialog closes
	private void saveInput() {
		this.m_strLipidName = m_txtLipidName.getText();

	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public String getLipidName() {
		return this.m_strLipidName;
	}

}
