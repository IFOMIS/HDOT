package org.ifomis.ontologyaggregator.recommendation;

import java.io.Console;
import java.util.HashSet;
import java.util.Set;

public class UserInputReader {

	 private Set<RecommendationAcceptListener> listeners;
	 
	    public UserInputReader() {
	        listeners = new HashSet<RecommendationAcceptListener>();
	    }
	 
	    public void addUserInputListener(RecommendationAcceptListener listener) {
	        this.listeners.add(listener);
	    }
	 
	    public void removeUserInputListener(RecommendationAcceptListener listener) {
	        this.listeners.remove(listener);
	    }
	 
	    // TODO change back just for testing
	    
	    public void startListeningAcceptInput() {
//	        Console console = System.console();
//	        System.out.println("console: " + console);
//	        if (console != null) {
//
//	        	String d = null;
//	            do {
//	                String readLine = console.readLine("Enter \"yes\" to accept the recommendation or \"no\" to reject it: ", (Object[])null);
//	                d = readLine;
//	                System.out.println("input " + d);
//	                if (!(d.equalsIgnoreCase("yes") || d.equalsIgnoreCase("no"))){
//	                	continue;
//	                }
//	                if (!d.isEmpty()) {
//	                    notifyListenersAboutAcceptInput(d);
//	                    break;
//	                }
//	            } while (d != null);
//	        }
	    	notifyListenersAboutAcceptInput("no");
	    }

	    public void startListeningIncludeSubclassesInput() {
	        Console console = System.console();
	        if (console != null) {

	        	String d = null;
	            do {
	                String readLine = console.readLine("Enter \"yes\" to include the subclasses with the integration of the term or \"no\" to reject it: ", (Object[])null);
	                d = readLine;
	                System.out.println("input " + d);
	                if (!(d.equalsIgnoreCase("yes") || d.equalsIgnoreCase("no"))){
	                	continue;
	                }
	                if (!d.isEmpty()) {
	                    notifyListenersAboutSubClassesInput(d);
	                    break;
	                }
	            } while (d != null);
	        }
	    }
	    
	    private void notifyListenersAboutSubClassesInput(String d) {
	    	for (RecommendationAcceptListener subClassesListener: listeners) {
	        	subClassesListener.readInputIncludeSubclasses(new IncludeSubClassesEvent(this, d));
	        }			
		}

		private void notifyListenersAboutAcceptInput(String d) {
	        for (RecommendationAcceptListener acceptListener: listeners) {
	        	acceptListener.readInputAccept(new RecommendationAcceptEvent(this, d));
	        }
	    }
}
