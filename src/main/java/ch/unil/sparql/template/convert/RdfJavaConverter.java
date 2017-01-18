package ch.unil.sparql.template.convert;

import ch.unil.sparql.template.mapping.RdfProperty;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.graph.Node_Literal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * @author gushakov
 */
public class RdfJavaConverter {
    private static final Logger logger = LoggerFactory.getLogger(RdfJavaConverter.class);

    public boolean canConvert(Node_Literal node, RdfProperty property){

        final Class<?> propertyType = property.isSimpleProperty() ? property.getType() : property.getActualType();
        final Class<?> nodeType = node.getLiteralValue().getClass();

        if (nodeType.equals(XSDDateTime.class)) {
            return ZonedDateTime.class.isAssignableFrom(propertyType) || Date.class.isAssignableFrom(propertyType);
        }
        else {
            return propertyType.isAssignableFrom(nodeType);
        }

    }

    public Object convert(Node_Literal node, RdfProperty property) {
        if (node == null) {
            return null;
        }
        final Class<?> propertyType = property.isSimpleProperty() ? property.getType() : property.getActualType();

        final Object literalValue = node.getLiteralValue();
        logger.debug("Converting node " + node + ", XSDDataType: " + literalValue.getClass().getSimpleName() +
                ", to a value for property " + property);

        // convert dates
        if (literalValue instanceof XSDDateTime) {

            final Calendar calendar = ((XSDDateTime) literalValue).asCalendar();
            if (ZonedDateTime.class.isAssignableFrom(propertyType)) {
                return convertToZonedDateTime(calendar);
            }

            if (Date.class.isAssignableFrom(propertyType)) {
                return convertToDate(calendar);
            }
        }

        // default Java object from XSDDataType conversion
        return literalValue;

    }

    private ZonedDateTime convertToZonedDateTime(Calendar calendar) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(calendar.getTimeInMillis()), calendar.getTimeZone().toZoneId());
    }

    private Date convertToDate(Calendar calendar) {
        return calendar.getTime();
    }
}
