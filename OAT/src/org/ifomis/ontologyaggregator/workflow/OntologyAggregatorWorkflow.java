package org.ifomis.ontologyaggregator.workflow;

import java.util.List;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;
import org.ifomis.ontologyaggregator.integration.HDOTExtender;
import org.ifomis.ontologyaggregator.notifications.EmailSender;
import org.ifomis.ontologyaggregator.recommendation.Recommendation;
import org.ifomis.ontologyaggregator.recommendation.RecommendationFilter;
import org.ifomis.ontologyaggregator.recommendation.RecommendationGenerator;
import org.ifomis.ontologyaggregator.search.SearchEngine;
import org.ifomis.ontologyaggregator.util.Configuration;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import uk.ac.ebi.ontocat.OntologyServiceException;

/**
 * Implements the workflow of the Ontology Aggregator. Two undespecified methods
 * that correcpond to the integration with the interface.
 * 
 * @author Nikolina
 * 
 */
public abstract class OntologyAggregatorWorkflow {

	private static final Logger log = Logger
			.getLogger(OntologyAggregatorWorkflow.class);

	private long start;
	private String term;

	private EmailSender mailSender;

	public OntologyAggregatorWorkflow(String terms, String userId,
			boolean userRights) throws Exception {
		this.start = System.currentTimeMillis();

		// 0. load configuration
		Configuration.getInstance();

		// the user can give more terms
		String[] termList = terms.split(";");
		for (int i = 0; i < termList.length; i++) {

			boolean isTopFive = true;
			term = termList[i];
			log.info("Search for term " + term + " in BioPortal");
			// 1. search term
			SearchEngine se = searchTerm();

			// 2. generate and sort recommendations
			RecommendationGenerator rgTopFive = generateRecommendations(se,
					isTopFive);

			// 3. sort the recommendations
			List<Recommendation> recommendations = fiterGeneratedRecommendations(rgTopFive);

			// if among the top five no recommendations are generated go for top
			// five to ten
			RecommendationGenerator rgTopFiveToTen = null;
			if (recommendations.isEmpty()) {
				isTopFive = false;
				rgTopFiveToTen = generateRecommendations(se, isTopFive);
				recommendations = fiterGeneratedRecommendations(rgTopFiveToTen);
			}

			if (recommendations.isEmpty()) {
				log.info("NO RECOMMENDATIONS ARE GENERATED FOR THE TERM: "
						+ term);
				terminate(start, userId, userRights);
			}
			while (!recommendations.isEmpty()) {

				// 4. display recommendation to the user
				Recommendation topRecommendation = recommendations.get(0);
				displayRecommendation(topRecommendation);

				// 5. check the user input
				boolean[] result = checkUserInput();

				boolean confirmed = result[0];
				boolean includeClasses = result[1];

				// 6. extend HDOT
				if (confirmed) {
					if (isTopFive) {
						new HDOTExtender(topRecommendation, includeClasses,
								rgTopFive.getHdot_ontology(),
								rgTopFive.getOntologyService(), userId,
								userRights);
					} else {
						new HDOTExtender(topRecommendation, includeClasses,
								rgTopFiveToTen.getHdot_ontology(),
								rgTopFiveToTen.getOntologyService(), userId,
								userRights);
					}
					break;
				} else {
					mailSender.sendMail("THE USER REJECTED THE RECOMMENDATION",
							topRecommendation.toString());
				}
			}
		}
	}

	private SearchEngine searchTerm() throws Exception {
		IRI fileWithOntologies = Configuration.ONTO_IDS_FILE;
		SearchEngine se = new SearchEngine(fileWithOntologies);
		se.searchTermInBioPortal(term);
		if (se.getListOfPaths().size() == 0) {
			log.info("list of paths with hits is empty");
			terminate(start, term, true);
		}
		return se;
	}

	/**
	 * @return a boolean array in the first position the value if the
	 *         recommendation was accepted is contained and in the second
	 *         position whether the subClasses should be integrated too.
	 */
	public abstract boolean[] checkUserInput();

	/**
	 * @param recommendation
	 *            the recommendation that will be shown the user
	 */
	public abstract void displayRecommendation(Recommendation recommendation);

	private List<Recommendation> fiterGeneratedRecommendations(
			RecommendationGenerator rg) throws OWLOntologyCreationException,
			OWLOntologyStorageException, FileNotFoundException, IOException, EmailException {
		RecommendationFilter rf = new RecommendationFilter(term,
				rg.getListOfRecommendations(),
				rg.getListOfRecsPossibleInCoreOfHDOT(),
				rg.getListImportedNotLeafMatches(),
				rg.getListOfInCoreNotLeafMatches());

		return rf.checkValidRecommendations();
	}

	private RecommendationGenerator generateRecommendations(SearchEngine se,
			boolean isTopFive) throws IOException, URISyntaxException,
			OntologyServiceException {
		RecommendationGenerator rg = new RecommendationGenerator(
				Configuration.HDOT_CONTAINER_AUTHORIZED, term,
				se.getRestrictedBps(), start);
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
		return rg;
	}

	private void terminate(long start, String term, boolean shouldExit) {
		long end = System.currentTimeMillis();

		long milliseconds = (end - start);

		long seconds = (milliseconds / 1000);

		long mins = seconds / 60;
		long restsecs = seconds % 60;

		log.info("Execution time was (in ms)  " + (end - start) + " ms.");
		log.info("Execution time was (in min) " + mins + ":" + restsecs
				+ " sec.");
	}

	private int[] computeIndexes(boolean isTopFive, int sizeOfresultList,
			String term, long start) {
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
}