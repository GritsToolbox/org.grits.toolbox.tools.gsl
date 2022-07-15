package org.grits.toolbox.tools.gsl.util.mass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Calculate combination of masses and intensities for isotopic distributions from a ChemicalComposition.
 * @author MasaakiMatsubara
 * @see ChemicalComposition
 *
 */
public class IsotopicDistributionCalculator {

	private static Map<AtomicMass, Map<Integer, Map<BigDecimal, BigDecimal>>> mapAtomToNumToMassesToIntensities = new TreeMap<>();

	private ChemicalComposition m_oChemComp;
	private List<String> m_lStrMasses;
	private List<String> m_lStrIntensities;
	private List<String> m_lStrScaledIntensities;
	private String m_strAverageMass;
	private BigDecimal m_dMinAbundance;
	private int m_iCutAbundanceScale;
	private int m_iDecimalScale;

	public IsotopicDistributionCalculator(ChemicalComposition a_oChemComp) {
		this.m_oChemComp = a_oChemComp;
		this.m_lStrMasses = new ArrayList<>();
		this.m_lStrIntensities = new ArrayList<>();
		this.m_lStrScaledIntensities = new ArrayList<>();
		this.m_dMinAbundance = new BigDecimal(0.001D);
		this.m_iCutAbundanceScale = 10;
		this.m_iDecimalScale = 5;
	}

	/**
	 * Set the minimum abundance for filtering out peaks having small intensities.
	 * @param a_dMinAb number of minimum abundance
	 */
	public void setMinimumAbundance(double a_dMinAb) {
		this.m_dMinAbundance = new BigDecimal(a_dMinAb);
	}

	/**
	 * Set the number of digits after the decimal point.
	 * @param a_iScale number of digits after the decimal point
	 */
	public void setDecimalScale(int a_iScale) {
		if ( a_iScale < 0 || a_iScale > 10 ) return;
		this.m_iDecimalScale = a_iScale;
	}

	public List<String> getMasses() {
		return this.m_lStrMasses;
	}

	public List<String> getIntensities() {
		return this.m_lStrIntensities;
	}

	public List<String> getScaledIntensities() {
		return this.m_lStrScaledIntensities;
	}

	public String getAverageMass() {
		return this.m_strAverageMass;
	}

	/**
	 * Calculate isotopic distribution.
	 */
	public void calculate() {
		Map<QuantumMass, Integer> t_mapQMassToNum = new HashMap<>();
		// Calculate mass distribution
		Map<BigDecimal, BigDecimal> t_mapAtomsMassToIntensity = null;
		for ( IMass t_mass : this.m_oChemComp.getNumberOfElements().keySet() ) {
			if ( !(t_mass instanceof AtomicMass) ) {
				if ( t_mass instanceof QuantumMass )
					t_mapQMassToNum.put((QuantumMass)t_mass, this.m_oChemComp.getNumberOfElements().get(t_mass));
				continue;
			}
			AtomicMass t_aMass = (AtomicMass)t_mass;
			int t_nAtom = this.m_oChemComp.getNumberOfElements().get(t_mass);
			if ( t_mapAtomsMassToIntensity == null ) {
				t_mapAtomsMassToIntensity = this.multiplyAtom(t_aMass, t_nAtom);
				continue;
			}
			t_mapAtomsMassToIntensity = this.combine( t_mapAtomsMassToIntensity, this.multiplyAtom(t_aMass, t_nAtom) );
		}
		// Calculate m/z values if contains quantum mass
		if ( !t_mapQMassToNum.isEmpty() ) {
			BigDecimal t_bdQMass = BigDecimal.ZERO;
			int t_iTotalCharge = 0;
			for ( QuantumMass t_qMass : t_mapQMassToNum.keySet() ) {
				int t_nQMass = t_mapQMassToNum.get(t_qMass);
				t_iTotalCharge += t_nQMass*t_qMass.getCharge();
				t_bdQMass = t_bdQMass.add( new BigDecimal(t_qMass.getExactMass()).multiply(new BigDecimal(t_nQMass)) );
			}
			if ( t_iTotalCharge != 0 ) {
				t_iTotalCharge = Math.abs(t_iTotalCharge);
				Map<BigDecimal, BigDecimal> t_mapMzToIntencity = new TreeMap<>();
				for ( BigDecimal t_bdMass : t_mapAtomsMassToIntensity.keySet() ) {
					BigDecimal t_bdMz = t_bdMass.add(t_bdQMass);
					if ( t_iTotalCharge > 1 )
//						t_bdMz = t_bdMz.divide( new BigDecimal(t_iTotalCharge), this.m_iDecimalScale, RoundingMode.HALF_UP );
						t_bdMz = t_bdMz.divide( new BigDecimal(t_iTotalCharge));
					t_mapMzToIntencity.put(t_bdMz, t_mapAtomsMassToIntensity.get(t_bdMass));
				}
				t_mapAtomsMassToIntensity = t_mapMzToIntencity;
			}
		}
		// Merge results
		if ( this.m_iDecimalScale < 10 ) {
			Map<BigDecimal, BigDecimal> t_mapAtomsMassToIntensityMerged = new TreeMap<>();
			for ( BigDecimal t_bdMass : t_mapAtomsMassToIntensity.keySet() ) {
				BigDecimal t_bdIntensity = t_mapAtomsMassToIntensity.get(t_bdMass);
				BigDecimal t_bdScaledMass = t_bdMass.setScale(this.m_iDecimalScale, RoundingMode.HALF_UP);
				if ( t_mapAtomsMassToIntensityMerged.containsKey(t_bdScaledMass) )
					t_bdIntensity = t_bdIntensity.add(t_mapAtomsMassToIntensityMerged.get(t_bdScaledMass));
				t_mapAtomsMassToIntensityMerged.put(t_bdScaledMass, t_bdIntensity);
			}
			t_mapAtomsMassToIntensity = t_mapAtomsMassToIntensityMerged;
		}
		// Normalize intensity
		t_mapAtomsMassToIntensity = this.normalize(t_mapAtomsMassToIntensity);
		// Search max intensity
		BigDecimal t_bdMaxInt = BigDecimal.ZERO;
		for ( BigDecimal t_bdInt : t_mapAtomsMassToIntensity.values() )
			if ( t_bdInt.compareTo(t_bdMaxInt) > 0 )
				t_bdMaxInt = t_bdInt;
		// Export results
		BigDecimal t_bdAve = BigDecimal.ZERO;
		for ( BigDecimal t_bdMass : t_mapAtomsMassToIntensity.keySet() ) {
			// Calculate average
			BigDecimal t_bdInt = t_mapAtomsMassToIntensity.get(t_bdMass);
			t_bdAve = t_bdAve.add(t_bdMass.multiply(t_bdInt));

			// Skip if the intensity is small
			BigDecimal t_bdScaledInt = t_bdInt.divide(t_bdMaxInt, this.m_iCutAbundanceScale, RoundingMode.HALF_UP);
			if ( t_bdScaledInt.compareTo( this.m_dMinAbundance ) <= 0 ) continue;

			this.m_lStrMasses.add( t_bdMass.toPlainString() );
			t_bdInt = t_bdInt.multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
			this.m_lStrIntensities.add( t_bdInt.toPlainString() );
			t_bdScaledInt = t_bdScaledInt.multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
			this.m_lStrScaledIntensities.add( t_bdScaledInt.toPlainString() );
		}
		this.m_strAverageMass = t_bdAve.toPlainString();
	}

	/**
	 * Multiply isotopic distributions for the given atom by the given number.
	 * @param a_massAtom AtomicMass to be used to multiply the mass-to-intensity
	 * @param a_nAtom number of atom
	 * @return Map of mass-to-intensity of the given atoms
	 */
	public Map<BigDecimal, BigDecimal> multiplyAtom(AtomicMass a_massAtom, int a_nAtom ) {
		if ( !mapAtomToNumToMassesToIntensities.containsKey(a_massAtom) ) {
			// Initialize mass-to-intensity map for the given atom
			Map<Integer, Map<BigDecimal, BigDecimal>> t_mapAtomNumToMassToIntensity = null;
			Map<BigDecimal, BigDecimal> t_mapAtomMassToIntensity = new TreeMap<>();
			// For Mass0
			BigDecimal t_bdMass = (new BigDecimal(a_massAtom.getExactMass()));
			t_mapAtomMassToIntensity.put( t_bdMass, BigDecimal.ONE );
			// For Mass1
			if ( a_massAtom.getMassPlus1() != null ) {
				t_bdMass = (new BigDecimal(a_massAtom.getMassPlus1()));
				BigDecimal t_bdInten = new BigDecimal(a_massAtom.getRelativeIntensityPlus1()).multiply( new BigDecimal(0.01) );
				t_mapAtomMassToIntensity.put( t_bdMass, t_bdInten );
			}
			// For Mass2
			if ( a_massAtom.getMassPlus2() != null ) {
				t_bdMass = (new BigDecimal(a_massAtom.getMassPlus2()));
				BigDecimal t_bdInten = new BigDecimal(a_massAtom.getRelativeIntensityPlus2()).multiply( new BigDecimal(0.01) );
				t_mapAtomMassToIntensity.put( t_bdMass, t_bdInten );
			}
			t_mapAtomNumToMassToIntensity = new TreeMap<>();
			t_mapAtomNumToMassToIntensity.put(0, t_mapAtomMassToIntensity);
			mapAtomToNumToMassesToIntensities.put(a_massAtom, t_mapAtomNumToMassToIntensity);
		}
		if ( a_nAtom == 1 )
			return mapAtomToNumToMassesToIntensities.get(a_massAtom).get(0);

		// Calculate with binary steps
		Map<BigDecimal, BigDecimal> t_mapAtomsMassToIntencity = null;
		String bin = Integer.toBinaryString(a_nAtom);
		for ( int i=0; i<bin.length(); i++ ) {
			if ( !mapAtomToNumToMassesToIntensities.get(a_massAtom).containsKey(i) ) {
				// Calculate twice of lower bin
				Map<BigDecimal, BigDecimal> t_mapLowerBin
					= mapAtomToNumToMassesToIntensities.get(a_massAtom).get(i-1);
				mapAtomToNumToMassesToIntensities.get(a_massAtom).put(i, this.combine(t_mapLowerBin, t_mapLowerBin));
			}
			// Skip if a digit of binary string is zero
			if ( bin.charAt(bin.length()-1-i) == '0' )
				continue;
			if ( t_mapAtomsMassToIntencity == null ) {
				t_mapAtomsMassToIntencity = mapAtomToNumToMassesToIntensities.get(a_massAtom).get(i);
				continue;
			}
			t_mapAtomsMassToIntencity = this.combine( t_mapAtomsMassToIntencity, mapAtomToNumToMassesToIntensities.get(a_massAtom).get(i) );
		}

		return t_mapAtomsMassToIntencity;
	}

	/**
	 * Combine two isotopic distributions.
	 * @param a_map1 a Map of mass-to-intensity to be combined
	 * @param a_map2 another Map of mass-to-intensity to be combined
	 * @return combined Map of mass-to-intensity
	 */
	public Map<BigDecimal, BigDecimal> combine(Map<BigDecimal, BigDecimal> a_map1, Map<BigDecimal, BigDecimal> a_map2) {
		Map<BigDecimal, BigDecimal> t_mapResult = new TreeMap<>();
		for ( BigDecimal t_bd1 : a_map1.keySet() ) {
			for ( BigDecimal t_bd2 : a_map2.keySet() ) {
				BigDecimal t_bdAdd = t_bd1.add(t_bd2);
				BigDecimal t_bdMultIntensity = a_map1.get(t_bd1).multiply(a_map2.get(t_bd2));
				if ( t_mapResult.containsKey(t_bdAdd) )
//				if ( this.cotainsMass(t_mapResult, t_bdAdd) )
					t_bdMultIntensity = t_bdMultIntensity.add( t_mapResult.get(t_bdAdd) );
				t_mapResult.put(t_bdAdd, t_bdMultIntensity);
			}
		}
		// Trim peaks having low intensity
		Map<BigDecimal, BigDecimal> t_mapResultTrim = new TreeMap<>();
		for ( BigDecimal t_bdMass : t_mapResult.keySet() ) {
			BigDecimal t_bdIntensity = t_mapResult.get(t_bdMass).setScale(this.m_iCutAbundanceScale, RoundingMode.HALF_UP);
			if ( t_bdIntensity.compareTo(BigDecimal.ZERO) == 0 )
				continue;
			t_mapResultTrim.put(t_bdMass, t_bdIntensity);
		}
		return t_mapResultTrim;
	}

	private Map<BigDecimal, BigDecimal> normalize(Map<BigDecimal, BigDecimal> a_map) {
		Map<BigDecimal, BigDecimal> t_mapResult = new TreeMap<>();
		// Total intensity
		BigDecimal t_bdTotalInt = BigDecimal.ZERO;
		for ( BigDecimal t_bdInt : a_map.values() )
			t_bdTotalInt = t_bdTotalInt.add(t_bdInt);
		// Normalize intensity
		for ( BigDecimal t_bdMass : a_map.keySet() ) {
			BigDecimal t_bdInt = a_map.get(t_bdMass);
			t_bdInt = t_bdInt.divide(t_bdTotalInt, this.m_iCutAbundanceScale, RoundingMode.HALF_UP);
			t_mapResult.put(t_bdMass, t_bdInt);
		}
		return t_mapResult;
	}

/*
	private boolean containsMass(Map<BigDecimal, BigDecimal> a_map, BigDecimal a_bdValue) {
		for ( BigDecimal a_bdValue0 : a_map.keySet() ) {
			if ( a_bdValue0.compareTo(a_bdValue) == 0 )
				return true;
		}
		return false;
	}
*/
}
