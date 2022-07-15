/*******************************************************************************
 * Copyright (c) 2012 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package org.grits.toolbox.tools.gsl.annotation.entry.command;

import org.grits.toolbox.display.control.table.command.GRITSTableDisplayColumnChooserCommand;
import org.grits.toolbox.display.control.table.dialog.GRITSTableColumnChooser;
import org.grits.toolbox.display.control.table.tablecore.IGritsTable;
import org.grits.toolbox.entry.ms.annotation.glycan.command.MSGlycanAnnotationViewColumnChooserCommandHandler;
import org.grits.toolbox.tools.gsl.annotation.entry.tablehelpers.gui.MSGlycolipidAnnotationTableColumnChooser;

/**
 * Extends MSGlycanAnnotationViewColumnChooserCommandHandler to use MSGlycolipidAnnotationTableColumnChooser.
 * @see org.grits.toolbox.tools.gsl.annotation.entry.tablehelpers.gui.MSGlycolipidAnnotationTableColumnChooser
 * @author Masaaki Matsubara
 *
 */
public class MSGlycolipidAnnotationViewColumnChooserCommandHandler 
	extends MSGlycanAnnotationViewColumnChooserCommandHandler {
	
	public MSGlycolipidAnnotationViewColumnChooserCommandHandler(
			IGritsTable gritsTable ) {

		this(false, gritsTable);
	}

	public MSGlycolipidAnnotationViewColumnChooserCommandHandler(
			boolean sortAvalableColumns,
			IGritsTable gritsTable ) {
		super( sortAvalableColumns, gritsTable );
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.command.MSGlycanAnnotationViewColumnChooserCommandHandler#getNewGRITSTableColumnChooser(org.grits.toolbox.display.control.table.command.GRITSTableDisplayColumnChooserCommand)
	 */
	@Override
	public GRITSTableColumnChooser getNewGRITSTableColumnChooser(
			GRITSTableDisplayColumnChooserCommand command) {
		MSGlycolipidAnnotationTableColumnChooser columnChooser = new MSGlycolipidAnnotationTableColumnChooser(
				command.getNatTable().getShell(),
				sortAvailableColumns, false, gritsTable );
		return columnChooser;
	}
		
}
