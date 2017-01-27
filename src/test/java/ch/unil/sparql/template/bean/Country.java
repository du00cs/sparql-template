package ch.unil.sparql.template.bean;

import ch.unil.sparql.template.annotation.Predicate;
import ch.unil.sparql.template.annotation.Rdf;
import ch.unil.sparql.template.annotation.Relation;

import static ch.unil.sparql.template.Prefixes.DBP;

/**
 * @author gushakov
 */
@Rdf
public class Country {

    @Predicate(DBP)
    private String commonName;

    @Predicate
    @Relation(virtual = true)
    private Person distinguishedCitizen;

    public String getCommonName() {
        return commonName;
    }

    public Person getDistinguishedCitizen() {
        return distinguishedCitizen;
    }
}
