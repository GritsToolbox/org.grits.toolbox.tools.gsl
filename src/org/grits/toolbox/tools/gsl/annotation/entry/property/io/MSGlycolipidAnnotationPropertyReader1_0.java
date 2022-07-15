package org.grits.toolbox.tools.gsl.annotation.entry.property.io;

import java.io.File;

import org.grits.toolbox.core.dataShare.PropertyHandler;
import org.grits.toolbox.core.datamodel.property.Property;
import org.grits.toolbox.entry.ms.annotation.property.datamodel.MSAnnotationMetaData;
import org.grits.toolbox.tools.gsl.annotation.entry.property.MSGlycolipidAnnotationProperty;
import org.jdom.Element;

/**
 * Property reader for MSGlycolipidAnnotationProperty ver_1.0
 * @see org.grits.toolbox.tools.gsl.annotation.entry.property.MSGlycolipidAnnotationProperty
 * @author Masaaki Matsubara
 *
 */
public class MSGlycolipidAnnotationPropertyReader1_0 {

	/**
	 * Read Property from a file specified by the given Element and MSGlycolipidAnnotationProperty
	 * @param propertyElement Element of the property
	 * @param msProperty MSGlycolipidAnnotation
	 * @return MSGlycolipidAnnotationProperty with meta data read from the specified file path
	 */
	public static Property read(Element propertyElement, MSGlycolipidAnnotationProperty msProperty) {
		msProperty.adjustPropertyFilePaths();
		Element entryElement = propertyElement.getDocument().getRootElement().getChild("entry");
		String projectName = entryElement == null ? null : entryElement.getAttributeValue("name");
		String workspaceFolder = PropertyHandler.getVariable("workspace_location");
		String msFolder = workspaceFolder.substring(0, workspaceFolder.length()-1) 
				+ File.separator
				+ projectName + File.separator
				+ msProperty.getArchiveFolder();
		String msFile = msProperty.getMetaDataFile().getName();
		String fullPath = msFolder + File.separator + msFile;
		MSAnnotationMetaData msMetaData = MSGlycolipidAnnotationProperty.unmarshallSettingsFile(fullPath);
		// fix the file paths here!
		msMetaData.adjustPropertyFilePaths();
		msProperty.setMSAnnotationMetaData(msMetaData);
		
		return msProperty;
	}
}
