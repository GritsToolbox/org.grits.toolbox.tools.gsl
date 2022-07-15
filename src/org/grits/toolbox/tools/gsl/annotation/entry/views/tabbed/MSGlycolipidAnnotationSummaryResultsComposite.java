package org.grits.toolbox.tools.gsl.annotation.entry.views.tabbed;

import org.eclipse.swt.widgets.Composite;
import org.grits.toolbox.core.datamodel.property.Property;
import org.grits.toolbox.core.editor.EntryEditorPart;
import org.grits.toolbox.datamodel.ms.tablemodel.FillTypes;
import org.grits.toolbox.display.control.table.process.TableDataProcessor;
import org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationSummaryResultsComposite;

/**
 * Extends MSGlycanAnnotationSummaryResultsComposite to use MSGlycolipidAnnotationSummaryTableBase.
 * @author Masaaki Matsubara
 * @see MSGlycolipidAnnotationSummaryTableBase
 *
 */
public class MSGlycolipidAnnotationSummaryResultsComposite extends MSGlycanAnnotationSummaryResultsComposite {

	public MSGlycolipidAnnotationSummaryResultsComposite(Composite parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationSummaryResultsComposite#createPartControl(org.eclipse.swt.widgets.Composite, org.grits.toolbox.core.editor.EntryEditorPart, org.grits.toolbox.core.datamodel.property.Property, org.grits.toolbox.display.control.table.process.TableDataProcessor, org.grits.toolbox.datamodel.ms.tablemodel.FillTypes)
	 */
	@Override
	public void createPartControl(Composite parent, EntryEditorPart parentEditor, Property entityProprty, 
			TableDataProcessor dataProcessor, FillTypes fillType ) throws Exception {
		this.baseView = new MSGlycolipidAnnotationSummaryTableBase(parent, parentEditor, entityProprty, dataProcessor, fillType);	
		this.baseView.initializeTable();
		this.baseView.layout();
	}

}
