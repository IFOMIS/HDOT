/**
 * Copyright (c) 2010 - 2011 European Molecular Biology Laboratory and University of Groningen
 *
 * Contact: ontocat-users@lists.sourceforge.net
 * 
 * This file is part of OntoCAT
 * 
 * OntoCAT is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * OntoCAT is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along
 * with OntoCAT. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.ebi.ontocat.bioportal.xmlbeans;

import java.util.ArrayList;
import uk.ac.ebi.ontocat.bioportal.xmlbeans.MetricsBean;
import uk.ac.ebi.ontocat.Ontology;

/**
 * Wraps for the Ontology representation of BioPortal and maps it to the
 * OntologyEntity interface.
 * 
 * @author Tomasz Adamusiak
 * 
 */
@SuppressWarnings("unused")
public class OntologyBean extends Ontology{
	private static final long serialVersionUID = 1L;
	private String id;
	private String ontologyId;
	private String virtualViewIds;
	private String isView;
	private String hasViews;
	private String viewOnOntologyVersionId;
	private String internalVersionNumber;
	private String userId;
	private String versionNumber;
	private String versionStatus;
	private String isRemote;
	private String isReviewed;
	private String statusId;
	private String dateCreated;
	private String dateReleased;
	private String isManual;
	private String displayLabel;
	private String description;
	private String abbreviation;
	private String format;
	private String contactName;
	private String contactEmail;
	private String homepage;
	private String documentation;
	private String urn;
	private String isFoundry;
	private String oboFoundryId;
	private String codingScheme;
	private String publication;
	private String documentationSlot;
	private String synonymSlot;
	private String targetTerminologies;
	private String preferredNameSlot;
	private ArrayList<String> categoryIds;
	private ArrayList<String> filenames;
	private ArrayList<Integer> groupIds;
	private String filePath;
	private String viewDefinition;
	private String viewDefinitionLanguage;
	private String downloadLocation;
	private String authorSlot;
	private String isMetadataOnly;
	private String userAcl;
	private String isFlat;
	private String viewGenerationEngine;
	private MetricsBean metricsBean;
	
	public MetricsBean getMetricsBean() {
		return metricsBean;
	}

	public String getIsView() {
		return isView;
	}

	public void setIsView(String isView) {
		this.isView = isView;
	}

	public String getHasViews() {
		return hasViews;
	}

	public void setHasViews(String hasViews) {
		this.hasViews = hasViews;
	}

	public String getIsRemote() {
		return isRemote;
	}

	public void setIsRemote(String isRemote) {
		this.isRemote = isRemote;
	}

	public String getIsReviewed() {
		return isReviewed;
	}

	public void setIsReviewed(String isReviewed) {
		this.isReviewed = isReviewed;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getIsManual() {
		return isManual;
	}

	public void setIsManual(String isManual) {
		this.isManual = isManual;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	public String getIsFoundry() {
		return isFoundry;
	}

	public void setIsFoundry(String isFoundry) {
		this.isFoundry = isFoundry;
	}

	public String getOboFoundryId() {
		return oboFoundryId;
	}

	public void setOboFoundryId(String oboFoundryId) {
		this.oboFoundryId = oboFoundryId;
	}

	public String getCodingScheme() {
		return codingScheme;
	}

	public void setCodingScheme(String codingScheme) {
		this.codingScheme = codingScheme;
	}

	public String getDocumentationSlot() {
		return documentationSlot;
	}

	public void setDocumentationSlot(String documentationSlot) {
		this.documentationSlot = documentationSlot;
	}

	public String getAuthorSlot() {
		return authorSlot;
	}

	public void setAuthorSlot(String authorSlot) {
		this.authorSlot = authorSlot;
	}

	public String getIsMetadataOnly() {
		return isMetadataOnly;
	}

	public void setIsMetadataOnly(String isMetadataOnly) {
		this.isMetadataOnly = isMetadataOnly;
	}

	public String getIsFlat() {
		return isFlat;
	}

	public void setIsFlat(String isFlat) {
		this.isFlat = isFlat;
	}



	public OntologyBean(String ontologyAccession) {
		super(ontologyAccession);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.efo.bioportal.xmlbeans.OntologyEntity#getSynonymSlot()
	 */
	@Override
	public String getSynonymSlot() {
		return synonymSlot;
	}

	public String getTargetTerminologies() {
		return targetTerminologies;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.ebi.efo.bioportal.xmlbeans.OntologyEntity#getPreferredNameSlot()
	 */
	public String getPreferredNameSlot() {
		return preferredNameSlot;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.efo.bioportal.xmlbeans.OntologyEntity#getVersionNumber()
	 */
	@Override
	public String getVersionNumber() {
		return versionNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.efo.bioportal.xmlbeans.OntologyEntity#getDateReleased()
	 */
	@Override
	public String getDateReleased() {
		return dateReleased;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.efo.bioportal.xmlbeans.OntologyEntity#getDisplayLabel()
	 */
	@Override
	public String getLabel() {
		return displayLabel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.efo.bioportal.xmlbeans.OntologyEntity#getAbbreviation()
	 */
	@Override
	public String getAbbreviation() {
		return abbreviation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.efo.bioportal.xmlbeans.OntologyEntity#getMetaAnnotation()
	 */
	public String getMetaAnnotation() {
		return "Bioportal mappings to " + displayLabel + " (" + abbreviation + ") ver" + versionNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see plugin.OntologyBrowser.OntologyEntity#getOntologyID()
	 */
	@Override
	public String getOntologyAccession() {
		return ontologyId;
	}

	@Override
	public String getDescription()
	{
		return description;
	}
	
	public ArrayList<Integer> getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(ArrayList<Integer> groupIds) {
		this.groupIds = groupIds;
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
	public String getOntologyId() {
		return ontologyId;
	}

	public void setOntologyId(String ontologyId) {
		this.ontologyId = ontologyId;
	}
	public String getHomepage() {
		return homepage;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public void setMetricsBean(MetricsBean metricsBean) {

		this.metricsBean = metricsBean;
	}
}
