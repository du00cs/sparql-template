package ch.unil.sparql.template.mapping;

import ch.unil.sparql.template.annotation.Predicate;
import ch.unil.sparql.template.annotation.Rdf;
import ch.unil.sparql.template.annotation.Relation;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

import static ch.unil.sparql.template.Vocabulary.DBP_NS;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author gushakov
 */
public class RdfMappingContextTest {

    @Rdf
    private static class Person1 {
    }

    @Rdf
    private static class Person4 {

        @Predicate(DBP_NS)
        private String birthName;
    }

    @Rdf
    private static class Person5 {

    }

    @Rdf
    public static class Person6 {

        @Predicate
        @Relation
        private Country1 citizenship;

        public Country1 getCitizenship() {
            return citizenship;
        }
    }

    @Rdf
    private static class Country1 {

    }

    @Rdf
    private static class Person7 {

        @Predicate(DBP_NS)
        private Collection<Integer> spouse;

    }

    private static class Person8 {

        private LocalDateTime localDateTime;
    }

    @Rdf
    private static class Person9 {

        @Predicate
        private ZonedDateTime zonedDateTime;

        // transient property
        private DateTimeFormatter formatter;
    }

    @Test
    public void testPredicateNamespace() throws Exception {
        final RdfMappingContext mappingContext = new RdfMappingContext();
        final RdfEntity entity = mappingContext.getPersistentEntity(Person4.class);
        final RdfProperty birthNameProperty = (RdfProperty) entity.getPersistentProperty("birthName");
        assertThat(birthNameProperty).isNotNull();
        assertThat(birthNameProperty.getNamespace()).isEqualTo(DBP_NS);
    }

    @Test
    public void testRelation() throws Exception {
        final RdfMappingContext mappingContext = new RdfMappingContext();
        final RdfEntity entity = mappingContext.getPersistentEntity(Person6.class);
        final RdfProperty citizenship = (RdfProperty) entity.getPersistentProperty("citizenship");
        assertThat(citizenship.isAssociation()).isTrue();
        assertThat(citizenship.isEntity()).isTrue();
        assertThat(citizenship.isCollectionLike()).isFalse();
    }

    @Test
    public void testCollectionOfSimpleProperties() throws Exception {
        final RdfMappingContext mappingContext = new RdfMappingContext();
        final RdfEntity entity = mappingContext.getPersistentEntity(Person7.class);
        final RdfProperty years = (RdfProperty) entity.getPersistentProperty("spouse");
        assertThat(years.isEntity()).isFalse();
        assertThat(years.isAssociation()).isFalse();
        assertThat(years.isCollectionLike()).isTrue();
        assertThat(years.getTypeInformation().getActualType().getType()).isEqualTo(Integer.class);
    }

    @Test
    public void testDoNotProcessTransientEntity() throws Exception {
        final RdfMappingContext mappingContext = new RdfMappingContext();
        final RdfEntity entity = mappingContext.getPersistentEntity(Person8.class);
        assertThat(entity).isNull();
    }

    @Test
    public void testDoNotProcessTransientProperties() throws Exception {
        final RdfMappingContext mappingContext = new RdfMappingContext(ZonedDateTime.class);
        final RdfEntity entity = mappingContext.getPersistentEntity(Person9.class);
        final RdfProperty formatterProperty = (RdfProperty) entity.getPersistentProperty("formatter");
        assertThat(formatterProperty).isNull();
    }

    @Test
    public void testZoneDateTimeIsASimpleType() throws Exception {
        final RdfMappingContext mappingContext = new RdfMappingContext(ZonedDateTime.class);
        final RdfEntity entity = mappingContext.getPersistentEntity(Person9.class);
        final RdfProperty zonedDateTimeProperty = (RdfProperty) entity.getPersistentProperty("zonedDateTime");
        assertThat(zonedDateTimeProperty.isSimpleProperty());
    }

}
