package org.ifomis.ontologyaggregator.search;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;

import uk.ac.ebi.ontocat.OntologyServiceException;

public class TestSearchEngine {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Logger log = Logger.getLogger(TestSearchEngine.class);

		long start = System.currentTimeMillis();

		String fileWithOntologies, terms = "";

		log.info("Start searching ...");
		Properties properties = new Properties();

		try {
			properties.load(new FileInputStream("config/aggregator.properties"));
			// check if the input args are specified otherwise take the default
			// values.
			if (args.length < 2) {
				fileWithOntologies = properties
						.getProperty("fileOntologiesOrder");
			} else {
				fileWithOntologies = args[0];
				 terms = args[0].replace("_", " ");

				 properties.setProperty("searchedTerms", terms);
		    	 properties.store(new FileOutputStream("config/aggregator.properties"), null);

			}
			String[] termList = terms.split(";");
			
			fileWithOntologies = properties.getProperty("fileOntologiesOrder");

			SearchEngine se = new SearchEngine(fileWithOntologies);
			for (int i = 0; i < termList.length; i++) {
				String term = termList[i];
				se.searchTermInBioPortal(term);
			}

			File logFile = new File("log/loggingSearchEngine.html");
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
			String date = dateFormat.format(new Date());

			logFile.renameTo(new File("log/" + date
					+ "_loggingSearchEngine.html"));

			log.info("Done.");
			log.info("Log messages written in: log/" + date
					+ "_loggingSearchEngine.html");

		} catch (IOException e) {
			e.printStackTrace();
		} catch (OntologyServiceException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();

		long milliseconds = (end - start);

		long seconds = (milliseconds / 1000);

		long mins = seconds / 60;
		long restsecs = seconds % 60;

		log.info("Execution time was " + (end - start) + " ms.");
		log.info("Execution time was " + mins + ":" + restsecs + " sec.");
	}

}
