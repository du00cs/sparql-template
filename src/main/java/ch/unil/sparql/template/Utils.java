package ch.unil.sparql.template;

import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.shared.impl.PrefixMappingImpl;

/**
 * @author gushakov
 */
public class Utils {

    public static PrefixMapping defaultPrefixMap() {

        final PrefixMapping prefixMap = new PrefixMappingImpl();

        prefixMap.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
        prefixMap.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        prefixMap.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        prefixMap.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
        prefixMap.setNsPrefix("dc", "http://purl.org/dc/elements/1.1/");
        prefixMap.setNsPrefix("dbpedia", "http://dbpedia.org/");
        prefixMap.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");

        return prefixMap;

    }

}
