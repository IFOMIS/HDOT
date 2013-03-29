package org.ifomis.ontologyaggregator.integration;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.ifomis.ontologyaggregator.recommendation.Recommendation;

import uk.ac.ebi.ontocat.OntologyServiceException;

public class HDOTURIManager {

	boolean keepOriginalURI;

	private String uri;

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

	public boolean keepOriginalURI(){
		//TODO change for real use
		
		return true;		
//		return this.keepOriginalURI;
	}
	
	public String getNewHdotUri() {
		return newHdotUri;
	}
}
