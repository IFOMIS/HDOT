package org.ifomis.ontologyaggregator.command_line_interface;

import java.util.EventObject;

public class RecommendationAcceptEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private String accept;

	public String getAccept() {
		return accept;
	}

	public RecommendationAcceptEvent(Object source, String accept) {
		super(source);
		this.accept = accept;
	}

}
