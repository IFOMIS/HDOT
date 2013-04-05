package org.ifomis.ontologyaggregator.integration;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.ifomis.ontologyaggregator.exception.HdotExtensionException;
import org.ifomis.ontologyaggregator.recommendation.Recommendation;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import uk.ac.ebi.ontocat.OntologyServiceException;

/**
 * Extends HDOT with the class that is recommended and confirmed by the user.
 * @author Nikolina
 *
 */
public class HDOTExtender {

	private HDOTURIGenerator uriGenerator;
	private HDOTURIManager uriManager;
	private HDOTVerifier hdotVerifier;
	private Recommendation acceptedRecommendation;
	private static final Logger log = Logger
			.getLogger(HDOTExtender.class);
	/**
	 * the data factory for the hdot ontology
	 */
	private OWLDataFactory dataFactory;

	/**
	 * the outgoing ontology, where the recommended class is going to be
	 * inserted
	 */
	private IRI ontoOut;
	private OWLOntologyManager ontology_manager;
	private OWLOntology hdot_ontology;
	private String pathToModules;
	private OWLClass hitForIntegration;

	public HDOTExtender(Recommendation accceptedRecommendation,
			boolean includeSubclasses, OWLOntologyManager ontology_manager,
			OWLOntology hdot_ontology) throws OntologyServiceException,
			IOException, OWLOntologyStorageException, URISyntaxException,
			HdotExtensionException {

		this.ontoOut = ontology_manager.getOntologyDocumentIRI(hdot_ontology);
		this.ontology_manager = ontology_manager;
		this.hdot_ontology = hdot_ontology;
		this.acceptedRecommendation = accceptedRecommendation;
		//TODO extract from the ontology iri
		this.pathToModules = "file:/Users/nikita/Documents/IFOMIS/terminalInterface/data/hdot/";
		this.dataFactory = ontology_manager.getOWLDataFactory();

		this.uriManager = new HDOTURIManager(accceptedRecommendation,
				includeSubclasses);
		this.hdotVerifier = new HDOTVerifier();

		extendHDOT();
	}

	public void extendHDOT() throws OWLOntologyStorageException,
			URISyntaxException, HdotExtensionException {
		String newHdotURI = "";
		if (uriManager.keepOriginalURI) {
			newHdotURI = uriManager.getNewHdotUri();
		}
		// add new class wrt accepted recommendation

		integrateClass();
		log.debug("class is integrated");
		
		integrateLabelOfNewClass();
		log.debug("label is for the class is added");

		integrateDefinitionOfNewClass();
		log.debug("definition(s) of the class is integrated");
		
		integrateOriginalId();
		log.debug("original id is integrated");

		if (hdotVerifier.verifyOntology(hdot_ontology)) {
			log.debug("extended ontology is verified");
			
			saveOntology();
		} else {
			throw new HdotExtensionException(
					"HDOT cannot be extended due to inconsistency");
		}
	}

	private void integrateClass() {
		hitForIntegration = dataFactory.getOWLClass(IRI
				.create(acceptedRecommendation.getHit().getURI().toString()));
		OWLClass hdotMatchedClass = dataFactory.getOWLClass(IRI
				.create(acceptedRecommendation.getMatchedClass().getURI()
						.toString()));

		OWLAxiom axiom = dataFactory.getOWLSubClassOfAxiom(hitForIntegration,
				hdotMatchedClass);

		AddAxiom addAxiom = new AddAxiom(
				acceptedRecommendation.getHdotModule(), axiom);

		ontology_manager.applyChange(addAxiom);
	}

	private void integrateLabelOfNewClass() {
		OWLAnnotation commentAnno = dataFactory.getOWLAnnotation(
				dataFactory
						.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL
								.getIRI()), dataFactory.getOWLLiteral(
						acceptedRecommendation.getHit().getLabel(), "en"));
		OWLAxiom ax = dataFactory.getOWLAnnotationAssertionAxiom(
				hitForIntegration.getIRI(), commentAnno);
		ontology_manager.applyChange(new AddAxiom(acceptedRecommendation
				.getHdotModule(), ax));
	}

	private void integrateDefinitionOfNewClass() {
		for (String definition : acceptedRecommendation.getHitDefinition()) {
			OWLAnnotation defAnno = dataFactory
					.getOWLAnnotation(
							dataFactory
									.getOWLAnnotationProperty(IRI
											.create("http://purl.obolibrary.org/obo/IAO_0000115")),
							dataFactory.getOWLLiteral(definition, "en"));

			OWLAxiom ax = dataFactory.getOWLAnnotationAssertionAxiom(
					hitForIntegration.getIRI(), defAnno);
			ontology_manager.applyChange(new AddAxiom(acceptedRecommendation
					.getHdotModule(), ax));
		}
	}

	private void integrateOriginalId() {
		OWLAnnotation sourceAnno = dataFactory.getOWLAnnotation(dataFactory
				.getOWLAnnotationProperty(IRI
						.create("http://purl.obolibrary.org/obo/IAO_0000119")),
				dataFactory.getOWLLiteral(acceptedRecommendation
						.getHit().getURI().toString()));
		
		OWLAxiom ax = dataFactory.getOWLAnnotationAssertionAxiom(
				hitForIntegration.getIRI(), sourceAnno);
		ontology_manager.applyChange(new AddAxiom(acceptedRecommendation
				.getHdotModule(), ax));
	}

	/**
	 * Saves the ontology administrated by the ontology manager.
	 * 
	 * @param ontoOut
	 *            the name of the file where the ontology is stored
	 * @throws OWLOntologyStorageException
	 *             occurs if the ontology can not be stored
	 * @throws URISyntaxException
	 */
	private void saveOntology() throws OWLOntologyStorageException,
			URISyntaxException {

		// finally the ontology can be stored
		
		String[] iriTokens = acceptedRecommendation.getHdotModule()
				.getOntologyID().getOntologyIRI().toString().split("/");
		String moduleAccession = iriTokens[iriTokens.length - 1];
		this.ontology_manager.saveOntology(
				acceptedRecommendation.getHdotModule(),
				IRI.create(pathToModules + moduleAccession));

		log.info("The extended ontology is saved in: " + pathToModules + moduleAccession);
	}
}
