package org.grits.toolbox.tools.gsl.report.entry.property;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.grits.toolbox.core.datamodel.io.PropertyWriter;
import org.grits.toolbox.core.utilShare.XMLUtils;
import org.grits.toolbox.entry.ms.annotation.glycan.report.property.MSGlycanAnnotationReportProperty;
import org.grits.toolbox.tools.gsl.Activator;
import org.grits.toolbox.tools.gsl.report.entry.property.datamodel.MSGlycolipidAnnotationReportMetaData;
import org.grits.toolbox.tools.gsl.report.entry.property.io.MSGlycolipidAnnotationReportPropertyWriter;

/**
 * Extends MSGlycanAnnotationReportProterty for MSGlycolipidAnnotationReport entry.
 * @author Masaaki Matsubara
 *
 */
public class MSGlycolipidAnnotationReportProperty extends MSGlycanAnnotationReportProperty {
	private static final Logger logger = Logger.getLogger(MSGlycolipidAnnotationReportProperty.class);

	public static final String TYPE = "org.grits.toolbox.property.report.ms_annotation_merge.glycolipid";
	protected static PropertyWriter writer = new MSGlycolipidAnnotationReportPropertyWriter();
	public static final String ARCHIVE_FOLDER = "merge_glycolipid";
	public static final String ARCHIVE_EXTENSION = ".xml";
	private static ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL( FileLocator.find(
			Platform.getBundle(Activator.PLUGIN_ID), new Path("icons" + File.separator + "merge.png"), null));
	private static final String META_DATA_FILE = "msGlycolipidAnnotReportMetaData.xml";

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.report.property.MSGlycanAnnotationReportProperty#getType()
	 */
	@Override
	public String getType() {
		return MSGlycolipidAnnotationReportProperty.TYPE;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.report.property.MSGlycanAnnotationReportProperty#getWriter()
	 */
	@Override
	public PropertyWriter getWriter() {
		return MSGlycolipidAnnotationReportProperty.writer;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.report.property.MSGlycanAnnotationReportProperty#getImage()
	 */
	@Override
	public ImageDescriptor getImage() {
		return MSGlycolipidAnnotationReportProperty.imageDescriptor;
	}

	/**
	 * Unmarshall MSGlycolipidAnnotatioanReportMetadata from .xml file.
	 * @param sFileName String of settings .xml file name
	 * @return MSGlycolipidAnnotatioanReportMetadata
	 */
	public static MSGlycolipidAnnotationReportMetaData unmarshallSettingsFile(String sFileName) {
		MSGlycolipidAnnotationReportMetaData metaData = null;
		try {
			metaData = (MSGlycolipidAnnotationReportMetaData) XMLUtils.unmarshalObjectXML(sFileName,
					MSGlycolipidAnnotationReportMetaData.class);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return metaData;
	}

	/**
	 * Marshall MSGlycolipidAnnotationReportMetaData to .xml file.
	 * @param sFileName String of settings .xml file name
	 * @param metaData MSGlycolipidAnnotationReportMetadata to be marshalled
	 */
	public static void marshallSettingsFile(String sFileName, MSGlycolipidAnnotationReportMetaData metaData) {
		try {
			String xmlString = XMLUtils.marshalObjectXML(metaData);
			// write the serialized data to the folder
			FileWriter fileWriter = new FileWriter(sFileName);
			fileWriter.write(xmlString);
			fileWriter.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.report.property.MSGlycanAnnotationReportProperty#getArchiveFile()
	 */
	@Override
	public String getArchiveFile() {
		return getMsGlycanAnnotReportMetaData().getReportId() + MSGlycolipidAnnotationReportProperty.ARCHIVE_EXTENSION;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.report.property.MSGlycanAnnotationReportProperty#getArchiveExtension()
	 */
	@Override
	public String getArchiveExtension() {
		return MSGlycolipidAnnotationReportProperty.ARCHIVE_EXTENSION;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.report.property.MSGlycanAnnotationReportProperty#getArchiveFolder()
	 */
	@Override
	public String getArchiveFolder() {
		return MSGlycolipidAnnotationReportProperty.ARCHIVE_FOLDER;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.report.property.MSGlycanAnnotationReportProperty#getMetaDataFileName()
	 */
	@Override
	public String getMetaDataFileName() {
		return getMsGlycanAnnotReportMetaData().getReportId() + "." + MSGlycolipidAnnotationReportProperty.META_DATA_FILE;
	}


}
