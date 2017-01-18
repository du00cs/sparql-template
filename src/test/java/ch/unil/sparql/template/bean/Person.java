package ch.unil.sparql.template.bean;

import ch.unil.sparql.template.annotation.Predicate;
import ch.unil.sparql.template.annotation.Relation;

import java.util.Date;

import static ch.unil.sparql.template.Prefixes.DBO;
import static ch.unil.sparql.template.Prefixes.DBP;


public class Person {

    @Predicate(DBP)
    private String birthName;

    @Predicate(DBP)
    private Date birthDate;

    @Predicate(DBO)
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