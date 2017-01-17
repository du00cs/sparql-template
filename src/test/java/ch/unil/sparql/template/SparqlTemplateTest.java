package ch.unil.sparql.template;

import ch.unil.sparql.template.bean.Person;
import ch.unil.sparql.template.query.SparqlQueryService;
import org.apache.commons.collections4.MapUtils;
import org.apache.jena.datatypes.xsd.impl.XSDDateType;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author gushakov
 */
public class SparqlTemplateTest {

    @Test
    public void testLoad() throws Exception {

        final SparqlQueryService mockQueryService = Mockito.mock(SparqlQueryService.class);
        when(mockQueryService.query(anyString(), any()))
                .thenReturn(Arrays.asList(
                        Triple.create(NodeFactory.createURI("http://dbpedia.org/resource/Angelina_Jolie"),
                                NodeFactory.createURI("http://dbpedia.org/property/birthDate"),
                                NodeFactory.createLiteral("1975-06-04", XSDDateType.XSDdate)),
                        Triple.create(NodeFactory.createURI("http://dbpedia.org/resource/Angelina_Jolie"),
                                NodeFactory.createURI("http://dbpedia.org/property/birthName"),
                                NodeFactory.createLiteral("Angelina Jolie Voight", "en"))
                ));

        final SparqlTemplate sparqlTemplate = new SparqlTemplate(mockQueryService, Utils.dbpediaPrefixMap());
        final Person person = sparqlTemplate.load("dbr:Angelina_Jolie", Person.class);
        assertThat(person.getBirthName()).isEqualTo("Angelina Jolie Voight");
        assertThat(person.getBirthDate()).hasYear(1975).hasMonth(Calendar.JUNE + 1).hasDayOfMonth(4);
    }

}
