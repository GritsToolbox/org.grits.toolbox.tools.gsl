package org.grits.toolbox.tools.gsl.annotation.entry.tablehelpers.gui;

import org.eclipse.swt.widgets.Shell;
import org.grits.toolbox.display.control.table.dialog.ColumnChooserDialog;
import org.grits.toolbox.display.control.table.preference.TableViewerColumnSettings;
import org.grits.toolbox.display.control.table.preference.TableViewerPreference;
import org.grits.toolbox.display.control.table.tablecore.IGritsTable;
import org.grits.toolbox.entry.ms.annotation.glycan.tablehelpers.gui.MSGlycanAnnotationSummaryTableColumnChooser;
import org.grits.toolbox.tools.gsl.annotation.entry.process.loader.MSGlycolipidAnnotationSummaryTableDataProcessor;

/**
 * Table column chooser for MSGlycolipidAnnotationSummary GRITS table.
 * Extends MSGlycanAnnotationSummaryTableColumnChooser to use MSGlycolipidAnnotationSummaryTableDataProcessor.setDefaultColumnViewSettings()
 * @see org.grits.toolbox.tools.gsl.annotation.entry.process.loader.MSGlycolipidAnnotationSummaryTableDataProcessor
 * @author Masaaki Matsubara
 *
 */
public class MSGlycolipidAnnotationSummaryTableColumnChooser extends MSGlycanAnnotationSummaryTableColumnChooser {

	public MSGlycolipidAnnotationSummaryTableColumnChooser(Shell shell,
			boolean sortAvailableColumns,
			boolean asGlobalPreference, 
			IGritsTable gritsTable ) {
		super(shell, sortAvailableColumns, asGlobalPreference, gritsTable );
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.tablehelpers.gui.MSGlycanAnnotationSummaryTableColumnChooser#getNewColumnChooserDialog(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected ColumnChooserDialog getNewColumnChooserDialog(Shell shell) {
		// Just do as super
		return super.getNewColumnChooserDialog(shell);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.tablehelpers.gui.MSGlycanAnnotationSummaryTableColumnChooser#getDefaultSettings()
	 */
	@Override
	protected TableViewerColumnSettings getDefaultSettings() {
		MSGlycolipidAnnotationSummaryTableDataProcessor proc = (MSGlycolipidAnnotationSummaryTableDataProcessor) getGRITSTable().getTableDataProcessor();
		TableViewerPreference newPref = proc.initializePreferences();
		TableViewerColumnSettings newSettings = newPref.getPreferenceSettings();
		MSGlycolipidAnnotationSummaryTableDataProcessor.setDefaultColumnViewSettings(newSettings);
		return newSettings;
	}
}
