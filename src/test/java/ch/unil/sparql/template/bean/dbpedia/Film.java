package ch.unil.sparql.template.bean.dbpedia;

import ch.unil.sparql.template.Prefixes;
import ch.unil.sparql.template.annotation.Predicate;
import ch.unil.sparql.template.annotation.Rdf;
import ch.unil.sparql.template.annotation.Relation;

import java.util.Collection;

/**
 * @author gushakov
 */
@Rdf
public class Film {

    @Predicate(Prefixes.DBR)
    private String name;

    @Predicate(Prefixes.DBP)
    @Relation
    private Collection<Actor> starring;

    public String getName() {
        return name;
    }

    public Collection<Actor> getStarring() {
        return starring;
    }
}
