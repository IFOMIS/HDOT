package org.ifomis.ontologyaggregator.recommendation.sort;

import java.util.Comparator;

import org.ifomis.ontologyaggregator.recommendation.Recommendation;


public class ComparatorNoParentsOfHdotClass implements Comparator<Recommendation>{

	@Override
	public int compare(Recommendation r1, Recommendation r2) {
		
		return ((Integer)r1.getHdotHierarchy().size()).compareTo(r2.getHdotHierarchy().size());
	}

}
