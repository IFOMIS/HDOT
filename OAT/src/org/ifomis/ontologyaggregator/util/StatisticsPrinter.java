package org.ifomis.ontologyaggregator.util;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class StatisticsPrinter {

	private static Logger log = Logger.getLogger(StatisticsPrinter.class);

	private static DateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd_HH-mm");
	private static String date = dateFormat.format(new Date());

	public static void printFinalTimeAndLogLocations(long start,
			String searchedTerm) {
		long end = System.currentTimeMillis();

		long milliseconds = (end - start);

		long seconds = (milliseconds / 1000);

		long mins = seconds / 60;
		long restsecs = seconds % 60;

		log.info("Execution time was " + (end - start) + " ms.");
		log.info("Execution time was " + mins + ":" + restsecs + " sec.");

		File logSearchFile = new File(Configuration.LOG_PATH.resolve(
				"loggingSearchEngine.html").toURI());

		File logRecommendFile = new File(Configuration.LOG_PATH.resolve(
				"loggingRecommendationGeneration.html").toURI());

		logSearchFile.renameTo(new File(Configuration.LOG_PATH.resolve(
				date + "_" + searchedTerm.replace(" ", "_")
						+ "_loggingSearchEngine.html").toURI()));

		logRecommendFile.renameTo(new File(Configuration.LOG_PATH.resolve(
				date + "_" + searchedTerm.replace(" ", "_")
						+ "_loggingRecommendationGeneration.html").toURI()));

		log.info("Done.");
		log.info("Log messages written in: "
				+ Configuration.LOG_PATH.toString() + date + "_"
				+ searchedTerm.replace(" ", "_")
				+ "_loggingSearchEngine.html and " + date + "_"
				+ searchedTerm.replace(" ", "_")
				+ "_loggingRecommendationGeneration.html");
	}

	public static void writeSuccessPathsInExternalFile(String term,
			String content) throws IOException {
		FileUtils.write(
				new File(Configuration.SPARQL_OUTPUT_PATH.resolve(
						"success/" + date + "_" + term.replace(" ", "_")
								+ "_success").toURI()), content);

	}

	public static void writeFailPathsInExternalFile(String searchedTerm,
			String content) throws IOException {

		FileUtils.write(
				new File(Configuration.SPARQL_OUTPUT_PATH.resolve(
						"fail/" + date + "_" + searchedTerm.replace(" ", "_")
								+ "_fail").toURI()), content);
	}
}
