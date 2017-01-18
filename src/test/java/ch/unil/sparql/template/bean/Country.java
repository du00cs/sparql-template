package ch.unil.sparql.template.bean;

import ch.unil.sparql.template.annotation.Predicate;

import static ch.unil.sparql.template.Prefixes.DBP;

/**
 * @author gushakov
 */
public class Country {

    @Predicate(DBP)
    private String commonName;

    public String getCommonName() {
        return commonName;
    }
}
