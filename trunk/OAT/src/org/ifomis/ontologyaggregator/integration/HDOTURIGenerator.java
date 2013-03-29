package org.ifomis.ontologyaggregator.integration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.i18n.templates.Template;
import org.apache.commons.io.FileUtils;
import org.ifomis.ontologyaggregator.recommendation.Recommendation;

public class HDOTURIGenerator {

	private boolean includeSubclasses;

	public HDOTURIGenerator(Recommendation acceptedRecommendation, boolean includeSubclasses) {
		this.includeSubclasses = includeSubclasses;
	}
	
	public String generateURI() throws IOException{
//		Template template = new Template("http://hdot.googlecode.com/svn/trunk/{hdotModule}#{prefix}_{counter}");
//
////		String counter  = FileUtils.readFileToString(new File( "data/counterForURIS"));
//				
//		Map map = new HashMap();
//		map.put("hdotModule", "a");
//		map.put("prefix", "b");
//		map.put("counter", counter);

//		System.out.println(template.expand(map));
		//TODO implement
		return "";
		
	}

}
