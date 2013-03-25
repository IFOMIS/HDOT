package org.ifomis.ontologyaggregator.recommendation.sort;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.comparators.ComparatorChain;
import org.ifomis.ontologyaggregator.recommendation.Recommendation;


public class RecommendationSorter {

	public void sortRecommendations(List<Recommendation> validRecommendations) {
		ComparatorChain comparatorChain = new ComparatorChain();
		comparatorChain.addComparator(new ComparatorPredefinedListOfOntologies());
		comparatorChain.addComparator(new ComparatorDefinitions());
		comparatorChain.addComparator(new ComparatorNoParentsOfHdotClass());
		comparatorChain.addComparator(new ComparatorNoOfMatchedParent());
		Collections.sort(validRecommendations, comparatorChain);

	}
}
