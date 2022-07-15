package org.grits.toolbox.tools.gsl.util.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.grits.toolbox.ms.file.reader.impl.MzXmlReader;
import org.grits.toolbox.ms.om.data.Peak;
import org.grits.toolbox.ms.om.data.Scan;
import org.grits.toolbox.tools.gsl.dango.ScanReader;
import org.grits.toolbox.tools.gsl.util.io.om.MSDataModelFileHandlar;

public class TestScanReader {

	public static void main(String[] args) {
		String t_strScanFilename = TestResourcePath.MZXML2_PATH;
		String t_strMethodFilename = TestResourcePath.RESOURCE_DIR+"method.xml";

//		List<Scan> t_lScans = MSDataModelFileHandlar.readNumberedScansFromMzXML(t_strScanFilename);
//		List<Scan> t_lScans = MSDataModelFileHandlar.readAllScansFromMzXML(t_strScanFilename);

		MzXmlReader t_reader = new MzXmlReader();
		List<Scan> t_lScans = t_reader.readMzXmlFile(t_strScanFilename, -1, 1, -1);

		Map<Integer, Scan> t_mapNumberToScan = new HashMap<>();
		List<ScanReader> t_lScanRs = new ArrayList<>();
		for ( Scan t_scan : t_lScans ) {
			System.out.println(t_scan);
			System.out.println("SubScans: "+t_scan.getSubScans());
			t_mapNumberToScan.put(t_scan.getScanNo(), t_scan);
			if ( t_scan.getPrecursor() == null || t_mapNumberToScan.containsKey( t_scan.getParentScan() ) )
				continue;
			// Check precursor ion
			System.out.println("Precursor "+t_scan.getPrecursor());
			Scan t_scanParent = t_mapNumberToScan.get( t_scan.getParentScan() );
			for ( Peak t_peakParent : t_scanParent.getPeaklist() ) {
				double t_dDev = t_peakParent.getMz() - t_scan.getPrecursor().getMz();
				if ( Math.abs(t_dDev) > 0.1 )
					continue;
				System.out.println("Matched!! "+t_scan.getPrecursor());
				System.out.println(t_peakParent);
				if ( t_scanParent.getMsLevel() < 2 )
					continue;
				System.out.println("Parent scan level: "+t_scanParent.getMsLevel());
			}
			
		}
		for ( Scan t_scan : t_lScans ) {
			System.out.println(t_scan);
			System.out.println("SubScans: "+t_scan.getSubScans());
		}
	}

}
