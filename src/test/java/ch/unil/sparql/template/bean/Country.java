package ch.unil.sparql.template.bean;

import ch.unil.sparql.template.annotation.Predicate;

/**
 * @author gushakov
 */
public class Country {

    @Predicate("dbp")
    private String commonName;

    public String getCommonName() {
        return commonName;
    }
}
