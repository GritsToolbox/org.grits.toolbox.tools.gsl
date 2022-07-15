package org.grits.toolbox.tools.gsl.annotation.entry.process.loader;

import java.util.ArrayList;
import java.util.List;

import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.core.datamodel.property.Property;
import org.grits.toolbox.datamodel.ms.annotation.preference.MSAnnotationViewerPreference;
import org.grits.toolbox.display.control.table.datamodel.GRITSColumnHeader;
import org.grits.toolbox.display.control.table.preference.TableViewerColumnSettings;
import org.grits.toolbox.display.control.table.preference.TableViewerPreference;
import org.grits.toolbox.entry.ms.annotation.glycan.process.loader.MSGlycanAnnotationSummaryTableDataProcessor;
import org.grits.toolbox.entry.ms.annotation.process.loader.MSAnnotationTableDataProcessor;
import org.grits.toolbox.entry.ms.property.MassSpecEntityProperty;
import org.grits.toolbox.ms.om.data.Annotation;
import org.grits.toolbox.ms.om.data.GlycanAnnotation;
import org.grits.toolbox.ms.om.data.GlycolipidAnnotation;
import org.grits.toolbox.tools.gsl.annotation.datamodel.preference.MSGlycolipidAnnotationSummaryViewerPreference;
import org.grits.toolbox.tools.gsl.annotation.datamodel.tablemodel.MSGlycolipidAnnotationSummaryTableDataObject;
import org.grits.toolbox.tools.gsl.annotation.datamodel.tablemodel.dmtranslate.DMLipidAnnotation;
import org.grits.toolbox.tools.gsl.annotation.entry.tablehelpers.MSGlycolipidAnnotationTable;

/**
 * Class for processing table data of MSGlycolipidAnnotationSummary GRITS table.
 * Extends MSGlycanAnnotationSummaryTableDataProcessor to handle glycolipid specific information.
 * @author Masaaki Matsubara
 *
 */
public class MSGlycolipidAnnotationSummaryTableDataProcessor extends MSGlycanAnnotationSummaryTableDataProcessor {
//	private static final Logger logger = Logger.getLogger(MSGlycolipidAnnotationSummaryTableDataProcessor.class);

	public MSGlycolipidAnnotationSummaryTableDataProcessor(Entry _entry, Property _sourceProperty,
			List<MSAnnotationTableDataProcessor> tableDataProcessors) {
		super(_entry, _sourceProperty, tableDataProcessors);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.process.loader.MSGlycanAnnotationSummaryTableDataProcessor#initializeColumnSettings()
	 */
	@Override
	protected TableViewerColumnSettings initializeColumnSettings() {
		TableViewerColumnSettings newSettings = getNewTableViewerSettings();
		int iCols = MSGlycolipidAnnotationSummaryTableDataProcessorUtil.fillMSGlycolipidAnnotationSummaryColumnSettings(newSettings);
		setLastVisibleCol(iCols);
		return newSettings;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.process.loader.MSGlycanAnnotationSummaryTableDataProcessor#initializePreferences()
	 */
	@Override
	public TableViewerPreference initializePreferences() {
		MSAnnotationViewerPreference preferences = (MSAnnotationViewerPreference) super.initializePreferences();
		MSGlycolipidAnnotationSummaryTableDataProcessorUtil.postProcessColumnSettings(preferences);
		return preferences;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.process.loader.MSGlycanAnnotationSummaryTableDataProcessor#getNewTableViewerPreferences()
	 */
	@Override
	protected TableViewerPreference getNewTableViewerPreferences() {
		return new MSGlycolipidAnnotationSummaryViewerPreference();
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.process.loader.MSGlycanAnnotationSummaryTableDataProcessor#initializeTableDataObject(org.grits.toolbox.core.datamodel.property.Property)
	 */
	@Override
	public void initializeTableDataObject(Property _sourceProperty) {
		MSGlycolipidAnnotationSummaryTableDataObject mobj = new MSGlycolipidAnnotationSummaryTableDataObject(( (MassSpecEntityProperty) _sourceProperty).getMsLevel(), this.fillType);
		setSimianTableDataObject(mobj);
		getSimianTableDataObject().initializePreferences();
		if (getSimianTableDataObject().getTablePreferences().settingsNeedInitialization()) {
			TableViewerPreference tvp = initializePreferences();
			MSGlycolipidAnnotationSummaryTableDataProcessor.setDefaultColumnViewSettings(tvp.getPreferenceSettings());
			getSimianTableDataObject().setTablePreferences(tvp);
			getSimianTableDataObject().getTablePreferences().writePreference();
		
		}
	}

	/**
	 * Set default column view settings for MSGlycolipidAnnotationSummary GRITS table.
	 * @param tvs TableViewerColumnSttings of the GRITS table
	 */
	public static void setDefaultColumnViewSettings(TableViewerColumnSettings tvs) {
		MSGlycanAnnotationSummaryTableDataProcessor.setDefaultColumnViewSettings(tvs);
		// Set default for lipid part
		GRITSColumnHeader header = tvs.getColumnHeader( DMLipidAnnotation.lipid_annotation_name.name());
		if( header != null ) {
			tvs.setVisColInx(header, -1);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.process.loader.MSGlycanAnnotationSummaryTableDataProcessor#getAnnotationStructureId(org.grits.toolbox.ms.om.data.Annotation)
	 */
	@Override
	protected String getAnnotationStructureId(Annotation annot) {
		if ( annot instanceof GlycanAnnotation )
			return super.getAnnotationStructureId(annot);
		if ( annot instanceof GlycolipidAnnotation )
			return MSGlycolipidAnnotationTable.GLYCOLIPID_ID_PREFIX + annot.getStringId();
		return null;
	}

	private MSGlycolipidAnnotationSummaryTableDataObject getMySimianTableDataObject() {
		return (MSGlycolipidAnnotationSummaryTableDataObject) getSimianTableDataObject();
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.process.loader.MSGlycanAnnotationSummaryTableDataProcessor#addHeaderLine(int, org.grits.toolbox.display.control.table.datamodel.GRITSColumnHeader, java.util.ArrayList)
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
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.process.loader.MSGlycanAnnotationSummaryTableDataProcessor#fillMSAnnotationSummaryEntryData(org.grits.toolbox.ms.om.data.Annotation, java.lang.String, java.lang.String, java.lang.String, int, java.util.ArrayList, org.grits.toolbox.display.control.table.preference.TableViewerColumnSettings)
	 */
	@Override
	protected void fillMSAnnotationSummaryEntryData(Annotation parentAnnot,
			String sFeatureId, String sSequence, String sFeatureCharge, int iOffset,
			ArrayList<Object> alDataRow, TableViewerColumnSettings preferenceSettings) {
		if ( parentAnnot instanceof GlycanAnnotation ) {
			super.fillMSAnnotationSummaryEntryData(parentAnnot, sFeatureId, sSequence, sFeatureCharge, iOffset, alDataRow, preferenceSettings);
			return;
		}
		if ( parentAnnot instanceof GlycolipidAnnotation ) {
			
			MSGlycolipidAnnotationSummaryTableDataProcessorUtil.fillMSGlycolipidAnnotationSummaryEntryData(
					(GlycolipidAnnotation)parentAnnot,
					sFeatureId, sSequence, sFeatureCharge, iOffset, alDataRow, preferenceSettings);
		}
	}
}
