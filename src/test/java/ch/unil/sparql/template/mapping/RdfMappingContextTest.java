package ch.unil.sparql.template.mapping;

import ch.unil.sparql.template.Utils;
import ch.unil.sparql.template.annotation.Predicate;
import ch.unil.sparql.template.annotation.PrefixMap;
import ch.unil.sparql.template.annotation.Relation;
import ch.unil.sparql.template.bean.Country;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.jena.shared.PrefixMapping;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static ch.unil.sparql.template.Prefixes.DBP;
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

    public static class Person4 {

        @Predicate(DBP)
        private String birthName;
    }

    public static class Person5 {

    }

    public static class Person6 {

        @Relation
        private Country citizenship;

        public Country getCitizenship() {
            return citizenship;
        }
    }

    public static class Country1 {

    }


    public static class Person7 {

        @Predicate(DBP)
        private Collection<Integer> spouse;

    }

    @Test
    public void testDefaultPrefixMap() throws Exception {
        final RdfMappingContext mappingContext = new RdfMappingContext();
        final RdfEntity<?> entity = mappingContext.getPersistentEntity(Person1.class);
        final PrefixMapping prefixMap = entity.getPrefixMap();
        final Map<String, String> defaultPrefixMap = Utils.defaultPrefixMap().getNsPrefixMap();
        assertThat(prefixMap.getNsPrefixMap()).containsKeys(defaultPrefixMap.keySet().toArray(new String[defaultPrefixMap.size()]));
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
        assertThat(birthNameProperty.getPrefix()).isEqualTo(DBP);
    }

    @Test
    public void testInitializePrefixMap() throws Exception {
        final RdfMappingContext mappingContext = new RdfMappingContext(Utils.defaultPrefixMap()
                .setNsPrefixes(Collections.singletonMap("foo", "http://foobar")));
        final RdfEntity<?> entity = mappingContext.getPersistentEntity(Person5.class);
        assertThat(entity.getPrefixMap().getNsPrefixMap()).contains(new MutablePair<>("foo", "http://foobar"));
    }

    @Test
    public void testRelation() throws Exception {
        final RdfMappingContext mappingContext = new RdfMappingContext();
        final RdfEntity<?> entity = mappingContext.getPersistentEntity(Person6.class);
        final RdfProperty citizenship = entity.getPersistentProperty("citizenship");
        assertThat(citizenship.isAssociation()).isTrue();
        assertThat(citizenship.isEntity()).isTrue();
        assertThat(citizenship.isCollectionLike()).isFalse();
    }

    @Test
    public void testCollectionOfSimpleProperties() throws Exception {
        final RdfMappingContext mappingContext = new RdfMappingContext();
        final RdfEntity<?> entity = mappingContext.getPersistentEntity(Person7.class);
        final RdfProperty years = entity.getPersistentProperty("spouse");
        assertThat(years.isEntity()).isFalse();
        assertThat(years.isAssociation()).isFalse();
        assertThat(years.isCollectionLike()).isTrue();
        assertThat(years.getTypeInformation().getActualType().getType()).isEqualTo(Integer.class);
    }

}
