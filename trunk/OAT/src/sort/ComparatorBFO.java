package sort;

import java.util.Comparator;

import uk.ac.ebi.ontocat.bioportal.xmlbeans.OntologyBean;

public class ComparatorBFO implements Comparator<OntologyBean> {

	@Override
	public int compare(OntologyBean o1, OntologyBean o2) {
		// TODO
		boolean o1InBFO = (o1.getOntologyId().contains("bfo") || o1
				.getOntologyId().contains("BFO"));
		boolean o2InBFO = (o2.getOntologyId().contains("bfo") || o2
				.getOntologyId().contains("BFO"));
		System.out.println("Id o1: " + o1.getOntologyId());
		System.out.println("Id o1: " + o1.getId()); // commit number
		System.out.println("Id o2: " + o2.getOntologyId());
		System.out.println("Homepage o1: " + o1.getHomepage());
		System.out.println("Homepage o2: " + o2.getHomepage());
		System.out.println("o1inBFO= " + o1InBFO);
		System.out.println("o2inBFO= " + o2InBFO);

		// System.out.println("IN BFO Comparator");
		// System.out.println(o1);
		// System.out.println(o2);
		// System.exit(0);

		if (o1InBFO && !o2InBFO) {
			System.out.println(o1 + " in BFO but " + o2 + " not");
			return 1;
		} else if (!o1InBFO && o2InBFO) {
			System.out.println(o2 + " in BFO but " + o1 + " not");
			return -1;
		}
		return 0;
	}

}
