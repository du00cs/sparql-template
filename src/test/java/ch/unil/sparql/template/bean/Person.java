package ch.unil.sparql.template.bean;

import ch.unil.sparql.template.annotation.Predicate;
import ch.unil.sparql.template.annotation.Rdf;
import ch.unil.sparql.template.annotation.Relation;

import java.time.ZonedDateTime;
import java.util.Collection;

import static ch.unil.sparql.template.Prefixes.*;


@Rdf
public class Person {

    @Predicate(value = RDFS, language = "ru")
    private String label;

    @Predicate(value = RDFS, localName = "label")
    private Collection<String> allLabels;

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

    public String getLabel() {
        return label;
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

    public Collection<String> getAllLabels() {
        return allLabels;
    }
}