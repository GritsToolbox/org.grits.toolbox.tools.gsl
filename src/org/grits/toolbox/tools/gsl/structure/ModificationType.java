package org.grits.toolbox.tools.gsl.structure;

/**
 * Definition of modifications including substituents and double bond
 * TODO: TBD
 * @author Masaaki Matsubara
 *
 */
public enum ModificationType {

	DB   ( "DB"   , "double bond", ""     , 2 ),
	TB   ( "TB"   , "triple bond", ""     , 3 ),
	OH   ( "OH"   , "hydroxy"    , "OH"   , 1 ),
	NH   ( "NH2"  , "amino"      , "NH2"  , 1 ),
	Me   ( "Me"   , "methyl"     , "CH3"  , 1 ),
	Ke   ( "Ke"   , "keto/oxo"   , "O"    , 2 ),
	Ep   ( "Ep"   , "epoxy"      , "O"    , 2 ),
	SH   ( "SH"   , "thio"       , "HS"   , 1 ),
	My   ( "My"   , "methylene"  , "CH2"  , 2 ),
//	Br   ( "Br"   , "bromo"      , "Br"   , 1 ),
//	Cl   ( "Cl"   , "chloro"     , "Cl"   , 1 ),
//	F    ( "F"    , "fluoro"     , "F"    , 1 ),
	CN   ( "CN"   , "cyano"      , "CN"   , 1 ),
	COOH ( "COOH" , "carboxyl"   , "CHO2" , 2 ),
	CONH2( "CONH2", "carbamoyl"  , "CH2NO", 1 );

	private String m_strLabel;
	private String m_strName;
	private String m_strMol;
	private int m_nValence;

	private ModificationType(String a_strLabel, String a_strName, String a_strMolAddition, int a_nValence) {
		this.m_strLabel = a_strLabel;
		this.m_strName = a_strName;
		this.m_strMol = a_strMolAddition;
		this.m_nValence = a_nValence;
	}

	public String getLabel() {
		return this.m_strLabel;
	}

	public String getName() {
		return this.m_strName;
	}

	public String getMolecularFomula() {
		return this.m_strMol;
	}

	public int getNumberOfValence() {
		return this.m_nValence;
	}

	public static ModificationType forLabel(String a_strLabel) {
		for ( ModificationType t_modType : ModificationType.values() ) {
			if ( t_modType.getLabel().equals(a_strLabel) )
				return t_modType;
		}
		return null;
	}
}
