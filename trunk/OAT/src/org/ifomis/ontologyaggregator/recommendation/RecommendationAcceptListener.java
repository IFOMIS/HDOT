package org.ifomis.ontologyaggregator.recommendation;

import java.util.EventListener;

public interface RecommendationAcceptListener extends EventListener{
	 
	    public void inputRead(RecommendationAcceptEvent recommendationAcceptEvent);
	 }
