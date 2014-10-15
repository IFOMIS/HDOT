package uk.ac.ebi.ontocat.bioportal.test;

import uk.ac.ebi.ontocat.bioportal.BioportalNewAPI;

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 8/10/14
 * Time: 9:23
 * To change this template use File | Settings | File Templates.
 */
public class TestNewAPI {

    public static void main(String[] args) {
        BioportalNewAPI par = new BioportalNewAPI();
        long start = System.currentTimeMillis();

        //        JSONObject jterm = par.getTerm("BRO", "http://bioontology.org/ontologies/BiomedicalResourceOntology.owl#Software");

        //        JSONObject jterm = par.getTerm("EFO", "http%3A%2F%2Fpurl.obolibrary.org%2Fobo%2FCHEBI_23888");
        //        String term = par.termXML(jterm);
        //        System.out.println(term);

        //        JSONObject jobject = par.getOntoLastVersion("TMO");
        //        String ont = par.ontoLastVersionXML(jobject);
        //        System.out.println(ont);

        //        JSONObject jobject = par.getOntoLastVersion("NCIT");
        //        String ont = par.rootXML(jobject);
        //        System.out.println(ont);

        //        JSONArray jsonObject2 = par.getOntologies();
        //        HashMap<String, String> a = par.allOntologiesXML(jsonObject2);
        //        System.out.println("tamaño de todas las ontologias:" + (a.get("ontologies").length() + a.get("views").length()));

        //                String termino = "drug";
        //                JSONArray jsonObject = par.getRecomendation(termino, "");
        //                System.out.println("Xml de lo que hay hasta ahora:\n" + par.recommendationXML(jsonObject));

        //        JSONObject allterms = par.getAllTerms("BRO", 7, 50);
        //        String xml = par.allTermsXML(allterms, 50);
        //        System.out.println(xml);

        long fin = System.currentTimeMillis();
        System.out.println("acaba de descargar y tarda: " + (fin - start) / 1000);

    }
}
