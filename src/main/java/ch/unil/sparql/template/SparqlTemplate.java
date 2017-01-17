package ch.unil.sparql.template;

import ch.unil.sparql.template.convert.RdfJavaConverter;
import ch.unil.sparql.template.mapping.RdfEntity;
import ch.unil.sparql.template.mapping.RdfMappingContext;
import ch.unil.sparql.template.mapping.RdfProperty;
import ch.unil.sparql.template.query.SparqlQueryService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.mapping.PersistentPropertyAccessor;

import java.util.Collection;

/**
 * @author gushakov
 */
public class SparqlTemplate {

    private SparqlQueryService queryService;
    private ConversionService conversionService;
    private RdfMappingContext mappingContext;

    public SparqlTemplate(String endpoint) {
        this(new SparqlQueryService(endpoint, true));
    }

    public SparqlTemplate(SparqlQueryService queryService) {
        this.queryService = queryService;
        conversionService = new DefaultConversionService();
        ((DefaultConversionService) conversionService).addConverter(new RdfJavaConverter());
        mappingContext = new RdfMappingContext();
    }

    public <T> T load(String iri, Class<T> type) {
        final RdfEntity<?> entity = mappingContext.getPersistentEntity(type);

        final PersistentPropertyAccessor propertyAccessor;
        final T bean;
        try {
            bean = type.newInstance();
            propertyAccessor = entity.getPropertyAccessor(bean);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        final Collection<Triple> triples = queryService.query(iri, entity.getPrefixMap());

        entity.doWithProperties((RdfProperty rdfProperty) -> {

            final Collection<Triple> matchingPredicates = filterForProperty(triples, entity, rdfProperty);

            if (matchingPredicates.isEmpty()) {
                throw new IllegalStateException("No matching RDF predicate found for IRI " +
                        iri + " and property " + rdfProperty.getName() + "  with prefix " + rdfProperty.getPrefix());
            }

            if (matchingPredicates.size() > 1) {
                throw new UnsupportedOperationException("Single matching RDF predicate expected for IRI " +
                        iri + " and property " + rdfProperty.getName());
            }

            final Triple triple = matchingPredicates.iterator().next();

            final Node objectNode = triple.getObject();

            if (objectNode.isLiteral()) {
                propertyAccessor.setProperty(rdfProperty, conversionService.convert(objectNode, Object.class));
            } else {
                throw new UnsupportedOperationException("Cannot convert (non literal) RDF node " + objectNode.getName() + " to Java object");
            }

        });
        return bean;

    }

    private Collection<Triple> filterForProperty(Collection<Triple> triples, RdfEntity<?> owner, RdfProperty rdfProperty) {
        final String predicateUri = owner.getPrefixMap().expandPrefix(rdfProperty.getPrefix() + ":" + rdfProperty.getName());
        return CollectionUtils.select(triples, triple -> triple.getPredicate().getURI()
                .equals(predicateUri));
    }

}
