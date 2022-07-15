package org.grits.toolbox.tools.gsl.annotation.entry.property;

import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.entry.ms.annotation.glycan.property.MSGlycanAnnotationEntityProperty;
import org.grits.toolbox.entry.ms.annotation.property.MSAnnotationEntityProperty;
import org.grits.toolbox.entry.ms.annotation.property.MSAnnotationProperty;
import org.grits.toolbox.entry.ms.property.MassSpecEntityProperty;
import org.grits.toolbox.entry.ms.property.MassSpecProperty;

/**
 * Extends MSGlycanAnnotationEntityProperty for MSGlycolipidAnnotation entry.
 * @author Masaaki Matsubara
 *
 */
public class MSGlycolipidAnnotationEntityProperty extends MSGlycanAnnotationEntityProperty {
	public static final String TYPE = MSGlycolipidAnnotationEntityProperty.class.getName();

	public MSGlycolipidAnnotationEntityProperty(MassSpecProperty msParentProperty,
			MSAnnotationProperty annotParentProperty) {
		super(msParentProperty, annotParentProperty);
	}

	/**
	 * Get table compatible Entry having MSGlycolipidEntityProperty as the property from given parent Entry.
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.property.MSGlycanAnnotationEntityProperty#getTableCompatibleEntry(Entry)
	 * @param parentEntry parent Entry
	 * @return Entry having MSGlycolipidEntityProperty
	 */
	public static Entry getTableCompatibleEntry( Entry parentEntry ) {
		Entry newEntry = MassSpecEntityProperty.getTableCompatibleEntry(parentEntry);
				
		Entry msAnnotEntry = MSAnnotationProperty.getFirstAnnotEntry(parentEntry);
		MSAnnotationProperty msAnnotProp = null;
		MSAnnotationEntityProperty msAnnotEntityProp = null;
		if( msAnnotEntry != null ) {
			msAnnotProp = (MSAnnotationProperty) msAnnotEntry.getProperty();
			MassSpecEntityProperty msEntityProp = (MassSpecEntityProperty) newEntry.getProperty();
			msAnnotEntityProp = new MSGlycolipidAnnotationEntityProperty((MassSpecProperty) msEntityProp.getMassSpecParentProperty(), msAnnotProp);
			newEntry.setProperty(msAnnotEntityProp);
			newEntry.setDisplayName(parentEntry.getDisplayName());
		} 		
		return newEntry;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.property.MSGlycanAnnotationEntityProperty#clone()
	 */
	@Override
	public Object clone() {
		MSGlycolipidAnnotationEntityProperty newProp = new MSGlycolipidAnnotationEntityProperty(this.getMassSpecParentProperty(), this.getMSAnnotationParentProperty());
		newProp.setDescription(this.getDescription());
		newProp.setId(this.getId());
		newProp.setAnnotationId(this.getAnnotationId());
		newProp.setScanNum(this.getScanNum());
		newProp.setMsLevel(this.getMsLevel());
		newProp.setParentScanNum(this.getParentScanNum());
		return newProp;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.property.MSGlycanAnnotationEntityProperty#getType()
	 */
	@Override
	public String getType() {
		return MSGlycolipidAnnotationEntityProperty.TYPE;
	}
	
	
}
