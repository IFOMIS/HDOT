/**
 * 
 */
package org.ifomis.ontologyaggregator.command_line_interface;

import java.util.Arrays;

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

	public OntologyAggregatorViaCommandLine() throws Exception {
		super();

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
		OntologyAggregatorViaCommandLine oat = new OntologyAggregatorViaCommandLine();
		oat.start(Arrays.asList(terms.split(",")), args[1], Boolean.parseBoolean(args[2]));
	}

	@Override
	public boolean checkUserInput() {

		UserInputReader inputReader = new UserInputReader();
		RecommendationAcceptListenerImpl inputListener = new RecommendationAcceptListenerImpl();
		inputReader.addUserInputListener(inputListener);
		inputReader.startListeningAcceptInput();

		if (inputListener.isAccept()
				&& (recommendation.getHitChildren() != null ) ) {
			inputReader.startListeningIncludeSubclassesInput();
			recommendation.setIncludeSubclasses(inputListener.isIncludeSubclasses());

			//			return inputListener.isIncludeSubclasses();
		}
		return inputListener.isAccept();
	}

	@Override
	public void displayRecommendation(Recommendation recommendation) {
		this.recommendation = recommendation;
		System.out.println(recommendation.toString());
	}

	private static void usage() {
		System.out
				.println("usage: java -jar OntologyAggregator.jar <term1,term2...> <userID> <userRights>\nyou can specify one or more terms\nnote: terms that have more than one word have to be written in quotes, e.g. \"bone marrow\"\n the userRights are true if the user can directly modify HDOT and false otherwise");
	}
}
