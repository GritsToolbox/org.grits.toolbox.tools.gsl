package org.grits.toolbox.tools.gsl.annotation.entry.views.tabbed;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.core.datamodel.property.Property;
import org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationDetails;
import org.grits.toolbox.entry.ms.annotation.property.MSAnnotationEntityProperty;
import org.grits.toolbox.entry.ms.annotation.views.tabbed.MSAnnotationMultiPageViewer;
import org.grits.toolbox.entry.ms.views.tabbed.MassSpecMultiPageViewer;
import org.grits.toolbox.utils.data.CartoonOptions;

/**
 * Class for displaying the glycolipid annotation details.
 * Extends MSGlycanAnnotationDetails to get new MSGlycolipidAnnotationPeaksView.
 * @see org.grits.toolbox.tools.gsl.annotation.entry.views.tabbed.MSGlycolipidAnnotationPeaksView
 * @author Masaaki Matsubara
 *
 */
public class MSGlycolipidAnnotationDetailsView extends MSGlycanAnnotationDetails {
//	private static final Logger logger = Logger.getLogger(MSGlycolipidAnnotationDetailsView.class);
	public static final String VIEW_ID = "ms.annotation.glycan.views.MSGlycolipidAnnotationDetails";

	@Inject
	public MSGlycolipidAnnotationDetailsView(MSAnnotationMultiPageViewer parentViewer, Entry entry, 
			Property msEntityProperty, CartoonOptions cartoonOptions, 
			@Named(MassSpecMultiPageViewer.MIN_MS_LEVEL_CONTEXT) int iMinMSLevel) {
		super(parentViewer, entry, msEntityProperty, cartoonOptions, iMinMSLevel);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationDetails#getParentMultiPageViewer()
	 */
	@Override
	protected MSGlycolipidAnnotationMultiPageViewer getParentMultiPageViewer() {
		return (MSGlycolipidAnnotationMultiPageViewer)super.getParentMultiPageViewer();
	}

	@Override
	public String toString() {
		return "MSGlycolipidAnnotationDetailsView (" + entry + ")";
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationDetails#getNewPeaksView(org.grits.toolbox.core.datamodel.Entry, org.grits.toolbox.entry.ms.annotation.property.MSAnnotationEntityProperty)
	 */
	@Override
	protected MSGlycolipidAnnotationPeaksView getNewPeaksView( Entry entry, MSAnnotationEntityProperty msEntityProperty ) {
		getPart().getContext().set(MassSpecMultiPageViewer.MIN_MS_LEVEL_CONTEXT, getMinMSLevel());
		getPart().getContext().set(Property.class, msEntityProperty);
		getPart().getContext().set(Entry.class, entry);
		MSGlycolipidAnnotationPeaksView view = ContextInjectionFactory.make(MSGlycolipidAnnotationPeaksView.class, getPart().getContext());
		return view;
	}

}
