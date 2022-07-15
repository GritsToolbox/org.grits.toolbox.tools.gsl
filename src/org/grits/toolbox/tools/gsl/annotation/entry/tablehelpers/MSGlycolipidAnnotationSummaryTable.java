package org.grits.toolbox.tools.gsl.annotation.entry.tablehelpers;

import org.eclipse.swt.widgets.Composite;
import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.display.control.table.process.TableDataProcessor;
import org.grits.toolbox.entry.ms.annotation.glycan.tablehelpers.MSGlycanAnnotationSummaryLastHeaderOverrideLabelAccumulator;
import org.grits.toolbox.entry.ms.annotation.glycan.tablehelpers.MSGlycanAnnotationSummaryTable;
import org.grits.toolbox.entry.ms.annotation.glycan.tablehelpers.MSGlycanAnnotationTable;
import org.grits.toolbox.entry.ms.annotation.views.tabbed.MSAnnotationTableBase;
import org.grits.toolbox.tools.gsl.annotation.datamodel.tablemodel.MSGlycolipidAnnotationSummaryTableDataObject;
import org.grits.toolbox.tools.gsl.annotation.entry.command.MSGlycolipidAnnotationSummaryViewColumnChooserCommandHandler;
import org.grits.toolbox.tools.gsl.annotation.entry.property.MSGlycolipidAnnotationEntityProperty;

/**
 * Extends MSGlycanAnnotationSummaryTable for MSGlycolipidAnnotationSummary GRITS table.
 * @author Masaaki Masaaki
 *
 */
public class MSGlycolipidAnnotationSummaryTable extends MSGlycanAnnotationSummaryTable {
//	private static final Logger logger = Logger.getLogger(MSGlycolipidAnnotationSummaryTable.class);

	public MSGlycolipidAnnotationSummaryTable(MSAnnotationTableBase parent, TableDataProcessor xmlExtractor) throws Exception {
		super(parent, xmlExtractor);
	}

	public MSGlycolipidAnnotationSummaryTable(Composite parent, TableDataProcessor tableDataExtractor) {
		super(parent, tableDataExtractor);
	}

	private MSGlycolipidAnnotationSummaryTableDataObject getMyTableDataObject() {
		return (MSGlycolipidAnnotationSummaryTableDataObject) getGRITSTableDataObject();
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.tablehelpers.MSGlycanAnnotationSummaryTable#setSummaryAccumulator()
	 */
	@Override
	public void setSummaryAccumulator() {
		if( parentTable == null && getColumnGroupHeaderLayer() != null ) {
			MSGlycanAnnotationSummaryLastHeaderOverrideLabelAccumulator ola = new MSGlycanAnnotationSummaryLastHeaderOverrideLabelAccumulator(
					columnHeaderDataLayer, 
					getMyTableDataObject().getCartoonCols());
			this.columnHeaderDataLayer.setConfigLabelAccumulator(ola);
			groupedAccumulator = new MSGlycolipidAnnotationSummaryTopHeaderOverrideLabelAccumulator(
					topColumnHeaderDataLayer, columnGroupHeaderLayer);
			this.topColumnHeaderDataLayer.setConfigLabelAccumulator(groupedAccumulator);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.tablehelpers.MSGlycanAnnotationSummaryTable#initColumnChooserLayer()
	 */
	@Override
	protected void initColumnChooserLayer() {
		MSGlycolipidAnnotationSummaryViewColumnChooserCommandHandler columnChooserCommandHandler
			= new MSGlycolipidAnnotationSummaryViewColumnChooserCommandHandler(this);
		columnGroupHeaderLayer.registerCommandHandler(columnChooserCommandHandler);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.tablehelpers.MSGlycanAnnotationSummaryTable#isAnnotationStructureId(java.lang.String)
	 */
	protected boolean isAnnotationStructureId(String sLabel) {
		// TODO: Is GlycanAnnotation needed?
		return sLabel.startsWith(MSGlycanAnnotationTable.GLYCAN_ID_PREFIX)
			|| sLabel.startsWith(MSGlycolipidAnnotationTable.GLYCOLIPID_ID_PREFIX);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.tablehelpers.MSGlycanAnnotationTable#getNewTableCompatibleEntry(org.grits.toolbox.core.datamodel.Entry)
	 */
	@Override
	protected Entry getNewTableCompatibleEntry(Entry parentEntry) {
		Entry newEntry = MSGlycolipidAnnotationEntityProperty.getTableCompatibleEntry(parentEntry);	
		return newEntry;
	}

}
