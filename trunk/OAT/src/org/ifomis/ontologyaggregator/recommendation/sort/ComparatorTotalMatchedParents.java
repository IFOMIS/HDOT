package org.ifomis.ontologyaggregator.recommendation.sort;

import java.util.Comparator;

import org.ifomis.ontologyaggregator.recommendation.Recommendation;

/**
 * Compares two @link{Recommendation}s wrt the number of the parent that matched
 * hdot classes. Greater number is preferred.
 * 
 * @author Nikolina
 * 
 */
public class ComparatorTotalMatchedParents implements
Comparator<Recommendation> {

	@Override
	public int compare(Recommendation recommendation1, Recommendation recommendation2) {
		Integer totalMatchedParentsR1 = recommendation1.getMatchedParents();
		Integer totalMatchedParentsR2 = recommendation2.getMatchedParents();
		System.out.println("totalMatchedParentsR1" + totalMatchedParentsR1 );
		System.out.println("totalMatchedParentsR2" + totalMatchedParentsR2 );

		return totalMatchedParentsR1.compareTo(totalMatchedParentsR2);
	}

}
