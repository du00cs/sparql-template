package ch.unil.sparql.template;

import ch.unil.sparql.template.bean.Country;
import ch.unil.sparql.template.bean.Person;
import ch.unil.sparql.template.query.SparqlQueryService;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Calendar;

import static ch.unil.sparql.template.Prefixes.DBO_NS;
import static ch.unil.sparql.template.Prefixes.DBP_NS;
import static ch.unil.sparql.template.Prefixes.DBR;
import static ch.unil.sparql.template.Prefixes.DBR_NS;
import static ch.unil.sparql.template.Utils.triple;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.endsWith;
import static org.mockito.Mockito.when;

/**
 * @author gushakov
 */
public class SparqlTemplateTest {

    @Test
    public void testLoad() throws Exception {
        final SparqlQueryService mockQueryService = Mockito.mock(SparqlQueryService.class);

        // :Angelina_Jolie
        when(mockQueryService.query(endsWith("Angelina_Jolie"), any()))
                .thenReturn(Arrays.asList(
                        triple(DBR_NS + "Angelina_Jolie", DBP_NS + "birthDate", "1975-06-04", XSDDatatype.XSDdate),
                        triple(DBR_NS + "Angelina_Jolie", DBP_NS + "birthName", "Angelina Jolie Voight", "en"),
                        triple(DBR_NS + "Angelina_Jolie", DBO_NS + "citizenship", DBR_NS + "Cambodia")
                ));

        // :Cambodia
        when(mockQueryService.query(endsWith("Cambodia"), any()))
                .thenReturn(Arrays.asList(
                        triple(DBR_NS + "Cambodia", DBP_NS + "commonName", "Cambodia", "en")
                ));

        final SparqlTemplate sparqlTemplate = new SparqlTemplate(mockQueryService);
        final Person person = sparqlTemplate.load(DBR + ":Angelina_Jolie", Person.class);
        assertThat(person instanceof DynamicBeanProxy).isTrue();
        assertThat(person.getBirthName()).isEqualTo("Angelina Jolie Voight");
        assertThat(person.getBirthDate()).hasYear(1975).hasMonth(Calendar.JUNE + 1).hasDayOfMonth(4);
        final Country citizenship = person.getCitizenship();
        assertThat(citizenship instanceof DynamicBeanProxy).isTrue();
        assertThat(citizenship.getCommonName()).isEqualTo("Cambodia");

    }

}
