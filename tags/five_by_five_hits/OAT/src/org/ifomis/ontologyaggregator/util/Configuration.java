package org.ifomis.ontologyaggregator.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

/**
 * Utility that sets up the configuration of the OAT.
 * 
 * @author Nikolina
 * 
 */
public class Configuration {
	private static Configuration instance = null;

	public static IRI BASE_PATH;
	public static IRI CONF_PATH;
	public static IRI LOG_PATH;
	public static IRI DATA_PATH;
	public static IRI SPARQL_OUTPUT_PATH;
	public static IRI HDOT_PATH;
	public static IRI PATH_TO_AUTHORIZED_USER_MODULES;
	public static IRI PATH_TO_NOT_AUTHORIZED_USER_MODULES;

	public static IRI ONTO_IDS_FILE;
	public static IRI MODULES_SORTING_FILE;
	public static IRI PREDEFINED_ONTOLOGIES_FILE;
	public static IRI IMPORTED_ONTOLOGIES_FILE;
	public static IRI CORE_MODULE_FILE;
	public static IRI HDOT_CONTAINER_AUTHORIZED;
	public static IRI HDOT_CONTAINER_NOT_AUTHORIZED;
	public static String[] CURATORS;

	private Properties PROPERTIES;

	public static String SMTP_USERNAME;

	public static String SMTP_PASS;

	public static Integer SMTP_PORT;

	public static String SMTP_HOST;

	private Configuration() throws IOException {
		BASE_PATH = IRI.create(new File(""));
		CONF_PATH = BASE_PATH.resolve("config/");
		LOG_PATH = BASE_PATH.resolve("log/");
		DATA_PATH = BASE_PATH.resolve("data/");
		SPARQL_OUTPUT_PATH = BASE_PATH.resolve("sparql/");
		HDOT_PATH = DATA_PATH.resolve("hdot/");

		PROPERTIES = new Properties();
		File confFile = new File(CONF_PATH.resolve("aggregator.properties")
				.toURI());
		PROPERTIES.load(new FileReader(confFile));

		PATH_TO_AUTHORIZED_USER_MODULES = BASE_PATH.resolve(PROPERTIES
				.getProperty("pathToAuthorizedUserModules"));

		PATH_TO_NOT_AUTHORIZED_USER_MODULES = BASE_PATH.resolve(PROPERTIES
				.getProperty("pathToNotAuthorizedUserModules"));

		ONTO_IDS_FILE = CONF_PATH.resolve(PROPERTIES
				.getProperty("fileOntologiesOrder"));

		MODULES_SORTING_FILE = CONF_PATH.resolve(PROPERTIES
				.getProperty("fileSortingHdotModulesURIs"));

		PREDEFINED_ONTOLOGIES_FILE = CONF_PATH.resolve(PROPERTIES
				.getProperty("filePredefinedOntolodies"));

		IMPORTED_ONTOLOGIES_FILE = CONF_PATH.resolve(PROPERTIES
				.getProperty("fileImportedOntologiesURIs"));

		CORE_MODULE_FILE = CONF_PATH.resolve(PROPERTIES
				.getProperty("fileCoreModule"));

		HDOT_CONTAINER_AUTHORIZED = HDOT_PATH.resolve(PROPERTIES
				.getProperty("fileOntologyContainerAuthorized"));

		HDOT_CONTAINER_NOT_AUTHORIZED = HDOT_PATH
				.resolve(PROPERTIES
						.getProperty("fileOntologyContainerNotAuthorized"));
		
		CURATORS = PROPERTIES.getProperty("curatorsMailAddresses").split(";");
		SMTP_USERNAME = (String) PROPERTIES.get("smtpUsername");
		SMTP_PASS = (String) PROPERTIES.get("smtpPassword");
		SMTP_PORT = Integer.parseInt((String) PROPERTIES.get("smtpPort"));
		SMTP_HOST = (String) PROPERTIES.get("smtpHost");
	}

	public static Configuration getInstance() throws IOException {
		if (instance == null) {
			instance = new Configuration();
		}
		return instance;
	}

	public static OWLOntologyManager mapIrisOfUserModules(OWLOntologyManager ontologyManager) throws IOException {
		List<String> modulesSorted = FileUtils.readLines(new File(
				Configuration.MODULES_SORTING_FILE.toURI()));

		for (String moduleIri : modulesSorted) {
			if (moduleIri.contains("hdot_module_user")) {
				String[] moduleIriParts = moduleIri.split("/");
				IRI ontologyIRI = IRI.create(moduleIri);

				IRI documentIRI = Configuration.PATH_TO_AUTHORIZED_USER_MODULES
						.resolve(moduleIriParts[moduleIriParts.length - 1]);
				OWLOntologyIRIMapper iriMapper = new SimpleIRIMapper(
						ontologyIRI, documentIRI);
				ontologyManager.addIRIMapper(iriMapper);
			}
		}
		return ontologyManager;
	}
	public static void main(String[] args) throws Exception {

		Configuration configuration = new Configuration();

		System.out.println(Configuration.BASE_PATH);
		System.out.println(configuration.CONF_PATH);
		System.out.println(configuration.PROPERTIES);
		System.out.println(configuration.LOG_PATH);
		System.out.println(configuration.HDOT_CONTAINER_AUTHORIZED);
		System.out.println(configuration.SPARQL_OUTPUT_PATH);
		System.out.println(configuration.ONTO_IDS_FILE);
		System.out.println(configuration.DATA_PATH);

	}
}