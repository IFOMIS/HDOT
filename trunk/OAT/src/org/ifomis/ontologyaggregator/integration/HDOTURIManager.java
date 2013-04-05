package org.ifomis.ontologyaggregator.integration;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.ifomis.ontologyaggregator.recommendation.Recommendation;

import uk.ac.ebi.ontocat.OntologyServiceException;

/**
 * Decides whether to keep the original URI of the class or to generate new HDOT
 * URI.
 * 
 * @author Nikolina
 * 
 */
public class HDOTURIManager {
	
	boolean keepOriginalURI;

	private String newHdotUri;

	public HDOTURIManager(Recommendation acceptedRecommendation,
			boolean includeSubclasses) throws OntologyServiceException,
			IOException {
		List<String> predefinedOntologies = FileUtils.readLines(new File(
				"data/predefinedListOfOntologies"));

		this.keepOriginalURI = predefinedOntologies
				.contains(acceptedRecommendation.getHit().getOntology()
						.getLabel());

		if (!keepOriginalURI) {
			HDOTURIGenerator uriGenerator = new HDOTURIGenerator(
					acceptedRecommendation, includeSubclasses);
			newHdotUri = uriGenerator.generateURI();
		}
	}

	public boolean keepOriginalURI() {

		return this.keepOriginalURI;
	}

	public String getNewHdotUri() {
		return newHdotUri;
	}
}
