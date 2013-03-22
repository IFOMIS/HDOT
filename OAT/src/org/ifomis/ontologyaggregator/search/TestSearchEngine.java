package org.ifomis.ontologyaggregator.search;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import uk.ac.ebi.ontocat.OntologyServiceException;

public class TestSearchEngine {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Logger log = Logger.getLogger(TestSearchEngine.class);

		long start = System.currentTimeMillis();

		String fileWithOntologies, fileWithTerms;

		log.info("Start searching ...");
		
		//check if the input args are specified otherwise take the default values.
		if (args.length < 2) {
			fileWithOntologies = "data/listOfOntoIds.txt";
			fileWithTerms = "data/TermList.txt";
		} else {
			fileWithOntologies = args[0];
			fileWithTerms = args[1];
		}
		try {
			SearchEngine se = new SearchEngine(fileWithOntologies,
					fileWithTerms);
			for (int i = 0; i < se.getTermsList().size(); i++) {
				String term = se.getTermsList().get(i);
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
