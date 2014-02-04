package org.ifomis.ontologyaggregator.util;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotation;

public class OWLUtilities {

	/**
	 * Selects the rdfs:label from the given set of annotations.
	 * 
	 * @param annotations
	 *            set of @link{OWLAnnotation}
	 * @return the rdfs:label
	 */
	public static String retriveRdfsLabel(Set<OWLAnnotation> annotations) {
		String pureLabelOfClass = "";
		// get all the annotations of the current class and extract the
		// label
		for (OWLAnnotation owlAnnotation : annotations) {
			// get just the rdfs: label annotations
			if (owlAnnotation.toString().contains("rdfs:label")) {
				pureLabelOfClass = owlAnnotation.getValue().toString()
						.split("\"")[1];
			}
		}
		return pureLabelOfClass;
	}
	/**
	 * Selects the hasSynonym relation from the given set of annotations.
	 * 
	 * @param annotations
	 *            set of @link{OWLAnnotation}
	 * @return the rdfs:label
	 */
	public static String[] retriveHasSynonyms(Set<OWLAnnotation> annotations) {
		String[] synonyms = null;
		// get all the annotations of the current class and extract the
		// label
		for (OWLAnnotation owlAnnotation : annotations) {
			// get just the rdfs: label annotations
			if (owlAnnotation.toString().contains("hasSynonym")) {
				// System.out.println("************");
				// System.out.println(owlAnnotation.toString());
				// System.out.println(owlAnnotation.getValue().toString().split("\"")[1]);
				// System.out.println(owlAnnotation.getValue().toString().split("\"")[1]
				// .split(";"));
				// if(owlAnnotation.getValue().toString().split("\"")[1].equals("biospecimen"))
				// log.debug("check and debug");

				synonyms = owlAnnotation.getValue().toString().split("\"")[1]
						.split(";");
			}
		}
		return synonyms;
	}
}
