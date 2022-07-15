package org.grits.toolbox.tools.gsl.annotation.entry.process.loader;

import java.util.ArrayList;

import org.grits.toolbox.datamodel.ms.annotation.glycan.tablemodel.dmtranslate.DMGlycanAnnotation;
import org.grits.toolbox.datamodel.ms.annotation.glycan.tablemodel.dmtranslate.DMGlycanFeature;
import org.grits.toolbox.datamodel.ms.annotation.preference.MSAnnotationViewerPreference;
import org.grits.toolbox.datamodel.ms.annotation.tablemodel.dmtranslate.DMFeature;
import org.grits.toolbox.datamodel.ms.tablemodel.dmtranslate.DMPeak;
import org.grits.toolbox.display.control.table.datamodel.GRITSColumnHeader;
import org.grits.toolbox.display.control.table.preference.TableViewerColumnSettings;
import org.grits.toolbox.entry.ms.process.loader.MassSpecTableDataProcessorUtil;
import org.grits.toolbox.ms.om.data.GlycolipidAnnotation;
import org.grits.toolbox.tools.gsl.annotation.datamodel.tablemodel.dmtranslate.DMGlycolipidFeature;
import org.grits.toolbox.tools.gsl.annotation.datamodel.tablemodel.dmtranslate.DMLipidAnnotation;

/**
 * Utility class for filling in rows in a GRITStable with fields appropriate for MS Glycolipid Annotation Summary of MS data.
 * Used in MSGlycolipidAnnotationSummaryTableDataProcessor.
 * Following MSGlycanAnnotationSummaryTableDataProcessorUtil.
 * @see org.grits.toolbox.tools.gsl.annotation.entry.process.loader.MSGlycolipidAnnotationSummaryTableDataProcessor
 * @see org.grits.toolbox.entry.ms.annotation.glycan.process.loader.MSGlycanAnnotationSummaryTableDataProcessorUtil
 * @author Masaaki Matsubara
 *
 */
public class MSGlycolipidAnnotationSummaryTableDataProcessorUtil {

	/**
	 * Process TableViewerColumnSettings of the given MSAnnotationViwerPreference before the table build.
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.process.loader.MSGlycanAnnotationSummaryTableDataProcessorUtil
	 * #postProcessColumnSettings(org.grits.toolbox.display.control.table.preference.TableViewerPreference)
	 * @param preference MSAnnotationViewerPreference to be processed its TableViewerColumnSettings
	 */
	public static void postProcessColumnSettings(MSAnnotationViewerPreference preference) {
		TableViewerColumnSettings columnSettings = preference.getPreferenceSettings();

		// feature -> glycolipid_feature
		// (Labels have changed to glycan_feature in MSGlycanAnnotationSummaryTableDataProcessorUtil.postProcessColumnSettings())
		replaceColumnHeaderLabel(columnSettings,
				DMFeature.feature_id.name(), DMGlycanFeature.glycan_feature_id.getLabel(),
				DMGlycolipidFeature.glycolipid_feature_id.getLabel()
			);
		replaceColumnHeaderLabel(columnSettings,
				DMFeature.feature_sequence.name(), DMGlycanFeature.glycan_feature_sequence.getLabel(),
				DMGlycolipidFeature.glycolipid_feature_sequence.getLabel()
			);
	}

	private static void replaceColumnHeaderLabel(TableViewerColumnSettings columnSettings, String oldName, String oldLabel, String newLabel) {
		GRITSColumnHeader oldHeader = columnSettings.getColumnHeader(oldName);
		if( oldHeader != null ) {
			if( oldHeader.getLabel().equals( oldLabel ) ) {
				int iFeatureDeviationCol = columnSettings.getColumnPosition( oldName );
				columnSettings.getHeaders().remove(oldHeader);
				GRITSColumnHeader header = new GRITSColumnHeader(newLabel, oldName);
				columnSettings.putColumn(header, iFeatureDeviationCol);
			}
		}
	}

	/**
	 * Fill the given TableViewerColumnSettings by the GlycolipidAnnotation specific information
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.process.loader.MSGlycanAnnotationSummaryTableDataProcessorUtil
	 * #fillMSGlycanAnnotationSummaryColumnSettings(TableViewerColumnSettings)
	 * @param _columnSettings TableViewerColumnSettings to be filled by column headers
	 * @return int of offset number
	 */
	public static int fillMSGlycolipidAnnotationSummaryColumnSettings(TableViewerColumnSettings _columnSettings) {
		GRITSColumnHeader header = new GRITSColumnHeader(DMPeak.peak_id.getLabel(), DMPeak.peak_id.name());
		header.setIsGrouped(false);
		_columnSettings.addColumn( header );
		header = new GRITSColumnHeader(DMPeak.peak_mz.getLabel(), DMPeak.peak_mz.name());
		header.setIsGrouped(false);
		_columnSettings.addColumn( header );
		header = new GRITSColumnHeader(DMPeak.peak_intensity.getLabel(), DMPeak.peak_intensity.name());
		header.setIsGrouped(false);
		_columnSettings.addColumn( header );

		_columnSettings.addColumn( DMGlycanAnnotation.glycan_annotation_glycanId.getLabel(), DMGlycanAnnotation.glycan_annotation_glycanId.name() );
		_columnSettings.addColumn( DMLipidAnnotation.lipid_annotation_name.getLabel(), DMLipidAnnotation.lipid_annotation_name.name() );
		_columnSettings.addColumn( DMGlycolipidFeature.glycolipid_feature_id.getLabel(), DMFeature.feature_id.name());
		_columnSettings.addColumn( DMGlycanAnnotation.glycan_annotation_glycancartoon.getLabel(), DMGlycanAnnotation.glycan_annotation_glycancartoon.name());
		_columnSettings.addColumn( DMGlycolipidFeature.glycolipid_feature_sequence.getLabel(), DMFeature.feature_sequence.name() );

		return 5; // in this case, sending number of grouped columns
	}

	/**
	 * Fill a row of the MSGlycolipidAnnotationSummary GRITS table using the given information.
	 * @see org.grits.toolbox.entry.ms.annotation.glycan.process.loader.MSGlycanAnnotationSummaryTableDataProcessorUtil
	 * #fillMSGlycanAnnotationSummryEntryData(org.grits.toolbox.ms.om.data.GlycanAnnotation, String, String, String, int, ArrayList, TableViewerColumnSettings)
	 * @param _annotation GlycolipidAnnotation to be used for filling the row
	 * @param _sFeatureId String of feature ID
	 * @param _sSequence String of glycolipid sequence written in GWB sequence for drawing cartoon
	 * @param sFeatureCharge String of feature charge value
	 * @param _iOffset Offset number in the row
	 * @param _tableRow the ArrayList representing the entire row of data Description
	 * @param _columnSettings TableViewerColumnSettings having position number of each information
	 */
	public static void fillMSGlycolipidAnnotationSummaryEntryData(GlycolipidAnnotation _annotation, String _sFeatureId,
			String _sSequence, String sFeatureCharge, int _iOffset, ArrayList<Object> _tableRow,
			TableViewerColumnSettings _columnSettings) {
		if( _annotation == null ) 
			return;
		MassSpecTableDataProcessorUtil.setRowValue( _iOffset +
				_columnSettings.getColumnPosition( DMGlycanAnnotation.glycan_annotation_glycanId.name() ), 
				_annotation.getGlycanAnnotation().getStringId(), _tableRow);
		MassSpecTableDataProcessorUtil.setRowValue( _iOffset +
				_columnSettings.getColumnPosition( DMLipidAnnotation.lipid_annotation_name.name() ), 
				_annotation.getLipidAnnotation().getSequence(), _tableRow);
		MassSpecTableDataProcessorUtil.setRowValue( _iOffset +
				_columnSettings.getColumnPosition( DMFeature.feature_sequence.name() ), 
				_sSequence, _tableRow);
		MassSpecTableDataProcessorUtil.setRowValue( _iOffset +
				_columnSettings.getColumnPosition( DMGlycanAnnotation.glycan_annotation_glycancartoon.name() ), 
				_sSequence + ".png", _tableRow);
		MassSpecTableDataProcessorUtil.setRowValue( _iOffset +
				_columnSettings.getColumnPosition( DMFeature.feature_id.name() ), 
				_sFeatureId, _tableRow);
	}

}
