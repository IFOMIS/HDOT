package org.ifomis.ontologyaggregator.integration;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

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

	public HDOTURIGenerator(Recommendation acceptedRecommendation,
			boolean includeSubclasses) {
	}

	public String generateURI(String hdotModule) throws IOException {

		Properties properties = new Properties();
		properties.load(new FileInputStream("data/counter.properties"));

		String prefix = hdotModule.split("\\.")[0].toUpperCase();

		String counter = properties.getProperty("counterForURIS_"
				+ prefix);
		log.debug("URI next number read");
		
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
		properties.setProperty("counterForURIS_" + prefix, sb.toString());
		properties.store(new FileOutputStream("data/counter.properties"), null);

		log.debug("URI next number updated");
				
		return String.format("http://www.ifomis.org/hdot/%s#%s_%s",
				 hdotModule, prefix, counter);
	}

}
