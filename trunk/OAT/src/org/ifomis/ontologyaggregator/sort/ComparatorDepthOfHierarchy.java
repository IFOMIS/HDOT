package org.ifomis.ontologyaggregator.sort;

import java.util.Comparator;

import uk.ac.ebi.ontocat.bioportal.xmlbeans.OntologyBean;

/**
 * Compares two ontologies with respect to the maximum depth of hierarchy.
 * 
 * @author Nikolina
 * 
 */
public class ComparatorDepthOfHierarchy implements Comparator<OntologyBean> {

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
		Integer maxDepthO1 = o1.getMetricsBean().getMaximumDepth();
		Integer maxDepthO2 = o2.getMetricsBean().getMaximumDepth();

		return maxDepthO1.compareTo(maxDepthO2);
	}

}
