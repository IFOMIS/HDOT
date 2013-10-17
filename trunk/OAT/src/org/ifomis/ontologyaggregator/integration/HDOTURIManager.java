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

	public void checkURIs(Recommendation acceptedRecommendation)
			throws IOException, OntologyServiceException {
		List<String> predefinedOntologies = FileUtils.readLines(new File(
				Configuration.PREDEFINED_ONTOLOGIES_FILE.toURI()));

		this.keepOriginalURI = predefinedOntologies
				.contains(acceptedRecommendation.getHit().getOntology()
						.getAbbreviation());

		this.acceptedRecommendation = acceptedRecommendation;

		this.uriGenerator = new HDOTURIGenerator(acceptedRecommendation,
				acceptedRecommendation.includeSubclasses());
	}

	public boolean keepOriginalURI() throws IOException,
			OntologyServiceException {

		return this.keepOriginalURI;
	}

	/**
	 * Generates new HDOT URI.
	 * 
	 * @return the next HDOT URI
	 * @throws IOException
	 */
	public String generateNextHdotUri() throws IOException {
		
//		String hdotModuleIRI = acceptedRecommendation.getHdotModule()
//				.getOntologyID().getOntologyIRI().toString();

		String hdotModuleIRI = acceptedRecommendation.getURIOfModuleForURIGeneration();
			
		System.out.println(" hdotModuleIRI: " +hdotModuleIRI);
		String[] hdotModules = hdotModuleIRI.split("/");
		String hdotModule = hdotModules[hdotModules.length - 1];
		
//		if(hdotModule.contains("owl")){
//			System.out.println("contains owl");
//			System.out.println("###---- " + hdotModule);
//			hdotModule = hdotModule.split("\\.")[0].toUpperCase();
//		}
		String newHdotUri = uriGenerator.generateURI(hdotModule);
		return newHdotUri;
	}
}
