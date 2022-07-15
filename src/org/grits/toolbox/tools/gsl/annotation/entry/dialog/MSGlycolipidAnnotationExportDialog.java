package org.grits.toolbox.tools.gsl.annotation.entry.dialog;

import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.grits.toolbox.entry.ms.annotation.adaptor.MSAnnotationExportFileAdapter;
import org.grits.toolbox.entry.ms.annotation.glycan.dialog.MSGlycanAnnotationExportDialog;

/**
 * Dialog for exporting MSGlycolipidAnnotation GRITS table data into other file type.
 * Extends MSGlycanAnnotationExportDialog not to export Bionic and GELATO database.
 * @author Masaaki Matsubara
 *
 */
public class MSGlycolipidAnnotationExportDialog extends MSGlycanAnnotationExportDialog {

	public MSGlycolipidAnnotationExportDialog(Shell parentShell,
			MSAnnotationExportFileAdapter msAnnotationExportFileAdapter) {
		super(parentShell, msAnnotationExportFileAdapter);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.dialog.MSAnnotationExportDialog#setDownloadOptions(org.eclipse.swt.widgets.List)
	 */
	@Override
	protected void setDownloadOptions(List downloadlist) {
		downloadlist.add(downloadOptions[0]);
		downloadlist.add(downloadOptions[1]);
		// Do not export Bionic and GELATO database
//		downloadlist.add(downloadOptions[2]);
//		downloadlist.add(downloadOptions[3]);
	}
}
