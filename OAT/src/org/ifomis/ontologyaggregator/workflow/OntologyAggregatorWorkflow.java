package org.ifomis.ontologyaggregator.workflow;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;
import org.ifomis.ontologyaggregator.integration.HDOTExtender;
import org.ifomis.ontologyaggregator.notifications.EmailSender;
import org.ifomis.ontologyaggregator.recommendation.Recommendation;
import org.ifomis.ontologyaggregator.recommendation.RecommendationFilter;
import org.ifomis.ontologyaggregator.recommendation.RecommendationGenerator;
import org.ifomis.ontologyaggregator.search.SearchEngine;
import org.ifomis.ontologyaggregator.util.Configuration;
import org.ifomis.ontologyaggregator.util.StatisticsPrinter;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

/**
 * Implements the workflow of the Ontology Aggregator. Two underspecified
 * methods that correspond to the integration with the interface.
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

	private SearchEngine se;

	public OntologyAggregatorWorkflow() throws Exception {
		this.start = System.currentTimeMillis();

		this.mailSender = new EmailSender();
		// 0. load configuration
		Configuration.getInstance();
	}

	public void start(List<String> terms, String userId, boolean userRights)
			throws Exception {

		for (String term : terms) {
			this.term = term;
			
			log.info("Search for term " + term + " in BioPortal");
			// 1. search term
			if (searchTerm()) {
				log.info("list of paths with hits is empty or no paths extracted");
				break;
			}
			
			// 2. generate recommendations
			RecommendationGenerator rg = new RecommendationGenerator(term,
					se.getRestrictedBps(), start);

			rg.generateRecommendations(se.getListOfPaths());

			// 3. sort the recommendations
			List<Recommendation> recommendations = fiterGeneratedRecommendations(rg);

			if (recommendations.isEmpty()) {
				log.info("NO RECOMMENDATIONS ARE GENERATED FOR THE TERM: "
						+ term);
				break;
			}
			while (!recommendations.isEmpty()) {

				// 4. display recommendation to the user
				Recommendation topRecommendation = recommendations.get(0);
				displayRecommendation(topRecommendation);

				// 5. check the user input
				boolean confirmed = checkUserInput();

				// 6. extend HDOT
				if (confirmed) {
					HDOTExtender hdotExtender = new HDOTExtender(
							rg.getOntologyService(), userId, userRights);
					hdotExtender.integrarteHitInHDOT(topRecommendation);
					break;
				} else {
					mailSender.sendMail("THE USER REJECTED THE RECOMMENDATION",
							topRecommendation.toString());
					recommendations.remove(0);
				}
			}
		}
		StatisticsPrinter.printFinalTimeAndLogLocations(start, term);
	}

	/**
	 * Searches term in BioPortal
	 * @return
	 * @throws Exception
	 */
	private boolean searchTerm() throws Exception {
		IRI fileWithOntologies = Configuration.ONTO_IDS_FILE;
		se = new SearchEngine(fileWithOntologies);
		return se.searchTermInBioPortal(term);
	}

	/**
	 * @return a boolean array in the first position the value if the
	 *         recommendation was accepted is contained and in the second
	 *         position whether the subClasses should be integrated too.
	 */
	public abstract boolean checkUserInput();

	/**
	 * @param recommendation
	 *            the recommendation that will be shown the user
	 */
	public abstract void displayRecommendation(Recommendation recommendation);

	private List<Recommendation> fiterGeneratedRecommendations(
			RecommendationGenerator rg) throws OWLOntologyCreationException,
			OWLOntologyStorageException, FileNotFoundException, IOException,
			EmailException {
		RecommendationFilter rf = new RecommendationFilter(term,
				rg.getListOfRecommendations(),
				rg.getListOfRecsPossibleInCoreOfHDOT(),
				rg.getListImportedNotLeafMatches(),
				rg.getListOfInCoreNotLeafMatches());

		return rf.checkValidRecommendations();
	}
}