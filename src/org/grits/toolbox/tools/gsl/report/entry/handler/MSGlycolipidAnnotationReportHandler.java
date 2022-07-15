package org.grits.toolbox.tools.gsl.report.entry.handler;

import java.util.List;

import org.eclipse.swt.widgets.Shell;
import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.entry.ms.annotation.glycan.report.handler.MSGlycanAnnotationReportAnnotationHandler;
import org.grits.toolbox.tools.gsl.annotation.entry.property.MSGlycolipidAnnotationProperty;
import org.grits.toolbox.tools.gsl.report.entry.dialog.MSGlycolipidAnnotationReportDialog;
import org.grits.toolbox.tools.gsl.report.entry.property.MSGlycolipidAnnotationReportProperty;

/**
 * Extends MSGlycanAnnotationReportAnnotationHandler to use MSGlycolipidAnnotationReportDialog.
 * @author Masaaki Matsubara
 *
 */
public class MSGlycolipidAnnotationReportHandler extends MSGlycanAnnotationReportAnnotationHandler {

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.report.handler.MSGlycanAnnotationReportAnnotationHandler#getAnnotationPropertyType()
	 */
	@Override
	protected String getAnnotationPropertyType() {
		return MSGlycolipidAnnotationProperty.TYPE;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.report.handler.MSGlycanAnnotationReportAnnotationHandler#getMergeFolder()
	 */
	@Override
	protected String getMergeFolder() {
		return MSGlycolipidAnnotationReportProperty.ARCHIVE_FOLDER;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.report.handler.MSGlycanAnnotationReportAnnotationHandler#getReportDialog(org.eclipse.swt.widgets.Shell, java.util.List)
	 */
	@Override
	protected MSGlycolipidAnnotationReportDialog getReportDialog(Shell parentShell, List<Entry> entries) {
		return new MSGlycolipidAnnotationReportDialog(parentShell, entries);
	}


}
