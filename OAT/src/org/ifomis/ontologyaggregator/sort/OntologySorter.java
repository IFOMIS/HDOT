package org.ifomis.ontologyaggregator.sort;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.ifomis.ontologyaggregator.parsing.MetricsParser;
import org.ifomis.ontologyaggregator.util.Configuration;

import uk.ac.ebi.ontocat.Ontology;
import uk.ac.ebi.ontocat.OntologyService;
import uk.ac.ebi.ontocat.OntologyServiceException;
import uk.ac.ebi.ontocat.bioportal.BioportalOntologyService;
import uk.ac.ebi.ontocat.bioportal.xmlbeans.MetricsBean;
import uk.ac.ebi.ontocat.bioportal.xmlbeans.OntologyBean;
import uk.ac.ebi.ontocat.virtual.CompositeServiceNoThreads;

/**
 * Sorts a list of ontologies according to predefined criteria.
 * 
 * @author Nikolina
 * 
 */
public class OntologySorter {
	Logger log = Logger.getLogger(OntologySorter.class);

	List<OntologyBean> listOfOntologyBeans;

	/**
	 * Creates an object OntologySorter. Thereby fetches all ontologies from the
	 * BioPortal repository together with their metrics.
	 * 
	 * @throws OntologyServiceException
	 */
	public OntologySorter() throws OntologyServiceException {

		OntologyService bps = CompositeServiceNoThreads
				.getService(new BioportalOntologyService());

		log.debug("BioPortalService instanciated");
		System.out.println("BioPortalService instanciated");
		List<Ontology> listOfOntologies = bps.getOntologies();
		log.info("number of ontologies returned by bps: "
				+ listOfOntologies.size());

		this.listOfOntologyBeans = new ArrayList<OntologyBean>();

		for (int i = 0; i < listOfOntologies.size(); i++) {
			OntologyBean ontologyBean = (OntologyBean) listOfOntologies.get(i);
			this.listOfOntologyBeans.add(ontologyBean);
		}

		// get the metrics
		for (OntologyBean ontologyBean : listOfOntologyBeans) {
			// rule out ontologies that slow down the process
			if (ontologyBean.getAbbreviation().equals("ICD10PCS")
					|| ontologyBean.getAbbreviation().equals("SCTSPA"))
				continue;

			String metricsUrl = createMetricsUrl(ontologyBean.getId());
			MetricsParser parser = new MetricsParser();
			MetricsBean metricsBean = parser.parse(metricsUrl);
			ontologyBean.setMetricsBean(metricsBean);
		}
	}

	/**
	 * Sorts the ontologies of gotten from BioPortal. A ChainComparator that a
	 * chain of different comparators is created and the ontologies are sorted
	 * according the criteria encoded there.
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void sortOntologyBeans() throws IOException {
		log.debug("Start sorting ontologies.");
		FileUtils.writeLines(
				new File(Configuration.DATA_PATH.resolve(
						"orderbeforeRunningChainComparator.txt").toURI()),
				listOfOntologyBeans);

		ComparatorChain comparatorChain = new ComparatorChain();

		comparatorChain.addComparator(new ComparatorPredefinedList(), true); // C1
		comparatorChain.addComparator(new ComparatorOBOFoundry(), true); // C2
		// comparatorChain.addComparator(new ComparatorBFO());
		comparatorChain.addComparator(new ComparatorDateRelease(), true); // C3
		comparatorChain.addComparator(new ComparatorIsFlat(), true); // C4
		comparatorChain.addComparator(new ComparatorIsMetadata(), true); // C5
		comparatorChain
				.addComparator(new ComparatorClassesWithNoAuthor(), true); // C6
		comparatorChain.addComparator(
				new ComparatorClassesWithNoDocumentation(), true); // C7
		comparatorChain.addComparator(new ComparatorDepthOfHierarchy(), true); // C8
		comparatorChain.addComparator(new ComparatorOneSubclass(), true); // C9

		Collections.sort(listOfOntologyBeans, comparatorChain);

		// collect accessions
		List<String> listOfAccessions = new ArrayList<String>();
		for (int i = 0; i < listOfOntologyBeans.size(); i++) {
			OntologyBean ontoBean = listOfOntologyBeans.get(i);
			listOfAccessions.add(ontoBean.getOntologyAccession());
		}
		// write the accessions in a file since it is needed for the search
		// engine
		FileUtils.writeLines(new File(Configuration.ONTO_IDS_FILE.toURI()),
				listOfAccessions);

		// write the sorted ontologies in a file so that they can be viewed
		FileUtils.writeLines(
				new File(Configuration.CONF_PATH
						.resolve("ontologySortingC1-9.txt").toURI()),
				listOfOntologyBeans);

		log.info(listOfOntologyBeans + "ontologies were compared");
	}

	/**
	 * Constructs the url for getting the metrics from the REST interface.
	 * 
	 * @param id
	 *            the id of the ontology for which the metrics are needed
	 * @return
	 */
	private String createMetricsUrl(String id) {
		String urlPefix = "http://rest.bioontology.org/bioportal/ontologies/metrics/";

		String apikey = "c6ae1b27-9f86-4e3c-9dcf-087e1156eabe";

		String url = urlPefix + id + "?apikey=" + apikey;

		return url;
	}
}
