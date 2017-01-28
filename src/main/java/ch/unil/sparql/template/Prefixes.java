package ch.unil.sparql.template;

/**
 * Well-known prefixes and their namespaces. Will be automatically registered with the default prefix map.
 * @author gushakov
 * @see Utils#defaultPrefixMap()
 */
public final class Prefixes {

    public static final String XSD = "xsd";
    public static final String XSD_NS = "http://www.w3.org/2001/XMLSchema#";

    public static final String RDF = "rdf";
    public static final String RDF_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    public static final String RDFS = "rdfs";
    public static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";

    public static final String FOAF = "foaf";
    public static final String FOAF_NS = "http://xmlns.com/foaf/0.1/";

    public static final String DC = "dc";
    public static final String DC_NS = "http://purl.org/dc/elements/1.1/";

    public static final String OWL = "owl";
    public static final String OWL_NS = "http://www.w3.org/2002/07/owl#";

    public static final String DBR = "dbr";
    public static final String DBR_NS = "http://dbpedia.org/resource/";

    public static final String DBP = "dbp";
    public static final String DBP_NS = "http://dbpedia.org/property/";

    public static final String DBO = "dbo";
    public static final String DBO_NS = "http://dbpedia.org/ontology/";

}
