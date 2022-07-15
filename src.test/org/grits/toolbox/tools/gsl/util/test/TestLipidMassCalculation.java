package org.grits.toolbox.tools.gsl.util.test;

import java.util.List;

import org.grits.toolbox.tools.gsl.structure.lipid.Ceramide;
import org.grits.toolbox.tools.gsl.structure.lipid.FattyAcid;
import org.grits.toolbox.tools.gsl.structure.lipid.Sphingosine;
import org.grits.toolbox.tools.gsl.util.mass.AtomicMass;
import org.grits.toolbox.tools.gsl.util.mass.CeramideChemicalComposition;
import org.grits.toolbox.tools.gsl.util.mass.IsotopicDistributionCalculator;
import org.grits.toolbox.tools.gsl.util.mass.QuantumMass;

public class TestLipidMassCalculation {

	public static void main(String[] args) {
		Sphingosine t_oSph = new Sphingosine(18, 2, 0, 1);
		FattyAcid t_oFA = new FattyAcid(200, 0, 0, 0);
		Ceramide t_oCer = new Ceramide(t_oSph, t_oFA);

		CeramideChemicalComposition t_oCerChemComp = new CeramideChemicalComposition(t_oCer);
		t_oCerChemComp.derivatize();
		// Ionize (M + H+)
		t_oCerChemComp.addNumberOfElements(AtomicMass.H, 1);
		t_oCerChemComp.addNumberOfElements(QuantumMass.e, -1);

		System.out.println( t_oCer.getCoreName()+" : "+t_oCerChemComp.getFormula());
		System.out.println( "Mono:\t"+ t_oCerChemComp.getMonoisotopicMass() );

		IsotopicDistributionCalculator t_calcIso = new IsotopicDistributionCalculator(t_oCerChemComp);
		t_calcIso.setDecimalScale(3);
		t_calcIso.setMinimumAbundance(0.001);
		t_calcIso.calculate();
		System.out.println( "Ave:\t"+ t_calcIso.getAverageMass() );
		List<String> t_listMasses = t_calcIso.getMasses();
		List<String> t_listIntensities = t_calcIso.getIntensities();
		List<String> t_listScaledIntensities = t_calcIso.getScaledIntensities();
		for ( int i=0; i<t_listMasses.size(); i++ )
			System.out.println( t_listMasses.get(i)+"\t"+t_listIntensities.get(i)+"\t"+t_listScaledIntensities.get(i) );
	}

}
