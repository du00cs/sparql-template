package ch.unil.sparql.template.convert;

import ch.unil.sparql.template.mapping.RdfProperty;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Literal;
import org.apache.jena.graph.Node_URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author gushakov
 */
public class DefaultRdfJavaConverter implements RdfJavaConverter {
    private static final Logger logger = LoggerFactory.getLogger(DefaultRdfJavaConverter.class);

    private Set<Class<?>> customTypes;

    public DefaultRdfJavaConverter() {
        customTypes = Collections.emptySet();
    }

    public DefaultRdfJavaConverter(Class<?>... customTypes) {
        this.customTypes = new HashSet<>(Arrays.asList(customTypes));
    }

    public DefaultRdfJavaConverter(Set<Class<?>> customTypes) {
        this.customTypes = customTypes;
    }

    @Override
    public Set<Class<?>> getCustomTypes() {
        return customTypes;
    }

    @Override
    public boolean canConvert(Node node, RdfProperty property) {

        final Class<?> propertyType = property.isSimpleProperty() ? property.getType() : property.getActualType();
        for (Class<?> customType : customTypes) {
            if (customType.equals(propertyType)) {
                return true;
            }
        }

        if (node.isLiteral()) {
            final Node_Literal literal = (Node_Literal) node;

            // match the language, if specified on the property
            if (literal.getLiteralLanguage() != null
                    && property.getLanguage() != null
                    && !literal.getLiteralLanguage().equals(property.getLanguage())) {
                return false;
            }

            // otherwise just check for assignment compatibility between the property type and the literal value type
            return propertyType.isAssignableFrom(node.getLiteralValue().getClass());

        } else
            // see if converting from URI to a string
            return node.isURI() && propertyType.equals(String.class);

    }

    @Override
    public Object convert(Node node, RdfProperty property) {
        if (node == null) {
            return null;
        }
        final Class<?> propertyType = property.isSimpleProperty() ? property.getType() : property.getActualType();

        if (node.isLiteral()) {
            final Object literalValue = node.getLiteralValue();
            logger.debug("Converting literal node " + node + ", XSDDataType: " + literalValue.getClass().getSimpleName() +
                    ", to a value for property " + property);
            return convertLiteralValueToJava(literalValue, propertyType);
        } else if (node.isURI()) {
            logger.debug("Converting URI node " + node + " to a value for property " + property);
            return convertNodeUriToJava((Node_URI) node, propertyType);
        } else {
            throw new IllegalStateException("Cannot convert node " + node + " to a value for the property " + property);
        }
    }

    protected Object convertLiteralValueToJava(Object literalValue, Class<?> propertyType) {
        return literalValue;
    }

    protected Object convertNodeUriToJava(Node_URI uriNode, Class<?> propertyType) {
        return uriNode.toString();
    }

}
