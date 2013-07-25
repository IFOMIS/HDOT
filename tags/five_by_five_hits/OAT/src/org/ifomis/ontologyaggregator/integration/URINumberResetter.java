package org.ifomis.ontologyaggregator.integration;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


/**
 * Resets the numbers used for the generation of new URIs.
 * 
 * @author Nikolina
 *
 */
public class URINumberResetter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
    	Properties properties = new Properties();

		try {
    		properties.load(new FileInputStream("data/counter.properties"));

    		properties.setProperty("counterForURIS_HDOT_PEM", "046");
    		properties.setProperty("counterForURIS_HDOT_PFM", "108");
    		properties.setProperty("counterForURIS_HDOT_PM", "0068");
    		properties.setProperty("counterForURIS_HDOT_CORE", "045");
    		properties.setProperty("counterForURIS_HDOT_BSDS", "104");
    		properties.store(new FileOutputStream("data/counter.properties"), null);
    		
			System.out.println("URI numbers are  reset.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
