package org.grits.toolbox.tools.gsl.annotation.entry.views.tabbed;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.swt.widgets.Composite;
import org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationSummary;
import org.grits.toolbox.entry.ms.annotation.process.loader.MSAnnotationTableDataProcessor;
import org.grits.toolbox.entry.ms.annotation.views.tabbed.MSAnnotationDetails;
import org.grits.toolbox.tools.gsl.annotation.entry.process.loader.MSGlycolipidAnnotationSummaryTableDataProcessor;

/**
 * Extends MSGlycanAnnotationSummary to use MSGlycolipidAnnotationSummaryTableDataProcessor
 * and MSGlycolipidAnnotationSummaryResultsComposite.
 * @author Masaaki Matsubara
 * @see org.grits.toolbox.tools.gsl.annotation.entry.process.loader.MSGlycolipidAnnotationSummaryTableDataProcessor
 * @see MSGlycolipidAnnotationSummaryResultsComposite
 *
 */
public class MSGlycolipidAnnotationSummaryView extends MSGlycanAnnotationSummary {
//	private static final Logger logger = Logger.getLogger(MSGlycolipidAnnotationSummaryView.class);
	public static final String VIEW_ID = "tools.gsl.entry.views.tabbed.MSGlycolipidAnnotationSummaryView";

	@Inject
	public MSGlycolipidAnnotationSummaryView(MSAnnotationDetails detailsView) {
		super(detailsView);
	}

	@Override
	public String toString() {
		return "MSGlycolipidAnnotationSummaryView (" + entry + ")";
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationSummary#getNewTableDataProcessor(java.util.List)
	 */
	@Override
	protected MSGlycolipidAnnotationSummaryTableDataProcessor getNewTableDataProcessor(List<MSAnnotationTableDataProcessor> alList) {
		return new MSGlycolipidAnnotationSummaryTableDataProcessor(
				this.detailsView.getEntry(), this.detailsView.getMsEntityProperty(), alList
			);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationSummary#getNewResultsComposite(org.eclipse.swt.widgets.Composite, int)
	 */
	@Override
	protected MSGlycolipidAnnotationSummaryResultsComposite getNewResultsComposite( Composite composite, int style ) {
		return new MSGlycolipidAnnotationSummaryResultsComposite(composite, style);
	}

}
