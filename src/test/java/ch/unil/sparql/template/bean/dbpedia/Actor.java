package ch.unil.sparql.template.bean.dbpedia;

import ch.unil.sparql.template.Vocabulary;
import ch.unil.sparql.template.annotation.Predicate;

import java.util.Collection;

import static ch.unil.sparql.template.Vocabulary.DBP_NS;


/**
 * @author gushakov
 */
public class Actor extends Person {

    @Predicate(DBP_NS)
    public Collection<Integer> yearsActive;

    public Collection<Integer> getYearsActive() {
        return yearsActive;
    }

}
