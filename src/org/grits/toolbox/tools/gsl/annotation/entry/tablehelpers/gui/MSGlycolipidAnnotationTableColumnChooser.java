package org.grits.toolbox.tools.gsl.annotation.entry.tablehelpers.gui;

import org.eclipse.swt.widgets.Shell;
import org.grits.toolbox.display.control.table.dialog.ColumnChooserDialog;
import org.grits.toolbox.display.control.table.preference.TableViewerColumnSettings;
import org.grits.toolbox.display.control.table.preference.TableViewerPreference;
import org.grits.toolbox.display.control.table.tablecore.IGritsTable;
import org.grits.toolbox.entry.ms.annotation.glycan.tablehelpers.gui.MSGlycanAnnotationTableColumnChooser;
import org.grits.toolbox.tools.gsl.annotation.entry.process.loader.MSGlycolipidAnnotationTableDataProcessor;

/**
 * Table column chooser for MSGlycolipidAnnotation GRITS table.
 * Extends MSGlycanAnnotationTableColumnChooser to use MSGlycolipidAnnotationTableDataProcessor.setDefaultColumnViewSettings()
 * @see org.grits.toolbox.tools.gsl.annotation.entry.process.loader.MSGlycolipidAnnotationTableDataProcessor
 * @author Masaaki Matsubara
 *
 */
public class MSGlycolipidAnnotationTableColumnChooser extends MSGlycanAnnotationTableColumnChooser {

	public MSGlycolipidAnnotationTableColumnChooser(Shell shell,
			boolean sortAvailableColumns,
			boolean asGlobalPreference, 
			IGritsTable gritsTable ) {
		super(shell, sortAvailableColumns, asGlobalPreference, gritsTable );
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.tablehelpers.gui.MSGlycanAnnotationTableColumnChooser#getNewColumnChooserDialog(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected ColumnChooserDialog getNewColumnChooserDialog(Shell shell) {
		// Just do as super
		return super.getNewColumnChooserDialog(shell);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.tablehelpers.gui.MSGlycanAnnotationTableColumnChooser#getDefaultSettings()
	 */
	@Override
	protected TableViewerColumnSettings getDefaultSettings() {
		MSGlycolipidAnnotationTableDataProcessor proc = (MSGlycolipidAnnotationTableDataProcessor) getGRITSTable().getTableDataProcessor();
		TableViewerPreference newPref = proc.initializePreferences();
		TableViewerColumnSettings newSettings = newPref.getPreferenceSettings();
		MSGlycolipidAnnotationTableDataProcessor.setDefaultColumnViewSettings(proc.getSimianTableDataObject().getFillType(), newSettings);
		return newSettings;
	}
	
}
