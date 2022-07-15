package org.grits.toolbox.tools.gsl.annotation.entry.views.tabbed;

import org.eclipse.swt.widgets.Composite;
import org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationSelectionView;
import org.grits.toolbox.tools.gsl.annotation.entry.tablehelpers.MSGlycolipidAnnotationTable;

/**
 * Extends MSGlycanAnnotationSelectionView to use MSGlycolipidAnnotationTable.
 * @author Masaaki Matsubara
 * @see MSGlycolipidAnnotationTable
 *
 */
public class MSGlycolipidAnnotationSelectionView extends MSGlycanAnnotationSelectionView {
//	private static final Logger logger = Logger.getLogger(MSGlycanAnnotationSelectionView.class);
	
	public MSGlycolipidAnnotationSelectionView( Composite parent ) {
		super(parent);
	}
	
	@Override
	public String toString() {
		return "MSGlycolipidAnnotationSelectionView (" + parentTable + ")";
	}	

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationSelectionView#initTable()
	 */
	@Override
	protected void initTable() {
		subTable = new MSGlycolipidAnnotationTable(parent, (MSGlycolipidAnnotationTable) parentTable, iParentRowIndex, iParentScanNum, sParentRowId);
		subTable.createSubsetTable();
		addListeners();
	}
}
