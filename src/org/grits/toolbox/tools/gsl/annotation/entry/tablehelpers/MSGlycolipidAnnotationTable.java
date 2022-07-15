package org.grits.toolbox.tools.gsl.annotation.entry.tablehelpers;

import org.eclipse.swt.widgets.Composite;
import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.datamodel.ms.annotation.preference.MSAnnotationViewerPreference;
import org.grits.toolbox.datamodel.ms.tablemodel.FillTypes;
import org.grits.toolbox.display.control.table.process.TableDataProcessor;
import org.grits.toolbox.entry.ms.annotation.glycan.tablehelpers.MSGlycanAnnotationTable;
import org.grits.toolbox.entry.ms.annotation.views.tabbed.MSAnnotationTableBase;
import org.grits.toolbox.ms.om.data.Annotation;
import org.grits.toolbox.ms.om.data.GlycanAnnotation;
import org.grits.toolbox.ms.om.data.GlycolipidAnnotation;
import org.grits.toolbox.ms.om.data.LipidAnnotation;
import org.grits.toolbox.tools.gsl.annotation.datamodel.preference.MSGlycolipidAnnotationViewerPreference;
import org.grits.toolbox.tools.gsl.annotation.datamodel.preference.MSGlycolipidAnnotationViewerPreferenceLoader;
import org.grits.toolbox.tools.gsl.annotation.entry.command.MSGlycolipidAnnotationViewColumnChooserCommandHandler;
import org.grits.toolbox.tools.gsl.annotation.entry.property.MSGlycolipidAnnotationEntityProperty;

/**
 * Extends MSGlycanAnnotationTable with specific option for glycolipid annotation.
 * 
 * @author Masaaki Matsubara
 */

public class MSGlycolipidAnnotationTable extends MSGlycanAnnotationTable {

	//log4J Logger
//	private static final Logger logger = Logger.getLogger(MSGlycolipidAnnotationTable.class);
	public static final String LIPID_ID_PREFIX = "LipidID: ";
	public static final String GLYCOLIPID_ID_PREFIX = "GlycolipidID: ";

	public MSGlycolipidAnnotationTable(MSAnnotationTableBase parent, TableDataProcessor xmlExtractor) throws Exception {
		super(parent, xmlExtractor);
	}

	public MSGlycolipidAnnotationTable(Composite parent, MSGlycolipidAnnotationTable parentTable, int iParentRowIndex, int iParentScanNum, String sParentRowId ) {
		super(parent, parentTable, iParentRowIndex, iParentScanNum, sParentRowId);
	}	

	public MSGlycolipidAnnotationTable(Composite parent, TableDataProcessor tableDataExtractor) {
		super(parent, tableDataExtractor);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.tablehelpers.MSGlycanAnnotationTable#loadPreference(org.grits.toolbox.datamodel.ms.annotation.preference.MSAnnotationViewerPreference)
	 */
	@Override
	protected MSGlycolipidAnnotationViewerPreference loadPreference(MSAnnotationViewerPreference parentPref) {
		return MSGlycolipidAnnotationViewerPreferenceLoader.getTableViewerPreference(parentPref.getMSLevel(), FillTypes.Selection);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.tablehelpers.MSGlycanAnnotationTable#initColumnChooserLayer()
	 */
	@Override
	protected void initColumnChooserLayer() {
		MSGlycolipidAnnotationViewColumnChooserCommandHandler columnChooserCommandHandler
			= new MSGlycolipidAnnotationViewColumnChooserCommandHandler( this );
		columnGroupHeaderLayer.registerCommandHandler(columnChooserCommandHandler);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.tablehelpers.MSGlycanAnnotationTable#getAnnotationStructureId(org.grits.toolbox.ms.om.data.Annotation)
	 */
	@Override
	protected String getAnnotationStructureId(Annotation annot) {
		String sAnnotStructureID = annot.getStringId();
		String sPrefix = ( annot instanceof GlycanAnnotation )?     GLYCAN_ID_PREFIX :
						 ( annot instanceof LipidAnnotation )?      LIPID_ID_PREFIX :
						 ( annot instanceof GlycolipidAnnotation )? GLYCOLIPID_ID_PREFIX :
						 null;
		if ( sPrefix != null )
			sAnnotStructureID = sPrefix+sAnnotStructureID;
		return sAnnotStructureID;
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
