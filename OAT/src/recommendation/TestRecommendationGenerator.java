package recommendation;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import search.SearchEngine;
import uk.ac.ebi.ontocat.OntologyServiceException;
import uk.ac.ebi.ontocat.OntologyTerm;

/**
 * Tests the @link{RecommendationGenerator}
 * 
 * @author Nikolina
 * 
 */
public class TestRecommendationGenerator {

	private static final Logger log = Logger
			.getLogger(TestRecommendationGenerator.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long start = System.currentTimeMillis();

		String fileWithOntologies, fileWithTerms;

		if (args.length < 1) {
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

				// TODO ontoIn
				RecommendationGenerator rg = new RecommendationGenerator(
						"data/hdot/hdot_all.owl", "", se.getListOfPaths()
								.subList(0, 5), term, se.getRestrictedBps(),
						start);

				RecommendationFilter rf = new RecommendationFilter(term,
						rg.getListOfRecommendations(),
						rg.getListOfRecsPossibleInCoreOfHDOT(),
						rg.getListImportedNotLeafMatches(),
						rg.getListOfInCoreNotLeafMatches());

				// TODO get the value by checking which button was pressed
				// yes/no
				while (!rg.getListOfRecommendations().isEmpty()) {
					rf.checkValidRecommendations();
					UserInputReader inputReader = new UserInputReader();
					inputReader.addUserInputListener(rf);
					inputReader.start();

					if (rf.isAccept()) {
						break;
					}
				}

				if (!rf.isAccept()) {
					RecommendationGenerator rgSecond = new RecommendationGenerator(
							"data/hdot/hdot_all.owl", "", se.getListOfPaths()
									.subList(5, 10), term,
							se.getRestrictedBps(), start);

					RecommendationFilter rfSecond = new RecommendationFilter(
							term, rgSecond.getListOfRecommendations(),
							rgSecond.getListOfRecsPossibleInCoreOfHDOT(),
							rgSecond.getListImportedNotLeafMatches(),
							rgSecond.getListOfInCoreNotLeafMatches());
					rfSecond.checkValidRecommendations();

					while (!rg.getListOfRecommendations().isEmpty()) {
						rf.checkValidRecommendations();
						UserInputReader inputReader = new UserInputReader();
						inputReader.addUserInputListener(rf);
						inputReader.start();

						if (rf.isAccept()) {
							break;
						}
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
		}
	}

	private static void usage() {
		System.out
				.println("usage: java -jar OntologyAggregator.jar <term1;term2...>\nyou can specify one or more terms\nnote: terms that have more than one word have to be written in quotes, e.g. \"bone marrow\"");
	}
}
