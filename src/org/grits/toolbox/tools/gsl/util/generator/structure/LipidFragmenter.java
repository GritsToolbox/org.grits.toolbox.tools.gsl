package org.grits.toolbox.tools.gsl.util.generator.structure;

import java.util.ArrayList;
import java.util.List;

import org.grits.toolbox.tools.gsl.structure.LipidFragmentInfo;
import org.grits.toolbox.tools.gsl.structure.lipid.Ceramide;
import org.grits.toolbox.tools.gsl.structure.lipid.FattyAcid;
import org.grits.toolbox.tools.gsl.structure.lipid.ILipid;
import org.grits.toolbox.tools.gsl.structure.lipid.Lipid;
import org.grits.toolbox.tools.gsl.structure.lipid.Sphingomyelin;
import org.grits.toolbox.tools.gsl.structure.lipid.Sphingosine;

/**
 * Class for generating lipid fragment.
 * Generates List of LipidFragmentInfo objects.
 * @author Masaaki Matsubara
 * @see LipidFragmentInfo
 *
 */
public class LipidFragmenter {

	private int m_nH2OLossLimit;

	/**
	 * Constructor without H2O loss limit.
	 */
	public LipidFragmenter() {
		this.m_nH2OLossLimit = -1;
	}

	/**
	 * Constructor with H2O loss limit.
	 * @param a_nLimit number of H2O loss limit
	 */
	public void setWaterLossLimit(int a_nLimit) {
		this.m_nH2OLossLimit = a_nLimit;
	}

	/**
	 * Generate fragments of the given lipid ILipid. The given lipid will be fragmented according to its instance.
	 * @param a_iLip ILipid to be fragmented
	 * @return List of LipidFragmentInfo containing instance of lipid fragment and its fragmentation type(s).
	 */
	public List<LipidFragmentInfo> fragment(ILipid a_iLip) {
		if ( this.m_nH2OLossLimit < 0 )
			this.m_nH2OLossLimit = a_iLip.getNumberOfHydroxylGroups();

		// For Sphingomyelin
		if ( a_iLip instanceof Sphingomyelin )
			return this.generateFragments((Sphingomyelin)a_iLip, this.m_nH2OLossLimit);

		// For Ceramide
		if ( a_iLip instanceof Ceramide )
			return this.generateFragments((Ceramide)a_iLip, this.m_nH2OLossLimit);

		// For Sphingosine
		if ( a_iLip instanceof Sphingosine )
			return this.generateFragments((Sphingosine)a_iLip, this.m_nH2OLossLimit);

		// For the other lipids
		if ( a_iLip instanceof Lipid )
			return this.generateFragments((Lipid)a_iLip, this.m_nH2OLossLimit);

		return null;
	}

	/**
	 * Fragmenter for Sphingomyelins.
	 * @param a_sm Sphingomyelin to be fragmented
	 * @param a_nLimit number of H2O loss limit
	 * @return List of LipidFragmentInfos containing the given Sphingomyelin fragment information
	 */
	private List<LipidFragmentInfo> generateFragments(Sphingomyelin a_sm, int a_nLimit) {
		List<LipidFragmentInfo> t_listFragments = new ArrayList<>();
		// Add itself
		LipidFragmentInfo t_fragInfoOrig = new LipidFragmentInfo(a_sm);
		t_listFragments.add(t_fragInfoOrig);

		// Create a ceramide lost a hydroxyl group and got a double bond on sphingosine base
		Sphingosine t_sp = a_sm.getSphingosine();
		t_sp = new Sphingosine(
				t_sp.getCarbonLength(),
				t_sp.getNumberOfHydroxylGroups() - 1,
				t_sp.getNumberOfOAcetylGroups(),
				t_sp.getNumberOfDoubleBonds() + 1
			);
		Ceramide t_cer = new Ceramide( t_sp, a_sm.getFattyAcid() );
		for ( LipidFragmentInfo t_fragInfo : this.generateFragments(t_cer, a_nLimit) ) {
			t_fragInfo.addFragmentType("Y", 1);
			t_listFragments.add( t_fragInfo );
		}

		return t_listFragments;
	}

	/**
	 * Fragmenter for Ceramides.
	 * @param a_cer Ceramide to be fragmented
	 * @param a_nLimit number of H2O loss limit
	 * @return List of LipidFragmentInfos containing the given Ceramide fragment information
	 */
	private List<LipidFragmentInfo> generateFragments(Ceramide a_cer, int a_nLimit) {
		List<LipidFragmentInfo> t_listFragments = new ArrayList<>();
		// Add itself
		LipidFragmentInfo t_fragInfoOrig = new LipidFragmentInfo(a_cer);
//		t_fragInfoOrig.addFragmentType("Y");
		t_listFragments.add(t_fragInfoOrig);

		Sphingosine t_sp = a_cer.getSphingosine();
		FattyAcid t_fa = a_cer.getFattyAcid();

		int t_nCSp = t_sp.getCarbonLength();
		int t_nOHSp = t_sp.getNumberOfHydroxylGroups();
		int t_nOAcSp = t_sp.getNumberOfOAcetylGroups();
		int t_nDBSp = t_sp.getNumberOfDoubleBonds();
		int t_nCFA = t_fa.getCarbonLength();
		int t_nOHFA = t_fa.getNumberOfHydroxylGroups();
		int t_nOAcFA = t_fa.getNumberOfOAcetylGroups();
		int t_nDBFA = t_fa.getNumberOfDoubleBonds();

		// Generate fragments with neutral loss of waters (H2Os).
		// They are represented as lipids reduced hydroxyl groups and added double bonds.
		// Remove H2Os until the number is reached to the limit or total number of hydroxyl groups.
		for ( int i=1; i<=a_nLimit; i++ ) {
			if ( i > t_nOHSp+t_nOHFA ) break;
			// Generate new Ceramides as fragments for all of possible H2O loss patterns
			for ( int j=0; j<=i; j++ ) {
				int k = i-j;
				if ( j > t_nOHSp || k > t_nOHFA ) continue;
				Sphingosine t_spFragment = new Sphingosine(t_nCSp, t_nOHSp-j, t_nOAcSp, t_nDBSp+j);
				FattyAcid t_faFragment   = new FattyAcid(t_nCFA, t_nOHFA-k, t_nOAcFA, t_nDBFA+k);
				LipidFragmentInfo t_fragInfo = new LipidFragmentInfo(new Ceramide(t_spFragment, t_faFragment));
				t_fragInfo.addFragmentType("H2O", i);
				t_listFragments.add(t_fragInfo);
			}
		}
		// Generate new Sphingosines as fragments of sphingosine side
		for ( LipidFragmentInfo t_fragInfo : this.generateFragments(t_sp, a_nLimit) ) {
			t_fragInfo.addFragmentType("FA", 1);
			t_listFragments.add(t_fragInfo);
		}
		// Generate new Lipids as fragments of fatty acid side (carboxyl OH is changed to amine)
		Lipid t_lipFAFrag = new Lipid( 0, 0, 1, t_nCFA, t_nOHFA, t_nOAcFA, t_nDBFA );
		for ( LipidFragmentInfo t_fragInfo : this.generateFragments(t_lipFAFrag, a_nLimit) ) {
			t_fragInfo.addFragmentType("Sp", 1);
			t_listFragments.add(t_fragInfo);
		}

		return t_listFragments;
	}

	/**
	 * Fragmenter for Lipid.
	 * @param a_lip Lipid to be fragmented
	 * @param a_nLimit number of H2O loss limit
	 * @return List of LipidFragmentInfos containing the given Lipid fragment information
	 */
	private List<LipidFragmentInfo> generateFragments(Lipid a_lip, int a_nLimit) {
		List<LipidFragmentInfo> t_listFragments = new ArrayList<>();
		// Add itself
		LipidFragmentInfo t_fragInfoOrig = new LipidFragmentInfo(a_lip);
		t_listFragments.add(t_fragInfoOrig);

		int t_nNH2 = a_lip.getNumberOfAmine();
		int t_nCOOH = a_lip.getNumberOfCarboxyl();
		int t_nCONH2 = a_lip.getNumberOfCarbamoyl();
		int t_nC = a_lip.getCarbonLength();
		int t_nOH = a_lip.getNumberOfHydroxylGroups();
		int t_nOAc = a_lip.getNumberOfOAcetylGroups();
		int t_nDB = a_lip.getNumberOfDoubleBonds();

		// Generate lipid fragments as lipids removed OHs and added double bond
		for ( int i=1; i<=t_nOH; i++ ) {
			if ( i > a_nLimit ) continue;
			if ( i > t_nOH ) continue;
			Lipid t_lip = new Lipid(t_nCOOH, t_nNH2, t_nCONH2, t_nC, t_nOH-i, t_nOAc, t_nDB+i);
			if ( a_lip instanceof Sphingosine )
				t_lip = new Sphingosine(t_nC, t_nOH-i, t_nOAc, t_nDB+i);
			if ( a_lip instanceof FattyAcid )
				t_lip = new FattyAcid(t_nC, t_nOH-i, t_nOAc, t_nDB+i);
			LipidFragmentInfo t_fragInfo = new LipidFragmentInfo(t_lip);
			t_fragInfo.addFragmentType("H2O", i);
			t_listFragments.add(t_fragInfo);
		}
		return t_listFragments;
	}
}
