package org.grits.toolbox.tools.gsl.util.mass;

/**
 * Interface for enum classes defining element and mass information.
 * @author Masaaki Matsubara
 *
 */
public interface IMass {

	/**
	 * Get element symbol.
	 * @return String of symbol
	 */
	public String getSymbol();
	/**
	 * Get exact (monoisotopic) mass of the element.
	 * @return String of exact mass
	 */
	public String getExactMass();
}
