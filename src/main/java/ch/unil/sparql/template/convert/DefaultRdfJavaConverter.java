package ch.unil.sparql.template.convert;

import ch.unil.sparql.template.mapping.RdfProperty;
import org.apache.jena.graph.Node_Literal;
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
    public boolean canConvert(Node_Literal node, RdfProperty property) {

        final Class<?> propertyType = property.isSimpleProperty() ? property.getType() : property.getActualType();
        final Class<?> nodeType = node.getLiteralValue().getClass();

        for (Class<?> customType : customTypes) {
            if (customType.equals(propertyType)) {
                return true;
            }
        }

        return propertyType.isAssignableFrom(nodeType);
    }

    @Override
    public Object convert(Node_Literal node, RdfProperty property) {
        if (node == null) {
            return null;
        }
        final Class<?> propertyType = property.isSimpleProperty() ? property.getType() : property.getActualType();

        final Object literalValue = node.getLiteralValue();
        logger.debug("Converting node " + node + ", XSDDataType: " + literalValue.getClass().getSimpleName() +
                ", to a value for property " + property);

        return convertLiteralValueToJava(literalValue, propertyType);
    }

    protected <T> Object convertLiteralValueToJava(Object literalValue, Class<T> propertyType) {
        return literalValue;
    }

}
