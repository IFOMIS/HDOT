package recommendation;

import java.util.EventListener;

public interface RecommendationAcceptListener extends EventListener{
	 
	    public void inputRead(RecommendationAcceptEvent recommendationAcceptEvent);
	 }
