package ch.unil.sparql.template.convert;

import org.apache.jena.datatypes.xsd.impl.XSDBaseStringType;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Node_Literal;
import org.junit.Test;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author gushakov
 */
public class ConversionServiceTest {

    @Test
    public void testConvertRdfToJava() throws Exception {
        final DefaultConversionService conversionService = new DefaultConversionService();

        conversionService.addConverter(new Converter<Node_Literal, Object>() {
            @Override
            public Object convert(Node_Literal node) {
                return node.getLiteralValue();
            }
        });

        assertThat(conversionService.canConvert(Node_Literal.class, Object.class)).isTrue();

        assertThat(conversionService.convert(NodeFactory.createLiteral("foobar", XSDBaseStringType.XSDstring), Object.class))
                .isEqualTo("foobar");
        assertThat(conversionService.convert(NodeFactory.createLiteral("123", XSDBaseStringType.XSDint), Object.class))
                .isEqualTo(new Integer(123));
        assertThat(conversionService.convert(NodeFactory.createLiteral("ça va", "fr"), Object.class))
                .isEqualTo("ça va");
    }

}
