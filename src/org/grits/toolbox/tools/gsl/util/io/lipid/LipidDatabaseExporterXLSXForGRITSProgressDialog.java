package org.grits.toolbox.tools.gsl.util.io.lipid;

import java.io.IOException;

import org.grits.toolbox.tools.gsl.database.lipid.LipidData;
import org.grits.toolbox.tools.gsl.database.lipid.LipidDatabase;
import org.grits.toolbox.widgets.processDialog.GRITSProgressDialog;
import org.grits.toolbox.widgets.processDialog.ProgressBarWithErrorListener;

/**
 * Extension of LipidDatabaseExporterXLSX for the use of progress bar.
 * @author Masaaki Matsubara
 *
 */
public class LipidDatabaseExporterXLSXForGRITSProgressDialog extends LipidDatabaseExporterXLSX {

	private GRITSProgressDialog m_dlgProg;
	private ProgressBarWithErrorListener m_barProg;
	private int m_nTotalLipid;
	private int m_nCurrentLipid;
	private int m_nProgressValue;

	public LipidDatabaseExporterXLSXForGRITSProgressDialog(LipidDatabase a_ldOutput, String a_strOutputFile, GRITSProgressDialog a_dlgProg) throws IOException {
		super(a_ldOutput, a_strOutputFile);
		this.m_dlgProg = a_dlgProg;
		this.m_barProg = a_dlgProg.getMinorProgressBarListener(0);
		this.m_nTotalLipid = a_ldOutput.getLipidData().size();
		this.m_nCurrentLipid = 0;
		this.m_nProgressValue = 0;
	}

	@Override
	public boolean createBook() {
		this.m_barProg.setMaxValue(this.m_nTotalLipid*2+3);
		this.m_barProg.setProgressValue(0);
		if ( !super.createBook() )
			return false;
		this.m_nProgressValue++;
		this.m_barProg.setProgressValue(this.m_nProgressValue);
		this.m_barProg.setProgressMessage("Done!");
		return true;
	}

	@Override
	protected boolean createSheetForDatabaseInformation() {
		if (this.m_dlgProg.isCanceled()) {
			this.closeBook();
			return false;
		}

		this.m_nProgressValue++;
		this.m_barProg.setProgressValue(this.m_nProgressValue);
		this.m_barProg.setProgressMessage("Creating DatabaseInformation sheet...");
		return super.createSheetForDatabaseInformation();
	}

	@Override
	protected boolean createSheetForDatabase(boolean a_bPerMe) {
		if (this.m_dlgProg.isCanceled()) {
			this.closeBook();
			return false;
		}

		this.m_nCurrentLipid = 0;
		this.m_nProgressValue++;
		this.m_barProg.setProgressValue(this.m_nProgressValue);
		this.m_barProg.setProgressMessage("Creating database sheet"+((a_bPerMe)? " for permethylated" : "")+"...");
		return super.createSheetForDatabase(a_bPerMe);
	}

	@Override
	protected boolean createRowForDataLine(LipidData a_ld, int a_nSubsts, boolean a_bPerMe) {
		if (this.m_dlgProg.isCanceled()) {
			this.closeBook();
			return false;
		}

		this.m_nProgressValue++;
		this.m_nCurrentLipid++;
		this.m_barProg.setProgressValue(this.m_nProgressValue);
		this.m_barProg.setProgressMessage("Creating"+((a_bPerMe)? " permethylated" : "")+" Lipid row: " + this.m_nCurrentLipid + " of " + this.m_nTotalLipid);
		return super.createRowForDataLine(a_ld, a_nSubsts, a_bPerMe);
	}

	
}
