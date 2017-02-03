package ch.unil.sparql.template.bean.bnb;

import ch.unil.sparql.template.annotation.Predicate;
import ch.unil.sparql.template.annotation.Rdf;
import ch.unil.sparql.template.annotation.Relation;

import java.util.Collection;

import static ch.unil.sparql.template.Vocabulary.BLT_NS;
import static ch.unil.sparql.template.Vocabulary.FOAF_NS;

/**
 * @author gushakov
 */
@Rdf
public class Author {

    @Predicate(FOAF_NS)
    private String name;

    @Predicate(value = BLT_NS, localName = "hasContributedTo")
    @Relation
    private Collection<Publication> publications;

    public String getName() {
        return name;
    }

    public Collection<Publication> getPublications() {
        return publications;
    }
}
