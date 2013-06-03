/**
 * 
 */
package org.ifomis.ontologyaggregator.recommendation.sort;

import java.util.Comparator;
import java.util.List;

import org.ifomis.ontologyaggregator.recommendation.Recommendation;


/**
 * Compares two @link{Recommendation}s wrt the definitions of the found hit.
 * Longer definitions are preferred.
 * 
 * @author Nikolina
 *
 */
public class ComparatorDefinitions implements Comparator<Recommendation>{

	@Override
	public int compare(Recommendation r1, Recommendation r2) {
		List<String> defR1 = r1.getHitDefinition();
		List<String> defR2 = r2.getHitDefinition();
		
		if(defR1.isEmpty()){
			return -1;
		}
		if(defR2.isEmpty()){
			return 1;
		}
		
		// TODO compare semantically
		//check the def of the upper classes 
		Integer lengthOfDef1 = (Integer) defR1.get(0).length();
		return lengthOfDef1.compareTo((Integer) defR2.get(0).length());
	}

}
