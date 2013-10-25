package org.ifomis.ontologyaggregator.recommendation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;
import org.ifomis.ontologyaggregator.notifications.EmailSender;
import org.ifomis.ontologyaggregator.recommendation.sort.RecommendationSorter;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

/**
 * The Recommendation filter sorts the generated recommendations.
 * 
 * @author Nikolina
 * 
 */
public class RecommendationFilter {

	private static final Logger log = Logger
			.getLogger(RecommendationFilter.class);

	private List<Recommendation> validRecommendations;

	private List<Recommendation> recommendationsInImportedOntologies;

	private List<Recommendation> recommendationsOfImportedNotLeafMatches;
	private List<Recommendation> inCoreNotLeafs;

	private EmailSender mailSender;

	private String searchedTerm;

	public RecommendationFilter(String searchedTerm,
			List<Recommendation> validRecommendations,
			List<Recommendation> recommendationsInImportedOntologies,
			List<Recommendation> recommendationsOfImportedNotLeafMatches,
			List<Recommendation> inCoreNotLeafs) {

		this.validRecommendations = validRecommendations;
		this.recommendationsInImportedOntologies = recommendationsInImportedOntologies;
		this.recommendationsOfImportedNotLeafMatches = recommendationsOfImportedNotLeafMatches;
		this.inCoreNotLeafs = inCoreNotLeafs;
		this.searchedTerm = searchedTerm;
		this.mailSender = new EmailSender();
	}

	/**
	 * checks if there are valid recommendations
	 * 
	 * @throws OWLOntologyCreationException
	 * @throws OWLOntologyStorageException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws EmailException
	 */
	public List<Recommendation> checkValidRecommendations()
			throws OWLOntologyCreationException, OWLOntologyStorageException,
			FileNotFoundException, IOException, EmailException {
		Recommendation validRecommendation = null;

		if (!validRecommendations.isEmpty()) {

			if (validRecommendations.size() == 1) {
				log.info("SINGLE RECOMMENDATION");

				validRecommendation = validRecommendations.get(0);

				// log.info(validRecommendation.toString());
				if (!(validRecommendation.getHitChildren() == null)) {
					validRecommendation.exportChildrenToOWLFile();
				}

			} else {
				// sort recommendations and pick the first one
				RecommendationSorter recommendationSorter = new RecommendationSorter();
				validRecommendations = recommendationSorter
						.sortRecommendations(validRecommendations);

				log.info(validRecommendations.size()
						+ " RECOMMENDATIONS WERE GENERATED");

				// validRecommendation = validRecommendations.get(0);
			}
		} else {
			checkPotentialRecommendations();
		}
		return validRecommendations;
	}

	/**
	 * checks if there are potential recommendations and notifies the curators.
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws EmailException
	 */
	public void checkPotentialRecommendations() throws FileNotFoundException,
			IOException, EmailException {
		if (recommendationsInImportedOntologies.isEmpty()
				&& recommendationsOfImportedNotLeafMatches.isEmpty()
				&& inCoreNotLeafs.isEmpty()) {
			mailSender
					.sendMail(
							searchedTerm + " NO SUITABLE RECOMMENDATION FOUND",
							"The user searched for the term \""
									+ searchedTerm
									+ "\" but there was no suitable recommendation found.");
		}
		if (!recommendationsInImportedOntologies.isEmpty()) {

			String subject = searchedTerm
					+ " INTEGRATION POSSIBLE IN IMPORTED ONTOLOGY";
			log.info(subject);

			StringBuffer mailBuffer = new StringBuffer();

			int i = 1;
			log.info("recomendation(s) in imported ontologies:");

			for (Recommendation recommendation : recommendationsInImportedOntologies) {
				log.info("potential recomendation No " + i + ":\n"
						+ recommendation.toString());
				mailBuffer.append("----------------------------------------\n");
				mailBuffer.append("potential recommendation No " + i + ":\n");
				mailBuffer.append(recommendation.toString());
				++i;
			}
			mailSender.sendMail(subject, mailBuffer.toString());
		}

		if (!recommendationsOfImportedNotLeafMatches.isEmpty()) {
			StringBuffer mailBuffer = new StringBuffer();

			String subject = searchedTerm
					+ " THE MATCHED CLASS IS IMPORTED IN THIS MODULE BUT IT IS NOT A LEAF NODE";
			log.info(subject);
			int i = 1;
			log.info("recomendation(s) in imported classes that are not leaf nodes:");

			for (Recommendation recommendation : recommendationsOfImportedNotLeafMatches) {
				log.info("potential recomendation No " + i + ":\n"
						+ recommendation.toString());
				mailBuffer.append("----------------------------------------\n");
				mailBuffer.append("potential recommendation No " + i + ":\n");
				mailBuffer.append(recommendation.toString());
				++i;
			}
			mailSender.sendMail(subject, mailBuffer.toString());
		}
		if (!inCoreNotLeafs.isEmpty()) {
			StringBuffer mailBuffer = new StringBuffer();
			String subject = searchedTerm
					+ " THE MATCHED CLASS WAS FOUND IN HDOT_CORE BUT IT IS NOT A LEAF NODE";
			log.info(subject);
			log.info("recomendation(s) in HDOT_CORE but not leaf node:");

			int i = 1;
			for (Recommendation recommendation : inCoreNotLeafs) {
				log.info("potential recomendation No " + i + ":\n"
						+ recommendation.toString());
				mailBuffer.append("----------------------------------------\n");
				mailBuffer.append("potential recommendation No " + i + ":\n");
				mailBuffer.append(recommendation.toString());
				mailBuffer.append("\n");
				++i;
			}
			mailSender.sendMail(subject, mailBuffer.toString());
		}
	}

	public List<Recommendation> getValidRecommendations() {
		return validRecommendations;
	}

	public List<Recommendation> getPotentialRecommendations() {
		List<Recommendation> potentialRecommendations = new ArrayList<Recommendation>();
		potentialRecommendations.addAll(inCoreNotLeafs);
		potentialRecommendations.addAll(recommendationsInImportedOntologies);
		potentialRecommendations
				.addAll(recommendationsOfImportedNotLeafMatches);
		return potentialRecommendations;
	}
}