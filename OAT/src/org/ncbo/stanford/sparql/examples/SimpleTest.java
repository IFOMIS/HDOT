package org.ncbo.stanford.sparql.examples;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * This is a standalone test that uses BioPortal SPARQL Endpoint without any
 * extra libraries. The result set format is CSV.
 */
public class SimpleTest {

	private String service = null;
	private String apikey = null;

	public SimpleTest(String service, String apikey) {
		this.service = service;
		this.apikey = apikey;
	}

	public String executeQuery(String queryText, String acceptFormat)
			throws Exception {
		String httpQueryString = String.format("query=%s&apikey=%s",
				URLEncoder.encode(queryText, "UTF-8"),
				URLEncoder.encode(this.apikey, "UTF-8"));

		URL url = new URL(this.service + "?" + httpQueryString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", acceptFormat);

		conn.connect();
		InputStream in = conn.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder buff = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			buff.append(line);
			buff.append("\n");
		}
		conn.disconnect();
		return buff.toString();
	}

	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();

		String sparqlService = "http://sparql.bioontology.org/sparql";
		String apikey = "063917f3-04c3-4f7c-8773-7187081c1b11";

		/*
		 * More query examples here: http://sparql.bioontology.org/examples
		 */
		String query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "SELECT * WHERE { "
				+ "GRAPH <http://bioportal.bioontology.org/ontologies/SWO> { "
				+ "<http://www.ifomis.org/bfo/1.1/snap#MaterialEntity> rdfs:subClassOf ?parent." 
				+ "OPTIONAL {?parent rdfs:label ?label}."
				+ "}"
				+ "}";

		SimpleTest test = new SimpleTest(sparqlService, apikey);

		// Accept formats can be: "text/plain", "application/json",
		// "application/rdfxml", "text/csv", text/tab-separated-values
		String response = test.executeQuery(query, "text/tab-separated-values");
		
		System.out.println(response);
		long end = System.currentTimeMillis();

		long milliseconds = (end - start);

		long seconds = (milliseconds / 1000);

		long mins = seconds / 60;
		long restsecs = seconds % 60;

		System.out.println("Execution time was " + (end - start) + " ms.");
		System.out.println("Execution time was " + mins + ":" + restsecs
				+ " sec.");

	}
}
