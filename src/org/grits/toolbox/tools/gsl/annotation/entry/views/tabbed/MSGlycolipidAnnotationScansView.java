package org.grits.toolbox.tools.gsl.annotation.entry.views.tabbed;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.swt.widgets.Composite;
import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.core.datamodel.property.Property;
import org.grits.toolbox.datamodel.ms.tablemodel.FillTypes;
import org.grits.toolbox.display.control.table.process.TableDataProcessor;
import org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationMultiPageViewer;
import org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationScansView;
import org.grits.toolbox.entry.ms.views.tabbed.MassSpecMultiPageViewer;
import org.grits.toolbox.tools.gsl.annotation.entry.process.loader.MSGlycolipidAnnotationTableDataProcessor;

/**
 * Extends MSGlycanAnnotationScansView to use MSGlycolipidAnnotationResultsComposite
 * and MSGlycolipidAnnotationTableDataProcessor.
 * @author Masaaki Matsubara
 * @see MSGlycolipidAnnotationResultsComposite
 * @see MSGlycolipidAnnotationTableDataProcessor
 *
 */
public class MSGlycolipidAnnotationScansView extends MSGlycanAnnotationScansView {

//	private static final Logger logger = Logger.getLogger(MSGlycolipidAnnotationScansView.class);
	public static final String VIEW_ID = "plugin.ms.annotation.views.MSGlycolipidAnnotationScansView"; //$NON-NLS-1$

	@Inject
	public MSGlycolipidAnnotationScansView(Entry entry, Property msEntityProperty,
			@Named(MassSpecMultiPageViewer.MIN_MS_LEVEL_CONTEXT) int iMinMSLevel) {
		super(entry, msEntityProperty, iMinMSLevel);
	}
	
	@Override
	public String toString() {
		return "MSGlycolipidAnnotationScansView (" + entry + ")";
	}	

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationScansView#getNewResultsComposite(org.eclipse.swt.widgets.Composite, int)
	 */
	@Override
	protected MSGlycolipidAnnotationResultsComposite getNewResultsComposite( Composite composite, int style ) {
		return new MSGlycolipidAnnotationResultsComposite(composite, style);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationScansView#getNewTableDataProcessor(org.grits.toolbox.core.datamodel.Entry, org.grits.toolbox.core.datamodel.property.Property)
	 */
	@Override
	protected TableDataProcessor getNewTableDataProcessor( Entry entry, Property entityProperty) {
		MSGlycolipidAnnotationTableDataProcessor proc = new MSGlycolipidAnnotationTableDataProcessor(
				entry, entityProperty, 
				FillTypes.Scans, getMinMSLevel() );
		proc.initializeTableDataObject(entityProperty);
		return proc;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationScansView#getNewTableDataProcessor(org.grits.toolbox.core.datamodel.property.Property)
	 */
	@Override
	protected TableDataProcessor getNewTableDataProcessor(Property entityProperty) {		
		MSGlycanAnnotationMultiPageViewer parentViewer = MSGlycanAnnotationMultiPageViewer.getActiveViewerForEntry(getPart().getContext(), getEntry().getParent());
		if( parentViewer == null || parentViewer.getScansView() == null ) {
			return null;
		}
		TableDataProcessor parentProc = parentViewer.getScansView().getTableDataProcessor();
		if( parentProc == null ) 
			return null;
//		if ( ! parentProc.getSourceProperty().equals(entityProperty) ) {
//			return null;
//		}
		MSGlycolipidAnnotationTableDataProcessor proc = new MSGlycolipidAnnotationTableDataProcessor(parentProc, entityProperty, 
				FillTypes.Scans, getMinMSLevel());
		proc.initializeTableDataObject(entityProperty);
		return proc;
	}	
}
