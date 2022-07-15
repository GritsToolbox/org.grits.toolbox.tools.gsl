package org.grits.toolbox.tools.gsl.util.mass;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.grits.toolbox.ms.annotation.structure.GlycanPreDefinedOptions;
import org.grits.toolbox.tools.gsl.database.IonizationType;

/**
 * Class for calculating chemical composition. Provides a String of molecular formula and monoisotopic mass.
 * @author Masaaki Matsubara
 * @see IMass
 *
 */
public class ChemicalComposition {

	private Map<IMass, Integer> m_mapAtomToNumber;
	private boolean m_bIsPermethylated;

	/*
	 * org.grits.toolbox.ms.annotation.structure.GlycanPreDefinedOptions
	public static String DERIVITIZATION_PERMETHYLATED = "perMe";
	public static String DERIVITIZATION_HEAVYPERMETHYLATION = "perMe(C^13)";
	public static String DERIVITIZATION_PERDMETHYLATED = "perDMe";
	public static String DERIVITIZATION_PERACETYLATED = "perAc";
	public static String DERIVITIZATION_PERDACETYLATED = "perDAc";
	public static String DERIVITIZATION_NO_DERIVATIZATION = "None";
	 */
	protected String m_strDerivatizationType;

	public ChemicalComposition() {
		this.m_mapAtomToNumber = new HashMap<IMass, Integer>();
		this.m_bIsPermethylated = false;
		this.m_strDerivatizationType = GlycanPreDefinedOptions.DERIVITIZATION_NO_DERIVATIZATION;
	}

	@Override
	public String toString() {
		return this.getFormula();
	}

	public void clear() {
		this.m_mapAtomToNumber = new HashMap<IMass, Integer>();
	}

	public Map<IMass, Integer> getNumberOfElements() {
		return this.m_mapAtomToNumber;
	}

	/**
	 * Set derivatization type. Also recalculate its chemical composition.
	 * @see org.grits.toolbox.ms.annotation.structure.GlycanPreDefinedOptions
	 * @param a_strDerivatizationType String of derivatization type defined in GlycanPreDefinedOptions
	 */
	public void derivatize(String a_strDerivatizationType) {
		this.m_bIsPermethylated = true;
		this.m_strDerivatizationType = a_strDerivatizationType;
		this.start();
	}

	/**
	 * Set permethylated derivatization type. Also recalculate chemical composition.
	 */
	public void derivatize() {
		this.m_strDerivatizationType = GlycanPreDefinedOptions.DERIVITIZATION_PERMETHYLATED;
		this.m_bIsPermethylated = true;
		this.start();
	}

	public boolean isDerivatized() {
		return this.m_bIsPermethylated;
	}

	/**
	 * Start addition of elements from the structure information.
	 */
	protected void start() {
		// Do nothing at this level.
		// This should be overrided in subclass
	}

	/**
	 * Add the given element with the given number to this.
	 * Remove the element if the number of element is zero as a result of the addition.
	 * @param a_enumAM IMass of an atom to be added (or subtracted)
	 * @param a_nAtom The number of atom (negative value means reducing the atom)
	 */
	public void addNumberOfElements( IMass a_enumAM, int a_nAtom ) {
		int t_nAtom = a_nAtom;
		if ( this.m_mapAtomToNumber.containsKey(a_enumAM) ) {
			t_nAtom += this.m_mapAtomToNumber.get(a_enumAM);
			// Remove atom if number of atom is zero
			if ( t_nAtom == 0 )
				this.m_mapAtomToNumber.remove(a_enumAM);
		}
		if ( t_nAtom != 0 )
			this.m_mapAtomToNumber.put(a_enumAM, t_nAtom);
	}

	/**
	 * Add elements contained in the given ChemicalComposition to this.
	 * @param a_ChemComp ChemicalComposition having elements to add
	 */
	public void addComposition( ChemicalComposition a_ChemComp ) {
		Map<IMass, Integer> t_mapAtomToNumber = a_ChemComp.getNumberOfElements();
		for ( IMass t_enumAM : t_mapAtomToNumber.keySet() ) {
			this.addNumberOfElements( t_enumAM, t_mapAtomToNumber.get(t_enumAM) );
		}
	}

	/**
	 * Add substituents with the specified number to this.
	 * @param a_strType String of substituent defined as derivatization type in GlycanPreDefinedOptions
	 * @param a_nSubst number of substituents
	 * @see org.grits.toolbox.ms.annotation.structure.GlycanPreDefinedOptions
	 */
	public void addSubstituents( String a_strType, int a_nSubst ) {
		if ( a_strType == null )
			return;

		if ( a_strType.equals(GlycanPreDefinedOptions.DERIVITIZATION_PERMETHYLATED) ) {
			// -H -> -CH3
			this.addNumberOfElements(AtomicMass.C,  a_nSubst);
			this.addNumberOfElements(AtomicMass.H,  a_nSubst*2);
		} else if ( a_strType.equals(GlycanPreDefinedOptions.DERIVITIZATION_HEAVYPERMETHYLATION) ) {
			// -H -> -(C^13)H3
			this.addNumberOfElements(AtomicMass.C13,  a_nSubst);
			this.addNumberOfElements(AtomicMass.H,  a_nSubst*2);
		} else if ( a_strType.equals(GlycanPreDefinedOptions.DERIVITIZATION_PERDMETHYLATED) ) {
			// -H -> -CD3
			this.addNumberOfElements(AtomicMass.H, -a_nSubst);
			this.addNumberOfElements(AtomicMass.C,  a_nSubst);
			this.addNumberOfElements(AtomicMass.D,  a_nSubst*3);
		} else if ( a_strType.equals(GlycanPreDefinedOptions.DERIVITIZATION_PERACETYLATED) ) {
			// -H -> -C(=O)CH3
			this.addNumberOfElements(AtomicMass.C,  a_nSubst*2);
			this.addNumberOfElements(AtomicMass.H,  a_nSubst*2);
			this.addNumberOfElements(AtomicMass.O,  a_nSubst);
		} else if ( a_strType.equals(GlycanPreDefinedOptions.DERIVITIZATION_PERDACETYLATED) ) {
			// -H -> -C(=O)CD3
			this.addNumberOfElements(AtomicMass.H, -a_nSubst);
			this.addNumberOfElements(AtomicMass.C,  a_nSubst*2);
			this.addNumberOfElements(AtomicMass.D,  a_nSubst*3);
			this.addNumberOfElements(AtomicMass.O,  a_nSubst);
		}
	}

	/**
	 * Remove elements contained in the given ChemicalComposition from this.
	 * @param a_ChemComp ChemicalComposition having elements to be removed from this
	 */
	public void removeComposition( ChemicalComposition a_ChemComp ) {
		Map<IMass, Integer> t_mapAtomToNumber = a_ChemComp.getNumberOfElements();
		for ( IMass t_enumAM : t_mapAtomToNumber.keySet() ) {
			this.addNumberOfElements( t_enumAM, -t_mapAtomToNumber.get(t_enumAM) );
		}
	}

	/**
	 * Get string of chemical composition formula according Hill system. i.e.
	 * the number of carbon atoms in a molecule is indicated first,
	 * the number of hydrogen atoms next, and then
	 * the number of all other chemical elements subsequently,
	 * in alphabetical order of the chemical symbols.
	 * Charge value is also added to end of the string.
	 * @return String of molecular chemical formula
	 */
	public String getFormula() {
		String t_strFormula = "";
		String t_strFormulaMinus = "";
		int t_iCharge = 0;

		// List carbon composition
		if ( this.m_mapAtomToNumber.containsKey( AtomicMass.C ) ) {
			int t_nC = this.m_mapAtomToNumber.get( AtomicMass.C );
			if ( t_nC > 0 )
				t_strFormula += this.getAtomicFormula( AtomicMass.C );
			if ( t_nC < 0 )
				t_strFormulaMinus += this.getAtomicFormula( AtomicMass.C );
		}
		// List hydrogen composition
		int t_nH = 0;
		int t_nMH = 0;
		if ( this.m_mapAtomToNumber.containsKey( AtomicMass.H ) ) {
			if ( this.m_mapAtomToNumber.get( AtomicMass.H ) > 0 )
				t_nH += this.m_mapAtomToNumber.get( AtomicMass.H );
			else
				t_nMH -= this.m_mapAtomToNumber.get( AtomicMass.H );
		}
		/// For proton (H+)
		if ( this.m_mapAtomToNumber.containsKey( QuantumMass.p ) ) {
			t_iCharge += this.m_mapAtomToNumber.get( QuantumMass.p );
			if ( this.m_mapAtomToNumber.get( QuantumMass.p ) > 0 )
				t_nH += this.m_mapAtomToNumber.get( QuantumMass.p );
			else
				t_nMH -= this.m_mapAtomToNumber.get( QuantumMass.p );
		}
		if ( t_nH > 0 ) {
			t_strFormula += "H";
			if ( t_nH > 1 )
				t_strFormula += t_nH;
		}
		if ( t_nMH > 0 ) {
			t_strFormulaMinus += "H"; 
			if ( t_nMH > 1 )
				t_strFormulaMinus += t_nMH; 
		}
		// For electron (e-)
		if ( this.m_mapAtomToNumber.containsKey( QuantumMass.e ) ) {
			if ( this.m_mapAtomToNumber.get( QuantumMass.e ) != 0 )
				t_iCharge -= this.m_mapAtomToNumber.get( QuantumMass.e );
			
		}

		// List other element symbols ordering alphabetically
		TreeSet<String> t_aSymbols = new TreeSet<String>();
		// Collect and sort element symbols other than C and H
		for ( IMass t_enumAM : this.m_mapAtomToNumber.keySet() ) {
			if ( t_enumAM == AtomicMass.C ) continue;
			if ( t_enumAM == AtomicMass.H ) continue;
			if ( t_enumAM == QuantumMass.p ) continue;
			if ( t_enumAM == QuantumMass.e ) continue;
			t_aSymbols.add( t_enumAM.getSymbol() );
		}
		// Add compositions ordered alphabetical by TreeSet
		for ( String t_strSymbol : t_aSymbols ) {
			IMass t_atom = AtomicMass.forSymbol(t_strSymbol);
			int t_nAtom = this.m_mapAtomToNumber.get( t_atom );
			// For negative number of atoms
			if ( t_nAtom > 0 )
				t_strFormula += this.getAtomicFormula( t_atom );
			if ( t_nAtom < 0 )
				t_strFormulaMinus += this.getAtomicFormula( t_atom );
		}
		if ( !t_strFormulaMinus.isEmpty() )
			t_strFormula = "("+t_strFormula+" - "+t_strFormulaMinus+")";
		// Add charge
		if ( t_iCharge != 0 )
			t_strFormula += ((Math.abs(t_iCharge)==1)? "" : "["+( Math.abs(t_iCharge))+((t_iCharge > 0)? "+" : "-" )+"]");

		return t_strFormula;
	}

	private String getAtomicFormula( IMass a_atom ) {
		String t_strFormula = "";
		int t_nAtom = Math.abs( this.m_mapAtomToNumber.get( a_atom ) );
		if ( t_nAtom > 0 ) {
			t_strFormula += a_atom.getSymbol();
			if ( t_nAtom > 1 )
				t_strFormula += t_nAtom;
		}
		return t_strFormula;
	}

	/**
	 * Parse string of molecular chemical formula to the elements
	 * and create a ChemicalComposition having the elements.
	 * If the string start with "-" this means subtracting the molecule.
	 * @param a_strFormula String of chemical composition formula
	 * @return ChemicalComposition parsed from formula (null if there is a symbol which cannot be converted)
	 */
	public static ChemicalComposition parseFormula(String a_strFormula) {
		String t_strFormula = a_strFormula;
		if ( a_strFormula.startsWith("-") )
			t_strFormula = a_strFormula.substring(1);
		ChemicalComposition t_ChemComp = new ChemicalComposition();
		int t_iSize = t_strFormula.length();
		for (int i = 0; i < t_iSize; i++) {
			char t_cX = t_strFormula.charAt(i);

			IMass t_iMass = null;
			int t_nAtom = 1;

			// Parse element symbol
			if ( Character.isAlphabetic(t_cX) ) {
				String t_strAtom = "" + t_cX;
				// Check and add next character
				if ( i+1 < t_iSize ) {
					char t_cNext = t_strFormula.charAt(i+1);
					if ( Character.isLowerCase(t_cNext) ) {
						t_strAtom += t_cNext;
						i++;
					}
				}
				t_iMass = AtomicMass.forSymbol(t_strAtom);
			}
			// Parse charge
			if ( t_cX == '+' || t_cX == '-' ) {
				t_iMass = QuantumMass.e;
				// Decrease electron if positive charge
				if ( t_cX == '+' )
					t_nAtom = -1;
			}
			if ( t_iMass == null )
				return null;

			// Parse number of element
			String t_strNum = "";
			while ( i+1 < t_iSize && Character.isDigit( t_strFormula.charAt(i+1) ) ) {
				t_strNum += t_strFormula.charAt(i+1);
				i++;
			}
			if ( !t_strNum.isEmpty() )
				t_nAtom *= Integer.valueOf(t_strNum);

			t_ChemComp.addNumberOfElements(t_iMass, t_nAtom);
		}
		return t_ChemComp;
	}
/*
	public String getAverageMass() {
		BigDecimal t_bdMass = new BigDecimal("0");
		for ( IMass t_enumM : this.m_mapAtomToNumber.keySet() ) {
			if ( !(t_enumM instanceof AtomicMass) ) continue;
			AtomicMass t_enumAM = (AtomicMass)t_enumM;
			// Calc average atom mass
			BigDecimal t_bdAtomMass = new BigDecimal( t_enumAM.getMass() );
			BigDecimal t_bdTotalIntencity = new BigDecimal(1);
			if ( t_enumAM.getMassPlus1() != null ) {
				BigDecimal t_bdRelInt = new BigDecimal( t_enumAM.getRelativeIntencityPlus1() ).multiply( new BigDecimal(0.01) );
				t_bdAtomMass = t_bdAtomMass.add( new BigDecimal( t_enumAM.getMassPlus1() ).multiply( t_bdRelInt ) );
				t_bdTotalIntencity = t_bdTotalIntencity.add( t_bdRelInt );
			}
			if ( t_enumAM.getMassPlus2() != null ) {
				BigDecimal t_bdRelInt = new BigDecimal( t_enumAM.getRelativeIntencityPlus2() ).multiply( new BigDecimal(0.01) );
				t_bdAtomMass = t_bdAtomMass.add( new BigDecimal( t_enumAM.getMassPlus2() ).multiply( t_bdRelInt ) );
				t_bdTotalIntencity = t_bdTotalIntencity.add( t_bdRelInt );
			}
			t_bdAtomMass = t_bdAtomMass.divide(t_bdTotalIntencity, 10, BigDecimal.ROUND_HALF_UP);
			int t_nAtom = this.m_mapAtomToNumber.get( t_enumAM );
			t_bdMass = t_bdMass.add( t_bdAtomMass.multiply(new BigDecimal(t_nAtom)) );
		}
		return t_bdMass.toPlainString();
	}
*/

	/**
	 * Return monoisotopic mass of this molecule.
	 * @return String of monoisotopic mass
	 */
	public String getMonoisotopicMass() {
		BigDecimal t_bdMass = new BigDecimal("0");
		for ( IMass t_enumAM : this.m_mapAtomToNumber.keySet() ) {
			BigDecimal t_bdAtomMass = new BigDecimal( t_enumAM.getExactMass() );
			int t_nAtom = this.m_mapAtomToNumber.get( t_enumAM );
			t_bdMass = t_bdMass.add( t_bdAtomMass.multiply(new BigDecimal(t_nAtom)) );
		}
		return t_bdMass.toPlainString();
	}

	/**
	 * Get String of monoisotopic mass with the given ion.
	 * @param a_type IonizationType to be added to this composition
	 * @return String of monoisotopic mass with the given ion
	 * @see IonizationType
	 */
	public String getMonoisotopicMass(IonizationType a_type) {
		return this.getIonizedComposition(a_type).getMonoisotopicMass();
	}

	/**
	 * Get molecular formula with the given ion.
	 * @param a_type IonizationType to be added to this composition
	 * @return String of molecular formula with the given ion.
	 * @see IonizationType
	 */
	public String getFormula(IonizationType a_type) {
		return this.getIonizedComposition(a_type).getFormula();
	}

	private ChemicalComposition getIonizedComposition(IonizationType a_type) {
		ChemicalComposition t_calcMass = null;
		switch ( a_type ) {
		case PH: // [M + H]+
			t_calcMass = this.getCustomIonizedComposition(AtomicMass.H, 1, false);
			break;
		case MH: // [M - H]-
			t_calcMass = this.getCustomIonizedComposition(AtomicMass.H, -1, false);
			break;
		case PNA: // [M + Na]+
			t_calcMass = this.getCustomIonizedComposition(AtomicMass.Na, 1, false);
			break;
		case PHMH2O: // [M + H - H2O]+
			t_calcMass = this.getCustomIonizedComposition(AtomicMass.H, 1, true);
			break;
		case NOIONMH2O: // M - H2O
			t_calcMass = this.getCustomIonizedComposition(AtomicMass.H, 0, true);
			break;
		default:
			t_calcMass = this.copy();
			break;
		}
		return t_calcMass;
	}

	/**
	 * Get chemical composition with the given ion.
	 * @param a_atom AtomicMass indicating an atom
	 * @param a_iCount The number of atoms
	 * @param a_bMH2O {@code true} 
	 * @return ChemicalComposition with the given ions
	 */
	public ChemicalComposition getCustomIonizedComposition(AtomicMass a_atom, int a_iCount, boolean a_bMH2O) {
		ChemicalComposition t_calcMass = this.copy();

		t_calcMass.addNumberOfElements(a_atom, a_iCount);
		t_calcMass.addNumberOfElements(QuantumMass.e, -a_iCount);
		if ( a_bMH2O ) {
			t_calcMass.addNumberOfElements(AtomicMass.H, -2);
			t_calcMass.addNumberOfElements(AtomicMass.O, -1);
			if ( this.m_bIsPermethylated ) { // [M + H - MeOH]+
				t_calcMass.addNumberOfElements(AtomicMass.C, -1);
				t_calcMass.addNumberOfElements(AtomicMass.H, -2);
			}
		}

		return t_calcMass;
	}

	/**
	 * Copy this.
	 * @return ChemicalComposition of copy.
	 */
	public ChemicalComposition copy() {
		ChemicalComposition t_copy = new ChemicalComposition();
		t_copy.m_strDerivatizationType = this.m_strDerivatizationType;
		t_copy.m_bIsPermethylated = this.m_bIsPermethylated;
		for ( IMass t_enumAM : this.m_mapAtomToNumber.keySet() )
			t_copy.m_mapAtomToNumber.put(t_enumAM, this.m_mapAtomToNumber.get(t_enumAM));
		return t_copy;
	}
}
