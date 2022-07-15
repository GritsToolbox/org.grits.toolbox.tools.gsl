package org.grits.toolbox.tools.gsl.annotation.datamodel.preference;

import javax.xml.bind.annotation.XmlRootElement;

import org.grits.toolbox.core.datamodel.UnsupportedVersionException;
import org.grits.toolbox.core.preference.share.PreferenceEntity;
import org.grits.toolbox.core.preference.share.PreferenceReader;
import org.grits.toolbox.core.preference.share.PreferenceWriter;
import org.grits.toolbox.datamodel.ms.annotation.glycan.preference.MSGlycanAnnotationSummaryViewerPreference;
import org.grits.toolbox.datamodel.ms.tablemodel.FillTypes;
import org.grits.toolbox.display.control.table.datamodel.GRITSColumnHeader;
import org.grits.toolbox.display.control.table.preference.TableViewerPreferenceReader;
import org.grits.toolbox.tools.gsl.annotation.datamodel.tablemodel.dmtranslate.DMLipidAnnotation;

/**
 * Class for storing summary viewer preference for glycolipid annotations. Extending {@link MSGlycanAnnotationSummaryViewerPreference}.
 * @see org.grits.toolbox.datamodel.ms.annotation.glycan.preference.MSGlycanAnnotationSummaryViewerPreference
 * @author Masaaki Matsubara
 *
 */
@XmlRootElement(name = "msGlycolipidAnnotationSummaryViewerPreference")
public class MSGlycolipidAnnotationSummaryViewerPreference extends MSGlycanAnnotationSummaryViewerPreference {
//	private static final Logger logger = Logger.getLogger(MSGlycolipidAnnotationSummaryViewerPreference.class);
	private static final String PREFERENCE_NAME_ALL = "org.grits.toolbox.tools.gsl.datamodel.preference.MSGlycolipidAnnotationSummaryViewerPreference";
	/*
	 * Version history:
	 * 1.0 - Original release w/ versioning
	 */
	private static final String CURRENT_VERSION = "1.0";

	public MSGlycolipidAnnotationSummaryViewerPreference() {
		super();
	}

	/**
	 * Constructor with MS level and FillTypes
	 * @param _iMSLevel MS level
	 * @param fillType FillTypes
	 */
	public MSGlycolipidAnnotationSummaryViewerPreference(int _iMSLevel, FillTypes fillType) {
		super(_iMSLevel, fillType);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.datamodel.ms.annotation.glycan.preference.MSGlycanAnnotationSummaryViewerPreference#getNewReader()
	 */
	@Override
	protected TableViewerPreferenceReader getNewReader() {
		return new MSGlycolipidAnnotationSummaryViewerPreferenceReader();
	}

	/**
	 * Called to create the String ID of this MS Glycolipid Annotation Summary GRITS table preference entry.
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
		sName += sAdder;
		sName += ".MSLevel" + (_iMSLevel - 1);
		return sName;
	}

	/**
	 * @param _iMSLevel
	 * 		the MS Level of the GRITS Table
	 * @param _fillType
	 * 		the fill type of the GRITS Table
	 * @return PreferenceEntity for the MSGlycanAnnotationSummary GRITS Table with the given MS level and fill type
	 * @throws UnsupportedVersionException - if the version was null for the elementor if the element with the given name was not serializableas the current version PreferenceEntity object
	 */
	public static PreferenceEntity getPreferenceEntity( int _iMSLevel, FillTypes _fillType  ) throws UnsupportedVersionException {
		PreferenceEntity preferenceEntity = PreferenceReader.getPreferenceByName(MSGlycolipidAnnotationSummaryViewerPreference.getPreferenceID(_iMSLevel, _fillType));
		return preferenceEntity;
	}
	

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.datamodel.ms.annotation.glycan.preference.MSGlycanAnnotationSummaryViewerPreference#getCurrentVersion()
	 */
	@Override
	protected String getCurrentVersion() {
		return CURRENT_VERSION;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.datamodel.ms.annotation.glycan.preference.MSGlycanAnnotationSummaryViewerPreference#writePreference()
	 */
	@Override
	public boolean writePreference() {
		PreferenceEntity preferenceEntity = new PreferenceEntity(MSGlycolipidAnnotationSummaryViewerPreference.getPreferenceID(getMSLevel(), getFillType()));
		preferenceEntity.setVersion(getCurrentVersion());
		preferenceEntity.setValue(marshalXML());
		return PreferenceWriter.savePreference(preferenceEntity);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.datamodel.ms.annotation.glycan.preference.MSGlycanAnnotationSummaryViewerPreference#getColumnHeader(java.lang.String)
	 */
	@Override
	public GRITSColumnHeader getColumnHeader(String _sKey) {
		if ( _sKey.equals(DMLipidAnnotation.lipid_annotation_name.name() ) ) {
			return new GRITSColumnHeader( DMLipidAnnotation.lipid_annotation_name.getLabel(), DMLipidAnnotation.lipid_annotation_name.name());
		}
		return super.getColumnHeader(_sKey);
	}	

}
