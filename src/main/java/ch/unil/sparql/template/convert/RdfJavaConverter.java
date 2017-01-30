package ch.unil.sparql.template.convert;

import ch.unil.sparql.template.mapping.RdfProperty;
import org.apache.jena.graph.Node_Literal;

import java.util.Set;

/**
 * @author gushakov
 */
public interface RdfJavaConverter {

    Set<Class<?>> getCustomTypes();

    boolean canConvert(Node_Literal node, RdfProperty property);

    Object convert(Node_Literal node, RdfProperty property);
}
