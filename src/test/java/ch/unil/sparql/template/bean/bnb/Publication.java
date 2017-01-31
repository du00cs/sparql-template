package ch.unil.sparql.template.bean.bnb;

import ch.unil.sparql.template.annotation.Predicate;
import ch.unil.sparql.template.annotation.Rdf;

import static ch.unil.sparql.template.TestPrefixes.IFE;
import static ch.unil.sparql.template.TestPrefixes.PLT;

/**
 * @author gushakov
 */
@Rdf
public class Publication {

    @Predicate(PLT)
    private String title;

    @Predicate(value = IFE, localName = "P1053", language = "en")
    private String pages;

    public String getTitle() {
        return title;
    }

    public String getPages() {
        return pages;
    }
}
