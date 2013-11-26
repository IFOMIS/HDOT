package org.ifomis.ontologyaggregator.integration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.ifomis.ontologyaggregator.exception.HdotExtensionException;
import org.ifomis.ontologyaggregator.recommendation.Recommendation;
import org.ifomis.ontologyaggregator.util.Configuration;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
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

	private static final Logger log = Logger.getLogger(HDOTExtender.class);
	/**
	 * the data factory for the hdot ontology
	 */
	private OWLDataFactory dataFactory;

	/**
	 * The ontology manager
	 */
	private OWLOntologyManager ontologyManager;

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

	private OWLOntology newModule;

	private String nameOfNewModule;

	// TODO use user rights for the integration of new modules
	private boolean userRights;

	private OntologyService ontologyService;

	private Recommendation acceptedRecommendation;

	public HDOTExtender(OntologyService ontologyService, String userID,
			boolean userRights) throws OntologyServiceException, IOException,
			OWLOntologyStorageException, URISyntaxException,
			HdotExtensionException, OWLOntologyCreationException {

		// initialize fields
		this.ontologyManager = OWLManager.createOWLOntologyManager();
		this.dataFactory = this.ontologyManager.getOWLDataFactory();
		this.nameOfNewModule = "hdot_module_user" + userID + ".owl";
		this.userRights = userRights;
		this.uriManager = new HDOTURIManager();
		this.hdotVerifier = new HDOTVerifier();
		this.ontologyService = ontologyService;
	}

	public void integrarteHitInHDOT(Recommendation acceptedRecommendation)
			throws OWLOntologyStorageException, OWLOntologyCreationException,
			URISyntaxException, HdotExtensionException, IOException,
			OntologyServiceException {
		
		this.acceptedRecommendation = acceptedRecommendation;
		
		this.uriManager.checkURIs(acceptedRecommendation);

		initNewModule(acceptedRecommendation);

		extendHDOT(
				acceptedRecommendation.getHit(),
				dataFactory.getOWLClass(IRI.create(acceptedRecommendation
						.getMatchedClass().getURI().toString())),
				acceptedRecommendation.getHitDefinition(), true);

		// in case the user want extends HDOT with the subclasses of the actual
		// hit
		if (acceptedRecommendation.includeSubclasses()) {

			List<OntologyTerm> subClasses = acceptedRecommendation
					.getHitChildren();

			for (OntologyTerm subClass : subClasses) {
				// TODO ensure that the classes are not already contained in
				// hdot
				// ensureNotAlreadyContainedInHDOT(subClass);

				List<String> definitionsOfSubClass = ontologyService
						.getDefinitions(subClass);
				extendHDOT(subClass, theAcceptedHit, definitionsOfSubClass,
						false);
			}
		}

		if (userRights) {
			createNewVisibleModuleAndUpdateHdotAll();
		} else {
			createNewModuleForCuration();
		}
	}

	private void initNewModule(Recommendation accceptedRecommendation)
			throws OWLOntologyCreationException, URISyntaxException {
		boolean found = false;
		// log.debug("path to hdot ontology " + this.pathToModules);

		File directory = new File(
				Configuration.PATH_TO_AUTHORIZED_USER_MODULES.toURI());

		IRI ontologyIRI = IRI.create("http://www.ifomis.org/hdot/"
				+ this.nameOfNewModule);

		IRI documentIRI;
		if (userRights) {
			documentIRI = Configuration.PATH_TO_AUTHORIZED_USER_MODULES
					.resolve(this.nameOfNewModule);
			found = new File(directory, this.nameOfNewModule).exists();

		} else {
			documentIRI = Configuration.PATH_TO_NOT_AUTHORIZED_USER_MODULES
					.resolve(this.nameOfNewModule);
			// for the not authorized modules we will have every time new module
		}

		OWLOntologyIRIMapper iriMapper = new SimpleIRIMapper(ontologyIRI,
				documentIRI);
		ontologyManager.addIRIMapper(iriMapper);

		if (found) {
			log.debug("MODULE EXISTS");

			this.newModule = ontologyManager.loadOntology(documentIRI);

		} else {
			log.debug("MODULE DOES NOT EXIST");

			this.newModule = this.ontologyManager.createOntology(ontologyIRI);
		}

		OWLImportsDeclaration importDeclaraton = this.dataFactory
				.getOWLImportsDeclaration(accceptedRecommendation
						.getHdotModule().getOntologyID().getOntologyIRI());

		this.ontologyManager.applyChange(new AddImport(newModule,
				importDeclaraton));
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
	 * @throws OWLOntologyCreationException
	 * @throws OntologyServiceException
	 */
	private void extendHDOT(OntologyTerm newClass, OWLClass parent,
			List<String> definitions, boolean isTheActualHit)
			throws OWLOntologyStorageException, URISyntaxException,
			HdotExtensionException, IOException, OWLOntologyCreationException,
			OntologyServiceException {

		String newHdotURI = "";
		if (!uriManager.keepOriginalURI()) {

			newHdotURI = uriManager.generateNextHdotUri();
			log.debug("new uri" + newHdotURI);

		}
		// OntologyBean o1 = (OntologyBean) newClass.getOntology();
		// log.info("*************METAANNO*************");
		// log.info(o1.getMetaAnnotation());
		// log.info("--");
		// log.info(o1.getDescription());
		// log.info("--");
		// log.info(o1.getDocumentation());
		// log.info("--");
		// log.info(o1.getMetricsBean());
		// log.info("--");
		// ArrayList<Integer> groupIds = o1.getGroupIds();
		// for (Integer integer : groupIds) {
		// log.info(integer);
		// }
		// log.info("***********************************");

		// add new class wrt accepted recommendation
		integrateClass(newClass, parent, newHdotURI, isTheActualHit);
		log.debug("class is integrated");

		integrateLabelOfNewClass(newClass);
		log.debug("label is for the class is added");

		integrateDefinitionOfNewClass(definitions);
		log.debug("definition(s) of the class is integrated");

		integrateOriginalId(newClass);
		log.debug("original id is integrated");
		
		integrateHDOTModule(newClass);
		log.debug("HDOT module annotation is integrated");
		
		if (hdotVerifier.verifyOntology(newModule)) {
			log.debug("extended ontology is verified");

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

		log.debug("class for integration: " + newClass);
		log.debug("parent of class for integration: " + parent);

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

		AddAxiom addAxiom = new AddAxiom(newModule, axiom);

		ontologyManager.applyChange(addAxiom);
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
								.getIRI()), dataFactory.getOWLLiteral(newClass
						.getLabel().toLowerCase(), "en"));

		OWLAxiom ax = dataFactory.getOWLAnnotationAssertionAxiom(
				hitForIntegration.getIRI(), commentAnno);
		ontologyManager.applyChange(new AddAxiom(newModule, ax));
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
			ontologyManager.applyChange(new AddAxiom(newModule, ax));
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
		ontologyManager.applyChange(new AddAxiom(newModule, ax));
	}

	
	/**
	 * Integrates the importedFrom annotation. The value is the module URI where
	 * the matching parent was found.
	 * 
	 * @param newClass
	 */
	private void integrateHDOTModule(OntologyTerm newClass) {

//		String value = "";
//		
//		if(acceptedRecommendation.getURIOfModuleForURIGeneration().equals("")){
//			value =acceptedRecommendation.getHdotModule().getOntologyID().getOntologyIRI().toString();
//		}else{
//			value = acceptedRecommendation.getURIOfModuleForURIGeneration();
//		}
//		
		OWLAnnotation importedFromAnno = dataFactory.getOWLAnnotation(dataFactory
				.getOWLAnnotationProperty(IRI
						.create("http://purl.obolibrary.org/obo/IAO_0000412")),
				dataFactory.getOWLLiteral(acceptedRecommendation.getURIOfModuleForURIGeneration()));
		
		OWLAxiom ax = dataFactory.getOWLAnnotationAssertionAxiom(
				hitForIntegration.getIRI(), importedFromAnno);
		ontologyManager.applyChange(new AddAxiom(newModule, ax));
	}
	
	/**
	 * Saves the ontology administrated by the ontology manager. This method
	 * should be used when the user is an expert.
	 * 
	 * @param ontoOut
	 *            the name of the file where the ontology is stored
	 * @throws OWLOntologyStorageException
	 *             occurs if the ontology can not be stored
	 * @throws URISyntaxException
	 * @throws OWLOntologyCreationException
	 * @throws IOException
	 */
	private void createNewVisibleModuleAndUpdateHdotAll()
			throws OWLOntologyStorageException, URISyntaxException,
			OWLOntologyCreationException, IOException {

		this.ontologyManager = Configuration
				.mapIrisOfVisibleUserModules(this.ontologyManager);

		// finally the ontology can be stored
		// ontology_manager.saveOntology(newModule,
		// IRI.create(this.pathToModules + this.nameOfNewModule));
		this.ontologyManager.saveOntology(this.newModule,
				Configuration.PATH_TO_AUTHORIZED_USER_MODULES
						.resolve(this.nameOfNewModule));
		this.ontologyManager.removeOntology(this.newModule);

		List<String> orderOfModules = FileUtils.readLines(new File(
				Configuration.MODULES_SORTING_FILE.toURI()));

		String newModuleIRI = this.newModule.getOntologyID().getOntologyIRI()
				.toString();

		// add the module id and import the new module if it does not already
		// exist
		if (!orderOfModules.contains(newModuleIRI)) {
			OWLOntology hdot_container = this.ontologyManager
					.loadOntologyFromOntologyDocument(new File(
							Configuration.HDOT_CONTAINER_AUTHORIZED.toURI()));
			// import the new module in hdot_all.owl and save it

			OWLImportsDeclaration importDeclaraton = this.dataFactory
					.getOWLImportsDeclaration(this.newModule.getOntologyID()
							.getOntologyIRI());

			this.ontologyManager.applyChange(new AddImport(hdot_container,
					importDeclaraton));

			this.ontologyManager.saveOntology(hdot_container);

			// add the new module to the list of sorted ids of the hdot modules
			orderOfModules.add(0, newModuleIRI);
			FileUtils.writeLines(
					new File(Configuration.MODULES_SORTING_FILE.toURI()),
					orderOfModules);
		}
		log.info("The extended HDOT module is saved in: "
				+ Configuration.PATH_TO_AUTHORIZED_USER_MODULES
				+ this.nameOfNewModule);
	}

	private void createNewModuleForCuration()
			throws OWLOntologyStorageException, OWLOntologyCreationException,
			IOException {

		this.ontologyManager = Configuration
				.mapIrisOfUserModulesForCuration(this.ontologyManager);

		this.ontologyManager.saveOntology(this.newModule,
				Configuration.PATH_TO_NOT_AUTHORIZED_USER_MODULES
						.resolve(this.nameOfNewModule));
		this.ontologyManager.removeOntology(this.newModule);

		OWLOntology hdot_container_not_autorized = this.ontologyManager
				.loadOntologyFromOntologyDocument(new File(
						Configuration.HDOT_CONTAINER_NOT_AUTHORIZED.toURI()));

		OWLImportsDeclaration importDeclaraton = this.dataFactory
				.getOWLImportsDeclaration(this.newModule.getOntologyID()
						.getOntologyIRI());

		this.ontologyManager.applyChange(new AddImport(
				hdot_container_not_autorized, importDeclaraton));

		this.ontologyManager.saveOntology(hdot_container_not_autorized);

		List<String> lines = FileUtils.readLines(new File(Configuration.MODULES_FOR_CURATION.toURI()));
		lines.add(newModule.getOntologyID().getOntologyIRI().toString());
		FileUtils.writeLines(new File(Configuration.MODULES_FOR_CURATION.toURI()), lines);
		
		log.info("The extended HDOT module is saved in: "
				+ Configuration.PATH_TO_NOT_AUTHORIZED_USER_MODULES.toString()
				+ this.nameOfNewModule);
	}
}
