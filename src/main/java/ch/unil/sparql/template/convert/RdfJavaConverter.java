package ch.unil.sparql.template.convert;

import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.graph.Node_Literal;
import org.springframework.core.convert.converter.Converter;

/**
 * @author gushakov
 */
public class RdfJavaConverter implements Converter<Node_Literal, Object> {
    @Override
    public Object convert(Node_Literal node) {
        if (node != null) {
            final Object rawRdfValue = node.getLiteralValue();
            if (rawRdfValue instanceof XSDDateTime) {
                return ((XSDDateTime) rawRdfValue).asCalendar().getTime();
            }
            return rawRdfValue;
        } else {
            return null;
        }
    }
}
