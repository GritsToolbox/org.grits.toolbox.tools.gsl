package org.grits.toolbox.tools.gsl.annotation.entry.views.tabbed;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.swt.widgets.Composite;
import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.core.datamodel.property.Property;
import org.grits.toolbox.datamodel.ms.tablemodel.FillTypes;
import org.grits.toolbox.display.control.table.process.TableDataProcessor;
import org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationMultiPageViewer;
import org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationPeaksView;
import org.grits.toolbox.entry.ms.annotation.property.MSAnnotationEntityProperty;
import org.grits.toolbox.entry.ms.annotation.views.tabbed.IMSAnnotationPeaksViewer;
import org.grits.toolbox.entry.ms.views.tabbed.MassSpecMultiPageViewer;
import org.grits.toolbox.tools.gsl.annotation.entry.process.loader.MSGlycolipidAnnotationTableDataProcessor;

/**
 * Extends MSGlycanAnnotationPeaksView to process and show glycolipid annotation results.
 * @author Masaaki Matsubara
 * @see MSGlycolipidAnnotationResultsComposite
 * @see MSGlycolipidAnnotationSelectionView
 * @see MSGlycolipidAnnotationTableDataProcessor
 *
 */
public class MSGlycolipidAnnotationPeaksView extends MSGlycanAnnotationPeaksView implements IMSAnnotationPeaksViewer {
//	private static final Logger logger = Logger.getLogger(MSGlycolipidAnnotationPeaksView.class);
	public static final String VIEW_ID = "plugin.ms.annotation.glycan.views.MSGlycolipidAnnotationPeaksView"; //$NON-NLS-1$
	
	@Inject
	public MSGlycolipidAnnotationPeaksView(@Optional Entry entry, @Optional Property msEntityProperty,
			@Named(MassSpecMultiPageViewer.MIN_MS_LEVEL_CONTEXT) int iMinMSLevel) {
		super (entry, msEntityProperty, iMinMSLevel);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationPeaksView#getNewResultsComposite(org.eclipse.swt.widgets.Composite, int)
	 */
	@Override
	protected MSGlycolipidAnnotationResultsComposite getNewResultsComposite( Composite composite, int style ) {
		return new MSGlycolipidAnnotationResultsComposite(composite, style);
	}

	@Override
	public String toString() {
		return "MSGlycolipidAnnotationPeaksView (" + entry + ")";
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationPeaksView#initNewSelectionView()
	 */
	@Override
	public void initNewSelectionView() {
		setSelectionView(new MSGlycolipidAnnotationSelectionView(getBottomPane()));
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationPeaksView#getNewTableDataProcessor(org.grits.toolbox.core.datamodel.Entry, org.grits.toolbox.core.datamodel.property.Property)
	 */
	@Override
	protected TableDataProcessor getNewTableDataProcessor( Entry entry, Property entityProperty  ) {
		MSGlycolipidAnnotationTableDataProcessor proc = new MSGlycolipidAnnotationTableDataProcessor(
				entry, entityProperty, 
				FillTypes.PeaksWithFeatures, getMinMSLevel());
		proc.initializeTableDataObject(entityProperty);
		return proc;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationPeaksView#getNewTableDataProcessor(org.grits.toolbox.core.datamodel.property.Property)
	 */
	@Override
	protected TableDataProcessor getNewTableDataProcessor(Property entityProperty) {
		Entry parentEntry = getEntry().getParent();
		MSGlycanAnnotationMultiPageViewer parentViewer = MSGlycanAnnotationMultiPageViewer.getActiveViewerForEntry(getPart().getContext(), parentEntry);
		if( parentViewer == null || parentViewer.getScansView() == null ) {
			return null;
		}
		MSGlycolipidAnnotationTableDataProcessor parentProc = (MSGlycolipidAnnotationTableDataProcessor) parentViewer.getScansView().getTableDataProcessor();
		if( parentProc.getGRITSdata() == null )
			return null;
		FillTypes fillType = FillTypes.PeaksWithFeatures;
		if( entityProperty instanceof MSAnnotationEntityProperty ) {
			MSAnnotationEntityProperty ep = ((MSAnnotationEntityProperty) entityProperty);
			if( ep.getMsLevel() > 3 && ep.getAnnotationId() == -1 ) {// if not annotated, show peak list only
				fillType = FillTypes.PeakList;
			}
		}
		MSGlycolipidAnnotationTableDataProcessor proc = new MSGlycolipidAnnotationTableDataProcessor(parentProc, entityProperty, 
				fillType, getMinMSLevel());
		proc.initializeTableDataObject(entityProperty);
		return proc;
	}

}
