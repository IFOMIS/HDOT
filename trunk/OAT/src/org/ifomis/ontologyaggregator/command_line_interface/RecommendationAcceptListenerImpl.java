package org.ifomis.ontologyaggregator.command_line_interface;

import org.apache.log4j.Logger;

public class RecommendationAcceptListenerImpl implements RecommendationAcceptListener {

	private static final Logger log = Logger
			.getLogger(RecommendationAcceptListenerImpl.class);
	private boolean accept;
	private boolean includeSubclasses;
		
	
	@Override
	public void readInputAccept(
			RecommendationAcceptEvent recommendationAcceptEvent) {
		if (recommendationAcceptEvent.getAccept().equalsIgnoreCase("yes")) {
			log.info("the user accepted the recommendation.");
			this.accept = true;

		} else {
			log.info("the user rejected the recommendation.");
			accept = false;
		}
	}

	@Override
	public void readInputIncludeSubclasses(
			IncludeSubClassesEvent includeClassesEvent) {
		if (includeClassesEvent.getIncludeSubClasses().equalsIgnoreCase("yes")) {
			log.info("the user wants to include the subClasses of the term.");
			this.includeSubclasses = true;
		} else {
			log.info("the user does not want to include the subClasses of the term.");
			this.includeSubclasses = false;
		}
	}

	public boolean isIncludeSubclasses() {
		return includeSubclasses;
	}

	public boolean isAccept() {
		return accept;
	}

}
