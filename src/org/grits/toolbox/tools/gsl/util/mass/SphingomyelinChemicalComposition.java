package org.grits.toolbox.tools.gsl.util.mass;

import org.grits.toolbox.tools.gsl.structure.lipid.Sphingomyelin;

/**
 * ChemicalComposition for Sphingomyeline.
 * @author Masaaki Matsubara
 *
 */
public class SphingomyelinChemicalComposition extends CeramideChemicalComposition {

	public SphingomyelinChemicalComposition(Sphingomyelin a_oSM) {
		super(a_oSM);
	}

	@Override
	public void start() {
		// Do ceramide part
		super.start();

		// Add phosphocoline
		// -OH -> -O-P(=O)(-O[-])-O-CH2CH2-N[+](-CH3)3
		this.addNumberOfElements( AtomicMass.O,  3 );
		this.addNumberOfElements( AtomicMass.H, 12 );
		this.addNumberOfElements( AtomicMass.C,  5 );
		this.addNumberOfElements( AtomicMass.N,  1 );
		this.addNumberOfElements( AtomicMass.P,  1 );

		if ( !this.isDerivatized() )
			return;
		// Remove a derivatized substituent for sphingosine connected to phosphocoline
		this.addSubstituents(this.m_strDerivatizationType, -1);
	}

	@Override
	public ChemicalComposition copy() {
		CeramideChemicalComposition t_copy = new SphingomyelinChemicalComposition((Sphingomyelin) this.m_oCeramide);
		if ( this.isDerivatized() )
			t_copy.derivatize();
		return t_copy;
	}
}
