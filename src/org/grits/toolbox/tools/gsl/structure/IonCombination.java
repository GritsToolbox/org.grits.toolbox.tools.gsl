package org.grits.toolbox.tools.gsl.structure;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.grits.toolbox.ms.om.data.Feature;
import org.grits.toolbox.ms.om.data.Ion;
import org.grits.toolbox.ms.om.data.IonAdduct;
import org.grits.toolbox.ms.om.data.Molecule;
import org.grits.toolbox.ms.om.data.MoleculeSettings;
import org.grits.toolbox.tools.gsl.util.mass.AtomicMass;
import org.grits.toolbox.tools.gsl.util.mass.QuantumMass;

/**
 * Class for storing ions and its number and calculating total charge and mass
 * @author Masaaki Matsubara
 *
 */
public class IonCombination {

	private List<IonAdduct> m_lIonAdducts;
	private List<IonAdduct> m_lIonExchanges;
	private List<MoleculeSettings> m_lLosses;
	private int m_iCharge;
	private BigDecimal m_bdMass;
	private boolean m_bHasChangedCharge;
	private boolean m_bHasChangedMass;

	public IonCombination() {
		this.m_lIonAdducts   = new ArrayList<>();
		this.m_lIonExchanges = new ArrayList<>();
		this.m_lLosses       = new ArrayList<>();
		this.m_iCharge = 0;
		this.m_bdMass = BigDecimal.ZERO;
		this.m_bHasChangedCharge = false;
		this.m_bHasChangedMass   = false;
	}

	/**
	 * Constructor with Feature.
	 * @param a_feature Feature having ion information
	 */
	public IonCombination(Feature a_feature) {
		this();
		this.m_lIonAdducts = a_feature.getIons();
		this.m_lIonExchanges = a_feature.getNeutralexchange();
		this.m_lLosses = a_feature.getNeutralLoss();
	}

	private void changed() {
		this.m_bHasChangedCharge = true;
		this.m_bHasChangedMass = true;
	}

	public void addIonAdduct(IonAdduct a_ionAdduct) {
		this.m_lIonAdducts.add(a_ionAdduct);
		this.changed();
	}

	public void addIonAdduct(Ion a_ion, int a_nIonCount) {
		this.addIonAdduct( this.createIonAdduct(a_ion, a_nIonCount) );
	}

	public void addIonAdduct(String a_strLabel, Double a_dMass, String a_strName, int a_iCharge, boolean a_bPolarity, int a_nIonCount) {
		this.addIonAdduct( this.createIonAdduct(a_strLabel, a_dMass, a_strName, a_iCharge, a_bPolarity, a_nIonCount) );
	}

	public List<IonAdduct> getIonAdducts() {
		return this.m_lIonAdducts;
	}

	public void addIonExchange(IonAdduct a_ionExchange) {
		this.m_lIonExchanges.add(a_ionExchange);
		this.changed();
	}

	public void addIonExchange(Ion a_ion, int a_nIonCount) {
		this.addIonExchange( this.createIonAdduct(a_ion, a_nIonCount) );
	}

	public void addIonExchange(String a_strLabel, Double a_dMass, String a_strName, int a_iCharge, boolean a_bPolarity, int a_nIonCount) {
		this.addIonExchange( this.createIonAdduct(a_strLabel, a_dMass, a_strName, a_iCharge, a_bPolarity, a_nIonCount) );
	}

	public List<IonAdduct> getIonExchanges() {
		return this.m_lIonExchanges;
	}

	public void addNeutralLoss(MoleculeSettings a_molSetLoss) {
		this.m_lLosses.add(a_molSetLoss);
		this.changed();
	}

	public List<MoleculeSettings> getNeutralLosses() {
		return this.m_lLosses;
	}

	private IonAdduct createIonAdduct(Ion a_ion, int a_nIonCount) {
		return this.createIonAdduct(a_ion.getLabel(), a_ion.getMass(), a_ion.getName(), a_ion.getCharge(), a_ion.getPolarity(), a_nIonCount);
	}

	private IonAdduct createIonAdduct(String a_strLabel, Double a_dMass, String a_strName, int a_iCharge, boolean a_bPolarity, int a_nIonCount) {
		IonAdduct t_ionAdduct = new IonAdduct();
		t_ionAdduct.setLabel(a_strLabel);
		t_ionAdduct.setMass(a_dMass);
		t_ionAdduct.setName(a_strName);
		t_ionAdduct.setCharge(a_iCharge);
		t_ionAdduct.setPolarity(a_bPolarity);
		t_ionAdduct.setCount(a_nIonCount);
		return t_ionAdduct;
	}

	/**
	 * Calculate charge from containing ions
	 * @return int, calculated charge
	 */
	public int calculateCharge() {
		// Calculate when combination has changed
		if ( !this.m_bHasChangedCharge )
			return this.m_iCharge;

		if ( this.m_lIonAdducts.isEmpty() )
			return 0;
		int t_nCharge = 0;
		for ( IonAdduct t_ionAdduct : this.m_lIonAdducts )
			t_nCharge += t_ionAdduct.getCharge() * (t_ionAdduct.getPolarity()? 1 : -1) * t_ionAdduct.getCount();
		this.m_iCharge = t_nCharge;
		this.m_bHasChangedCharge = false;

		return t_nCharge;
	}

	/**
	 * Calculate and get the total mass of this ion combination
	 * @return double value of calculated ion combination mass
	 */
	public double calculateMass() {
		// Calculate when combination has changed
		if ( !this.m_bHasChangedMass )
			return this.m_bdMass.doubleValue();

		if ( this.m_lIonAdducts.isEmpty() )
			return -1;
		BigDecimal t_bdElectronMass = new BigDecimal( QuantumMass.e.getExactMass() );
		BigDecimal t_bdMass = new BigDecimal(0);
		// Add all adduct ions
		for ( IonAdduct t_ionAdduct : this.m_lIonAdducts ) {
			BigDecimal t_bdAdductMass = new BigDecimal( t_ionAdduct.getMass() );
			t_bdAdductMass = t_bdAdductMass.add( (t_ionAdduct.getPolarity())? t_bdElectronMass.negate() : t_bdElectronMass );
			t_bdMass = t_bdMass.add( new BigDecimal( t_ionAdduct.getMass() ).multiply( new BigDecimal(t_ionAdduct.getCount()) ) );
		}

		if ( !this.m_lIonExchanges.isEmpty() ) {
			// Add all exchange ions
			int t_nHydrogen = 0;
			for ( IonAdduct t_ionExchange : this.m_lIonExchanges ) {
				t_bdMass = t_bdMass.add( new BigDecimal( t_ionExchange.getMass() ).multiply( new BigDecimal(t_ionExchange.getCount()) ) );
				t_nHydrogen += t_ionExchange.getCount();
			}
			// Subtract hydrogens
			t_bdMass = t_bdMass.subtract( new BigDecimal( AtomicMass.H.getExactMass() ).multiply( new BigDecimal(t_nHydrogen) ) );
		}
		if ( !this.m_lLosses.isEmpty() ) {
			// Subtract all neutral losses
			for ( MoleculeSettings t_molLoss : this.m_lLosses ) {
				t_bdMass = t_bdMass.add( new BigDecimal( t_molLoss.getMass() ).multiply( new BigDecimal( t_molLoss.getCount() ) ) );
			}
		}
		// Store calculated mass rounded at 10th decimal
		this.m_bdMass = t_bdMass.setScale(10, BigDecimal.ROUND_HALF_UP);
		this.m_bHasChangedMass = false;

		return this.m_bdMass.doubleValue();
	}

	public boolean isEmpty() {
		if ( !this.m_lIonAdducts.isEmpty() )
			return false;
		if ( !this.m_lIonExchanges.isEmpty() )
			return false;
		if ( !this.m_lLosses.isEmpty() )
			return false;
		return true;
	}

	@Override
	public String toString() {
		if ( this.m_lIonAdducts.isEmpty() )
			return null;
		String t_strIons = "";
		for ( IonAdduct t_ion : this.m_lIonAdducts ) {
			if ( !t_strIons.equals("") )
				t_strIons += " + ";
			t_strIons += t_ion.getCount()+t_ion.getLabel();
		}
		if ( !this.m_lIonExchanges.isEmpty() ) {
			// Add all exchange ions
			int t_nHydrogen = 0;
			for ( IonAdduct t_ionExchange : this.m_lIonExchanges ) {
				t_strIons += " + "+t_ionExchange.getCount()+t_ionExchange.getLabel();
				t_nHydrogen += t_ionExchange.getCount();
			}
			// Subtract hydrogens
			t_strIons += " - "+t_nHydrogen+"H";
		}
		if ( !this.m_lLosses.isEmpty() ) {
			// Subtract all neutral losses
			for ( MoleculeSettings t_molLoss : this.m_lLosses ) {
				t_strIons += (t_molLoss.getMass() >= 0)? " + " : " - ";
				t_strIons += t_molLoss.getCount()+t_molLoss.getLabel();
			}
		}
		return t_strIons;
	}

	@Override
	public boolean equals(Object obj) {
		if ( ! (obj instanceof IonCombination) )
			return false;

		IonCombination t_ionCombo = (IonCombination)obj;
		// For adduct ions
		if ( this.getIonAdducts().size() != t_ionCombo.getIonAdducts().size() )
			return false;
		int t_nCount = 0;
		for ( IonAdduct t_ion1 : this.getIonAdducts() ) {
			for ( IonAdduct t_ion2 : t_ionCombo.getIonAdducts() ) {
				if ( this.isSameIonAdduct(t_ion1, t_ion2) )
					t_nCount++;
			}
		}
		if ( this.getIonAdducts().size() != t_nCount )
			return false;

		// For exchange ions
		if ( this.getIonExchanges().size() != t_ionCombo.getIonExchanges().size() )
			return false;
		t_nCount = 0;
		for ( IonAdduct t_ion1 : this.getIonExchanges() ) {
			for ( IonAdduct t_ion2 : t_ionCombo.getIonExchanges() ) {
				if ( this.isSameIonAdduct(t_ion1, t_ion2) )
					t_nCount++;
			}
		}
		if ( this.getIonExchanges().size() != t_nCount )
			return false;

		// For neutral loss
		if ( this.getNeutralLosses().size() != t_ionCombo.getNeutralLosses().size() )
			return false;
		t_nCount = 0;
		for ( MoleculeSettings t_mol1 : this.getNeutralLosses() ) {
			for ( MoleculeSettings t_mol2 : t_ionCombo.getNeutralLosses() ) {
				if ( this.isSameMoleculeSettings(t_mol1, t_mol2) )
					t_nCount++;
			}
		}
		if ( this.getNeutralLosses().size() != t_nCount )
			return false;

		return true;
	}

	private boolean isSameIonAdduct( IonAdduct a_ion1, IonAdduct a_ion2 ) {
		if ( !this.isSameIon(a_ion1, a_ion2) || !a_ion1.getCount().equals(a_ion2.getCount()) )
			return false;
		return true;
	}

	private boolean isSameIon( Ion a_ion1, Ion a_ion2 ) {
		if ( !this.isSameMolecule(a_ion1, a_ion2)
		  || !a_ion1.getCharge().equals(a_ion2.getCharge())
		  || !a_ion1.getPolarity().equals(a_ion2.getPolarity())
		)
			return false;
		return true;
	}

	private boolean isSameMoleculeSettings( MoleculeSettings a_mol1, MoleculeSettings a_mol2) {
		if ( !this.isSameMolecule(a_mol1, a_mol2) || !a_mol1.getCount().equals(a_mol2.getCount()) )
			return false;
		return true;
	}

	private boolean isSameMolecule( Molecule a_mol1, Molecule a_mol2) {
		if ( !a_mol1.getLabel().equals( a_mol2.getLabel() )
		  || !a_mol1.getName().equals(  a_mol2.getName()  )
		  || !a_mol1.getMass().equals(  a_mol2.getMass()  )
		)
			return false;
		return true;
	}

	@Override
	public IonCombination clone() {
		return this.copy();
	}

	/**
	 * Copy this IonCombination
	 * @return Copied IonCombination
	 */
	public IonCombination copy() {
		IonCombination t_copy = new IonCombination();
		for ( IonAdduct t_ion : this.m_lIonAdducts )
			t_copy.m_lIonAdducts.add( this.copyIonAdduct(t_ion) );
		for ( IonAdduct t_ion : this.m_lIonExchanges )
			t_copy.m_lIonExchanges.add( this.copyIonAdduct(t_ion) );
		for ( MoleculeSettings t_mol : this.m_lLosses )
			t_copy.m_lLosses.add( this.copyMoleculeSettings(t_mol) );

		t_copy.m_iCharge = this.m_iCharge;
		t_copy.m_bdMass  = this.m_bdMass;
		t_copy.m_bHasChangedCharge = this.m_bHasChangedCharge;
		t_copy.m_bHasChangedMass   = this.m_bHasChangedMass;
		return t_copy;
	}

	/**
	 * Copy the given IonAdduct
	 * @param a_ion IonAdduct to be copied
	 * @return Copied IonAdduct
	 */
	private IonAdduct copyIonAdduct(IonAdduct a_ion) {
		IonAdduct t_ionCopy = new IonAdduct();
		t_ionCopy.setLabel(a_ion.getLabel());
		t_ionCopy.setName(a_ion.getName());
		t_ionCopy.setMass(a_ion.getMass());
		t_ionCopy.setCharge(a_ion.getCharge());
		t_ionCopy.setPolarity(a_ion.getPolarity());
		t_ionCopy.setCount(a_ion.getCount());
		return t_ionCopy;
	}

	/**
	 * Copy the given MoleculeSettings
	 * @param a_ion MoleculeSettings to be copied
	 * @return Copied MoleculeSettings
	 */
	private MoleculeSettings copyMoleculeSettings(MoleculeSettings a_mol) {
		MoleculeSettings t_molCopy = new MoleculeSettings();
		t_molCopy.setLabel(a_mol.getLabel());
		t_molCopy.setName(a_mol.getName());
		t_molCopy.setMass(a_mol.getMass());
		t_molCopy.setCount(a_mol.getCount());
		return t_molCopy;
	}
}
