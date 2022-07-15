package org.grits.toolbox.tools.gsl.util.mass;

import org.grits.toolbox.ms.annotation.structure.GlycanPreDefinedOptions;
import org.grits.toolbox.tools.gsl.structure.lipid.Lipid;

/**
 * ChemicalComposition for single chain lipid.
 * Necessary atoms are automatically added from the lipid structure information.
 * @author Masaaki Matsubara
 *
 */
public class LipidChemicalComposition extends ChemicalComposition {

	private Lipid m_oLipid;

	public LipidChemicalComposition( Lipid a_oLipid ) {
		super();
		this.m_oLipid = a_oLipid;
		this.start();
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.tools.gsl.util.mass.ChemicalComposition#start()
	 */
	@Override
	protected void start() {
		// Clear composition
		this.clear();

		int t_nCarbon = 0;
		int t_nHydrogen = 0;
		int t_nOxygen = 0;
		int t_nNitrogen = 0;

		// Add alkyl chain (add n carbons and n + 2 hydrogens)
		// CH3-CH2-...-CH3 = H-(CH2)n-H
		t_nCarbon += this.m_oLipid.getCarbonLength();
		t_nHydrogen += t_nCarbon * 2 + 2;

		// Change terminal Methyl group to Carboxyl group (add two oxygens and remove two hydrogens)
		// CH3 -> COOH
		t_nOxygen += this.m_oLipid.getNumberOfCarboxyl() * 2;
		t_nHydrogen -= this.m_oLipid.getNumberOfCarboxyl() * 2;

		// Change terminal Methyl group to Carbamoyl group (add one oxygen and nitrogen and remove one hydrogen )
		// CH3 -> CONH2
		t_nOxygen += this.m_oLipid.getNumberOfCarbamoyl();
		t_nNitrogen += this.m_oLipid.getNumberOfCarbamoyl();
		t_nHydrogen -= this.m_oLipid.getNumberOfCarbamoyl();

		// Add amine group to chain (add an nitrogen and add a hydrogen)
		// CH -> C-NH2
		t_nNitrogen += this.m_oLipid.getNumberOfAmine();
		t_nHydrogen += this.m_oLipid.getNumberOfAmine();

		// Add hydroxyl group to chain (add an oxygen)
		// CH -> C-OH
		t_nOxygen += this.m_oLipid.getNumberOfHydroxylGroups();

		// Add O-acetyl group to chain
		// CH -> C-O-C(=O)CH3
		t_nOxygen += this.m_oLipid.getNumberOfOAcetylGroups();
		t_nOxygen += this.m_oLipid.getNumberOfOAcetylGroups();
		t_nCarbon += this.m_oLipid.getNumberOfOAcetylGroups() * 2;
		t_nHydrogen += this.m_oLipid.getNumberOfOAcetylGroups() * 2;

		// Unsaturate chain (remove two hydrogens)
		// CH-CH -> C=C
		t_nHydrogen -= this.m_oLipid.getNumberOfDoubleBonds() * 2;

		// Set atoms with the number
		this.addNumberOfElements(AtomicMass.C, t_nCarbon);
		this.addNumberOfElements(AtomicMass.H, t_nHydrogen);
		this.addNumberOfElements(AtomicMass.O, t_nOxygen);
		this.addNumberOfElements(AtomicMass.N, t_nNitrogen);

		if ( !this.isDerivatized() )
			return;

		// For derivatized lipid
		int t_nSubst = this.m_oLipid.getNumberOfAmine()
					+ this.m_oLipid.getNumberOfCarbamoyl()
					+ this.m_oLipid.getNumberOfHydroxylGroups()
					+ this.m_oLipid.getNumberOfOAcetylGroups();
		this.addSubstituents(this.m_strDerivatizationType, t_nSubst);
		// Remove O-acetyl groups
		this.addSubstituents(GlycanPreDefinedOptions.DERIVITIZATION_PERACETYLATED, -this.m_oLipid.getNumberOfOAcetylGroups());
	}

	@Override
	public ChemicalComposition copy() {
		LipidChemicalComposition t_copy = new LipidChemicalComposition(this.m_oLipid);
		if ( this.isDerivatized() )
			t_copy.derivatize();
		return t_copy;
	}
}
