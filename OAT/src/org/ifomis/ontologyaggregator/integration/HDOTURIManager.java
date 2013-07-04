package org.ifomis.ontologyaggregator.integration;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.ifomis.ontologyaggregator.recommendation.Recommendation;
import org.ifomis.ontologyaggregator.util.Configuration;

import uk.ac.ebi.ontocat.OntologyServiceException;

/**
 * Decides whether to keep the original URI of the class or to generate new HDOT
 * URI.
 * 
 * @author Nikolina
 * 
 */
public class HDOTURIManager {

	private boolean keepOriginalURI;
	private Recommendation acceptedRecommendation;
	private HDOTURIGenerator uriGenerator;

	public HDOTURIManager(Recommendation acceptedRecommendation,
			boolean includeSubclasses) throws OntologyServiceException,
			IOException {

		List<String> predefinedOntologies = FileUtils.readLines(new File(
				Configuration.PREDEFINED_ONTOLOGIES_FILE.toURI()));

		this.keepOriginalURI = predefinedOntologies
				.contains(acceptedRecommendation.getHit().getOntology()
						.getAbbreviation());

		this.acceptedRecommendation = acceptedRecommendation;

		this.uriGenerator = new HDOTURIGenerator(acceptedRecommendation,
				includeSubclasses);
	}

	public boolean keepOriginalURI() {

		return this.keepOriginalURI;
	}

	/**
	 * Generates new HDOT URI.
	 * 
	 * @return the next HDOT URI
	 * @throws IOException
	 */
	public String generateNextHdotUri() throws IOException {
		String hdotModuleIRI = acceptedRecommendation.getHdotModule()
				.getOntologyID().getOntologyIRI().toString();

		String[] hdotModules = hdotModuleIRI.split("/");
		String hdotModule = hdotModules[hdotModules.length - 1];

		String newHdotUri = uriGenerator.generateURI(hdotModule);
		return newHdotUri;
	}
}
