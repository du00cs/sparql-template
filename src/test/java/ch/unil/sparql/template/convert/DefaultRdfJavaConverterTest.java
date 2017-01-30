package ch.unil.sparql.template.convert;

import ch.unil.sparql.template.annotation.Predicate;
import ch.unil.sparql.template.annotation.Rdf;
import ch.unil.sparql.template.mapping.RdfMappingContext;
import ch.unil.sparql.template.mapping.RdfProperty;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_Literal;
import org.apache.jena.graph.Node_URI;
import org.junit.Test;

import java.net.URL;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author gushakov
 */
public class DefaultRdfJavaConverterTest {

    @Rdf
    public static class Person {
        @Predicate
        private String label;

        @Predicate
        private Collection<Integer> collectionOfIntegers;

        @Predicate
        private String href;

    }

    @Test
    public void testLabelWithLanguage() throws Exception {
        final RdfProperty rdfProperty = new RdfMappingContext().getPersistentEntity(Person.class).getPersistentProperty("label");
        final Node_Literal node = (Node_Literal) NodeFactory.createLiteral("ça va", "fr");
        final DefaultRdfJavaConverter converter = new DefaultRdfJavaConverter();
        assertThat(converter.canConvert(node, rdfProperty)).isTrue();
        final String label = (String) converter.convert(node, rdfProperty);
        assertThat(label).isEqualTo("ça va");
    }

    @Test
    public void testConvertCollectionOfXSDIntegerToCollectionOfIntegers() throws Exception {
        final RdfProperty rdfProperty = new RdfMappingContext().getPersistentEntity(Person.class).getPersistentProperty("collectionOfIntegers");
        final Node_Literal node = (Node_Literal) NodeFactory.createLiteral("123", XSDDatatype.XSDinteger);
        final DefaultRdfJavaConverter converter = new DefaultRdfJavaConverter();
        assertThat(converter.canConvert(node, rdfProperty)).isTrue();
        final Integer number = (Integer) converter.convert(node, rdfProperty);
        assertThat(number).isEqualTo(123);
    }

    @Test
    public void testConvertNodeUriToString() throws Exception {
        final RdfProperty rdfProperty = new RdfMappingContext().getPersistentEntity(Person.class).getPersistentProperty("href");
        final Node_URI node = (Node_URI) NodeFactory.createURI("http://example.org");
        final DefaultRdfJavaConverter converter = new DefaultRdfJavaConverter();
        assertThat(converter.canConvert(node, rdfProperty)).isTrue();
        final String uri = (String) converter.convert(node, rdfProperty);
        assertThat(uri).isEqualTo("http://example.org");
    }

}
