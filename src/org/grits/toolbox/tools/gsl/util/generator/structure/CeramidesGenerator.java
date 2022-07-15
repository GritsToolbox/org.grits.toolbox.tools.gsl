package org.grits.toolbox.tools.gsl.util.generator.structure;

import org.grits.toolbox.tools.gsl.database.lipid.LipidClass;
import org.grits.toolbox.tools.gsl.structure.lipid.Ceramide;
import org.grits.toolbox.tools.gsl.structure.lipid.FattyAcid;
import org.grits.toolbox.tools.gsl.structure.lipid.Lipid;
import org.grits.toolbox.tools.gsl.structure.lipid.Sphingosine;
import org.grits.toolbox.tools.gsl.util.generator.LipidParametersIO;

/**
 * Class for generating possible ceramides from settings of Sphingosine and FattyAcid.
 * @author Masaaki Matsubara
 *
 */
public class CeramidesGenerator extends LipidsGeneratorAbstract {

	private LipidParametersIO m_paramSph;
	private LipidParametersIO m_paramFA;

	public CeramidesGenerator() {
		this.m_paramSph = new LipidParametersIO();
		this.m_paramSph.setLipidClass(LipidClass.SPHINGOSINE);
		this.m_paramFA = new LipidParametersIO();
		this.m_paramFA.setLipidClass(LipidClass.FATTY_ACID);
	}

	public void setParametersForSphingosine(LipidParametersIO a_params) {
		this.m_paramSph = a_params;
	}

	public LipidParametersIO getParametersForSphingosine() {
		return this.m_paramSph;
	}

	public void setParametersForFattyAcid(LipidParametersIO a_params) {
		this.m_paramFA = a_params;
	}

	public LipidParametersIO getParametersForFattyAcid() {
		return this.m_paramFA;
	}

	/*
	 * (non-Javadoc)
	 * @see org.grits.toolbox.tools.gsl.util.generator.structure.LipidsGeneratorAbstract#generate()
	 */
	@Override
	public void generate() {
		SphingosinesGenerator t_genSph = new SphingosinesGenerator();
		t_genSph.setParameters(this.m_paramSph);
		t_genSph.generate();

		FattyAcidsGenerator t_genFA = new FattyAcidsGenerator();
		t_genFA.setParameters(m_paramFA);
		t_genFA.generate();

		// Generate ceramide for each combination of Sphingosines and FattyAcids
		for ( Sphingosine t_oSph : t_genSph.getSphingosines() ) {
			for ( FattyAcid t_oFA : t_genFA.getFattyAcids() ) {
				// Check skip
				if ( !this.canGenerate(t_oSph, t_oFA) ) continue;
				Ceramide t_oCer = this.getNewLipid(t_oSph, t_oFA);
				this.addLipid(t_oCer);
			}
		}
	}

	/**
	 * Gets new lipid from the given Sphingosine and FattyAcid.
	 * @param a_oSph Sphingosine to be composed to the Ceramide
	 * @param a_oFA FattyAcid to be composed to the Ceramide
	 * @return
	 */
	protected Ceramide getNewLipid(Sphingosine a_oSph, FattyAcid a_oFA) {
		return new Ceramide( a_oSph, a_oFA );
	}

	/**
	 * Returns true if a lipid can be generated from the given Sphingosine and FattyAcid
	 * @param a_oSph Sphingosine to be checked
	 * @param a_oFA FattyAcid to be checked
	 * @return true if ceramide can be generated
	 */
	protected boolean canGenerate(Sphingosine a_oSph, FattyAcid a_oFA) {
		// Always can generate for now
		return true;
	}

	@Override
	public String printParameters() {
		String t_strParams = "";
		t_strParams += this.m_paramSph.printParameters();
		t_strParams += this.m_paramFA.printParameters();
		return t_strParams;
	}

	@Override
	protected Lipid generateLipid(int a_nC, int a_nOH, int a_nOAc, int a_nDB) {
		// Do not generate any lipid
		return null;
	}
}
