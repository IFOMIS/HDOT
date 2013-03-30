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
	private Recommendation acceptedRecommendation;

	public HDOTURIGenerator(Recommendation acceptedRecommendation,
			boolean includeSubclasses) {
		this.includeSubclasses = includeSubclasses;
		this.acceptedRecommendation = acceptedRecommendation;
	}

	public String generateURI() throws IOException {
		Template template = new Template(
				"http://hdot.googlecode.com/svn/trunk/{hdotModule}#{prefix}_{counter}");
		String hdotModuleIRI = acceptedRecommendation.getHdotModule()
				.getOntologyID().getOntologyIRI().toString();

		String hdotModule = hdotModuleIRI.split("/")[hdotModuleIRI.length() - 1];

		String prefix = hdotModule.split("\\.")[0].toUpperCase();

		String counter = "";
			counter = FileUtils.readFileToString(new File(
					"data/counterForURIS_" + prefix));

		Map<String, String> map = new HashMap<>();
		map.put("hdotModule", hdotModule);
		map.put("prefix", prefix);
		map.put("counter", counter);

		return template.expand(map);
	}

}
