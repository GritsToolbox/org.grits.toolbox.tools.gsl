package org.grits.toolbox.tools.gsl.database;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class for storing list of default database indices.
 * LipidDatabaseIndex is an element of this.
 * @author Masaaki Matsubara
 * @see LipidDatabaseIndex
 *
 */
@XmlRootElement(name = "database_list")
public class LipidDatabaseList {
	private List<LipidDatabaseIndex> m_indices = new ArrayList<>();

	@XmlElement(name = "index", required = false)
	public List<LipidDatabaseIndex> getIndices() {
		return this.m_indices;
	}

	public void setIndices(List<LipidDatabaseIndex> a_database) {
		this.m_indices = a_database;
	}

}
