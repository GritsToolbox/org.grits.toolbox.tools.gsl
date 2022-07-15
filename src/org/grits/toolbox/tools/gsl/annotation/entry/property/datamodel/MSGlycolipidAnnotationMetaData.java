package org.grits.toolbox.tools.gsl.annotation.entry.property.datamodel;

import javax.xml.bind.annotation.XmlRootElement;

import org.grits.toolbox.entry.ms.annotation.glycan.property.datamodel.MSGlycanAnnotationMetaData;

/**
 * Extends MSGlycanAnnotationMetaData to use for MSGlycolipidAnnotation entry.
 * Also overrides clone() and equal() methods.
 * @author Masaaki Matsubara
 *
 */
@XmlRootElement(name = "msGlycolipidAnnotationMetaData")
public class MSGlycolipidAnnotationMetaData extends MSGlycanAnnotationMetaData {
	public static final String CURRENT_VERSION = "1.0";

	public MSGlycolipidAnnotationMetaData() {
		super();
	}

	@Override
	public Object clone() {
		MSGlycolipidAnnotationMetaData newSettings = new MSGlycolipidAnnotationMetaData();
		super.cloneSettings(newSettings);
//		newSettings.setDescription(this.getDescription());
//		newSettings.setAnnotationId(this.getAnnotationId());
//		newSettings.setVersion(this.getVersion());
//		newSettings.setDescription(this.getDescription());
//		newSettings.setCustomAnnotationText(customAnnotationsText);
//		newSettings.setCustomAnnotationText(this.getCustomAnnotationText());
		newSettings.updateCustomAnnotationList();
//		newSettings.setAnnotationFile(this.getAnnotationFile());
		return newSettings;
	}

	@Override
	public boolean equals(Object obj) {
		if ( ! (obj instanceof MSGlycolipidAnnotationMetaData) )
			return false;
		
		return super.equals(obj);
	}

}
