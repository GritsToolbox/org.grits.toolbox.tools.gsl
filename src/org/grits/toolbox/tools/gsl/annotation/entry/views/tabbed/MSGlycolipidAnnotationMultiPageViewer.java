package org.grits.toolbox.tools.gsl.annotation.entry.views.tabbed;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.core.datamodel.UnsupportedVersionException;
import org.grits.toolbox.core.datamodel.property.Property;
import org.grits.toolbox.core.preference.share.IGritsPreferenceStore;
import org.grits.toolbox.core.preference.share.PreferenceEntity;
import org.grits.toolbox.display.control.table.preference.TableViewerPreference;
import org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationMultiPageViewer;
import org.grits.toolbox.entry.ms.annotation.property.MSAnnotationEntityProperty;
import org.grits.toolbox.entry.ms.annotation.views.tabbed.MSAnnotationDetails;
import org.grits.toolbox.entry.ms.property.MassSpecEntityProperty;
import org.grits.toolbox.entry.ms.views.tabbed.MassSpecMultiPageViewer;
import org.grits.toolbox.entry.ms.views.tabbed.MassSpecSpectraView;
import org.grits.toolbox.tools.gsl.annotation.datamodel.preference.MSGlycolipidAnnotationSummaryViewerPreference;
import org.grits.toolbox.tools.gsl.annotation.datamodel.preference.MSGlycolipidAnnotationViewerPreference;
import org.grits.toolbox.tools.gsl.annotation.entry.property.MSGlycolipidAnnotationEntityProperty;
import org.grits.toolbox.utils.data.CartoonOptions;

/**
 * A tabbed-editor for displaying information for MS Glycolipid Annotation Data.<br>
 * This editor extends MSGlycanAnnotationMultiPageViewer.
 * 
 * @author Masaaki Matsubara
 * @see MSGlycolipidAnnotationSpectraView
 * @see MSGlycolipidAnnotationPropertyView
 * @see MSGlycolipidAnnotationPeaksView
 * @see MSGlycolipidAnnotationScansView
 * @see MSGlycolipidAnnotationDetailsView
 * @see MSGlycolipidAnnotationSummaryView
 *
 */
public class MSGlycolipidAnnotationMultiPageViewer extends MSGlycanAnnotationMultiPageViewer {

	public static String VIEW_ID = "plugin.ms.annotation.glycan.views.MSGlycolipidAnnotationMultiPageViewer";
	private static final Logger logger = Logger.getLogger(MSGlycolipidAnnotationMultiPageViewer.class);

	@Inject
	public MSGlycolipidAnnotationMultiPageViewer( Entry entry ) {
		super(entry);
	}
	
	@Inject
	public MSGlycolipidAnnotationMultiPageViewer( MPart part ) {
		super(part);
	}

	@Override
	public String toString() {
		return "MSGlycolipidAnnotationMultiPageViewer (" + entry + ")";
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationMultiPageViewer#getNewSpectraView()
	 */
	@Override
	protected MassSpecSpectraView getNewSpectraView() {
		getPart().getContext().set(Entry.class, this.entry);
		return ContextInjectionFactory.make(MSGlycolipidAnnotationSpectraView.class, getPart().getContext());
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationMultiPageViewer#initMSAnnotationPropertyView()
	 */
	@Override
	protected boolean initMSAnnotationPropertyView() {
		try {
			getPart().getContext().set(Entry.class, entry);
			this.msAnnotPropertyView = ContextInjectionFactory.make(MSGlycolipidAnnotationPropertyView.class, getPart().getContext());
			return true;
		} catch( Exception ex ) {
			logger.error("Unable to open property view", ex);
		}		
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationMultiPageViewer#getNewSummaryView(org.grits.toolbox.core.datamodel.Entry, org.grits.toolbox.entry.ms.property.MassSpecEntityProperty)
	 */
	@Override
	protected MSGlycolipidAnnotationSummaryView getNewSummaryView( Entry entry, MassSpecEntityProperty entityProperty) {
		MSGlycanAnnotationMultiPageViewer parent = MSGlycanAnnotationMultiPageViewer.getActiveViewerForEntry(getContext(), entry.getParent());
		if ( parent != null ) {		
			getPart().getContext().set(MSAnnotationDetails.class, getDetailsView());
			// TODO: Creation MSGlycolipidAnnotationSummaryView 
			MSGlycolipidAnnotationSummaryView view = ContextInjectionFactory.make(MSGlycolipidAnnotationSummaryView.class, getPart().getContext());
			view.setDtpdThreadedDialog(getThreadedDialog());
			return view;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationMultiPageViewer#getNewScansView(org.grits.toolbox.core.datamodel.Entry, org.grits.toolbox.entry.ms.property.MassSpecEntityProperty)
	 */
	@Override
	protected MSGlycolipidAnnotationScansView getNewScansView( Entry entry, MassSpecEntityProperty entityProperty ) {
		MSAnnotationEntityProperty msProp = (MSAnnotationEntityProperty) entityProperty.clone();
		msProp.setParentScanNum( entityProperty.getScanNum() );
		msProp.setScanNum(null);
		getPart().getContext().set(MIN_MS_LEVEL_CONTEXT, getMinMSLevel());
		getPart().getContext().set(Property.class, msProp);
		getPart().getContext().set(Entry.class, entry);
		return ContextInjectionFactory.make(MSGlycolipidAnnotationScansView.class, getPart().getContext());
				//new MSGlycanAnnotationScansView(getContainer(), entry, msProp, getMinMSLevel());
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationMultiPageViewer#getNewPeaksView(org.grits.toolbox.core.datamodel.Entry, org.grits.toolbox.entry.ms.property.MassSpecEntityProperty)
	 */
	@Override
	protected MSGlycolipidAnnotationPeaksView getNewPeaksView( Entry entry, MassSpecEntityProperty entityProperty) {
		getPart().getContext().set(MIN_MS_LEVEL_CONTEXT, getMinMSLevel());
		getPart().getContext().set(Property.class, entityProperty);
		getPart().getContext().set(Entry.class, entry);
		return ContextInjectionFactory.make(MSGlycolipidAnnotationPeaksView.class, getPart().getContext());
				//new MSGlycanAnnotationPeaksView( getContainer(), entry, (MSAnnotationEntityProperty) entityProperty, getMinMSLevel());
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationMultiPageViewer#getNewDetailsView(org.grits.toolbox.core.datamodel.Entry, org.grits.toolbox.entry.ms.property.MassSpecEntityProperty)
	 */
	@Override
	protected MSGlycolipidAnnotationDetailsView getNewDetailsView( Entry entry, MassSpecEntityProperty entityProperty) {
		//		return new MassSpecPeaksView(entry, entityProperty);
		MSGlycanAnnotationMultiPageViewer parent = MSGlycanAnnotationMultiPageViewer.getActiveViewerForEntry(getContext(), entry.getParent());
		if ( parent != null ) {
			CartoonOptions cOptions = getCartoonOptions();	
			getPart().getContext().set(MSGlycolipidAnnotationMultiPageViewer.class, (MSGlycolipidAnnotationMultiPageViewer)parent);
			getPart().getContext().set(Entry.class, entry);
			getPart().getContext().set(Property.class, entityProperty);
			getPart().getContext().set(CartoonOptions.class, cOptions);
			getPart().getContext().set(MassSpecMultiPageViewer.MIN_MS_LEVEL_CONTEXT, getMinMSLevel());
			MSGlycolipidAnnotationDetailsView view = ContextInjectionFactory.make(MSGlycolipidAnnotationDetailsView.class, getPart().getContext());
					//new MSGlycanAnnotationDetails(parent, entry, (MSAnnotationEntityProperty) entityProperty, cOptions, getMinMSLevel());
			//		view.updateFeature(entry, (MSAnnotationEntityProperty) entityProperty, cOptions);
			return view;
		}
		return null;
	}

	@Optional @Inject
	public void updatePreferences(@UIEventTopic(IGritsPreferenceStore.EVENT_TOPIC_PREF_VALUE_CHANGED)
	 					String preferenceName)
	{
		if(preferenceName != null && preferenceName.startsWith(MSGlycolipidAnnotationViewerPreference.class.getName())) {
	 		PreferenceEntity preferenceEntity;
			try {
				preferenceEntity = gritsPreferenceStore.getPreferenceEntity(preferenceName);
			
				MSGlycolipidAnnotationViewerPreference updatePref
					= (MSGlycolipidAnnotationViewerPreference) TableViewerPreference.getTableViewerPreference(
						preferenceEntity, MSGlycolipidAnnotationViewerPreference.class);
				this.updateColumnVisibility(updatePref);
			} catch (UnsupportedVersionException e) {
				logger.error("Error updating column visibility", e);
			}
		}
		
		if(preferenceName != null && preferenceName.startsWith(MSGlycolipidAnnotationSummaryViewerPreference.class.getName())) {
	 		PreferenceEntity preferenceEntity;
			try {
				preferenceEntity = gritsPreferenceStore.getPreferenceEntity(preferenceName);
			
				MSGlycolipidAnnotationSummaryViewerPreference updatePref
					= (MSGlycolipidAnnotationSummaryViewerPreference) TableViewerPreference.getTableViewerPreference(
						preferenceEntity, MSGlycolipidAnnotationSummaryViewerPreference.class);
				this.updateColumnVisibility(updatePref);
			} catch (UnsupportedVersionException e) {
				logger.error("Error updating column visibility", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationMultiPageViewer#getNewTableCompatibleEntry(org.grits.toolbox.core.datamodel.Entry)
	 */
	@Override
	protected Entry getNewTableCompatibleEntry(Entry parentEntry) {
		Entry newEntry = MSGlycolipidAnnotationEntityProperty.getTableCompatibleEntry(parentEntry);	
		return newEntry;
	}
}
