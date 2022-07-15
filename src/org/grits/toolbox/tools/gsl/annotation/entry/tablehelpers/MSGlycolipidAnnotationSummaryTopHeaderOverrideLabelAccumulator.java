package org.grits.toolbox.tools.gsl.annotation.entry.tablehelpers;

import org.eclipse.nebula.widgets.nattable.group.ColumnGroupHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.grits.toolbox.entry.ms.annotation.glycan.tablehelpers.MSGlycanAnnotationSummaryTopHeaderOverrideLabelAccumulator;
import org.grits.toolbox.entry.ms.annotation.glycan.tablehelpers.MSGlycanAnnotationTable;

/**
 * Extends MSGlycanAnnotationSummaryTopHeaderOverrideLabelAccumulator to handle glycolipid annotation structure ID
 * @author Masaaki Matsubara
 *
 * @param <T>
 */
public class MSGlycolipidAnnotationSummaryTopHeaderOverrideLabelAccumulator<T>
		extends MSGlycanAnnotationSummaryTopHeaderOverrideLabelAccumulator<T> {

	public MSGlycolipidAnnotationSummaryTopHeaderOverrideLabelAccumulator(DataLayer dataLayer,
			ColumnGroupHeaderLayer groupHeaderLayer) {
		super(dataLayer, groupHeaderLayer);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.tablehelpers.MSGlycanAnnotationSummaryTopHeaderOverrideLabelAccumulator#isStructureID(java.lang.String)
	 */
	@Override
	protected boolean isAnnotationStructureId(String sStructureID) {
		return sStructureID.startsWith( MSGlycanAnnotationTable.GLYCAN_ID_PREFIX)
			|| sStructureID.startsWith( MSGlycolipidAnnotationTable.GLYCOLIPID_ID_PREFIX);
	}
}
