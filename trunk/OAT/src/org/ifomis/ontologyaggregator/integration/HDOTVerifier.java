package org.ifomis.ontologyaggregator.integration;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

/**
 * Verifies if the extended ontology is consistent by runnning a reasoner.
 * @author Nikolina
 *
 */
public class HDOTVerifier {
	private static final Logger log = Logger
			.getLogger(HDOTVerifier.class);
	
	public boolean verifyOntology(OWLOntology ontologyForVerification) {
        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        
        ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
        
        OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
        
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontologyForVerification, config);

        reasoner.precomputeInferences();
        
        boolean consistent = reasoner.isConsistent();
        log.info("Consistent: " + consistent);
        return consistent;
        
	}
}
