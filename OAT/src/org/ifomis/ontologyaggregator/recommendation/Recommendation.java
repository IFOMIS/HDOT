package org.ifomis.ontologyaggregator.recommendation;

import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

import uk.ac.ebi.ontocat.OntologyTerm;

/**
 * Represents the recommendation for the integration of a hit found in BioPortal
 * under HDOT.
 * 
 * @author Nikolina
 * 
 */
public class Recommendation {

	private int hitNo;
	private OntologyTerm hit;

	private List<String> hitDefinitions;
	private boolean idsMatched;
	private boolean labelsMatched;
	private String searchedTerm;
	private List<OWLClass> hdotHierarchy;
	private OWLOntology hdotModule;
	private int parentNoOfHit;
	private OntologyTerm matchedClass;
	private List<String> hitSynonyms;
	private List<OntologyTerm> hitChildren;
	

	private Stack<OntologyTerm> hitHierarchy;

	public Stack<OntologyTerm> getHitHierarchy() {
		return hitHierarchy;
	}

	public Recommendation(int hitNo, OntologyTerm hit, boolean idsMatched,
			boolean labelsMatched, String searchedTerm,
			List<OWLClass> hierarchy, Stack<OntologyTerm> hierarchyOfHit, OWLOntology hdotModule, int parentNo,
			OntologyTerm matchedClass, List<String> definitions,
			List<String> synonyms, List<OntologyTerm> childrenOfHit) {

		this.hitNo = hitNo;
		this.hit = hit;
		this.idsMatched = idsMatched;
		this.labelsMatched = labelsMatched;
		this.searchedTerm = searchedTerm;
		this.hdotHierarchy = hierarchy;
		this.hdotModule = hdotModule;
		this.parentNoOfHit = parentNo;
		this.matchedClass = matchedClass;
		this.hitDefinitions = definitions;
		this.hitSynonyms = synonyms;
		this.hitChildren = childrenOfHit;
		this.hitHierarchy = hierarchyOfHit;
	}

	@Override
	public String toString() {
		StringBuffer messageBuffer = new StringBuffer();

		messageBuffer.append("searched term:" + this.searchedTerm);
		messageBuffer.append("\nhit No:" + hitNo);
		messageBuffer.append("\nRECOMMENDATION:");
		messageBuffer.append("\n\thierarchy of the HDOT class:");

		for (int i = hdotHierarchy.size() - 1; i >= 0; i--) {
			OWLClass entryOfHierarchy = hdotHierarchy.get(i);
			messageBuffer.append("\n\t\t");
			messageBuffer.append(entryOfHierarchy.toString());

			String label = retriveRdfsLabel(entryOfHierarchy
					.getAnnotations(hdotModule));
			messageBuffer.append("\t");
			messageBuffer.append(label);

		}
		messageBuffer.append("\n\n\t\tparent No:" + (this.parentNoOfHit));
		messageBuffer.append("  of the current hit matched the concept:\n\t\t");
		messageBuffer.append(matchedClass.getURI().toString());
		messageBuffer.append("\t");
		messageBuffer.append(matchedClass.getLabel());

		messageBuffer
				.append("\n\n\t\tDo you want to integrate the following term under the hierarchy displayed above:\n\t\t");
		messageBuffer.append(hit.getURI().toString());
		messageBuffer.append("\t");
		messageBuffer.append(hit.getLabel());

		messageBuffer.append("\n\n\tdefinotion(s):\n\t\t");

		for (String def : hitDefinitions) {
			messageBuffer.append(def);
			messageBuffer.append("\n\t\t");
		}

		messageBuffer.append("\n\tsynonyms:\n\t\t");

		for (String syn : hitSynonyms) {
			messageBuffer.append(syn);
			messageBuffer.append("\n\t\t");
		}

		messageBuffer.append("\n\tsubClasses:\n\t\t");
		if (hitChildren != null) {
			for (OntologyTerm child : hitChildren) {
				messageBuffer.append(child);
			}
		}
		messageBuffer.append("\n\tthe hieratchy of the hit:\n\t\t");
		for (OntologyTerm parent : hitHierarchy) {
			messageBuffer.append("\n\t\t");
			messageBuffer.append(parent.getURI());
			messageBuffer.append("\t");
			messageBuffer.append(parent.getLabel());
		}
		messageBuffer
				.append("\n\n\t\tThe hdot module where the match was found is: ");
		messageBuffer.append(hdotModule);

		return messageBuffer.toString();
	}

	/**
	 * Selects the rdfs:label from the given set of annotations.
	 * 
	 * @param annotations
	 *            set of @link{OWLAnnotation}
	 * @return the rdfs:label
	 */
	private String retriveRdfsLabel(Set<OWLAnnotation> annotations) {
		String pureLabelOfClass = "";
		// get all the annotations of the current class and extract the
		// label
		for (OWLAnnotation owlAnnotation : annotations) {
			// get just the rdfs: label annotations
			if (owlAnnotation.toString().contains("rdfs:label")) {
				// log.info("label of currentClass: " +
				// owlAnnotation.getValue());

				pureLabelOfClass = owlAnnotation.getValue().toString()
						.split("\"")[1];
				// log.info("pureLabelOfClass: " + pureLabelOfClass);
			}
		}
		return pureLabelOfClass;
	}

	public List<String> getHitDefinition() {
		return hitDefinitions;
	}

	public void setHitDefinition(List<String> hitDefinition) {
		this.hitDefinitions = hitDefinition;
	}

	public int getHitNo() {
		return hitNo;
	}

	public OntologyTerm getHit() {
		return hit;
	}

	public boolean isIdsMatched() {
		return idsMatched;
	}

	public boolean isLabelsMatched() {
		return labelsMatched;
	}

	public List<OWLClass> getHdotHierarchy() {
		return hdotHierarchy;
	}

	public OWLOntology getHdotModule() {
		return hdotModule;
	}

	public int getParentNo() {
		return parentNoOfHit;
	}

	public OntologyTerm getMatchedClass() {
		return matchedClass;
	}
	public List<OntologyTerm> getHitChildren() {
		return hitChildren;
	}
}
