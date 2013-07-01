package org.ifomis.ontologyaggregator.search;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.ncbo.stanford.sparql.examples.SimpleTest;

import uk.ac.ebi.ontocat.OntologyTerm;

/**
 * Extracts the path to root for a hit in a given ontology found in the
 * BioPortal using a SPARQL query.
 * 
 * @author Nikolina
 * 
 */
public class RootpathExtractor {
	private static final Logger log = Logger.getLogger(RootpathExtractor.class);

	/**
	 * the term that the user searches
	 */
	private String searchedTerm;

	/**
	 * counts the empty responses from SPARQL
	 */
	private int counterForEmptyResonses = 0;

	/**
	 * time stamp needed for uniqueness of the names of created files
	 */
	private String date;

	/**
	 * Writes the queries that failed.
	 */
	private StringBuffer sbFailed;

	/**
	 * Writes the queries that succeed.
	 */
	private StringBuffer sbSuccess;

	private Stack<OntologyTerm> path = new Stack<>();
	private Set<OntologyTerm> onPath = new HashSet<>();

	private int counterForHitsThatDoNotHaveAnyPath = 0;
	private int counterForPrefLabels = 0;
	private int counterForLabels = 0;
	private List<Stack<OntologyTerm>> listOfPaths;

	private String ontologyAbbreviation;

	private List<String> listOfQueries;

	public RootpathExtractor(String currentTerm, String date) {
		this.searchedTerm = currentTerm;
		this.date = date;
		sbFailed = new StringBuffer();
		sbSuccess = new StringBuffer();

		listOfQueries = new ArrayList<>();
	}

	public List<Stack<OntologyTerm>> computeAllPaths(
			String ontologyAbbreviation, OntologyTerm ot) throws Exception {

		// counterForHitsThatDoNotHaveAnyPath = 0;
		listOfPaths = new ArrayList<>();

		this.ontologyAbbreviation = ontologyAbbreviation.toUpperCase();

		enumerate(ot);
		writePathToRootIntoFile(this.ontologyAbbreviation, ot);

		if (listOfPaths.isEmpty()) {
			++counterForHitsThatDoNotHaveAnyPath;
		}
		log.info("\t\tnumber of extracted paths: " + listOfPaths.size());
		log.info("---------------------------------------------------------------------");

		return listOfPaths;
	}

	private void enumerate(OntologyTerm ot) throws Exception {
		path.push(ot);
		onPath.add(ot);

		// log.debug("conceptFullID: " + ot.getURI().toString());

		String response = executeSparqlQuery(ot.getURI().toString());

		List<String> parents = parseSparqlResponse(response);
		
		//there are no parents retrieved by the last SPARQL query
		if (parents.size() == 0) {
//			 log.info("path" + path);

			Stack<OntologyTerm> deepCopyOfPath = getDeepReverseCopy(path);
			// log.info("path.peek(): " + deepCopyOfPath.peek());
			if (!(deepCopyOfPath.size() <= 1)) {
				// log.debug("!!!!!soll nicht leer sein!!!!!!!!!!!!!! " +
				// deepCopyOfPath );

				listOfPaths.add(deepCopyOfPath);
			} else {
//				 log.debug("!!!!!!!soll leer sein!!!!!!!!!!!! " +
//				 deepCopyOfPath );

				++counterForEmptyResonses;
			}
		}
		for (String parent : parents) {
			// log.debug("parent" + parent);
			// exclude exceptions
			// if(parent.contains("hit complexity limit")){
			// continue;
			// }
			String[] parentTokens = parent.split("\t");

			String parentURI = parentTokens[0].substring(1,
					parentTokens[0].length() - 1);
			// log.debug("parentURI" + parentURI);

			OntologyTerm otParent = generateOntologyTerm(parentTokens, ot);

			if (!onPath.contains(parentURI)) {

				enumerate(otParent);
			}
		}
		path.pop();
		onPath.remove(ot);
	}

	/**
	 * Copies the given path.
	 * 
	 * @param path
	 *            the path to be copied
	 * @return a deep copy of the given path
	 */
	private Stack<OntologyTerm> getDeepReverseCopy(Stack<OntologyTerm> path) {
		Stack<OntologyTerm> deepCopy = new Stack<>();

		for (int i = path.size() - 1; i >= 0; i--) {
			deepCopy.push(path.elementAt(i));
		}

		return deepCopy;
	}

	/**
	 * Generates an @link{OntologyTerm} from the given array.
	 * 
	 * @param parentTokens
	 *            an array that contains the values of the response returned by
	 *            sparql
	 * @param ot
	 *            the actual hit needed to get the ontology accession
	 * @return an @link{OntologyTerm}
	 * @throws URISyntaxException
	 */
	private OntologyTerm generateOntologyTerm(String[] parentTokens,
			OntologyTerm ot) throws URISyntaxException {
		OntologyTerm concept = new OntologyTerm();

		String label = "";
		String prefLabel = "";

		switch (parentTokens.length) {
		case 2:
			label = parentTokens[1];
			++counterForLabels;
			break;
		case 3:
			label = parentTokens[1];
			prefLabel = parentTokens[2];
			++counterForPrefLabels;
			++counterForLabels;
			if (label.isEmpty()) {
				--counterForLabels;
			}
			break;
		default:
			break;
		}

		String labelToBeSet = "";
		// String labelToBeSet = parentTokens[1];
		if (!label.isEmpty()) {
			labelToBeSet = label.split("\"")[1];
		} else if (!prefLabel.isEmpty()) {
			labelToBeSet = prefLabel.split("\"")[1];
		}
		// log.debug("!!!! string for generatng URI: !!!"
		// + parentTokens[0].substring(1, parentTokens[0].length() - 1));

		concept.setURI(new URI(parentTokens[0].substring(1,
				parentTokens[0].length() - 1)));
		concept.setLabel(labelToBeSet);
		concept.setOntologyAccession(ot.getOntologyAccession());

		return concept;
	}

	private List<String> parseSparqlResponse(String response) {
		Set<String> parentURIs = new HashSet<>();

		String[] lines = response.split("\n");
		List<String> linesWOHeader = new ArrayList<>();

		// start by 1 since line 0 contains always the names of the
		// variables
		for (int i = 1; i < lines.length; i++) {
			String parentUri = lines[i].split("\t")[0];

			if (!parentURIs.contains(parentUri)) {
				linesWOHeader.add(lines[i]);
			}
			parentURIs.add(parentUri);
		}
		return linesWOHeader;
	}

	private String executeSparqlQuery(String parent) throws Exception {
		String sparqlService = "http://sparql.bioontology.org/sparql";
		String apikey = "063917f3-04c3-4f7c-8773-7187081c1b11";

		// Accept formats can be: "text/plain", "application/json",
		// "application/rdfxml", "text/csv", text/tab-separated-values

		SimpleTest test = new SimpleTest(sparqlService, apikey);

		// for the first time the parent is the term we are looking for
		// set LIMIT to 100 since sometimes the limit (40) causes problems
		String query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"
				+ "SELECT * WHERE { "
				+ "GRAPH <http://bioportal.bioontology.org/ontologies/"
				+ ontologyAbbreviation
				+ "> { "
				+ "<"
				+ parent
				+ "> rdfs:subClassOf ?parent."
				+ "OPTIONAL {?parent rdfs:label ?label}. OPTIONAL {?parent skos:prefLabel ?prefLabel}.}"
				+ "FILTER (!isBlank(?parent))"
				// + "FILTER (str(?label) = lcase(?label))"
				+ "} LIMIT 100";

		// String query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
		// + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"
		// + "SELECT DISTINCT ?parent, ?label WHERE {"
		// + "GRAPH <http://bioportal.bioontology.org/ontologies/"
		// + this.ontologyAbbreviation
		// + "> { <"
		// + parent
		// + "> rdfs:subClassOf ?parent."
		// + "OPTIONAL { ?parent ?p ?o. }"
		// + "BIND (lcase(str(?o)) AS ?label)"
		// +
		// "FILTER (!isBlank(?parent) && ?p IN (rdfs:label, skos:prefLabel)) }}";
		String response = test.executeQuery(query, "text/tab-separated-values");
		// log.debug("response:\n " + response);
		listOfQueries.add(query);

		return response;
	}

	/**
	 * Writes the extracted path and the executed queries into text files.
	 * 
	 * @param ontologyAbbreviation
	 *            the ontology where the term was found
	 * @param ot
	 *            the hit
	 * @param listOfPaths
	 *            list that contains the parents of the hit with their labels
	 *            separates by \t
	 * @param listOfQueries
	 *            the list of executed queries
	 * @throws IOException
	 */
	private void writePathToRootIntoFile(String ontologyAbbreviation,
			OntologyTerm ot) throws IOException {

		File outFile = new File("sparql/" + this.date + "_" + searchedTerm
				+ "/" + ontologyAbbreviation + "-"
				+ ot.getLabel().replace("/", "_or_"));

		new File("sparql/queries/" + this.date + "_" + searchedTerm).mkdir();

		// write the queries executed in this iteration for the particular
		// term in a file in order to be able to look at them
		File outQueryFile = new File("sparql/queries/" + this.date + "_"
				+ searchedTerm + "/" + ontologyAbbreviation + "-"
				+ ot.getLabel());
		FileUtils.writeLines(outQueryFile, listOfQueries, "\n");
		FileUtils.writeLines(outFile, listOfPaths);

		if (outFile.length() == 0) {
			log.info("\t\tfor the term: " + ot.toString());
			log.info("\t\tSPARQL response is empty");

//			++counterForEmptyResonses;
			this.sbFailed.append(ontologyAbbreviation + "\t" + ot.getURI()
					+ "\n");
		} else {
			this.sbSuccess.append(ontologyAbbreviation + "\t" + ot.getURI()
					+ "\n");
		}
		log.debug("Output of query is written in " + outFile);
	}

	public int getCounterForEmptyResonses() {
		return counterForEmptyResonses;
	}

	public StringBuffer getSbFailed() {
		return sbFailed;
	}

	public StringBuffer getSbSuccess() {
		return sbSuccess;
	}

	public void setCounterForEmptyResonses(int value) {
		this.counterForEmptyResonses = 0;
	}

	public int getCounterForHitsThatDoNotHaveAnyPath() {
		return counterForHitsThatDoNotHaveAnyPath;
	}

	public void setCounterForHitsThatDoNotHaveAnyPath(int i) {
		this.counterForHitsThatDoNotHaveAnyPath = 0;
	}

	public int getCounterForPrefLabels() {
		return counterForPrefLabels;
	}

	public int getCounterForLabels() {
		return counterForLabels;
	}

	public static void main(String[] args) throws Exception {
		RootpathExtractor rpe = new RootpathExtractor("blood", "");
		rpe.computeAllPaths("RCD", new OntologyTerm("", "", "Blood", new URI(
				"http://purl.bioontology.org/ontology/RCD/X79cn")));
	}

}
