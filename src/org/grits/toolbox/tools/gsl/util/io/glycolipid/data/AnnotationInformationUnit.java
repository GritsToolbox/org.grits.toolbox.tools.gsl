package org.grits.toolbox.tools.gsl.util.io.glycolipid.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.grits.toolbox.ms.om.data.Peak;
import org.grits.toolbox.ms.om.data.Scan;

public class AnnotationInformationUnit {

	private int m_iPeakID;
	private int m_iAnnotID;
	private List<AnnotatedIon> m_lAnnotInfo;

	public AnnotationInformationUnit() {
		this.m_lAnnotInfo = new ArrayList<>();
	}

	public void setPeakID(int a_iPeakID) {
		this.m_iPeakID = a_iPeakID;
	}

	public int getPeakID() {
		return m_iPeakID;
	}

	public void setAnnotationID(int a_iAnnotID) {
		this.m_iAnnotID = a_iAnnotID;
	}

	public int getAnnotationID() {
		return m_iAnnotID;
	}

	public void addStructureInformation(AnnotatedIon a_info) {
		// No null
		if ( a_info == null )
			return;
		// No duplicate
		if ( this.contains(a_info) )
			return;
		this.m_lAnnotInfo.add(a_info);
	}

	public List<AnnotatedIon> getAnnotationInfo() {
		return m_lAnnotInfo;
	}

	public boolean contains(AnnotatedIon t_info) {
		for ( AnnotatedIon t_info0 : this.m_lAnnotInfo ) {
			if ( t_info0.equals(t_info) )
				return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "peak id: "+this.m_iPeakID+", annotation id: "+this.m_iAnnotID+", # info: "+this.m_lAnnotInfo.size()+", info: "+this.m_lAnnotInfo;
	}
}
