package ch.unil.sparql.template.convert;

import java.util.UUID;

/**
 * @author gushakov
 */
public class CustomRdfJavaConverter extends ExtendedRdfJavaConverter {

    public CustomRdfJavaConverter() {
        super(UUID.class);
    }

    @Override
    protected Object convertLiteralValueToJava(Object literalValue, Class<?> propertyType) {

        if (UUID.class.isAssignableFrom(propertyType)){
            try {
                return UUID.fromString(literalValue.toString());
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Cannot create an instance of UUID from " + literalValue);
            }
        }

        return super.convertLiteralValueToJava(literalValue, propertyType);
    }
}
