package org.grits.toolbox.tools.gsl.util.scan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.grits.toolbox.ms.om.data.Peak;
import org.grits.toolbox.ms.om.data.Scan;

public class MSProfileToCentroid {

	private Scan m_scan;

	private double m_dMinPeakDistance;
	private HashMap<Peak, Peak> m_mapPickedToCentroid;

	
	public MSProfileToCentroid() {
		this.m_dMinPeakDistance = 0.02D;
		this.m_mapPickedToCentroid = new HashMap<>();
	}

	public void setMinPeakDistance(double a_dDist) {
		this.m_dMinPeakDistance = a_dDist;
	}

	public HashMap<Peak, Peak> getPickedPeakToCentroidPeak() {
		return this.m_mapPickedToCentroid;
	}

	public Scan convert(Scan a_scan) {
		this.m_scan = a_scan;
		// Pick peaks
		List<Peak> t_lPickedPeaks = this.pickPeaks();
		Map<Peak, List<Peak>> t_mapPickedPeakToGroup = this.groupPeaks(t_lPickedPeaks);

		// Create Scan object for centroid peaks
		Scan t_scanCentroid = new Scan();
		t_scanCentroid.setActivationMethode(this.m_scan.getActivationMethode());
		t_scanCentroid.setMsLevel(this.m_scan.getMsLevel());
		t_scanCentroid.setParentScan(this.m_scan.getParentScan());
		t_scanCentroid.setPolarity(this.m_scan.getPolarity());
		t_scanCentroid.setRetentionTime(this.m_scan.getRetentionTime());
		t_scanCentroid.setScanNo(this.m_scan.getScanNo());
		for ( Integer t_iSub : this.m_scan.getSubScans() )
			t_scanCentroid.getSubScans().add(t_iSub);
		// Recalculate values
		double t_dStartMz = Double.MAX_VALUE;
		double t_dEndMz = Double.MIN_VALUE;
		double t_dMostAbandunt = 0.0D;
		double t_dTotalIntensity = 0.0D;
		int i=1;
		for ( Peak t_peakPicked : t_lPickedPeaks ) {
			Peak t_peakCentroid = this.getCentroidPeak(t_mapPickedPeakToGroup.get(t_peakPicked), i++);

			// Map picked peak to centroid peak
			this.m_mapPickedToCentroid.put(t_peakPicked, t_peakCentroid);

			t_scanCentroid.getPeaklist().add(t_peakCentroid);
			t_dTotalIntensity += t_peakCentroid.getIntensity();
			if ( t_dStartMz > t_peakCentroid.getMz() )
				t_dStartMz = t_peakCentroid.getMz();
			if ( t_dEndMz < t_peakCentroid.getMz() )
				t_dEndMz = t_peakCentroid.getMz();
			if ( t_dMostAbandunt < t_peakCentroid.getIntensity() )
				t_dMostAbandunt = t_peakCentroid.getIntensity();
		}
		t_scanCentroid.setMostAbundantPeak(t_dMostAbandunt);
		t_scanCentroid.setScanStart(t_dStartMz);
		t_scanCentroid.setScanEnd(t_dEndMz);
		t_scanCentroid.setTotalIntensity(t_dTotalIntensity);
		t_scanCentroid.setTotalNumPeaks(t_lPickedPeaks.size());
		// Set relative intensity to centroid peaks
		for ( Peak t_peakCentroid : t_scanCentroid.getPeaklist() )
			t_peakCentroid.setRelativeIntensity(t_peakCentroid.getIntensity()/t_dMostAbandunt);

		return t_scanCentroid;
	}

	private List<Peak> pickPeaks() {
		List<Peak> t_lPickedPeaks = new ArrayList<>();

		List<Peak> t_lPeaks = this.m_scan.getPeaklist();
		int t_nPeaks = t_lPeaks.size();

		// Check for head and tail
		if ( t_lPeaks.get(1).getMz() - t_lPeaks.get(0).getMz() > this.m_dMinPeakDistance
			|| t_lPeaks.get(1).getIntensity() < t_lPeaks.get(0).getIntensity() )
			t_lPickedPeaks.add(t_lPeaks.get(0));
		if ( t_lPeaks.get(t_nPeaks-1).getMz() - t_lPeaks.get(t_nPeaks-2).getMz() > this.m_dMinPeakDistance
			|| t_lPeaks.get(t_nPeaks-1).getIntensity() > t_lPeaks.get(t_nPeaks-2).getIntensity() )
			t_lPickedPeaks.add(t_lPeaks.get(t_nPeaks-1));

		// Pick peaks
		for ( int i=1; i<t_nPeaks-1; i++ ) {
			Peak t_peakCenter = this.m_scan.getPeaklist().get(i);
			Peak t_peakHead = this.m_scan.getPeaklist().get(i-1);
			Peak t_peakTail = this.m_scan.getPeaklist().get(i+1);
			if ( t_peakCenter.getMz() - t_peakHead.getMz() > this.m_dMinPeakDistance
			  && t_peakTail.getMz() - t_peakCenter.getMz() > this.m_dMinPeakDistance ) {
				t_lPickedPeaks.add(t_peakCenter);
				continue;
			}
			Peak t_peakPrev = t_lPickedPeaks.get(t_lPickedPeaks.size()-1);
			if ( t_peakCenter.getIntensity() > t_peakHead.getIntensity()
			  && t_peakCenter.getIntensity() > t_peakTail.getIntensity() ) {
				// Remove lower peak if two picked peaks are close
				if ( t_peakCenter.getMz() - t_peakPrev.getMz() < m_dMinPeakDistance ) {
					if ( t_peakCenter.getIntensity() > t_peakPrev.getIntensity() )
						t_lPickedPeaks.remove(t_peakPrev);
					else
						continue;
				}
				t_lPickedPeaks.add(t_peakCenter);
			}
		}

		return t_lPickedPeaks;
	}

	private Map<Peak, List<Peak>> groupPeaks(List<Peak> a_lPickedPeaks) {
		Map<Peak, List<Peak>> t_mapPickedPeakToGroup = new HashMap<>();

		List<Peak> t_lPrevGroup = new ArrayList<>();
		Peak t_peakPrevPicked = null;
		int t_iPrevPickedPeakID = 0;
		int t_nPeaks = this.m_scan.getPeaklist().size();
		for ( Peak t_peakPicked : a_lPickedPeaks ) {
			List<Peak> t_lGroup = new ArrayList<>();
			// Start from next peak of previous picked peak
			for (int i=t_iPrevPickedPeakID+1; i<t_nPeaks; i++) {
				Peak t_peak = this.m_scan.getPeaklist().get(i);

				// Skip if out of range from picked peak
				if ( Math.abs( t_peak.getMz() - t_peakPicked.getMz() ) > this.m_dMinPeakDistance ) {
					if ( t_lGroup.isEmpty() )
						continue;
					else
						break;
				}
				if ( t_lPrevGroup.contains(t_peak) ) {
					// Remove peaks from previous peak group if the peaks is closer to current picked peak than previous one
					if ( t_peakPicked.getIntensity() > t_peakPrevPicked.getIntensity() )
						t_lPrevGroup.remove(t_peak);
					else
						continue;
				}
				t_lGroup.add(t_peak);
			}

			// Set current group and picked peak as previous info
			t_lPrevGroup = t_lGroup;
			t_peakPrevPicked = t_peakPicked;
			t_iPrevPickedPeakID = this.m_scan.getPeaklist().indexOf(t_peakPicked);

			t_mapPickedPeakToGroup.put(t_peakPicked, t_lGroup);
		}

		return t_mapPickedPeakToGroup;
	}

	/**
	 * Create a peak having total intensity and mean m/z value of the peak group
	 * @param a_lGroup - List of peak group for centroid peak
	 * @param a_iID - Number of centroid peak id
	 * @return Peak created from the specified Peak group
	 */
	private Peak getCentroidPeak(List<Peak> a_lGroup, int a_iID) {
		Peak t_peakCentroid = new Peak();
		t_peakCentroid.setId(a_iID);

		// Set m/z and intensity as PrecursorMz and PrecursorIntensity
		double t_dTotalIntensity = 0.0D;
		for ( Peak t_peak : a_lGroup )
			t_dTotalIntensity += t_peak.getIntensity();

		double t_dMeanMz = 0.0D;
		for ( Peak t_peak : a_lGroup )
			t_dMeanMz += t_peak.getMz() * t_peak.getIntensity() / t_dTotalIntensity;

		t_peakCentroid.setPrecursorIntensity(t_dTotalIntensity);
		t_peakCentroid.setPrecursorMz(t_dMeanMz);

		return t_peakCentroid;
	}
}
