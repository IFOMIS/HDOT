package org.ifomis.ontologyaggregator.sort;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import uk.ac.ebi.ontocat.OntologyServiceException;

public class TestChainComparator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Logger log = Logger.getLogger(TestChainComparator.class);
		
		long start = System.currentTimeMillis();

		try {
			OntologySorter os = new OntologySorter();
			System.out.println("Start sorting ontologies...");

			os.sortOntologyBeans();

			File logFile = new File("log/loggingSorting.html");
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
			String date = dateFormat.format(new Date());

			logFile.renameTo(new File("log/" + date
					+ "_loggingSorting.html"));
			System.out
					.println("Ontologies have been soreted.\nLog messages are written in log/"
							+ date + "_loggingSorting.html");

			
			long end = System.currentTimeMillis();
			long milliseconds = (end-start);
			long seconds = (milliseconds / 1000);
	        long mins = seconds / 60;
	        long restsecs = seconds % 60;
	        
			System.out.println("Execution time was "+(end-start)+" ms.");
			System.out.println("Execution time was "+ mins + ":" + restsecs +" sec.");
			log.info("Execution time was "+(end-start)+" ms.");
			log.info("Execution time was "+ mins + ":" + restsecs+" sec.");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OntologyServiceException e) {
			e.printStackTrace();
		}
	}

}
