package parsing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import uk.ac.ebi.ontocat.bioportal.xmlbeans.MetricsBean;

public class MetricsParser {

	XMLOutputter printer = null;
	Logger log = Logger.getLogger(MetricsParser.class);

	public MetricsParser() {
		printer = new XMLOutputter();
		log.debug("MetricsParser instantiated");
	}

	public MetricsBean parse(String query) {
		log.debug("url for metrics: " + query);

		String xml = "";
		StringBuilder responseBuilder = new StringBuilder();
		try {
			URL url = new URL(query);
			URLConnection conn = url.openConnection();
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					httpConn.getInputStream()));

			String line;
			while ((line = rd.readLine()) != null) {
				responseBuilder.append(line + '\n');

				xml = responseBuilder.toString();
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return parseResponse(xml);
	}

	/**
	 * Parsing an XML file that stores the metrics of an ontology from
	 * BioPortal.
	 * 
	 * @param xml
	 */
	public MetricsBean parseResponse(String xml) {
		// create a parser
		SAXBuilder parser = new SAXBuilder();
		MetricsBean metricsBean = new MetricsBean();
		try {
			// get the dom-document
			Document doc = parser.build(new StringReader(xml));

			// parse the elements of the metrics bean
			Element id = (Element) doc.getRootElement().getChild("data")
					.getChild("ontologyMetricsBean").getChild("id");
			Element numberOfAxioms = (Element) doc.getRootElement()
					.getChild("data").getChild("ontologyMetricsBean")
					.getChild("numberOfAxioms");

			Element numberOfClasses = (Element) doc.getRootElement()
					.getChild("data").getChild("ontologyMetricsBean")
					.getChild("numberOfClasses");

			Element numberOfIndividuals = (Element) doc.getRootElement()
					.getChild("data").getChild("ontologyMetricsBean")
					.getChild("numberOfIndividuals");

			Element numberOfProperties = (Element) doc.getRootElement()
					.getChild("data").getChild("ontologyMetricsBean")
					.getChild("numberOfProperties");

			Element maximumDepth = (Element) doc.getRootElement()
					.getChild("data").getChild("ontologyMetricsBean")
					.getChild("maximumDepth");

			Element maximumNumberOfSiblings = (Element) doc.getRootElement()
					.getChild("data").getChild("ontologyMetricsBean")
					.getChild("maximumNumberOfSiblings");

			Element averageNumberOfSiblings = (Element) doc.getRootElement()
					.getChild("data").getChild("ontologyMetricsBean")
					.getChild("averageNumberOfSiblings");

			Element classesWithOneSubclass = (Element) doc.getRootElement()
					.getChild("data").getChild("ontologyMetricsBean")
					.getChild("classesWithOneSubclass");

			Element classesWithMoreThanXSubclasses = (Element) doc
					.getRootElement().getChild("data")
					.getChild("ontologyMetricsBean")
					.getChild("classesWithMoreThanXSubclasses");

			Element classesWithNoDocumentation = (Element) doc.getRootElement()
					.getChild("data").getChild("ontologyMetricsBean")
					.getChild("classesWithNoDocumentation");

			Element classesWithNoAuthor = (Element) doc.getRootElement()
					.getChild("data").getChild("ontologyMetricsBean")
					.getChild("classesWithNoAuthor");

			// set the fields of the metrics bean
			if (id != null) {
				metricsBean.setId(id.getText());
			}
			if (averageNumberOfSiblings != null) {
				metricsBean.setAverageNumberOfSiblings(Integer
						.parseInt(averageNumberOfSiblings.getText()));
			}
			if (maximumDepth != null) {
				metricsBean.setMaximumDepth(Integer.parseInt(maximumDepth
						.getText()));
			}
			if (maximumNumberOfSiblings != null) {
				metricsBean.setMaximumNumberOfSiblings(Integer
						.parseInt(maximumNumberOfSiblings.getText()));
			}
			if (numberOfAxioms != null) {
				metricsBean.setNumberOfAxioms(Integer.parseInt(numberOfAxioms
						.getText()));
			}
			if (numberOfClasses != null) {
				metricsBean.setNumberOfClasses(Integer.parseInt(numberOfClasses
						.getText()));
			}
			if (numberOfIndividuals != null) {
				metricsBean.setNumberOfIndividuals(Integer
						.parseInt(numberOfIndividuals.getText()));
			}
			if (numberOfProperties != null) {
				metricsBean.setNumberOfProperties(Integer
						.parseInt(numberOfProperties.getText()));
			}
			if (classesWithMoreThanXSubclasses != null) {
				metricsBean
						.setClassesWithMoreThanXSubclasses(classesWithMoreThanXSubclasses
								.getChildren());
			}
			if (classesWithNoAuthor != null) {
				metricsBean.setClassesWithNoAuthor(classesWithNoAuthor
						.getChildren());
			}
			if (classesWithNoDocumentation != null) {
				metricsBean
						.setClassesWithNoDocumentation(classesWithNoDocumentation
								.getChildren());
			}
			if (classesWithOneSubclass != null) {
				metricsBean.setClassesWithOneSubclass(classesWithOneSubclass
						.getChildren());
			}
			// this.printJDom(doc);

		} catch (JDOMException ex) {
			log.error(ex.getMessage());
		} catch (IOException ex) {
			log.error(ex.getMessage());
		}
		log.debug(metricsBean.toString());

		return metricsBean;
	}

	/**
	 * Prints the tree in the console.
	 * 
	 * @param aJDOMdoc
	 *            A JDOM Document
	 * @throws java.io.IOException
	 */
	public void printJDom(Document aJDOMdoc) throws IOException {
		System.out.println("<------------XML DOCUMENT------------>");
		this.printer.output(aJDOMdoc, System.out);
	}
}
