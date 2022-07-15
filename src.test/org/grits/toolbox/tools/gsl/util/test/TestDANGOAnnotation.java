package org.grits.toolbox.tools.gsl.util.test;

import org.grits.toolbox.ms.file.FileCategory;
import org.grits.toolbox.ms.file.MSFile;
import org.grits.toolbox.ms.file.reader.impl.MzXmlReader;
import org.grits.toolbox.ms.om.data.Data;
import org.grits.toolbox.ms.om.data.DataHeader;
import org.grits.toolbox.ms.om.data.Method;
import org.grits.toolbox.tools.gsl.dango.DANGOAnnotation;
import org.grits.toolbox.tools.gsl.util.io.om.MSDataModelFileHandlar;

public class TestDANGOAnnotation {

	public static void main(String[] args) {
		String t_strMethodFileFullpath = TestResourcePath.METHOD_PATH;
//		String t_strMethodFilepath = TestResourcePath.METHOD_GGMIX_HIGHACC_PATH;
		String t_strScanFilename = TestResourcePath.MZXML2;
//		String t_strScanFilename = TestResourcePath.MZXML_GGMIX_CID;
//		String t_strScanFilename = TestResourcePath.MZXML_GGMIX_HCD;
		String t_strScanFileFullpath = TestResourcePath.getResourcePath(t_strScanFilename);

		String t_strZipFilepath = TestResourcePath.getResourcePath("archive.zip");
		Data t_data = createData(t_strMethodFileFullpath);
		MSFile t_msFile = getMSFileWithReader(t_strScanFileFullpath);
		DANGOAnnotation t_DANGO = new DANGOAnnotation(t_data, t_msFile, t_strZipFilepath);
		t_DANGO.setIntactGlycanAnnotation(true);
		t_DANGO.setFilterByKeyFragment(true);
		t_DANGO.initialize();
		t_DANGO.processAnnotation();
		t_DANGO.archiveData();

	}

	private static Data createData(String a_strMethodFile) {
		// Data is part of GRITS object model. It is top-level object for linking MS data to annotation information	
		Data data = new Data();
		// DataHeader, also part of object Model, tracks meta-data, in particular the CustomExtraData that are associated with a project
		DataHeader t_dataHeader = new DataHeader();
		// Store Method in the DataHeader
		t_dataHeader.setMethod( MSDataModelFileHandlar.readMethodXML(a_strMethodFile) );
		// Store the DataHeader in the Data object
		data.setDataHeader(t_dataHeader);
		return data;
	}

	private static MSFile getMSFileWithReader(String a_strScanFile) {
		MSFile msFile = new MSFile();
//		msFile.setFileName(pathToFile + File.separator + getName());
		msFile.setFileName(a_strScanFile);
		msFile.setExperimentType(Method.MS_TYPE_INFUSION);
		msFile.setCategory(FileCategory.ANNOTATION_CATEGORY);
		msFile.setVersion("1.0");
		msFile.setReader(new MzXmlReader());  
		return msFile;
	}
}
