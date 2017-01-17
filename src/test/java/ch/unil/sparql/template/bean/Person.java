package ch.unil.sparql.template.bean;

import ch.unil.sparql.template.annotation.Predicate;
import ch.unil.sparql.template.annotation.Relation;

import java.util.Date;


public class Person {

    @Predicate("dbp")
    private String birthName;

    @Predicate("dbp")
    private Date birthDate;

    @Predicate("dbo")
    @Relation
    private Country citizenship;

    public String getBirthName() {
        return birthName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public Country getCitizenship() {
        return citizenship;
    }
}