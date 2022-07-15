package org.grits.toolbox.tools.gsl.wizard.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.widgets.grid.Grid;
//import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
//import org.eclipse.wb.swt.SWTResourceManager;
import org.grits.toolbox.core.utilShare.TextFieldUtils;
import org.grits.toolbox.ms.om.data.AnalyteSettings;
import org.grits.toolbox.ms.om.data.LipidFragment;
import org.grits.toolbox.ms.om.data.LipidFragmentPerActivationMethod;
import org.grits.toolbox.ms.om.data.LipidFragmentPerMSLevel;
import org.grits.toolbox.ms.om.data.LipidSettings;
import org.grits.toolbox.ms.om.data.Method;

/**
 * WizardPage for lipid fragment settings form.
 * @author Masaaki Matsubara
 *
 */
public class LipidFragmentSettingsForm extends WizardPage {
	private Text txtDefMaxNumClvg;
	private Button btnXXX; // TODO: Add fragmentation patterns
	private Grid gridActivation,gridMs;
//	private GridColumn clmnActivationMethod,clmnFragSettings,clmnEnabled;
	private HashMap<String,LipidFragmentPerActivationMethod> fpa = null;
	private HashMap<Integer,LipidFragmentPerMSLevel> fpml = null;
	private List<GridItem> activationGridItems = new ArrayList<GridItem>();
	private List<GridItem> msGridItems = new ArrayList<GridItem>();
	private HashMap<String,GridItem> filter = new HashMap<String,GridItem>();
	private HashMap<Integer,GridItem> filterMsLevel = new HashMap<Integer,GridItem>();
	private boolean readyToFinish = false;
	private Method method;
//	private Label lblNewLabel;
	/**
	 * Create the wizard.
	 * @param method - Method to be filled
	 */
	public LipidFragmentSettingsForm(Method method) {
		super("wizardPage");
		setTitle("Lipid Fragment Settings");
		setDescription("Choose the lipid fragment settings rom different options");
		this.method = method;
	}

	/**
	 * Create contents of the wizard.
	 * @param parent parent Composite
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		container.setLayout(new GridLayout(2, false));

		Label lblDefaultSettings_1 = new Label(container, SWT.NONE);
//		lblDefaultSettings_1.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		lblDefaultSettings_1.setText("Default Settings");
		new Label(container, SWT.NONE);

		this.createFragmentPatternControl(container);
//		this.createFragmentsPerControl(container);

		setControl(container);
		validateInput();
	}

	private void createFragmentPatternControl(Composite container) {
		Label lblMaxNumOf = new Label(container, SWT.NONE);
		lblMaxNumOf.setText("Max Num of Cleavages");

		txtDefMaxNumClvg = new Text(container, SWT.BORDER);
		txtDefMaxNumClvg.setText("2");
		txtDefMaxNumClvg.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if(validateInput()){
					canFlipToNextPage();
					getWizard().getContainer().updateButtons();
				}else{
					readyToFinish = false;
					canFlipToNextPage();
					getWizard().getContainer().updateButtons();
				}
			}
		});

		txtDefMaxNumClvg.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblGlycoCleavages = new Label(container, SWT.NONE);
		lblGlycoCleavages.setText("Lipid Cleavages");

		btnXXX = new Button(container, SWT.CHECK);
		btnXXX.setSelection(true);
		btnXXX.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(validateInput()){
					canFlipToNextPage();
					getWizard().getContainer().updateButtons();
				}
				else{
					readyToFinish = false;
					canFlipToNextPage();
					getWizard().getContainer().updateButtons();
				}
			}
		});

		btnXXX.setText("XXX");
		new Label(container, SWT.NONE);

	}
/*
	private void createFragmentsPerControl(Composite container) {

		Button btnFragmentsPerActivation = new Button(container, SWT.NONE);
		btnFragmentsPerActivation.addSelectionListener(new FragmentSelectionAdapter(this) {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FragmentPerOptionForm dialog = new FragmentPerActivation(getShell(), getParentForm());
				int result = dialog.open();
				if(result == 0) { // nothing to do on close. updates are "live"
//					fpa = dialog.getPerActivation();
//					addToGrid(fpa);
				}
			}			
		});
		btnFragmentsPerActivation.setText("Fragments Per Activation Method");

		gridActivation = new Grid(container, SWT.BORDER);
		gridActivation.setHeaderVisible(true);
		gridActivation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		clmnActivationMethod = new GridColumn(gridActivation, SWT.NONE);
		clmnActivationMethod.setText("Activation Method");
		clmnActivationMethod.setWidth(150);

		clmnFragSettings = new GridColumn(gridActivation, SWT.NONE);
		clmnFragSettings.setText("Fragment Settings");
		clmnFragSettings.setWidth(150);

		clmnEnabled = new GridColumn(gridActivation, SWT.CHECK);
		clmnEnabled.setText("Enabled");
		clmnEnabled.setWidth(150);

		Button btnFragmentsPerMs = new Button(container, SWT.NONE);
		btnFragmentsPerMs.addSelectionListener(new FragmentSelectionAdapter(this) {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FragmentPerOptionForm dialog = new FragmentPerMsLevelForm(getShell(), getParentForm());
				int result = dialog.open();
				if(result == 0) { // nothing to do on close. updates are "live"
//					fpml = dialog.getPerMsLevel();
//					addToMsLevelGrid(fpml);
				}

			}
		});
		btnFragmentsPerMs.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnFragmentsPerMs.setText("Fragments Per Ms Level");

		gridMs = new Grid(container, SWT.BORDER);
		gridMs.setHeaderVisible(true);
		gridMs.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		GridColumn clmnMsLevel = new GridColumn(gridMs, SWT.NONE);
		clmnMsLevel.setText("MS Level");
		clmnMsLevel.setWidth(150);

		GridColumn clmnMsFragSettings = new GridColumn(gridMs, SWT.NONE);
		clmnMsFragSettings.setText("Fragment Settings");
		clmnMsFragSettings.setWidth(150);

		GridColumn clmnMsEnabled = new GridColumn(gridMs, SWT.CHECK);
		clmnMsEnabled.setText("Enabled");
		clmnMsEnabled.setWidth(150);

		setControl(container);

		lblNewLabel = new Label(container, SWT.WRAP);
		GridData gd_lblNewLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_lblNewLabel.widthHint = 567;
		lblNewLabel.setLayoutData(gd_lblNewLabel);
		lblNewLabel.setText("*If more than one fragment settings given, the per-activation settings will overwrite the others if it is given, otherwise the per-MS level settings will be used if it is given, in the other cases the default settings will be used");
		setPageComplete(false);
		if(validateInput()){
			canFlipToNextPage();
			//getWizard().getContainer().updateButtons();
		}else{
			readyToFinish = false;
			canFlipToNextPage();
			//getWizard().getContainer().updateButtons();
		}
		
		fpa = new HashMap<>();
		fpml = new HashMap<>();		
	}
*/
	public boolean canFlipToNextPage(){
		if(readyToFinish){
//			return ! wizard.getGlycanSettingsForm().useMetaInfoControls();
			return true;
		}
		else
			return false;
	}
 
	public boolean canFinish() {
		return readyToFinish;
	}

	/**
	 * Adds contents of the given LipidFragmentPerActivationMethod to the table
	 * @param method LipidFragmentPerActivationMethod
	 * @return pass/fail
	 */
	public boolean addToGrid(LipidFragmentPerActivationMethod method){
		if(filter.get(method.getActivationMethod())==null){
			GridItem item = new GridItem(gridActivation, SWT.NONE);
			item.setText(0, method.getActivationMethod());
			StringBuilder builder = new StringBuilder();
			builder.append("max clvg: " + method.getMaxNumOfCleavages()+",");
			builder.append("fragment types: ");
			for(LipidFragment f : method.getFragments()){
				builder.append(f.getType()+" ");
			}
			item.setText(1, builder.toString());
			item.setChecked(2,true);
			activationGridItems.add(item);	
			filter.put(method.getActivationMethod(),item);
		}else{
			int index=0;
			for(int i = 0; i < gridActivation.getItems().length;i++)
				if(gridActivation.getItem(i).getText(0).trim().equals(method.getActivationMethod()))
					index = i;
			gridActivation.remove(index);
			GridItem item = new GridItem(gridActivation, SWT.NONE);
			item.setText(0, method.getActivationMethod());
			StringBuilder builder = new StringBuilder();
			builder.append("max clvg: " + method.getMaxNumOfCleavages()+",");
			builder.append("fragment types: ");
			for(LipidFragment f : method.getFragments()){
				builder.append(f.getType()+" ");
			}
			item.setText(1, builder.toString());
			item.setChecked(2, true);
			activationGridItems.add(item);	
			filter.put(method.getActivationMethod(),item);

		}
		fpa.put(method.getActivationMethod(), method);
		return true;
	}

	/**
	 * Adds contents of the given LipidFragmentPerMSLevel to the table
	 * @param method LipidFragmentPerMSLevel
	 * @return pass/fail
	 */
	public boolean addToMsLevelGrid(LipidFragmentPerMSLevel method){
		if(filterMsLevel.get(method.getMsLevel())==null){
			GridItem item = new GridItem(gridMs, SWT.NONE);
			item.setText(0, ""+method.getMsLevel());
			StringBuilder builder = new StringBuilder();
			builder.append("max clvg: " + method.getM_maxNumOfCleavages()+",");
			builder.append("fragment types: ");
			for(LipidFragment f : method.getFragments()){
				builder.append(f.getType()+" ");
			}
			item.setText(1, builder.toString());
			item.setChecked(2,true);
			msGridItems.add(item);	
			filterMsLevel.put(method.getMsLevel(),item);
		}else{
			int index=0;
			for(int i = 0; i < gridMs.getItems().length;i++)
				if(gridMs.getItem(i).getText(0).trim().equals(""+method.getMsLevel()))
					index = i;
			gridMs.remove(index);
			GridItem item = new GridItem(gridMs, SWT.NONE);
			item.setText(0, ""+method.getMsLevel());
			StringBuilder builder = new StringBuilder();
			builder.append("max clvg: " + method.getM_maxNumOfCleavages()+",");
			builder.append("fragment types: ");
			for(LipidFragment f : method.getFragments()){
				builder.append(f.getType()+" ");
			}
			item.setText(1, builder.toString());
			item.setChecked(2,true);
			activationGridItems.add(item);	
			filterMsLevel.put(method.getMsLevel(),item);
		}
		fpml.put(method.getMsLevel(), method);
		return true;
	}

//	@Override
//	public IWizardPage getNextPage() {
//		save();
//		return super.getNextPage();
//	}

	/**
	 * Save lipid fragment setting information to LipidSettings
	 */
	public void save(){
		//Default Settings
		for ( AnalyteSettings t_aSettings : method.getAnalyteSettings() ) {
			if ( t_aSettings.getLipidSettings() == null )
				continue;

			LipidSettings t_lSettings = t_aSettings.getLipidSettings();
			
			t_lSettings.setMaxNumOfCleavages(Integer.parseInt(txtDefMaxNumClvg.getText()));
			List<LipidFragment> fragments = new ArrayList<>();
			LipidFragment f = null;
			if(btnXXX.getSelection()){
				f = new LipidFragment();
				f.setNumber(LipidFragment.UNKNOWN);
				f.setType(LipidFragment.TYPE_XXX);
				fragments.add(f);
			}
			t_lSettings.setLipidFragments(fragments);
			//Per Activation
			t_lSettings.getLipidFragmentsPerActivationMethod().clear();
			if(fpa != null && ! fpa.isEmpty()){
				for(GridItem item : activationGridItems){
					if(! item.isDisposed() && item.getChecked(2)){
						LipidFragmentPerActivationMethod activationMethodFragments = fpa.get(item.getText(0).trim());
						t_lSettings.getLipidFragmentsPerActivationMethod().add(activationMethodFragments);
					}//if item

				}//for GridItem
			}//if fpa
			t_lSettings.getLipidFragmentsPerMSLevel().clear();
			if(fpml != null && ! fpml.isEmpty()){
				for(GridItem item : msGridItems){
					if( ! item.isDisposed() && item.getChecked(2)){
						LipidFragmentPerMSLevel msLevelFragments = fpml.get(Integer.parseInt(item.getText(0).trim()));
						t_lSettings.getLipidFragmentsPerMSLevel().add(msLevelFragments);
					}//if item

				}//for GridItem
			}//if fpml
		}
	}

	/**
	 * Validates input.
	 * @return pass/fail
	 */
	public boolean validateInput() {
		if( ! TextFieldUtils.isNonZero(txtDefMaxNumClvg) ) {
			setErrorMessage("Please enter a valid number");
			return false;
		}

		if( ! btnXXX.getSelection() ) {
			setErrorMessage("Please select at least one lipid cleavage type");
			return false;
		}

		if( ! TextFieldUtils.isNonZero(txtDefMaxNumClvg) && btnXXX.getSelection() ) {
			setErrorMessage("Please enter Max Cleavge value ");
			return false;
		}

		setErrorMessage(null);
		readyToFinish = true;
		return true;

	}
	
	class FragmentSelectionAdapter extends SelectionAdapter {
		LipidFragmentSettingsForm parentForm = null;
		public FragmentSelectionAdapter( LipidFragmentSettingsForm parentForm ) {
			this.parentForm = parentForm;
		}
		
		public LipidFragmentSettingsForm getParentForm() {
			return parentForm;
		}
	}
}
