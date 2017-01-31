package ch.unil.sparql.template.bean.bnb;

import ch.unil.sparql.template.annotation.Predicate;
import ch.unil.sparql.template.annotation.Rdf;
import ch.unil.sparql.template.annotation.Relation;

import java.util.Collection;

import static ch.unil.sparql.template.Prefixes.FOAF;
import static ch.unil.sparql.template.TestPrefixes.BLT;

/**
 * @author gushakov
 */
@Rdf
public class Author {

    @Predicate(FOAF)
    private String name;

    @Predicate(value = BLT, localName = "hasContributedTo")
    @Relation
    private Collection<Publication> publications;

    public String getName() {
        return name;
    }

    public Collection<Publication> getPublications() {
        return publications;
    }
}
