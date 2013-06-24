package org.ifomis.ontologyaggregator.recommendation;

import java.util.List;

import org.apache.log4j.Logger;
import org.ifomis.ontologyaggregator.notifications.EmailSender;
import org.ifomis.ontologyaggregator.recommendation.sort.RecommendationSorter;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class RecommendationFilter implements RecommendationAcceptListener {

	private static final Logger log = Logger
			.getLogger(RecommendationFilter.class);

	private List<Recommendation> validRecommendations;
	private List<Recommendation> recommendationsInImportedOntologies;

	private List<Recommendation> recommendationsOfImportedNotLeafMatches;
	private List<Recommendation> inCoreNotLeafs;

	private EmailSender mailSender;

	private String searchedTerm;

	private boolean accept = false;

	private Recommendation acceptedRecommendation;

	private boolean includeSubclasses = false;

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

	public void checkValidRecommendations()
			throws OWLOntologyCreationException, OWLOntologyStorageException {

		if (!validRecommendations.isEmpty()) {

			if (validRecommendations.size() == 1) {
				log.info("SINGLE RECOMMENDATION");

				Recommendation recommendation = validRecommendations.get(0);

				log.info(recommendation.toString());
				if (!(recommendation.getHitChildren() == null)) {
					recommendation.exportChildrenToOWLFile();
				}

			} else {
				// sort recommendations and pick the first one
				RecommendationSorter recommendationSorter = new RecommendationSorter();
				recommendationSorter.sortRecommendations(validRecommendations);

				log.info(validRecommendations.size()
						+ " RECOMMENDATIONS WERE GENERATED");

				log.info(validRecommendations.get(0).toString());

				// int i = 0;
				// for (Recommendation rec : validRecommendations) {
				// ++i;
				// log.info("recommendation No:" + i);
				// log.info(rec.toString());
				// }
			}
		} else {
			log.info("no valid recommendations were generated");
			checkPotentialRecommendations();
		}
	}

	/**
	 * checks if there are potential recommendations and notifies the curators.
	 */
	public void checkPotentialRecommendations() {
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
			for (Recommendation recommendation : recommendationsInImportedOntologies) {
				log.info("recomendation in imported ontologies:\n"
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

			for (Recommendation recommendation : recommendationsOfImportedNotLeafMatches) {
				log.info(recommendation.toString());
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

	@Override
	public void readInputAccept(
			RecommendationAcceptEvent recommendationAcceptEvent) {
		if (recommendationAcceptEvent.getAccept().equalsIgnoreCase("yes")) {
			log.info("the user accepted the recommendation.");
			accept = true;

			acceptedRecommendation = validRecommendations.get(0);
		} else {
			validRecommendations.remove(0);
			log.info("the user rejected the recommendation.");
			accept = false;
		}
	}

	@Override
	public void readInputIncludeSubclasses(
			IncludeSubClassesEvent includeClassesEvent) {
		if (includeClassesEvent.getIncludeSubClasses().equalsIgnoreCase("yes")) {
			log.info("the user wants to include the subClasses of the term.");
			this.includeSubclasses = true;
		} else {
			log.info("the user does not want to include the subClasses of the term.");
			this.includeSubclasses = false;
		}
	}

	public boolean isIncludeSubclasses() {
		return includeSubclasses;
	}

	public Recommendation getAcceptedRecommendation() {
		return acceptedRecommendation;
	}

	public boolean isAccept() {
		return accept;
	}

}
