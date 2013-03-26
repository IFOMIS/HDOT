package org.ifomis.ontologyaggregator.search;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.ncbo.stanford.sparql.examples.SimpleTest;

import uk.ac.ebi.ontocat.OntologyTerm;

/**
 * Extracts the path to root for a hit in a given ontology found in the BioPortal
 * using a SPARQL query.
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

	private int counterForPrefLabels = 0;
	private int counterForLabels = 0;
	private List<Stack<OntologyTerm>> listOfPaths;
	

	public RootpathExtractor(String currentTerm, String date) {
		this.searchedTerm = currentTerm;
		this.date = date;
		sbFailed = new StringBuffer();
		sbSuccess = new StringBuffer();

	}

	/**
	 * Construct and execute a query for the sparql endpoint of BioPortal that
	 * queries for the path from the given ontology term to the root.
	 * 
	 * @param ontologyAbbreviation
	 *            the ontology where the term was found
	 * @param ot
	 *            the hit
	 * @throws URISyntaxException
	 */
	public Stack<OntologyTerm> querySparql(String ontologyAbbreviation,
			OntologyTerm ot) throws URISyntaxException {
		List<String> parentLabels = new ArrayList<String>();
		List<OntologyTerm> path = new ArrayList<OntologyTerm>();

		String sparqlService = "http://sparql.bioontology.org/sparql";
		String apikey = "063917f3-04c3-4f7c-8773-7187081c1b11";

		// capitalize the ontology abbreviation such that it works
		ontologyAbbreviation = ontologyAbbreviation.toUpperCase();

		// insert the concept into the path
		path.add(ot);

		String conceptFullId = ot.getURI().toString();
		String parent = conceptFullId;
		String label = "";
		String prefLabel = "";
		String response = "";
		String query = "";
		List<String> listOfqueries = new ArrayList<String>();

		while (!parent.equals("")) {

			// for the first time the parent is the term we are looking for
			// set LIMIT to 100 since sometimes the limit (40) causes problems
			response = "";
			label = "";
			prefLabel = "";
			
			query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
					+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"
					+ "SELECT * WHERE { "
					+ "GRAPH <http://bioportal.bioontology.org/ontologies/"
					+ ontologyAbbreviation
					+ "> { "
					+ "<"
					+ parent
					+ "> rdfs:subClassOf ?parent."
					+ "OPTIONAL {?parent rdfs:label ?label}. OPTIONAL {?parent skos:prefLabel ?prefLabel}.}} LIMIT 100";

			listOfqueries.add(query);
			// log.debug("query:"+ query);

			SimpleTest test = new SimpleTest(sparqlService, apikey);

			// Accept formats can be: "text/plain", "application/json",
			// "application/rdfxml", "text/csv", text/tab-separated-values
			try {
				response = test
						.executeQuery(query, "text/tab-separated-values");
//				log.debug("response:\n " + response);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// parse response
			String[] lines = response.split("\n");
			
			//the first line is always the name of the variables to be returned
			if (lines.length == 1) {
				// no results
				parent = "";
				break;
//			} else if (lines.length == 1) {
			} else {
				//TODO parse all paths to root
				// take always the last returned parent
				
				String line = lines[lines.length - 1];

				String[] lineTokens = line.split("\t");
				// in the first column we receive the parent of the
				// searched concept
				parent = lineTokens[0].substring(1, lineTokens[0].length() - 1);
				// log.debug("parent in else: " + parent);

				switch (lineTokens.length) {
				case 2:
					label = lineTokens[1];
					++counterForLabels;
					break;
				case 3:
					label = lineTokens[1];
					prefLabel = lineTokens[2];
					++counterForPrefLabels;
					++counterForLabels;
					if (label.isEmpty()) {
						--counterForLabels;
					}
					break;
				default:
					break;
				}

				// before leaving the loop put the entry into the list
				// String entry = parent + "\t" + label;
				// parentLabels.add(entry);
				parentLabels.add(line);

				OntologyTerm concept = new OntologyTerm();
//				log.debug("URI of parent:" + parent);
//				log.debug("label: " + label);
//				log.debug("prefLabel: " + prefLabel);
				
				String labelToBeSet = "";
				
				if(!label.isEmpty()){
					 labelToBeSet = label.split("\"")[1];
				}else if(!prefLabel.isEmpty()) {
					labelToBeSet = prefLabel.split("\"")[1];
				}
//				log.debug("lableToBeSet: " + labelToBeSet);	
				concept.setURI(new URI(parent));
				concept.setLabel(labelToBeSet);
				concept.setOntologyAccession(ot.getOntologyAccession());
				path.add(concept);
			}
		}
		// write the output into a file
		try {
			writePathToRootIntoFile(ontologyAbbreviation, ot, parentLabels,
					listOfqueries);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// store the path into a stack and return it
		Stack<OntologyTerm> stack = new Stack<OntologyTerm>();
		for (int i = path.size() - 1; i >= 0; i--) {
			OntologyTerm ontoTerm = path.get(i);
			stack.push(ontoTerm);
		}
		return stack;
	}



	/**
	 * Writes the extracted path and the executed queries into text files.
	 * 
	 * @param ontologyAbbreviation
	 *            the ontology where the term was found
	 * @param ot
	 *            the hit
	 * @param parentLabels
	 *            list that contains the parents of the hit with their labels
	 *            separates by \t
	 * @param listOfQueries
	 *            the list of executed queries
	 * @throws IOException
	 */
	private void writePathToRootIntoFile(String ontologyAbbreviation,
			OntologyTerm ot, List<String> parentLabels,
			List<String> listOfQueries) throws IOException {

		File outFile = new File("sparql/" + this.date + "_" + searchedTerm
				+ "/" + ontologyAbbreviation + "-" + ot.getLabel().replace("/", "_or_"));

		new File("sparql/queries/" + this.date + "_" + searchedTerm).mkdir();

		// write the queries executed in this iteration for the particular
		// term in a file in order to be able to look at them
		File outQueryFile = new File("sparql/queries/" + this.date + "_"
				+ searchedTerm + "/" + ontologyAbbreviation + "-"
				+ ot.getLabel());
		FileUtils.writeLines(outQueryFile, listOfQueries, "\n");
		FileUtils.writeLines(outFile, parentLabels);

		if (outFile.length() == 0) {
			log.info("for the term: " + ot.toString());
			log.info("SPARQL response is empty");

			++counterForEmptyResonses;
			this.sbFailed.append(ontologyAbbreviation + "\t" + ot.getURI()
					+ "\n");
		} else {
			this.sbSuccess.append(ontologyAbbreviation + "\t" + ot.getURI()
					+ "\n");
		}
		log.debug("Output of query written in " + outFile);
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

	public int getCounterForPrefLabels() {
		return counterForPrefLabels;
	}

	public int getCounterForLabels() {
		return counterForLabels;
	}

}
