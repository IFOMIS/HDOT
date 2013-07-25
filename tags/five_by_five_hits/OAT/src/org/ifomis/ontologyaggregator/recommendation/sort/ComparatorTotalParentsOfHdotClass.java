package org.ifomis.ontologyaggregator.recommendation.sort;

import java.util.Comparator;

import org.ifomis.ontologyaggregator.recommendation.Recommendation;


/**
 *  Compares two @link{Recommendation}s wrt the number of the parents that matched
 *	 the hdot class. Higher number is preferred.
 *
 * @author Nikolina
 *
 */
public class ComparatorTotalParentsOfHdotClass implements Comparator<Recommendation>{

	@Override
	public int compare(Recommendation r1, Recommendation r2) {
		
		return ((Integer)r1.getHdotHierarchy().size()).compareTo(r2.getHdotHierarchy().size());
	}

}
