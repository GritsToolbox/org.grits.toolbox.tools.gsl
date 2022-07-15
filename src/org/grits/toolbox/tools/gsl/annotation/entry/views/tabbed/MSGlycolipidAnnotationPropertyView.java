package org.grits.toolbox.tools.gsl.annotation.entry.views.tabbed;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationPropertyView;
import org.grits.toolbox.ms.om.data.AnalyteSettings;
import org.grits.toolbox.tools.gsl.wizard.annotation.GlycolipidDatabaseSettingsTableComposite;

/**
 * Extends MSGlycanAnnotationPropertyView to use GlycolipidDatabaseSettingTableComposite
 * @author Masaaki Matsubara
 * @see org.grits.toolbox.tools.gsl.wizard.annotation.GlycolipidDatabaseSettingsTableComposite
 *
 */
public class MSGlycolipidAnnotationPropertyView extends MSGlycanAnnotationPropertyView {

	@Inject
	public MSGlycolipidAnnotationPropertyView(Entry entry) {
		super(entry);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationPropertyView#addDatabaseSettingTable(java.util.List)
	 */
	@Override
	protected void addDatabaseSettingTable(List<AnalyteSettings> aSettings) {
		Label lSpacer1 = new Label(getContainer(), SWT.NONE);	// column 1	
		lSpacer1.setText("Database Settings");
		GridData gridDataSpacer1 = new GridData();
		gridDataSpacer1.horizontalSpan = 6;
		lSpacer1.setLayoutData(gridDataSpacer1);
		
		GlycolipidDatabaseSettingsTableComposite dbSettingTable
			= new GlycolipidDatabaseSettingsTableComposite(getContainer(), SWT.NONE);
		GridData gd2 = new GridData(SWT.FILL, SWT.FILL, true, true, 6, 1);
		dbSettingTable.setLayoutData(gd2);
		dbSettingTable.setAnalyteSettings(aSettings);
		dbSettingTable.createTable();
	}

}
