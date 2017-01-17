package ch.unil.sparql.template.mapping;

import ch.unil.sparql.template.Utils;
import ch.unil.sparql.template.annotation.Predicate;
import ch.unil.sparql.template.annotation.PrefixMap;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.jena.shared.PrefixMapping;
import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author gushakov
 */
public class RdfMappingContextTest {

    public static class Person1 {
    }

    @PrefixMap({"foo", "http://foobar/", "wam"})
    public static class Person2 {
    }

    @PrefixMap({"foo"})
    public static class Person3 {
    }

    @PrefixMap({"dbp", "http://dbpedia.org/property/"})
    public static class Person4 {

        @Predicate("dbp")
        private String birthName;
    }

    public static class Person5 {

    }

    @Test
    public void testDefaultPrefixMap() throws Exception {
        final RdfMappingContext mappingContext = new RdfMappingContext();
        final RdfEntity<?> entity = mappingContext.getPersistentEntity(Person1.class);
        final PrefixMapping prefixMap = entity.getPrefixMap();
        assertThat(prefixMap.getNsPrefixMap()).containsKeys("rdf", "rdfs", "owl");
    }

    @Test
    public void testCustomPrefixMap() throws Exception {
        final RdfMappingContext mappingContext = new RdfMappingContext();
        final RdfEntity<?> entity = mappingContext.getPersistentEntity(Person2.class);
        final PrefixMapping prefixMap = entity.getPrefixMap();
        assertThat(prefixMap.getNsPrefixMap())
                .containsKeys("foo")
                .containsValues("http://foobar/")
        ;
    }

    @Test
    public void testInvalidPrefixMap() throws Exception {
        final RdfMappingContext mappingContext = new RdfMappingContext();
        final RdfEntity<?> entity = mappingContext.getPersistentEntity(Person3.class);
        final PrefixMapping prefixMap = entity.getPrefixMap();
        assertThat(prefixMap.getNsPrefixMap()).doesNotContainKeys("foo");
    }

    @Test
    public void testPredicatePrefix() throws Exception {
        final RdfMappingContext mappingContext = new RdfMappingContext();
        final RdfEntity<?> entity = mappingContext.getPersistentEntity(Person4.class);
        final RdfProperty birthNameProperty = entity.getPersistentProperty("birthName");
        assertThat(birthNameProperty).isNotNull();
        assertThat(birthNameProperty.getPrefix()).isEqualTo("dbp");
    }

    @Test
    public void testInitializePrefixMap() throws Exception {
        final RdfMappingContext mappingContext = new RdfMappingContext(Utils.defaultPrefixMap()
                .setNsPrefixes(Collections.singletonMap("foo", "http://foobar")));
        final RdfEntity<?> entity = mappingContext.getPersistentEntity(Person5.class);
        assertThat(entity.getPrefixMap().getNsPrefixMap()).contains(new MutablePair<>("foo", "http://foobar"));
    }

}
