package org.grits.toolbox.tools.gsl.dango;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.grits.toolbox.ms.file.reader.IMSFileReader;
import org.grits.toolbox.ms.om.data.Peak;
import org.grits.toolbox.ms.om.data.Scan;

/**
 * Wrapper class of {@link org.grits.toolbox.ms.om.Scan} for matching and filtering peaks.
 * Finding matched peaks with a user specified accuracy from the intensity cut off peaks.
 * @author Masaaki Matsubara
 * @see org.grits.toolbox.ms.om.Scan
 *
 */
public class ScanReader {

//	private static final Logger logger = Logger.getLogger(ScanReader.class);

	private static double RELATIVE_INTENSITY_FOR_FILTER = 0.05D;
	private Scan m_scan;

	private List<Peak> m_lFilteredPeaks;
	private List<Peak> m_lFilteredPeaksForKeyFragments;
	private double m_dShift;
	private BigDecimal m_dAccuracy;
	private boolean m_bAccuracyPPM;
	private BigDecimal m_dFragAccuracy;
	private boolean m_bFragAccuracyPPM;
	private BigDecimal m_bdMinPeakMz;
	private BigDecimal m_bdMaxPeakMz;

	/**
	 * @param a_scan Scan object to be wrapped
	 */
	public ScanReader(Scan a_scan) {
		this.m_scan = a_scan;

		// Set peaks without precursor peak
		this.m_lFilteredPeaks = new ArrayList<>();
		this.m_lFilteredPeaksForKeyFragments = new ArrayList<>();
		for ( Peak t_peak : a_scan.getPeaklist() ) {
			this.m_lFilteredPeaks.add(t_peak);

			// Calculate relative intensity if not set
			if ( t_peak.getRelativeIntensity() == null ) {
				double t_dRelInt = t_peak.getIntensity();
				t_dRelInt /= a_scan.getMostAbundantPeak();
				t_peak.setRelativeIntensity(t_dRelInt);
			}
			// Filter peaks of less than a certain relative intensity out for key fragments
			if ( t_peak.getRelativeIntensity() < RELATIVE_INTENSITY_FOR_FILTER )
				continue;
			this.m_lFilteredPeaksForKeyFragments.add(t_peak);
		}

		this.m_dShift = 0.0D;
		this.m_dAccuracy = BigDecimal.ZERO;
		this.m_bAccuracyPPM = false;
		this.m_dFragAccuracy = BigDecimal.ZERO;
		this.m_bFragAccuracyPPM = false;
		this.setPeakMzRange();
	}

	public Scan getScan() {
		return this.m_scan;
	}

	/**
	 * Gets scan no. of sub scan if the specified peak is precursor of the sub scan.
	 * @param a_peak - Peak to be a precursor of sub scan
	 * @return Number of sub scan corresponding the peak (-1 if no sub scan correspond to the peak or the peak is not contained in this scan)
	 */
/*	public int getSubScanNumber(Peak a_peak) {
		if ( !this.m_scan.getPeaklist().contains(a_peak) )
			return -1;
		if ( !this.m_mapPeakToSubScanNo.containsKey(a_peak) )
			return -1;
		return this.m_mapPeakToSubScanNo.get(a_peak);
	}
*/
	/**
	 * Sets shift value of m/z
	 * @param a_dShift double value of m/z shift
	 */
	public void setShift(double a_dShift) {
		this.m_dShift = a_dShift;
	}

	/**
	 * Sets accuracy value and the type (in ppm or not) for precursor peak
	 * @param a_dAccuracy double value of the accuracy
	 * @param a_bIsPPM true if the accuracy type is PPM
	 */
	public void setAccuracy(double a_dAccuracy, boolean a_bIsPPM) {
		this.m_dAccuracy = new BigDecimal(a_dAccuracy);
		this.m_bAccuracyPPM = a_bIsPPM;
	}

	/**
	 * Sets accuracy value and the type (in ppm or not) for fragment peak
	 * @param a_dFragAccuracy double value of the accuracy
	 * @param a_bIsPPM true if the accuracy type is PPM
	 */
	public void setFragmentAccuracy(double a_dFragAccuracy, boolean a_bIsPPM) {
		this.m_dFragAccuracy = new BigDecimal(a_dFragAccuracy);
		this.m_bFragAccuracyPPM = a_bIsPPM;
	}

	/**
	 * Sets intensity cutoff value and cutoff type ("Absolute" or "Percentage")
	 * @param a_dCutoff double value of cutoff
	 * @param a_strCutoffType String of cutoff type
	 */
	public void setIntencityCutoff(double a_dCutoff, String a_strCutoffType) {
		if ( !IMSFileReader.FILTER_ABSOLUTE.equals(a_strCutoffType) && !IMSFileReader.FILTER_PERCENTAGE.equals(a_strCutoffType) )
			return;
		// Set filtered peaks
		this.m_lFilteredPeaks = new ArrayList<>();
		for ( Peak t_peak : this.m_scan.getPeaklist() ) {
			if ( t_peak.equals(this.m_scan.getPrecursor()) )
				continue;
			if ( IMSFileReader.FILTER_ABSOLUTE.equals(a_strCutoffType) && t_peak.getIntensity() < a_dCutoff )
				continue;
			if ( IMSFileReader.FILTER_PERCENTAGE.equals(a_strCutoffType) && t_peak.getRelativeIntensity() * 100 < a_dCutoff )
				continue;
			this.m_lFilteredPeaks.add(t_peak);
		}
	}

	private void setPeakMzRange() {
		double t_dMin = Double.MAX_VALUE;
		double t_dMax = Double.MIN_VALUE;
		for ( Peak t_peak : this.m_scan.getPeaklist() ) {
			// Ignore precursor
			if ( t_peak.equals(this.m_scan.getPrecursor()) )
				continue;
			if ( t_peak.getMz() + this.m_dShift > t_dMax )
				t_dMax = t_peak.getMz() + this.m_dShift;
			if ( t_peak.getMz() + this.m_dShift < t_dMin )
				t_dMin = t_peak.getMz() + this.m_dShift;
		}
		this.m_bdMinPeakMz = new BigDecimal(t_dMin);
		this.m_bdMaxPeakMz = new BigDecimal(t_dMax);
	}

	/**
	 * Checks whether the precursor peak of this scan is matched to the specified m/z value
	 * @param a_bdMz BigDecimal of m/z value
	 * @return True if the precursor is matched to the specified m/z value
	 */
	public boolean isMatchedToPrecursor(BigDecimal a_bdMz) {
		// Calculate accuracy
		BigDecimal t_bdAcc = this.calculateAccuracy(a_bdMz, this.m_dAccuracy, this.m_bAccuracyPPM);
		Peak t_peakPrecursor = this.m_scan.getPrecursor();
		// Use original peak m/z value if existed
		if ( t_peakPrecursor.getPrecursorMz() != null && t_peakPrecursor.getPrecursorMz() > 0.0d ) {
			t_peakPrecursor = new Peak();
			t_peakPrecursor.setMz(this.m_scan.getPrecursor().getPrecursorMz());
			t_peakPrecursor.setIntensity(this.m_scan.getPrecursor().getPrecursorIntensity());
		}
		return this.isMatchedPeak(t_peakPrecursor, a_bdMz, t_bdAcc);
	}

	/**
	 * Gets peaks filtered by intensity cutoff
	 * @return List of filtered Peaks
	 */
	public List<Peak> getFilteredPeaks() {
		return this.m_lFilteredPeaks;
	}

	/**
	 * @param a_bdMz BigDecimal of m/z value to be matched to peak
	 * @return Matched Peak (null if no peak matched)
	 * @see #getMatchedPeak(BigDecimal, List)
	 */
	public Peak getMatchedPeak(BigDecimal a_bdMz) {
		return this.getMatchedPeak(a_bdMz, this.m_lFilteredPeaks);
	}

	/**
	 * Gets matched peak for key fragment within peaks having 5% or higher relative intensity
	 * @param a_bdMz BigDecimal of m/z value to be matched to peak for the key fragment
	 * @return Matched Peak (null if no peak matched)
	 * @see #getMatchedPeak(BigDecimal, List)
	 */
	public Peak getMatchedPeakForKeyFragment(BigDecimal a_bdMz) {
		return this.getMatchedPeak(a_bdMz, this.m_lFilteredPeaksForKeyFragments);
	}

	/**
	 * Gets a peak having maximum intensity within the all matched peaks to the specified m/z value
	 * @param a_bdMz BigDecimal of m/z value
	 * @param a_lFilteredPeaks List of Peaks filtered by intensity cutoff
	 * @return Matched Peak having maximum intensity (null if no peak matched)
	 */
	private Peak getMatchedPeak(BigDecimal a_bdMz, List<Peak> a_lFilteredPeaks) {
		// Calculate fragment accuracy
		BigDecimal t_bdAcc = this.calculateAccuracy(a_bdMz, this.m_dFragAccuracy, this.m_bFragAccuracyPPM);

		// Return null if out of range
		if ( this.isOutOfRange(a_bdMz, t_bdAcc) )
			return null;

		// Filter by intensity cutoff
		int t_nPeaks = a_lFilteredPeaks.size();
		int t_iFirstPeakID = -1;
//		int t_iLastPeakID = -1;
//		int t_iMaxIntensityID = -1;
		// Get first matching peak
		Peak t_peak = null;
		int t_nSkip = t_nPeaks;
		BigDecimal t_bdMinMz = a_bdMz.subtract(t_bdAcc);
		while ( t_nSkip > 10 ) {
			t_nSkip /= 10;
			int t_iPrePeakID = 0;
			boolean t_bIsMatched = false;
			for ( int i=t_iFirstPeakID+t_nSkip; i<t_nPeaks; i+=t_nSkip ) {
				t_iPrePeakID = i;
				t_peak = a_lFilteredPeaks.get(i);
				// Skip if peak m/z > calculated m/z - accuracy
				if ( this.calculateDifference(t_peak, t_bdMinMz).compareTo(BigDecimal.ZERO) < 0 )
					continue;
				t_bIsMatched = true;
				t_iFirstPeakID = i-t_nSkip;
				break;
			}
			if ( !t_bIsMatched )
				t_iFirstPeakID = t_iPrePeakID;
		}
		BigDecimal t_bdMaxMz = a_bdMz.add(t_bdAcc);
		boolean t_bIsMatched = false;
		for ( int i=t_iFirstPeakID; i<t_nPeaks; i++ ) {
			if ( i<0 ) continue;
			t_peak = a_lFilteredPeaks.get(i);
			// Return null if out of range (peak m/z > calculated m/z + accuracy)
			if ( this.calculateDifference(t_peak, t_bdMaxMz).compareTo(BigDecimal.ZERO) > 0 )
				return null;
			// Continue if not match
			if ( this.calculateDifference(t_peak, t_bdMinMz).compareTo(BigDecimal.ZERO) < 0 )
				continue;
			t_bIsMatched = true;
			t_iFirstPeakID = i;
			break;
		}
		if ( !t_bIsMatched )
			return null;

		// Choose a peak having maximum intensity within matched peaks
		Peak t_peakMax = t_peak;
		for ( int i=t_iFirstPeakID+1; i<t_nPeaks; i++ ) {
			t_peak = a_lFilteredPeaks.get(i);
			if( t_peak == null || t_peak.getMz() == null || t_peak.getIntensity() == null)
				continue;

			// Break if out of range (peak m/z > calculated m/z + accuracy)
			if ( this.calculateDifference(t_peak, t_bdMaxMz).compareTo(BigDecimal.ZERO) > 0 )
				break;
			if ( t_peakMax.getIntensity() < t_peak.getIntensity() )
				t_peakMax = t_peak;
//			t_iLastPeakID = i;
		}
//		t_iMaxIntensityID = t_peakMax.getId();

		return t_peakMax;
	}
/*
	private int findMatchedMinimumPeakID(BigDecimal a_bdMinMz, List<Peak> a_lPeaks) {
		int t_iHalf = a_lPeaks.size()/2;
		int t_iCenter = t_iHalf;
		BigDecimal t_bdMinDeff = new BigDecimal(Double.MAX_VALUE);
		while (t_iHalf > 0) {
			Peak t_peak = a_lPeaks.get(t_iCenter);
			BigDecimal t_bdDeff = this.calculateDifference(t_peak, a_bdMinMz);
			if ( t_bdDeff.compareTo(BigDecimal.ZERO) > 0 && t_bdMinDeff.compareTo(t_bdDeff) > 0 )
				t_bdMinDeff = t_bdDeff;

			t_iHalf /= 2;
			t_iCenter += ( t_bdDeff.compareTo(BigDecimal.ZERO) > 0 )? t_iHalf : -t_iHalf;
		}

	}
*/
	/**
	 * Returns true if the specified m/z value is out of range in this scan.
	 * @param a_bdMz BigDecimal of m/z value
	 * @param a_bdAcc BigDecimal of accuracy
	 * @return true if the specified m/z is out of range
	 */
	private boolean isOutOfRange(BigDecimal a_bdMz, BigDecimal a_bdAcc) {
		// Min peak > m/z value + accuracy
		if ( this.m_bdMinPeakMz.compareTo(a_bdMz.add(a_bdAcc)) > 0 )
			return true;
		// Max peak < m/z value - accuracy
		if ( this.m_bdMaxPeakMz.compareTo(a_bdMz.subtract(a_bdAcc)) < 0 )
			return true;
		return false;
	}

	/**
	 * Calculates accuracy in m/z value if the accuracy value is specified in PPM
	 * @param a_bdMz BigDecimal of m/z value for using accuracy calculation (only for PPM)
	 * @param a_bdAcc BigDecimal of accuracy (the value depends on the accuracy unit)
	 * @param a_bIsPPM Is accuracy unit PPM or not
	 * @return BigDecimal of accuracy in m/z value
	 */
	private BigDecimal calculateAccuracy(BigDecimal a_bdMz, BigDecimal a_bdAcc, boolean a_bIsPPM) {
		// Calculate accuracy
		BigDecimal t_bdAcc = a_bdAcc;
		if ( a_bIsPPM )
			t_bdAcc = a_bdMz.multiply(a_bdAcc).divide( new BigDecimal(1000000) );

		return t_bdAcc;
	}

	private boolean isMatchedPeak(Peak a_peak, BigDecimal a_bdMz, BigDecimal a_bdAcc) {
		if( a_peak == null || a_peak.getMz() == null || a_peak.getIntensity() == null)
			return false;

		// abs(peakMz - analyteMz) >= accuracy
		return ( this.calculateDifference(a_peak, a_bdMz).abs().compareTo(a_bdAcc) < 0 );
	}

	private BigDecimal calculateDifference(Peak a_peak, BigDecimal a_bdMz) {
		// Calculate the shift if there is any.
		BigDecimal t_bdPeakMz = new BigDecimal( a_peak.getMz() + this.m_dShift );
		return t_bdPeakMz.subtract(a_bdMz);
	}

	public BigDecimal calculatePrecursorDeviation(BigDecimal a_bdMz) {
		return this.calculateDeviation(this.m_scan.getPrecursor(), a_bdMz);
	}

	public BigDecimal calculateDeviation(Peak a_peak, BigDecimal a_bdMz) {
		// PPM: (Math.abs(dPeakMz - glycanMz)/glycanMz)*1000000.0
		return this.calculateDifference(a_peak, a_bdMz).abs()
				.divide(a_bdMz, 10, BigDecimal.ROUND_HALF_UP)
				.multiply(new BigDecimal(1000000.0));
	}

}
