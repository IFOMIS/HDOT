package sort;

import java.util.Comparator;

import org.apache.commons.lang3.ObjectUtils;

import uk.ac.ebi.ontocat.bioportal.xmlbeans.OntologyBean;

/**
 * Compares two ontologies by checking if they are included in the OBO Foundry.
 * 
 * @author Nikolina
 * 
 */
public class ComparatorOBOFoundry implements Comparator<OntologyBean> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(OntologyBean o1, OntologyBean o2) {
		return ObjectUtils.compare(o1.getGroupIds().contains(6001),
				o2.getGroupIds().contains(6001));
		
		
//		boolean o1InOBO = o1.getGroupIds().contains(6001);
//		boolean o2InOBO = o2.getGroupIds().contains(6001);
//		// System.out.println("IN OBO Comparator");
//		if (o1InOBO && !o2InOBO) {
////			System.out.println(o1 + " in OBO but NOT" + o2);
//			return 1;
//		} else if (!o1InOBO && o2InOBO) {
////			System.out.println(o2 + " in OBO but NOT" + o1);
//
//			return -1;
//		}
//		return 0;
	}

}
