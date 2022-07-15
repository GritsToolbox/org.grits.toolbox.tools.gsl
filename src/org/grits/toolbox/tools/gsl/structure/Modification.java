package org.grits.toolbox.tools.gsl.structure;

/**
 * Class for modification including substituents
 * TODO: TBD
 * @author Masaaki Matsubara
 *
 */
public class Modification {

	private ModificationType m_enumModType;
	private int m_iPosition;
	private char m_cStereo;

	public Modification(ModificationType a_enumModType, int a_iPos, char m_cStereo) {
		this.m_enumModType = a_enumModType;
		this.m_iPosition = a_iPos;
		this.m_cStereo = m_cStereo;
	}

	public ModificationType getType() {
		return this.m_enumModType;
	}

	public int getPosition() {
		return this.m_iPosition;
	}

	public char getStereo() {
		return this.m_cStereo;
	}
}
