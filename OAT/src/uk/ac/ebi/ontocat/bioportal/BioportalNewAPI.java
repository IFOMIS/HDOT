package uk.ac.ebi.ontocat.bioportal;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.xml.sax.InputSource;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 17/09/14
 * Time: 9:14
 * To change this template use File | Settings | File Templates.
 */
public class BioportalNewAPI {

    private static final String urlBASE = "http://data.bioontology.org/";
    private final String apikey;


    public BioportalNewAPI(String auth) {
        apikey = "apikey=" + auth;
    }

    public BioportalNewAPI() {
        this("c6ae1b27-9f86-4e3c-9dcf-087e1156eabe");
    }

    private JSONObject getSimpleJSONObject(String self) {
        JSONObject jsonObject = null;
        try {
            String line;
            URL url;
            if(!self.contains(apikey)) {
                if(!self.contains("?"))
                    url = new URL(self + "?" + apikey);
                else
                    url = new URL(self + "&" + apikey);
            } else {
                url = new URL(self);
            }
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            int resp = conn.getResponseCode();
            if(resp == 200) {
                String result = "";
                JSONParser jpar = new JSONParser();
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while((line = rd.readLine()) != null) {
                    result += line;
                }
                rd.close();
                jsonObject = (JSONObject) jpar.parse(result);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private JSONArray getSimpleJSONArray(String self) {
        JSONArray jsonObject = null;
        try {
            String line;
            URL url = new URL(self + "?" + apikey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            int resp = conn.getResponseCode();
            if(resp == 200) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String result = "";
                JSONParser jpar = new JSONParser();
                while((line = rd.readLine()) != null) {
                    result += line;
                }
                rd.close();
                jsonObject = (JSONArray) jpar.parse(result);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private JSONObject getClassComplete(String self) {
        JSONObject jsonObject = null;
        try {
            String line;
            URL url = new URL(self + "?" + apikey + "&include=prefLabel,synonym,definition,notation,cui,semanticType,properties");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            int resp = conn.getResponseCode();
            if(resp == 200) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String result = "";
                JSONParser jpar = new JSONParser();
                while((line = rd.readLine()) != null) {
                    result += line;
                }
                rd.close();
                jsonObject = (JSONObject) jpar.parse(result);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private JSONArray getJSONArrayComplete(String self) {
        JSONArray jsonObject = null;
        try {
            String line;
            URL url = new URL(self + "?" + apikey + "&include=prefLabel,synonym,definition,notation,cui,semanticType,properties");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            int resp = conn.getResponseCode();
            if(resp == 200) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String result = "";
                JSONParser jpar = new JSONParser();
                while((line = rd.readLine()) != null) {
                    result += line;
                }
                rd.close();
                jsonObject = (JSONArray) jpar.parse(result);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * @param jsonArray
     * @return returns the viwes of a ontology
     */
    private String getviews(JSONArray jsonArray) {
        String viewxml = "";
        for(Object obj : jsonArray) {
            String iD;
            JSONObject jobj = (JSONObject) obj;
            //retrieve the info from the ontologies
            jobj = getSimpleJSONObject((String) ((JSONObject) jobj.get("links")).get("latest_submission"));
            if(jobj != null && jobj.size() > 0) {
                JSONObject ont = (JSONObject) jobj.get("ontology");
                iD = StringEscapeUtils.escapeXml(String.valueOf(ont.get("acronym")));
                if(!viewxml.contains("<ontologyId>" + iD + "</ontologyId>")) {

                    String contactname = "";
                    String contactemail = "";
                    String categor = "";
                    String groups = "";
                    String viewsID = "";
                    String versionID = StringEscapeUtils.escapeXml(String.valueOf(jobj.get("submissionId")));
                    String displayLabel = StringEscapeUtils.escapeXml(String.valueOf(ont.get("name")));
                    String creationdate = StringEscapeUtils.escapeXml(String.valueOf(jobj.get("creationDate")));
                    String releaseddate = StringEscapeUtils.escapeXml(String.valueOf(jobj.get("released")));
                    String versionnumber = StringEscapeUtils.escapeXml(String.valueOf(jobj.get("version")));
                    String description = StringEscapeUtils.escapeXml(String.valueOf(jobj.get("description")));
                    String format = StringEscapeUtils.escapeXml(String.valueOf(jobj.get("hasOntologyLanguage")));

                    if(((JSONArray) jobj.get("contact")).size() > 0) {
                        contactname = StringEscapeUtils
                                .escapeXml(String.valueOf(((JSONObject) ((JSONArray) jobj.get("contact")).get(0)).get("name")));
                        contactemail = StringEscapeUtils
                                .escapeXml(String.valueOf(((JSONObject) ((JSONArray) jobj.get("contact")).get(0)).get("email")));
                    }

                    String homepage = StringEscapeUtils.escapeXml(String.valueOf(jobj.get("homepage")));
                    String publication = StringEscapeUtils.escapeXml(String.valueOf(jobj.get("publication")));
                    String documentation = StringEscapeUtils.escapeXml(String.valueOf(jobj.get("documentation")));
                    String status = StringEscapeUtils.escapeXml(String.valueOf(jobj.get("status")));
                    boolean summaryOnly = ont.get("summaryOnly") != null && (Boolean) ont.get("summaryOnly");
                    jobj = (JSONObject) ont.get("links");
                    String download = StringEscapeUtils.escapeXml(String.valueOf(jobj.get("download")));

                    JSONArray JArr = getSimpleJSONArray((String) jobj.get("categories"));
                    for(Object cat : JArr) {
                        categor +=
                                "<string>" + StringEscapeUtils.escapeXml(String.valueOf(((JSONObject) cat).get("acronym"))) + "</string>";
                    }

                    JArr = getSimpleJSONArray((String) jobj.get("groups"));
                    for(Object gr : JArr) {
                        groups += "<string>" + StringEscapeUtils.escapeXml(String.valueOf(((JSONObject) gr).get("acronym"))) + "</string>";
                    }

                    JArr = getSimpleJSONArray((String) jobj.get("views"));
                    for(Object vie : JArr) {
                        viewsID +=
                                "<string>" + StringEscapeUtils.escapeXml(String.valueOf(((JSONObject) vie).get("acronym"))) + "</string>";
                    }

                    viewxml += getviews(JArr);
                    viewxml += "<ontologyBean><id>" + iD + "</id><ontologyId>" + iD + "</ontologyId><virtualViewIds>" + viewsID +
                            "</virtualViewIds><internalVersionNumber>" + versionID + "</internalVersionNumber><userIds/><versionNumber>" +
                            versionnumber + "</versionNumber><versionStatus>" + status +
                            "</versionStatus><isRemote/><statusId/><dateCreated>" + creationdate + "</dateCreated><dateReleased>" +
                            releaseddate + "</dateReleased><isManual/><displayLabel>" + displayLabel + "</displayLabel><description>" +
                            description + "</description><abbreviation>" + iD + "</abbreviation><format>" + format +
                            "</format><contactName>" + contactname + "</contactName><contactEmail>" + contactemail +
                            "</contactEmail><homepage>" + homepage + "</homepage><documentation>" + documentation +
                            "</documentation><publication>" + publication + "</publication><isFlat/><isFoundry/><isMetadataOnly>" +
                            summaryOnly + "</isMetadataOnly><synonymSlot/><preferredNameSlot/><documentationSlot/><authorSlot/>" +
                            "<userAcl/><isView>true</isView><categoryIds>" + categor + "</categoryIds><groupIds>" + groups +
                            "</groupIds><filenames/><filePath/><downloadLocation>" + download + "</downloadLocation><hasViews>" + viewsID +
                            "</hasViews>" + "<viewOnOntologyVersionId/></ontologyBean>";
                }
            }
        }
        return viewxml;
    }

    /**
     * @param jsonArrayrr
     * @return xml of getRecomendation's result
     */
    public String recommendationXML(JSONArray jsonArrayrr) {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String ret = "";
        String versionID;
        String ontoID;
        String displayLabel;
        int tamtot = 0;
        int hittotal = 0;
        String classxml = "<searchResultList>";
        String ontoxml = "<ontologyHitList>";

        for(Object obj : jsonArrayrr) {
            JSONObject jobj = (JSONObject) obj;
            int hits = Integer.valueOf(String.valueOf(jobj.get("numTermsMatched")));

            //retrieve the info from the ontologies

            JSONObject onto = getSimpleJSONObject((String) ((JSONObject) jobj.get("ontology")).get("@id"));
            if(onto != null) {
                ontoID = (String) onto.get("acronym");
                displayLabel = (String) onto.get("name");
                onto = getSimpleJSONObject((String) ((JSONObject) onto.get("links")).get("latest_submission"));
                if(onto != null) {
                    hittotal++;
                    versionID = String.valueOf(onto.get("submissionId"));

                    for(Object clase : (JSONArray) jobj.get("annotatedClasses")) {
                        //retrieve the info from the clases
                        onto = getClassComplete((String) ((JSONObject) ((JSONObject) clase).get("links")).get("self"));
                        if(onto != null && !classxml.contains(onto.get("@id").toString())) {
                            tamtot++;
                            hittotal++;
                            String tipo = onto.get("@type").toString();
                            tipo = tipo.substring(tipo.lastIndexOf("#") + 1).toLowerCase();
                            String concID = onto.get("@id").toString();
                            String prefName = onto.get("prefLabel").toString();
                            String obsolete =
                                    ((JSONObject) onto.get("properties")).get("http://www.w3.org/2002/07/owl#deprecated") != null ? "1" :
                                            "0";
                            String concIDShort =
                                    ((JSONObject) onto.get("properties")).get("http://data.bioontology.org/metadata/prefixIRI") != null ?
                                            (String) ((JSONArray) ((JSONObject) onto.get("properties"))
                                                    .get("http://data.bioontology.org/metadata/prefixIRI")).get(0) :
                                            concID.substring(concID.lastIndexOf("#") + 1);

                            classxml += "<searchBean><ontologyVersionId>" + versionID + "</ontologyVersionId><ontologyId>" + ontoID +
                                    "</ontologyId><ontologyDisplayLabel>" + displayLabel + "</ontologyDisplayLabel><objectType>" + tipo +
                                    "</objectType><conceptId>" + concID + "</conceptId><conceptIdShort>" + concIDShort +
                                    "</conceptIdShort><preferredName>" + prefName + "</preferredName><isObsolete>" + obsolete +
                                    "</isObsolete></searchBean>";
                        } else {
                            hits--;
                        }
                    }
                    ontoxml += "<ontologyHitBean><ontologyVersionId>" + versionID + "</ontologyVersionId><ontologyId>" + ontoID +
                            "</ontologyId><ontologyDisplayLabel>" + displayLabel + "</ontologyDisplayLabel><numHits>" + hits +
                            "</numHits></ontologyHitBean>";
                }
            }
        }


        classxml += "</searchResultList>";
        ontoxml += "</ontologyHitList>";

        //put all the info together to parse to xml later
        ret += "<?xml version=\"1.0\" encoding=\"UTF-8\"?><success><accessedResource>/bioportal/search/</accessedResource>" +
                "<accessDate>" + format.format(now) + "</accessDate><data><page><pageNum>1</pageNum><numPages>1</numPages>" +
                "<pageSize>" + tamtot + "</pageSize><numResultsPage>" + tamtot + "</numResultsPage><numResultsTotal>" + tamtot +
                "</numResultsTotal><contents class=\"org.ncbo.stanford.bean.search.SearchResultListBean\">" + classxml + ontoxml +
                "<numHitsTotal>" + hittotal + "</numHitsTotal></contents></page></data></success>";
        try {
            //Format the string result to xml
            Transformer serializer = SAXTransformerFactory.newInstance().newTransformer();
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            Source xmlSource = new SAXSource(new InputSource(new ByteArrayInputStream(ret.getBytes())));
            StreamResult res = new StreamResult(new ByteArrayOutputStream());
            serializer.transform(xmlSource, res);
            ret = new String(((ByteArrayOutputStream) res.getOutputStream()).toByteArray());
        } catch(TransformerException e) {
            e.printStackTrace();
        }

        return ret.replace("><", ">\n<");
    }

    public String searchTermXML(JSONObject jsonObject, String term) {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String ret = "";
        int hits = 0;
        String page;
        String pagecount;
        int hittotal = 0;
        String classxml = "<searchResultList>";
        String ontoxml = "<ontologyHitList>";
        HashMap<String, Integer> ontohits = new HashMap<>();
        HashMap<String, String> ontos = new HashMap<>();


        JSONArray jsonArrayrr = (JSONArray) jsonObject.get("collection");
        page = String.valueOf((Long) jsonObject.get("page"));
        pagecount = String.valueOf((Long) jsonObject.get("pageCount"));
        int hitpage = jsonArrayrr.size();
        if(Integer.valueOf(String.valueOf((Long) jsonObject.get("pageCount"))) == 1) {
            hits += jsonArrayrr.size();
        } else {
            hits += (Integer.valueOf(String.valueOf((Long) jsonObject.get("pageCount"))) - 1) * 50;
            if(!page.equals(pagecount)) {
                JSONObject jobj = getSimpleJSONObject(((JSONObject) jsonObject.get("links")).get("nextPage").toString()
                        .replace("&page=" + String.valueOf((Long) jsonObject.get("nextPage")),
                                "&page=" + String.valueOf((Long) jsonObject.get("pageCount"))));
                hits += ((JSONArray) jobj.get("collection")).size();
            } else {
                hits += hitpage;
            }
        }
        //retrieve the info from the ontologies
        for(Object clase : jsonArrayrr) {
            jsonObject = (JSONObject) clase;
            if(!classxml.contains(jsonObject.get("@id").toString())) {
                JSONObject onto = getSimpleJSONObject((String) ((JSONObject) jsonObject.get("links")).get("ontology"));
                String ontoID = (String) onto.get("acronym");
                String displayLabel = (String) onto.get("name");
                onto = getSimpleJSONObject((String) ((JSONObject) onto.get("links")).get("latest_submission"));
                String versionID = String.valueOf(onto.get("submissionId"));
                if(!ontos.containsKey(ontoID)) {
                    ontos.put(ontoID, "<ontologyVersionId>" + versionID + "</ontologyVersionId><ontologyId>" + ontoID +
                            "</ontologyId><ontologyDisplayLabel>" + displayLabel + "</ontologyDisplayLabel>");
                    ontohits.put(ontoID, 1);
                } else {
                    ontohits.put(ontoID, ontohits.get(ontoID) + 1);
                }
                //retrieve the info from the clases

                String tipo = jsonObject.get("@type").toString();
                tipo = StringEscapeUtils.escapeXml(tipo.substring(tipo.lastIndexOf("#") + 1).toLowerCase());
                String concID = StringEscapeUtils.escapeXml(jsonObject.get("@id").toString());
                String prefName = StringEscapeUtils.escapeXml(jsonObject.get("prefLabel").toString());
                String obsolete = ((Boolean) jsonObject.get("obsolete")) ? "1" : "0";
                String concIDShort = StringEscapeUtils.escapeXml(concID.substring(concID.lastIndexOf("#") + 1));
                String matchtype = StringEscapeUtils.escapeXml(jsonObject.get("matchType").toString());
                String contents = "";

                term = term.replace(" ", "&&");
                term = term.replace("+", "&&");
                String terms[] = term.split("&&");

                for(int i = 0; i < terms.length; i++) {
                    if(jsonObject.get(matchtype).toString().toLowerCase().contains(terms[i].toLowerCase())) {
                        if(contents.equals(""))
                            contents = terms[i];
                        else
                            contents += "+" + terms[i];
                    }
                }

                classxml += "<searchBean><ontologyVersionId>" + versionID + "</ontologyVersionId><ontologyId>" + ontoID +
                        "</ontologyId><ontologyDisplayLabel>" + displayLabel + "</ontologyDisplayLabel><objectType>" + tipo +
                        "</objectType><conceptId>" + concID + "</conceptId><conceptIdShort>" + concIDShort +
                        "</conceptIdShort><preferredName>" + prefName + "</preferredName><contents>" + contents +
                        "</contents><isObsolete>" + obsolete + "</isObsolete></searchBean>";
            }
        }

        for(String key : ontos.keySet()) {
            ontoxml += "<ontologyHitBean>" + ontos.get(key) + "<numHits>" + ontohits.get(key) +
                    "</numHits></ontologyHitBean>";
            hittotal += ontohits.get(key) + 1;
        }


        classxml += "</searchResultList>";
        ontoxml += "</ontologyHitList>";

        //put all the info together to parse to xml later
        ret += "<?xml version=\"1.0\" encoding=\"UTF-8\"?><success><accessedResource>/bioportal/search/</accessedResource>" +
                "<accessDate>" + format.format(now) + "</accessDate><data><page><pageNum>" + page + "</pageNum><numPages>" + pagecount +
                "</numPages>" + "<pageSize>" + hitpage + "</pageSize><numResultsPage>" + hitpage + "</numResultsPage><numResultsTotal>" +
                hits + "</numResultsTotal><contents class=\"org.ncbo.stanford.bean.search.SearchResultListBean\">" + classxml + ontoxml +
                "<numHitsTotal>" + hittotal + "</numHitsTotal></contents></page></data></success>";
        try {
            //Format the string result to xml
            Transformer serializer = SAXTransformerFactory.newInstance().newTransformer();
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            Source xmlSource = new SAXSource(new InputSource(new ByteArrayInputStream(ret.getBytes())));
            StreamResult res = new StreamResult(new ByteArrayOutputStream());
            serializer.transform(xmlSource, res);
            ret = new String(((ByteArrayOutputStream) res.getOutputStream()).toByteArray());
        } catch(TransformerException e) {
            e.printStackTrace();
        }

        return ret.replace("><", ">\n<");
    }

    /**
     * @param jsonArrayrr
     * @return xml of getOntologies's result
     */
    public HashMap<String, String> allOntologiesXML(JSONArray jsonArrayrr) {
        Date now = new Date();
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String viewxml = "";
        String ontoxml = "";

        for(Object obj : jsonArrayrr) {

            JSONObject jobj = (JSONObject) obj;
            //retrieve the info from the ontologies
            jobj = getSimpleJSONObject((String) ((JSONObject) jobj.get("links")).get("latest_submission"));
            if(jobj != null && jobj.size() > 0) {
                JSONObject ont = (JSONObject) jobj.get("ontology");
                String iD = StringEscapeUtils.escapeXml(String.valueOf(ont.get("acronym")));
                System.out.println("Trata el objeto con id: " + iD);
                if(!ontoxml.contains("<id>" + iD + "</id>")) {
                    //                    String ontoID = StringEscapeUtils.escapeXml(String.valueOf(ont.get("@id")));

                    String contactname = "";
                    String contactemail = "";
                    String categor = "";
                    String groups = "";
                    String viewsID = "";
                    String versionID = StringEscapeUtils.escapeXml(String.valueOf(jobj.get("submissionId")));
                    String displayLabel = StringEscapeUtils.escapeXml(String.valueOf(ont.get("name")));
                    String creationdate = StringEscapeUtils.escapeXml(String.valueOf(jobj.get("creationDate")));
                    String releaseddate = StringEscapeUtils.escapeXml(String.valueOf(jobj.get("released")));
                    String versionnumber = StringEscapeUtils.escapeXml(String.valueOf(jobj.get("version")));
                    String description = StringEscapeUtils.escapeXml(String.valueOf(jobj.get("description")));
                    String format = StringEscapeUtils.escapeXml(String.valueOf(jobj.get("hasOntologyLanguage")));

                    if(((JSONArray) jobj.get("contact")).size() > 0) {
                        contactname = StringEscapeUtils
                                .escapeXml(String.valueOf(((JSONObject) ((JSONArray) jobj.get("contact")).get(0)).get("name")));
                        contactemail = StringEscapeUtils
                                .escapeXml(String.valueOf(((JSONObject) ((JSONArray) jobj.get("contact")).get(0)).get("email")));
                    }

                    String homepage = StringEscapeUtils.escapeXml(String.valueOf(jobj.get("homepage")));
                    String publication = StringEscapeUtils.escapeXml(String.valueOf(jobj.get("publication")));
                    String documentation = StringEscapeUtils.escapeXml(String.valueOf(jobj.get("documentation")));
                    String status = StringEscapeUtils.escapeXml(String.valueOf(jobj.get("status")));
                    boolean summaryOnly = ont.get("summaryOnly") != null && (Boolean) ont.get("summaryOnly");

                    jobj = (JSONObject) ont.get("links");
                    String download = StringEscapeUtils.escapeXml(String.valueOf(jobj.get("download")));
                    JSONArray JArr = getSimpleJSONArray((String) jobj.get("categories"));
                    if(JArr != null) {
                        for(Object cat : JArr) {
                            categor += "<string>" + StringEscapeUtils.escapeXml(String.valueOf(((JSONObject) cat).get("acronym"))) +
                                    "</string>";
                        }
                    }

                    JArr = getSimpleJSONArray((String) jobj.get("groups"));
                    if(JArr != null) {
                        for(Object gr : JArr) {
                            groups += "<string>" + StringEscapeUtils.escapeXml(String.valueOf(((JSONObject) gr).get("acronym"))) +
                                    "</string>";
                        }
                    }

                    JArr = getSimpleJSONArray((String) jobj.get("views"));
                    if(JArr != null) {
                        for(Object vie : JArr) {
                            viewsID += "<string>" + StringEscapeUtils.escapeXml(String.valueOf(((JSONObject) vie).get("acronym"))) +
                                    "</string>";
                        }
                    }
                    viewxml += getviews(JArr);
                    ontoxml += "<ontologyBean><id>" + iD + "</id><ontologyId>" + iD + "</ontologyId><virtualViewIds>" + viewsID +
                            "</virtualViewIds><internalVersionNumber>" + versionID +
                            "</internalVersionNumber><userIds/><versionNumber>" +
                            versionnumber + "</versionNumber><versionStatus>" + status +
                            "</versionStatus><isRemote/><statusId/><dateCreated>" + creationdate + "</dateCreated><dateReleased>" +
                            releaseddate + "</dateReleased><isManual/><displayLabel>" + displayLabel + "</displayLabel><description>" +
                            description + "</description><abbreviation>" + iD + "</abbreviation><format>" + format +
                            "</format><contactName>" + contactname + "</contactName><contactEmail>" + contactemail +
                            "</contactEmail><homepage>" + homepage + "</homepage><documentation>" + documentation +
                            "</documentation><publication>" + publication + "</publication><isFlat/><isFoundry/><isMetadataOnly>" +
                            summaryOnly + "</isMetadataOnly><synonymSlot/><preferredNameSlot/><documentationSlot/><authorSlot/>" +
                            "<userAcl/><isView>false</isView><categoryIds>" + categor + "</categoryIds><groupIds>" + groups +
                            "</groupIds><filenames/><filePath/><downloadLocation>" + download + "</downloadLocation><hasViews>" +
                            viewsID +
                            "</hasViews>" + "<viewOnOntologyVersionId/></ontologyBean>";
                }
            }
        }

        //put all the info together to parse to xml later

        String ret = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><success><accessedResource>/bioportal/ontologies</accessedResource>" +
                "<accessDate>" + formater.format(now) + "</accessDate><data><list>" + ontoxml.replace(">null<", "><") +
                "</list></data></success>";

        HashMap<String, String> result = new HashMap<>();
        Transformer serializer;
        Source xmlSource;
        StreamResult res;
        try {
            //Format the string result to xml
            serializer = SAXTransformerFactory.newInstance().newTransformer();
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            xmlSource = new SAXSource(new InputSource(new ByteArrayInputStream(ret.getBytes())));
            res = new StreamResult(new ByteArrayOutputStream());
            serializer.transform(xmlSource, res);
            ret = new String(((ByteArrayOutputStream) res.getOutputStream()).toByteArray());
        } catch(TransformerException e) {
            e.printStackTrace();
        }

        result.put("ontologies", ret.replace("><", ">\n<"));

        ret = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><success><accessedResource>/bioportal/view</accessedResource><accessDate>" +
                formater.format(now) + "</accessDate><data><list>" + viewxml.replace(">null<", "><") + "</list></data></success>";
        try {
            //Format the string result to xml
            serializer = SAXTransformerFactory.newInstance().newTransformer();
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            xmlSource = new SAXSource(new InputSource(new ByteArrayInputStream(ret.getBytes())));
            res = new StreamResult(new ByteArrayOutputStream());
            serializer.transform(xmlSource, res);
            ret = new String(((ByteArrayOutputStream) res.getOutputStream()).toByteArray());
        } catch(TransformerException e) {
            e.printStackTrace();
        }
        result.put("views", ret.replace("><", ">\n<"));

        return result;
    }

    /**
     * @param jsonObject
     * @return xml of getOntoLastVersion's result
     */

    public String ontoLastVersionXML(JSONObject jsonObject) {
        Date now = new Date();
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String xml = "";


        //retrieve the info from the ontologies
        jsonObject = getSimpleJSONObject((String) ((JSONObject) jsonObject.get("links")).get("latest_submission"));
        if(jsonObject != null && jsonObject.size() > 0) {
            String contactname = "";
            String contactemail = "";
            String categor = "";
            String groups = "";
            String viewsID = "";
            String versionID = StringEscapeUtils.escapeXml(String.valueOf(jsonObject.get("submissionId")));
            String creationdate = StringEscapeUtils.escapeXml(String.valueOf(jsonObject.get("creationDate")));
            String releaseddate = StringEscapeUtils.escapeXml(String.valueOf(jsonObject.get("released")));
            String versionnumber = StringEscapeUtils.escapeXml(String.valueOf(jsonObject.get("version")));
            String description = StringEscapeUtils.escapeXml(String.valueOf(jsonObject.get("description")));
            String format = StringEscapeUtils.escapeXml(String.valueOf(jsonObject.get("hasOntologyLanguage")));

            if(((JSONArray) jsonObject.get("contact")).size() > 0) {
                contactname = StringEscapeUtils
                        .escapeXml(String.valueOf(((JSONObject) ((JSONArray) jsonObject.get("contact")).get(0)).get("name")));
                contactemail = StringEscapeUtils
                        .escapeXml(String.valueOf(((JSONObject) ((JSONArray) jsonObject.get("contact")).get(0)).get("email")));
            }

            String homepage = StringEscapeUtils.escapeXml(String.valueOf(jsonObject.get("homepage")));
            String publication = StringEscapeUtils.escapeXml(String.valueOf(jsonObject.get("publication")));
            String documentation = StringEscapeUtils.escapeXml(String.valueOf(jsonObject.get("documentation")));
            String status = StringEscapeUtils.escapeXml(String.valueOf(jsonObject.get("status")));

            jsonObject = (JSONObject) jsonObject.get("ontology");
            String ontoID = StringEscapeUtils.escapeXml(String.valueOf(jsonObject.get("@id")));
            boolean summaryOnly = jsonObject.get("summaryOnly") != null && (Boolean) jsonObject.get("summaryOnly");
            String displayLabel = StringEscapeUtils.escapeXml(String.valueOf(jsonObject.get("name")));

            String iD = StringEscapeUtils.escapeXml(String.valueOf(jsonObject.get("acronym")));

            jsonObject = (JSONObject) jsonObject.get("links");
            String download = StringEscapeUtils.escapeXml(String.valueOf(jsonObject.get("download")));
            JSONArray jsonArray = getSimpleJSONArray((String) jsonObject.get("categories"));
            if(jsonArray != null) {
                for(Object cat : jsonArray) {
                    categor += "<string>" + StringEscapeUtils.escapeXml(String.valueOf(((JSONObject) cat).get("acronym"))) + "</string>";
                }
            }

            jsonArray = getSimpleJSONArray((String) jsonObject.get("groups"));
            if(jsonArray != null) {
                for(Object gr : jsonArray) {
                    groups += "<string>" + StringEscapeUtils.escapeXml(String.valueOf(((JSONObject) gr).get("acronym"))) + "</string>";
                }
            }

            jsonArray = getSimpleJSONArray((String) jsonObject.get("views"));
            if(jsonArray != null) {
                for(Object vie : jsonArray) {
                    JSONObject view = (JSONObject) vie;
                    viewsID += "<string>" + StringEscapeUtils.escapeXml(String.valueOf(view.get("acronym"))) + "</string>";
                }
            }

            xml += "<ontologyBean><id>" + iD + "</id><ontologyId>" + iD + "</ontologyId><virtualViewIds>" + viewsID +
                    "</virtualViewIds><internalVersionNumber>" + versionID + "</internalVersionNumber><userIds/><versionNumber>" +
                    versionnumber + "</versionNumber><versionStatus>" + status +
                    "</versionStatus><isRemote/><statusId/><dateCreated>" + creationdate + "</dateCreated><dateReleased>" +
                    releaseddate + "</dateReleased><isManual/><displayLabel>" + displayLabel + "</displayLabel><description>" +
                    description + "</description><abbreviation>" + iD + "</abbreviation><format>" + format +
                    "</format><contactName>" + contactname + "</contactName><contactEmail>" + contactemail +
                    "</contactEmail><homepage>" + homepage + "</homepage><documentation>" + documentation +
                    "</documentation><urn>" + ontoID + "</urn><publication>" + publication +
                    "</publication><isFlat/><isFoundry/><isMetadataOnly>" + summaryOnly +
                    "</isMetadataOnly><synonymSlot/><preferredNameSlot/><documentationSlot/><authorSlot/><userAcl/><isView>false" +
                    "</isView><categoryIds>" + categor + "</categoryIds><groupIds>" + groups +
                    "</groupIds><filenames/><filePath/><downloadLocation>" + download + "</downloadLocation><hasViews>" + viewsID +
                    "</hasViews>" + "<viewOnOntologyVersionId/></ontologyBean>";

            System.out.println("llega aqui onto = " + iD);
        }


        //put all the info together to parse to xml later

        String start =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><success><accessedResource>/bioportal/ontologies</accessedResource><accessDate" +
                        ">" + formater.format(now) + "</accessDate><data><list>";
        String ret = start + xml.replace(">null<", "><") + "</list></data></success>";

        try {
            //Format the string result to xml
            Transformer serializer = SAXTransformerFactory.newInstance().newTransformer();
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            Source xmlSource = new SAXSource(new InputSource(new ByteArrayInputStream(ret.getBytes())));
            StreamResult res = new StreamResult(new ByteArrayOutputStream());
            serializer.transform(xmlSource, res);
            ret = new String(((ByteArrayOutputStream) res.getOutputStream()).toByteArray());
        } catch(TransformerException e) {
            e.printStackTrace();
        }

        return ret.replace("><", ">\n<");
    }

    /**
     * @param jsonObject
     * @return xml of getTerm's result
     */
    public String termXML(JSONObject jsonObject) {
        Date now = new Date();
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String xml = "";


        //retrieve the info from the ontologies
        JSONObject onto = getClassComplete((String) ((JSONObject) jsonObject.get("links")).get("self"));
        if(onto != null && onto.size() > 0) {

            String fullID = StringEscapeUtils.escapeXml(String.valueOf(onto.get("@id")));
            String prefLabel = StringEscapeUtils.escapeXml(String.valueOf(onto.get("prefLabel")));
            String iD = "";
            String definition = "";
            JSONArray definitions = (JSONArray) onto.get("definition");
            if(definitions != null) {
                for(Object value : definitions) {
                    definition = "<string>" + StringEscapeUtils.escapeXml((String) value) + "</string>";
                }
            }
            String tipo = onto.get("@type").toString();
            tipo = StringEscapeUtils.escapeXml(tipo.substring(tipo.lastIndexOf("#") + 1).toLowerCase());

            String obsolete = "0";
            Set propertiesSet = ((JSONObject) onto.get("properties")).keySet();

            String prop = "";

            //relations
            for(Object key : propertiesSet) {
                JSONArray proparr = (JSONArray) ((JSONObject) onto.get("properties")).get(key);
                if(((String) key).contains("deprecated")) {
                    obsolete = proparr.get(0) == "true" ? "1" : "0";
                } else if(((String) key).contains("prefixIRI")) {
                    iD = StringEscapeUtils.escapeXml(proparr.get(0).toString());
                } else if(!((String) key).contains("subClassOf") && !((String) key).contains("type")) {
                    prop += "<entry><string>" + StringEscapeUtils.escapeXml(key.toString()) + "</string><list>";
                    for(Object value : proparr) {

                        prop += "<string>" + StringEscapeUtils.escapeXml((String) value) + "</string>";
                    }

                    prop += "</list></entry>";

                }
            }

            JSONObject links = (JSONObject) onto.get("links");
            //Subclass
            String childsinfo = "";
            JSONObject childs = getClassComplete((String) links.get("children"));
            int nchilds = 0;
            int pages = Integer.valueOf(childs.get("pageCount").toString());
            int page = Integer.valueOf(childs.get("page").toString());
            do {
                JSONArray children = (JSONArray) childs.get("collection");
                if(children != null && children.size() > 0) {
                    //obtain all necesary data from the childs
                    for(Object child : children) {
                        JSONObject son = (JSONObject) child;
                        String fullIDchild = StringEscapeUtils.escapeXml(String.valueOf(son.get("@id")));
                        String prefLabelchild = StringEscapeUtils.escapeXml(String.valueOf(son.get("prefLabel")));
                        String iDchild = "";
                        String definitionchild = "";
                        JSONArray definitionschild = (JSONArray) son.get("definition");
                        if(definitionschild != null) {
                            for(Object value : definitionschild) {
                                definitionchild = "<string>" + StringEscapeUtils.escapeXml((String) value) + "</string>";
                            }
                        }
                        String tipochild = son.get("@type").toString();
                        tipochild = StringEscapeUtils.escapeXml(tipochild.substring(tipochild.lastIndexOf("#") + 1).toLowerCase());
                        String obsoletechild = "0";
                        Set propertiesSetchild = ((JSONObject) son.get("properties")).keySet();
                        String propchild = "";

                        //childs relations
                        for(Object key : propertiesSetchild) {
                            JSONArray proparr = (JSONArray) ((JSONObject) son.get("properties")).get(key);
                            if(((String) key).contains("deprecated")) {
                                obsoletechild = proparr.get(0) == "true" ? "1" : "0";
                            } else if(((String) key).contains("prefixIRI")) {
                                iDchild = StringEscapeUtils.escapeXml(proparr.get(0).toString());
                            } else if(!((String) key).contains("subClassOf") && !((String) key).contains("type")) {
                                propchild += "<entry><string>" + StringEscapeUtils.escapeXml(key.toString()) + "</string><list>";
                                for(Object value : proparr) {
                                    propchild += "<string>" + StringEscapeUtils.escapeXml((String) value) + "</string>";
                                }
                                propchild += "</list></entry>";
                            }
                        }

                        JSONObject links1 = (JSONObject) son.get("links");
                        JSONObject childs1 = getClassComplete((String) links1.get("children"));
                        int nchilds1 = 0;
                        int pages1 = Integer.valueOf(childs1.get("pageCount").toString());
                        int page1 = Integer.valueOf(childs1.get("page").toString());
                        while(pages1 >= page1) {
                            JSONArray children1 = (JSONArray) childs1.get("collection");
                            if(children1 != null && children1.size() > 0) {
                                nchilds1 += children1.size();//sons of the child counter
                            }
                            if(childs1.get("nextPage") != null) {
                                childs1 = getClassComplete((String) ((JSONObject) childs1.get("links")).get("nextPage"));
                                if(childs1 != null) {
                                    page1 = Integer.valueOf(childs1.get("page").toString());
                                } else {
                                    page1 = pages1 + 1;
                                }
                            } else {
                                page1 = pages1 + 1;
                            }
                        }

                        String parentsxml = "<entry><string>rdfs:subClassOf</string><list><classBean><id>" + iD + "</id><fullId>" + fullID +
                                "</fullId><label>" + prefLabel + "</label><type>" + tipo + "</type><isObsolete>" + obsolete +
                                "</isObsolete><relations/></classBean></list></entry>";

                        String childcount = "<entry><string>ChildCount</string><int>" + nchilds1 + "</int></entry>";

                        childsinfo += "<classBean><id>" + iDchild + "</id><fullId>" + fullIDchild + "</fullId><label>" + prefLabelchild +
                                "</label><type>" + tipochild + "</type><definitions>" + definitionchild + "</definitions><isObsolete>" +
                                obsoletechild +
                                "</isObsolete><relations><entry><string>SubClass</string><list/></entry><entry><string>RdfType</string" +
                                "><list/></entry>" + parentsxml + childcount + propchild + "</relations></classBean>";

                        nchilds++;//general Child Count
                    }
                }
                if(childs.get("nextPage") != null) {
                    childs = getClassComplete((String) (((JSONObject) childs.get("links")).get("nextPage")));
                    if(childs != null) {
                        page = Integer.valueOf(childs.get("page").toString());
                    } else {
                        page = pages + 1;
                    }
                } else {
                    page = pages + 1;
                }
            } while(pages >= page);
            String subclass = "<entry><string>SubClass</string><list>" + childsinfo + "</list></entry>";


            //subclassOf
            String parent = "";
            JSONArray parents = getJSONArrayComplete((String) links.get("parents"));
            for(Object obj : parents) {
                JSONObject parentObj = (JSONObject) obj;
                String parentfullID = StringEscapeUtils.escapeXml(String.valueOf(parentObj.get("@id")));
                String parentPrefLabel = StringEscapeUtils.escapeXml(String.valueOf(parentObj.get("prefLabel")));
                String parenttipo = parentObj.get("@type").toString();
                parenttipo = StringEscapeUtils.escapeXml(parenttipo.substring(parenttipo.lastIndexOf("#") + 1).toLowerCase());
                String parentId = "";
                String parentObsolete = "0";

                propertiesSet = ((JSONObject) parentObj.get("properties")).keySet();
                for(Object key : propertiesSet) {
                    JSONArray proparr = (JSONArray) ((JSONObject) parentObj.get("properties")).get(key);
                    if(((String) key).contains("deprecated")) {
                        parentObsolete = proparr.get(0) == "true" ? "1" : "0";
                    } else if(((String) key).contains("prefixIRI")) {
                        parentId = StringEscapeUtils.escapeXml(proparr.get(0).toString());
                    }
                }
                parent += "<classBean><id>" + parentId + "</id><fullId>" + parentfullID + "</fullId><label>" + parentPrefLabel +
                        "</label><type>" + parenttipo + "</type><isObsolete>" + parentObsolete +
                        "</isObsolete><relations/></classBean>";
            }
            parent = "<entry><string>rdfs:subClassOf</string><list>" + parent + "</list></entry>";
            String childcount = "<entry><string>ChildCount</string><int>" + nchilds + "</int></entry>";
            //superclass

            String superinfo = "";
            JSONArray antecesor = getSimpleJSONArray((String) links.get("ancestors"));
            if(antecesor != null && antecesor.size() > 0) {
                //obtain all necesary data from the childs
                for(Object anteces : antecesor) {
                    JSONObject classsuper =
                            getClassComplete(String.valueOf(((JSONObject) ((JSONObject) anteces).get("links")).get("self")));
                    String fullIDsuper = StringEscapeUtils.escapeXml(String.valueOf(classsuper.get("@id")));
                    String prefLabelsuper = StringEscapeUtils.escapeXml(String.valueOf(classsuper.get("prefLabel")));
                    String iDsuper = "";
                    String definitionsuper = "";
                    JSONArray definitionssuper = (JSONArray) classsuper.get("definition");
                    if(definitionssuper != null) {
                        for(Object value : definitionssuper) {
                            definitionsuper = "<string>" + StringEscapeUtils.escapeXml((String) value) + "</string>";
                        }
                    }
                    String tiposuper = classsuper.get("@type").toString();
                    tiposuper = StringEscapeUtils.escapeXml(tiposuper.substring(tiposuper.lastIndexOf("#") + 1).toLowerCase());
                    String obsoletesuper = "0";
                    Set propertiesSetsuper = ((JSONObject) classsuper.get("properties")).keySet();
                    String propsuper = "";

                    //childs relations
                    for(Object key : propertiesSetsuper) {
                        JSONArray proparr = (JSONArray) ((JSONObject) classsuper.get("properties")).get(key);
                        if(((String) key).contains("deprecated")) {
                            obsoletesuper = proparr.get(0) == "true" ? "1" : "0";
                        } else if(((String) key).contains("prefixIRI")) {
                            iDsuper = StringEscapeUtils.escapeXml(proparr.get(0).toString());
                        } else if(!((String) key).contains("subClassOf") && !((String) key).contains("type")) {
                            propsuper += "<entry><string>" + StringEscapeUtils.escapeXml(key.toString()) + "</string><list>";
                            for(Object value : proparr) {
                                propsuper += "<string>" + StringEscapeUtils.escapeXml((String) value) + "</string>";
                            }
                            propsuper += "</list></entry>";
                        }
                    }

                    JSONObject links1 = (JSONObject) classsuper.get("links");
                    JSONObject childs1 = getClassComplete((String) links1.get("children"));
                    int nchilds1 = 0;
                    int pages1 = Integer.valueOf(childs1.get("pageCount").toString());
                    int page1 = Integer.valueOf(childs1.get("page").toString());
                    while(pages1 >= page1) {
                        JSONArray children1 = (JSONArray) childs1.get("collection");
                        if(children1 != null && children1.size() > 0) {
                            nchilds1 += children1.size();//sons of the child counter
                        }
                        if(childs1.get("nextPage") != null) {
                            String nextpage = "";
                            try {
                                nextpage = URLDecoder.decode(String.valueOf(((JSONObject) childs1.get("links")).get("nextPage")), "UTF-8");
                            } catch(UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            childs1 = getSimpleJSONObject(nextpage);
                            if(childs1 != null) {
                                page1 = Integer.valueOf(childs1.get("page").toString());
                            } else {
                                page1 = pages1 + 1;
                            }
                        } else {
                            page1 = pages1 + 1;
                        }
                    }

                    String Superchildscount = "<entry><string>ChildCount</string><int>" + nchilds1 + "</int></entry>";

                    superinfo += "<classBean><id>" + iDsuper + "</id><fullId>" + fullIDsuper + "</fullId><label>" + prefLabelsuper +
                            "</label><type>" + tiposuper + "</type><definitions>" + definitionsuper + "</definitions><isObsolete>" +
                            obsoletesuper + "</isObsolete><relations><entry><string>SubClass</string><list/></entry><entry>" +
                            "<string>RdfType</string><list/></entry>" + Superchildscount + propsuper + "</relations></classBean>";

                    nchilds++;//general Child Count
                }
            }

            String supreclass = "<entry><string>SuperClass</string><list>" + superinfo + "</list></entry>";

            xml += "<?xml version=\"1.0\" encoding=\"UTF-8\"?><success><accessedResource>/bioportal/virtual/ontology/1104" +
                    "</accessedResource><accessDate>" + formater.format(now) + "</accessDate><data><classBean><id>" + iD +
                    "</id><fullId>" + fullID + "</fullId><label>" + prefLabel + "</label><type>" + tipo + "</type><definitions>" +
                    definition + "</definitions><isObsolete>" + obsolete +
                    "</isObsolete><relations><entry><string>InstanceCount</string><int>0</int></entry>" + subclass +
                    "<entry><string>RdfType</string><list/></entry>" + parent + childcount + prop + supreclass +
                    "</relations></classBean></data></success>";
        }


        //put all the info together to parse to xml later

        String ret = xml.replace(">null<", "><");

        try {
            //Format the string result to xml
            Transformer serializer = SAXTransformerFactory.newInstance().newTransformer();
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            Source xmlSource = new SAXSource(new InputSource(new ByteArrayInputStream(ret.getBytes())));
            StreamResult res = new StreamResult(new ByteArrayOutputStream());
            serializer.transform(xmlSource, res);
            ret = new String(((ByteArrayOutputStream) res.getOutputStream()).toByteArray());
        } catch(TransformerException e) {
            e.printStackTrace();
        }

        return ret.replace("><", ">\n<");
    }

    /**
     * @param jsonObject
     * @return xml of get roots of an ontology
     */
    public String rootXML(JSONObject jsonObject) {

        Date now = new Date();
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String xml = "";
        String roots = "";

        jsonObject = getSimpleJSONObject((String) ((JSONObject) jsonObject.get("links")).get("latest_submission"));

        JSONArray jsonArray =
                getJSONArrayComplete((String) ((JSONObject) ((JSONObject) jsonObject.get("ontology")).get("links")).get("roots"));

        //retrieve the info from the ontologies
        for(Object jObj : jsonArray) {
            jsonObject = (JSONObject) jObj;

            if(jsonObject != null && jsonObject.size() > 0) {

                String fullID = StringEscapeUtils.escapeXml(String.valueOf(jsonObject.get("@id")));
                String prefLabel = StringEscapeUtils.escapeXml(String.valueOf(jsonObject.get("prefLabel")));
                String iD = "";
                String tipo = jsonObject.get("@type").toString();
                tipo = StringEscapeUtils.escapeXml(tipo.substring(tipo.lastIndexOf("#") + 1).toLowerCase());

                String obsolete = "0";
                Set propertiesSet = ((JSONObject) jsonObject.get("properties")).keySet();

                String prop = "";

                //relations
                for(Object key : propertiesSet) {
                    JSONArray proparr = (JSONArray) ((JSONObject) jsonObject.get("properties")).get(key);
                    if(((String) key).contains("deprecated")) {
                        obsolete = proparr.get(0) == "true" ? "1" : "0";
                    } else if(((String) key).contains("prefixIRI")) {
                        iD = StringEscapeUtils.escapeXml(proparr.get(0).toString());
                    } else if(!((String) key).contains("subClassOf") && !((String) key).contains("type")) {
                        prop += "<entry><string>" + StringEscapeUtils.escapeXml(key.toString()) + "</string><list>";
                        for(Object value : proparr) {

                            prop += "<string>" + StringEscapeUtils.escapeXml((String) value) + "</string>";
                        }

                        prop += "</list></entry>";

                    }
                }
                roots += "<propertyBean><id>" + iD + "</id><fullId>" + fullID + "</fullId><label>" + prefLabel + "</label><type>" + tipo +
                        "</type><isObsolete>" + obsolete + "</isObsolete><relations>" + prop + "</relations></propertyBean>";
            }
        }

        xml += "<?xml version=\"1.0\" encoding=\"UTF-8\"?><success><accessedResource>/bioportal/ontologies/properties/" +
                "</accessedResource><accessDate>" + formater.format(now) + "</accessDate><data><list>" + roots + "</list></data></success>";

        //put all the info together to parse to xml later

        String ret = xml.replace(">null<", "><");

        try {
            //Format the string result to xml
            Transformer serializer = SAXTransformerFactory.newInstance().newTransformer();
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            Source xmlSource = new SAXSource(new InputSource(new ByteArrayInputStream(ret.getBytes())));
            StreamResult res = new StreamResult(new ByteArrayOutputStream());
            serializer.transform(xmlSource, res);
            ret = new String(((ByteArrayOutputStream) res.getOutputStream()).toByteArray());
        } catch(TransformerException e) {
            e.printStackTrace();
        }

        return ret.replace("><", ">\n<");
    }

    /**
     * @param jsonObject
     * @param pagesize
     * @return xml of getAllTerms's result
     */
    public String allTermsXML(JSONObject jsonObject, int pagesize) {
        Date now = new Date();
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String xml = "";
        int pages = Integer.valueOf(jsonObject.get("pageCount").toString());
        int page = Integer.valueOf(jsonObject.get("page").toString());
        int respage = 0;

        JSONArray jsonArray = (JSONArray) jsonObject.get("collection");

        String ontogeneral = "";

        String class_ = "";
        int i = 0;
        for(Object clas : jsonArray) {
            jsonObject = (JSONObject) ((JSONObject) clas).get("links");
            String onto = String.valueOf(jsonObject.get("ontology"));
            onto = onto.substring(onto.lastIndexOf("/") + 1);
            String self = String.valueOf(jsonObject.get("self"));
            //            self = self.substring(self.lastIndexOf("http"));

            //                selfobj = getTerm(onto, URLDecoder.decode(self, "UTF-8"));
            jsonObject = getSimpleJSONObject(self);
            if(ontogeneral.equals("")) {
                ontogeneral = onto;
            }

            if(jsonObject != null) {
                System.out.println("pasa " + i++);
                String term = termXML(jsonObject);
                class_ += term.substring(term.indexOf("<classBean>"), term.lastIndexOf("</classBean>") + "</classBean>".length());
                class_ = class_.replace("\r\n", "");
                while(class_.contains(" <")) {
                    class_ = class_.replace(" <", "<");
                }
                respage++;
            }
        }
        jsonArray = null;
        if(page < pages) {
            jsonArray = (JSONArray) getAllTerms(ontogeneral, pages, pagesize).get("collection");
        }

        int nresults = jsonArray != null ? jsonArray.size() : 0;

        xml += "<page><pageNum>" + page + "</pageNum><numPages>" + pages + "</numPages><pageSize>" + pagesize +
                "</pageSize><numResultsPage>" + respage + "</numResultsPage><numResultsTotal>" +
                (pagesize * (pages - 1) + nresults) + "</numResultsTotal><contents class=\"org.ncbo.stanford.bean.concept." +
                "ClassBeanResultListBean\"><classBeanResultList>" + class_ + "</classBeanResultList></contents></page>";


        String start = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><success><accessedResource>/bioportal/ontologies</accessedResource>" +
                "<accessDate>" + formater.format(now) + "</accessDate><data>";
        String ret = start + xml.replace(">null<", "><") + "</data></success>";

        try {
            //Format the string result to xml
            Transformer serializer = SAXTransformerFactory.newInstance().newTransformer();
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            Source xmlSource = new SAXSource(new InputSource(new ByteArrayInputStream(ret.getBytes())));
            StreamResult res = new StreamResult(new ByteArrayOutputStream());
            serializer.transform(xmlSource, res);
            ret = new String(((ByteArrayOutputStream) res.getOutputStream()).toByteArray());
        } catch(TransformerException e) {
            e.printStackTrace();
        }

        return ret.replace("><", ">\n<");
    }

    /**
     * This method search the term on the clases and ontologies and returns the one that have it
     *
     * @param term term to search
     * @param onto optional parameter to narrow the results to a concrete ontology
     * @return
     */
    public JSONArray getRecomendation(String term, String onto) {
        String urlToGet = urlBASE + "recommender?text=" + term + "&include_classes=true&format=json&" + apikey;
        if(onto != null && onto.length() > 0 && !onto.equals("null")) {
            urlToGet += "&ontologies=" + onto;
        }

        JSONArray jsonObject = null;
        try {
            String line;
            URL url = new URL(urlToGet);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            int resp = conn.getResponseCode();
            if(resp == 200) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                JSONParser jpar = new JSONParser();
                String result = "";
                while((line = rd.readLine()) != null) {
                    result += line;
                }
                rd.close();
                jsonObject = (JSONArray) jpar.parse(result);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * @return all the ontologies and its views
     */
    public JSONArray getOntologies() {
        String urlToGet = urlBASE + "ontologies?" + apikey;

        JSONArray jsonObject = null;
        try {
            String line;
            URL url = new URL(urlToGet);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            int resp = conn.getResponseCode();
            if(resp == 200) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                JSONParser jpar = new JSONParser();
                String result = "";
                while((line = rd.readLine()) != null) {
                    result += line;
                }
                rd.close();
                jsonObject = (JSONArray) jpar.parse(result);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * gets the last version of an ontology
     *
     * @param id id of the ontology ex: BRO, NCIT
     * @return the ontology
     */
    public JSONObject getOntoLastVersion(String id) {
        String urlToGet = urlBASE + "ontologies/" + id + "?" + apikey;

        JSONObject jsonObject = null;
        try {
            String line;
            URL url = new URL(urlToGet);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            int resp = conn.getResponseCode();
            if(resp == 200) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                JSONParser jpar = new JSONParser();
                String result = "";
                while((line = rd.readLine()) != null) {
                    result += line;
                }
                rd.close();
                jsonObject = (JSONObject) jpar.parse(result);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * gets the class from an ontology
     *
     * @param ontId ontology where the class was included
     * @param term  class name
     * @return the class
     */
    public JSONObject getTerm(String ontId, String term) {

        JSONObject jsonObject = null;
        try {
            String urlToGet;
            //            String enco = URLEncoder.encode(term, "UTF-8");
            if(term.contains("%3") || term.contains("%2")) {
                urlToGet = urlBASE + "ontologies/" + ontId + "/classes/" + term + "?" + apikey;
            } else {
                urlToGet = urlBASE + "ontologies/" + ontId + "/classes/" + URLEncoder.encode(term, "UTF-8") + "?" + apikey;
            }
            String line;
            URL url = new URL(urlToGet);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            int resp = conn.getResponseCode();
            if(resp == 200) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                JSONParser jpar = new JSONParser();
                String result = "";
                while((line = rd.readLine()) != null) {
                    result += line;
                }
                rd.close();
                jsonObject = (JSONObject) jpar.parse(result);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject searchTerm(String term, String onto, String concept, boolean exactMatch, int page, int pagesize) {
        JSONObject jsonObject = null;
        try {
            if(onto != null && !onto.equals("null") && onto.length() > 0) {
                if(onto.contains("%3") || onto.contains("%2")) {
                    onto = "&ontology=" + onto;
                } else {
                    onto = "&ontology=" + URLEncoder.encode(onto, "UTF-8");
                }
            } else
                onto = "";
            if(concept != null && !concept.equals("null") && concept.length() > 0) {

                if(concept.contains("%3") || concept.contains("%2")) {
                    concept = "&subtree_root=" + concept;
                } else {
                    concept = "&subtree_root=" + URLEncoder.encode(concept, "UTF-8");
                }

            } else
                concept = "";
            term = term.replace(" ", "+");
            String urlToGet = urlBASE + "search?q=" + term + onto + concept + "&" + apikey;
            String line;
            if(exactMatch) {
                urlToGet += "&exact_match=true";
            }
            if(page > 1)
                urlToGet += "&page=" + page;
            if(pagesize > 1) {
                if(pagesize > 500)
                    pagesize = 500;
                urlToGet += "&pagesize=" + pagesize;
            }

            URL url = new URL(urlToGet);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            int resp = conn.getResponseCode();
            if(resp == 200) {
                System.out.println("responde 200");
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                JSONParser jpar = new JSONParser();
                String result = "";
                while((line = rd.readLine()) != null) {
                    result += line;
                }
                rd.close();
                jsonObject = (JSONObject) jpar.parse(result);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * Gets all the class included on a ontology
     *
     * @param ontId    id of the ontology
     * @param page     page of the results to retrieve
     * @param pagesize size of the page to return
     * @return
     */
    public JSONObject getAllTerms(String ontId, int page, int pagesize) {

        JSONObject jsonObject = null;
        try {
            if(pagesize > 5000) {
                pagesize = 5000;
            }
            if(page < 1) {
                page = 1;
            }
            String urlToGet = urlBASE + "ontologies/" + ontId + "/classes" + "?" + apikey + "&page=" + page + "&pagesize=" + pagesize;
            String line;
            URL url = new URL(urlToGet);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            int resp = conn.getResponseCode();
            if(resp == 200) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                JSONParser jpar = new JSONParser();
                String result = "";
                while((line = rd.readLine()) != null) {
                    result += line;
                }
                rd.close();
                jsonObject = (JSONObject) jpar.parse(result);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}