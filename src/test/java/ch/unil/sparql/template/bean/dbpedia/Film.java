package ch.unil.sparql.template.bean.dbpedia;

import ch.unil.sparql.template.annotation.Predicate;
import ch.unil.sparql.template.annotation.Rdf;
import ch.unil.sparql.template.annotation.Relation;

import java.util.Collection;

import static ch.unil.sparql.template.Vocabulary.DBP_NS;
import static ch.unil.sparql.template.Vocabulary.DBR_NS;

/**
 * @author gushakov
 */
@Rdf
public class Film {

    @Predicate(DBR_NS)
    private String name;

    @Predicate(DBP_NS)
    @Relation
    private Collection<Actor> starring;

    public String getName() {
        return name;
    }

    public Collection<Actor> getStarring() {
        return starring;
    }
}
