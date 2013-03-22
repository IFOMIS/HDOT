package org.ifomis.ontologyaggregator.sort;

import java.util.Comparator;

import org.apache.commons.lang3.ObjectUtils;

import uk.ac.ebi.ontocat.bioportal.xmlbeans.OntologyBean;

/**
 * Compares two ontologies by checking if they are defined as flat.
 * 
 * @author Nikolina
 * 
 */
public class ComparatorIsFlat implements Comparator<OntologyBean> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(OntologyBean o1, OntologyBean o2) {
		return ObjectUtils.compare(o2.getIsFlat(), o1.getIsFlat());
	}
//		if (o1.getIsFlat() == null || o2.getIsFlat() == null) {
//			return 0;
//		}
//
//		boolean o1IsFlat = o1.getIsFlat().equals("1");
//		boolean o2IsFlat = o2.getIsFlat().equals("1");
//
//		if (o1IsFlat && !o2IsFlat) {
//			return -1;
//		} else if (!o1IsFlat && o2IsFlat) {
//			return 1;
//		}
//		return 0;
//	}

}
