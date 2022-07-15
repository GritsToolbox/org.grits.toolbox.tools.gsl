package org.grits.toolbox.tools.gsl.wizard.annotation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.grits.toolbox.ms.om.data.AnalyteSettings;
import org.grits.toolbox.ms.om.data.GlycanSettings;
import org.grits.toolbox.ms.om.data.LipidSettings;

/**
 * Composite class for glycolipid database settings table
 * @author Masaaki Matsubara
 *
 */
public class GlycolipidDatabaseSettingsTableComposite extends Composite {
	
//	private static final Logger logger = Logger.getLogger(GlycolipidDatabaseSettingsTableComposite.class);
	
	List<AnalyteSettings> analyteSettings = new ArrayList<>();
	TableViewer settingsTableViewer;

	public GlycolipidDatabaseSettingsTableComposite(Composite parent, int style) {
		super(parent, style);
	}
	
	/**
	 * sets the anaylteSettings to be displayed in the table
	 * 
	 * @param analyteSettings list of analyteSettings
	 */
	public void setAnalyteSettings(List<AnalyteSettings> analyteSettings) {
		this.analyteSettings = analyteSettings;
		if (settingsTableViewer != null) {
			settingsTableViewer.setInput(analyteSettings);
			settingsTableViewer.refresh();
		}
	}
	
	/**
	 * return the analyteSettings in the table
	 * 
	 * @return the list of analyte settings in the table
	 */
	public List<AnalyteSettings> getAnalyteSettings() {
		return analyteSettings;
	}
	
	public TableViewer getSettingsTableViewer() {
		return settingsTableViewer;
	}

	/**
	 * adds the table viewer to the composite
	 */
	public void createTable() {	
		GridLayout layout = new GridLayout(1, true);
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		this.setLayout(layout);

		settingsTableViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION);
		Table settingsTable = settingsTableViewer.getTable();
		GridData gd_table_2 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_table_2.heightHint = 80;
		settingsTable.setLayoutData(gd_table_2);
		settingsTable.setHeaderVisible(true);
		settingsTable.setLinesVisible(true);

		// Add column for glycan database name
		addTableViewerColumn( settingsTableViewer, "Glycan Database Name", 200,
				new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if ( !(element instanceof AnalyteSettings) )
					return "";

				GlycanSettings gSettings = ((AnalyteSettings) element).getGlycanSettings();
				if ( gSettings == null || gSettings.getFilter() == null )
					return "";

				String dbName = gSettings.getFilter().getDatabase();
				if (dbName.lastIndexOf(File.separator) != -1)
					dbName = dbName.substring (dbName.lastIndexOf(File.separator) + 1);
				return dbName + " (Ver. " + (gSettings.getFilter().getVersion() != null ? gSettings.getFilter().getVersion() : "1.0") + ")";
			}
		});
/*
		// Add column for use of glycan database metadata name
		addTableViewerColumn( settingsTableViewer, "Use Glycan Database Metadata", 100,
				new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if ( !(element instanceof AnalyteSettings) )
					return "";

				GlycanSettings gSettings = ((AnalyteSettings) element).getGlycanSettings();
				if (gSettings != null && gSettings.getFilter() != null) {
					if (gSettings.getFilter().getUseDatabaseStructureMetaInfo()) {
						//TODO how to grey out the columns??
					}
					return gSettings.getFilter().getUseDatabaseStructureMetaInfo() ? "yes" : "no";
				}
				// Do nothing for lipid
				return "--";
			}
		});
*/
		// Add column for glycan filter
		addTableViewerColumn( settingsTableViewer, "Glycan Filter", 200,
				new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if ( !(element instanceof AnalyteSettings) )
					return "";

				GlycanSettings gSettings = ((AnalyteSettings) element).getGlycanSettings();
				if (gSettings != null && gSettings.getFilterSetting() != null) {
					return gSettings.getFilterSetting().getName() != null ? gSettings.getFilterSetting().getName() : gSettings.getFilterSetting().getFilter().toString();
				}

				return "";
			}
		});

		// Add column for lipid database name
		addTableViewerColumn( settingsTableViewer, "Lipid Database Name", 200,
				new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if ( !(element instanceof AnalyteSettings) )
					return "";

				LipidSettings lSettings = ((AnalyteSettings) element).getLipidSettings();
				if ( lSettings == null || lSettings.getDatabase() == null )
					return "";

				String dbName = lSettings.getDatabase().getURI();
				if (dbName.lastIndexOf(File.separator) != -1)
					dbName = dbName.substring (dbName.lastIndexOf(File.separator) + 1);
				return dbName;
			}
		});

		// Add column for derivatization name
		addTableViewerColumn( settingsTableViewer, "Derivatization Name", 150,
				new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if ( !(element instanceof AnalyteSettings) )
					return "";

				GlycanSettings gSettings = ((AnalyteSettings) element).getGlycanSettings();
				if (gSettings != null)
					return gSettings.getPerDerivatisationType();
				LipidSettings lSettings = ((AnalyteSettings) element).getLipidSettings();
				if (lSettings != null)
					return lSettings.getPerDerivatisationType();

				return "";
			}
		});

		// Set AnalyteSettings as array content
		settingsTableViewer.setContentProvider(new ArrayContentProvider());
		settingsTableViewer.setInput(getAnalyteSettings());	
	}

	/**
	 * Add TableViewerColumn with name, width and ColumnLabelProvider to specified TableViewer.
	 * @param a_settingsTV TableViewer to be added the column
	 * @param a_text String of column name
	 * @param a_iWidth column width
	 * @param a_labelProvider ColumnLabelProvider
	 * @return new TableViewerColumn
	 */
	private TableViewerColumn addTableViewerColumn(TableViewer a_settingsTV, String a_text, int a_iWidth, ColumnLabelProvider a_labelProvider ) {
		TableViewerColumn t_col = new TableViewerColumn(a_settingsTV, SWT.NONE);
		t_col.getColumn().setText(a_text);
		t_col.getColumn().setWidth(a_iWidth);
		t_col.setLabelProvider(a_labelProvider);
		return t_col;
	}
}
