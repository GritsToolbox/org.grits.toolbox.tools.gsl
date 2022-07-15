package org.grits.toolbox.tools.gsl.report.entry.property.io;

import java.io.File;

import org.grits.toolbox.core.dataShare.PropertyHandler;
import org.grits.toolbox.core.datamodel.property.Property;
import org.grits.toolbox.core.datamodel.property.ReportsProperty;
import org.grits.toolbox.tools.gsl.report.entry.property.MSGlycolipidAnnotationReportProperty;
import org.grits.toolbox.tools.gsl.report.entry.property.datamodel.MSGlycolipidAnnotationReportMetaData;
import org.jdom.Element;

/**
 * Property reader for MSGlycolipidAnnotationReport ver_1
 * @author Masaaki Matsubara
 *
 */
public class MSGlycolipidAnnotationReportPropertyReaderVer1 {

	/**
	 * Reads MSGlycolipidAnnotationReportMetaData from the given Element and
	 * sets it to the given MSGlycolipidAnnotationReportProperty
	 * @param propertyElement entry Element
	 * @param property MSGlycolipidAnnotationReportProperty to be stored meta data
	 * @return MSGlycolipidAnnotationReportProperty having the meta data
	 */
	public static Property read(Element propertyElement, MSGlycolipidAnnotationReportProperty property) {
		property.adjustPropertyFilePaths();
		Element entryElement = propertyElement.getDocument().getRootElement().getChild("entry");
		String projectName = entryElement == null ? null : entryElement.getAttributeValue("name");


		String workspaceFolder = PropertyHandler.getVariable("workspace_location");
		String reportFolder = workspaceFolder.substring(0, workspaceFolder.length()-1) 
				+ File.separator + projectName
				+ File.separator + ReportsProperty.getFolder()
				+ File.separator + MSGlycolipidAnnotationReportProperty.ARCHIVE_FOLDER;
				
		// lets read the settings file
		String settingsFile = property.getMetaDataFile().getName();
		String fullPath = reportFolder + File.separator + settingsFile;
		MSGlycolipidAnnotationReportMetaData reportMetaData = MSGlycolipidAnnotationReportProperty.unmarshallSettingsFile(fullPath);
		property.setMsGlycanAnnotReportMetaData(reportMetaData);
		return property;
	}
}
