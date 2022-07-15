package org.grits.toolbox.tools.gsl.annotation.entry.property;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.grits.toolbox.core.utilShare.XMLUtils;
import org.grits.toolbox.entry.ms.annotation.glycan.property.MSGlycanAnnotationProperty;
import org.grits.toolbox.entry.ms.annotation.glycan.property.datamodel.MSGlycanAnnotationMetaData;
import org.grits.toolbox.entry.ms.annotation.property.MSAnnotationProperty;
import org.grits.toolbox.entry.ms.annotation.property.datamodel.MSAnnotationMetaData;
import org.grits.toolbox.tools.gsl.Activator;
import org.grits.toolbox.tools.gsl.annotation.entry.property.datamodel.MSGlycolipidAnnotationMetaData;

/**
 * An extension of the MSGlycolipidAnnotationProperty class in order to support MSGlycolipidAnnotation data.
 * @author Masaaki Matsubara
 *
 */
public class MSGlycolipidAnnotationProperty extends MSGlycanAnnotationProperty
{
	private static final Logger logger = Logger.getLogger(MSGlycolipidAnnotationProperty.class);
	public static final String CURRENT_VERSION = "1.0";
	public static final String TYPE = "org.grits.toolbox.property.ms_annotation.glycolipid";
//	private static ImageDescriptor imageDescriptor = ImageRegistry.getImageDescriptor(Activator.PLUGIN_ID, ImageRegistry.MSImage.MSANNOTATION_ICON);
	private static ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL( FileLocator.find(
			Platform.getBundle(Activator.PLUGIN_ID), new Path("icons" + File.separator + "IconAnnotation.png"), null));
	private static final String ARCHIVE_EXTENSION = ".zip";
	private static final String ARCHIVE_FOLDER = "glycolipid_annotation";

	public MSGlycolipidAnnotationProperty()
	{
		super();
		setVersion(CURRENT_VERSION);
	}

	@Override
	public boolean equals(Object obj) {
		if ( ! (obj instanceof MSGlycolipidAnnotationProperty) )
			return false;

		return super.equals(obj);
	}

	@Override
	public Object clone() {
		MSGlycolipidAnnotationProperty newProp = new MSGlycolipidAnnotationProperty();
		if ( getMSAnnotationMetaData() != null ) {
			MSGlycolipidAnnotationMetaData settings = (MSGlycolipidAnnotationMetaData) getMSAnnotationMetaData().clone();
			newProp.setMSAnnotationMetaData(settings);
		}
		return newProp;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.property.MSGlycanAnnotationProperty#getType()
	 */
	@Override
	public String getType() {
		return MSGlycolipidAnnotationProperty.TYPE;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.property.MSGlycanAnnotationProperty#getImage()
	 */
	@Override
	public ImageDescriptor getImage() {
		return MSGlycolipidAnnotationProperty.imageDescriptor;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.property.MSGlycanAnnotationProperty#getArchiveExtension()
	 */
	@Override
	public String getArchiveExtension() {
		return MSGlycolipidAnnotationProperty.ARCHIVE_EXTENSION;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.property.MSGlycanAnnotationProperty#getArchiveFolder()
	 */
	@Override
	public String getArchiveFolder() {
		return MSGlycolipidAnnotationProperty.ARCHIVE_FOLDER;
	}

	/**
	 * Get MSGlycolipidAnnotationMetaData from .xml file.
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.property.MSGlycanAnnotationProperty#unmarshallSettingsFile(String)
	 * @param sFileName String of the .xml file name
	 * @return MSGlycolipidAnnotationMetaData
	 */
	public static MSAnnotationMetaData unmarshallSettingsFile( String sFileName ) {
		MSAnnotationMetaData metaData = null;
		try {
			metaData = (MSAnnotationMetaData) XMLUtils.unmarshalObjectXML(sFileName, MSGlycolipidAnnotationMetaData.class);

		} catch (Exception e ) {
			logger.error(e.getMessage(), e);
		}
		return metaData;
	}
	
	@Override
	protected MSAnnotationProperty getNewAnnotationProperty(String msAnnotationFolder) {
		MSGlycolipidAnnotationProperty t_property = new MSGlycolipidAnnotationProperty();
		MSGlycolipidAnnotationMetaData metaData = new MSGlycolipidAnnotationMetaData();		
		t_property.setMSAnnotationMetaData(metaData);
		try {
			metaData.setAnnotationId(createRandomId(msAnnotationFolder));
			metaData.setVersion(MSGlycolipidAnnotationMetaData.CURRENT_VERSION);
			metaData.setName(metaData.getAnnotationId() + "." + getMetaDataFileExtension());
		} catch (IOException e2) {
			logger.error(e2.getMessage(), e2);
			return null;
		}

		return t_property;
	}
}
