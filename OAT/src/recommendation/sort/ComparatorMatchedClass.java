package recommendation.sort;

import java.util.Comparator;

import recommendation.Recommendation;

public class ComparatorMatchedClass implements Comparator<Recommendation>{

	@Override
	public int compare(Recommendation r1, Recommendation r2) {
		
		//TODO define how to compare it
		return r1.getMatchedClass().compareTo(r2.getMatchedClass());
	}

}
