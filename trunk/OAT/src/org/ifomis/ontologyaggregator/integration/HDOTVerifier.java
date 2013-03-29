package org.ifomis.ontologyaggregator.integration;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

public class HDOTVerifier {
	
	public boolean verifyOntology(OWLOntology ontologyForVerification) {
        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        
        ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
        
        OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
        
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontologyForVerification, config);

        reasoner.precomputeInferences();
        
        boolean consistent = reasoner.isConsistent();
        System.out.println("Consistent: " + consistent);
        return consistent;
        
	}
}
