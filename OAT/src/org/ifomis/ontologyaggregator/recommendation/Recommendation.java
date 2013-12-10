package org.ifomis.ontologyaggregator.recommendation;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.collections.map.HashedMap;
import org.ifomis.ontologyaggregator.util.Configuration;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import uk.ac.ebi.ontocat.OntologyServiceException;
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
	private int matchedParents;
	private OntologyTerm matchedClass;
	private List<String> hitSynonyms;
	private List<OntologyTerm> hitChildren;

	private Stack<OntologyTerm> hitHierarchy;
	private OWLOntology hdot;
	private boolean includeSubclasses;
	private String importedFrom;



	public Recommendation(int hitNo, OntologyTerm hit, boolean idsMatched,
			boolean labelsMatched, String searchedTerm,
			List<OWLClass> hierarchy, Stack<OntologyTerm> hierarchyOfHit,
			OWLOntology hdot_all, OWLOntology hdotModule, int parentNo,
			OntologyTerm matchedClass, List<String> definitions,
			List<String> synonyms, List<OntologyTerm> childrenOfHit, int matchedParents, String importedFrom) {

		this.matchedParents = matchedParents;
		this.hitNo = hitNo;
		this.hit = hit;
		this.idsMatched = idsMatched;
		this.labelsMatched = labelsMatched;
		this.searchedTerm = searchedTerm;
		this.hdotHierarchy = hierarchy;
		this.hdot = hdot_all;
		this.hdotModule = hdotModule;
		this.parentNoOfHit = parentNo;
		this.matchedClass = matchedClass;
		this.hitDefinitions = definitions;
		this.hitSynonyms = synonyms;
		this.hitChildren = childrenOfHit;
		this.hitHierarchy = hierarchyOfHit;
		
		getMapOfMatchedParents();
		//back pointer to hdot module the parent was find in
		this.importedFrom = importedFrom;
	}

	@Override
	public String toString() {
		StringBuffer messageBuffer = new StringBuffer();
		messageBuffer
				.append("\n==================================RECOMMENDATION:===============================");
		messageBuffer
				.append("==========================================================================");

		messageBuffer.append("\nsearched term:" + this.searchedTerm);
		messageBuffer.append("\nhit No:" + hitNo);
		messageBuffer.append("\nnumber of matched parents:" + this.matchedParents);
		messageBuffer.append("\n\tthe hit hierarchy:");
		for (OntologyTerm parent : hitHierarchy) {
			messageBuffer.append("\n\t\t");
			messageBuffer.append(parent.getURI().toString());
			messageBuffer.append("\t");
			messageBuffer.append(parent.getLabel());
		}

		messageBuffer.append("\n\n\tthe HDOT class hierarchy:");

		for (int i = hdotHierarchy.size() - 1; i >= 0; i--) {
			OWLClass entryOfHierarchy = hdotHierarchy.get(i);
			messageBuffer.append("\n\t\t");
			messageBuffer.append(entryOfHierarchy.getIRI().toString());

			String label = "";
			// get the labels of the parents to display them
			for (OWLOntology currOnto : hdot.getImports()) {
				if (!entryOfHierarchy.getAnnotations(currOnto).isEmpty()) {
					label = retriveRdfsLabel(entryOfHierarchy
							.getAnnotations(currOnto));
					break;
				}
			}
			messageBuffer.append("\t");
			messageBuffer.append(label);

		}
		messageBuffer.append("\n\t\t");
		messageBuffer.append(matchedClass.getURI().toString());
		messageBuffer.append("\t");
		messageBuffer.append(matchedClass.getLabel());

		messageBuffer.append("\n\n\t\tparent No:" + (this.parentNoOfHit));
		messageBuffer.append("  of the current hit matched the concept:\n\t\t");
		messageBuffer.append(matchedClass.getURI().toString());
		messageBuffer.append("\t");
		messageBuffer.append(matchedClass.getLabel());

		messageBuffer
				.append("\n\n\t\tDo you want to integrate the following concept under the HDOT hierarchy displayed above:\n");

		messageBuffer
				.append("\n\t\t........................................................................................\n");
		messageBuffer.append("\t\t");
		messageBuffer.append(hit.getURI().toString());
		messageBuffer.append("\t");
		messageBuffer.append(hit.getLabel());
		try {
			messageBuffer.append("\n\n\t\t\tsource ontology: " + hit.getOntology().getLabel());
		} catch (OntologyServiceException e) {
			e.printStackTrace();
		}
		messageBuffer.append("\n\n\t\t\tdefinotion(s):\n\t\t");

		for (String def : hitDefinitions) {
			messageBuffer.append("\t\t");
			messageBuffer.append(def);
			messageBuffer.append("\n\n\t\t");
		}

		messageBuffer.append("\n\t\t\tsynonyms:\n\t\t");

		for (String syn : hitSynonyms) {
			messageBuffer.append("\t\t\t\t");
			messageBuffer.append(syn);
			messageBuffer.append("\n\t\t\t\t");
		}

		messageBuffer.append("\n\t\t\tsubClasses:\n\t\t");
		if (hitChildren != null) {
			for (OntologyTerm child : hitChildren) {
				messageBuffer.append("\n\t\t\t\t");
				messageBuffer.append(child.getURI().toString());
				messageBuffer.append("\t");
				messageBuffer.append(child.getLabel());
			}
		}
		messageBuffer
				.append("\n\t\t........................................................................................");

		messageBuffer
				.append("\n\n\t\tThe hdot module where the match was found is: ");
//		messageBuffer.append(hdotModule.getOntologyID().getOntologyIRI().toString());
		messageBuffer.append(importedFrom);
		
		messageBuffer
				.append("\n==========================================================================");
		messageBuffer
				.append("==========================================================================");

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
	
	public Stack<OntologyTerm> getHitHierarchy() {
		return hitHierarchy;
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

	public int getMatchedParents() {
		return matchedParents;
	}
	
	//TODO complete impl
	public void exportChildrenToOWLFile() throws OWLOntologyCreationException,
			OWLOntologyStorageException {
		OWLOntologyManager ontologyManager = OWLManager
				.createOWLOntologyManager();
		OWLDataFactory dataFactory = ontologyManager.getOWLDataFactory();

		IRI ontologyIRI = IRI.create("http://www.ifomis.org/hdot/"
				+ hit.getOntologyAccession() + "_subclasses");

		// Now create the ontology - we use the ontology IRI (not the
		// physical
		// IRI)
		OWLOntology owlFileThatConsinsSubclasses = ontologyManager
				.createOntology(ontologyIRI);

		OWLClass classHIT = dataFactory.getOWLClass(IRI.create(hit.getURI()));

		OWLAxiom axiom = dataFactory.getOWLSubClassOfAxiom(classHIT,
				dataFactory.getOWLThing());

		AddAxiom addAxiom = new AddAxiom(owlFileThatConsinsSubclasses, axiom);
		ontologyManager.applyChange(addAxiom);

		for (OntologyTerm child : hitChildren) {

			OWLAxiom childAxiom = dataFactory.getOWLSubClassOfAxiom(
					dataFactory.getOWLClass(IRI.create(child.getURI())),
					classHIT);
			AddAxiom childAddAxiom = new AddAxiom(owlFileThatConsinsSubclasses,
					childAxiom);
			ontologyManager.applyChange(childAddAxiom);

		}
		// extract the path to the physical documents from the ontology iri
		// String[] partsOfPath = ontology_manager
		// .getOntologyDocumentIRI(hdot_all).toString().split("/");
		// String pathToModules = "";
		//
		// for (int i = 0; i < partsOfPath.length - 1; i++) {
		// pathToModules += partsOfPath[i] + "/";
		//
		// }
//		ontologyManager.saveOntology(
//				owlFileThatConsinsSubclasses,
//				IRI.create(new File(Configuration.DATA_PATH.resolve(hit.getLabel().replace(" ", "-")
//						+ "_subclasses.owl").toURI())));
	}

	public boolean includeSubclasses() {
		return this.includeSubclasses;
	}
	public boolean setIncludeSubclasses(boolean includeOrNot) {
		return this.includeSubclasses = includeOrNot;
	}

	public String getURIOfModuleForURIGeneration() {
		String uriOfModuleForURIGeneration;
		if(!importedFrom.isEmpty()){
			uriOfModuleForURIGeneration = importedFrom;
		}else{
			uriOfModuleForURIGeneration = hdotModule.getOntologyID().getOntologyIRI().toString();
		}
		return uriOfModuleForURIGeneration;
	}

	public List<String> getHitSynonyms() {
		return hitSynonyms;
	}

	public OWLOntology getHdot() {
		return hdot;
	}

	public Map<OntologyTerm, OWLClass> getMapOfMatchedParents() {
		Map<OntologyTerm, OWLClass> mapOfMatchedParents = new HashMap<>();
		for (OntologyTerm ot : hitHierarchy) {
			for (OWLClass oc : hdotHierarchy) {
			
				if (ot.getLabel().toLowerCase().equals(retriveRdfsLabel(oc
										.getAnnotations(hdotModule))) || ot.equals(oc.getIRI().toURI().toString())) {
					mapOfMatchedParents.put(ot, oc);
				}
			}
		}
		return mapOfMatchedParents;
	}
	
}