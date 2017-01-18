package ch.unil.sparql.template;

import ch.unil.sparql.template.bean.Person;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.Collections;

import static ch.unil.sparql.template.Prefixes.DBR;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author gushakov
 */
public class SparqlTemplateTestIT {

    @Test
    public void testLoad() throws Exception {
        final SparqlTemplate sparqlTemplate = new SparqlTemplate("https://dbpedia.org/sparql");
        final Person person = sparqlTemplate.load(DBR + ":Angelina_Jolie", Person.class);
        assertThat(person.getBirthName()).isEqualTo("Angelina Jolie Voight");
        assertThat(person.getBirthDate().getYear()).isEqualTo(1975);
        assertThat(person.getCitizenship().getCommonName()).isEqualTo("Cambodia");
        assertThat(person.getSpouse()).containsOnly(1996, 1999, 2000, 2003, 2014);
    }

}
