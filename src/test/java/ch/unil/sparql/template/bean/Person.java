package ch.unil.sparql.template.bean;

import ch.unil.sparql.template.annotation.Predicate;

import java.util.Date;

public class Person {

    @Predicate("dbp")
    private String birthName;

    @Predicate("dbp")
    private Date birthDate;

    public String getBirthName() {
        return birthName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

}