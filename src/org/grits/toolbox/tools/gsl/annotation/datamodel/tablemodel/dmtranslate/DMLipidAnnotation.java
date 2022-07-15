package org.grits.toolbox.tools.gsl.annotation.datamodel.tablemodel.dmtranslate;

/**
 * Abstraction for the LipidAnnotation class in the GRITS object model. Note that LipidAnnotation extends Annotation, and there 
 * is a new field for lipid name. Here they are just named lipid-centric for readability. 
 * 
 * @author Masaaki Matsubara
 * 
 */
public enum DMLipidAnnotation {

	lipid_annotation_id("Annotation Id", "The auto-assigned ID of the object in the annotation database."),
	lipid_annotation_type("Annotation Type", "The system-assigned type of annotation used."),
	lipid_annotation_name("Lipid Name", "If applicable, the sequence representation of the object in the database.");

	private String sLabel;
	private String sDescription;

	private DMLipidAnnotation( String sLabel, String sDescription ) {
		this.sLabel = sLabel;
		this.sDescription = sDescription;
	}

	public String getDescription() {
 		return sDescription;
 	}

	public String getLabel() {
		return this.sLabel;
	}

	public static DMLipidAnnotation lookUp( String _sKey ) {
		for ( DMLipidAnnotation t_dm : DMLipidAnnotation.values() ) {
			if ( t_dm.name().equals(_sKey) )
				return t_dm;
		}
		return null;
	}

}
