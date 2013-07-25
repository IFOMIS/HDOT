package org.ifomis.ontologyaggregator.command_line_interface;

import java.util.EventObject;

public class IncludeSubClassesEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String includeSubClasses;
	
	public String getIncludeSubClasses() {
		return includeSubClasses;
	}

	public IncludeSubClassesEvent(Object source, String includeSubClasses) {
		super(source);
		this.includeSubClasses = includeSubClasses;
}
}
