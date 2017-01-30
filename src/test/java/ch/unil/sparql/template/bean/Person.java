package ch.unil.sparql.template.bean;

import ch.unil.sparql.template.annotation.Predicate;
import ch.unil.sparql.template.annotation.Rdf;
import ch.unil.sparql.template.annotation.Relation;

import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Collection;

import static ch.unil.sparql.template.Prefixes.*;


@Rdf
public class Person {

    @Predicate(value = RDFS, language = "ru")
    private String label;

    @Predicate(value = RDFS, localName = "label")
    private Collection<String> allLabels;

    @Predicate(OWL)
    private Collection<String> sameAs;

    @Predicate(value = OWL, localName = "sameAs")
    private Collection<URL> sameAsUrl;

    @Predicate(DBP)
    private String birthName;

    @Predicate(DBP)
    private ZonedDateTime birthDate;

    @Predicate(value = DBP, localName = "spouse")
    private Collection<Integer> yearsMarried;

    @Predicate(DBO)
    @Relation
    private Country citizenship;

    @Predicate(DBP)
    @Relation
    private Collection<Person> spouse;

    @Predicate(FOAF)
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