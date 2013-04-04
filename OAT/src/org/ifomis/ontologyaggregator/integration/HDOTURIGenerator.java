package org.ifomis.ontologyaggregator.integration;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.ifomis.ontologyaggregator.recommendation.Recommendation;

public class HDOTURIGenerator {

	private boolean includeSubclasses;
	private Recommendation acceptedRecommendation;

	public HDOTURIGenerator(Recommendation acceptedRecommendation,
			boolean includeSubclasses) {
		this.includeSubclasses = includeSubclasses;
		this.acceptedRecommendation = acceptedRecommendation;
	}

	public String generateURI() throws IOException {
		
		String hdotModuleIRI = acceptedRecommendation.getHdotModule()
				.getOntologyID().getOntologyIRI().toString();

		String hdotModule = hdotModuleIRI.split("/")[hdotModuleIRI.length() - 1];

		String prefix = hdotModule.split("\\.")[0].toUpperCase();

		String counter = "";
			counter = FileUtils.readFileToString(new File(
					"data/counterForURIS_" + prefix));
			
		String[] arg = { hdotModule, prefix , counter}; 

		return String.format( "http://hdot.googlecode.com/svn/trunk/%s#%s_%s", arg ); 
	}
}
