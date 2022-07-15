package org.grits.toolbox.tools.gsl.annotation.process;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.grits.toolbox.tools.gsl.dango.DANGOAnnotation;
import org.grits.toolbox.widgets.progress.CancelableThread;
import org.grits.toolbox.widgets.progress.IProgressHandler;
import org.grits.toolbox.widgets.progress.IProgressListener;
import org.grits.toolbox.widgets.progress.IProgressThreadHandler;
import org.grits.toolbox.widgets.tools.GRITSProcessStatus;
import org.grits.toolbox.widgets.tools.GRITSWorker;

/**
 * An extension of GRITSWoker for DANGO annotation processes.
 * @author Masaaki Matsubara
 * @see org.grits.toolbox.tools.gsl.dango.DANGOAnnotation
 *
 */
public class DANGOWorker extends GRITSWorker {
	private static final Logger logger = Logger.getLogger(DANGOWorker.class);

	private DANGOAnnotation m_dango;

	protected IProgressThreadHandler m_progressThreadHandler;
	protected IProgressHandler m_progressHandler;

	/**
	 * @param dango DANGOAnnotation to be processed
	 * @param progressHandler GRITSProcessDialog
	 * @param progressThreadHandler GRITSProcessDialog
	 * @param progressMajors List of ProgressListner showing major progress bar for this worker
	 * @param progressDANGOAnnotationMinors List of ProgressListner showing minor progress bar for DANGO annotation
	 */
	public DANGOWorker(DANGOAnnotation dango, IProgressHandler progressHandler,
			IProgressThreadHandler progressThreadHandler, 
			List<IProgressListener> progressMajors, List<IProgressListener> progressDANGOAnnotationMinors) {
//			, List<IProgressListener> progressMSAnnotationMinors) {
		this.m_dango = dango;
		this.m_progressThreadHandler = progressThreadHandler;
		setProgressListeners(progressMajors);
		this.m_progressHandler = progressHandler;

		dango.setProgressListeners(progressDANGOAnnotationMinors);
	}

	/**
	 * Do following processes while showing progress bars: initialize, process scans and archive results.
	 */
	@Override
	public int doWork() {
		try{
			setMaxValue(3);
			updateListeners("Initializing...", 0);
			CancelableThread s1 = getInitializeProcess();
			int iRes = runProcess(s1);
			if (s1.isCanceled()) // check this way since the iRes may show OK although the process is canceled
				return GRITSProcessStatus.CANCEL;
			if( iRes != GRITSProcessStatus.OK ) {
				return iRes;
			}
/*
			updateListeners("Applying database filters...", 1);
			CancelableThread s2 = getApplyFiltersProcess();
			runProcess(s2);
			if (s2.isCanceled()) // check this way since the iRes may show OK although the process is canceled
				return GRITSProcessStatus.CANCEL;
			if( iRes != GRITSProcessStatus.OK ) {
				return iRes;
			}
*/
			updateListeners("Performing DANGO annotation...", 1);
			CancelableThread s3 = getProcessScansProcess();
			iRes = runProcess(s3);
			if (s3.isCanceled()) // check this way since the iRes may show OK although the process is canceled
				return GRITSProcessStatus.CANCEL;

			updateListeners("Archiving annotation results...", 2);
			CancelableThread s4 = getArchiveProcess();
			iRes = runProcess(s4);
			if (s4.isCanceled()) // check this way since the iRes may show OK although the process is canceled
				return GRITSProcessStatus.CANCEL;

			updateListeners("Done!", 3);
			return iRes;
		}catch(Exception e){
			logger.error("Error in annotateGlycanStructure", e);
		}
		return GRITSProcessStatus.ERROR;
	}

	protected int runProcess( CancelableThread cp ) {
		try {
			cp.setProgressThreadHandler(this.m_progressThreadHandler); // override the thread's default handler with the progress handler
			this.m_progressHandler.setThread(cp); // make sure the progresshandler has the thread so it can be notified upon cancel/finish
			cp.start();	
			while ( ! cp.isCanceled() && ! cp.isFinished() && cp.isAlive() ) 
			{
				if (!Display.getDefault().readAndDispatch()) 
				{
			//		Display.getDefault().sleep();
				}
			}
			if( cp.isCanceled() ) {
				this.m_dango.setCanceled(true);
				cp.interrupt();
				return GRITSProcessStatus.CANCEL;
			} else {
				return GRITSProcessStatus.OK;
			}			
		} catch( Exception ex ) {
			logger.error("Error in runProcess", ex);
		}
		return GRITSProcessStatus.ERROR;
	}

	protected CancelableThread getInitializeProcess() {
		try {
			CancelableThread cp = new CancelableThread() {
				@Override
				public boolean threadStart(IProgressThreadHandler a_progressThreadHandler) throws Exception {
					logger.debug("Starting job: getInitializeProcess");
					try {
						m_dango.initialize();
						return true;
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						return false;
					}
				}
			};
			return cp;
		} catch( Exception ex ) {
			logger.error("Error in getInitializeProcess", ex);
		}
		return null;
	}
/*
	protected CancelableThread getApplyFiltersProcess() {
		try {
			CancelableThread cp = new CancelableThread() {
				@Override
				public boolean threadStart(IProgressThreadHandler a_progressThreadHandler) throws Exception {
					logger.debug("Starting job: getApplyFiltersProcess");
					try {
						gsa.applyFilters();
						return true;
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						return false;
					}
				}
			};
			return cp;
		} catch( Exception ex ) {
			logger.error("Error in getApplyFiltersProcess", ex);			
		}
		return null;
	}
*/
	protected CancelableThread getProcessScansProcess() {
		try {
			CancelableThread cp = new CancelableThread() {
				@Override
				public boolean threadStart(IProgressThreadHandler a_progressThreadHandler) throws Exception {
					logger.debug("Starting job: processScans");
					try {
						int iRes = m_dango.processAnnotation();
						if (iRes == GRITSProcessStatus.ERROR) {
							logger.info("An error has occurred during processing scans");
						}
						return true;
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						return false;
					}
				}
			};
			return cp;
		} catch( Exception ex ) {
			logger.error("Error in getProcessScansProcess", ex);
		}
		return null;
	}

	private CancelableThread getArchiveProcess() {
		try {
			CancelableThread cp = new CancelableThread() {
				@Override
				public boolean threadStart(IProgressThreadHandler a_progressThreadHandler) throws Exception {
					logger.debug("Starting job: getArchiveProcess");
					try {
						if ( !m_dango.archiveData() )
							logger.info("An error has occurred during archiving data");
						return true;
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						return false;
					}
				}
			};
			return cp;
		} catch( Exception ex ) {
			logger.error("Error in getArchiveProcess", ex);
		}
		return null;
	}


}
