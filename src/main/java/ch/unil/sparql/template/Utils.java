package ch.unil.sparql.template;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;

/**
 * @author gushakov
 */
public class Utils {

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
