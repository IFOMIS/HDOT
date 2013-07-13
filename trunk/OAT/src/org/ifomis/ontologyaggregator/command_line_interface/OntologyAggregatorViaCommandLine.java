/**
 * 
 */
package org.ifomis.ontologyaggregator.command_line_interface;

import org.ifomis.ontologyaggregator.recommendation.Recommendation;
import org.ifomis.ontologyaggregator.workflow.OntologyAggregatorWorkflow;

/**
 * Extends the workflow and integrates a command line interface.
 * 
 * @author Nikolina
 * 
 */
public class OntologyAggregatorViaCommandLine extends
		OntologyAggregatorWorkflow {

	private Recommendation recommendation;

	public OntologyAggregatorViaCommandLine(String terms, String userId,
			boolean userRights) throws Exception {
		super(terms, userId, userRights);

	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String terms = "";
		if (args.length < 3) {
			usage();
			System.exit(0);
		} else {
			terms = args[0].replace("_", " ");
		}
		OntologyAggregatorViaCommandLine oat = new OntologyAggregatorViaCommandLine(
				terms, args[1], Boolean.parseBoolean(args[2]));
	}

	@Override
	public boolean[] checkUserInput() {
		boolean[] result = new boolean[2];

		UserInputReader inputReader = new UserInputReader();
		RecommendationAcceptListenerImpl inputListener = new RecommendationAcceptListenerImpl();
		inputReader.addUserInputListener(inputListener);
		inputReader.startListeningAcceptInput();

		result[0] = inputListener.isAccept();
System.out.println(recommendation.getHitChildren()  + "****");
		if (inputListener.isAccept()
				&& (recommendation.getHitChildren() != null ) ) {
			inputReader.startListeningIncludeSubclassesInput();
		}
		result[1] = inputListener.isIncludeSubclasses();

		return result;
	}

	@Override
	public void displayRecommendation(Recommendation recommendation) {
		this.recommendation = recommendation;
		System.out.println(recommendation.toString());
	}

	private static void usage() {
		System.out
				.println("usage: java -jar OntologyAggregator.jar <term1;term2...> <userID> <userRights>\nyou can specify one or more terms\nnote: terms that have more than one word have to be written in quotes, e.g. \"bone marrow\"\n the userRights are true if the user can directly modify HDOT and false otherwise");
	}
}
