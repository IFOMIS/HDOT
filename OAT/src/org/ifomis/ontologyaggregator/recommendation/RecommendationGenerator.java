package org.ifomis.ontologyaggregator.recommendation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Stack;


import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.ebi.ontocat.OntologyService;
import uk.ac.ebi.ontocat.OntologyServiceException;
import uk.ac.ebi.ontocat.OntologyTerm;

/**
 * The RecommendationGenerator generates a recommendation under which concept to
 * integrate a hit found in BioPortal based on the similarity of their paths.
 * 
 * @author Nikolina
 * 
 */
public class RecommendationGenerator {

	private static final Logger log = Logger
			.getLogger(RecommendationGenerator.class);

	File statisticsFile = new File("data/statistics");
	FileWriter statistcsWriter = new FileWriter(statisticsFile, true);

	/**
	 * The @link{IRI} of the input ontology.
	 */
	private IRI iriIn;

	/**
	 * The hdot ontology where the term is going to be inserted as a class.
	 */
	private OWLOntology hdot_ontology;

	/**
	 * The manager of the ontology.
	 */
	private OWLOntologyManager ontology_manager;


	/**
	 * the current hit
	 */
	private OntologyTerm currentHit;

	/**
	 * list that stores the parsed hdot hierarchy
	 */
	private List<OWLClass> hierarchyOfHdotClass;

	/**
	 * global counter for parents
	 */
	private int counterForParents;

	

	/**
	 * The term that the user searches for.
	 */
	private String searchedTerm;

	boolean conceptIdsMatch;
	/**
	 * stores the list of ontologies that are imported in HDOT
	 */
	private List<String> importedOntologies;

	private int hitsCounter;
	private int recommendationCounter;

	private boolean labelsMatch;

	/**
	 * stores the generated recommendations
	 */
	private List<Recommendation> listOfRecommendations;

	/**
	 * the system time by the start of the execution
	 */
	private long start;

	private List<Recommendation> listOfRecsPossibleInCoreOfHDOT;

	private List<Recommendation> listOfInCoreNotLeafMatches;

	private List<Recommendation> listImportedNotLeafMatches;

	private OntologyService ontologyService;

	private Stack<OntologyTerm> hierarchyOfHit;
	
	private OWLOntology[] sortedHdotModules;

	/**
	 * Creates a RecommendationGenerator and loads the specified input ontology.
	 * 
	 * @param ontoIn
	 *            the ontology to be loaded
	 * @param ontoOut
	 *            the extended ontology that is saved
	 * @param ontologyService
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws OntologyServiceException
	 */
	public RecommendationGenerator(String ontoIn, String ontoOut,
			List<Stack<OntologyTerm>> listOfPaths, String searchedTerm,
			OntologyService ontologyService, long start) throws IOException,
			URISyntaxException, OntologyServiceException {

		// initialize the fields
		this.start = start;
		this.ontologyService = ontologyService;

		this.listOfRecommendations = new ArrayList<>();
		this.listOfRecsPossibleInCoreOfHDOT = new ArrayList<>();
		this.listOfInCoreNotLeafMatches = new ArrayList<>();
		this.listImportedNotLeafMatches = new ArrayList<>();

		this.importedOntologies = new ArrayList<String>();
		this.importedOntologies
				.add("http://www.ifomis.org/hdot/doid_import.owl");
		this.importedOntologies
				.add("http://purl.obolibrary.org/obo/bfo.owl");

		// Get hold of an ontology manager
		this.ontology_manager = OWLManager.createOWLOntologyManager();
		this.searchedTerm = searchedTerm;

		File file = new File(ontoIn);

		try {
			// Now load the local copy of hdot that include all modules
			this.hdot_ontology = ontology_manager
					.loadOntologyFromOntologyDocument(file);

		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
		// We can always obtain the location where an ontology was loaded from
		this.iriIn = ontology_manager.getOntologyDocumentIRI(hdot_ontology);
		log.info("iriIn: " + iriIn);
		log.info("Loaded ontology: " + hdot_ontology);
		Set<OWLOntology> hdotModules = ontology_manager.getOntologies();
		log.debug("modules: " );
		for (OWLOntology owlOntology : hdotModules) {
			log.debug(owlOntology);
		}
		// sort modules such that the recommendation will be found in more
		// specific module
		sortedHdotModules = new ModuleSorter()
				.sortHdotModules(hdotModules);
		
		generateRecommendation(listOfPaths);
	}


	/**
	 * Process all five best candidates and generates recommendation for
	 * integration whenever possible.
	 * 
	 * @param listOfPaths
	 *            list of paths for the best 5 hits
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws OntologyServiceException
	 */
	private void generateRecommendation(List<Stack<OntologyTerm>> listOfPaths)
			throws URISyntaxException, IOException, OntologyServiceException {

		hitsCounter = 0;
		recommendationCounter = 0;

		// iterate over paths of the 5-best hits
		for (Stack<OntologyTerm> path : listOfPaths) {
			++hitsCounter;
			hierarchyOfHit = (Stack<OntologyTerm>) path.clone();

			log.info("\n*******hit Nr:" + hitsCounter + "******\n");
			// the path response was empty
			if (path.size() == 1) {
				log.info(path.peek());
				log.info("SPARQL response for the root path was empty");
				continue;
			}

			log.info("Length of path to root is: " + path.size());
			log.debug("____________________________________________________________________");
			counterForParents = 0;
			if (!path.isEmpty()) {
				this.currentHit = path.peek();
				// log.info("current hit=" + this.currentHit);
			} else {
				log.debug("path was empty");
				continue;
			}

			if (recommend(path)) {
				// if term has been recommended do not search further
				// return;
				++recommendationCounter;
			}
		}
		if (recommendationCounter == 0) {
			log.info("NO SUITABLE RECOMMENDATION WAS FOUND!\n");
		} else {
			log.info(recommendationCounter + " recommendations were generated");
		}
	}

	/**
	 * Process the given path and checks if recommendation can be created.
	 * 
	 * @param path
	 *            the path to root for the current hit that is retrieved from
	 *            BioPortal
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws OntologyServiceException
	 */
	private boolean recommend(Stack<OntologyTerm> path)
			throws URISyntaxException, IOException, OntologyServiceException {

		OntologyTerm currentCandidate = null;
		boolean termHasBeenRecommended = false;

		while (!path.isEmpty()) {

			// iterate over all ontologies we can use it for modularization
			// for (OWLOntology hdotModule : hdotModules) {
			for (int j = 0; j < sortedHdotModules.length; j++) {

				OWLOntology hdotModule = sortedHdotModules[j];

				currentCandidate = path.peek();
				// exclude owl:Thing
				if (currentCandidate.getURI().toString()
						.equals("http://www.w3.org/2002/07/owl#Thing")) {
					break;
				}

				log.info("currentCandidate: "
						+ currentCandidate.getURI().toString() + "\t"
						+ currentCandidate.getLabel());

				log.info("Module: " + hdotModule.getOntologyID());

				OntologyTerm matchedConcept = findMatch(currentCandidate,
						hdotModule);

				if (matchedConcept != null) {
					if (buildRecommendaton(hdotModule, matchedConcept,
							termHasBeenRecommended)) {
						// return true in order to terminate
						return true;
					}
				} else {
					log.info("no match found");
				}
				log.info("__________________________________");
			}
			// after we checked in all modules pop the current candidate and
			// continue with the next in the next iteration
			path.pop();
			++counterForParents;
		}
		return termHasBeenRecommended;
	}

	/**
	 * Builds the recommendation and prints it out on the console.
	 * 
	 * @param hdotModule
	 *            the module where the match was found
	 * @param matchedConcept
	 *            class that matches a parent of the current hit
	 * @param termHasBeenRecommended
	 * @return true if termHasBeenRecommended
	 * @throws OntologyServiceException
	 */
	private boolean buildRecommendaton(OWLOntology hdotModule,
			OntologyTerm matchedConcept, boolean termHasBeenRecommended)
			throws OntologyServiceException {

		boolean onlyInCore = false;
		List<String> definitions = ontologyService
				.getDefinitions(this.currentHit);
		List<String> synonyms = ontologyService.getSynonyms(this.currentHit);
		List<OntologyTerm> childrenOfHit = ontologyService
				.getChildren(this.currentHit);

		if (importedOntologies.contains(hdotModule.getOntologyID().getOntologyIRI().toString())) {
			log.info("RECOMMENDATION POSSIBLE ONLY IN CORE OF HDOT");
			onlyInCore = true;
		}
		Recommendation recommendation = new Recommendation(hitsCounter,
				currentHit, conceptIdsMatch, labelsMatch, searchedTerm,
				hierarchyOfHdotClass, hierarchyOfHit, hdotModule,
				counterForParents, matchedConcept, definitions, synonyms,
				childrenOfHit);

		if (onlyInCore) {
			termHasBeenRecommended = false;
			listOfRecsPossibleInCoreOfHDOT.add(recommendation);

		} else {
			termHasBeenRecommended = true;
			listOfRecommendations.add(recommendation);
			// log.info(recommendation.toString());
		}
		return termHasBeenRecommended;
	}

	/**
	 * @param matchedConcept
	 *            the match found in HDOT
	 * @return true if the match is the found concept
	 */
	private void isMatchedClassTheSearchedTerm(OntologyTerm matchedConcept) {
		if (counterForParents == 0) {
			log.error("OAT SHOULD NOT BE EVOKED! Since:");
			log.error("The concept: " + matchedConcept + " already exists.");
			long end = System.currentTimeMillis();

			long milliseconds = (end - start);

			long seconds = (milliseconds / 1000);

			long mins = seconds / 60;
			long restsecs = seconds % 60;

			log.info("Execution time was " + (end - start) + " ms.");
			log.info("Execution time was " + mins + ":" + restsecs + " sec.");

			File logSearchFile = new File("log/loggingSearchEngine.html");

			File logRecommendFile = new File(
					"log/loggingRecommendationGeneration.html");
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
			String date = dateFormat.format(new Date());

			logSearchFile.renameTo(new File("log/" + date + "_" + searchedTerm
					+ "_loggingSearchEngine.html"));

			logRecommendFile.renameTo(new File("log/" + date + "_"
					+ searchedTerm + "_loggingRecommendationGeneration.html"));

			log.info("Done.");
			log.info("Log messages written in: log/" + date + "_"
					+ searchedTerm + "_loggingSearchEngine.html and " + date
					+ "_" + searchedTerm
					+ "_loggingRecommendationGeneration.html");
			System.exit(0);
		}
	}

	/**
	 * Tries to find a match for the given @link{OntologyTerm} with any class in
	 * the given module.
	 * 
	 * @param currentCandidate
	 *            the candidate term we are looking at
	 * @param currentOntology
	 *            the current ontology we are looking at
	 * @return the concept where the term will be integrated under
	 * @throws URISyntaxException
	 * @throws OntologyServiceException
	 */
	private OntologyTerm findMatch(OntologyTerm currentCandidate,
			OWLOntology currentOntology) throws URISyntaxException,
			OntologyServiceException {

		Set<OWLClass> classesInSignature = currentOntology
				.getClassesInSignature();

		OntologyTerm matchedTerm = null;

		// check if the current class is hdot_core
		boolean isHdotCore = ( currentOntology.getOntologyID().getOntologyIRI().toString()
				.contains("http://www.ifomis.org/hdot/hdot_core.owl"));

		// iterate over all classes of the ontology and try to find a match of
		// uri or label
		for (OWLClass hdotClass : classesInSignature) {
			//exclude class Nothing in hdot
			if (hdotClass.isOWLNothing()) {
				continue;
			}
			Set<OWLAnnotation> annotations = hdotClass
					.getAnnotations(currentOntology);

			hierarchyOfHdotClass = new ArrayList<OWLClass>();
			conceptIdsMatch = false;
			labelsMatch = false;

			String pureLabelOfHdotClass = retriveRdfsLabel(annotations);

			// compare concept ids
			if (currentCandidate.getURI().toString()
					.equals(hdotClass.toStringID())) {
				log.info("\nCONCEPT IDs of BioPortal HIT and HDOT CLASS MATCH\n");
				// log.info("id of current candidate: "
				// + currentCandidate.getURI().toString());
				// log.info("id of hdot class: " + hdotClass.toStringID());
				conceptIdsMatch = true;
				// if we find a match then we will retrieve this concept
				matchedTerm = new OntologyTerm();
				matchedTerm.setAccession(hdotClass.toStringID());
			}

			// compare labels
			String currentLabel = currentCandidate.getLabel();
			if (currentLabel.isEmpty()) {
				continue;
			}

			if (currentLabel.trim().equalsIgnoreCase(
					pureLabelOfHdotClass.trim())) {

				labelsMatch = true;
				log.info("\nLABELS of BioPortal HIT and HDOT CLASS MATCH\n");
				log.info("pureLabelOfHdotClass: " + pureLabelOfHdotClass);

				log.info("label of currentCandidate: "
						+ currentCandidate.getLabel());
				if (conceptIdsMatch) {
					log.info("\nIDs and LABELS MATCH");
				}
				if (matchedTerm == null) {
					matchedTerm = new OntologyTerm();
				}
				matchedTerm.setLabel(pureLabelOfHdotClass);
			} else {
				double similarityOfLabels = DiceCoefficient
						.diceCoefficientOptimized(currentLabel.toLowerCase(),
								pureLabelOfHdotClass.toLowerCase());
				if (similarityOfLabels > 0.95) {

					log.info("\nLABELS of BioPortal HIT and HDOT CLASS SIMILARITY > 90\n");
					log.info("pureLabelOfHdotClass: " + pureLabelOfHdotClass);

					log.info("label of currentCandidate: "
							+ currentCandidate.getLabel());
					log.info("similarity of labels: " + similarityOfLabels);

					if (conceptIdsMatch) {
						log.info("\nIDs MATCH and LABELS SIMILAR");
					}
					if (matchedTerm == null) {
						matchedTerm = new OntologyTerm();
					}
					matchedTerm.setLabel(pureLabelOfHdotClass);
				}
			}
			// check after the comparison if a match was found
			// to collect the hierarchy and set the accessions
			if (matchedTerm != null) {
				matchedTerm.setURI(new URI(hdotClass.toStringID()));
				matchedTerm.setLabel(pureLabelOfHdotClass);
				matchedTerm.setOntologyAccession(currentOntology
						.getOntologyID().getOntologyIRI().toString());

				List<String> definitions = ontologyService
						.getDefinitions(this.currentHit);
				List<String> synonyms = ontologyService
						.getSynonyms(this.currentHit);
				List<OntologyTerm> childrenOfHit = ontologyService.getChildren(
						this.currentHit.getOntologyAccession(),
						this.currentHit.getAccession());

				isMatchedClassTheSearchedTerm(matchedTerm);

				extractHierarchyOfMatchedTerm(currentOntology, hdotClass);

				// if the match is found in hdot core and the matched class is
				// not a leaf node continue with the next class
				if (isHdotCore) {
					if (!hdotClass.getSubClasses(currentOntology).isEmpty()) {

						log.debug("THE MATCHED CLASS WAS FOUND IN HDOT_CORE BUT IT IS NOT A LEAF NODE");

						// listOfInCoreNotLeafMatches.add(notification);
						listOfInCoreNotLeafMatches.add(new Recommendation(
								hitsCounter, currentHit, conceptIdsMatch,
								labelsMatch, searchedTerm,
								hierarchyOfHdotClass, hierarchyOfHit,
								currentOntology, counterForParents,
								matchedTerm, definitions, synonyms,
								childrenOfHit));
						log.debug("search for further matches ...\n");

						matchedTerm = null;
						continue;
					}
				}

				if (hdotClass.getSuperClasses(currentOntology).size() == 0) {
					log.debug("THE MATCHED CLASS IS IMPORTED IN THIS MODULE");

					if (!(hdotClass.getSubClasses(ontology_manager
							.getOntologies()).isEmpty())) {
						log.debug("BUT IT IS NOT A LEAF NODE");
						log.debug("search for further matches ...\n");

						listImportedNotLeafMatches.add(new Recommendation(
								hitsCounter, currentHit, conceptIdsMatch,
								labelsMatch, searchedTerm,
								hierarchyOfHdotClass, hierarchyOfHit,
								currentOntology, counterForParents,
								matchedTerm, definitions, synonyms,
								childrenOfHit));
						matchedTerm = null;
						continue;
					}
				}
				// in case a match was found quit the loop for the classes
				// possibly we can collect the matches in order to have them for
				// further recommendations
				break;
			}
			// otherwise search further
		}
		return matchedTerm;
	}

	/**
	 * Extracts the hierarchy of the given hdot owlClass.
	 * 
	 * @param currentOntology
	 *            the current module we found the match
	 * @param hdotClass
	 *            the @link{OWLClass} that matched one of the parents
	 * @return true if the matched class is imported in the given module
	 */
	private void extractHierarchyOfMatchedTerm(OWLOntology currentOntology,
			OWLClass hdotClass) {

		// extract the hierarchy of this class
		// set the parent to the current class for the first time
		OWLClass parent = hdotClass;

		while (parent != null) {

			Set<OWLClassExpression> superClasses = parent
					.getSuperClasses(ontology_manager.getOntologies());

			OWLClass parent_old = parent;

			// iterate over super classes
			for (OWLClassExpression owlSuperClassExpression : superClasses) {

				if (owlSuperClassExpression.isClassExpressionLiteral()) {

					// asOWLClass can be called only if the class
					// expression is not anonymous
					if (!owlSuperClassExpression.isAnonymous()) {
						parent = owlSuperClassExpression.asOWLClass();
					}
					if (!this.hierarchyOfHdotClass.contains(parent))
						this.hierarchyOfHdotClass.add(parent);
				}
			}
			// due to reflexivity
			if (parent_old.equals(parent)) {
				break;
			}
		}
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
				pureLabelOfClass = owlAnnotation.getValue().toString()
						.split("\"")[1];
			}
		}
		return pureLabelOfClass;
	}

	public List<Recommendation> getListOfRecommendations() {
		return listOfRecommendations;
	}

	public List<Recommendation> getListOfRecsPossibleInCoreOfHDOT() {
		return listOfRecsPossibleInCoreOfHDOT;
	}

	public List<Recommendation> getListOfInCoreNotLeafMatches() {
		return listOfInCoreNotLeafMatches;
	}

	public List<Recommendation> getListImportedNotLeafMatches() {
		return listImportedNotLeafMatches;
	}
	public OWLOntologyManager getOntology_manager() {
		return ontology_manager;
	}
	public OWLOntology getHdot_ontology() {
		return hdot_ontology;
	}
	public OntologyService getOntologyService() {
		return ontologyService;
	}

}