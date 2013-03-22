package org.ifomis.ontologyaggregator.sort;

import java.util.Comparator;

import org.apache.commons.lang3.ObjectUtils;

import uk.ac.ebi.ontocat.bioportal.xmlbeans.OntologyBean;

/**
 * Compares two ontologies by checking if they are defined as meta data only.
 * 
 * @author Nikolina
 * 
 */
public class ComparatorIsMetadata implements Comparator<OntologyBean> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(OntologyBean o1, OntologyBean o2) {		
		return ObjectUtils.compare(o2.getIsMetadataOnly(), o1.getIsMetadataOnly());
//		if (o1.getIsMetadataOnly() == null || o2.getIsMetadataOnly() == null){
//			return 0;
//		}
//
//		boolean o1IsMetadataOnly = o1.getIsMetadataOnly().equals("1");
//		boolean o2IsMetadataOnly = o2.getIsMetadataOnly().equals("1");
//
//		if (o1IsMetadataOnly && !o2IsMetadataOnly) {
//			return -1;
//		} else if (!o1IsMetadataOnly && o2IsMetadataOnly) {
//			return 1;
//		}
//		return 0;
	}

}
