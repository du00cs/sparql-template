package ch.unil.sparql.template;

// Description of the resources is available http://www.bl.uk/bibliographic/download.html, see sample files.

import ch.unil.sparql.template.bean.bnb.Author;
import ch.unil.sparql.template.bean.bnb.Publication;
import org.junit.Test;

import static ch.unil.sparql.template.Vocabulary.BNP_NS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests using <a href="British National Bibliography">http://www.bl.uk/bibliographic/datafree.html</a>
 * public dataset.
 *
 * @author gushakov
 */
public class SparqlTemplateBNBCatalogueTestIT {

    @Test
    public void testLoad() throws Exception {

        final SparqlTemplate sparqlTemplate = new SparqlTemplate("http://bnb.data.bl.uk/sparql");

        final Author author = sparqlTemplate.load(BNP_NS + "WestClare", Author.class);
        assertThat(author.getName()).isNotEmpty();
        final Publication publication = author.getPublications().stream().findAny().get();
        assertThat(publication.getTitle()).isNotEmpty();
        assertThat(publication.getPages()).isNotEmpty();

    }

}
