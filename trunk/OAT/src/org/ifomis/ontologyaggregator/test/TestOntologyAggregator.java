package org.ifomis.ontologyaggregator.test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.ifomis.ontologyaggregator.exception.HdotExtensionException;
import org.ifomis.ontologyaggregator.integration.HDOTExtender;
import org.ifomis.ontologyaggregator.recommendation.RecommendationFilter;
import org.ifomis.ontologyaggregator.recommendation.RecommendationGenerator;
import org.ifomis.ontologyaggregator.recommendation.TestRecommendationGenerator;
import org.ifomis.ontologyaggregator.recommendation.UserInputReader;
import org.ifomis.ontologyaggregator.search.SearchEngine;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import uk.ac.ebi.ontocat.OntologyServiceException;

public class TestOntologyAggregator {
	private static final Logger log = Logger
			.getLogger(TestRecommendationGenerator.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long start = System.currentTimeMillis();

		String fileWithOntologies, fileWithTerms;

		if (args.length < 2) {
			usage();
			System.exit(0);
		} else {
			String terms = args[0].replace(";", "\n");
			terms = terms.replace("_", " ");
			try {
				FileUtils.write(new File("data/TermList.txt"), terms);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		fileWithOntologies = "data/listOfOntoIds.txt";
		fileWithTerms = "data/TermList.txt";

		try {

			SearchEngine se = new SearchEngine(fileWithOntologies,
					fileWithTerms);
			for (int i = 0; i < se.getTermsList().size(); i++) {
				String term = se.getTermsList().get(i);
				se.searchTermInBioPortal(term);

				log.info("\nRECOMMENDATION GENERATION\n");

				boolean recommendationWasAccepted = processingSearchResults(0,
						5, se, term, start, args[1]);

				if (!recommendationWasAccepted) {
					boolean recommendationWasAcceptedSecondTurn = processingSearchResults(
							5, 10, se, term, start, args[1]);
					if (!recommendationWasAcceptedSecondTurn) {
						log.info("NO RECOMMENDATION FOUND IN THE TOP 10 HITS");
					}
				}
				long end = System.currentTimeMillis();

				long milliseconds = (end - start);

				long seconds = (milliseconds / 1000);

				long mins = seconds / 60;
				long restsecs = seconds % 60;

				log.info("Execution time was " + (end - start) + " ms.");
				log.info("Execution time was " + mins + ":" + restsecs
						+ " sec.");

				File logSearchFile = new File("log/loggingSearchEngine.html");

				File logRecommendFile = new File(
						"log/loggingRecommendationGeneration.html");
				DateFormat dateFormat = new SimpleDateFormat(
						"yyyy-MM-dd-HH:mm:ss");
				String date = dateFormat.format(new Date());

				logSearchFile.renameTo(new File("log/" + date + "_" + term
						+ "_loggingSearchEngine.html"));

				logRecommendFile.renameTo(new File("log/" + date + "_" + term
						+ "_loggingRecommendationGeneration.html"));

				log.info("Done.");
				System.out.println("Log messages written in: log/" + date + "_"
						+ term + "_loggingSearchEngine.html and " + date + "_"
						+ term + "_loggingRecommendationGeneration.html");
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (OntologyServiceException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		} catch (HdotExtensionException e) {
			e.printStackTrace();
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
	}

	private static boolean processingSearchResults(int i, int j,
			SearchEngine se, String term, long start, String userID) throws IOException,
			URISyntaxException, OntologyServiceException,
			OWLOntologyStorageException, HdotExtensionException, OWLOntologyCreationException {
		RecommendationGenerator rg = new RecommendationGenerator(
				"data/hdot/hdot_all.owl", "",
				se.getListOfPaths().subList(i, j), term, se.getRestrictedBps(),
				start);

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
		return rf.isAccept();
	}

	private static void usage() {
		System.out
				.println("usage: java -jar OntologyAggregator.jar <term1;term2...> <userID>\nyou can specify one or more terms\nnote: terms that have more than one word have to be written in quotes, e.g. \"bone marrow\"");
	}
}
