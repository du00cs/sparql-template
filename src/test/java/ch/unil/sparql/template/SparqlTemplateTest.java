package ch.unil.sparql.template;

import ch.unil.sparql.template.bean.Actor;
import ch.unil.sparql.template.bean.Country;
import ch.unil.sparql.template.bean.Person;
import ch.unil.sparql.template.query.SparqlQueryService;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Month;
import java.util.Arrays;
import java.util.function.Consumer;

import static ch.unil.sparql.template.Prefixes.*;
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
        final String personIri = DBR_NS + "Angelina_Jolie";
        final String countryIri = DBR_NS + "Cambodia";
        when(mockQueryService.query(endsWith("Angelina_Jolie"), any()))
                .thenReturn(Arrays.asList(
                        triple(personIri, RDFS_NS +"label", "Angelina Jolie", "en"),
                        triple(personIri, RDFS_NS +"label", "Angelina Jolie", "fr"),
                        triple(personIri, RDFS_NS +"label", "Джоли, Анджелина", "ru"),
                        triple(personIri, DBP_NS + "birthDate", "1975-06-04", XSDDatatype.XSDdate),
                        triple(personIri, DBP_NS + "birthName", "Angelina Jolie Voight", "en"),
                        triple(personIri, DBO_NS + "citizenship", countryIri),
                        triple(personIri, DBP_NS + "spouse", "1996", XSDDatatype.XSDinteger),
                        triple(personIri, DBP_NS + "spouse", "1999", XSDDatatype.XSDinteger),
                        triple(personIri, DBP_NS + "spouse", "2000", XSDDatatype.XSDinteger),
                        triple(personIri, DBP_NS + "spouse", "2003", XSDDatatype.XSDinteger),
                        triple(personIri, DBP_NS + "spouse", "2014", XSDDatatype.XSDinteger),
                        triple(personIri, DBP_NS + "spouse", "div.", "en"),
                        triple(personIri, DBP_NS + "spouse", DBR_NS + "Billy_Bob_Thornton"),
                        triple(personIri, DBP_NS + "spouse", DBR_NS + "Jonny_Lee_Miller"),
                        triple(personIri, DBP_NS + "spouse", DBR_NS + "Brad_Pitt")
                ));

        // :Cambodia
        when(mockQueryService.query(endsWith("Cambodia"), any()))
                .thenReturn(Arrays.asList(
                        triple(countryIri, DBP_NS + "commonName", "Cambodia", "en")
                ));

        final SparqlTemplate sparqlTemplate = new SparqlTemplate(mockQueryService);
        final Person person = sparqlTemplate.load(DBR + ":Angelina_Jolie", Person.class);
        assertThat(person instanceof DynamicBeanProxy).isTrue();
        assertThat(person.getBirthName()).isEqualTo("Angelina Jolie Voight");
        assertThat(person.getBirthDate().getYear()).isEqualTo(1975);
        assertThat(person.getBirthDate().getMonth()).isEqualTo(Month.JUNE);
        assertThat(person.getBirthDate().getDayOfMonth()).isEqualTo(4);
        final Country citizenship = person.getCitizenship();
        assertThat(citizenship instanceof DynamicBeanProxy).isTrue();
        assertThat(citizenship.getCommonName()).isEqualTo("Cambodia");
        assertThat(person.getYearsMarried()).containsOnly(1996, 1999, 2000, 2003, 2014);
        assertThat(person.getSpouse().size()).isGreaterThanOrEqualTo(3);
        assertThat(person.getLabel()).isEqualTo("Джоли, Анджелина");
        assertThat(person.getAllLabels()).hasSize(3).containsOnly("Angelina Jolie", "Джоли, Анджелина");
    }

}
