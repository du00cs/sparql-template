package ch.unil.sparql.template.convert;

import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.datatypes.xsd.XSDDuration;
import org.apache.jena.graph.Node_URI;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

/**
 * @author gushakov
 */
public class ExtendedRdfJavaConverter extends DefaultRdfJavaConverter {

    public ExtendedRdfJavaConverter() {
        super(Date.class, ZonedDateTime.class, Duration.class, URL.class);
    }

    public ExtendedRdfJavaConverter(Class<?>... customTypes){
        super(new HashSet<>(Arrays.asList(customTypes)));
    }

    @Override
    protected Object convertLiteralValueToJava(Object literalValue, Class<?> propertyType) {

        if (ZonedDateTime.class.isAssignableFrom(propertyType)) {
            final Calendar calendar = ((XSDDateTime) literalValue).asCalendar();
            return ZonedDateTime.ofInstant(Instant.ofEpochMilli(calendar.getTimeInMillis()), calendar.getTimeZone().toZoneId());
        }

        if (Date.class.isAssignableFrom(propertyType)) {
            final Calendar calendar = ((XSDDateTime) literalValue).asCalendar();
            return calendar.getTime();
        }

        if (Duration.class.isAssignableFrom(propertyType)){
           final  XSDDuration duration = (XSDDuration) literalValue;
            if (duration.getYears() != 0 || duration.getMonths() != 0 || duration.getDays() != 0) {
                throw new IllegalStateException("Only time based duration (hours, minutes, seconds, etc.) can be converted. But was " + duration);
            }
            return Duration.parse(literalValue.toString());
        }

        // convert by default
        return super.convertLiteralValueToJava(literalValue, propertyType);
    }

    @Override
    protected Object convertNodeUriToJava(Node_URI uriNode, Class<?> propertyType) {

        // convert String to URL
        if (propertyType.equals(URL.class)){
            try {
                return new URL(uriNode.toString());
            } catch (MalformedURLException e) {
                throw new IllegalStateException("Cannot convert URI node " + uriNode + " to an URL. " + e.getMessage());
            }
        }

        return super.convertNodeUriToJava(uriNode, propertyType);
    }
}
