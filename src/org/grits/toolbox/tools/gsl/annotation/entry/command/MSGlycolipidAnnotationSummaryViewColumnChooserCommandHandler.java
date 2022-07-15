package org.grits.toolbox.tools.gsl.annotation.entry.command;

import org.grits.toolbox.display.control.table.command.GRITSTableDisplayColumnChooserCommand;
import org.grits.toolbox.display.control.table.dialog.GRITSTableColumnChooser;
import org.grits.toolbox.display.control.table.tablecore.IGritsTable;
import org.grits.toolbox.entry.ms.annotation.glycan.command.MSGlycanAnnotationSummaryViewColumnChooserCommandHandler;
import org.grits.toolbox.tools.gsl.annotation.entry.tablehelpers.gui.MSGlycolipidAnnotationSummaryTableColumnChooser;

/**
 * Extends MSGlycanAnnotationSummaryViewColumnChooserCommandHandler to use MSGlycolipidAnnotationSummaryTableColumnChooser.
 * @see org.grits.toolbox.tools.gsl.annotation.entry.tablehelpers.gui.MSGlycolipidAnnotationSummaryTableColumnChooser
 * @author Masaaki Matsubara
 *
 */
public class MSGlycolipidAnnotationSummaryViewColumnChooserCommandHandler
		extends MSGlycanAnnotationSummaryViewColumnChooserCommandHandler {

	public MSGlycolipidAnnotationSummaryViewColumnChooserCommandHandler(
			IGritsTable gritsTable ) {
		this(false, gritsTable);
	}

	public MSGlycolipidAnnotationSummaryViewColumnChooserCommandHandler(
			boolean sortAvalableColumns,
			IGritsTable gritsTable ) {
		super( sortAvalableColumns, gritsTable );
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.command.MSGlycanAnnotationSummaryViewColumnChooserCommandHandler#getNewGRITSTableColumnChooser(org.grits.toolbox.display.control.table.command.GRITSTableDisplayColumnChooserCommand)
	 */
	@Override
	public GRITSTableColumnChooser getNewGRITSTableColumnChooser(
			GRITSTableDisplayColumnChooserCommand command) {
		MSGlycolipidAnnotationSummaryTableColumnChooser columnChooser = new MSGlycolipidAnnotationSummaryTableColumnChooser(
				command.getNatTable().getShell(),
				sortAvailableColumns, false, gritsTable );
		return columnChooser;
	}

}
