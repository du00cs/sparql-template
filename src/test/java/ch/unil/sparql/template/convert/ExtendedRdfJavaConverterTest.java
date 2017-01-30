package ch.unil.sparql.template.convert;

import ch.unil.sparql.template.annotation.Predicate;
import ch.unil.sparql.template.annotation.Rdf;
import ch.unil.sparql.template.mapping.RdfMappingContext;
import ch.unil.sparql.template.mapping.RdfProperty;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_Literal;
import org.junit.Test;

import java.time.Month;
import java.time.ZonedDateTime;
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

    @Test
    public void testConvertXSDDateToDate() throws Exception {
        // Date is a simple type by default
        final RdfProperty rdfProperty = new RdfMappingContext().getPersistentEntity(Person1.class).getPersistentProperty("date");
        final Node_Literal node = (Node_Literal) NodeFactory.createLiteral("2017-01-18", XSDDatatype.XSDdate);
        final ExtendedRdfJavaConverter converter = new ExtendedRdfJavaConverter();
        assertThat(converter.canConvert(node, rdfProperty)).isTrue();
        final Date date = (Date) converter.convert(node, rdfProperty);
        assertThat(date).isEqualToIgnoringHours("2017-01-18");
    }

    @Test
    public void testConvertXSDDateToZonedDateTime() throws Exception {
        final RdfProperty rdfProperty = new RdfMappingContext(ZonedDateTime.class).getPersistentEntity(Person2.class).getPersistentProperty("zonedDateTime");
        final Node_Literal node = (Node_Literal) NodeFactory.createLiteral("2017-01-18", XSDDatatype.XSDdate);
        final ExtendedRdfJavaConverter converter = new ExtendedRdfJavaConverter();
        assertThat(converter.canConvert(node, rdfProperty)).isTrue();
        final ZonedDateTime zonedDateTime = (ZonedDateTime) converter.convert(node, rdfProperty);
        assertThat(zonedDateTime.getYear()).isEqualTo(2017);
        assertThat(zonedDateTime.getMonth()).isEqualTo(Month.JANUARY);
        assertThat(zonedDateTime.getDayOfMonth()).isEqualTo(18);
    }

}
