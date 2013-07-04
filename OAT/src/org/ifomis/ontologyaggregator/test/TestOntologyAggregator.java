package org.ifomis.ontologyaggregator.test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.ifomis.ontologyaggregator.exception.HdotExtensionException;
import org.ifomis.ontologyaggregator.integration.HDOTExtender;
import org.ifomis.ontologyaggregator.recommendation.RecommendationFilter;
import org.ifomis.ontologyaggregator.recommendation.RecommendationGenerator;
import org.ifomis.ontologyaggregator.recommendation.UserInputReader;
import org.ifomis.ontologyaggregator.search.SearchEngine;
import org.ifomis.ontologyaggregator.util.Configuration;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import uk.ac.ebi.ontocat.OntologyServiceException;

/**
 * This is the main class that tests the OAT.
 * 
 * @author Nikolina
 * 
 */
public class TestOntologyAggregator {
	private static final Logger log = Logger
			.getLogger(TestOntologyAggregator.class);

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
//		Properties properties = new Properties();

//		properties.load(new FileInputStream("config/aggregator.properties"));

		Configuration.getInstance();
		
		String terms = "";

		if (args.length < 2) {
			usage();
			System.exit(0);
		} else {
			terms = args[0].replace("_", " ");

		}

		String[] termList = terms.split(";");

		IRI fileWithOntologies = Configuration.ONTO_IDS_FILE;

		SearchEngine se = new SearchEngine(fileWithOntologies);
		for (int i = 0; i < termList.length; i++) {
			String term = termList[i];
			se.searchTermInBioPortal(term);
			if (se.getListOfPaths().size() == 0) {
				log.info("list of paths with hits is empty");
				terminate(start, term, true);
			}
			log.info("\nRECOMMENDATION GENERATION\n");

			RecommendationGenerator rg = new RecommendationGenerator(
					Configuration.HDOT_CONTAINER_AUTHORIZED, term,
					se.getRestrictedBps(), start);

			boolean recommendationWasAccepted = processingSearchResults(true,
					se, term, start, args[1], rg);

			if (!recommendationWasAccepted) {
				log.info("search for further recommendations ...");

				boolean recommendationWasAcceptedSecondTurn = processingSearchResults(
						false, se, term, start, args[1], rg);

				if (!recommendationWasAcceptedSecondTurn) {
					log.info("NO RECOMMENDATION FOUND IN THE TOP 5-10 HITS");
				}
			}
			terminate(start, term, false);
		}
	}

	private static void terminate(long start, String term, boolean shouldExit) {
		long end = System.currentTimeMillis();

		long milliseconds = (end - start);

		long seconds = (milliseconds / 1000);

		long mins = seconds / 60;
		long restsecs = seconds % 60;

		log.info("Execution time was (in ms)  " + (end - start) + " ms.");
		log.info("Execution time was (in min) " + mins + ":" + restsecs
				+ " sec.");


		File logSearchFile = new File(Configuration.LOG_PATH.resolve("loggingSearchEngine.html").toURI());

		File logRecommendFile = new File(Configuration.LOG_PATH.resolve("loggingRecommendationGeneration.html").toURI());
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
		String date = dateFormat.format(new Date());

		logSearchFile.renameTo(new File(Configuration.LOG_PATH.resolve(date + "_" + term.replace(" ", "_")
				+ "_loggingSearchEngine.html").toURI()));

		logRecommendFile.renameTo(new File(Configuration.LOG_PATH.resolve( date + "_" + term.replace(" ", "_")
				+ "_loggingRecommendationGeneration.html").toURI()));

		log.info("Done.");
		System.out.println("Log messages written in: log/" + date + "_" + term.replace(" ", "_")
				+ "_loggingSearchEngine.html and " + date + "_" + term.replace(" ", "_")
				+ "_loggingRecommendationGeneration.html");

		if (shouldExit) {
			System.exit(0);
		}
	}

	private static boolean processingSearchResults(boolean isTopFive,
			SearchEngine se, String term, long start, String userID,
			RecommendationGenerator rg) throws IOException, URISyntaxException,
			OntologyServiceException, OWLOntologyStorageException,
			HdotExtensionException, OWLOntologyCreationException {

		int sizeOfresultList = se.getListOfPaths().size();
		log.debug("in process results size of list with Paths: "
				+ sizeOfresultList);
		int[] indexes = computeIndexes(isTopFive, sizeOfresultList, term, start);
		int startIndex = indexes[0];
		int endIndex = indexes[1];
		if (startIndex != -1) {
			rg.generateRecommendation(
					se.getListOfPaths().subList(startIndex, endIndex),
					isTopFive);
		}
		if (isTopFive) {
			log.info("number of valid recommendations under top 5 "
					+ rg.getListOfRecommendations().size());

			if (rg.getListOfRecommendations().isEmpty()) {
				computeIndexes(false, sizeOfresultList, term, start);
				int sIndex = indexes[0];
				int eIndex = indexes[1];
				if (sIndex != -1) {
					rg.generateRecommendation(
							se.getListOfPaths().subList(sIndex, eIndex), false);
				}
			}
		} else {
			log.info("number of valid recommendations top 5-10 "
					+ rg.getListOfRecommendations().size());
		}
		RecommendationFilter rf = new RecommendationFilter(term,
				rg.getListOfRecommendations(),
				rg.getListOfRecsPossibleInCoreOfHDOT(),
				rg.getListImportedNotLeafMatches(),
				rg.getListOfInCoreNotLeafMatches());

		while (!rg.getListOfRecommendations().isEmpty()) {

			rf.checkValidRecommendations();

			UserInputReader inputReader = new UserInputReader();
			inputReader.addUserInputListener(rf);
			inputReader.startListeningAcceptInput();

			if (rf.isAccept()) {

				if (!(rf.getAcceptedRecommendation().getHitChildren() == null)) {
					inputReader.startListeningIncludeSubclassesInput();
				}
				log.debug("rf.isIncludeSubclasses() "
						+ rf.isIncludeSubclasses());
				new HDOTExtender(rf.getAcceptedRecommendation(),
						rf.isIncludeSubclasses(), rg.getOntology_manager(),
						rg.getHdot_ontology(), rg.getOntologyService(), userID);
				break;
			}
		}
		if (rg.getListOfRecommendations().isEmpty()) {
			rf.checkPotentialRecommendations();
		}
		return rf.isAccept();
	}

	private static int[] computeIndexes(boolean isTopFive,
			int sizeOfresultList, String term, long start) {
		int startIndex = 0;
		int endIndex = 0;
		int[] result = new int[2];
		if (isTopFive) {

			// change only the end index but check the length of the result list
			if (sizeOfresultList < 5) {
				endIndex = sizeOfresultList - 1;
			} else {
				endIndex = 5;
			}
		} else {
			// change both indexes but again check the length of the result list

			if (sizeOfresultList < 10) {
				if (sizeOfresultList <= 5) {
					// well there were less than 5 results
					// terminate(start, term, true);
					startIndex = -1;
					endIndex = -1;
				} else {
					// there were more than 5 but less than 10
					startIndex = 5;
					endIndex = sizeOfresultList - 1;
				}
			} else {
				// there were more than 10 results
				startIndex = 5;
				endIndex = 10;
			}
		}
		result[0] = startIndex;
		result[1] = endIndex;
		return result;
	}

	private static void usage() {
		System.out
				.println("usage: java -jar OntologyAggregator.jar <term1;term2...> <userID>\nyou can specify one or more terms\nnote: terms that have more than one word have to be written in quotes, e.g. \"bone marrow\"");
	}
}
