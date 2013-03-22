package org.ifomis.ontologyaggregator.search;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import uk.ac.ebi.ontocat.OntologyService;
import uk.ac.ebi.ontocat.OntologyServiceException;
import uk.ac.ebi.ontocat.OntologyTerm;
import uk.ac.ebi.ontocat.bioportal.BioportalOntologyService;
import uk.ac.ebi.ontocat.bioportal.xmlbeans.ConceptBean;
import uk.ac.ebi.ontocat.virtual.CompositeServiceNoThreads;
import uk.ac.ebi.ontocat.virtual.SortedSubsetDecorator;

/**
 * The search engine searches a term via the REST interface in BioPortal with
 * respect to a predefined list of ontologies.
 * 
 * @author Nikolina
 * 
 */
public class SearchEngine {

	/**
	 * list of preferred ontologies
	 */
	private List<String> ontologiesList;

	/**
	 * list of terms
	 */
	private List<String> termsList;

	private static final Logger log = Logger.getLogger(SearchEngine.class);

	/**
	 * The date format for the time stamp.
	 */
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");

	/**
	 * The time as a unique stamp in order to distinguish the produced files.
	 */
	private String date = dateFormat.format(new Date());

	/**
	 * Stores the stacks that keep the paths to root for the searched terms.
	 */
	private List<Stack<OntologyTerm>> listOfPaths;

	/**
	 * the current term for which we run the search
	 */
	private String searchedTerm;

	private OntologyService restrictedBps;

	public OntologyService getRestrictedBps() {
		return restrictedBps;
	}

	public SearchEngine(String fileWithOntologies, String fileWithTerms)
			throws IOException {

		File fileOntologies = new File(fileWithOntologies);

		ontologiesList = FileUtils.readLines(fileOntologies);

		log.info("List of ontologies imported from file: " + fileWithOntologies);

		File fileTerms = new File(fileWithTerms);

		setTermsList(FileUtils.readLines(fileTerms));

		log.info("List of terms imported from file: " + fileWithTerms);
	}

	/**
	 * Searches the given term in BioPortal. The the ontologyList specifies the
	 * sorting of the ontologies.
	 * 
	 * @return list of stacks that store the root paths of the first 5 hits
	 * 
	 * @throws OntologyServiceException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public List<Stack<OntologyTerm>> searchTermInBioPortal(String term)
			throws OntologyServiceException, IOException, URISyntaxException {

		OntologyService bps = CompositeServiceNoThreads
				.getService(new BioportalOntologyService());

		// create a restricted BioPortal ontology service that searches in the
		// ontologies specified in the given list and considers their order
		 restrictedBps = SortedSubsetDecorator.getService(bps,
				this.ontologiesList);

		log.debug("Ontology service according to predefined list of ontologies is created");

		this.searchedTerm = term;

		log.info("Search for term: " + term);

		List<OntologyTerm> listWithHits = restrictedBps.searchAll(term);

		RootpathExtractor pathExtractor = new RootpathExtractor(searchedTerm,
				date);
		if (listWithHits == null) {
			log.info("No results for " + term);
			// continue;
			System.exit(0);
		}

		log.info(listWithHits.size() + " hits for the searched term: " + term);
		FileUtils.writeLines(new File("data/listsWithHits/" + searchedTerm
				+ "_listOfHits.txt"), listWithHits);
		listOfPaths = new ArrayList<Stack<OntologyTerm>>();

		int counterForQueriesRootPath = 0;
		int totalCounterForQueriesRootPath = 0;
		int totalEmptyResponces = 0;
		int threshold = 10;

		for (int i = 0; i < listWithHits.size(); i++) {
			OntologyTerm ot = listWithHits.get(i);

			StringBuffer sb = new StringBuffer();
			StringBuffer sb1 = new StringBuffer();
			sb.append("Found: ");
			sb.append(ot);
			sb.append(";\n Similarity to query: ");
			sb.append(ot.getContext().getSimilarityScore());
			sb.append("%");
			sb.append(";\n Term source: ");
			sb.append(ot.getContext().getServiceType());
			log.debug(sb.toString());

			int similarityScore = ot.getContext().getSimilarityScore();
			if (similarityScore > 90) {

				sb1.append("\n*****************");
				sb1.append("\nSimilatity score of searched term and hit is greater than 90%.");
				sb1.append("\nOntology of concept given for root path extraction = ");
				sb1.append(ot.getOntology().getLabel());
				sb1.append("\naccession of the given concept = ");
				sb1.append(ot.getAccession());
				sb1.append("\nlabel of the given concept = ");
				sb1.append(ot.getAccession());
				sb1.append(";\n This term is going to be processed further.");

				log.info(sb1.toString());

				++counterForQueriesRootPath;
				++totalCounterForQueriesRootPath;

				Stack<OntologyTerm> stackPath = pathExtractor.querySparql(ot
						.getOntology().getAbbreviation(), ot);

				listOfPaths.add(stackPath);

				if (counterForQueriesRootPath == threshold) {
					int emptyResponces = pathExtractor
							.getCounterForEmptyResonses();
					totalEmptyResponces += emptyResponces;
					if (emptyResponces > 0) {
						threshold = emptyResponces;
						// set the counter for empty responses to 0 otherwise to
						// many queries are sent
						pathExtractor.setCounterForEmptyResonses(0);
						counterForQueriesRootPath = 0;
						continue;
					} else {
						break;
					}
				}
			}
			log.debug("_____________________________________________");
		}
		log.info("skos:prefLabels= " + pathExtractor.getCounterForPrefLabels());
		log.info("rdfs:labels= " + pathExtractor.getCounterForLabels());

		log.info(totalCounterForQueriesRootPath
				+ " queries for the root path were sent.");

		log.info(totalEmptyResponces + " empty responses from sparql.");

		FileUtils.write(new File("sparql/fail/" + this.date + "_" + term
				+ "_fail"), pathExtractor.getSbFailed().toString());
		FileUtils.write(new File("sparql/success/" + this.date + "_" + term
				+ "_success"), pathExtractor.getSbSuccess().toString());
		// FileUtils.writeLines(new File("data/testPath"), listOfPaths);

		return listOfPaths;
	}

	public List<Stack<OntologyTerm>> getListOfPaths() {
		return this.listOfPaths;
	}

	public String getCurrentTerm() {
		return searchedTerm;
	}

	public List<String> getTermsList() {
		return termsList;
	}

	public void setTermsList(List<String> termsList) {
		this.termsList = termsList;
	}
}