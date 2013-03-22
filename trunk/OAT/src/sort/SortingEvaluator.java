package sort;

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
		File newSorting = new File("data/sortingC1-9.txt");
		File oldSorting = new File("data/sortingC1-9_old.txt");
		try {

			SortingEvaluator se = new SortingEvaluator();
			se.compareSortings(oldSorting, newSorting);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void compareSortings(File oldSorting, File newSorting)
			throws IOException {
		// load the sortings
		List<String> oldSortingList = FileUtils.readLines(oldSorting);
		List<String> newSortingList = FileUtils.readLines(newSorting);

		List<String> ontologiesNotInNewSorting = new ArrayList<String>();
		List<String> ontologiesNotInOldSorting = new ArrayList<String>();

		for (String currentOntology : oldSortingList) {
			if (!newSortingList.contains(currentOntology)) {
				ontologiesNotInNewSorting.add(currentOntology);
			}
		}

		for (String currentOntology : newSortingList) {
			if (!oldSortingList.contains(currentOntology)) {
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
