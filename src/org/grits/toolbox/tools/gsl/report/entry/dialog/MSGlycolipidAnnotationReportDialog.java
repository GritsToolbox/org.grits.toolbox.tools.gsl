package org.grits.toolbox.tools.gsl.report.entry.dialog;

import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Shell;
import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.entry.ms.annotation.glycan.report.dialog.MSGlycanAnnotationReportDialog;
import org.grits.toolbox.tools.gsl.annotation.entry.property.MSGlycolipidAnnotationProperty;
import org.grits.toolbox.tools.gsl.report.entry.property.MSGlycolipidAnnotationReportProperty;
import org.grits.toolbox.tools.gsl.report.entry.property.datamodel.MSGlycolipidAnnotationReportMetaData;

/**
 * Extends MSGlycanAnnotationReportDialog to create MSGlycolipidAnnotationReportProperty/MetaData.
 * @author Masaaki Matsubara
 *
 */
public class MSGlycolipidAnnotationReportDialog extends MSGlycanAnnotationReportDialog {

	public MSGlycolipidAnnotationReportDialog(Shell parentShell, List<Entry> entries) {
		super(parentShell, entries);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.report.dialog.MSGlycanAnnotationReportDialog#getAnnotationPropertyType()
	 */
	@Override
	protected String getAnnotationPropertyType() {
		return MSGlycolipidAnnotationProperty.TYPE;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.report.dialog.MSGlycanAnnotationReportDialog#createEntry()
	 */
	@Override
	public Entry createEntry() {
		// return a new SimGlycanMerge Entry
		Entry simGlycanMergerEntry = new Entry();
		simGlycanMergerEntry.setDisplayName(getName());
		MSGlycolipidAnnotationReportProperty property = new MSGlycolipidAnnotationReportProperty();
		MSGlycolipidAnnotationReportMetaData metaData = new MSGlycolipidAnnotationReportMetaData();
		metaData.setName(getName());
		metaData.setVersion( MSGlycolipidAnnotationReportMetaData.CURRENT_VERSION );
//		metaData.setReportId(getName());
		
		metaData.setDescription(getDescription());
		metaData.setCreationDate(new Date());
		metaData.setUpdateDate(metaData.getCreationDate());
		property.setMsGlycanAnnotReportMetaData(metaData);
		simGlycanMergerEntry.setProperty(property);
		return simGlycanMergerEntry;
	}


}
