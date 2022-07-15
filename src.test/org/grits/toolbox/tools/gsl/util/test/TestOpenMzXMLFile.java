package org.grits.toolbox.tools.gsl.util.test;

import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.grits.toolbox.ms.file.FileCategory;
import org.grits.toolbox.ms.file.MSFile;
import org.grits.toolbox.ms.file.reader.IMSAnnotationFileReader;
import org.grits.toolbox.ms.file.reader.impl.MzXmlReader;
import org.grits.toolbox.ms.om.data.Method;
import org.grits.toolbox.ms.om.data.Scan;

public class TestOpenMzXMLFile {

	public static void main(String[] args) {
		Display display = new Display();
		final Shell shell = new Shell(display);

		String t_strMzXMLFile = null;
		while ( t_strMzXMLFile == null )
			t_strMzXMLFile = openFileDialog(shell);

		System.out.println(t_strMzXMLFile);
		readMzXMLFile(t_strMzXMLFile);
/*
		MSFile t_msFile = getMSFileWithReader(t_strMzXMLFile);
		getScans(t_msFile);
*/
	}

	private static String openFileDialog(Shell shell) {
		// Open file dialog
		FileDialog dlgRead = new FileDialog(shell, SWT.READ_ONLY);
		// Create file extension filters
		dlgRead.setFilterNames(new String[] { "XML database (*.mzML, *.mzXML)" });
		dlgRead.setFilterExtensions(new String[] { "*.mzML;*.mzXML" });
		return dlgRead.open();
	}

	private static List<Scan> readMzXMLFile(String a_strMzXMLFile) {
		MzXmlReader t_reader = new MzXmlReader();
		List<Scan> t_lScans = t_reader.readMzXmlFileForDirectInfusion(a_strMzXMLFile);
		return t_lScans;
	}

	private static MSFile getMSFileWithReader(String a_strScanFile) {
		MSFile msFile = new MSFile();
//		msFile.setFileName(pathToFile + File.separator + getName());
		msFile.setFileName(a_strScanFile);
//		msFile.setExperimentType(Method.MS_TYPE_INFUSION);
		msFile.setExperimentType(Method.MS_TYPE_TIM);
		msFile.setCategory(FileCategory.ANNOTATION_CATEGORY);
		msFile.setVersion("1.0");
		msFile.setReader(new MzXmlReader());
		return msFile;
	}

	private static HashMap<Integer, Scan> getScans(MSFile a_msFile) {
		if ( !(a_msFile.getReader() instanceof IMSAnnotationFileReader) )
			return null;

		HashMap<Integer, Scan> t_mapIDToScan = new HashMap<Integer, Scan>();
		IMSAnnotationFileReader t_msFileReader = (IMSAnnotationFileReader) a_msFile.getReader();
		List<Scan> t_lScans = t_msFileReader.readMSFile(a_msFile);
		for ( Scan t_scan : t_lScans )
			t_mapIDToScan.put(t_scan.getScanNo(), t_scan);
		return t_mapIDToScan;
	}

}
