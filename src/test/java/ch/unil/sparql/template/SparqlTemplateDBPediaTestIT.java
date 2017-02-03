package ch.unil.sparql.template;

import ch.unil.sparql.template.bean.dbpedia.Actor;
import ch.unil.sparql.template.bean.dbpedia.Country;
import ch.unil.sparql.template.bean.dbpedia.Film;
import ch.unil.sparql.template.bean.dbpedia.Person;
import org.junit.Test;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.stream.Collectors;

import static ch.unil.sparql.template.Vocabulary.DBR_NS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author gushakov
 */
public class SparqlTemplateDBPediaTestIT {

    @Test
    public void testReadMe() throws Exception {

        // get the default SPARQL template
        final SparqlTemplate sparqlTemplate = new SparqlTemplate("https://dbpedia.org/sparql");

        // load information about Angelina Jolie
        final Person person = sparqlTemplate.load(DBR_NS + "Angelina_Jolie", Person.class);

        System.out.println(person.getBirthName());
        // Angelina Jolie Voight

        System.out.println(person.getLabel());
        // Джоли, Анджелина

        System.out.println(person.getBirthDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy (EEE)", Locale.ENGLISH)));
        // 04/06/1975 (Wed)

        System.out.println(person.getSpouse().stream()
                .filter(p -> p.getBirthName() != null && p.getBirthName().contains("Pitt"))
                .findAny().get().getBirthName());
        // William Bradley Pitt
    }

    @Test
    public void testLoadPerson() throws Exception {
        final SparqlTemplate sparqlTemplate = new SparqlTemplate("https://dbpedia.org/sparql");
        final Person person = sparqlTemplate.load(DBR_NS + "Angelina_Jolie", Person.class);
        assertThat(person.getBirthName()).isEqualTo("Angelina Jolie Voight");
        assertThat(person.getBirthDate().getYear()).isEqualTo(1975);
        assertThat(person.getCitizenship().getCommonName()).isEqualTo("Cambodia");
        assertThat(person.getYearsMarried()).contains(1996, 1999, 2000, 2003, 2014);
        assertThat(person.getHomepage()).isEqualTo("http://www.unhcr.org/pages/49c3646c56.html");
        assertThat(person.getSpouse()).extracting("birthName").contains("William Bradley Pitt");
        assertThat(person.getSameAsUrl().stream().map(URL::getHost).collect(Collectors.toSet()))
                .contains("data.europa.eu", "rdf.freebase.com", "www.wikidata.org");
    }

    @Test
    public void testLoadCountry() throws Exception {
        final SparqlTemplate sparqlTemplate = new SparqlTemplate("https://dbpedia.org/sparql");
        final Country country = sparqlTemplate.load(DBR_NS + "Cambodia", Country.class);
        System.out.println(country.getCommonName());
    }

    @Test
    public void testSubtype() throws Exception {
        final SparqlTemplate sparqlTemplate = new SparqlTemplate("https://dbpedia.org/sparql");
        final Actor actor = sparqlTemplate.load(DBR_NS + "Brad_Pitt", Actor.class);
        assertThat(actor.getYearsActive()).contains(1987);
        assertThat(actor.getBirthName()).contains("Bradley", "Pitt");
    }

    @Test
    public void testCollection() throws Exception {
        final SparqlTemplate sparqlTemplate = new SparqlTemplate("https://dbpedia.org/sparql");
        final Film film = sparqlTemplate.load(DBR_NS + "The_Usual_Suspects", Film.class);
        film.getStarring().forEach(a -> System.out.println(a.getBirthName()));
    }

}
