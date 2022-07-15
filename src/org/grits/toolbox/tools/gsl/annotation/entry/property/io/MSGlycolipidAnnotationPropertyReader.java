package org.grits.toolbox.tools.gsl.annotation.entry.property.io;

import java.io.File;
import java.io.IOException;

import org.grits.toolbox.core.dataShare.PropertyHandler;
import org.grits.toolbox.core.datamodel.UnsupportedVersionException;
import org.grits.toolbox.core.datamodel.io.PropertyReader;
import org.grits.toolbox.core.datamodel.property.Property;
import org.grits.toolbox.entry.ms.annotation.property.MSAnnotationProperty;
import org.grits.toolbox.entry.ms.property.datamodel.MSPropertyDataFile;
import org.grits.toolbox.tools.gsl.annotation.entry.property.MSGlycolipidAnnotationProperty;
import org.jdom.Element;

/**
 * PropertyReader for MSGlycolipidAnnotationProperty.
 * @see org.grits.toolbox.tools.gsl.annotation.entry.property.MSGlycolipidAnnotationProperty
 * @author Masaaki Matsubara
 *
 */
public class MSGlycolipidAnnotationPropertyReader extends PropertyReader {

	protected MSAnnotationProperty getNewMSAnnotationProperty() {
		return new MSGlycolipidAnnotationProperty();
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.core.datamodel.io.PropertyReader#read(org.jdom.Element)
	 */
	@Override
	public Property read(Element propertyElement) throws UnsupportedVersionException, IOException {
		
		MSGlycolipidAnnotationProperty property = (MSGlycolipidAnnotationProperty) getNewMSAnnotationProperty();
		
		PropertyReader.addGenericInfo(propertyElement, property);
		
		if(property.getVersion().equals("1.0")) {
			MSGlycolipidAnnotationPropertyReader1_0.read(propertyElement, property);
		}
		else 
			throw new UnsupportedVersionException("This version is currently not supported.", property.getVersion());
		
		// need to adjust source file path between different operating systems
		adjustSourceFileListFilePaths(propertyElement, property);
		
		return property;
	}
	
	private void adjustSourceFileListFilePaths(Element propertyElement, MSGlycolipidAnnotationProperty property) {
		boolean changed = false;
		if (property.getMSAnnotationMetaData() != null) {
			if (property.getMSAnnotationMetaData().getSourceDataFileList() != null) {
				for (MSPropertyDataFile file : property.getMSAnnotationMetaData().getSourceDataFileList()) {
					if( file.getName().contains("\\") && ! File.separator.equals("\\") ) {
						file.setName( file.getName().replace("\\", File.separator));
						changed = true;
					} else if( file.getName().contains("/") && ! File.separator.equals("/") ){
						file.setName( file.getName().replace("/", File.separator));
						changed = true;
					}
				}
				if (changed) {
					Element entryElement = propertyElement.getDocument().getRootElement().getChild("entry");
					String projectName = entryElement == null ? null : entryElement.getAttributeValue("name");
					String workspaceFolder = PropertyHandler.getVariable("workspace_location");
					String msFolder = workspaceFolder.substring(0, workspaceFolder.length()-1) 
							+ File.separator
							+ projectName + File.separator
							+ property.getArchiveFolder();
					String msFile = property.getMetaDataFile().getName();
					String fullPath = msFolder + File.separator + msFile;
					MSGlycolipidAnnotationProperty.marshallSettingsFile(fullPath, property.getMSAnnotationMetaData());
				}
			}
		}
		
	}
}
