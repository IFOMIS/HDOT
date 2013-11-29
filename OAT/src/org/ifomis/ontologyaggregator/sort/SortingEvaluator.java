package org.ifomis.ontologyaggregator.sort;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class SortingEvaluator {
	Logger log = Logger.getLogger(SortingEvaluator.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File newSorting = new File("config/ontologySortingC1-9.txt");
		File oldSorting = new File("config/ontologySortingC1-9_old.txt");
		File newSortingIds = new File("config/listOfOntoIds");
		File oldSortingIds = new File("config/listOfOntoIds_new");
		
		try {
			SortingEvaluator se = new SortingEvaluator();
			se.compareSortings(oldSorting, newSorting, oldSortingIds, newSortingIds);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void compareSortings(File oldSorting, File newSorting, File oldSortingIds, File newSortingIds)
			throws IOException {
		// load the sortings
		List<String> oldSortingList = FileUtils.readLines(oldSorting);
		List<String> newSortingList = FileUtils.readLines(newSorting);

		List<String> oldSortingListIds = FileUtils.readLines(newSortingIds);
		List<String> newSortingListIds = FileUtils.readLines(oldSortingIds);
		
		List<String> ontologiesNotInNewSorting = new ArrayList<String>();
		List<String> ontologiesNotInOldSorting = new ArrayList<String>();

		for (String currentOntology : oldSortingList) {
			String[] parts = currentOntology.split("=");
			String currentOntologyID = parts[parts.length-1].replace(")","");
			
			if (!newSortingListIds.contains(currentOntologyID)) {
				ontologiesNotInNewSorting.add(currentOntology);
			}
		}

		for (String currentOntology : newSortingList) {
			String[] parts = currentOntology.split("=");
			String currentOntologyID = parts[parts.length-1].replace(")","");
			
			if (!oldSortingListIds.contains(currentOntologyID)) {
				ontologiesNotInOldSorting.add(currentOntology);
			}
		}

		FileUtils.writeLines(
				new File("data/ontologiesRemovedFromBioPortal.txt"),
				ontologiesNotInNewSorting);
		FileUtils.writeLines(new File("data/ontologiesAddedInBioPortal.txt"),
				ontologiesNotInOldSorting);

		log.info("#ontologies in old sorting: " + oldSortingList.size());
		log.info("#ontologies in new sorting: " + newSortingList.size());
		log.info("#ontologies removed from BioPortal: "
				+ ontologiesNotInNewSorting.size());
		log.info("#ontologies added to BioPortal: "
				+ ontologiesNotInOldSorting.size());
		System.out
				.println("summary saved in:\n\t\t data/ontologiesAddedInBioPortal.txt\n\t\t data/ontologiesRemovedFromBioPortal.txt");
	}
}
