package sort;

import java.util.Comparator;

import uk.ac.ebi.ontocat.bioportal.xmlbeans.OntologyBean;

/**
 * Compares two ontologies with respect to the number of classes with no
 * Documentation.
 * 
 * @author Nikolina
 * 
 */
public class ComparatorClassesWithNoDocumentation implements
		Comparator<OntologyBean> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(OntologyBean o1, OntologyBean o2) {

		if (o1.getMetricsBean() == null
				|| o1.getMetricsBean().getClassesWithNoDocumentation() == null) {
			return 0;
		}
		if (o2.getMetricsBean() == null
				|| o2.getMetricsBean().getClassesWithNoDocumentation() == null) {
			return 0;
		}
		Integer numberOfClassesNoAuthorO1 = o1.getMetricsBean()
				.getClassesWithNoDocumentation().size();
		Integer numberOfClassesNoAuthorO2 = o2.getMetricsBean()
				.getClassesWithNoDocumentation().size();
		Integer numberOfClassesWithNoDocumentationThreshold = 10;

		Integer o1vsThreshold = numberOfClassesWithNoDocumentationThreshold
				.compareTo(numberOfClassesNoAuthorO1);
		Integer o2vsThreshold = numberOfClassesWithNoDocumentationThreshold
				.compareTo(numberOfClassesNoAuthorO2);

		return o1vsThreshold.compareTo(o2vsThreshold);
	}

}
