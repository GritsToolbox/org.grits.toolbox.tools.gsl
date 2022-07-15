package org.grits.toolbox.tools.gsl.handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Shell;
import org.grits.toolbox.core.dataShare.PropertyHandler;
import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.core.datamodel.property.ProjectProperty;
import org.grits.toolbox.core.datamodel.property.PropertyDataFile;
import org.grits.toolbox.core.datamodel.util.DataModelSearch;
import org.grits.toolbox.core.utilShare.ErrorUtils;
import org.grits.toolbox.entry.ms.annotation.property.MSAnnotationProperty;
import org.grits.toolbox.entry.ms.annotation.property.datamodel.MSAnnotationFileInfo;
import org.grits.toolbox.entry.ms.property.MassSpecProperty;
import org.grits.toolbox.entry.ms.property.datamodel.MSPropertyDataFile;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.handler.NewGelatoHandler;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.MSGlycanAnnotationWizard;
import org.grits.toolbox.ms.file.FileCategory;
import org.grits.toolbox.ms.file.MSFile;
import org.grits.toolbox.ms.om.data.AnalyteSettings;
import org.grits.toolbox.ms.om.data.Data;
import org.grits.toolbox.ms.om.data.DataHeader;
import org.grits.toolbox.ms.om.data.GlycanFilter;
import org.grits.toolbox.ms.om.data.LipidDatabase;
import org.grits.toolbox.ms.om.data.Method;
import org.grits.toolbox.tools.gsl.annotation.entry.property.MSGlycolipidAnnotationProperty;
import org.grits.toolbox.tools.gsl.annotation.entry.property.datamodel.MSGlycolipidAnnotationMetaData;
import org.grits.toolbox.tools.gsl.annotation.process.DANGOWorker;
import org.grits.toolbox.tools.gsl.dango.DANGOAnnotation;
import org.grits.toolbox.tools.gsl.util.DatabaseUtilsForGSL;
import org.grits.toolbox.widgets.processDialog.GRITSProgressDialog;
import org.grits.toolbox.widgets.progress.IProgressListener;
import org.grits.toolbox.widgets.tools.GRITSProcessStatus;

/**
 * Class for handling DANGO annotation. Extends NewGelatoHandler.
 * First, the workspace location for storing the DANGO annotation result are specified from parent entry (MSEntry) information.
 * After that, DANGOAnnotation is performed and then the generated archive files of the annotation results are stored and associated to the workspace.
 * @author Masaaki Matsubara
 *
 */
public class DANGOHandler extends NewGelatoHandler {

	//log4J Logger
	private static final Logger logger = Logger.getLogger(DANGOHandler.class);

	private String m_strArchiveFilePath;

	/**
	 * @return String (the workspace location stored in the PropertyHandler)
	 */
	private String getWorkspaceLocation() {
		return PropertyHandler.getVariable("workspace_location");
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.handler.NewGelatoHandler#getTempFolder()
	 */
	@Override
	protected File getTempFolder() {
		String workspaceLocation = this.getWorkspaceLocation();
		String t_tempFolder = workspaceLocation + ".temp" + File.separator + "DANGO_" + Long.toString(System.currentTimeMillis()) + File.separator;
		File t_tempFolderFile = new File(t_tempFolder);
		t_tempFolderFile.mkdirs();
		return t_tempFolderFile;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.handler.NewGelatoHandler#getAnnotationFolder(org.grits.toolbox.core.datamodel.Entry)
	 */
	@Override
	protected File getAnnotationFolder( Entry msEntry ) {

		String workspaceLocation = this.getWorkspaceLocation();
		MSGlycolipidAnnotationProperty t_property = new MSGlycolipidAnnotationProperty();
		Entry projectEntry = DataModelSearch.findParentByType(msEntry, ProjectProperty.TYPE);
		String projectName = projectEntry.getDisplayName();

		String msAnnotationFolder = workspaceLocation + projectName + File.separator + t_property.getArchiveFolder();	
		File msAnnotationFolderFile = new File(msAnnotationFolder);
		msAnnotationFolderFile.mkdirs();

		return msAnnotationFolderFile;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.handler.NewGelatoHandler#getMSAnnotationProperty(org.eclipse.swt.widgets.Shell, org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.MSGlycanAnnotationWizard, java.lang.String)
	 */
	@Override
	protected MSAnnotationProperty getMSAnnotationProperty(Shell shell, MSGlycanAnnotationWizard wizard, String msAnnotationFolder) {
		MSGlycolipidAnnotationProperty t_property = new MSGlycolipidAnnotationProperty();
		MSGlycolipidAnnotationMetaData metaData = new MSGlycolipidAnnotationMetaData();
		t_property.setMSAnnotationMetaData(metaData);
		try {
			metaData.setAnnotationId(this.createRandomId(msAnnotationFolder));
			metaData.setDescription(wizard.getInitial().getMSDescription());
			metaData.setVersion(MSGlycolipidAnnotationMetaData.CURRENT_VERSION);
			metaData.setName(t_property.getMetaDataFileName());
		} catch (IOException e2) {
			logger.error(e2.getMessage(), e2);
			ErrorUtils.createErrorMessageBox(shell, "Exception", e2);
			return null;
		}

		return t_property;
	}

	/**
	 * Updates the database path references to overcome any issues with sharing workspaces between different computers.
	 * 
	 * @param method Method having glycan and lipid database to be updated references
	 */
	private void updateDatabaseReferences(Method method) {
		for( AnalyteSettings aSettings : method.getAnalyteSettings() ) {
			GlycanFilter filter = aSettings.getGlycanSettings().getFilter();
			if (filter.getDatabase().indexOf(File.separator) == -1) {  // does not have the full path
				try {
					filter.setDatabase(DatabaseUtilsForGSL.getDatabasePath() + File.separator + filter.getDatabase());
				} catch (IOException e) {
					logger.error("Database path cannot be determined", e);
				}
			}
			if ( aSettings.getLipidSettings() == null || aSettings.getLipidSettings().getDatabase() == null )
				continue;
			LipidDatabase lipidDb = aSettings.getLipidSettings().getDatabase();
			if (lipidDb.getURI().indexOf(File.separator) == -1) {  // does not have the full path
				try {
					lipidDb.setURI(DatabaseUtilsForGSL.getDatabasePath() + File.separator + lipidDb.getURI());
				} catch (IOException e) {
					logger.error("Lipid database path cannot be determined", e);
				}
			}
		}
	}

	private int performAnnotation( GRITSProgressDialog gpd, Data data, String workspaceFolder, 
			String msAnnotationFolder, MSAnnotationProperty property, MSFile msFile) {
//		logger.debug("Starting job: determineScanBounds");
		try {
			// Update the Database references
			this.updateDatabaseReferences(data.getDataHeader().getMethod());
			// Set MSType to current Method
			String sMSType =  msFile.getExperimentType();
			data.getDataHeader().getMethod().setMsType(sMSType);
			// Load archive file path
			MSGlycolipidAnnotationProperty t_prop = (MSGlycolipidAnnotationProperty)property;
			String t_strMSArchivePath = msAnnotationFolder + File.separator + t_prop.getMSAnnotationMetaData().getAnnotationId();
			this.m_strArchiveFilePath = t_strMSArchivePath+".zip";

			List<IProgressListener> workerListeners = new ArrayList<>();
			workerListeners.add(gpd.getMinorProgressBarListeners()[0]);
			List<IProgressListener> annotationListeners = new ArrayList<>();
			annotationListeners.add(gpd.getMinorProgressBarListeners()[1]);

			// Start annotation
			// TODO: Add selection for different MS types
			DANGOAnnotation t_DANGO = new DANGOAnnotation(data, msFile, this.m_strArchiveFilePath);
			t_DANGO.setIntactGlycanAnnotation(true);
			t_DANGO.setFilterByKeyFragment(true);

			wizard.getMethod().setMsType( sMSType );

			DANGOWorker dw = new DANGOWorker(t_DANGO, gpd, gpd, workerListeners, annotationListeners);

			// Time stamp
//			long t_lStart = System.nanoTime();

			int iRes = dw.doWork();

//			long t_lEnd = System.nanoTime();
//			System.out.println("Annotation time[ns]: "+(t_lEnd-t_lStart));

			return iRes;

//			return GRITSProcessStatus.OK;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return GRITSProcessStatus.ERROR;	
	}
	

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.handler.NewGelatoHandler#doWork()
	 */
	@Override
	public int doWork() {
		File fWorkspaceFolderFile = getTempFolder();
		int iStatus = GRITSProcessStatus.OK;
		try {
			this.setMaxValue(wizard.getInitial().getMsEntryList().size());
			// iterate over all selected MS Entries. Continue so long as an error or cancel is not encountered
			for( int i = 0; i < wizard.getInitial().getMsEntryList().size() && iStatus == GRITSProcessStatus.OK; i++ ) {
				// get the current MS Entry
				Entry msEntry = wizard.getInitial().getMsEntryList().get(i);
				this.updateListeners("Processing entry: " + msEntry.getDisplayName(), i);

				// Create annotation entry
				final MassSpecProperty prop = (MassSpecProperty) msEntry.getProperty();
				Entry msAnnotationEntry = new Entry();
				String sWorkspaceFolder = fWorkspaceFolderFile.getAbsolutePath();

				File msAnnFile = this.getAnnotationFolder(msEntry);	
				String msAnnotationFolder = msAnnFile.getAbsolutePath();
				MSAnnotationProperty msAnnotProperty = this.getMSAnnotationProperty(shell, wizard, msAnnotationFolder);
				msAnnotationEntry.setProperty(msAnnotProperty);

				String msEntryDisplayName = msEntry.getDisplayName();
				String msAnnotName = wizard.getInitial().getListEntries().get(msEntryDisplayName);
				msAnnotationEntry.setDisplayName(msAnnotName);

				Data data = getNewDataObject(wizard);
				DataHeader dHeader = data.getDataHeader();

				MSPropertyDataFile dataFile = wizard.getInitial().getFileMap().get(msEntryDisplayName);
				String workspaceLocation = this.getWorkspaceLocation();
				String projectName = DataModelSearch.findParentByType(msEntry, ProjectProperty.TYPE).getDisplayName();
				String pathToFile = workspaceLocation + projectName + File.separator + MassSpecProperty.getFoldername();

				// call performAnnotation - does all the work. 
				iStatus = this.performAnnotation( gpd, data, sWorkspaceFolder, msAnnotationFolder, msAnnotProperty, 
						dataFile.getMSFileWithReader(pathToFile, prop.getMassSpecMetaData().getMsExperimentType()));
				if( iStatus == GRITSProcessStatus.OK ) {
					// if everything worked, save to workspace all of the appropriate files for Annotation
					msAnnotProperty.getMSAnnotationMetaData().addAnnotationFile(dataFile);

					PropertyDataFile msMetaData = MSAnnotationProperty.getNewSettingsFile(msAnnotProperty.getMetaDataFileName(), msAnnotProperty.getMSAnnotationMetaData());
					msAnnotProperty.getDataFiles().add(msMetaData);
					this.addResultFileToMetaData(dHeader.getMethod().getMsType(), msAnnotProperty);
					MSAnnotationProperty.marshallSettingsFile(msAnnotProperty.getAnnotationFolder(msEntry) + File.separator +
							msAnnotProperty.getMetaDataFileName(), msAnnotProperty.getMSAnnotationMetaData());

					Entry[] entries = new Entry[2];
					entries[0] = msEntry; // entry for the source MS
					entries[1] = msAnnotationEntry; // the new entry for the MS Annotation

					// return list is a List<Entry[]>. 
					returnList.add(entries);
				} else if (iStatus == GRITSProcessStatus.CANCEL) {
					deleteResultFiles(dHeader.getMethod().getMsType());
				}
			}			
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			iStatus = GRITSProcessStatus.ERROR;
		}
		for( File f : fWorkspaceFolderFile.listFiles() ) {
			f.delete();
		}
		fWorkspaceFolderFile.delete();
		updateListeners("Done!", getMsEntries().size());
		return iStatus;

	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.handler.NewGelatoHandler#addResultFileToMetaData(java.lang.String, org.grits.toolbox.entry.ms.annotation.property.MSAnnotationProperty)
	 */
	@Override
	protected void addResultFileToMetaData(String sMSType, MSAnnotationProperty msAnnotProperty) {
		File annotationFile = new File( this.m_strArchiveFilePath );
		if( sMSType.equals(Method.MS_TYPE_LC) ) {
			addResultFileToMetaData(annotationFile, msAnnotProperty);
		} else {
			addResultFileToMetaData(annotationFile, msAnnotProperty);
		}
	}

	private void addResultFileToMetaData(File annotationFile, MSAnnotationProperty msAnnotProperty) {
		MSPropertyDataFile pdfFolder = new MSPropertyDataFile(annotationFile.getName(), 
				MSAnnotationFileInfo.MS_ANNOTATION_CURRENT_VERSION, 
				MSAnnotationFileInfo.MS_ANNOTATION_TYPE_FILE,
				FileCategory.ANNOTATION_CATEGORY, 
//				GeneralInformationMulti.FILE_TYPE_GELATO,
				"DANGO",
				annotationFile.getPath(), new ArrayList<String>() );
		msAnnotProperty.getMSAnnotationMetaData().addFile(pdfFolder);

	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.handler.NewGelatoHandler#deleteResultFiles(java.lang.String)
	 */
	@Override
	protected void deleteResultFiles(String msType) {
		try {
			File annotationFile = new File( this.m_strArchiveFilePath );
			if (annotationFile.exists())
				annotationFile.delete();
		} catch (Exception e) {
			logger.warn("Could not delete generated files", e);
		}
	}

}
