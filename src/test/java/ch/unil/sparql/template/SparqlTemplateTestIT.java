package ch.unil.sparql.template;

import ch.unil.sparql.template.bean.Person;
import org.junit.Test;

import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author gushakov
 */
public class SparqlTemplateTestIT {

    @Test
    public void testLoad() throws Exception {
        final SparqlTemplate sparqlTemplate = new SparqlTemplate("https://dbpedia.org/sparql", Utils.dbpediaPrefixMap());
        final Person person = sparqlTemplate.load("dbr:Angelina_Jolie", Person.class);
        assertThat(person.getBirthName()).isEqualTo("Angelina Jolie Voight");
        assertThat(person.getBirthDate()).hasYear(1975).hasMonth(Calendar.JUNE + 1).hasDayOfMonth(4);
        assertThat(person.getCitizenship().getCommonName()).isEqualTo("Cambodia");
    }

}
