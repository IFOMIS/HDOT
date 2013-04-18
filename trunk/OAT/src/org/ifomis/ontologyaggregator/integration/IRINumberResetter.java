package org.ifomis.ontologyaggregator.integration;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class IRINumberResetter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			FileUtils.writeStringToFile(
					new File("data/counterForURIS_HDOT_PEM"), "046");

			FileUtils.writeStringToFile(
					new File("data/counterForURIS_HDOT_PFM"), "108");

			FileUtils.writeStringToFile(
					new File("data/counterForURIS_HDOT_PM"), "0068");
			FileUtils.writeStringToFile(new File(
					"data/counterForURIS_HDOT_CORE"), "045");
			FileUtils.writeStringToFile(new File(
					"data/counterForURIS_HDOT_BSDS"), "104");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
