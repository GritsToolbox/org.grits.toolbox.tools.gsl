package org.grits.toolbox.tools.gsl.dango;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.grits.toolbox.ms.annotation.structure.GlycanPreDefinedOptions;
import org.grits.toolbox.ms.om.data.Ion;
import org.grits.toolbox.ms.om.data.IonAdduct;
import org.grits.toolbox.ms.om.data.IonSettings;
import org.grits.toolbox.ms.om.data.Method;
import org.grits.toolbox.ms.om.data.Molecule;
import org.grits.toolbox.ms.om.data.MoleculeSettings;
import org.grits.toolbox.tools.gsl.structure.IonCombination;

/**
 * Class for generating possible combinations of adduct ion, exchange ion, and neutral loss.
 * @author Masaaki Matsubara
 *
 */
public class IonCombinationGenerator {

	private List<IonSettings> m_lISAdducts;
	private Integer m_nMaxIonCount;
	private List<IonSettings> m_lISExchanges;
	private Integer m_nMaxIonExchangeCount;
	private List<MoleculeSettings> m_lMSLoss;
	
	private List<IonCombination> m_lPositiveIonCombinations;
	private List<IonCombination> m_lNegativeIonCombinations;
	private List<IonCombination> m_lIonCombinations;
	// Use as temporary 
	private List<Map<Molecule, Integer>> m_lMolToNumbers;

	/**
	 * Constructor for using Method
	 * @param a_method Method containing ion information
	 */
	public IonCombinationGenerator(Method a_method) {
		this.m_lISAdducts = a_method.getIons();
		// Check ion counts
		for ( IonSettings t_isAdduct : this.m_lISAdducts )
			this.fillLackingIonCounts(t_isAdduct);
		this.m_nMaxIonCount = a_method.getMaxIonCount();
		if ( this.m_nMaxIonCount == null )
			this.m_nMaxIonCount = -1;
		this.m_lISExchanges = a_method.getIonExchanges();
		// Check ion counts
		for ( IonSettings t_isExchange : this.m_lISExchanges )
			this.fillLackingIonCounts(t_isExchange);
		this.m_nMaxIonExchangeCount = a_method.getMaxIonExchangeCount();
		if ( this.m_nMaxIonExchangeCount == null )
			this.m_nMaxIonExchangeCount = -1;
		this.m_lMSLoss = a_method.getNeutralLoss();
		this.m_lIonCombinations = null;
	}

	/**
	 * Constructor for using IonCombination
	 * @param a_ionCombination IonCombination for using generation of ion combinations as a parent
	 */
	public IonCombinationGenerator(IonCombination a_ionCombination) {
		// Generate adduct IonSettings
		this.m_lISAdducts = new ArrayList<>();
		for ( IonAdduct t_ion : a_ionCombination.getIonAdducts() ) {
			IonSettings t_ionSettings = new IonSettings();
			t_ionSettings.setCharge(   t_ion.getCharge()   );
			t_ionSettings.setLabel(    t_ion.getLabel()    );
			t_ionSettings.setMass(     t_ion.getMass()     );
			t_ionSettings.setName(     t_ion.getName()     );
			t_ionSettings.setPolarity( t_ion.getPolarity() );
			List<Integer> t_lIonCount = new ArrayList<>();
			for ( int i=1; i<=t_ion.getCount(); i++ )
				t_lIonCount.add(i);
			t_ionSettings.setCounts(t_lIonCount);
			this.m_lISAdducts.add(t_ionSettings);
		}
		this.m_nMaxIonCount = -1;

		// Generate exchange IonSettings
		this.m_lISExchanges = new ArrayList<>();
		for ( IonAdduct t_ion : a_ionCombination.getIonExchanges() ) {
			IonSettings t_ionSettings = new IonSettings();
			t_ionSettings.setCharge(   t_ion.getCharge()   );
			t_ionSettings.setLabel(    t_ion.getLabel()    );
			t_ionSettings.setMass(     t_ion.getMass()     );
			t_ionSettings.setName(     t_ion.getName()     );
			t_ionSettings.setPolarity( t_ion.getPolarity() );
			List<Integer> t_lIonCount = new ArrayList<>();
			for ( int i=1; i<=t_ion.getCount(); i++ )
				t_lIonCount.add(i);
			t_ionSettings.setCounts(t_lIonCount);
			this.m_lISExchanges.add(t_ionSettings);
		}
		this.m_nMaxIonExchangeCount = -1;

		// Generate neutral loss MoleculeSettings
		this.m_lMSLoss = new ArrayList<>();
		for ( MoleculeSettings t_mol : a_ionCombination.getNeutralLosses() ) {
			MoleculeSettings t_molSettings = new MoleculeSettings();
			t_molSettings.setLabel( t_mol.getLabel() );
			t_molSettings.setMass(  t_mol.getMass()  );
			t_molSettings.setName(  t_mol.getName()  );
			t_molSettings.setCount( t_mol.getCount() );
			this.m_lMSLoss.add(t_molSettings);
		}
		this.m_nMaxIonExchangeCount = -1;
		this.m_lIonCombinations = null;
	}

	public List<IonCombination> getPossibleIonCombinations() {
		return this.m_lIonCombinations;
	}

	public List<IonCombination> getPositiveIonCombinations() {
		return this.m_lPositiveIonCombinations;
	}

	public List<IonCombination> getNegativeIonCombinations() {
		return this.m_lNegativeIonCombinations;
	}

	/**
	 * Checks ion counts in the specified IonSettings and fills lacking ion counts.
	 * e.g. If there is only one ion count "4" in the IonSettings, "1", "2" and "3" will be added.
	 * TODO: This is only for current input.
	 * @param a_is IonSettings to be checked ion count
	 */
	private void fillLackingIonCounts(IonSettings a_is) {
		// Ignore if no data or no ion count
		if ( a_is == null || a_is.getCounts() == null || a_is.getCounts().isEmpty() )
			return;
		// Do nothing if there are multiple ion counts
		if ( a_is.getCounts().size() > 1 )
			return;
		// Do nothing if list of ion counts have only one element and it is "1"
		if ( a_is.getCounts().get(0) == 1 )
			return;
		// Fill lacking ion counts
		List<Integer> t_lFilledIonCounts = new ArrayList<>();
		for ( int i=1; i<=a_is.getCounts().get(0); i++ )
			t_lFilledIonCounts.add(i);
		a_is.setCounts(t_lFilledIonCounts);
	}

	/**
	 * Generates possible IonCombinations. User must run this method to generate possible ion combinations.
	 */
	public void generate() {
		this.m_lPositiveIonCombinations = this.generate(true);
		this.m_lNegativeIonCombinations = this.generate(false);
		this.m_lIonCombinations = new ArrayList<>();
		this.m_lIonCombinations.addAll(this.m_lPositiveIonCombinations);
		this.m_lIonCombinations.addAll(this.m_lNegativeIonCombinations);
	}

	/**
	 * Generates IonCombinations only for the given polarity.
	 * @param polarity true if positive, false otherwise
	 * @return List of IonCombinations only for the given polarity
	 */
	public List<IonCombination> generate(boolean polarity) {
		// Generate possible ion combinations
		/// For adduct ions
		List<IonCombination> t_lIonCombinations = new ArrayList<>();
		List<Map<Molecule, Integer>> t_lAdductIonToNumbers
			= generatePossibleIonCombinations(this.m_lISAdducts, this.m_nMaxIonCount, false);

		for ( Map<Molecule, Integer> t_mapAdductIonToNum : t_lAdductIonToNumbers ) {
			IonCombination t_ionComb = new IonCombination();
			boolean t_bHasOppositePolarity = false;
			for ( Molecule t_ion : t_mapAdductIonToNum.keySet() ) {
				// Break if polarities are not matched
				if ( ((Ion)t_ion).getPolarity() != polarity ) {
					t_bHasOppositePolarity = true;
					break;
				}
				// Add ion and its count as a new IonAdduct
				t_ionComb.addIonAdduct((Ion)t_ion, t_mapAdductIonToNum.get(t_ion));
			}
			if ( !t_bHasOppositePolarity )
				t_lIonCombinations.add(t_ionComb);
		}

		// Return empty array if no ion adducts
		if ( t_lIonCombinations.isEmpty() )
			return t_lIonCombinations;

		/// For exchanged ions
		if ( this.m_lISExchanges != null && !this.m_lISExchanges.isEmpty() ) {

			List<IonCombination> t_lIonCombinations2 = new ArrayList<>();
			List<Map<Molecule, Integer>> t_lExchangeIonToNumbers
				= generatePossibleIonCombinations(this.m_lISExchanges, this.m_nMaxIonExchangeCount, true);

			for ( Map<Molecule, Integer> t_mapExchangeIonToNum : t_lExchangeIonToNumbers ) {
				for ( IonCombination t_ionComb : t_lIonCombinations ) {
					IonCombination t_ionComb2 = t_ionComb.copy();
					for ( Molecule t_ion : t_mapExchangeIonToNum.keySet() ) {
						// Add ion and its count as a new IonAdduct
						t_ionComb2.addIonExchange( (Ion)t_ion, t_mapExchangeIonToNum.get(t_ion) );
						t_lIonCombinations2.add(t_ionComb2);
					}
				}
			}

			// Move exchange ion to adduct ion if adduct ion has one or more hydrogen ions
			// e.g. 1Na1H adduct + 1Na exchange => 2Na adduct
			t_lIonCombinations2 = this.moveIonExchangeToIonAdduct(t_lIonCombinations2);

			// Add IonCombinations with no duplicate
			for ( IonCombination t_ionCombo : t_lIonCombinations2 ) {
				if ( t_lIonCombinations.contains(t_ionCombo) )
					continue;
				t_lIonCombinations.add(t_ionCombo);
			}
		}

		/// For neutral losses
		if ( this.m_lMSLoss != null && !this.m_lMSLoss.isEmpty() ) {

			List<IonCombination> t_lIonCombinations2 = new ArrayList<>();
			List<Map<Molecule, Integer>> t_lNeutralLossToNumbers
				= generatePossibleMoleculeCombination(this.m_lMSLoss);

			for (Map<Molecule, Integer> t_mapNeutralLossToNum : t_lNeutralLossToNumbers) {
				for ( IonCombination t_ionComb : t_lIonCombinations ) {
					IonCombination t_ionComb2 = t_ionComb.copy();
					for ( Molecule t_mol : t_mapNeutralLossToNum.keySet() ) {
						MoleculeSettings t_molLoss = new MoleculeSettings();
						t_molLoss.setLabel( t_mol.getLabel() );
						t_molLoss.setMass(  t_mol.getMass()  );
						t_molLoss.setName(  t_mol.getName()  );
						t_molLoss.setCount( t_mapNeutralLossToNum.get(t_mol) );
						t_ionComb2.addNeutralLoss(t_molLoss);
						t_lIonCombinations2.add(t_ionComb2);
					}
				}
			}
			t_lIonCombinations.addAll(t_lIonCombinations2);
		}

		return t_lIonCombinations;
	}

	/**
	 * Moves exchange ions to adduct ions if there are protons (H+, hydrogen ions)
	 * e.g. 1Na1H adduct + 1Na exchange => 2Na adduct
	 * @param a_ionCombos List of IonCombinations to be modified
	 * @return new List of IonCombinations after ions moved
	 */
	private List<IonCombination> moveIonExchangeToIonAdduct(List<IonCombination> a_ionCombos) {
		List<IonCombination> t_lIonComboToBeRemoved = new ArrayList<>();
		List<IonCombination> t_lIonComboToBeAdded = new ArrayList<>();
		for ( IonCombination t_ionCombo : a_ionCombos ) {
			int t_nHydrogen = 0;
			for ( IonAdduct t_ionAdduct : t_ionCombo.getIonAdducts() ) {
				if ( isAdductHydrogen(t_ionAdduct) )
					t_nHydrogen += t_ionAdduct.getCount();
			}
			if ( t_nHydrogen == 0 )
				continue;

			int t_nExchange = 0;
			for ( IonAdduct t_ionExchange : t_ionCombo.getIonExchanges() ) {
				t_nExchange += t_ionExchange.getCount();
			}

			if ( t_nExchange == 0 )
				continue;

			// Collect IonCombinations having hydrogens
			t_lIonComboToBeRemoved.add(t_ionCombo);

			// Create a new IonCombo with exchanged hydrogen
			IonCombination t_ionComboNew = new IonCombination();
			for ( IonAdduct t_ionAdduct : t_ionCombo.getIonAdducts() ) {

				// Add adduct ions except for hydrogen
				if ( !isAdductHydrogen(t_ionAdduct) ) {
					t_ionComboNew.addIonAdduct(t_ionAdduct, t_ionAdduct.getCount());
					continue;
				}

				if ( t_nHydrogen <= t_nExchange )
					continue;

				// Add hydrogens
				t_ionComboNew.addIonAdduct(t_ionAdduct, t_ionAdduct.getCount()-t_nExchange);
			}
			// Generate the possible count combinations of ion exchange after ion exchanges moved
			List<int[]> t_lCountCombos = new ArrayList<>();
			if ( t_nHydrogen < t_nExchange ) {
				// Generate all possible ion exchange count combo
				t_lCountCombos.add( new int[t_ionCombo.getIonExchanges().size()] );
				for ( int i=0; i<t_ionCombo.getIonExchanges().size(); i++ ) {
					List<int[]> t_lCountCombosNew = new ArrayList<>();
					for ( int[] t_lCountCombo : t_lCountCombos ) {
						int t_nCount = t_ionCombo.getIonExchanges().get(i).getCount();
						for ( int j=0; j<=t_nCount; j++ ) {
							// Copy combos
							int[] t_lCountComboNew = new int[t_ionCombo.getIonExchanges().size()];
							for ( int k=0; k<i; k++ )
								t_lCountComboNew[k] = t_lCountCombo[k];
							// Set values
							t_lCountComboNew[i] = j;
							t_lCountCombosNew.add(t_lCountComboNew);
						}
					}
					t_lCountCombos = t_lCountCombosNew;
				}
				// Trim different count combo
				int t_nTotalRemovedHs = t_nExchange - t_nHydrogen;
				List<int[]> t_lCountCombosNew = new ArrayList<>();
				for ( int[] t_lCountCombo : t_lCountCombos ) {
					int t_nTotalCount = 0;
					for ( int i=0; i<t_lCountCombo.length; i++ )
						t_nTotalCount += t_lCountCombo[i];
					if ( t_nTotalCount != t_nTotalRemovedHs )
						continue;
					t_lCountCombosNew.add(t_lCountCombo);
				}
				t_lCountCombos = t_lCountCombosNew;
			} else {
				int[] t_lCountCombo = new int[t_ionCombo.getIonExchanges().size()];
				for ( int i=0; i<t_ionCombo.getIonExchanges().size(); i++ )
					t_lCountCombo[i] = 0;
				t_lCountCombos.add(t_lCountCombo);
			}
			// Generate possible IonCombinations
			for ( int[] t_lCountCombo : t_lCountCombos ) {
				IonCombination t_ionComboCopy = t_ionComboNew.copy();
				for ( int i=0; i<t_ionCombo.getIonExchanges().size(); i++ ) {
					IonAdduct t_ionExchange = t_ionCombo.getIonExchanges().get(i);
					if ( t_lCountCombo[i] > 0 )
						t_ionComboCopy.addIonExchange(t_ionExchange, t_lCountCombo[i]);

					int t_nMovedIons = t_ionExchange.getCount() - t_lCountCombo[i];
					if ( t_nMovedIons == 0 )
						continue;

					// Add ion count of IonAdduct if the same ones are contained
					boolean t_bIsExistSameIon = false;
					for ( IonAdduct t_ionAdduct : t_ionComboCopy.getIonAdducts() ) {
						if ( !this.isSameIon(t_ionAdduct, t_ionExchange) )
							continue;
						t_bIsExistSameIon = true;
						t_ionAdduct.setCount( t_ionAdduct.getCount() + t_nMovedIons );
					}
					if ( !t_bIsExistSameIon )
						t_ionComboCopy.addIonAdduct( t_ionExchange, t_nMovedIons );
				}

				// Add the new IonCombination if not contained
				if ( !t_lIonComboToBeAdded.contains(t_ionComboCopy) )
					t_lIonComboToBeAdded.add(t_ionComboCopy);
			}
		}

		List<IonCombination> t_lIonCombosNew = new ArrayList<>();
		for ( IonCombination t_ionCombo : a_ionCombos ) {
			if ( t_lIonComboToBeRemoved.contains(t_ionCombo) )
				continue;
			t_lIonCombosNew.add(t_ionCombo);
		}
		t_lIonCombosNew.addAll( t_lIonComboToBeAdded );

		return t_lIonCombosNew;
	}

	/**
	 * Checks the given IonAdduct is hydrogen.
	 * @param a_ionAdduct IonAdduct to be checked
	 * @return true if the given IonAdduct is hydrogen
	 */
	private boolean isAdductHydrogen(IonAdduct a_ionAdduct) {
		return ( a_ionAdduct.getLabel().equals("H") && a_ionAdduct.getMass() == 1.007825032 );
	}

	private boolean isSameIon( Ion a_ion1, Ion a_ion2 ) {
		if ( !a_ion1.getLabel().equals( a_ion2.getLabel() )
		  || !a_ion1.getName().equals(  a_ion2.getName()  )
		  || !a_ion1.getMass().equals(  a_ion2.getMass()  )
		  || !a_ion1.getCharge().equals( a_ion2.getCharge() )
		  || !a_ion1.getPolarity().equals( a_ion2.getPolarity() )
		)
			return false;
		return true;
	}

	/**
	 * Generates possible Molecules with the number from the given List of MoleculeSettings.
	 * @param a_lMols List of MoleculeSettings
	 * @return List of Map of Moelcule to the number
	 * @see #searchPossibleMoleculeCombination(Map, List, List)
	 */
	private List<Map<Molecule, Integer>> generatePossibleMoleculeCombination(List<MoleculeSettings> a_lMols) {
		this.m_lMolToNumbers = new ArrayList<>();

		// Search possible combination
		List<Molecule> t_lSearchedMols = new ArrayList<>();
		// Start with each molecule
		for ( MoleculeSettings t_mol : a_lMols ) {
			// Add each number of molecule
			for (int i = 0; i < t_mol.getCount(); i++) {
				Map<Molecule, Integer> t_mapMolToNum = new HashMap<>();
				t_mapMolToNum.put(t_mol, i+1);
				this.searchPossibleMoleculeCombination(t_mapMolToNum, t_lSearchedMols, a_lMols);
			}
			t_lSearchedMols.add(t_mol);
		}
		return this.m_lMolToNumbers;
	}

	/**
	 * Searches and collects possible molecule combinations from the given List of MoleculeSettings recursively.
	 * @param a_mapMolToNumber Map of Molecule to the number
	 * @param a_lSearchedIons List of searched Molecules for ignoring duplication
	 * @param a_lMols List of MoleculeSettings
	 */
	private void searchPossibleMoleculeCombination(
			Map<Molecule, Integer> a_mapMolToNumber, List<Molecule> a_lSearchedMols,
			List<MoleculeSettings> a_lMols) {

		// Add if new combination
		if ( this.isNewMoleculeCombination(a_mapMolToNumber) ) {
			// Copy and add combination
			Map<Molecule, Integer> t_mapCopy = new HashMap<>();
			for ( Molecule t_mol : a_mapMolToNumber.keySet() )
				t_mapCopy.put(t_mol, a_mapMolToNumber.get(t_mol));
			this.m_lMolToNumbers.add(t_mapCopy);
		}

		// Add other ions
		for ( MoleculeSettings t_mol : a_lMols ) {
			// Skip ions to ignore
			if ( a_lSearchedMols.contains(t_mol) )
				continue;
			// Skip exist ions
			if ( a_mapMolToNumber.containsKey(t_mol) )
				continue;
			// Add each number of molecule
			for (int i = 0; i < t_mol.getCount(); i++) {
				a_mapMolToNumber.put(t_mol, i+1);
				this.searchPossibleMoleculeCombination(a_mapMolToNumber, a_lSearchedMols, a_lMols);
				a_mapMolToNumber.remove(t_mol, i+1);
			}
		}
	}

	/**
	 * Generates possible Ion with the number from the given List of IonSettings.
	 * @param a_lIons List of IonSettings
	 * @param a_nMaxIonCnt Max number of ion count
	 * @param a_bIsExchange Whether or not this is for ion exchange
	 * @return List of Map of Molecule to the number for the possible Ion with the number
	 */
	private List<Map<Molecule, Integer>> generatePossibleIonCombinations(List<IonSettings> a_lIons, int a_nMaxIonCnt, boolean a_bIsExchange) {
		this.m_lMolToNumbers = new ArrayList<>();

		// Search possible combination
		List<Ion> t_lSearchedIons = new ArrayList<>();
		// Start with each ion
		for ( IonSettings t_ion : a_lIons ) {
			// Add each number of ion
			for ( int t_nIon : t_ion.getCounts() ) {
				Map<Ion, Integer> t_mapIonToNum = new HashMap<>();
				t_mapIonToNum.put(t_ion, t_nIon);
				this.searchPossibleIonCombination(t_mapIonToNum, t_lSearchedIons, a_lIons, a_nMaxIonCnt, a_bIsExchange);
			}
			t_lSearchedIons.add(t_ion);
		}

		return this.m_lMolToNumbers;
	}

	/**
	 * Searches possible ion combinations recursively. 
	 * @param a_mapIonToNumber Map of Ion to the number
	 * @param a_lSearchedIons List of searched ions for ignoring duplication
	 * @param a_lIons List of IonSettings
	 * @param a_nMaxIonCnt Number of max ion count
	 * @param a_bIsExchange Whether or not this is for exchange ions
	 */
	private void searchPossibleIonCombination(
			Map<Ion, Integer> a_mapIonToNumber, List<Ion> a_lSearchedIons,
			List<IonSettings> a_lIons, int a_nMaxIonCnt, boolean a_bIsExchange) {

		// Check ion count
		if ( a_nMaxIonCnt != -1 ) {
			// Count current ion count
			int t_nCurIonCount = 0;
			for ( Ion t_ion : a_mapIonToNumber.keySet() )
				t_nCurIonCount += a_mapIonToNumber.get(t_ion)
									* ((a_bIsExchange)? 1 : t_ion.getCharge());
			// Return if ion count is exceeded
			if ( t_nCurIonCount > a_nMaxIonCnt )
				return;
		}

		// Add if new combination
		if ( this.isNewIonCombination(a_mapIonToNumber) ) {
			// Copy and add combination
			Map<Molecule, Integer> t_mapCopy = new HashMap<>();
			for ( Ion t_ion : a_mapIonToNumber.keySet() )
				t_mapCopy.put(t_ion, a_mapIonToNumber.get(t_ion));
			this.m_lMolToNumbers.add(t_mapCopy);
		}

		// Add other ions
		for ( IonSettings t_ion : a_lIons ) {
			// Skip ions to ignore
			if ( a_lSearchedIons.contains(t_ion) )
				continue;
			// Skip exist ions
			if ( a_mapIonToNumber.containsKey(t_ion) )
				continue;
			// Add each number of ion
			for ( int t_nIon : t_ion.getCounts() ) {
				a_mapIonToNumber.put(t_ion, t_nIon);
				this.searchPossibleIonCombination(a_mapIonToNumber, a_lSearchedIons, a_lIons, a_nMaxIonCnt, a_bIsExchange);
				a_mapIonToNumber.remove(t_ion, t_nIon);
			}
		}
	}

	private boolean isNewIonCombination(Map<Ion, Integer> a_mapIonToNumber) {
		Map<Molecule, Integer> t_mapMolToNumber = new HashMap<>();
		for ( Ion t_ion : a_mapIonToNumber.keySet() )
			t_mapMolToNumber.put(t_ion, a_mapIonToNumber.get(t_ion));

		return this.isNewMoleculeCombination(t_mapMolToNumber);
	}

	private boolean isNewMoleculeCombination(Map<Molecule, Integer> a_mapMolToNumber) {
		for ( Map<Molecule, Integer> t_mapIonToNumberStored : this.m_lMolToNumbers )
			if ( this.compareCombination(t_mapIonToNumberStored, a_mapMolToNumber) )
				return false;
		return true;
	}

	private boolean compareCombination(Map<Molecule, Integer> a_map1, Map<Molecule, Integer> a_map2) {
		// False if number of ion types are different
		if ( a_map1.keySet().size() != a_map2.keySet().size() )
			return false;

		for ( Molecule t_mol1 : a_map1.keySet() ) {
			// False if there is different ion between two combinations
			if ( !a_map2.keySet().contains(t_mol1) )
				return false;

			int t_nMol1 = a_map1.get(t_mol1);
			for ( Molecule t_mol2 : a_map2.keySet() ) {
				if ( !t_mol1.equals(t_mol2) )
					continue;
				int t_nMol2 = a_map2.get(t_mol2);
				// False if there are same ions having different number
				if ( t_nMol1 != t_nMol2 )
					return false;
			}
		}
		return true;
	}

	/**
	 * Gets IonCombination having just one ionAdduct.
	 * @param a_ion Ion to be stored as IonAdduct
	 * @param a_nIon Number of the given Ion
	 * @return IonCombination having only the given Ion with the given number
	 */
	public static IonCombination getSingleIonCombo(Ion a_ion, int a_nIon) {
		IonCombination t_ionCombo = new IonCombination();
		t_ionCombo.addIonAdduct(a_ion, a_nIon);
		return t_ionCombo;
	}

	/**
	 * Gets IonCombination having only one hydrogen.
	 * @return IonCombination having only one hydrogen
	 * @see #getSingleIonCombo(Ion, int)
	 */
	public static IonCombination getHydrogenIon() {
		return getSingleIonCombo(GlycanPreDefinedOptions.ION_ADDUCT_HYDROGEN, 1);
	}

}
