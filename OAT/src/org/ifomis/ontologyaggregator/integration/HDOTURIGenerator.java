package org.ifomis.ontologyaggregator.integration;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.ifomis.ontologyaggregator.recommendation.Recommendation;

/**
 * Generates new URI for the integration of the selected class.
 *  
 * @author Nikolina
 *
 */
public class HDOTURIGenerator {

	private static final Logger log = Logger
			.getLogger(HDOTURIGenerator.class);
	
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

		String counter = FileUtils.readFileToString(new File("data/counterForURIS_"
				+ prefix));
		log.debug("URI next number read");
		
		String[] arg = { hdotModule, prefix, counter };

		int lengthOfCounter = counter.length();
		
		Integer intCounter = Integer.parseInt(counter);
		++intCounter;
		StringBuffer sb = new StringBuffer();
		if (lengthOfCounter > intCounter.toString().length()){
			int numberOfPrefixzeros = lengthOfCounter -intCounter.toString().length();
			
			while(numberOfPrefixzeros != 0){
				sb.append("0");
				--numberOfPrefixzeros;
			}
		}
		
		sb.append(intCounter.toString());
		FileUtils.write(new File("data/counterForURIS_HDOT_PM"),
				sb.toString());
		log.debug("URI next number updated");
		
		//TODO extend with sub classes
		
		return String.format("http://hdot.googlecode.com/svn/trunk/%s#%s_%s",
				arg);
	}

}
