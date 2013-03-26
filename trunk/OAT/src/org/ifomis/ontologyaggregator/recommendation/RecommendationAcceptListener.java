package org.ifomis.ontologyaggregator.recommendation;

import java.util.EventListener;

public interface RecommendationAcceptListener extends EventListener{
	 
	    public void readInputAccept(RecommendationAcceptEvent recommendationAcceptEvent);
	    public void readInputIncludeSubclasses(IncludeSubClassesEvent includeClassesEvent);
	    
	 }
