package ch.unil.sparql.template.bean.dbpedia;

import ch.unil.sparql.template.annotation.Predicate;
import ch.unil.sparql.template.annotation.Rdf;
import ch.unil.sparql.template.annotation.Relation;

import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Collection;

import static ch.unil.sparql.template.Vocabulary.DBO_NS;
import static ch.unil.sparql.template.Vocabulary.DBP_NS;
import static ch.unil.sparql.template.Vocabulary.FOAF_NS;
import static ch.unil.sparql.template.Vocabulary.OWL_NS;
import static ch.unil.sparql.template.Vocabulary.RDFS_NS;


@Rdf
public class Person {

    @Predicate(value = RDFS_NS, language = "ru")
    private String label;

    @Predicate(value = RDFS_NS, localName = "label")
    private Collection<String> allLabels;

    @Predicate(OWL_NS)
    private Collection<String> sameAs;

    @Predicate(value = OWL_NS, localName = "sameAs")
    private Collection<URL> sameAsUrl;

    @Predicate(DBP_NS)
    private String birthName;

    @Predicate(DBP_NS)
    private ZonedDateTime birthDate;

    @Predicate(value = DBP_NS, localName = "spouse")
    private Collection<Integer> yearsMarried;

    @Predicate(DBO_NS)
    @Relation
    private Country citizenship;

    @Predicate(DBP_NS)
    @Relation
    private Collection<Person> spouse;

    @Predicate(FOAF_NS)
    private String homepage;

    public String getLabel() {
        return label;
    }

    public Collection<String> getAllLabels() {
        return allLabels;
    }

    public Collection<String> getSameAs() {
        return sameAs;
    }

    public Collection<URL> getSameAsUrl() {
        return sameAsUrl;
    }

    public String getBirthName() {
        return birthName;
    }

    public ZonedDateTime getBirthDate() {
        return birthDate;
    }

    public Collection<Integer> getYearsMarried() {
        return yearsMarried;
    }

    public Country getCitizenship() {
        return citizenship;
    }

    public Collection<Person> getSpouse() {
        return spouse;
    }

    public String getHomepage() {
        return homepage;
    }
}