package ch.unil.sparql.template.convert;

import org.apache.jena.datatypes.xsd.XSDDateTime;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * @author gushakov
 */
public class ExtendedRdfJavaConverter extends DefaultRdfJavaConverter {

    public ExtendedRdfJavaConverter() {
        super(Date.class, ZonedDateTime.class);
    }

    @Override
    protected <T> Object convertLiteralValueToJava(Object literalValue, Class<T> propertyType) {
        // convert dates
        if (literalValue instanceof XSDDateTime) {

            final Calendar calendar = ((XSDDateTime) literalValue).asCalendar();
            if (ZonedDateTime.class.isAssignableFrom(propertyType)) {
                return ZonedDateTime.ofInstant(Instant.ofEpochMilli(calendar.getTimeInMillis()), calendar.getTimeZone().toZoneId());
            }

            if (Date.class.isAssignableFrom(propertyType)) {
                return calendar.getTime();
            }
        }

        // convert by default
        return super.convertLiteralValueToJava(literalValue, propertyType);
    }
}
