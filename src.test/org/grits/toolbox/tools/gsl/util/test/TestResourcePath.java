package org.grits.toolbox.tools.gsl.util.test;

public class TestResourcePath {

	public static final String RESOURCE_DIR = "src.test.resource/";
	public static final String METHOD_PATH = RESOURCE_DIR+"method.xml";
	public static final String METHOD_GGMIX_PATH = RESOURCE_DIR+"method_GgMix.xml";
	public static final String METHOD_GGMIX_HIGHACC_PATH = RESOURCE_DIR+"method_GgMix_HighAcc.xml";
	public static final String METHOD_GGMIX_LOWACC_PATH = RESOURCE_DIR+"method_GgMix_LowAcc.xml";
//	public static final String MZXML_PATH = RESOURCE_DIR+"MS2 - AlphaGalCerPermeMSMS978_45.mzXML";
	public static final String MZXML1 = "AlphaGalCerPermeMSMS978_45_Centroid.mzXML";
	public static final String MZXML1_PATH = RESOURCE_DIR+MZXML1;
	public static final String MZXML2 = "KA-mEXE-acidic-GSL-NL-Cer.mzML";
	public static final String MZXML2_PATH = RESOURCE_DIR+MZXML2;
	public static final String MZXML_GGMIX_HCD = "Ganglioside mixture Topdown_FT_HCD_MIPS.mzXML";
	public static final String MZXML_GGMIX_HCD_PATH = RESOURCE_DIR+MZXML_GGMIX_HCD;
	public static final String MZXML_GGMIX_CID = "Ganglioside mixture Topdown_FT_CID_MIPS.mzXML";
	public static final String MZXML_GGMIX_CID_PATH = RESOURCE_DIR+MZXML_GGMIX_CID;
	public static final String MZXML_SERUMGSL = "Serum_GSL__both_with unassigned_FT_160504104943.mzXML";
	public static final String MZXML_SERUMGSL_PATH = RESOURCE_DIR+MZXML_SERUMGSL;

	public static String getResourcePath(String a_strFilename) {
		return RESOURCE_DIR+a_strFilename;
	}
}
