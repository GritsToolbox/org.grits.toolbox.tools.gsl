package org.grits.toolbox.tools.gsl.wizard.lipidgeneration;

import org.apache.log4j.Logger;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.grits.toolbox.tools.gsl.database.lipid.LipidDatabase;
import org.grits.toolbox.tools.gsl.util.generator.CeramideDatabaseGenerator;
import org.grits.toolbox.tools.gsl.util.generator.LipidDatabaseGeneratorAbstract;
import org.grits.toolbox.tools.gsl.util.generator.SphingosineDatabaseGenerator;
import org.grits.toolbox.tools.gsl.util.io.lipid.LipidDatabaseExporterXLSXForGRITSProgressDialog;
import org.grits.toolbox.tools.gsl.util.io.lipid.LipidDatabaseFileHandler;
import org.grits.toolbox.widgets.processDialog.GRITSProgressDialog;
import org.grits.toolbox.widgets.progress.CancelableThread;
import org.grits.toolbox.widgets.progress.IProgressThreadHandler;
import org.grits.toolbox.widgets.progress.IProgressListener.ProgressType;
import org.grits.toolbox.widgets.tools.GRITSProcessStatus;
import org.grits.toolbox.widgets.tools.GRITSWorker;

/**
 * Wizard for lipid database generator.
 * @author Masaaki Matsubara
 *
 */
public class LipidDatabaseGenerationWizard extends Wizard {
	private static Logger logger = Logger.getLogger(LipidDatabaseGenerationWizard.class);

	private DataEntryPage m_pageEntry;
	private LipidGenerationSettingPage m_pageLipidParam;

	public LipidDatabaseGenerationWizard() {
		setWindowTitle("Lipid Database Generation");
	}

	@Override
	public void addPages() {
		this.m_pageEntry = new DataEntryPage();
		this.m_pageLipidParam = new LipidGenerationSettingPage();
		addPage(this.m_pageEntry);
		addPage(this.m_pageLipidParam);
	}

	@Override
	public boolean performFinish() {
		LipidDatabaseGeneratorAbstract t_genDatabase = this.setParametersForLipidDatabase();
		String t_strFileName = this.m_pageEntry.getOutputFileName();

		GRITSProgressDialog t_progressDialog = new GRITSProgressDialog(getShell(), 1, true);
		t_progressDialog.open();
		t_progressDialog.getMajorProgressBarListener().setMaxValue(2);
		t_progressDialog.setGritsWorker(new GRITSWorker() {
			
			@Override
			public int doWork() {
				try {
					updateListeners("Generating and exporting database...", 1);
					
					CancelableThread t_thread = new CancelableThread() {
						@Override
						public boolean threadStart(IProgressThreadHandler a_progressThreadHandler) throws Exception {
							// Generate lipid database
							t_progressDialog.getMinorProgressBarListener(0).setMaxValue(1);
							t_progressDialog.getMinorProgressBarListener(0).setProgressValue(0);
							t_progressDialog.getMinorProgressBarListener(0).setProgressMessage("Generating lipid database...");

							t_genDatabase.generate();

							t_progressDialog.getMinorProgressBarListener(0).setProgressValue(1);
							t_progressDialog.getMinorProgressBarListener(0).setProgressMessage("Done!");

							LipidDatabase t_ldOutput = t_genDatabase.getLipidDatabase();
							if ( t_ldOutput.getLipidData().isEmpty() )
								return false;

							// Export XML file
							t_progressDialog.getMinorProgressBarListener(0).setMaxValue(1);
							t_progressDialog.getMinorProgressBarListener(0).setProgressValue(0);
							t_progressDialog.getMinorProgressBarListener(0).setProgressMessage("Output XML file...");

							LipidDatabaseFileHandler.exportXML(t_strFileName, t_ldOutput);

							t_progressDialog.getMinorProgressBarListener(0).setProgressValue(1);
							t_progressDialog.getMinorProgressBarListener(0).setProgressMessage("Done!");

							// Create book
							try {
								String t_strXLSXFileName = t_strFileName.replace(".xml", ".xlsx");
								LipidDatabaseExporterXLSXForGRITSProgressDialog t_exportExcel
									= new LipidDatabaseExporterXLSXForGRITSProgressDialog(t_ldOutput, t_strXLSXFileName, t_progressDialog);
								if ( !t_exportExcel.createBook() )
									return false;
							
								// Export file
								t_exportExcel.write();
								return true;
							} catch (Exception e) {
								logger.error(e.getMessage(), e);
								return false;
							}
						}
					};
					t_thread.setProgressThreadHandler(t_progressDialog);
					t_progressDialog.setThread(t_thread);
					t_progressDialog.getMinorProgressBarListener(0).setProgressType(ProgressType.Determinant);
					t_thread.start();  
					while ( ! t_thread.isCanceled() && ! t_thread.isFinished() && t_thread.isAlive() ) {
						if (!Display.getDefault().readAndDispatch()) {
							//Display.getDefault().sleep();
						}
					}
					if( t_thread.isCanceled() ) {
						t_thread.interrupt();
						return GRITSProcessStatus.CANCEL;
					}
				} catch( Exception e ) {
					logger.error(e.getMessage(), e);
				}
				updateListeners("Done", 2);
				return GRITSProcessStatus.OK;
			}
			
		});
		t_progressDialog.startWorker();
		return true;
	}

	private LipidDatabaseGeneratorAbstract setParametersForLipidDatabase() {
		LipidDatabaseGeneratorAbstract t_genDatabase;
		if ( this.m_pageLipidParam.isSelectedCeramide() ) {
			CeramideDatabaseGenerator t_genCerDatabase = new CeramideDatabaseGenerator();
			// Set generation information
			/// For Sphingosines
			t_genCerDatabase.setCarbonLengthesForSphingosine(                this.m_pageLipidParam.getCarbonLengthForSphingosine()           );
			t_genCerDatabase.setNumbersOfHydrxylGroupsForSphingosine(        this.m_pageLipidParam.getNumberOfHydroxyGroupsForSphingosine()  );
			t_genCerDatabase.setNumbersOfUnsaturationsForSphingosine(        this.m_pageLipidParam.getNumberOfUnsaturationForSphingosine()   );
			t_genCerDatabase.allowOnlyEvenNumberedCarbonLengthOfSphingosine( this.m_pageLipidParam.isSelectedOnlyEvenNumbersForSphingosine() );
			/// For FattyAcids
			t_genCerDatabase.setCarbonLengthesForFattyAcid(                this.m_pageLipidParam.getCarbonLengthForFattyAcid()           );
			t_genCerDatabase.setNumbersOfHydrxylGroupsForFattyAcid(        this.m_pageLipidParam.getNumberOfHydroxyGroupsForFattyAcid()  );
			t_genCerDatabase.setNumbersOfUnsaturationsForFattyAcid(        this.m_pageLipidParam.getNumberOfUnsaturationForFattyAcid()   );
			t_genCerDatabase.allowOnlyEvenNumberedCarbonLengthOfFattyAcid( this.m_pageLipidParam.isSelectedOnlyEvenNumbersForFattyAcid() );

			t_genDatabase = t_genCerDatabase;
		} else {
			SphingosineDatabaseGenerator t_genSphDatabase = new SphingosineDatabaseGenerator();
			// Set generation information
			t_genSphDatabase.setCarbonLengths(                  this.m_pageLipidParam.getCarbonLengthForSphingosine()           );
			t_genSphDatabase.setNumbersOfHydroxylGroups(         this.m_pageLipidParam.getNumberOfHydroxyGroupsForSphingosine()  );
			t_genSphDatabase.setNumbersOfDoubleBonds(         this.m_pageLipidParam.getNumberOfUnsaturationForSphingosine()   );
			t_genSphDatabase.allowOnlyEvenNumberedCarbonLength( this.m_pageLipidParam.isSelectedOnlyEvenNumbersForSphingosine() );

			t_genDatabase = t_genSphDatabase;
		}

		// Set meta information
		t_genDatabase.setLipidDatabaseName(  this.m_pageEntry.getDatabaseTitle()       );
		t_genDatabase.setDescription(        this.m_pageEntry.getDatabaseDescription() );
		t_genDatabase.setVersion(            this.m_pageEntry.getVersion()             );
		t_genDatabase.setCreatorName(        this.m_pageEntry.getCreatorName()         );
		t_genDatabase.setCreatorInstitution( this.m_pageEntry.getCreatorInstitution()  );

		t_genDatabase.setGeneratedBy("Lipid Database Generation Wizard");

		return t_genDatabase;
	}
}
