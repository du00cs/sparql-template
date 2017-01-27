package ch.unil.sparql.template;

import ch.unil.sparql.template.bean.Actor;
import ch.unil.sparql.template.bean.Film;
import ch.unil.sparql.template.bean.Person;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.Collection;
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
        assertThat(person.getYearsMarried()).contains(1996, 1999, 2000, 2003, 2014);
    }

    @Test
    public void testSubtype() throws Exception {
        final SparqlTemplate sparqlTemplate = new SparqlTemplate("https://dbpedia.org/sparql");
        final Actor actor = sparqlTemplate.load(DBR + ":Brad_Pitt", Actor.class);
        assertThat(actor.getYearsActive()).contains(1987);
        assertThat(actor.getBirthName()).contains("Bradley", "Pitt");

    }

    @Test
    public void testCollection() throws Exception {
        final SparqlTemplate sparqlTemplate = new SparqlTemplate("https://dbpedia.org/sparql");
        final Film film = sparqlTemplate.load(DBR + ":The_Usual_Suspects", Film.class);
        film.getStarring().forEach(a -> System.out.println(a.getBirthName()));

    }


}
