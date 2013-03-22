package recommendation.sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.comparators.ComparatorChain;

import recommendation.Recommendation;

public class RecommendationSorter {

	public void sortRecommendations(List<Recommendation> validRecommendations) {
		ComparatorChain comparatorChain = new ComparatorChain();
//		comparatorChain.addComparator(new ComparatorMatchedClass());
		comparatorChain.addComparator(new ComparatorDefinitions());
		comparatorChain.addComparator(new ComparatorNoOfMatchedParent());

		Collections.sort(validRecommendations, comparatorChain);

	}

	public static void main(String[] args) {
		List<Recommendation> list = new ArrayList<>();
//		list.add(new Recommendation(0, null, false, false, null, null,
//				null, 3, null, null, null));
//		list.add(new Recommendation(0, null, false, false, null, null,
//				null, 5, null, null, null));
//		list.add(new Recommendation(0, null, false, false, null, null,
//				null, 1, null, null, null));
		
		ComparatorChain comparatorChain = new ComparatorChain();
		comparatorChain.addComparator(new ComparatorDefinitions(), true);
		comparatorChain.addComparator(new ComparatorNoOfMatchedParent());
		

		Collections.sort(list, comparatorChain);
		for (Recommendation recommendation : list) {
			System.out.println(recommendation.getParentNo());
			System.out.println(recommendation.getHitDefinition());
			
		}

	}
}
