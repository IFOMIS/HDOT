package org.ifomis.ontologyaggregator.recommendation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.ifomis.ontologyaggregator.util.Configuration;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

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

	// File statisticsFile = new File("data/statistics");
	// FileWriter statistcsWriter = new FileWriter(statisticsFile, true);

	/**
	 * The hdot ontology where the term is going to be inserted as a class.
	 */
	private OWLOntology hdotOntology;

	/**
	 * The manager of the ontology.
	 */
	private OWLOntologyManager ontologyManager;

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

	private int numMatchedParents;

	private Properties properties;

	/**
	 * Creates a RecommendationGenerator and loads the specified input ontology.
	 * 
	 * @param HDOT_CONTAINER_AUTHORIZED
	 *            the ontology to be loaded
	 * @param ontoOut
	 *            the extended ontology that is saved
	 * @param ontologyService
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws OntologyServiceException
	 */
	public RecommendationGenerator(String searchedTerm,
			OntologyService ontologyService, long start) throws IOException,
			URISyntaxException, OntologyServiceException {

		// initialize the fields
		this.start = start;
		this.ontologyService = ontologyService;

		this.listOfRecommendations = new ArrayList<>();
		this.listOfRecsPossibleInCoreOfHDOT = new ArrayList<>();
		this.listOfInCoreNotLeafMatches = new ArrayList<>();
		this.listImportedNotLeafMatches = new ArrayList<>();
		this.properties = new Properties();
		properties.load(new FileInputStream("config/aggregator.properties"));

		this.importedOntologies = FileUtils.readLines(new File(
				Configuration.IMPORTED_ONTOLOGIES_FILE.toURI()));

		// Get hold of an ontology manager
		this.ontologyManager = OWLManager.createOWLOntologyManager();
		this.searchedTerm = searchedTerm;

		this.ontologyManager  = Configuration.mapIrisOfUserModules(ontologyManager);

		try {
			// Now load the local copy of hdot that include all modules
			this.hdotOntology = ontologyManager
					.loadOntologyFromOntologyDocument(Configuration.HDOT_CONTAINER_AUTHORIZED);

		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}

		log.info("Loaded ontology: " + hdotOntology);

		List<OWLOntology> hdotModules = ontologyManager
				.getSortedImportsClosure(hdotOntology);

		sortedHdotModules = new ModuleSorter().sortHdotModules(hdotModules);

		// generateRecommendation(listOfPathsOfAllHits);

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
	public void generateRecommendation(
			List<List<Stack<OntologyTerm>>> listOfPathsOfAllHits,
			boolean isTopFive) throws URISyntaxException, IOException,
			OntologyServiceException {

		// init depending if first or second run
		if (isTopFive) {
			hitsCounter = 0;
		} else {
			hitsCounter = 5;
		}
		recommendationCounter = 0;

		// loop over hits
		for (List<Stack<OntologyTerm>> listOfPaths : listOfPathsOfAllHits) {
			++hitsCounter;

			// loop over elements of the path
			for (Stack<OntologyTerm> path : listOfPaths) {

				hierarchyOfHit = (Stack<OntologyTerm>) path.clone();

				// the path response was empty
				if (path.size() <= 1) {
					// log.info(path.peek());
					log.info("SPARQL response for the root path was empty");
					continue;
				}
				log.info("\n***************************hit Nr:" + hitsCounter
						+ "***************************");
				log.info("The length of the current path to root is: "
						+ path.size());
				log.debug("____________________________________________________________________");
				counterForParents = 0;

				if (!path.isEmpty()) {
					this.currentHit = path.peek();
					// log.info("current hit=" + this.currentHit);

					if (recommend(path)) {
						++recommendationCounter;
						// if term has been recommended do not examine next
						// concepts
						break;
					}
				} else {
					log.debug("path was empty");
					continue;
				}
				log.info("\n***************************************************************");

			}
		}
		if (recommendationCounter == 0) {
			log.info("NO SUITABLE RECOMMENDATION WAS FOUND!\n");

			// mailSender
			// .sendMail(
			// "NO SUITABLE RECOMMENDATION WAS FOUND!",
			// "Neither valid nor potentail recommendations were generated for the serched term "
			// + searchedTerm
			// +
			// " because no match of a parent with HDOT class was recognized. ");
		} else {
			log.info(recommendationCounter
					+ " RECOMMENDATION(S) WERE GENERATED");
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

				log.debug("currentCandidate: "
						+ currentCandidate.getURI().toString() + "\t"
						+ currentCandidate.getLabel());

				log.debug("Module: " + hdotModule.getOntologyID());

				OntologyTerm matchedConcept = findMatch(currentCandidate,
						hdotModule);

				if (matchedConcept != null) {
					// return true in order to terminate

					path.clear();
					return true;

				} else {
					log.debug("no match found");
				}
				log.debug("____________________________________________________________________");
			}
			// after we checked in all modules pop the current candidate and
			// continue with the next in the next iteration
			path.pop();
			++counterForParents;
		}
		return termHasBeenRecommended;
	}

	/**
	 * Builds the recommendation and adds it to the corresponding list.
	 * 
	 * @param hdotModule
	 *            the module where the match was found
	 * @param matchedConcept
	 *            class that matches a parent of the current hit
	 * @param termHasBeenRecommended
	 * @return true if termHasBeenRecommended
	 * @throws OntologyServiceException
	 */
	private Recommendation buildRecommendaton(OWLOntology hdotModule,
			OntologyTerm matchedConcept) throws OntologyServiceException {

		List<String> definitions = ontologyService
				.getDefinitions(this.currentHit);
		List<String> synonyms = ontologyService.getSynonyms(this.currentHit);
		List<OntologyTerm> childrenOfHit = ontologyService.getChildren(
				this.currentHit.getOntologyAccession(),
				this.currentHit.getAccession());

		Recommendation recommendation = new Recommendation(hitsCounter,
				currentHit, conceptIdsMatch, labelsMatch, searchedTerm,
				hierarchyOfHdotClass, hierarchyOfHit, hdotOntology, hdotModule,
				counterForParents, matchedConcept, definitions, synonyms,
				childrenOfHit, numMatchedParents);

		// log.info(recommendation.toString());

		return recommendation;
	}

	/**
	 * @param matchedConcept
	 *            the match found in HDOT
	 * @return true if the match is the found concept
	 */
	private void isMatchedClassTheSearchedTerm(OntologyTerm matchedConcept) {

		if (counterForParents == 0) {
			log.error("\n\n");
			log.error("##############################################################################################################################");
			log.error("\t\t\t\tOAT SHOULD NOT BE EVOKED! Since:");
			log.error("The concept: " + matchedConcept + " already exists.");
			log.error("##############################################################################################################################");
			log.error("\n\n");
			long end = System.currentTimeMillis();

			long milliseconds = (end - start);

			long seconds = (milliseconds / 1000);

			long mins = seconds / 60;
			long restsecs = seconds % 60;

			log.info("Execution time was " + (end - start) + " ms.");
			log.info("Execution time was " + mins + ":" + restsecs + " sec.");

			File logSearchFile = new File(Configuration.LOG_PATH.resolve(
					"loggingSearchEngine.html").toURI());

			File logRecommendFile = new File(Configuration.LOG_PATH.resolve(
					"loggingRecommendationGeneration.html").toURI());

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
			String date = dateFormat.format(new Date());

			logSearchFile.renameTo(new File(Configuration.LOG_PATH.resolve(
					date + "_" + searchedTerm.replace(" ", "_") + "_loggingSearchEngine.html")
					.toURI()));

			logRecommendFile
					.renameTo(new File(Configuration.LOG_PATH.resolve(
							date + "_" + searchedTerm.replace(" ", "_")
									+ "_loggingRecommendationGeneration.html")
							.toURI()));

			log.info("Done.");
			log.info("Log messages written in: "
					+ Configuration.LOG_PATH.toString() + date + "_"
					+ searchedTerm.replace(" ", "_") + "_loggingSearchEngine.html and " + date
					+ "_" + searchedTerm.replace(" ", "_")
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
	 * @throws IOException
	 */
	private OntologyTerm findMatch(OntologyTerm currentCandidate,
			OWLOntology currentOntology) throws URISyntaxException,
			OntologyServiceException, IOException {

		numMatchedParents = 1;
		Set<OWLClass> classesInSignature = currentOntology
				.getClassesInSignature();

		OntologyTerm matchedTerm = null;

		// iterate over all classes of the ontology and try to find a match of
		// uri or label
		for (OWLClass hdotClass : classesInSignature) {
			// exclude class Nothing in hdot
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
			conceptIdsMatch = compareIds(currentCandidate.getURI().toString(),
					hdotClass.toStringID());

			if (conceptIdsMatch) {
				log.info("\tCONCEPT IDs of BioPortal HIT and HDOT CLASS MATCH");
				// log.debug("id of current candidate: "
				// + currentCandidate.getURI().toString());
				// log.debug("id of hdot class: " + hdotClass.toStringID());
				// conceptIdsMatch = true;

				// if we find a match then we will retrieve this concept
				matchedTerm = new OntologyTerm();
				matchedTerm.setAccession(hdotClass.toStringID());
			}

			// compare labels
			if (currentCandidate.getLabel().isEmpty()) {
				continue;
			}
			labelsMatch = compareLabels(currentCandidate.getLabel().trim(),
					pureLabelOfHdotClass.trim());

			if (labelsMatch) {

				log.info("\tLABELS of BioPortal HIT and HDOT CLASS MATCH");
				// log.info("pureLabelOfHdotClass: " + pureLabelOfHdotClass);
				//
				// log.info("label of currentCandidate: "
				// + currentCandidate.getLabel());
				if (conceptIdsMatch) {
					log.info("\tIDs and LABELS MATCH");
				}
				if (matchedTerm == null) {
					matchedTerm = new OntologyTerm();
				}
				matchedTerm.setLabel(pureLabelOfHdotClass);

			}
			// check after the comparison if a match was found
			// to collect the hierarchy and set the accessions
			if (matchedTerm != null) {
				log.info("_________________________________________________");

				matchedTerm.setURI(new URI(hdotClass.toStringID()));
				matchedTerm.setLabel(pureLabelOfHdotClass);
				matchedTerm.setOntologyAccession(currentOntology
						.getOntologyID().getOntologyIRI().toString());

				log.info("\tparent of the current hit: "
						+ currentCandidate.getURI().toString() + "\t"
						+ currentCandidate.getLabel());
				log.info("\tmatched class of hdot: "
						+ matchedTerm.getURI().toString() + "\t"
						+ matchedTerm.getLabel());

				isMatchedClassTheSearchedTerm(matchedTerm);

				extractHierarchyOfMatchedTerm(currentOntology, hdotClass);

				countMatchingParents();

				if (!(extensionConditionsAreSatisfied(currentOntology,
						hdotClass, matchedTerm))) {
					matchedTerm = null;
					continue;
				} else {
					listOfRecommendations.add(buildRecommendaton(
							currentOntology, matchedTerm));
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

	private boolean extensionConditionsAreSatisfied(
			OWLOntology currentOntology, OWLClass hdotClass,
			OntologyTerm matchedTerm) throws IOException,
			OntologyServiceException {

		boolean result = true;

		// check if the current class is hdot_core
		boolean isHdotCore = (currentOntology.getOntologyID().getOntologyIRI()
				.toString().contains(FileUtils.readFileToString(new File(
				Configuration.CORE_MODULE_FILE.toURI()))));

		Recommendation recommendation = buildRecommendaton(currentOntology,
				matchedTerm);

		// if the match is found in hdot core and the matched class is
		// not a leaf node continue with the next class
		if (isHdotCore) {
			if (!hdotClass.getSubClasses(currentOntology).isEmpty()) {

				log.debug("THE MATCHED CLASS WAS FOUND IN HDOT_CORE BUT IT IS NOT A LEAF NODE");

				// listOfInCoreNotLeafMatches.add(notification);
				listOfInCoreNotLeafMatches.add(recommendation);
				log.debug("search for further matches ...\n");

				result = false;
			}
		}

		if (hdotClass.getSuperClasses(currentOntology).size() == 0) {
			log.debug("THE MATCHED CLASS IS IMPORTED IN THIS MODULE");

			if (!(hdotClass.getSubClasses(ontologyManager.getOntologies())
					.isEmpty())) {
				log.debug("BUT IT IS NOT A LEAF NODE");
				log.debug("search for further matches ...\n");

				listImportedNotLeafMatches.add(recommendation);
				result = false;
			}
		}

		if (importedOntologies.contains(currentOntology.getOntologyID()
				.getOntologyIRI().toString())) {
			log.info("RECOMMENDATION POSSIBLE ONLY IN CORE OF HDOT");
			listOfRecsPossibleInCoreOfHDOT.add(recommendation);
			result = false;
		}

		return result;
	}

	private void countMatchingParents() throws IOException {

		Map<String, String> urisTOLabels = new HashMap<>();

		for (OntologyTerm ontologyTerm : hierarchyOfHit) {
			urisTOLabels.put(ontologyTerm.getURI().toString(), ontologyTerm
					.getLabel().toLowerCase());
		}

		for (OWLClass classInHierarchy : hierarchyOfHdotClass) {
			String uri = classInHierarchy.toStringID();
			// String label =
			// retriveRdfsLabel(classInHierarchy.getAnnotations(currentOntology));
			String label = "";
			// get the labels of the parents to display them
			for (OWLOntology currOnto : hdotOntology.getImports()) {
				if (!classInHierarchy.getAnnotations(currOnto).isEmpty()) {
					label = retriveRdfsLabel(classInHierarchy
							.getAnnotations(currOnto));
					break;
				}
			}
			label = label.toLowerCase();

			if (urisTOLabels.containsKey(uri)
					|| (!(label.isEmpty()) && urisTOLabels.containsValue(label))) {
				numMatchedParents++;
			}
		}
		// log.debug("number of matching parents: " + numMatchedParents);
	}

	private boolean compareLabels(String currentLabel,
			String pureLabelOfHdotClass) {

		if (currentLabel.isEmpty() || pureLabelOfHdotClass.isEmpty()) {
			return false;
		}
		boolean result = false;
		if (currentLabel.trim().equalsIgnoreCase(pureLabelOfHdotClass.trim())) {
			result = true;

		} else {
			double similarityOfLabels = DiceCoefficient
					.diceCoefficientOptimized(currentLabel.toLowerCase(),
							pureLabelOfHdotClass.toLowerCase());
			if (similarityOfLabels > 0.95) {

				log.info("\nLABELS of BioPortal HIT and HDOT CLASS SIMILARITY > 95\n");
				// log.info("pureLabelOfHdotClass: " + pureLabelOfHdotClass);
				//
				// log.info("label of currentCandidate: " + currentLabel);
				log.info("similarity of labels: " + similarityOfLabels);
				result = true;
			}
		}
		return result;
	}

	private boolean compareIds(String id1, String id2) {
		return id1.equals(id2);
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
					.getSuperClasses(ontologyManager.getOntologies());

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
		return ontologyManager;
	}

	public OWLOntology getHdot_ontology() {
		return hdotOntology;
	}

	public OntologyService getOntologyService() {
		return ontologyService;
	}

}