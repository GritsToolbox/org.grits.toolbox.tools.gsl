package org.grits.toolbox.tools.gsl.wizard.lipidgeneration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

/**
 * WizardPage for data entry page of lipid database generator wizard.
 * @author Masaaki Matsubara
 *
 */
public class DataEntryPage extends WizardPage {
	private static final Logger logger = Logger.getLogger(LipidGenerationSettingPage.class);
	
	private Text m_textTitle;
	private Text m_textFileName;
	private StyledText m_textDescription;
	private Text m_textVersion;
	private Text m_textCreatorName;
	private Text m_textCreatorInstitution;

	/**
	 * Create the wizard.
	 */
	public DataEntryPage() {
		super("Lipid Database Generation");
		setTitle("Lipid Database Generation");
		setDescription("Generate lipid database for identifying lipids in Glycosphingolipid.");
	}

	public String getDatabaseTitle() {
		return this.m_textTitle.getText().trim();
	}

	public String getOutputFileName() {
		return this.m_textFileName.getText().trim();
	}

	public String getDatabaseDescription() {
		return this.m_textDescription.getText().trim();
	}

	public String getVersion() {
		return this.m_textVersion.getText().trim();
	}

	public String getCreatorName() {
		return this.m_textCreatorName.getText().trim();
	}

	public String getCreatorInstitution() {
		return this.m_textCreatorInstitution.getText().trim();
	}

	/**
	 * Create contents of the wizard.
	 * @param parent parent Composite
	 */
	public void createControl(Composite parent) {
		// Start 
		logger.debug("BEGIN DataEntryPage");

		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(3, false));
		
		// Output file name
		Label lblOutputFileName = new Label(container, SWT.NONE);
		lblOutputFileName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblOutputFileName.setText("Output File Name");
		
		this.m_textFileName = new Text(container, SWT.BORDER);
		this.m_textFileName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button btnBrowse = new Button(container, SWT.NONE);
		btnBrowse.setText("Browse");
		GridData gdButton = new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1);
		gdButton.widthHint = 100;
		btnBrowse.setLayoutData(gdButton);
		
		// Button action
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				// Open file dialog
				FileDialog dlgSave = new FileDialog(parent.getShell(), SWT.SAVE);
				// Create file extension filters
				dlgSave.setFilterNames(new String[] { "XML database (*.xml)", "All files (*.*)" });
				dlgSave.setFilterExtensions(new String[] { "*.xml", "*.*" });
				// Show overwrite confirmation
				dlgSave.setOverwrite(true);
				dlgSave.open();
				// get the selected path the selected files from the dialog
				// if the cancel button is pressed the path will be "" or
				// null and the file name will be empty
				String strFilePath = dlgSave.getFilterPath();
				String strFileName = dlgSave.getFileName();
				if ( strFileName.length() == 0 || strFilePath == null )
					return;
				strFileName = strFilePath+File.separator+strFileName;
				m_textFileName.setText(strFileName);
			}
		});

		// Lipid database name
		Label lblLipidDatabaseName = new Label(container, SWT.NONE);
		lblLipidDatabaseName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLipidDatabaseName.setText("Lipid Database Title");
		
		this.m_textTitle = this.makeText(container);

		// Description
		Label lblDescription = new Label(container, SWT.NONE);
		lblDescription.setText("Description");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		this.m_textDescription = new StyledText(container, SWT.BORDER);
		this.m_textDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		
		// Version
		Label lblVersion = new Label(container, SWT.NONE);
		lblVersion.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblVersion.setText("Version");
		
		this.m_textVersion = this.makeText(container);
		this.m_textVersion.setText("1.0");

		// Contributer Name
		Label lblContributerName = new Label(container, SWT.NONE);
		lblContributerName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblContributerName.setText("Creator Name");

		this.m_textCreatorName = this.makeText(container);

		// Contributer Institution
		Label lblContributerInstitution = new Label(container, SWT.NONE);
		lblContributerInstitution.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblContributerInstitution.setText("Creator Institution");

		this.m_textCreatorInstitution = this.makeText(container);

		setPageComplete(false);

		// Set page complete information to required Texts' listener
		Map<Text, String>  t_mapRequiredTextToErrorMessage = new HashMap<>();
		t_mapRequiredTextToErrorMessage.put(this.m_textFileName, "File name is required.");
		t_mapRequiredTextToErrorMessage.put(this.m_textTitle, "Database title is required.");
		t_mapRequiredTextToErrorMessage.put(this.m_textVersion, "Version is required.");
		this.setRequieredTexts(t_mapRequiredTextToErrorMessage);

		logger.debug("END DataEntryPage");
	}

	private Text makeText(Composite cmp) {
		Text txt = new Text(cmp, SWT.BORDER);
		txt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		return txt;
	}

	private void setRequieredTexts(Map<Text, String> a_mapRequiredTextToErrorMessage) {

		for ( Text t_textRequired : a_mapRequiredTextToErrorMessage.keySet() ) {
			t_textRequired.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					for ( Text t_textRequiredMod : a_mapRequiredTextToErrorMessage.keySet() ) {
						if ( !t_textRequiredMod.getText().trim().isEmpty() )
							continue;
						setErrorMessage(a_mapRequiredTextToErrorMessage.get(t_textRequiredMod));
						setPageComplete(false);
						return;
					}
					setErrorMessage(null);
					setPageComplete(true);
				}

			});
			t_textRequired.addListener(SWT.Traverse, new Listener() {

				@Override
				public void handleEvent(Event event) {
					for ( Text t_textRequiredMod : a_mapRequiredTextToErrorMessage.keySet() ) {
						if ( !t_textRequiredMod.getText().trim().isEmpty() )
							continue;
						setErrorMessage(a_mapRequiredTextToErrorMessage.get(t_textRequiredMod));
						setPageComplete(false);
						return;
					}
					setErrorMessage(null);
					setPageComplete(true);
				}
			});
		}

	}
}
