package org.grits.toolbox.tools.gsl.util.mass;

import org.grits.toolbox.tools.gsl.structure.lipid.Ceramide;

/**
 * ChemicalComposition for ceramide.
 * @author Masaaki Matsubara
 * @see Ceramide
 *
 */
public class CeramideChemicalComposition extends ChemicalComposition {

	protected Ceramide m_oCeramide;

	public CeramideChemicalComposition( Ceramide a_oCeramide ) {
		super();
		this.m_oCeramide = a_oCeramide;
		this.start();
	}

	@Override
	protected void start() {
		// Clear composition
		this.clear();

		// Calc composition for sphingosine
		LipidChemicalComposition t_oSphComp = new LipidChemicalComposition(this.m_oCeramide.getSphingosine());
		if ( this.isDerivatized() )
			t_oSphComp.derivatize();
		this.addComposition(t_oSphComp);

		// Calc composition for fatty acid
		LipidChemicalComposition t_oFAComp = new LipidChemicalComposition(this.m_oCeramide.getFattyAcid());
		if ( this.isDerivatized() )
			t_oFAComp.derivatize();
		this.addComposition(t_oFAComp);

		// Dehydration at NH2 on Sphingosine and OH on carboxy group of Fatty Acid
		this.addNumberOfElements( AtomicMass.O, -1 );
		this.addNumberOfElements( AtomicMass.H, -2 );
	}

	@Override
	public ChemicalComposition copy() {
		CeramideChemicalComposition t_copy = new CeramideChemicalComposition(this.m_oCeramide);
		if ( this.isDerivatized() )
			t_copy.derivatize();
		return t_copy;
	}
}
