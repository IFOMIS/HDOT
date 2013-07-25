package org.ifomis.ontologyaggregator.sort;

import java.util.Comparator;

import uk.ac.ebi.ontocat.bioportal.xmlbeans.OntologyBean;

/**
 * Compares two ontologies with respect to the number of classes with no Author.
 * 
 * @author Nikolina
 * 
 */
public class ComparatorClassesWithNoAuthor implements Comparator<OntologyBean> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(OntologyBean o1, OntologyBean o2) {
		if (o1.getMetricsBean() == null
				|| o1.getMetricsBean().getClassesWithNoAuthor() == null) {
			return 0;
		}
		if (o2.getMetricsBean() == null
				|| o2.getMetricsBean().getClassesWithNoAuthor() == null) {
			return 0;
		}
		Integer numberOfClassesNoAuthorO1 = o1.getMetricsBean()
				.getClassesWithNoAuthor().size();
		Integer numberOfClassesNoAuthorO2 = o2.getMetricsBean()
				.getClassesWithNoAuthor().size();
		Integer numberOfClassesWithNoAuthorThreshold = 10;

		Integer o1vsThreshold = numberOfClassesWithNoAuthorThreshold
				.compareTo(numberOfClassesNoAuthorO1);
		Integer o2vsThreshold = numberOfClassesWithNoAuthorThreshold
				.compareTo(numberOfClassesNoAuthorO2);

		return o1vsThreshold.compareTo(o2vsThreshold);
	}
}
