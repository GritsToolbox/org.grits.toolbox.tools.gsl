package org.grits.toolbox.tools.gsl.annotation.datamodel.tablemodel.dmtranslate;

/**
 * Abstraction for the GlycolipidFeature class in the GRITS object model. Note that GlycolipidFeature extends Feature, and there 
 * is a new field for lipid name. Here they are just named glycolipid-centric for readability. 
 * 
 * @author Masaaki Matsubara
 * 
 */
public enum DMGlycolipidFeature {

	glycolipid_feature_id("Glycolipid Feature Id", "The auto-assigned ID of the glycolipid object assigned to a scan."),
	glycolipid_feature_type("Glycolipid Type", "The system-assigned type of glycolipid assigned."),
	glycolipid_feature_sequence("Glycan Sequence", "The sequence of the annotation object associated with this glycan part."),
	glycolipid_feature_mz("Glycolipid m/z", "The observed m/z of the glycolipid."),
	glycolipid_feature_deviation("Glycolipid Mass Error", "The delta between the observed precursor and glycolipid m/zs in parts-per-million (ppm)."),	
	glycolipid_feature_charge("Glycolipid Charge", "The observed charge of the glycan (could differ from scan if annotation considers other charge states)."),
	glycolipid_feature_lipidname("Lipid Name", "The name of the annotation object associated with this lipid part.");

	private String sLabel;
	private String sDescription;

	private DMGlycolipidFeature( String sLabel, String sDescription ) {
		this.sLabel = sLabel;
		this.sDescription = sDescription;
	}

	public String getDescription() {
 		return sDescription;
 	}

	public String getLabel() {
		return this.sLabel;
	}

	public static DMGlycolipidFeature lookUp( String _sKey ) {
		for ( DMGlycolipidFeature dmFeature : DMGlycolipidFeature.values() ) {
			if ( dmFeature.name().equals(_sKey) )
				return dmFeature;
		}
		return null;
	}

}
