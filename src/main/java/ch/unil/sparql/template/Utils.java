package ch.unil.sparql.template;

import org.apache.commons.collections4.MapUtils;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.shared.impl.PrefixMappingImpl;

import java.util.HashMap;

import static ch.unil.sparql.template.Prefixes.*;

/**
 * @author gushakov
 */
public class Utils {

    public static PrefixMapping defaultPrefixMap() {

        final PrefixMapping prefixMap = new PrefixMappingImpl();
        prefixMap.setNsPrefixes(MapUtils.putAll(new HashMap<>(),
                new String[]{
                        XSD, XSD_NS,
                        RDF, RDF_NS,
                        RDFS, RDFS_NS,
                        FOAF, FOAF_NS,
                        DC, DC_NS,
                        OWL, OWL_NS,
                        DBR, DBR_NS,
                        DBP, DBP_NS,
                        DBO, DBO_NS
                }));

        return prefixMap;

    }

    public static Triple triple(String sUri, String pUri, String oUri){
         return Triple.create(NodeFactory.createURI(sUri),
                 NodeFactory.createURI(pUri),
                 NodeFactory.createURI(oUri));
    }

    public static Triple triple(String sUri, String pUri, String oLexical, XSDDatatype oType){
        return Triple.create(NodeFactory.createURI(sUri),
                NodeFactory.createURI(pUri),
                NodeFactory.createLiteral(oLexical, oType));

    }

    public static Triple triple(String sUri, String pUri, String oLexical, String oLang){
        return Triple.create(NodeFactory.createURI(sUri),
                NodeFactory.createURI(pUri),
                NodeFactory.createLiteral(oLexical, oLang));
    }

}
