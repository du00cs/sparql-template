package ch.unil.sparql.template.convert;

import ch.unil.sparql.template.Utils;
import ch.unil.sparql.template.annotation.Predicate;
import ch.unil.sparql.template.annotation.Rdf;
import ch.unil.sparql.template.mapping.RdfMappingContext;
import ch.unil.sparql.template.mapping.RdfProperty;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_Literal;
import org.junit.Test;

import java.net.URL;
import java.time.Duration;
import java.time.Month;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author gushakov
 */
public class ExtendedRdfJavaConverterTest {

    @Rdf
    public static class Person1 {

        @Predicate
        private Date date;

    }

    @Rdf
    public static class Person2 {

        @Predicate
        private ZonedDateTime zonedDateTime;

    }

    @Rdf
    public static class Person3 {

        @Predicate
        private Duration duration;

    }

    @Rdf
    public static class Person4 {

        @Predicate
        private URL url;

    }


    @Test
    public void testConvertXSDDateToDate() throws Exception {
        final ExtendedRdfJavaConverter converter = new ExtendedRdfJavaConverter();
        final RdfProperty rdfProperty = new RdfMappingContext(Utils.defaultPrefixMap(), converter.getCustomTypes())
                .getPersistentEntity(Person1.class).getPersistentProperty("date");
        final Node_Literal node = (Node_Literal) NodeFactory.createLiteral("2017-01-18", XSDDatatype.XSDdate);
        assertThat(converter.canConvert(node, rdfProperty)).isTrue();
        final Date date = (Date) converter.convert(node, rdfProperty);
        assertThat(date).isEqualToIgnoringHours("2017-01-18");
    }

    @Test
    public void testConvertXSDDateToZonedDateTime() throws Exception {
        final ExtendedRdfJavaConverter converter = new ExtendedRdfJavaConverter();
        final RdfProperty rdfProperty = new RdfMappingContext(Utils.defaultPrefixMap(), converter.getCustomTypes())
                .getPersistentEntity(Person2.class).getPersistentProperty("zonedDateTime");
        final Node_Literal node = (Node_Literal) NodeFactory.createLiteral("2017-01-18", XSDDatatype.XSDdate);
        assertThat(converter.canConvert(node, rdfProperty)).isTrue();
        final ZonedDateTime zonedDateTime = (ZonedDateTime) converter.convert(node, rdfProperty);
        assertThat(zonedDateTime.getYear()).isEqualTo(2017);
        assertThat(zonedDateTime.getMonth()).isEqualTo(Month.JANUARY);
        assertThat(zonedDateTime.getDayOfMonth()).isEqualTo(18);
    }

    @Test
    public void testConvertXSDDurationToDuration() throws Exception {
        final ExtendedRdfJavaConverter converter = new ExtendedRdfJavaConverter();
        final RdfProperty rdfProperty = new RdfMappingContext(Utils.defaultPrefixMap(), converter.getCustomTypes())
                .getPersistentEntity(Person3.class).getPersistentProperty("duration");
        final Node_Literal node = (Node_Literal) NodeFactory.createLiteral("PT15M", XSDDatatype.XSDduration);
        assertThat(converter.canConvert(node, rdfProperty)).isTrue();
        final Duration duration = (Duration) converter.convert(node, rdfProperty);
        assertThat(duration).isEqualTo(Duration.ofMinutes(15));
    }

    @Test
    public void testConvertNodeUriToUrl() throws Exception {
        final ExtendedRdfJavaConverter converter = new ExtendedRdfJavaConverter();
        final RdfProperty rdfProperty = new RdfMappingContext(Utils.defaultPrefixMap(), converter.getCustomTypes())
                .getPersistentEntity(Person4.class).getPersistentProperty("url");
        final Node node = NodeFactory.createURI("http://example.org");
        assertThat(converter.canConvert(node, rdfProperty)).isTrue();
        URL url = (URL) converter.convert(node, rdfProperty);
    }

}
