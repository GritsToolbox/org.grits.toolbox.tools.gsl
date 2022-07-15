package org.grits.toolbox.tools.gsl.annotation.datamodel.tablemodel;

import java.util.ArrayList;

import org.grits.toolbox.datamodel.ms.annotation.glycan.preference.cartoon.MSGlycanAnnotationCartoonPreferencesLoader;
import org.grits.toolbox.datamodel.ms.annotation.glycan.tablemodel.MSGlycanAnnotationTableDataObject;
import org.grits.toolbox.datamodel.ms.annotation.tablemodel.MSAnnotationTableDataObject;
import org.grits.toolbox.datamodel.ms.tablemodel.FillTypes;
import org.grits.toolbox.tools.gsl.annotation.datamodel.preference.MSGlycolipidAnnotationViewerPreferenceLoader;

/**
 * Class for table data object of MSGlycolipidAnnotation GRITS table.
 * @author Masaaki Matsubara
 *
 */
public class MSGlycolipidAnnotationTableDataObject extends MSGlycanAnnotationTableDataObject {
	protected ArrayList<Integer> alLipidCols = null;

	public MSGlycolipidAnnotationTableDataObject(int _iMSLevel, FillTypes fillTypes) {
		super(_iMSLevel, fillTypes);
		this.alLipidCols = new ArrayList<>();
	}

	public void addLipidCol( Integer _iCol ) {
		this.alLipidCols.add(_iCol);
	}

	public ArrayList<Integer> getLipidCols() {
		return this.alLipidCols;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.datamodel.ms.annotation.glycan.tablemodel.MSGlycanAnnotationTableDataObject#initializePreferences()
	 */
	@Override
	public void initializePreferences() {
		setTablePreferences(MSGlycolipidAnnotationViewerPreferenceLoader.getTableViewerPreference(getMSLevel(), getFillType()));
		setCartoonPrefs(MSGlycanAnnotationCartoonPreferencesLoader.getCartoonPreferences());
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.datamodel.ms.annotation.glycan.tablemodel.MSGlycanAnnotationTableDataObject#getSubsetSimianTableDataObject(int, java.lang.String, boolean)
	 */
	@Override
	public MSAnnotationTableDataObject getSubsetSimianTableDataObject(int _iScanNum, String _sRowId,  boolean _bCheckParentScan) {
		MSGlycolipidAnnotationTableDataObject subsetSimianTableData = new MSGlycolipidAnnotationTableDataObject(getMSLevel(), getFillType());
		return getSubsetSimianTableDataObject(_iScanNum, _sRowId, subsetSimianTableData, _bCheckParentScan);
	}

}
