package ch.unil.sparql.template.bean;

import ch.unil.sparql.template.annotation.Predicate;
import ch.unil.sparql.template.annotation.Rdf;
import ch.unil.sparql.template.annotation.Relation;

import java.time.ZonedDateTime;
import java.util.Collection;

import static ch.unil.sparql.template.Prefixes.DBO;
import static ch.unil.sparql.template.Prefixes.DBP;


@Rdf
public class Person {

    @Predicate(DBP)
    private String birthName;

    @Predicate(DBP)
    private ZonedDateTime birthDate;

    @Predicate(DBP)
    private Collection<Integer> spouse;

    @Predicate(DBO)
    @Relation
    private Country citizenship;

    public String getBirthName() {
        return birthName;
    }

    public ZonedDateTime getBirthDate() {
        return birthDate;
    }

    public Collection<Integer> getSpouse() {
        return spouse;
    }

    public Country getCitizenship() {
        return citizenship;
    }
}