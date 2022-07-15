package org.grits.toolbox.tools.gsl.report.entry.property.datamodel;

import javax.xml.bind.annotation.XmlRootElement;

import org.grits.toolbox.entry.ms.annotation.glycan.report.property.datamodel.MSGlycanAnnotationReportMetaData;

/**
 * Extends MSGlycanAnnotationReportMetaData for MSGlycolipidAnnotationReport entry.
 * @author Masaaki Matsubara
 *
 */
@XmlRootElement(name = "msGlycolipidAnnotationReportMetaData")
public class MSGlycolipidAnnotationReportMetaData extends MSGlycanAnnotationReportMetaData {
	public static final String CURRENT_VERSION = "1.0";

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		MSGlycolipidAnnotationReportMetaData newSettings = new MSGlycolipidAnnotationReportMetaData();
		
		newSettings.setDescription(this.getDescription());
		newSettings.setReportId(this.getReportId());
		newSettings.setVersion(this.getVersion());
		newSettings.setDescription(this.getDescription());
		newSettings.setCreationDate(this.getCreationDate());
		newSettings.setUpdateDate(newSettings.getCreationDate());
		return newSettings;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if ( ! (obj instanceof MSGlycolipidAnnotationReportMetaData) )
			return false;
		return super.equals(obj);
	}

}
