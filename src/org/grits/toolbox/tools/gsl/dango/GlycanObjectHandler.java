package org.grits.toolbox.tools.gsl.dango;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.grits.toolbox.ms.annotation.structure.GlycanStructure;
import org.grits.toolbox.ms.annotation.sugar.GlycanExtraInfo;
import org.grits.toolbox.ms.om.data.GlycanAnnotation;

/**
 * Class for handling Glycan object of "org.eurocarbdb.application.glycanbuilder"
 * <i>Currently, this isn't called from any classes.</i>
 * @author Masaaki Matsubara
 *
 */
public class GlycanObjectHandler {

	private static final Logger logger = Logger.getLogger(GlycanObjectHandler.class);
	
	private static final HashMap<String, Glycan> m_hmGStructureToGlycan = new HashMap<>();

	// For using GlycoCT fix
	private static final Pattern m_p1 = Pattern.compile("(-1)(\\)\\d+n)");
	private static final Pattern m_p2 = Pattern.compile("(n\\()(\\-1)");

	/**
	 * For the GlycanStructure objects in the "structures" variable (read from the database xml file),
	 * calculate sequence info and the Glycan objects and store to static HashMap.
	 * @param structure GlycanStructure
	 * @return Glycan object
	 */
	public static Glycan getGlycanObject(GlycanStructure structure) {
//		if( isCanceled() )
//			return;
//		updateListeners("Structure: " + structure.getId(), iProgress++);
		// Get from hashmap if already exist
		if( (structure.getSequence() != null || structure.getGWBSequence() != null)
		 && m_hmGStructureToGlycan.containsKey(structure.getId()) ) {
			return m_hmGStructureToGlycan.get(structure.getId());
		}

		// Convert from sequence
		Glycan t_glycan = null;
		try {
			// For GWB sequence
			if ( structure.getGWBSequence() != null && !structure.getGWBSequence().isEmpty() ) {
				t_glycan = Glycan.fromString(structure.getGWBSequence());
				// Set GlycoCT sequence if no sequence
				if (  structure.getSequenceFormat() == null
				  || !structure.getSequenceFormat().equals(GlycanAnnotation.SEQ_FORMAT_GLYCOCT_CONDENSED) )
					structure.setSequence( t_glycan.toGlycoCTCondensed() );
			// For other sequences
			} else if ( structure.getSequence() != null && !structure.getSequence().isEmpty() ) {
				// For GlydeII
				if ( structure.getSequenceFormat() == null
				  || structure.getSequenceFormat().equals(GlycanAnnotation.SEQ_FORMAT_GLYDEII) ) {
					Sugar t_sugar = GlycanExtraInfo.glydeToSugar(structure.getSequence());
					String t_glycoCT = GlycanExtraInfo.sugarToGlycoCT(t_sugar);
					t_glycan = Glycan.fromGlycoCTCondensed(t_glycoCT);
					structure.setSequence(t_glycan.toGlycoCTCondensed());
				// For GlycoCT condensed
				} else if ( structure.getSequenceFormat().equals(GlycanAnnotation.SEQ_FORMAT_GLYCOCT_CONDENSED) )  {
					t_glycan = Glycan.fromGlycoCTCondensed( structure.getSequence() );
				// For GlycoCT XML
				} else if ( structure.getSequenceFormat().equals(GlycanAnnotation.SEQ_FORMAT_GLYCOCT_XML) )  {
					t_glycan = Glycan.fromGlycoCT( structure.getSequence() );
					structure.setSequence(t_glycan.toGlycoCTCondensed());
				// For others
				} else {
					throw new Exception("Unsupported sequence type (" + structure.getSequenceFormat() + ") for structure: " + structure.getId());
				}
			// If no sequence
			} else {
				throw new Exception("Sequence not specfied for structure: " + structure.getId());
			}

			// Fix GlycoCT if it have wrong patterns
			Matcher m1 = m_p1.matcher(structure.getSequence());
			Matcher m2 = m_p2.matcher(structure.getSequence());
			if( m1.find() ) {
				StringBuffer sb = new StringBuffer(structure.getSequence().length());
				m1.reset();
				while( m1.find() ) {
					String match1 = m2.group(1);
					String match2 = m2.group(2);
					match1 = match1.replace("-1", "1");
					String sNew = Matcher.quoteReplacement(match1) + match2;
					m1.appendReplacement(sb, sNew);
				}
				m1.appendTail(sb);
				structure.setSequence(sb.toString().trim());
			} else if( m2.find() ) {
				StringBuffer sb = new StringBuffer(structure.getSequence().length());
				m2.reset();
				while( m2.find() ) {
					String match1 = m2.group(1);
					String match2 = m2.group(2);
					match2 = match2.replace("-1", "1");
					String sNew = match1 + Matcher.quoteReplacement(match2);
					m2.appendReplacement(sb, sNew);
				}
				m2.appendTail(sb);
				structure.setSequence(sb.toString().trim());
			}
			structure.setSequenceFormat(GlycanAnnotation.SEQ_FORMAT_GLYCOCT_CONDENSED);

			// Set GWB sequence if no
			if ( t_glycan != null && structure.getGWBSequence() == null )
				structure.setGWBSequence( t_glycan.toString() );

		} catch (GlycoVisitorException e) {
			logger.error(e.getMessage());
		} catch (SugarImporterException e) {
			logger.error(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		// Map glycan ID to object
		if ( t_glycan != null )
			m_hmGStructureToGlycan.put(structure.getId(), t_glycan);

		return t_glycan;
	}

}
