package recommendation;

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
	 
	    public void start() {
	        Console console = System.console();
	        System.out.println("console: " + console);
	        if (console != null) {

	        	String d = null;
	            do {
	                String readLine = console.readLine("Enter \"yes\" to accept the recommendation or \"no\" to reject it: ", (Object[])null);
	                d = readLine;
	                System.out.println("input " + d);
	                if (!(d.equalsIgnoreCase("yes") || d.equalsIgnoreCase("no"))){
	                	continue;
	                }
	                if (!d.isEmpty()) {
	                    notifyListeners(d);
	                    break;
	                }
	            } while (d != null);
	        }
	    }
	 

	    private void notifyListeners(String d) {
	        for (RecommendationAcceptListener acceptListener: listeners) {
	        	acceptListener.inputRead(new RecommendationAcceptEvent(this, d));
	        }
	    }
}
