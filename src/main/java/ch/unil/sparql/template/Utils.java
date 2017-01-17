package ch.unil.sparql.template;

import org.apache.commons.collections4.MapUtils;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.shared.impl.PrefixMappingImpl;

import java.util.HashMap;
import java.util.Map;

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
        prefixMap.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");

        return prefixMap;

    }

    public static Map<String, String> dbpediaPrefixMap(){
        return MapUtils.putAll(new HashMap<>(),
                new String[]{"dbr", "http://dbpedia.org/resource/",
                        "dbp", "http://dbpedia.org/property/",
                        "dbo", "http://dbpedia.org/ontology/",
                });
    }

}
