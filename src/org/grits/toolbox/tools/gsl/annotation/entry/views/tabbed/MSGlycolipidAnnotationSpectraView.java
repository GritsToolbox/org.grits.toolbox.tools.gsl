package org.grits.toolbox.tools.gsl.annotation.entry.views.tabbed;

import javax.inject.Inject;

import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.entry.ms.annotation.glycan.views.tabbed.MSGlycanAnnotationSpectraView;

/**
 * Just extends MSGlycanAnnotationSpectraView for the future update.
 * @author Masaaki Matsubara
 *
 */
public class MSGlycolipidAnnotationSpectraView extends MSGlycanAnnotationSpectraView {
	//log4J Logger
//	private static final Logger logger = Logger.getLogger(MSGlycolipidAnnotationSpectraView.class);

	@Inject
	public MSGlycolipidAnnotationSpectraView(Entry entry) {
		super(entry);
	}

/* To be overrided to update spectral viewer
	@Override
	protected GRITSSpectralViewerChart getNewSpectralViewerChart() {
		return new MSGlycanAnnotationSpectralViewerChart( this.sDescription, 
				this.iScanNum, this.iMSLevel, ! this.bIsCentroid, true, this.sID, this.dMz );
	}
*/

}
