package ch.unil.sparql.template.bean;

import ch.unil.sparql.template.annotation.Predicate;

import java.util.Collection;

import static ch.unil.sparql.template.Prefixes.DBP;

/**
 * @author gushakov
 */
public class Actor extends Person {

    @Predicate(DBP)
    public Collection<Integer> yearsActive;

    public Collection<Integer> getYearsActive() {
        return yearsActive;
    }

}
