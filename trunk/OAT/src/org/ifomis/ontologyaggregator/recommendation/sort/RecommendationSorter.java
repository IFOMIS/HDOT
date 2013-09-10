package org.ifomis.ontologyaggregator.recommendation.sort;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.comparators.ComparatorChain;
import org.ifomis.ontologyaggregator.recommendation.Recommendation;


/**
 * Sorts the recommendations.
 * 
 * @author Nikolina
 *
 */
public class RecommendationSorter {

	public List<Recommendation> sortRecommendations(List<Recommendation> validRecommendations) {
		ComparatorChain comparatorChain = new ComparatorChain();
		comparatorChain.addComparator(new ComparatorPredefinedListOfOntologies());
		comparatorChain.addComparator(new ComparatorDefinitions());
		comparatorChain.addComparator(new ComparatorTotalParentsOfHdotClass());
		comparatorChain.addComparator(new ComparatorPositionOfMatchedParent());
		comparatorChain.addComparator(new ComparatorTotalMatchedParents());
		Collections.sort(validRecommendations, comparatorChain);
		
		return validRecommendations;
	}
}
