package org.grits.toolbox.tools.gsl.util.generator;

import java.util.ArrayList;
import java.util.List;

import org.grits.toolbox.tools.gsl.database.lipid.LipidClass;
import org.grits.toolbox.tools.gsl.database.lipid.LipidData;
import org.grits.toolbox.tools.gsl.database.lipid.LipidDatabase;
import org.grits.toolbox.tools.gsl.database.lipid.LipidGenerationSettings;
import org.grits.toolbox.tools.gsl.structure.lipid.ILipid;
import org.grits.toolbox.tools.gsl.util.generator.structure.LipidsGeneratorAbstract;
import org.grits.toolbox.tools.gsl.util.mass.ChemicalComposition;

/**
 * Abstract class for lipid database generator.
 * @author Masaaki Masaaki
 *
 */
public abstract class LipidDatabaseGeneratorAbstract {

	private LipidDatabase m_lipidDatabase;
	private LipidClass m_lipClass;
	private LipidGenerationSettings m_lipGenStgs;

	public LipidDatabaseGeneratorAbstract() {
		this.m_lipidDatabase = new LipidDatabase();
		this.m_lipGenStgs = new LipidGenerationSettings();
		this.m_lipidDatabase.setOrigin(this.m_lipGenStgs);
		this.setLipidClass(LipidClass.FRAGMENT);
	}

	protected void setLipidClass(LipidClass a_lipClass) {
		this.m_lipClass = a_lipClass;
		this.m_lipGenStgs.setLipidClass(a_lipClass);
	}

	public void setLipidDatabaseName(String a_strName) {
		this.m_lipidDatabase.setName(a_strName);
	}

	public void setVersion(String a_strVer) {
		this.m_lipidDatabase.setVersion(a_strVer);
	}

	public void setDescription(String a_strDesc) {
		this.m_lipidDatabase.setDescription(a_strDesc);
	}

	public void setCreatorName(String a_strCreator) {
		this.m_lipidDatabase.setCreatorName(a_strCreator);
	}

	public void setCreatorInstitution(String a_strInst) {
		this.m_lipidDatabase.setCreatorInstitution(a_strInst);
	}

	public void setGeneratedBy(String a_strOrigin) {
		this.m_lipGenStgs.setOrigin(a_strOrigin);
	}

	public LipidDatabase getLipidDatabase() {
		return this.m_lipidDatabase;
	}

	protected LipidGenerationSettings getLipidGenerationSettings() {
		return this.m_lipGenStgs;
	}

	/**
	 * Generate LipidDatabase object.
	 */
	public void generate() {

		// Generate lipids
		List<ILipid> t_listLipids = this.generateLipids();
		if (t_listLipids.isEmpty())
			return;

		// Calculate and store lipid masses
		for ( ILipid t_ILip : t_listLipids ) {
			LipidData t_lipidData = this.convertLipidToLipidData(t_ILip);

			this.m_lipidDatabase.addLipidData(t_lipidData);
		}

	}

	protected List<ILipid> generateLipids() {
		// Generate lipids
		List<ILipid> t_listLipids = new ArrayList<ILipid>();
		/// For sphingosine
		LipidsGeneratorAbstract t_genLipids = this.getLipidsGenerator();
		// Start generation
		t_genLipids.generate();
		// Print parameters
		System.out.println( t_genLipids.printParameters() );
		for ( ILipid t_ILip : t_genLipids.getLipids() )
			t_listLipids.add(t_ILip);

		System.out.println("# of generated lipids: "+t_listLipids.size());

		return t_listLipids;
	}

	protected LipidData convertLipidToLipidData(ILipid a_iLip) {
		LipidData t_lipidData = new LipidData();
		t_lipidData.setCommonName(a_iLip.getName());
		t_lipidData.setLipidClass(this.m_lipClass);

		if ( a_iLip.hasSubstructure() )
			for ( String t_strSubstName : a_iLip.getSubstructureNames() )
				t_lipidData.addSubstructures(t_strSubstName);

		return t_lipidData;
	}

	/**
	 * Gets a lipid generator extending LipidsGeneratorAbstract.
	 * @return Subclass of LipidsGeneratorAbstract
	 */
	protected abstract LipidsGeneratorAbstract getLipidsGenerator();
	/**
	 * Gets ChemicalComposition of the given ILipid.
	 * @param a_ILip - A lipid
	 * @return ChemicalComposition of the given ILipid
	 */
	protected abstract ChemicalComposition getChemicalComposition(ILipid a_ILip);
}
