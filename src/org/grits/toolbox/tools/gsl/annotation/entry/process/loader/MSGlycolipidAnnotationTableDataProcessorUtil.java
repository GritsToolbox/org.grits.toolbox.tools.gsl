package org.grits.toolbox.tools.gsl.annotation.entry.process.loader;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.grits.toolbox.datamodel.ms.annotation.glycan.tablemodel.dmtranslate.DMGlycanAnnotation;
import org.grits.toolbox.datamodel.ms.annotation.glycan.tablemodel.dmtranslate.DMGlycanFeature;
import org.grits.toolbox.datamodel.ms.annotation.tablemodel.dmtranslate.DMFeature;
import org.grits.toolbox.display.control.table.datamodel.GRITSColumnHeader;
import org.grits.toolbox.display.control.table.preference.TableViewerColumnSettings;
import org.grits.toolbox.display.control.table.preference.TableViewerPreference;
import org.grits.toolbox.entry.ms.annotation.process.loader.MSAnnotationTableDataProcessorUtil;
import org.grits.toolbox.entry.ms.process.loader.MassSpecTableDataProcessorUtil;
import org.grits.toolbox.ms.om.data.Annotation;
import org.grits.toolbox.ms.om.data.Feature;
import org.grits.toolbox.ms.om.data.GlycanAnnotation;
import org.grits.toolbox.ms.om.data.GlycolipidAnnotation;
import org.grits.toolbox.ms.om.data.GlycolipidFeature;
import org.grits.toolbox.ms.om.data.LipidAnnotation;
import org.grits.toolbox.ms.om.data.LipidFeature;
import org.grits.toolbox.tools.gsl.annotation.datamodel.tablemodel.dmtranslate.DMGlycolipidFeature;
import org.grits.toolbox.tools.gsl.annotation.datamodel.tablemodel.dmtranslate.DMLipidAnnotation;

/**
 * Utility class for filling in rows in a GRITStable with fields appropriate for MS Glycolipid Annotation of MS data.
 * Used in MSGlycolipidAnnotationTableDataProcessor.
 * Following MSGlycanAnnotationTableDataProcessorUtil.
 * @see org.grits.toolbox.tools.gsl.annotation.entry.process.loader.MSGlycolipidAnnotationTableDataProcessor
 * @see org.grits.toolbox.entry.ms.annotation.glycan.process.loader.MSGlycanAnnotationTableDataProcessorUtil
 * @author Masaaki Matsubara
 *
 */
public class MSGlycolipidAnnotationTableDataProcessorUtil {

	private static final Logger logger = Logger.getLogger(MSGlycolipidAnnotationTableDataProcessorUtil.class);

	/**
	 * Description: adds the Glycolipid Annotation-specific columns to the TableViewerColumnSettings object
	 * @param _columnSettings a TableViewerColumnSettings object
	 * @return int the number of columns added
	 */
	public static int fillMSGlycolipidAnnotationColumnSettingsLipidAnnotation( TableViewerColumnSettings _columnSettings ) {
		_columnSettings.addColumn( DMLipidAnnotation.lipid_annotation_name.getLabel(), DMLipidAnnotation.lipid_annotation_name.name() );
		return 1;
	}

	/**
	 * Description: allows the modification of column labels and other info AFTER reading from preferences.
	 * @param preference a TableViewerPreference object
	 */
	public static void postProcessColumnSettings(TableViewerPreference preference) {
		/*
		 	glycolipid_feature_charge("Glycolipid Charge"),
			glycolipid_feature_id("Glycolipid Id"),
			glycolipid_feature_type("Glycolipid Type"),
			glycolipid_feature_sequence("Glycolipid Sequence"),
			glycolipid_feature_mz("Glycolipid m/z"),
			glycolipid_feature_deviation("Glycolipid Mass Error");	
		 */
		TableViewerColumnSettings columnSettings = preference.getPreferenceSettings();

		// feature -> glycolipid_feature (Labels have changed to glycan_feature in MSGlycanAnnotationTableDataProcessorUtil.postProcessColumnSettings())
		replaceColumnHeaderLabel(columnSettings,
				DMFeature.feature_charge.name(), DMGlycanFeature.glycan_feature_charge.getLabel(),
				DMGlycolipidFeature.glycolipid_feature_charge.getLabel()
			);
		replaceColumnHeaderLabel(columnSettings,
				DMFeature.feature_id.name(), DMGlycanFeature.glycan_feature_id.getLabel(),
				DMGlycolipidFeature.glycolipid_feature_id.getLabel()
			);
		replaceColumnHeaderLabel(columnSettings,
				DMFeature.feature_type.name(), DMGlycanFeature.glycan_feature_type.getLabel(),
				DMGlycolipidFeature.glycolipid_feature_type.getLabel()
			);
		replaceColumnHeaderLabel(columnSettings,
				DMFeature.feature_sequence.name(), DMGlycanFeature.glycan_feature_sequence.getLabel(),
				DMGlycolipidFeature.glycolipid_feature_sequence.getLabel()
			);
		replaceColumnHeaderLabel(columnSettings,
				DMFeature.feature_mz.name(), DMGlycanFeature.glycan_feature_mz.getLabel(),
				DMGlycolipidFeature.glycolipid_feature_mz.getLabel()
			);
		replaceColumnHeaderLabel(columnSettings,
				DMFeature.feature_deviation.name(), DMGlycanFeature.glycan_feature_deviation.getLabel(),
				DMGlycolipidFeature.glycolipid_feature_deviation.getLabel()
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
	 * Description: fills in the Glycan Annotation-specific information into the table row.
	 * 
	 * @param a_annotation a GlycolipidAnnotation object
	 * @param _tableRow The ArrayList of Objects to be filled
	 * @param _columnSettings the TableViewerColumnSettings object with the positions of the columns in the table row
	 */
	public static void fillMSGlycanAnnotationData(GlycanAnnotation a_annotation,
			ArrayList<Object> _tableRow, TableViewerColumnSettings _columnSettings) {

		if ( a_annotation == null )
			return;
		// Set Glycan ID
		MassSpecTableDataProcessorUtil.setRowValue(
				_columnSettings.getColumnPosition( DMGlycanAnnotation.glycan_annotation_glycanId.name() ),
				a_annotation.getStringId(),
				_tableRow
			);
		MassSpecTableDataProcessorUtil.setRowValue(
				_columnSettings.getColumnPosition( DMGlycanAnnotation.glycan_annotation_glytoucanid.name() ),
				a_annotation.getGlytoucanId(),
				_tableRow
			);
		// Set derivatisation type
		MassSpecTableDataProcessorUtil.setRowValue(
				_columnSettings.getColumnPosition( DMGlycanAnnotation.glycan_annotation_perDerivatisationType.name() ), 
				a_annotation.getPerDerivatisationType(),
				_tableRow
			);
		// Set composition (not yet)
//		MassSpecTableDataProcessorUtil.setRowValue(
//				_columnSettings.getColumnPosition( DMGlycanAnnotation.glycan_annotation_composition.name() ), 
//				a_annotation.getComposition(),
//				_tableRow
//			);
		// Set GWB sequence of glycan
		MassSpecTableDataProcessorUtil.setRowValue(
				_columnSettings.getColumnPosition( DMGlycanAnnotation.glycan_annotation_sequenceGWB.name() ), 
				a_annotation.getSequenceGWB(),
				_tableRow
			);
	}

	/**
	 * Description: fills in the Lipid Annotation-specific information into the table row.
	 * 
	 * @param a_annotation a GlycolipidAnnotation object
	 * @param _tableRow The ArrayList of Objects to be filled
	 * @param _columnSettings the TableViewerColumnSettings object with the positions of the columns in the table row
	 */
	public static void fillMSLipidAnnotationData(LipidAnnotation a_annotation,
			ArrayList<Object> _tableRow, TableViewerColumnSettings _columnSettings) {

		if ( a_annotation == null )
			return;
		// Set Lipid sequence
		MassSpecTableDataProcessorUtil.setRowValue(
				_columnSettings.getColumnPosition( DMLipidAnnotation.lipid_annotation_name.name() ),
				a_annotation.getSequence(),
				_tableRow
			);
	}

	/**
	 * Description: fills in the Glycolipid Annotation-specific information into the table row.
	 * 
	 * @see #fillMSGlycanAnnotationData(GlycanAnnotation, ArrayList, TableViewerColumnSettings)
	 * @see #fillMSLipidAnnotationData(LipidAnnotation, ArrayList, TableViewerColumnSettings)
	 * @param a_annotation a GlycolipidAnnotation object
	 * @param _tableRow The ArrayList of Objects to be filled
	 * @param _columnSettings the TableViewerColumnSettings object with the positions of the columns in the table row
	 */
	public static void fillMSGlycolipidAnnotationData(GlycolipidAnnotation a_annotation,
			ArrayList<Object> _tableRow, TableViewerColumnSettings _columnSettings) {

		// Set glycan annotation data
		fillMSGlycanAnnotationData(a_annotation.getGlycanAnnotation(), _tableRow, _columnSettings);
		// Set lipid annotation data
		fillMSLipidAnnotationData(a_annotation.getLipidAnnotation(), _tableRow, _columnSettings);
	}

	/**
	 * Description: fills in the Annotation-specific information into the table row.
	 * 
	 * @see #fillMSGlycolipidAnnotationData(GlycolipidAnnotation, ArrayList, TableViewerColumnSettings)
	 * @see #fillMSGlycanAnnotationData(GlycanAnnotation, ArrayList, TableViewerColumnSettings)
	 * @see #fillMSLipidAnnotationData(LipidAnnotation, ArrayList, TableViewerColumnSettings)
	 * @param a_annotation an Annotation object
	 * @param _tableRow The ArrayList of Objects to be filled
	 * @param _columnSettings the TableViewerColumnSettings object with the positions of the columns in the table row
	 */
	public static void fillMSAnnotationData(Annotation a_annotation,
			ArrayList<Object> _tableRow, TableViewerColumnSettings _columnSettings) {
		if ( a_annotation instanceof GlycolipidAnnotation ) {
			fillMSGlycolipidAnnotationData((GlycolipidAnnotation)a_annotation, _tableRow, _columnSettings);
		} else if ( a_annotation instanceof GlycanAnnotation ) {
			fillMSGlycanAnnotationData((GlycanAnnotation)a_annotation, _tableRow, _columnSettings);
			// Set empty lipid annotation data
			LipidAnnotation t_lipAnnot = new LipidAnnotation();
			t_lipAnnot.setSequence("");
			fillMSLipidAnnotationData(t_lipAnnot, _tableRow, _columnSettings);
		}
	}

	/**
	 * Fill table row with the specified glycolipid feature data
	 * @param a_feature The Feature containing row data
	 * @param _tableRow The ArrayList representing the entire row of data
	 * @param _columnSettings The TableViewerColumnSettings containing the column number of data
	 */
	public static void fillMSGlycolipidFeatureData(Feature a_feature, ArrayList<Object> _tableRow, TableViewerColumnSettings _columnSettings ) {
		if ( a_feature == null )
			return;

		// Fill common feature data
		MSAnnotationTableDataProcessorUtil.fillFeatureData(a_feature, _tableRow, _columnSettings);

		// Get lipid name
		String t_strLipidName = null;
		if ( a_feature instanceof GlycolipidFeature )
			t_strLipidName = ((GlycolipidFeature)a_feature).getLipidName();
		else if ( a_feature instanceof LipidFeature )
			t_strLipidName = ((LipidFeature)a_feature).getLipidName();

		if ( t_strLipidName == null )
			return;

		// Fill lipid specific feature data
		try {
			MassSpecTableDataProcessorUtil.setRowValue(
					_columnSettings.getColumnPosition( DMGlycolipidFeature.glycolipid_feature_lipidname.name() ), 
					t_strLipidName, _tableRow);
		} catch( Exception ex ) {
			logger.error(ex.getMessage(), ex);
		}
	}
}
