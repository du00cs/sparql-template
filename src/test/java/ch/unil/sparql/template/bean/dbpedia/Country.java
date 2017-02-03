package ch.unil.sparql.template.bean.dbpedia;

import ch.unil.sparql.template.annotation.Predicate;
import ch.unil.sparql.template.annotation.Rdf;

import static ch.unil.sparql.template.Vocabulary.DBP_NS;

/**
 * @author gushakov
 */
@Rdf
public class Country {

    @Predicate(DBP_NS)
    private String commonName;

    public String getCommonName() {
        return commonName;
    }

}
