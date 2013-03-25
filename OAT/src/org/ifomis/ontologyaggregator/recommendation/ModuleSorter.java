package org.ifomis.ontologyaggregator.recommendation;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Sorts the HDOT modules using an external list that predefines the order.
 * 
 * @author Nikolina
 * 
 */
public class ModuleSorter {

	/**
	 * Sorts the HDOT modules. The file data/sortedHdotModuleIds contain the sorted
	 * module IDs.
	 * 
	 * @param hdotModules unsorted modules of HDOT
	 * @return array with sorted HDOT modules
	 * @throws IOException
	 */
	public OWLOntology[] sortHdotModules(Set<OWLOntology> hdotModules)
			throws IOException {

		OWLOntology[] sortedHdotModules = new OWLOntology[hdotModules.size()];
		try {
			List<String> sortedIds = FileUtils.readLines(new File(
					"data/sortedHdotModuleIds"));

			for (OWLOntology owlOntology : hdotModules) {
				String currentId = owlOntology.getOntologyID().getOntologyIRI().toString();
				System.out.println("sorted ids " + sortedIds);
				System.out.println("sorted modules " + sortedHdotModules.toString());
				System.out.println(sortedIds.indexOf(currentId));
				System.out.println("current id " + currentId);
				sortedHdotModules[sortedIds.indexOf(currentId)] = owlOntology;
			}
			// System.out.println("Sorted HDOT modules:");
			// for (int i = 0; i < sortedHdotModules.length; i++) {
			// OWLOntology module = sortedHdotModules[i];
			// System.out.println("module Nr: " + i +"is" + module);
			// }
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sortedHdotModules;
	}
}
