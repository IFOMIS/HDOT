package org.ifomis.ontologyaggregator.search;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.ifomis.ontologyaggregator.util.Configuration;
import org.semanticweb.owlapi.model.IRI;

public class TestSearchEngine {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Logger log = Logger.getLogger(TestSearchEngine.class);

		long start = System.currentTimeMillis();

		String terms = "";
		IRI fileWithOntologies;

		log.info("Start searching ...");

		// check if the input args are specified otherwise take the default
		// values.
		if (args.length < 2) {
			System.out
					.println("usage: java -jar OntologyAggregator.jar <term1;term2...> <userID>\nyou can specify one or more terms\nnote: terms that have more than one word have to be written in quotes, e.g. \"bone marrow\"");

		} else {
			terms = args[0].replace("_", " ");
		}
		String[] termList = terms.split(";");

		fileWithOntologies = Configuration.ONTO_IDS_FILE;

		SearchEngine se = new SearchEngine(fileWithOntologies);
		for (int i = 0; i < termList.length; i++) {
			String term = termList[i];
			se.searchTermInBioPortal(term);
		}

		File logFile = new File("log/loggingSearchEngine.html");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
		String date = dateFormat.format(new Date());

		logFile.renameTo(new File("log/" + date + "_loggingSearchEngine.html"));

		log.info("Done.");
		log.info("Log messages written in: log/" + date
				+ "_loggingSearchEngine.html");

		long end = System.currentTimeMillis();

		long milliseconds = (end - start);

		long seconds = (milliseconds / 1000);

		long mins = seconds / 60;
		long restsecs = seconds % 60;

		log.info("Execution time was " + (end - start) + " ms.");
		log.info("Execution time was " + mins + ":" + restsecs + " sec.");
	}
}
