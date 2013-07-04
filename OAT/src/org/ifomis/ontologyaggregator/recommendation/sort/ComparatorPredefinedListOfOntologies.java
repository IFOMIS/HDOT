package org.ifomis.ontologyaggregator.recommendation.sort;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.ifomis.ontologyaggregator.recommendation.Recommendation;
import org.ifomis.ontologyaggregator.util.Configuration;

import uk.ac.ebi.ontocat.OntologyServiceException;

/**
 * Compares two @link{Recommendation}s wrt the source ontology preference.
 *  
 * @author Nikolina
 *
 */
public class ComparatorPredefinedListOfOntologies implements
		Comparator<Recommendation> {

	@Override
	public int compare(Recommendation r1, Recommendation r2) {
		List<String> predefinedOntologies = new ArrayList<>();
		boolean r1FromPredefinedList = false;
		boolean r2FromPredefinedList = false;

		try {
			predefinedOntologies = FileUtils.readLines(new File(
					Configuration.PREDEFINED_ONTOLOGIES_FILE.toURI()));

			r1FromPredefinedList = predefinedOntologies.contains(r1.getHit()
					.getOntology().getAbbreviation());

			r2FromPredefinedList = predefinedOntologies.contains(r2.getHit()
					.getOntology().getAbbreviation());
			
		} catch (IOException | OntologyServiceException e) {
			e.printStackTrace();
		}

		if (r1FromPredefinedList && !r2FromPredefinedList) {
			return -1;
		} else if (!r1FromPredefinedList && r2FromPredefinedList) {
			return 1;
		}
		return 0;

	}

}
