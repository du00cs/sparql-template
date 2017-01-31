package ch.unil.sparql.template.bean.example;

import ch.unil.sparql.template.annotation.Predicate;
import ch.unil.sparql.template.annotation.Rdf;

import static ch.unil.sparql.template.TestPrefixes.EXP;

/**
 * @author gushakov
 */
@Rdf
public class Resource {

    @Predicate(EXP)
    private String name;

    public String getName() {
        return name;
    }
}
