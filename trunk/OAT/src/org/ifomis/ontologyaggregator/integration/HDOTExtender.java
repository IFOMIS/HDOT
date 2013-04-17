package org.ifomis.ontologyaggregator.integration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

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

import uk.ac.ebi.ontocat.OntologyService;
import uk.ac.ebi.ontocat.OntologyServiceException;
import uk.ac.ebi.ontocat.OntologyTerm;

/**
 * Extends HDOT with the class that is recommended and confirmed by the user.
 * 
 * @author Nikolina
 * 
 */
public class HDOTExtender {

	
	/**
	 * The URI manager that decides if the original URI should be kept or new
	 * should be generated.
	 */
	private HDOTURIManager uriManager;

	/**
	 * the HDOTVerifier that check the consistency of the extended ontology.
	 */
	private HDOTVerifier hdotVerifier;

	/**
	 * The recommendation that was accepted by the user.
	 */
	private Recommendation acceptedRecommendation;

	private static final Logger log = Logger.getLogger(HDOTExtender.class);
	/**
	 * the data factory for the hdot ontology
	 */
	private OWLDataFactory dataFactory;

	/**
	 * the outgoing ontology, where the recommended class is going to be
	 * inserted
	 */
	private IRI ontoOut;
	
	/**
	 * The ontology manager
	 */
	private OWLOntologyManager ontology_manager;

	/**
	 * The HDOT ontology
	 */
	private OWLOntology hdot_ontology;

	/**
	 * The path to the modules.
	 */
	private String pathToModules = "";

	/**
	 * The current class that should be integrated under HDOT.
	 */
	private OWLClass hitForIntegration;

	/**
	 * The accepted class. Needed to be stored here in order to know the parent
	 * of the subclasses if the user wants to include them, too.
	 */
	private OWLClass theAcceptedHit;

	public HDOTExtender(Recommendation accceptedRecommendation,
			boolean includeSubclasses, OWLOntologyManager ontology_manager,
			OWLOntology hdot_ontology, OntologyService ontologyService)
			throws OntologyServiceException, IOException,
			OWLOntologyStorageException, URISyntaxException,
			HdotExtensionException {

		this.ontology_manager = ontology_manager;
		this.hdot_ontology = hdot_ontology;
		this.acceptedRecommendation = accceptedRecommendation;

		// extract the path to the physical documents from the ontology iri
		String[] partsOfPath = ontology_manager
				.getOntologyDocumentIRI(hdot_ontology).toString().split("/");

		for (int i = 0; i < partsOfPath.length - 1; i++) {
			this.pathToModules +=  partsOfPath[i] + "/";
		}
		log.debug("path to hdot ontology " + this.pathToModules);

		this.dataFactory = ontology_manager.getOWLDataFactory();

		this.uriManager = new HDOTURIManager(accceptedRecommendation,
				includeSubclasses);

		this.hdotVerifier = new HDOTVerifier();

		extendHDOT(
				acceptedRecommendation.getHit(),
				dataFactory.getOWLClass(IRI.create(acceptedRecommendation
						.getMatchedClass().getURI().toString())),
				acceptedRecommendation.getHitDefinition(), true);

		if (includeSubclasses) {

			List<OntologyTerm> subClasses = accceptedRecommendation
					.getHitChildren();

			for (OntologyTerm subClass : subClasses) {
				List<String> definitionsOfSubClass = ontologyService
						.getDefinitions(subClass);
				extendHDOT(subClass, theAcceptedHit, definitionsOfSubClass,
						false);
			}
		}
	}

	/**
	 * Extends HDOT with the given new class and embeds it under the given
	 * parent.
	 * 
	 * @param newClass
	 *            the class to be integrated in the ontology
	 * @param parent
	 *            the parent class of the new class
	 * @param definitions
	 *            the definitions of the new class
	 * @param isTheActualHit
	 *            true if the new class is the hit and false if the new class is
	 *            one of the subclasses of the hit
	 * @throws OWLOntologyStorageException
	 * @throws URISyntaxException
	 * @throws HdotExtensionException
	 * @throws IOException
	 */
	public void extendHDOT(OntologyTerm newClass, OWLClass parent,
			List<String> definitions, boolean isTheActualHit)
			throws OWLOntologyStorageException, URISyntaxException,
			HdotExtensionException, IOException {

		String newHdotURI = "";
		if (!uriManager.keepOriginalURI()) {

			newHdotURI = uriManager.generateNextHdotUri();
			log.debug("new uri" + newHdotURI);

		}
		// add new class wrt accepted recommendation

		integrateClass(newClass, parent, newHdotURI, isTheActualHit);
		log.debug("class is integrated");

		integrateLabelOfNewClass(newClass);
		log.debug("label is for the class is added");

		integrateDefinitionOfNewClass(definitions);
		log.debug("definition(s) of the class is integrated");

		integrateOriginalId(newClass);
		log.debug("original id is integrated");

		if (hdotVerifier.verifyOntology(hdot_ontology)) {
			log.debug("extended ontology is verified");

			saveOntology();
		} else {
			throw new HdotExtensionException(
					"HDOT cannot be extended due to inconsistency");
		}
	}

	/**
	 * Integrates the new class with the given uri.
	 * 
	 * @param newClass
	 * @param parent
	 * @param newHdotURI
	 * @param isTheActualHit
	 */
	private void integrateClass(OntologyTerm newClass, OWLClass parent,
			String newHdotURI, boolean isTheActualHit) {

		if (newHdotURI.isEmpty()) {
			hitForIntegration = dataFactory.getOWLClass(IRI.create(newClass
					.getURI().toString()));

			if (isTheActualHit) {
				theAcceptedHit = dataFactory.getOWLClass(IRI.create(newClass
						.getURI().toString()));
			}
		} else {
			hitForIntegration = dataFactory.getOWLClass(IRI.create(newHdotURI));

			if (isTheActualHit) {
				theAcceptedHit = dataFactory
						.getOWLClass(IRI.create(newHdotURI));
			}
		}

		OWLAxiom axiom = dataFactory.getOWLSubClassOfAxiom(hitForIntegration,
				parent);

		AddAxiom addAxiom = new AddAxiom(
				acceptedRecommendation.getHdotModule(), axiom);

		ontology_manager.applyChange(addAxiom);
	}

	/**
	 * Adds a label to the given class.
	 * 
	 * @param newClass
	 */
	private void integrateLabelOfNewClass(OntologyTerm newClass) {
		OWLAnnotation commentAnno = dataFactory.getOWLAnnotation(
				dataFactory
						.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL
								.getIRI()), dataFactory.getOWLLiteral(
						newClass.getLabel().toLowerCase(), "en"));

		OWLAxiom ax = dataFactory.getOWLAnnotationAssertionAxiom(
				hitForIntegration.getIRI(), commentAnno);
		ontology_manager.applyChange(new AddAxiom(acceptedRecommendation
				.getHdotModule(), ax));
	}

	/**
	 * Adds definitions to the integrated class.
	 * 
	 * @param definitions
	 */
	private void integrateDefinitionOfNewClass(List<String> definitions) {
		for (String definition : definitions) {
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

	/**
	 * Integrates the original URI as definition source.
	 * 
	 * @param newClass
	 */
	private void integrateOriginalId(OntologyTerm newClass) {
		OWLAnnotation sourceAnno = dataFactory.getOWLAnnotation(dataFactory
				.getOWLAnnotationProperty(IRI
						.create("http://purl.obolibrary.org/obo/IAO_0000119")),
				dataFactory.getOWLLiteral(newClass.getURI().toString()));

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

		log.info("The extended ontology is saved in: " + pathToModules
				+ moduleAccession);
	}
}
