package org.grits.toolbox.tools.gsl.util.io.excel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * Abstract class for exporitng XLSX file.
 * Helps creating sheets and rows 
 * @author Masaaki Matsubara
 *
 */
public abstract class ExporterXLSXAbstract {

	private FileOutputStream m_fos;
	protected Workbook m_book;
	protected Sheet m_sheetCurent;
	protected int m_nRowCurrent;
	private int m_nSheet;
	private CellStyle m_csWrapText;

	public ExporterXLSXAbstract(String a_strOutputFile) throws IOException {
		this.m_book = new SXSSFWorkbook();
		this.m_csWrapText = this.m_book.createCellStyle();
		this.m_csWrapText.setWrapText(true);


		this.m_fos =  new FileOutputStream(a_strOutputFile);
	}

	/**
	 * Creates workbook.
	 * @return true if the creation is succeeded.
	 */
	public abstract boolean createBook();

	/**
	 * Closes workbook.
	 */
	public void closeBook() {
		if ( this.m_book == null )
			return;
		// Dispose book
		try {
			((SXSSFWorkbook) this.m_book).dispose();
		} catch (Exception e) {
		}
	}

	/**
	 * Writes .XLSX file.
	 * @throws IOException if an I/O error occurs
	 */
	public void write() throws IOException {
		// Write book
		this.m_book.write(this.m_fos);
		this.m_fos.close();
		// Close book
		this.closeBook();
	}

	/**
	 * Creates a row from the given strings.
	 * @param a_listContents List of String of content
	 */
	protected void createRow( List<String> a_listContents ) {
		int t_nCol = 0;
		Row t_row = this.m_sheetCurent.createRow(this.m_nRowCurrent);
		int t_nLines = 1;
		Cell t_cell;
		for ( String t_strCell : a_listContents ) {
			t_cell = t_row.createCell(t_nCol++);
			if ( t_strCell.isEmpty() )
				continue;
			// For numbers
			if ( this.isNumeric(t_strCell) ) {
				t_cell.setCellType(CellType.NUMERIC);
				t_cell.setCellValue(Double.parseDouble(t_strCell));
				continue;
			}
			t_cell.setCellType(CellType.STRING);
			t_cell.setCellValue(t_strCell);
/*
			// For wrapped text
			if ( t_strCell.contains("\n") ) {
				// Set wrap text
				t_cell.setCellStyle(this.m_csWrapText);
				// Count lines
				String[] t_lStrings = t_strCell.split("\n");
				if ( t_nLines < t_lStrings.length )
					t_nLines = t_lStrings.length;
			}
*/
		}
		// Set row height
		if ( t_nLines > 1 ) {
			t_row.setHeightInPoints(this.m_sheetCurent.getDefaultRowHeightInPoints() * t_nLines);
		}
		this.m_nRowCurrent++;
	}

	/**
	 * Judges whether the given string is numeric or not.
	 * @param a_strCell String of cell content to be judged
	 * @return true if the given string is numeric
	 */
	protected boolean isNumeric(String a_strCell) {
		if ( a_strCell == null || a_strCell.isEmpty() )
			return false;
		try {
			Double.parseDouble(a_strCell);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Creates a sheet having the given name.
	 * @param a_strSheetName String of sheet name
	 * @return Sheet having the given name
	 */
	protected Sheet createSheet(String a_strSheetName) {
		Sheet t_sheet = this.m_book.createSheet();
		if ( t_sheet instanceof SXSSFSheet ) {
			((SXSSFSheet) t_sheet).trackAllColumnsForAutoSizing();
		}

		this.m_book.setSheetName(this.m_nSheet++, a_strSheetName);
		// Reset row #
		this.m_nRowCurrent = 0;
		return t_sheet;
	}

	/**
	 * Sizes all column automatically.
	 * @param a_sheet Sheet to be sized
	 */
	protected void autoSizeAllColumn(Sheet a_sheet) {
		((SXSSFSheet)a_sheet).trackAllColumnsForAutoSizing();
		int t_iLastCol = 0;
		int t_iLastRow = a_sheet.getLastRowNum();
		for ( int i=0; i < t_iLastRow; i++ ) {
			Row t_row = a_sheet.getRow(i);
			if ( t_row == null )
				continue;
			int t_iLastCell = t_row.getLastCellNum();
			if ( t_iLastCol < t_iLastCell )
				t_iLastCol = t_iLastCell;
		}
		for ( int i=0; i < t_iLastCol; i++ )
			a_sheet.autoSizeColumn(i, true);
	}

}
