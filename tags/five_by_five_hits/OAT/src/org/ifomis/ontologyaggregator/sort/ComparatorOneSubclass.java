package org.ifomis.ontologyaggregator.sort;

import java.util.Comparator;

import uk.ac.ebi.ontocat.bioportal.xmlbeans.OntologyBean;

/**
 * Compares two ontologies based on numver of classes that have one subclass.
 * 
 * @author Nikolina
 * 
 */
public class ComparatorOneSubclass implements Comparator<OntologyBean> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(OntologyBean o1, OntologyBean o2) {
		if (o1.getMetricsBean() == null || o2.getMetricsBean() == null) {
			return 0;
		}
		if (o1.getMetricsBean().getClassesWithOneSubclass() == null
				|| o2.getMetricsBean().getClassesWithOneSubclass() == null) {
			return 0;
		}
		Integer classesWithOneSiblingO1 = o1.getMetricsBean()
				.getClassesWithOneSubclass().size();
		Integer classesWithOneSiblingO2 = o1.getMetricsBean()
				.getClassesWithOneSubclass().size();

		Integer zero = 0;
		Integer o1vs0 = zero.compareTo(classesWithOneSiblingO1);
		Integer o2vs0 = zero.compareTo(classesWithOneSiblingO2);

		return o1vs0.compareTo(o2vs0);
	}

}
