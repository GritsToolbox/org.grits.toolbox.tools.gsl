package org.grits.toolbox.tools.gsl.util.io.lipid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.grits.toolbox.tools.gsl.database.lipid.LipidClass;
import org.grits.toolbox.tools.gsl.database.lipid.LipidData;
import org.grits.toolbox.tools.gsl.database.lipid.LipidDatabase;
import org.grits.toolbox.tools.gsl.database.lipid.LipidParameters;
import org.grits.toolbox.tools.gsl.structure.LipidFragmentInfo;
import org.grits.toolbox.tools.gsl.structure.LipidNameParser;
import org.grits.toolbox.tools.gsl.structure.lipid.Ceramide;
import org.grits.toolbox.tools.gsl.structure.lipid.ILipid;
import org.grits.toolbox.tools.gsl.structure.lipid.Lipid;
import org.grits.toolbox.tools.gsl.util.generator.structure.LipidFragmenter;
import org.grits.toolbox.tools.gsl.util.io.excel.ExporterXLSXAbstract;
import org.grits.toolbox.tools.gsl.util.mass.CeramideChemicalComposition;
import org.grits.toolbox.tools.gsl.util.mass.ChemicalComposition;
import org.grits.toolbox.tools.gsl.util.mass.LipidChemicalComposition;
import org.grits.toolbox.tools.gsl.database.IonizationType;

/**
 * Class for exporting LipidDatabase to .XLSX file.
 * @author Masaaki Matsubara
 *
 */
public class LipidDatabaseExporterXLSX extends ExporterXLSXAbstract{

	private LipidDatabase m_ldOutput;
	private boolean m_bOutPH;
	private boolean m_bOutMH;
	private boolean m_bOutPNa;
	private boolean m_bOutPHMH2O;

	public LipidDatabaseExporterXLSX(LipidDatabase a_ldOutput, String a_strOutputFile) throws IOException {
		super(a_strOutputFile);

		this.m_ldOutput = a_ldOutput;

		this.m_bOutPH = true;
		this.m_bOutMH = true;
		this.m_bOutPNa = true;
		this.m_bOutPHMH2O = true;
	}

	public boolean isOutputPlusH() {
		return m_bOutPH;
	}

	public boolean isOutputMinusH() {
		return m_bOutMH;
	}

	public boolean isOutputPlusNa() {
		return m_bOutPNa;
	}

	public boolean isOutputPlusHMinusH2O() {
		return m_bOutPHMH2O;
	}

	public void setOutputPlusH(boolean a_bOutPH) {
		this.m_bOutPH = a_bOutPH;
	}

	public void setOutputMinusH(boolean a_bOutMH) {
		this.m_bOutMH = a_bOutMH;
	}

	public void setOutputPlusNa(boolean a_bOutPNa) {
		this.m_bOutPNa = a_bOutPNa;
	}

	public void setOutputPlusHMinusH2O(boolean a_bOutPHMH2O) {
		this.m_bOutPHMH2O = a_bOutPHMH2O;
	}

	@Override
	public boolean createBook() {
		if ( !this.createSheetForDatabaseInformation() ) {
			System.err.println("cannot create sheet for information");
			this.closeBook();
			return false;
		}
		// For lipid database
		if ( !this.createSheetForDatabase(false) ) {
			System.err.println("cannot create sheet for database");
			this.closeBook();
			return false;
		}
		// For permethylated lipid database
		if ( !this.createSheetForDatabase(true) ) {
			System.err.println("cannot create sheet for permethylated database");
			this.closeBook();
			return false;
		}
		return true;
	}

	protected boolean createSheetForDatabaseInformation() {
		Row t_row;
		Cell t_cell;
		int t_nCol;

		// Get first sheet with name
		this.m_sheetCurent = this.createSheet("Database Information");
		this.m_nRowCurrent = 0;

		// Lipid database name
//		String[] t_listName = {"Lipid database name:", this.m_ldOutput.getLipidDatabaseName()};
		this.createRow( Arrays.asList("Lipid database name:", this.m_ldOutput.getName()) );

		// Version
//		String[] t_listVer = {"Version:", this.m_ldOutput.getVersion()};
		this.createRow( Arrays.asList("Version:", this.m_ldOutput.getVersion()) );

		// Description
		t_row = this.m_sheetCurent.createRow(this.m_nRowCurrent++);
		t_nCol = 0;

		t_cell = t_row.createCell(t_nCol++);
		t_cell.setCellType(CellType.STRING);
		t_cell.setCellValue("Description:");

		t_cell = t_row.createCell(t_nCol);
		t_cell.setCellType(CellType.STRING);
		// Set style for wrap text
		CellStyle t_styleStringWrap = this.m_book.createCellStyle();
		t_styleStringWrap.setVerticalAlignment(VerticalAlignment.TOP);
		t_styleStringWrap.setWrapText(true);
		t_cell.setCellStyle(t_styleStringWrap);
		t_cell.setCellValue(this.m_ldOutput.getDescription());

		// Information for the person created database
		this.m_nRowCurrent++;
//		String[] t_listInfo = {"Information of the person created database:"};
		this.createRow(Arrays.asList("Information of the person created database:"));

		/// For name
//		String[] t_listUName = {"Name:", this.m_ldOutput.getUserName()};
		this.createRow(Arrays.asList("Name:", this.m_ldOutput.getCreatorName()));

		/// For institution
//		String[] t_listUInst = {"Institution:", this.m_ldOutput.getUserInstitution()};
		this.createRow(Arrays.asList("Institution:", this.m_ldOutput.getCreatorInstitution()));

		// Information for the inputed parameters
		this.m_nRowCurrent++;
//		String[] t_listParam = {"Inputed parameters:"};
		this.createRow(Arrays.asList("Inputed parameters:"));

		/// For parameters of each substructure
		//// For Sphingosine
		this.createRow(Arrays.asList(LipidClass.SPHINGOSINE.getName()+":"));
		this.addRowsForParameters( this.m_ldOutput.getOrigin().getSphingosine() );

		//// For Fatty Acid
		if ( this.m_ldOutput.getOrigin().getFattyAcid() != null ) {
			this.createRow(Arrays.asList(LipidClass.FATTY_ACID.getName()+":"));
			this.addRowsForParameters( this.m_ldOutput.getOrigin().getFattyAcid() );
		}

		// Number of generated lipids
		this.m_nRowCurrent++;
		this.createRow(Arrays.asList("# of generated lipids:", ""+this.m_ldOutput.getLipidData().size()) );
		
		// Arrange column size
		this.autoSizeAllColumn(this.m_sheetCurent);

		return true;
	}

	private void addRowsForParameters(LipidParameters a_params) {

		/// Carbon length
		this.createRow(Arrays.asList("Carbon length:", a_params.getCarbonLengths()));

		/// Number of hydroxyl groups
		if ( !a_params.getNumberOfHydroxylGroups().isEmpty() )
			this.createRow(Arrays.asList("# of Hydroxyl groups:", a_params.getNumberOfHydroxylGroups()));

		/// Number of O-acetyl groups
		if ( !a_params.getNumberOfOAcetylGroups().isEmpty() )
			this.createRow(Arrays.asList("# of O-acetyl groups:", a_params.getNumberOfOAcetylGroups()));

		/// Number of unsaturations
		if ( !a_params.getNumberOfDoubleBonds().isEmpty() )
			this.createRow(Arrays.asList("# of unsaturations:", a_params.getNumberOfDoubleBonds()));

		/// Only even number for carbon length
		if ( a_params.getIsAllowedOnlyEvenNumbers() )
			this.createRow(Arrays.asList("Only allow even carbon length"));
	}

	/**
	 * Creates sheet for lipid database
	 * @param a_bPerMe - Whether or not the lipids are permethylated
	 * @return true if sheet is created successfully
	 */
	protected boolean createSheetForDatabase(boolean a_bPerMe) {
		// Get sheet with name
		this.m_sheetCurent = this.createSheet("Lipid Database"+ ((a_bPerMe)? " PerMe" : "") );
		this.m_nRowCurrent = 0;

		// Check maximum number of substructures
		int t_nSubsts = 0;
		for ( LipidData t_ld : this.m_ldOutput.getLipidData() ) {
			if ( t_ld.getSubstructures().isEmpty() ) continue;
			if ( t_nSubsts < t_ld.getSubstructures().size() )
				t_nSubsts = t_ld.getSubstructures().size();
		}

		// Create headers
		this.createRowForDataHeader(t_nSubsts, a_bPerMe);

		// Create data lines
		for ( LipidData t_ld : this.m_ldOutput.getLipidData() ) {
			if ( !this.createRowForDataLine(t_ld, t_nSubsts, a_bPerMe) )
				return false;
		}

		// Freeze pane
		this.m_sheetCurent.createFreezePane(1, 2);
		// Auto filter
		this.m_sheetCurent.setAutoFilter( new CellRangeAddress(1,1,0,7+t_nSubsts) );
		// Merge header's cells
		if ( t_nSubsts > 0 )
			this.m_sheetCurent.addMergedRegion( new CellRangeAddress(0,0,2,1+t_nSubsts) );
		int t_nRange = 1;
		if ( this.m_bOutPH ) t_nRange++;
		if ( this.m_bOutMH ) t_nRange++;
		if ( this.m_bOutPNa ) t_nRange++;
		if ( this.m_bOutPHMH2O ) t_nRange++;
		this.m_sheetCurent.addMergedRegion( new CellRangeAddress(0,0,3+t_nSubsts,2+t_nSubsts+t_nRange) );
		// Auto size column
		this.autoSizeAllColumn(this.m_sheetCurent);

		return true;
	}

	protected boolean createRowForDataHeader(int a_nSubsts, boolean a_bPerMe) {
		// Create headers
		String t_strMassTitle = (a_bPerMe)? "Permethylated Mass" : "Mass";
		String t_strWaterLoss = (a_bPerMe)? "MeOH" : "H2O";
		List<String> t_listHeaders1 = new ArrayList<>();
		Collections.addAll(t_listHeaders1,
				"", "", "", t_strMassTitle
				);
		List<String> t_listHeaders2 = new ArrayList<>();
		Collections.addAll(t_listHeaders2,
				"Common Name", "Type", "Composition", "M", "M - "+t_strWaterLoss
				);
		if ( this.m_bOutPH ) {
			t_listHeaders1.add( "" );
			t_listHeaders2.add( "[M + H]+" );
		}
		if ( this.m_bOutMH ) {
			t_listHeaders1.add( "" );
			t_listHeaders2.add( "[M - H]-" );
		}
		if ( this.m_bOutPNa ) {
			t_listHeaders1.add( "" );
			t_listHeaders2.add( "[M + Na]+" );
		}
		if ( this.m_bOutPHMH2O ) {
			t_listHeaders1.add( "" );
			t_listHeaders2.add( "[M + H - "+t_strWaterLoss+"]+" );
		}
		for ( int i=0; i<a_nSubsts; i++ ) {
			t_listHeaders1.add(2+i, (i==0)? "Substructures" : "" );
			t_listHeaders2.add(2+i, ""+(i+1) );
		}
		// For fragment column
		t_listHeaders1.addAll( Arrays.asList("", "Fragments") );
		t_listHeaders2.addAll( Arrays.asList("", "") );

		this.createRow(t_listHeaders1);
		this.createRow(t_listHeaders2);

		return true;
	}

	protected boolean createRowForDataLine(LipidData a_ld, int a_nSubsts, boolean a_bPerMe) {
		List<String> t_listMasses = new ArrayList<>();

		Collections.addAll( t_listMasses,
				a_ld.getCommonName(), // Lipid name
				a_ld.getLipidClass().getAbbreviation() // Lipid Type
				);

		ILipid t_lipid = LipidNameParser.parseLipidName(a_ld.getCommonName());

		// Calculate mass
		ChemicalComposition t_calcMass;
		if ( t_lipid instanceof Ceramide )
			t_calcMass = new CeramideChemicalComposition((Ceramide)t_lipid);
		else
			t_calcMass = new LipidChemicalComposition((Lipid)t_lipid);
		if ( a_bPerMe )
			t_calcMass.derivatize();
		t_listMasses.add( t_calcMass.getFormula() );
		t_listMasses.add( t_calcMass.getMonoisotopicMass() ); // M
		t_listMasses.add( t_calcMass.getMonoisotopicMass(IonizationType.NOIONMH2O) ); // M - H2O
		if ( this.m_bOutPH )
			t_listMasses.add( t_calcMass.getMonoisotopicMass(IonizationType.PH) ); // [M + H]+
		if ( this.m_bOutMH )
			t_listMasses.add( t_calcMass.getMonoisotopicMass(IonizationType.MH) ); // [M - H]-
		if ( this.m_bOutPNa )
			t_listMasses.add( t_calcMass.getMonoisotopicMass(IonizationType.PNA) ); // [M + Na]+
		if ( this.m_bOutPHMH2O )
			t_listMasses.add( t_calcMass.getMonoisotopicMass(IonizationType.PHMH2O) ); // [M + H - H2O]+
/*
		List<String> t_listMassesPerMe = new ArrayList<>();
		for ( Mass t_mass : a_ld.getMasses() ) {
			List<String> t_list = t_listMasses;
			if ( t_mass.getIsPermethylated() )
				t_list = t_listMassesPerMe;
			if ( !t_mass.getIsPermethylated() && t_mass.getIonizationType() == IonizationType.NOION )
				t_listMasses.add( t_mass.getComposition() );
			t_list.add( t_mass.getValue() );
		}
		t_listMasses.addAll(t_listMassesPerMe);
/*
		Collections.addAll( t_listMasses,
				a_ld.getCommonName(), // Lipid name
				(a_ld.getLipidClass() == LipidClass.CERAMIDE )? "Cer" : "Sp", // Lipid Type
				a_ld.getComposition(), // Composition
				a_ld.getExactMass(),         // M
				a_ld.getMassPlusH(),         // [M + H]+
				a_ld.getMassMinusH(),        // [M - H]-
				a_ld.getMassPlusNa(),        // [M + Na]+
				a_ld.getMassPlusHMinusH2O(), // [M + H - H2O]+
				a_ld.getExactMassPerMe(),        // Permethylated M
				a_ld.getMassPlusHPerMe(),        // Permethylated [M + H]+
				a_ld.getMassMinusHPerMe(),       // Permethylated [M - H]-
				a_ld.getMassPlusNaPerMe(),       // Permethylated [M + Na]+
				a_ld.getMassPlusHMinusH2OPerMe() // Permethylated [M + H - H2O]+
				);
*/
		for ( int i=0; i<a_nSubsts; i++ ) {
			String t_strSubst = "";
			if ( i < a_ld.getSubstructures().size() )
				t_strSubst = a_ld.getSubstructures().get(i);
			t_listMasses.add(2+i, t_strSubst);
		}

		// For fragments
		LipidFragmenter t_genFrags = new LipidFragmenter();
		List<LipidFragmentInfo> t_listFrags = t_genFrags.fragment(t_lipid);
		if ( t_listFrags != null && !t_listFrags.isEmpty() ) {
			t_listMasses.add("");
			for ( LipidFragmentInfo t_lipInfo : t_listFrags ) {
				ILipid t_lip = t_lipInfo.getFragment();
				// Add name
				t_listMasses.add(t_lip.getName());
				// Calculate mass
				if ( t_lip instanceof Ceramide )
					t_calcMass = new CeramideChemicalComposition((Ceramide)t_lip);
				else
					t_calcMass = new LipidChemicalComposition((Lipid)t_lip);
				if ( a_bPerMe )
					t_calcMass.derivatize();
				// Add mass for only [M + H]+
				t_listMasses.add( t_calcMass.getMonoisotopicMass(IonizationType.PH) );
			}
		}
		
		this.createRow(t_listMasses);
		return true;
	}

}
