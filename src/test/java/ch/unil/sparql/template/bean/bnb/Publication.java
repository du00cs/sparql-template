package ch.unil.sparql.template.bean.bnb;

import ch.unil.sparql.template.annotation.Predicate;
import ch.unil.sparql.template.annotation.Rdf;

import static ch.unil.sparql.template.Vocabulary.IFE_NS;
import static ch.unil.sparql.template.Vocabulary.PLT_NS;

/**
 * @author gushakov
 */
@Rdf
public class Publication {

    @Predicate(PLT_NS)
    private String title;

    @Predicate(value = IFE_NS, localName = "P1053", language = "en")
    private String pages;

    public String getTitle() {
        return title;
    }

    public String getPages() {
        return pages;
    }
}
