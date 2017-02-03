package ch.unil.sparql.template.query;

import org.apache.jena.graph.Triple;
import org.junit.Test;

import java.util.Collection;

import static ch.unil.sparql.template.Vocabulary.DBR_NS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * @author gushakov
 */
public class SparqlQueryServiceTestIT {

    @Test
    public void testQueryDBPedia() throws Exception {
        final SparqlQueryService sparqlQueryService = new SparqlQueryService("https://dbpedia.org/sparql", true);
        try {
            final Collection<Triple> triples = sparqlQueryService.query(DBR_NS + "Angelina_Jolie");
            assertThat(triples)
                    .extracting("subject.localName", "predicate.localName").contains(tuple("Angelina_Jolie", "birthDate"))
            ;
        } finally {
            sparqlQueryService.shutdown();
        }


    }

}
