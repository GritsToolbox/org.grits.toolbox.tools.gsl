package org.grits.toolbox.tools.gsl.annotation.entry.handler;

import org.eclipse.swt.widgets.Shell;
import org.grits.toolbox.core.dataShare.PropertyHandler;
import org.grits.toolbox.datamodel.ms.annotation.glycan.tablemodel.MSGlycanAnnotationSummaryTableDataObject;
import org.grits.toolbox.entry.ms.annotation.adaptor.MSAnnotationExportFileAdapter;
import org.grits.toolbox.entry.ms.annotation.dialog.MSAnnotationExportDialog;
import org.grits.toolbox.entry.ms.annotation.glycan.dialog.MSGlycanAnnotationSummaryExportDialog;
import org.grits.toolbox.entry.ms.annotation.glycan.handler.MSGlycanAnnotationExportCommand;
import org.grits.toolbox.tools.gsl.annotation.entry.dialog.MSGlycolipidAnnotationExportDialog;

/**
 * Class for provide dialog for MSGlycolipidAnnotation GRITS table.
 * Extends MSGlycanAnnotationExportCommand to use MSGlycolipidANnotationExportDialog
 * @see org.grits.toolbox.tools.gsl.annotation.entry.dialog.MSGlycolipidAnnotationExportDialog
 * @author Masaaki Matsubara
 *
 */
public class MSGlycolipidAnnotationExportCommand extends MSGlycanAnnotationExportCommand {

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.handler.MSGlycanAnnotationExportCommand#getNewExportDialog(org.eclipse.swt.widgets.Shell, org.grits.toolbox.entry.ms.annotation.adaptor.MSAnnotationExportFileAdapter)
	 */
	@Override
	protected MSAnnotationExportDialog getNewExportDialog(Shell activeShell, MSAnnotationExportFileAdapter adapter) {
		if (this.getTableDataObject() != null && this.getTableDataObject() instanceof MSGlycanAnnotationSummaryTableDataObject) {
			return new MSGlycanAnnotationSummaryExportDialog(PropertyHandler.getModalDialog(activeShell), adapter);
		} else {
			return new MSGlycolipidAnnotationExportDialog(PropertyHandler.getModalDialog(activeShell), adapter);
		}
	}
	

}
