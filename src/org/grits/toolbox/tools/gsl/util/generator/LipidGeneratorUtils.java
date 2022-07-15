package org.grits.toolbox.tools.gsl.util.generator;

import java.util.ArrayList;
import java.util.List;

public class LipidGeneratorUtils {

	/**
	 * Parse string of numerical values and/or value ranges to list of numerical values (integer)
	 * @param a_strValues String of values and/or value ranges
	 * @return List of numerical values (integer)
	 */
	public static List<Integer> parseValueRanges(String a_strValues) {
		List<Integer> t_listParams = new ArrayList<Integer>();
		// Validate string
		try {
			if (!validateValueRangesFormat(a_strValues))
				return t_listParams;
		} catch (NumberFormatException e) {
			return t_listParams;
		}
		String[] t_listParsedParams = a_strValues.split(",");
		for ( String t_strParam : t_listParsedParams ) {
			// For single value
			if ( !t_strParam.contains("-") ) {
				int t_iParam = Integer.parseInt(t_strParam);
				t_listParams.add(t_iParam);
				continue;
			}
			// For value range
			String[] t_listParamRange = t_strParam.split("-");
			int t_iMin = Integer.parseInt(t_listParamRange[0]);
			int t_iMax = Integer.parseInt(t_listParamRange[1]);
			for ( int i=t_iMin; i<=t_iMax; i++ )
				t_listParams.add(i);
		}
		return t_listParams;
	}

	/**
	 * Validate the string of numerical values and/or value ranges
	 * <pre>
	 * 1-5     = 1,2,3,4,5
	 * 1,3-6,8 = 1,3,4,5,6,8
	 * </pre>
	 * @param a_strValues String containing numerical values and/or value ranges
	 * @return False if augment is null or empty ""
	 * @throws NumberFormatException If contains not digit character(s) or not valid format
	 */
	public static boolean validateValueRangesFormat(String a_strValues) throws NumberFormatException {
		// Throw exception if null or empty
		if ( a_strValues == null || a_strValues.isEmpty() )
			return false;

		// Throw exception if contains non digit
		for ( int i=0; i<a_strValues.length(); i++ ) {
			char t_char = a_strValues.charAt(i);
			if ( !Character.isDigit(t_char) && t_char != '-' && t_char != ',' )
				throw new NumberFormatException("Non digit character(s) are not allowed except for comma \",\" and hyphen \"-\".");
		}
		String[] t_listParsedParams = a_strValues.split(",",-1);
		for (String t_str : t_listParsedParams) {
			if ( t_str.contains("-") ) {
				if ( t_str.startsWith("-") )
					throw new NumberFormatException("Negative number is not allowed.");
				String[] t_strRange = t_str.split("-",-1);
				// Throw exception if two or more "-" is contained
				if ( t_strRange.length > 2 ) {
					// For hyphens are consecutive
					for ( String t_strParsed : t_strRange ) {
						if ( t_strParsed.isEmpty() )
							throw new NumberFormatException("Negative number is not allowed");
					}
					throw new NumberFormatException("Two or more hyphens between two commas are not allowed.");
				}
				// Throw exception if empty after hyphen
				if ( t_strRange[1].isEmpty() )
					throw new NumberFormatException("Number(s) are necessary after hyphen.");
				// Throw exception if max is equal or lower than min
				int t_iMin = Integer.parseInt(t_strRange[0]);
				int t_iMax = Integer.parseInt(t_strRange[1]);
				if ( t_iMin >= t_iMax )
					throw new NumberFormatException("First number must be smaller than last number in the value ranges.");
				continue;
			}
			// Throw exception if empty
			if (t_str.isEmpty())
				throw new NumberFormatException("A number between two commas are needed.");
		}
		
		return true;
	}

}
