package org.ifomis.ontologyaggregator.recommendation.sort;

import java.util.Comparator;

import org.ifomis.ontologyaggregator.recommendation.Recommendation;


/**
 * Compares two @link{Recommendation}s wrt the position of the parent that matched
 * the hdot class. Lower number is preferred.
 * 
 * @author Nikolina
 * 
 */
public class ComparatorPositionOfMatchedParent implements Comparator<Recommendation> {

	@Override
	public int compare(Recommendation r1, Recommendation r2) {
		Integer r1ParentNo = r1.getParentNo();

		Integer r2ParentNo = r2.getParentNo();

		return r2ParentNo.compareTo(r1ParentNo);
	}

}
