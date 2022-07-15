package org.grits.toolbox.tools.gsl.util.test;

import java.util.Arrays;
import java.util.List;

import org.grits.toolbox.tools.gsl.dango.LipidAnnotationUtils;

public class TestLipidFragmentsGenerationNew {

	public static void main(String[] args) {
		List<String> t_listLipids = Arrays.asList(
				"SM(d18:1/16:0)",
				"Cer(d18:1/16:0)",
				"Cer(d18:1/h16:0)",
				"Sp(d18:1)",
				"FA(16:0)",
				"Fr(h18:2)",
				"Fr(h18:2)(CONH2)",
				"FA(16:0",
				"Cer(d18:1)",
				"Sp(d18:1/16:0)",
				"Fr(h18:2)(Xxx)",
				"");

		for ( String t_strLipid : t_listLipids ) {
			System.out.println("Original: "+t_strLipid);
			printFragments(t_strLipid, null, 1);
		}

	}

	private static void printFragments(String a_strLipid, String a_strFragType, int a_iDepth) {
		List<String[]> t_lFragments = LipidAnnotationUtils.getLipidFragments(a_strLipid, a_strFragType);
		if ( t_lFragments.isEmpty() ) {
			System.out.println("Fragmentation failed or no fragments");
			return;
		}
		for ( String[] fragment : t_lFragments ) {
			if ( fragment[0].equals(a_strLipid) )
				continue;
			for ( int i=0; i<a_iDepth; i++ )
				System.out.print("\t");
			System.out.println(fragment[0]+": "+fragment[1]);
			printFragments(fragment[0], fragment[1], a_iDepth+1);
		}
	}
}
