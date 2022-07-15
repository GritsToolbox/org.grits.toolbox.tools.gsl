package org.grits.toolbox.tools.gsl.wizard.annotation;

import java.util.List;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.GlycanSettingsTableWithActions;
import org.grits.toolbox.ms.om.data.AnalyteSettings;

/**
 * Extends GlycanSettingsTableWithActions to use GlycolipidDatabaseSettingsTableComposite and GlycolipidSettingsDialog.
 * @author Masaaki Matsubara
 * @see GlycolipidDatabaseSettingsTableComposite
 * @see GlycolipidSettingsDialog
 *
 */
public class GlycolipidSettingsTableWithActions extends GlycanSettingsTableWithActions {

	private GlycolipidDatabaseSettingsTableComposite table;

	public GlycolipidSettingsTableWithActions(Composite parent, int style, IPropertyChangeListener listener) {
		super(parent, style, listener);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.GlycanSettingsTableWithActions#createTable()
	 */
	@Override
	protected void createTable() {
		table = new GlycolipidDatabaseSettingsTableComposite(this, SWT.NONE);
		GridData gd2 = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
		List<AnalyteSettings> existing = null;
		if (preferences != null && preferences.getMethod() != null)
			existing = preferences.getMethod().getAnalyteSettings();
		if (existing != null) table.setAnalyteSettings(existing);
		table.setLayoutData(gd2);
		table.createTable();
		settingsTableViewer = table.getSettingsTableViewer();
		settingsTable = settingsTableViewer.getTable();
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.GlycanSettingsTableWithActions#addEditButton()
	 */
	@Override
	protected void addEditButton() {
		Button editButton = new Button(this, SWT.PUSH);
		editButton.setText("Edit");
		
		editButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = settingsTable.getSelection();
				if (items.length == 0)
					return;

				AnalyteSettings setting = (AnalyteSettings) items[0].getData();
				if (setting == null)
					return;

				GlycolipidSettingsDialog dialog = new GlycolipidSettingsDialog(getShell(), filterLibrary, setting);
				if (dialog.open() != Window.OK)
					return;
				settingsTableViewer.refresh();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.GlycanSettingsTableWithActions#addAddButton()
	 */
	@Override
	protected void addAddButton() {
		Button addGlycanButton = new Button(this, SWT.PUSH);
		addGlycanButton.setText("Add");
		addGlycanButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				GlycolipidSettingsDialog dialog = new GlycolipidSettingsDialog(getShell(), filterLibrary);
				if (dialog.open() != Window.OK)
					return;
				AnalyteSettings analSettings = dialog.getAnalyteSettings();
				getAnalyteSettings().add(analSettings);

				settingsTableViewer.refresh();

				PropertyChangeEvent ce = new PropertyChangeEvent(this, "Page Complete", !getAnalyteSettings().isEmpty(), true);
				listener.propertyChange(ce);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.GlycanSettingsTableWithActions#getAnalyteSettings()
	 */
	@Override
	public List<AnalyteSettings> getAnalyteSettings() {
		return table.getAnalyteSettings();
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.GlycanSettingsTableWithActions#setAnalyteSettings(java.util.List)
	 */
	@Override
	public void setAnalyteSettings (List<AnalyteSettings> settings) {
		table.setAnalyteSettings(settings);
	}

}
