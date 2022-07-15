package org.grits.toolbox.tools.gsl.util.io.om;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.grits.toolbox.ms.file.reader.impl.MzXmlReader;
import org.grits.toolbox.ms.om.data.Method;
import org.grits.toolbox.ms.om.data.Peak;
import org.grits.toolbox.ms.om.data.Scan;
import org.grits.toolbox.util.structure.glycan.util.FilterUtils;

/**
 * Class for handling the Method object in org.grits.toolbox.ms.om plug-in.
 * <i>Currently, we just use this for the test.</i>
 * @author Masaaki Matsubara
 *
 */
public class MSDataModelFileHandlar {

	private static final Logger logger = Logger.getLogger(MSDataModelFileHandlar.class);

	/**
	 * Serializes Method object to XML file
	 * @param method Method for serializing to XML file
	 * @param filename String of full path of XML full path file name
	 */
	public static void serializeMethod(Method method, String filename) {
		try {
			// Add context list for Filter classes
			List<Class> contextList = new ArrayList<>(Arrays.asList(FilterUtils.filterClassContext));
			contextList.add(Method.class);
			// Create JAXB context and instantiate marshallar
			JAXBContext context = JAXBContext.newInstance( contextList.toArray(new Class[contextList.size()]));
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			// Write to File
			m.marshal(method, new File(filename));
			
		} catch (JAXBException e) {
			logger.error("An error during serializing method object.", e);
		}

	}

	/**
	 * Reads Method object from XML file
	 * @param filename String of full path of Method XML file
	 * @return Method reading from filename
	 */
	public static Method readMethodXML(String filename) {
		try {
			// Add context list for Filter classes
			List<Class> contextList = new ArrayList<>(Arrays.asList(FilterUtils.filterClassContext));
			contextList.add(Method.class);
			// Create JAXB context and instantiate marshallar
			JAXBContext context = JAXBContext.newInstance( contextList.toArray(new Class[contextList.size()]));
			Unmarshaller um = context.createUnmarshaller();

			// Read from File
			Method method = (Method) um.unmarshal(new FileReader(filename));
			return method;
		} catch (JAXBException e) {
			logger.error("An error during deserializing Method object.", e);
		} catch (FileNotFoundException e) {
			logger.error("Not found Method XML file .", e);
		}
		return null;
	}

	/**
	 * Gets Scan numbers with their subscan numbers
	 * @param a_strFilename String of the mzXML file name
	 * @return Map of Scan number to List of the subscan numbers (the List will be empty if there is no subscan in the Scan)
	 */
	public static Map<Integer, List<Integer>> getScanNumberToSubscanNumbersFromMzXML(String a_strFilename) {
		Map<Integer, List<Integer>> t_mapScanToSubscans = new TreeMap<>();
		// Load mzXML parser
		MzXmlReader t_mzXMLParser = new MzXmlReader();

		// Get scan object
		int t_nMaxScan = MzXmlReader.getMaxScanNumber(a_strFilename);
		for ( int t_iScanNum=0; t_iScanNum<t_nMaxScan; t_iScanNum++ ) {
			// Read by scan number. Each scan will not have sub scans.
			List<Scan> t_lPreScans = t_mzXMLParser.readMzXmlFile(a_strFilename, -1, -1, t_iScanNum);
			
			// Skip if read scan is empty.
			if ( t_lPreScans.isEmpty() ) {
//				System.out.println("Empty!!");
				continue;
			}
			Scan t_scan = null;
			for ( Scan t_scan0 : t_lPreScans ) {
				if ( t_scan0.getScanNo() != t_iScanNum ) {
//					System.out.println("Scan numbers are not match: "+t_scan.getScanNo()+" vs "+t_iScanNum);
					continue;
				}
				t_scan = t_scan0;
				break;
			}
			// Put a scan number
			if ( !t_mapScanToSubscans.containsKey(t_iScanNum) )
				t_mapScanToSubscans.put(t_iScanNum, new ArrayList<Integer>());
			// Add subscan numbers
			if ( t_scan.getSubScans() == null || t_scan.getSubScans().isEmpty() )
				continue;
			t_mapScanToSubscans.get(t_iScanNum).addAll( t_scan.getSubScans() );
		}

		return t_mapScanToSubscans;
	}

	public static List<Scan> readAllScansFromMzXML(String a_strFilename) {
		List<Scan> t_lScans = new ArrayList<>();
		// Parse mzXML file
		MzXmlReader t_mzXMLParser = new MzXmlReader();
		// Read scans
		int t_nMS=1;
		while ( true ) {
			List<Scan> t_lMSnScans = t_mzXMLParser.readMzXmlFile(a_strFilename, t_nMS, -1, -1);
			if ( t_lMSnScans.isEmpty() )
				break;
			t_lScans.addAll(t_lMSnScans);
			t_nMS++;
		}
		// Sort by scan no
		Collections.sort(t_lScans, new Comparator<Scan>(){
			@Override
			public int compare(Scan o1, Scan o2) {
				if (o1.getScanNo() < o2.getScanNo() ) return -1;
				if (o1.getScanNo() > o2.getScanNo() ) return 1;
				return 0;
			}
		});
		return t_lScans;
	}

	/**
	 * Reads and get all Scans in the specified mzXML file.
	 * Each scan will not have sub scans.
	 * @param a_strFilename String of mzXML file name
	 * @return List of Scans
	 */
	public static List<Scan> readNumberedScansFromMzXML(String a_strFilename) {
		List<Scan> t_lScans = new ArrayList<>();
		// Parse mzXML file
		MzXmlReader t_mzXMLparser = new MzXmlReader();
		// Get list of scan numbers which has no parent
//		List<Integer> t_lScanNums = t_mzXMLparser.getScanList(a_strFilename, -1);
		int t_nMaxScanNum = MzXmlReader.getMaxScanNumber(a_strFilename);
		System.out.println("Total scan number: "+t_nMaxScanNum);
//		for ( Integer t_iScanNum : t_lScanNums ) {
		for (int t_iScanNum = 1; t_iScanNum <= t_nMaxScanNum; t_iScanNum++) {
			// Read by scan number. Each scan will not have sub scans.
			List<Scan> t_lPreScans = t_mzXMLparser.readMzXmlFile(a_strFilename, -1, -1, t_iScanNum);
			
			// Skip 
			if ( t_lPreScans.isEmpty() ) {
				System.out.println("Empty!!");
				continue;
			}
			for ( Scan t_scan : t_lPreScans ) {
				if ( t_scan.getScanNo() != t_iScanNum ) {
					System.out.println("Scan numbers are not match: "+t_scan.getScanNo()+" vs "+t_iScanNum);
					continue;
				}
				t_lScans.add(t_scan);

				System.out.println("Scan number: "+t_scan.getScanNo());
				System.out.println("Number of peaks: "+t_scan.getPeaklist().size());
				if ( t_scan.getPrecursor() != null )
					System.out.println("Precursor mz: "+t_scan.getPrecursor().getMz());
				System.out.println("MS level: "+t_scan.getMsLevel());
				System.out.println("Most abundant peak: "+t_scan.getMostAbundantPeak());
				System.out.println("Is there parent scan: "+t_scan.getParentScan());
				System.out.println();
			}
		}
		return t_lScans;
	}

	private static void checkPeaks(Scan a_scan) {
		List<Peak> t_lPeak = a_scan.getPeaklist();
		Collections.sort(t_lPeak, new Comparator<Peak>(){

			@Override
			public int compare(Peak o1, Peak o2) {
				if ( o1.getIntensity() > o2.getIntensity() ) return -1;
				if ( o1.getIntensity() < o2.getIntensity() ) return 1;
				return 0;
			}
			
		});
		Peak t_peak = t_lPeak.get(0);
		double t_dMaxIntensity = t_peak.getIntensity();
		int i=0;
		while ((t_peak.getIntensity()/t_dMaxIntensity)>0.01D) {
			System.out.println(" -> "+t_peak.getMz()+"\t"+(t_peak.getIntensity()/t_dMaxIntensity));
			i++;
			t_peak = t_lPeak.get(i);
		}
	}
}
