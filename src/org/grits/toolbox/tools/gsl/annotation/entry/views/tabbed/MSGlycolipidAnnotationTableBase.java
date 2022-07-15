package org.grits.toolbox.tools.gsl.annotation.entry.views.tabbed;

import org.eclipse.swt.widgets.Composite;
import org.grits.toolbox.core.datamodel.property.Property;
import org.grits.toolbox.core.editor.EntryEditorPart;
import org.grits.toolbox.datamodel.ms.tablemodel.FillTypes;
import org.grits.toolbox.display.control.table.process.TableDataProcessor;
import org.grits.toolbox.display.control.table.tablecore.GRITSTable;
import org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationTableBase;
import org.grits.toolbox.entry.ms.annotation.views.tabbed.MSAnnotationTableBase;
import org.grits.toolbox.entry.ms.views.tabbed.MassSpecTableBase;
import org.grits.toolbox.tools.gsl.annotation.entry.tablehelpers.MSGlycolipidAnnotationTable;

/**
 * Extends MSGlycanAnnotationTableBase to use MSGlycolipidAnnotationTable.
 * @author Masaaki Matsubara
 * @see org.grits.toolbox.tools.gsl.annotation.entry.tablehelpers.MSGlycolipidAnnotationTable
 *
 */
public class MSGlycolipidAnnotationTableBase extends MSGlycanAnnotationTableBase {	

	public MSGlycolipidAnnotationTableBase( Composite parent, EntryEditorPart parentEditor, 
			Property entityProperty, TableDataProcessor dataProcessor, FillTypes fillType ) throws Exception {
		super(parent, parentEditor, entityProperty, dataProcessor, fillType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationTableBase#getNewSimianTable(org.grits.toolbox.entry.ms.views.tabbed.MassSpecTableBase, org.grits.toolbox.display.control.table.process.TableDataProcessor)
	 */
	@Override
	public GRITSTable getNewSimianTable( MassSpecTableBase _viewBase, TableDataProcessor _extractor ) throws Exception {
		return new MSGlycolipidAnnotationTable( (MSAnnotationTableBase) _viewBase, _extractor);
	}

}
