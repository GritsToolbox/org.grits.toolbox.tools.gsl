 
package org.grits.toolbox.tools.gsl.handler;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.core.datamodel.io.ProjectFileHandler;
import org.grits.toolbox.core.service.IGritsDataModelService;
import org.grits.toolbox.core.service.IGritsUIService;
import org.grits.toolbox.core.utilShare.ErrorUtils;
import org.grits.toolbox.entry.ms.property.FileLockManager;
import org.grits.toolbox.entry.ms.property.FileLockingUtils;
import org.grits.toolbox.entry.ms.property.MassSpecProperty;
import org.grits.toolbox.entry.ms.property.datamodel.MSPropertyDataFile;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.MSGlycanAnnotationWizard;
import org.grits.toolbox.tools.gsl.wizard.annotation.MSGlycolipidAnnotationWizard;

/**
 * Handler class for executing MSGlycolipidAnnotationWizard
 * @author Masaaki Matsubara
 *
 */
public class OpenDANGOWizardHandler {

	private static final Logger logger = Logger.getLogger(OpenDANGOWizardHandler.class);
	
	@Inject private static IGritsDataModelService injectGritsDataModelService = null;
	@Inject static IGritsUIService injectGritsUIService = null;
	@Inject MApplication m_mApp;

	@Execute
	public Object execute(@Named(IServiceConstants.ACTIVE_SELECTION) Object object,
			IEventBroker eventBroker, @Named (IServiceConstants.ACTIVE_SHELL) Shell shell, EPartService partService) {
		logger.debug("BEGIN OpenDANGOWizardHandler");
		try {
			List<Entry> msEntries = this.getMSEntries(object);
			MSGlycolipidAnnotationWizard wizard = this.createNewMSAnnotationWizerd(shell, msEntries);
			if ( wizard == null || wizard.getInitial().getMsEntryList() == null ) {
				return null;
			}
//			this.serialize(wizard.getMethod());
			Shell shell2 = new Shell(shell);
			DANGOHandler handler = new DANGOHandler();
			handler.setWizard(wizard);
			handler.setMsEntries(msEntries);
			List<Entry[]> resultEntries = handler.process(shell2);
			if( resultEntries != null ) {
				for( int i = 0; i < resultEntries.size(); i++ ) {
					Entry[] curEntries = resultEntries.get(i);
					injectGritsDataModelService.addEntry(curEntries[0], curEntries[1]);
				}
			}

			if( resultEntries != null && ! resultEntries.isEmpty() && resultEntries.get(resultEntries.size()-1)[1] != null ) {
				// save the project first
				try
				{
					// parent of MS Entry is Sample, parent of Sample is the Project
					ProjectFileHandler.saveProject(resultEntries.get(resultEntries.size()-1)[0].getParent().getParent());
				} catch (IOException e)
				{
					logger.error("Something went wrong while saving project entry \n" + e.getMessage(),e);
					logger.fatal("Closing project entry \""
							+ resultEntries.get(resultEntries.size()-1)[0].getParent().getParent().getDisplayName() + "\"");
					injectGritsDataModelService.closeProject(resultEntries.get(resultEntries.size()-1)[0].getParent().getParent());
					throw e;
				}
				for( int i = 0; i < resultEntries.size(); i++ ) {
					Entry[] curEntries = resultEntries.get(i);
					lockFiles (wizard, curEntries[0], curEntries[1]);
				}
				final Entry lastMSEntry = (Entry) resultEntries.get(resultEntries.size()-1)[1] ;
				eventBroker.send(IGritsDataModelService.EVENT_SELECT_ENTRY, lastMSEntry);
				
				try {
				    // need to set the partService to refresh gritsUIServices' stale partService, see ticket #799
					injectGritsUIService.setPartService(partService);
					injectGritsUIService.openEntryInPart(lastMSEntry);
				} catch (Exception e) {
					logger.debug("Could not open the part", e);
				}
			}
			return resultEntries;

		} catch ( Exception ex ) {
			logger.error("General Exception executing OpenDANGOWizardHandler.", ex);
		}
		logger.debug("END OpenDANGOWizardHandler");
		return null;
	}

	/**
	 * lock the file used for the annotation so that the file cannot be removed before removing this annotation entry
	 * 
	 * @param wizard MSGlycanAnnotationWizard having the MSPropertyDataFile
	 * @param msEntry Entry for the MassSpecProperty
	 * @param msAnnotationEntry Entry to be locked
	 */
	private void lockFiles(MSGlycanAnnotationWizard wizard, Entry msEntry, Entry msAnnotationEntry) {
		MassSpecProperty prop = (MassSpecProperty) msEntry.getProperty();
		FileLockManager mng;
		try {
			String lockFileLocation = prop.getLockFilePath(msEntry);
			mng = FileLockingUtils.readLockFile(lockFileLocation);
			MSPropertyDataFile file = wizard.getInitial().getFileMap().get(msEntry.getDisplayName());
			if (file != null) {
				mng.lockFile(file.getName(), msAnnotationEntry);
				FileLockingUtils.writeLockFile(mng, lockFileLocation);
			}
		} catch (IOException e) {
			logger.error("Could not lock the file", e);
		} catch (JAXBException e) {
			logger.error("Could not lock the file", e);
		}	
	}

	/**
	 * @param shell current active Shell
	 * @param msEntries List of Entry
	 * @return new instance of the MSGlycolipidAnnotationWizard
	 */
	private MSGlycolipidAnnotationWizard createNewMSAnnotationWizerd(Shell shell, List<Entry> msEntries) {
		MSGlycolipidAnnotationWizard wizard = new MSGlycolipidAnnotationWizard();
		//set the Sample entry if there is one chosen
		wizard.setMSEntries(msEntries);
		WizardDialog dialog = new WizardDialog(shell, wizard);
		try {
			if (dialog.open() == Window.OK) {
				return wizard;
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			ErrorUtils.createErrorMessageBox(shell, "Exception", e);
		}
		return null;
	}

	/**
	 * Get MS entries from the selection on grits interface
	 * @param object An object from IServiceConstants.ACTIVE_SELECTION
	 * @return List of MS Entry
	 */
	private List<Entry> getMSEntries(Object object)  {
		List<Entry> entries = new ArrayList<Entry>();
		
		StructuredSelection structuredEntries = null;
		Entry selectedEntry = null;
		// For single selection
		if ( object instanceof Entry )
			selectedEntry = (Entry) object;
		// For multiple selections
		if ( object instanceof StructuredSelection ) {
			if(((StructuredSelection) object).getFirstElement() instanceof Entry) {
				structuredEntries = (StructuredSelection) object;
			}
		}
		// Try getting the last selection from the data model
		if ( selectedEntry == null
				&& injectGritsDataModelService.getLastSelection() != null
				&& injectGritsDataModelService.getLastSelection().getFirstElement() instanceof Entry) {
			structuredEntries = injectGritsDataModelService.getLastSelection();
		}
		if ( structuredEntries == null ) {
			if ( selectedEntry != null ) {
				if ( selectedEntry.getProperty().getType().equals(MassSpecProperty.TYPE)) {
					entries.add(selectedEntry);
				}
			}
			return entries;
		}
		// Extract entry from Structured Selection
		List<Entry> selList = structuredEntries.toList();
		for ( int i=0; i < selList.size(); i++ ) {
			Entry msEntry = selList.get(i);
			// Add if the property is MSProperty
			if ( msEntry.getProperty().getType().equals(MassSpecProperty.TYPE) ) {
				entries.add(msEntry);
			}
		}

		return entries;
	}

}