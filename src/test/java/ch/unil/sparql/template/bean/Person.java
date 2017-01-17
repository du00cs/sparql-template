package ch.unil.sparql.template.bean;

import ch.unil.sparql.template.annotation.Predicate;
import ch.unil.sparql.template.annotation.PrefixMap;

import java.util.Date;

@PrefixMap({"dbr", "http://dbpedia.org/resource/",
        "dbp", "http://dbpedia.org/property/"})
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