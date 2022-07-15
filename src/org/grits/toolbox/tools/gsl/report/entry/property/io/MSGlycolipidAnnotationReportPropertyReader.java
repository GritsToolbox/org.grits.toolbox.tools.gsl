package org.grits.toolbox.tools.gsl.report.entry.property.io;

import java.io.IOException;

import org.grits.toolbox.core.datamodel.UnsupportedVersionException;
import org.grits.toolbox.core.datamodel.io.PropertyReader;
import org.grits.toolbox.core.datamodel.property.Property;
import org.grits.toolbox.tools.gsl.report.entry.property.MSGlycolipidAnnotationReportProperty;
import org.jdom.Element;

/**
 * Reader for MSGlycolipidAnnotationReport sample entry.
 * @author Masaaki Matsubara
 *
 */
public class MSGlycolipidAnnotationReportPropertyReader extends PropertyReader {

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.core.datamodel.io.PropertyReader#read(org.jdom.Element)
	 */
	@Override
	public Property read(Element propertyElement) throws IOException, UnsupportedVersionException
	{
		MSGlycolipidAnnotationReportProperty property = getNewMSGlycolipidAnnotationReportProperty();
		
		PropertyReader.addGenericInfo(propertyElement, property);

		if(property.getVersion().equals("1.0")) {
			MSGlycolipidAnnotationReportPropertyReaderVer1.read(propertyElement, property);
		}
		else 
			throw new UnsupportedVersionException("This version is currently not supported.", property.getVersion());
		return property;
	}
	
	protected MSGlycolipidAnnotationReportProperty getNewMSGlycolipidAnnotationReportProperty() {
		return new MSGlycolipidAnnotationReportProperty();
	}
}
