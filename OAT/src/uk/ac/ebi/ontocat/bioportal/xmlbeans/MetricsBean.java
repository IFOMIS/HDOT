package uk.ac.ebi.ontocat.bioportal.xmlbeans;

import java.util.List;

import org.jdom2.Element;


/**
 * Encapsulates the metrics bean of an ontology from BioPortal.
 * 
 * @author Nikolina
 *
 */
public class MetricsBean {	
	private String id;
	private int numberOfAxioms;
	private int numberOfClasses;
	private int numberOfIndividuals;
	private int numberOfProperties;
	private int maximumDepth;
	private int maximumNumberOfSiblings;
	private int averageNumberOfSiblings;
	private List<Element> classesWithOneSubclass;
	private List<Element> classesWithMoreThanXSubclasses;
	private List<Element> classesWithNoAuthor;
	private List<Element> classesWithNoDocumentation;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getNumberOfAxioms() {
		return numberOfAxioms;
	}
	public void setNumberOfAxioms(int numberOfAxioms) {
		this.numberOfAxioms = numberOfAxioms;
	}
	public int getNumberOfClasses() {
		return numberOfClasses;
	}
	public void setNumberOfClasses(int numberOfClasses) {
		this.numberOfClasses = numberOfClasses;
	}
	public int getNumberOfIndividuals() {
		return numberOfIndividuals;
	}
	public void setNumberOfIndividuals(int numberOfIndividuals) {
		this.numberOfIndividuals = numberOfIndividuals;
	}
	public int getNumberOfProperties() {
		return numberOfProperties;
	}
	public void setNumberOfProperties(int numberOfProperties) {
		this.numberOfProperties = numberOfProperties;
	}
	public int getMaximumDepth() {
		return maximumDepth;
	}
	public void setMaximumDepth(int maximumDepth) {
		this.maximumDepth = maximumDepth;
	}
	public int getMaximumNumberOfSiblings() {
		return maximumNumberOfSiblings;
	}
	public void setMaximumNumberOfSiblings(int maximumNumberOfSiblings) {
		this.maximumNumberOfSiblings = maximumNumberOfSiblings;
	}
	public int getAverageNumberOfSiblings() {
		return averageNumberOfSiblings;
	}
	public void setAverageNumberOfSiblings(int averageNumberOfSiblings) {
		this.averageNumberOfSiblings = averageNumberOfSiblings;
	}
	public List<Element> getClassesWithOneSubclass() {
		return classesWithOneSubclass;
	}
	public void setClassesWithOneSubclass(List<Element> classesWithOneSubclass) {
		this.classesWithOneSubclass = classesWithOneSubclass;
	}
	public List<Element> getClassesWithMoreThanXSubclasses() {
		return classesWithMoreThanXSubclasses;
	}
	public void setClassesWithMoreThanXSubclasses(
			List<Element> classesWithMoreThanXSubclasses) {
		this.classesWithMoreThanXSubclasses = classesWithMoreThanXSubclasses;
	}
	public List<Element> getClassesWithNoAuthor() {
		return classesWithNoAuthor;
	}
	public void setClassesWithNoAuthor(List<Element> classesWithNoAuthor) {
		this.classesWithNoAuthor = classesWithNoAuthor;
	}
	public List<Element> getClassesWithNoDocumentation() {
		return classesWithNoDocumentation;
	}
	public void setClassesWithNoDocumentation(
			List<Element> classesWithNoDocumentation) {
		this.classesWithNoDocumentation = classesWithNoDocumentation;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("version id: ");
		sb.append(this.id);
		sb.append("; numberOfAxioms: ");
		sb.append(this.numberOfAxioms);
		sb.append("; numberOfClasses: ");
		sb.append("; numberOfIndividuals: ");
		sb.append(this.numberOfIndividuals);
		sb.append("numberOfProperties: ");
		sb.append(this.numberOfProperties);
		sb.append("; maximumDepth: ");
		sb.append(this.maximumDepth);
//		sb.append("; classesWithOneSubclass: ");
//		sb.append(this.classesWithOneSubclass != null ? this.classesWithOneSubclass.size(): null);
//		sb.append(";\n classesWithNoAuthor: ");
//		sb.append(this.classesWithNoAuthor!= null ? this.classesWithNoAuthor.size(): null);
//		sb.append("; classesWithNoDocumentation: ");
//		sb.append(this.classesWithNoDocumentation != null ? this.classesWithNoDocumentation.size(): null);
		sb.append("; ");
		return sb.toString();
	}
}
