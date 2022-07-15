package org.grits.toolbox.tools.gsl.annotation.datamodel.preference;

import javax.xml.bind.annotation.XmlRootElement;

import org.grits.toolbox.core.datamodel.UnsupportedVersionException;
import org.grits.toolbox.core.preference.share.PreferenceEntity;
import org.grits.toolbox.core.preference.share.PreferenceReader;
import org.grits.toolbox.core.preference.share.PreferenceWriter;
import org.grits.toolbox.datamodel.ms.annotation.glycan.preference.MSGlycanAnnotationViewerPreference;
import org.grits.toolbox.datamodel.ms.tablemodel.FillTypes;
import org.grits.toolbox.display.control.table.datamodel.GRITSColumnHeader;
import org.grits.toolbox.tools.gsl.annotation.datamodel.tablemodel.dmtranslate.DMLipidAnnotation;

/**
 * Extends the MSGlycanAnnotation Viewer preferences to add elements that are relevant to 
 * MS Glycolipid Annotation of Mass Spec data.
 * 
 * @author Masaaki Matsubara
 *
 */
@XmlRootElement(name = "msGlycolipidAnnotationViewerPreference")
public class MSGlycolipidAnnotationViewerPreference extends MSGlycanAnnotationViewerPreference {
//	private static final Logger logger = Logger.getLogger(MSGlycolipidAnnotationViewerPreference.class);
	protected static final String PREFERENCE_NAME_ALL = "org.grits.toolbox.tools.gsl.datamodel.preference.MSGlycolipidAnnotationViewerPreference";
	private static final String CURRENT_VERSION = "1.0";

	public MSGlycolipidAnnotationViewerPreference() {
		this( 0, FillTypes.Scans);
	}

	public MSGlycolipidAnnotationViewerPreference(int _iMSLevel, FillTypes fillType) {
		super(_iMSLevel, fillType);
	}

	/* (non-Javadoc)
	 * @see org.grits.toolbox.datamodel.ms.annotation.preference.MSAnnotationViewerPreference#getCurrentVersion()
	 */
	@Override
	protected String getCurrentVersion() {
		return MSGlycolipidAnnotationViewerPreference.CURRENT_VERSION;
	}

	/**
	 * Called to create the String ID of this MS Glycolipid Annotation GRITS table preference entry.
	 * @param _iMSLevel
	 * 		the MS Level of the GRITS Table
	 * @param _fillType
	 * 		the fill type of the GRITS Table
	 * @return an ID for the preference file for this GRITS Table
	 */
	protected static String getPreferenceID( int _iMSLevel, FillTypes fillType ) {
		String sAdder = "";
		String sName = PREFERENCE_NAME_ALL;
		if ( fillType == FillTypes.Scans ) {
			sAdder = ".Scans";
		}
		else if ( fillType == FillTypes.PeakList ) {
			sAdder = ".Peaks";
		}
		else if ( fillType == FillTypes.PeaksWithFeatures ) {
			sAdder = ".PeaksWithFeatures";
		}
		else if ( fillType == FillTypes.Selection ) {
			sAdder = ".Selection";
		}
		sName += sAdder;
		sName += ".MSLevel" + (_iMSLevel - 1);
		return sName;
	}

	/**
	 * @param _iMSLevel
	 * 		the MS Level of the GRITS Table
	 * @param _fillType
	 * 		the fill type of the GRITS Table
	 * @return the MSGlycanAnnotation PreferenceEntity for the GRITS Table with the specified MS level and fill type
	 * @throws UnsupportedVersionException - if the version was null for the elementor if the element with the given name was not serializableas the current version PreferenceEntity object
	 */
	public static PreferenceEntity getPreferenceEntity( int _iMSLevel, FillTypes _fillType  ) throws UnsupportedVersionException {
		PreferenceEntity preferenceEntity = PreferenceReader.getPreferenceByName(MSGlycolipidAnnotationViewerPreference.getPreferenceID(_iMSLevel, _fillType));
		return preferenceEntity;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.datamodel.ms.annotation.glycan.preference.MSGlycanAnnotationViewerPreference#writePreference()
	 */
	@Override
	public boolean writePreference() {
		PreferenceEntity preferenceEntity = new PreferenceEntity(MSGlycolipidAnnotationViewerPreference.getPreferenceID(getMSLevel(), getFillType()));
		preferenceEntity.setVersion(getCurrentVersion());
		preferenceEntity.setValue(marshalXML());
		return PreferenceWriter.savePreference(preferenceEntity);
	}

	/** 
	 * Creates MS Glycolipid Annotation column header objects (if the key is recognized). 
	 * If the key isn't recognized, call the super-class method to see if it is known there.
	 * @see org.grits.toolbox.datamodel.ms.annotation.glycan.preference.MSGlycanAnnotationViewerPreference#getColumnHeader(java.lang.String)
	 */
	@Override
	public GRITSColumnHeader getColumnHeader(String _sKey) {
		if ( _sKey.equals(DMLipidAnnotation.lipid_annotation_name.name() ) ) {
			return new GRITSColumnHeader( DMLipidAnnotation.lipid_annotation_name.getLabel(), DMLipidAnnotation.lipid_annotation_name.name());
		}
		return super.getColumnHeader(_sKey);
	}	
}