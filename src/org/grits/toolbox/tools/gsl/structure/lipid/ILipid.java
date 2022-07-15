package org.grits.toolbox.tools.gsl.structure.lipid;

import java.util.List;

/**
 * Interface for Lipid object model
 * @author Masaaki Matsubara
 *
 */
public interface ILipid {

	/**
	 * Returns carbon length.
	 * @return Number of carbon length
	 */
	public int getCarbonLength();
	/**
	 * Returns number of hydroxyl groups.
	 * @return Number of hydroxyl groups
	 */
	public int getNumberOfHydroxylGroups();
	/**
	 * Returns number of O-acetyl groups.
	 * @return Number of O-acetyl groups
	 */
	public int getNumberOfOAcetylGroups();
	/**
	 * Returns number of double bonds.
	 * @return Number of double bonds
	 */
	public int getNumberOfDoubleBonds();
	/**
	 * Returns core name of lipid similar to LIPID MAPS abbreviation.
	 * Doesn't contain type of the lipid
	 * (carbon length, number of double bonds and number of hydroxyl group are contained).
	 * @return String of lipid core name
	 */
	public String getCoreName();
	/**
	 * Returns name of lipid similar to LIPID MAS abbreviation. The name has the type of lipid (e.g. Cer, Sph or FA) with core name.
	 * @see #getCoreName()
	 * @return String of lipid name
	 */
	public String getName();
	/**
	 * Returns composition name of lipid similar to LIPID MAS abbreviation.
	 * The name is as a single chain lipid even if the lipid has two or more components.
	 * @see #getCoreName()
	 * @return String of lipid composition name
	 */
	public String getCompositionName();
	/**
	 * Returns true if the lipid has substructure(s).
	 * @return True if the lipid has substructures(s)
	 */
	public boolean hasSubstructure();
	/**
	 * Returns substructure names of this lipid.
	 * @return List of String of substructure names
	 */
	public List<String> getSubstructureNames();
}
