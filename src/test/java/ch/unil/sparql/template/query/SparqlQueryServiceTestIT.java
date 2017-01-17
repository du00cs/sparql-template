package ch.unil.sparql.template.query;

import org.apache.commons.collections4.MapUtils;
import org.apache.jena.graph.Triple;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.shared.impl.PrefixMappingImpl;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * @author gushakov
 */
public class SparqlQueryServiceTestIT {

    @Test
    public void testQueryDBPedia() throws Exception {
        final PrefixMapping prefixMap = new PrefixMappingImpl();
        prefixMap.setNsPrefixes(MapUtils.putAll(new HashMap<>(), new String[]{
                "dbr", "http://dbpedia.org/resource/",
                "dbp", "http://dbpedia.org/property/birthDate"
        }));

        final SparqlQueryService sparqlQueryService = new SparqlQueryService("https://dbpedia.org/sparql", true);
        try {
            final Collection<Triple> triples = sparqlQueryService.query("dbr:Angelina_Jolie", prefixMap);
            assertThat(triples)
                    .extracting("subject.localName", "predicate.localName").contains(tuple("Angelina_Jolie", "birthDate"))
            ;
        } finally {
            sparqlQueryService.shutdown();
        }


    }

}
