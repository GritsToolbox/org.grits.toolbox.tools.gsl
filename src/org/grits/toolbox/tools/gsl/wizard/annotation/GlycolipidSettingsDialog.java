package org.grits.toolbox.tools.gsl.wizard.annotation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.grits.toolbox.core.datamodel.UnsupportedVersionException;
import org.grits.toolbox.core.utilShare.FileSelectionAdapter;
import org.grits.toolbox.core.utilShare.TextFieldUtils;
import org.grits.toolbox.entry.ms.annotation.glycan.preference.FilterSettingLibrary;
import org.grits.toolbox.entry.ms.annotation.glycan.preference.MSGlycanFilterCateogoryPreference;
import org.grits.toolbox.entry.ms.annotation.glycan.preference.MSGlycanFilterPreference;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.utils.DatabaseUtils;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.utils.GlycanStructureDatabase;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.utils.GlycanStructureDatabaseIndex;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.Filtering;
import org.grits.toolbox.ms.annotation.structure.GlycanPreDefinedOptions;
import org.grits.toolbox.ms.annotation.structure.StructureHandlerFileSystem;
import org.grits.toolbox.ms.om.data.AnalyteSettings;
import org.grits.toolbox.ms.om.data.GlycanFilter;
import org.grits.toolbox.ms.om.data.GlycanSettings;
import org.grits.toolbox.ms.om.data.LipidDatabase;
import org.grits.toolbox.ms.om.data.LipidSettings;
import org.grits.toolbox.tools.gsl.database.LipidDatabaseIndex;
import org.grits.toolbox.tools.gsl.database.LipidDatabaseList;
import org.grits.toolbox.tools.gsl.util.DatabaseUtilsForGSL;
import org.grits.toolbox.tools.gsl.util.io.lipid.LipidDatabaseFileHandler;
import org.grits.toolbox.util.structure.glycan.filter.om.Category;
import org.grits.toolbox.util.structure.glycan.filter.om.FilterSetting;
import org.grits.toolbox.util.structure.glycan.filter.om.FiltersLibrary;

/**
 * TitleAreaDialog class for creating AnalyteSettings for glycolipid
 * @author Masaaki Matsubara
 *
 */
public class GlycolipidSettingsDialog extends TitleAreaDialog {

	private static final Logger logger = Logger.getLogger(GlycolipidSettingsDialog.class);

	private final static String GLYCAN = "Glycan";
	private final static String LIPID = "Lipid";
	private final static String NONE = "None";

	private AnalyteSettings analSettings;

	private FilterSetting filterSetting;
	private Filtering filtering;
	private FilterSettingLibrary preferenceFilterLibrary;
	private Category preferredFilterCategory;

	private String m_strGDBVersion;
	private String m_strDBPath;

	private HashMap<String, GlycanStructureDatabase> m_mapGlycanDBIndex = new HashMap<String, GlycanStructureDatabase>();
	private HashMap<String, LipidDatabaseIndex> m_mapLipidDBIndex = new HashMap<String, LipidDatabaseIndex>();

	private Combo m_cmbGlycanDBs;
	private Text m_txtNewGlycanDB;
	private Combo m_cmbLipidDBs;
	private Text m_txtNewLipidDB;
	// TODO: Check use of database structure meta info
//	private Button m_chkUseDatabaseMetaInfo;
	private Combo m_cmbPerDeriv;

	private boolean m_bEditingMode;

	/**
	 * Constructor for adding mode (create a new AnalyteSettings)
	 * @param parentShell parent Shell
	 * @param filterLibrary FiltersLibrary of the glycan database for GlycanSettings
	 */
	public GlycolipidSettingsDialog(Shell parentShell, FiltersLibrary filterLibrary) {
		super(parentShell);

		this.analSettings = new AnalyteSettings();
		this.analSettings.setGlycanSettings(new GlycanSettings());
//		this.analSettings.setLipidSettings(new LipidSettings());

		loadFilterPreferences();
		this.filtering = new Filtering(getShell(), filterLibrary, preferredFilterCategory, null);
	}
	

	/**
	 * Constructor for editing mode (modify the given AnalyteSettings)
	 * @param parentShell parent Shell
	 * @param filterLibrary FiltersLibrary of the glycan database for GlycanSettings
	 * @param aSettings AnalyteSettings to be modified
	 */
	public GlycolipidSettingsDialog(Shell parentShell, FiltersLibrary filterLibrary, AnalyteSettings aSettings) {
		super(parentShell);

		this.analSettings = aSettings;
		if ( this.analSettings.getGlycanSettings() == null )
			aSettings.setGlycanSettings(new GlycanSettings());
//		if ( this.analSettings.getLipidSettings() == null )
//			aSettings.setLipidSettings(new LipidSettings());

		loadFilterPreferences();
		this.filtering = new Filtering(getShell(), filterLibrary, preferredFilterCategory, null);

		this.m_bEditingMode = true;
	}

	public AnalyteSettings getAnalyteSettings() {
		return this.analSettings;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}
	
	private void loadFilterPreferences() {
		try {
			MSGlycanFilterPreference preferences = MSGlycanFilterPreference.getMSGlycanFilterPreferences (
					MSGlycanFilterPreference.getPreferenceEntity());
			if (preferences != null) 
				this.preferenceFilterLibrary = preferences.getFilterSettings();
			MSGlycanFilterCateogoryPreference categoryPreferences = MSGlycanFilterCateogoryPreference.getMSGlycanFilterCategoryPreferences (
					MSGlycanFilterCateogoryPreference.getPreferenceEntity());
			if (categoryPreferences != null) 
				preferredFilterCategory = categoryPreferences.getCategoryPreference();
		} catch (UnsupportedVersionException e) {
			logger.error("Cannot load filter preference");
		}
	}

	protected Label setMandatoryLabel(Label lable) {
		lable.setText(lable.getText() + "*");
		lable.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		return lable;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Glycolipid Database Settings");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(4, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		addDatabaseControls(container, GLYCAN);
		addSelectGlycanFilterItem(container);
		addDatabaseControls(container, LIPID);
		// TODO: Check use of database structure meta info
//		addUseMetaInfoControls(container);
		addPerDerivTypeControls(container);

		return container;
	}

	/**
	 * Add database controls for the given database type
	 * @param container parent Composite
	 * @param a_strDBType String of the DB type ({@link #GLYCAN} or {@link #LIPID})
	 */
	protected void addDatabaseControls(Composite container, String a_strDBType) {
		// Add DB Label
		Label lblDatabase = new Label(container, SWT.NONE);
		String t_strDBLabel = "Database";
		if ( a_strDBType != null )
			t_strDBLabel = a_strDBType+" "+t_strDBLabel;
		lblDatabase.setText(t_strDBLabel);
		lblDatabase = setMandatoryLabel(lblDatabase);

		// Add DB Combo
		Combo cmbDatabases = new Combo(container, SWT.NONE);

		GridData gd_cmbDatabases = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		gd_cmbDatabases.widthHint = 131;
		cmbDatabases.setLayoutData(gd_cmbDatabases);

		// Blank
		new Label(container, SWT.NONE);

		// Add DB name Text and browse Button when specified "Other"
		Text txtNewDb = new Text(container, SWT.BORDER);

		txtNewDb.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		Button btnBrowse = new Button(container, SWT.PUSH);
		btnBrowse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnBrowse.setText("Browse");
		FileSelectionAdapter rawFileBrowserSelectionAdapter = new FileSelectionAdapter();
		rawFileBrowserSelectionAdapter.setShell(container.getShell());
		rawFileBrowserSelectionAdapter.setText(txtNewDb);
		btnBrowse.addSelectionListener(rawFileBrowserSelectionAdapter);


		// Set database items
		setDatabaseItems(cmbDatabases, txtNewDb, a_strDBType);
		if( ! cmbDatabases.getItem(cmbDatabases.getSelectionIndex()).equals(GlycanPreDefinedOptions.OTHER) ) {
			btnBrowse.setEnabled(false);
			txtNewDb.setEnabled(false);
		} 
		if ( m_bEditingMode )
			setDefaultDatabaseItems(cmbDatabases, txtNewDb, a_strDBType);

		// Set listners
		cmbDatabases.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (cmbDatabases.getItem(cmbDatabases.getSelectionIndex()).equals("other")) {
					txtNewDb.setEnabled(true);
					btnBrowse.setEnabled(true);
					getButton(OK).setEnabled(validateInput());
				} else {
					txtNewDb.setEnabled(false);
					btnBrowse.setEnabled(false);
					getButton(OK).setEnabled(validateInput());
				}
			}

		});

		txtNewDb.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				getButton(OK).setEnabled(validateInput());
			}

		});

		if ( a_strDBType.equals(GLYCAN) ) {
			this.m_cmbGlycanDBs = cmbDatabases;
			this.m_txtNewGlycanDB = txtNewDb;
		}
		if ( a_strDBType.equals(LIPID) ) {
			this.m_cmbLipidDBs = cmbDatabases;
			this.m_txtNewLipidDB = txtNewDb;
		}
	}

	private void setDatabaseItems(Combo a_cmbDB, Text a_txtNewDB, String a_strDBType) {
		List<String> t_lDBs = this.loadDatabases(a_strDBType);
		t_lDBs.add(GlycanPreDefinedOptions.OTHER);

		String[] cmbDBs = t_lDBs.toArray(new String[t_lDBs.size()]);
		int inx = 0;

		a_cmbDB.setItems(cmbDBs);
		a_cmbDB.select(inx);
	}

	protected List<String> loadDatabases(String a_strDBType) {
		if ( a_strDBType.equals(GLYCAN) )
			return this.loadGlycanDatabases();
		if ( a_strDBType.equals(LIPID) )
			return this.loadLipidDatabases();
		return new ArrayList<>();
	}

	private List<String> loadGlycanDatabases() {
		List<String> t_lGlycanDatabases = new ArrayList<>();
		try {
			GlycanStructureDatabaseIndex t_databaseIndex = DatabaseUtilsForGSL.getGelatoDatabases();
			for (GlycanStructureDatabase t_db : t_databaseIndex.getDatabase()) {
				String t_nameString = t_db.getName() + " - " + t_db.getNumberOfStructures().toString() + " glycans";
				t_lGlycanDatabases.add(t_nameString);
				this.m_mapGlycanDBIndex.put(t_nameString, t_db);
			}
		} catch (IOException e) {
			logger.error("Unable to find GELATO database index", e);
		} catch (JAXBException e) {
			logger.error("XML format problem in GELATO database index", e);
		}
		return t_lGlycanDatabases;
	}

	private List<String> loadLipidDatabases() {
		List<String> t_lLipidDatabases = new ArrayList<>();
		try {
			LipidDatabaseList t_databaseList = DatabaseUtilsForGSL.getLipidDatabases();
			for ( LipidDatabaseIndex t_dbIndex : t_databaseList.getIndices() ) {
				String t_nameString = t_dbIndex.getName() + " - " + t_dbIndex.getNumberOfStructures().toString() + " lipids";
				t_lLipidDatabases.add(t_nameString);
				this.m_mapLipidDBIndex.put(t_nameString, t_dbIndex);
			}
			// Add "none" for annotating intact glycan (not glycolipid)
			t_lLipidDatabases.add(NONE+" (Glycans will be annotated)");
			this.m_strDBPath = DatabaseUtilsForGSL.getDatabasePath();
		}
		catch (IOException e) {
			logger.error("Unable to find lipid database list", e);
		}
		catch (JAXBException e) {
			logger.error("XML format problem in lipid database list", e);
		}
		return t_lLipidDatabases;
	}

	private void setDefaultDatabaseItems(Combo a_cmbDB, Text a_txtNewDB, String a_strDBType) {
		// Set default DB name (and URI) if editing mode
		String[] strDBInfo = this.getDBNameAndURI(a_strDBType);
		String strDBName = strDBInfo[0];
		String strDBURI = strDBInfo[1];

		if (strDBName == null) {
			// Select the last option "OTHER" if no DB name is specified
			if ( strDBURI != null ) {
				a_cmbDB.select(a_cmbDB.getItemCount()-1);
				a_txtNewDB.setText(strDBURI);
			}
			// Select the second option from last "NONE" if no DB name and URI are specified
			else {
				a_cmbDB.select(a_cmbDB.getItemCount()-2);
			}
			return;
		}

		int selected = -1;
		int i=0;
		for (String item: a_cmbDB.getItems()) {
			if (strDBName.equals(item)) {
				selected = i;
				break;
			}
			i++;
		}
		if (selected != -1)
			a_cmbDB.select(selected);
	}

	/**
	 * Get database name and URI from a glycan or lipid database
	 * @param a_strDBType String of the DB type ({@link #GLYCAN} or {@link #LIPID})
	 * @return String pair of [0] database name and [1] database URI
	 */
	protected String[] getDBNameAndURI(String a_strDBType) {
		if ( a_strDBType.equals(GLYCAN) && this.getAnalyteSettings().getGlycanSettings() != null )
			return this.getGlycanDBNameAndURI();
		if ( a_strDBType.equals(LIPID) && this.getAnalyteSettings().getLipidSettings() != null )
			return this.getLipidDBNameAndURI();
		return new String[2];
	}

	private String[] getGlycanDBNameAndURI() {
		GlycanFilter filter = this.analSettings.getGlycanSettings().getFilter();
		if (filter == null || filter.getDatabase() == null)
			return new String[2];

		String dbNameString= null;
		for (GlycanStructureDatabase t_db: this.m_mapGlycanDBIndex.values()) {
			if (t_db.getPath().contains(filter.getDatabase())) {
				dbNameString = t_db.getName() + " - " + t_db.getNumberOfStructures().toString() + " glycans";
				break;
			}
		}
		return new String[]{dbNameString, filter.getDatabase()};
	}

	private String[] getLipidDBNameAndURI() {
		LipidDatabase lDB = this.analSettings.getLipidSettings().getDatabase();
		if ( lDB == null || lDB.getDatabase() == null )
			return new String[2];

		String strDBName = null;
		for ( String t_strLDBName : this.m_mapLipidDBIndex.keySet() ) {
			LipidDatabaseIndex t_lDBInx = this.m_mapLipidDBIndex.get(t_strLDBName);
			if ( !lDB.getURI().contains( t_lDBInx.getFileName() ) )
				continue;
			strDBName = t_strLDBName;
			break;
		}

		return new String[]{strDBName, lDB.getURI()};
	}
/* 
	// TODO: Check use of database structure meta info
	private void addUseMetaInfoControls(Composite container) {
		// Blank
		new Label(container, SWT.NONE);

		m_chkUseDatabaseMetaInfo = new Button(container, SWT.CHECK);
		m_chkUseDatabaseMetaInfo.setText("Use specified structure settings from database.");
		m_chkUseDatabaseMetaInfo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		if (m_bEditingMode)
			m_chkUseDatabaseMetaInfo.setSelection(this.analSettings.getGlycanSettings().getFilter().getUseDatabaseStructureMetaInfo());

		m_chkUseDatabaseMetaInfo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (m_cmbPerDeriv != null)
					m_cmbPerDeriv.setEnabled(!m_chkUseDatabaseMetaInfo.getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

		});

		// Blank
		new Label(container, SWT.NONE);
	}
*/
	private void addSelectGlycanFilterItem( Composite parent ) {
		GridData gd1 = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		Label lblSelectFilter = new Label(parent, SWT.NONE);
		lblSelectFilter.setText("Glycan Database Filter");
		lblSelectFilter.setLayoutData(gd1);

		GridData gd2 = new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1);
		Combo cmbSelectFilter = new Combo(parent, SWT.READ_ONLY);
		cmbSelectFilter.setLayoutData(gd2);
		initStoredFiltersList(cmbSelectFilter);
		
		if (m_bEditingMode) {
			GlycanSettings gSettings = this.analSettings.getGlycanSettings();
			if (gSettings.getFilterSetting() != null && gSettings.getFilterSetting().getName() != null) {
				// named filter, select from the combo
				int selected = -1;
				int i=0;
				for (String filter: cmbSelectFilter.getItems()) {
					if (gSettings.getFilterSetting().getName().equals(filter)) {
						selected = i;
						break;
					}
					i++;
				}
				if (selected != -1)
					cmbSelectFilter.select(selected);
			} else if (gSettings.getFilterSetting() != null) {
				// OTHER
				cmbSelectFilter.select(cmbSelectFilter.getItemCount()-1);
			}
		}
		
		cmbSelectFilter.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				processSelection(cmbSelectFilter);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	
	private void initStoredFiltersList(Combo cmbSelectFilter) {
		cmbSelectFilter.removeAll();
		if ( preferenceFilterLibrary != null && preferenceFilterLibrary.getFilterSettings() != null ) {
			for (FilterSetting filter : preferenceFilterLibrary.getFilterSettings()) {
				cmbSelectFilter.add(filter.getName());
			}
		}
		cmbSelectFilter.add(GlycanPreDefinedOptions.OTHER);
		cmbSelectFilter.add("", 0);
	}

	private void processSelection(Combo cmbSelectFilter) {
		if(cmbSelectFilter.getText().trim().equals("") ) {
			filterSetting = null;
			return;
		}

		if (!cmbSelectFilter.getText().equals(GlycanPreDefinedOptions.OTHER)) {
			filterSetting = getCurrentFilter(cmbSelectFilter.getText().trim());
			return;
		}
		// Open up filter dialog
		if (filtering != null && filtering.open() == Window.OK) {
			filterSetting = filtering.getFilterSetting();
			filtering.setFilterSetting (filterSetting);
		}
	}

	private FilterSetting getCurrentFilter( String selFilter ) {
		if (preferenceFilterLibrary == null || preferenceFilterLibrary.getFilterSettings() == null)
			return null;

		for( int i = 0; i < preferenceFilterLibrary.getFilterSettings().size(); i++ ) {
			FilterSetting curFilter =  preferenceFilterLibrary.getFilterSettings().get(i);
			if( curFilter.getName().equals(selFilter) )
				return curFilter;
		}

		return null;
	}

	private void addPerDerivTypeControls(Composite container) {
		Label lblPerdrivType = new Label(container, SWT.NONE);
		lblPerdrivType.setText("PerDeriv Type");
		lblPerdrivType = setMandatoryLabel(lblPerdrivType);

		m_cmbPerDeriv = new Combo(container, SWT.NONE);
		m_cmbPerDeriv.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));

		m_cmbPerDeriv.setItems(GlycanPreDefinedOptions.getAllDerivitizationTypes());
		m_cmbPerDeriv.select(0);

		if (m_bEditingMode) {
			int selected = -1;
			int i=0;
			for (String item: m_cmbPerDeriv.getItems()) {
				if (this.analSettings.getGlycanSettings().getPerDerivatisationType().equals(item)) {
					selected = i;
					break;
				}
				i++;
			}
			if (selected != -1) m_cmbPerDeriv.select(selected);
		}
	}

	/**
	 * Validate inputed database information. Mainly, this check the location of the selected database is correct.
	 * Error message is also turn on if there is an error.
	 * @return True if validated
	 */
	public boolean validateInput() {
		if (this.m_txtNewGlycanDB != null && this.m_txtNewGlycanDB.getEnabled()) {
			if (TextFieldUtils.isEmpty(this.m_txtNewGlycanDB) || !isValidGlycanDatabase(this.m_txtNewGlycanDB.getText())) {
				setErrorMessage("Please select a valid glycan database");
				return false;
			}
		}
		if (this.m_txtNewLipidDB != null && this.m_txtNewLipidDB.getEnabled()) {
			if (TextFieldUtils.isEmpty(this.m_txtNewLipidDB) || !isValidLipidDatabase(this.m_txtNewLipidDB.getText())) {
				setErrorMessage("Please select a valid lipid database");
				return false;
			}
		}

		setErrorMessage(null);
		return true;

	}

	private boolean isValidGlycanDatabase(String dbName) {
		try {
			GlycanFilter filter = new GlycanFilter();
			if (dbName.indexOf(File.separator) == -1) {  // does not have the full path
				try {
					filter.setDatabase(DatabaseUtils.getDatabasePath() + File.separator + dbName);
				} catch (IOException e) {
					logger.error("Database path cannot be determined", e);
				}
			} else 
				filter.setDatabase(dbName);   // if "other" is selected, the dbName will contain the full path to the file
			StructureHandlerFileSystem handler = new StructureHandlerFileSystem();
			if (handler.getStructures(filter) == null)
				return false;
			else {
				// get the version
				m_strGDBVersion  = filter.getVersion();
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}

	private boolean isValidLipidDatabase(String a_strLDBFileName) {
		try {
			LipidDatabaseFileHandler.importXML(a_strLDBFileName);
		} catch (Exception e) {
			logger.info("Unable to read lipid database file "+a_strLDBFileName);
			return false;
		}
		return true;
	}

	@Override
	protected void okPressed() {
		if (!validateInput())
			return;
		saveGlycanSettings();
		saveLipidSettings();
		super.okPressed();
	}

	private void saveGlycanSettings() {
		String sSelectedDb = this.m_cmbGlycanDBs.getText();
		String sSelectedPerDerivType = m_cmbPerDeriv.getText();
		this.analSettings.getGlycanSettings().setPerDerivatisationType(sSelectedPerDerivType);

		GlycanStructureDatabase selectedDatabase = this.m_mapGlycanDBIndex.get(sSelectedDb);

		GlycanFilter filter = new GlycanFilter();

		if (selectedDatabase == null) { // other
			filter.setDatabase(this.m_txtNewGlycanDB.getText());
		} else {
			filter.setDatabase(selectedDatabase.getFileName());
		}
		// TODO: Check use of database structure meta info
//		filter.setUseDatabaseStructureMetaInfo(m_chkUseDatabaseMetaInfo.getSelection());
		filter.setVersion(m_strGDBVersion != null ? m_strGDBVersion : "1.0");
		this.analSettings.getGlycanSettings().setFilter(filter);

		// Save filter settings
		this.analSettings.getGlycanSettings().setFilterSetting(filterSetting);
	}

	private void saveLipidSettings() {
		String sSelectedDb = this.m_cmbLipidDBs.getText();
		// Just set null for LipidSettings if "None" is specified
		if ( sSelectedDb.startsWith(NONE) ) {
			this.analSettings.setLipidSettings(null);
			return;
		}

		if ( this.analSettings.getLipidSettings() == null )
			this.analSettings.setLipidSettings(new LipidSettings());

		String sSelectedPerDerivType = m_cmbPerDeriv.getText();

		this.analSettings.getLipidSettings().setPerDerivatisationType(sSelectedPerDerivType);

		LipidDatabaseIndex t_lDBInx = this.m_mapLipidDBIndex.get(sSelectedDb);

		LipidDatabase t_lDB = new LipidDatabase();
		if ( t_lDBInx == null ) { // Other
			t_lDB.setDatabase("");
			t_lDB.setURI( this.m_txtNewLipidDB.getText() );
		} else {
			t_lDB.setDatabase( t_lDBInx.getName() );
			t_lDB.setURI( m_strDBPath + File.separator + t_lDBInx.getFileName() );
		}
		this.analSettings.getLipidSettings().setDatabase(t_lDB);

	}

}
