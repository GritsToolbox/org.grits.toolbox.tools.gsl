package org.grits.toolbox.tools.gsl.annotation.entry.views.tabbed;

import org.eclipse.swt.widgets.Composite;
import org.grits.toolbox.core.datamodel.property.Property;
import org.grits.toolbox.core.editor.EntryEditorPart;

import org.grits.toolbox.datamodel.ms.tablemodel.FillTypes;
import org.grits.toolbox.display.control.table.process.TableDataProcessor;
import org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationResultsComposite;

/**
 * Extends MSGlycanAnnotationResultsComposite to use MSGlycolipidAnnotationTableBase.
 * @author Masaaki Matsubara
 * @see MSGlycolipidAnnotationTableBase
 *
 */
public class MSGlycolipidAnnotationResultsComposite extends MSGlycanAnnotationResultsComposite {

	public MSGlycolipidAnnotationResultsComposite(Composite parent, int style) {
		super(parent, style);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationResultsComposite#createPartControl(org.eclipse.swt.widgets.Composite, org.grits.toolbox.core.editor.EntryEditorPart, org.grits.toolbox.core.datamodel.property.Property, org.grits.toolbox.display.control.table.process.TableDataProcessor, org.grits.toolbox.datamodel.ms.tablemodel.FillTypes)
	 */
	@Override
	public void createPartControl(Composite parent, EntryEditorPart parentEditor, Property entityProprty, 
			TableDataProcessor dataProcessor, FillTypes fillType ) throws Exception {
		this.baseView = new MSGlycolipidAnnotationTableBase(parent, parentEditor, entityProprty, dataProcessor, fillType);	
		this.baseView.initializeTable();
		this.baseView.layout();
	}
	
}
