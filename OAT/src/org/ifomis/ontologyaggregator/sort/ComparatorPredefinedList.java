package org.ifomis.ontologyaggregator.sort;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;

import uk.ac.ebi.ontocat.bioportal.xmlbeans.OntologyBean;

/**
 * Compares two ontologies with respect to the fact if they are contained in
 * predefined list.
 * 
 * @author Nikolina
 * 
 */
public class ComparatorPredefinedList implements Comparator<OntologyBean> {


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(OntologyBean o1, OntologyBean o2) {
		
		List<String> predefinedOntologies = new ArrayList<>();
		
		try {
			predefinedOntologies = FileUtils.readLines(new File("data/predefinedListOfOntologies"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		boolean o1InPredefinedList = predefinedOntologies.contains(o1
				.getAbbreviation());
		boolean o2InPredefinedList = predefinedOntologies.contains(o2
				.getAbbreviation());

		if (o1InPredefinedList && !o2InPredefinedList) {
			return 1;
		} else if (!o1InPredefinedList && o2InPredefinedList) {
			return -1;
		}
		return 0;
	}

}
