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
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author gushakov
 */
public class RdfJavaConverterTest {

    @Rdf
    public static class Person {
        @Predicate
        private String label;

        @Predicate
        private ZonedDateTime zonedDateTime;

        @Predicate
        private Collection<Integer> collectionOfIntegers;
    }

    @Test
    public void testLabelWithLanguage() throws Exception {
        final RdfProperty rdfProperty = new RdfMappingContext().getPersistentEntity(Person.class).getPersistentProperty("label");
        final Node_Literal node = (Node_Literal) NodeFactory.createLiteral("ça va", "fr");
        final RdfJavaConverter converter = new RdfJavaConverter();
        assertThat(converter.canConvert(node, rdfProperty)).isTrue();
        final String label = (String) converter.convert(node, rdfProperty);
        assertThat(label).isEqualTo("ça va");
    }

    @Test
    public void testConvertXSDDateToZonedDateTime() throws Exception {
        final RdfProperty rdfProperty = new RdfMappingContext().getPersistentEntity(Person.class).getPersistentProperty("zonedDateTime");
        final Node_Literal node = (Node_Literal) NodeFactory.createLiteral("2017-01-18", XSDDatatype.XSDdate);
        final RdfJavaConverter converter = new RdfJavaConverter();
        assertThat(converter.canConvert(node, rdfProperty)).isTrue();
        final ZonedDateTime zonedDateTime = (ZonedDateTime) converter.convert(node, rdfProperty);
        assertThat(zonedDateTime.getYear()).isEqualTo(2017);
        assertThat(zonedDateTime.getMonth()).isEqualTo(Month.JANUARY);
        assertThat(zonedDateTime.getDayOfMonth()).isEqualTo(18);
    }

    @Test
    public void testConvertCollectionOfXSDIntegerToCollectionOfIntegers() throws Exception {
        final RdfProperty rdfProperty = new RdfMappingContext().getPersistentEntity(Person.class).getPersistentProperty("collectionOfIntegers");
        final Node_Literal node = (Node_Literal) NodeFactory.createLiteral("123", XSDDatatype.XSDinteger);
        final RdfJavaConverter converter = new RdfJavaConverter();
        assertThat(converter.canConvert(node, rdfProperty)).isTrue();
        final Integer number = (Integer) converter.convert(node, rdfProperty);
        assertThat(number).isEqualTo(123);
    }
}
