package org.ifomis.ontologyaggregator.sort;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import uk.ac.ebi.ontocat.bioportal.xmlbeans.OntologyBean;

/**
 * Compares two ontologies with respect to the date on which they are released.
 * 
 * @author Nikolina
 * 
 */
public class ComparatorDateRelease implements Comparator<OntologyBean> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(OntologyBean o1, OntologyBean o2) {

//		System.out.println("Date Comparator");
		String[] o1dateTokens = o1.getDateReleased().split(" ");
		String[] o2dateTokens = o2.getDateReleased().split(" ");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Date dateThreshold = null;
		Date o1ReleasedDate = null;
		Date o2ReleasedDate = null;

		try {
			dateThreshold = sdf.parse("2008-12-31");

			o1ReleasedDate = sdf.parse(o1dateTokens[0]);
			o2ReleasedDate = sdf.parse(o2dateTokens[0]);
			// System.out.println(o1dateTokens[0] + " releaseDate of" + o1);
			// System.out.println(o2dateTokens[0] + " releaseDate of" + o2);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Integer o1vsThreshold = dateThreshold.compareTo(o1ReleasedDate);
		Integer o2vsThreshold = dateThreshold.compareTo(o2ReleasedDate);
		// System.out.println("o1vsTh " + o1vsThreshold);
		// System.out.println("o2vsTh " + o2vsThreshold);
		// System.out.println("return value " +
		// o1vsThreshold.compareTo(o2vsThreshold));
		return o1vsThreshold.compareTo(o2vsThreshold);
	}
}
