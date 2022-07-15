package org.grits.toolbox.tools.gsl.annotation.entry.process.loader;

import java.util.ArrayList;

import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.core.datamodel.property.Property;
import org.grits.toolbox.datamodel.ms.tablemodel.FillTypes;
import org.grits.toolbox.display.control.table.datamodel.GRITSColumnHeader;
import org.grits.toolbox.display.control.table.datamodel.GRITSListDataRow;
import org.grits.toolbox.display.control.table.preference.TableViewerColumnSettings;
import org.grits.toolbox.display.control.table.preference.TableViewerPreference;
import org.grits.toolbox.display.control.table.process.TableDataProcessor;
import org.grits.toolbox.entry.ms.annotation.glycan.process.loader.MSGlycanAnnotationTableDataProcessor;
import org.grits.toolbox.entry.ms.annotation.glycan.process.loader.MSGlycanAnnotationTableDataProcessorUtil;
import org.grits.toolbox.entry.ms.annotation.process.loader.MSAnnotationTableDataProcessorUtil;
import org.grits.toolbox.ms.om.data.Annotation;
import org.grits.toolbox.ms.om.data.Feature;
import org.grits.toolbox.ms.om.data.GlycanAnnotation;
import org.grits.toolbox.ms.om.data.Scan;
import org.grits.toolbox.tools.gsl.annotation.datamodel.preference.MSGlycolipidAnnotationViewerPreference;
import org.grits.toolbox.tools.gsl.annotation.datamodel.tablemodel.MSGlycolipidAnnotationTableDataObject;
import org.grits.toolbox.tools.gsl.annotation.datamodel.tablemodel.dmtranslate.DMLipidAnnotation;

/**
 * Processor class for filling MSGlycolipidAnnotationTableDataObject with DANGO annotation results.
 * Extends MSGlycanAnnotationTableDataProcessor with specific options for displaying glycolipid annotated mass spec data.
 * @author Masaaki Matsubara
 *
 */
public class MSGlycolipidAnnotationTableDataProcessor extends MSGlycanAnnotationTableDataProcessor {

//	private static final Logger logger = Logger.getLogger(MSGlycolipidAnnotationTableDataProcessor.class);

	public MSGlycolipidAnnotationTableDataProcessor(Entry _entry, Property _sourceProperty, FillTypes _fillType, int iMinMSLevel) {
		super(_entry, _sourceProperty, _fillType, iMinMSLevel);
		// TODO Auto-generated constructor stub
	}

	public MSGlycolipidAnnotationTableDataProcessor(Entry _entry, Property _sourceProperty, int iMinMSLevel) {
		super(_entry, _sourceProperty, iMinMSLevel);
		// TODO Auto-generated constructor stub
	}

	public MSGlycolipidAnnotationTableDataProcessor( TableDataProcessor _parent, Property _sourceProperty, FillTypes _fillType, int iMinMSLevel ) {
		super(_parent, _sourceProperty, _fillType, iMinMSLevel);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.process.loader.MSAnnotationTableDataProcessor#initializeTableDataObject(org.grits.toolbox.core.datamodel.property.Property)
	 */
	@Override
	public void initializeTableDataObject(Property _sourceProperty) {
		setSimianTableDataObject(new MSGlycolipidAnnotationTableDataObject(getMassSpecEntityProperty().getMsLevel(), this.fillType));
		getSimianTableDataObject().initializePreferences();
		if( getSimianTableDataObject().getTablePreferences().settingsNeedInitialization() ) {
			TableViewerPreference tvp = initializePreferences();		
			MSGlycolipidAnnotationTableDataProcessor.setDefaultColumnViewSettings(this.fillType, tvp.getPreferenceSettings());
			getSimianTableDataObject().setTablePreferences(tvp);
			getSimianTableDataObject().getTablePreferences().writePreference();
		}  
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.process.loader.MSGlycanAnnotationTableDataProcessor#getNewTableViewerPreferences()
	 */
	@Override
	protected TableViewerPreference getNewTableViewerPreferences() {
		return new MSGlycolipidAnnotationViewerPreference();
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.process.loader.MSGlycanAnnotationTableDataProcessor#initializePreferences()
	 */
	@Override
	public TableViewerPreference initializePreferences() {
		MSGlycolipidAnnotationViewerPreference newPreferences = (MSGlycolipidAnnotationViewerPreference) super.initializePreferences();

		MSGlycolipidAnnotationViewerPreference oldPreferences = (MSGlycolipidAnnotationViewerPreference) getSimianTableDataObject().getTablePreferences();
		if( oldPreferences != null ) { // preserve previous setting if present
			newPreferences.setShowExtraInfo(oldPreferences.getShowExtraInfo());
		}
		MSGlycolipidAnnotationTableDataProcessorUtil.postProcessColumnSettings(newPreferences);
		return newPreferences;
	}

	/**
	 * Description: sets the default order of columns for the MS Glycolipid Annotation table.
	 * 
	 * @param fillType the FillType of the current page
	 * @param tvs a TableViewerColumnSettings object
	 */
	public static void setDefaultColumnViewSettings(FillTypes fillType, TableViewerColumnSettings tvs) {
		// Set default using parent processor
		MSGlycanAnnotationTableDataProcessor.setDefaultColumnViewSettings(fillType, tvs);
		// Set default for lipid part
		if ( fillType == FillTypes.PeaksWithFeatures ) {
			GRITSColumnHeader header = tvs.getColumnHeader( DMLipidAnnotation.lipid_annotation_name.name());
			if( header != null ) {
				tvs.setVisColInx(header, -1);
			}
		}
	}

	/**
	 * @return MSGlycolipidAnnotationTableDataObject - casts the TableDataObject to MSGlycolipidAnnotationTableDataObject
	 */
	private MSGlycolipidAnnotationTableDataObject getMySimianTableDataObject() {
		return (MSGlycolipidAnnotationTableDataObject) getSimianTableDataObject();
	}

	/* (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.process.loader.MSAnnotationTableDataProcessor#addHeaderLine(int, org.grits.toolbox.display.control.table.datamodel.GRITSColumnHeader, java.util.ArrayList)
	 */
	@Override
	protected void addHeaderLine( int iPrefColNum, GRITSColumnHeader colHeader, ArrayList<GRITSColumnHeader> alHeader ) {
		if ( colHeader.getKeyValue().equals( DMLipidAnnotation.lipid_annotation_name.name() ) ) {
			this.getMySimianTableDataObject().addLipidCol(iPrefColNum);
		}
		super.addHeaderLine(iPrefColNum, colHeader, alHeader);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.process.loader.MSAnnotationTableDataProcessor#fillFeatureData(org.grits.toolbox.ms.om.data.Feature, org.grits.toolbox.display.control.table.preference.TableViewerColumnSettings, org.grits.toolbox.display.control.table.datamodel.GRITSListDataRow)
	 */
	@Override
	protected void fillFeatureData( Feature feature, TableViewerColumnSettings _settings, GRITSListDataRow alRow  ) {
		if ( feature != null ) {
			// Use super if feature is GlycanAnnotation
			Annotation annot = this.getAnnotation(feature.getAnnotationId());
			if ( annot instanceof GlycanAnnotation ) {
				super.fillFeatureData(feature, _settings, alRow);
				return;
			}
		}
		MSGlycolipidAnnotationTableDataProcessorUtil.fillMSGlycolipidFeatureData(feature, alRow.getDataRow(), _settings);
		MSAnnotationTableDataProcessorUtil.fillMSFeatureCustomExtraData(feature, alRow.getDataRow(), _settings, getGRITSdata().getDataHeader().getFeatureCustomExtraData());
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.process.loader.MSGlycanAnnotationTableDataProcessor#addAnnotationColumns(org.grits.toolbox.display.control.table.preference.TableViewerColumnSettings)
	 */
	@Override
	protected void addAnnotationColumns(TableViewerColumnSettings _settings) {
		super.addAnnotationColumns(_settings);
		MSGlycolipidAnnotationTableDataProcessorUtil.fillMSGlycolipidAnnotationColumnSettingsLipidAnnotation(_settings);
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.process.loader.MSGlycanAnnotationTableDataProcessor#fillAnnotationData(org.grits.toolbox.ms.om.data.Annotation, org.grits.toolbox.ms.om.data.Feature, org.grits.toolbox.ms.om.data.Scan, int, org.grits.toolbox.display.control.table.preference.TableViewerColumnSettings, org.grits.toolbox.display.control.table.datamodel.GRITSListDataRow)
	 */
	@Override
	protected void fillAnnotationData(Annotation a_annotation, Feature feature, Scan a_scan, int _iNumCandidates, 
			TableViewerColumnSettings _settings, GRITSListDataRow alRow) {
		// Fill annotation data
		MSAnnotationTableDataProcessorUtil.fillAnnotationData(a_annotation, _iNumCandidates, alRow.getDataRow(), _settings);	
		MSAnnotationTableDataProcessorUtil.fillMSAnnotationCustomExtraData(a_annotation, alRow.getDataRow(), _settings, getGRITSdata().getDataHeader().getAnnotationCustomExtraData());

		// Fill glycolipid annotation data
		MSGlycolipidAnnotationTableDataProcessorUtil.fillMSAnnotationData(a_annotation, alRow.getDataRow(), _settings);
		if( feature != null ) {
			MSGlycanAnnotationTableDataProcessorUtil.fillMSGlycanAnnotationCartoonColumns(feature.getId(), alRow.getDataRow(),
					_settings, (getMassSpecEntityProperty().getMsLevel() > 2));
		}
	}


}
