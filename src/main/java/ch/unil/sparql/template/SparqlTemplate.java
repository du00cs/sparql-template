package ch.unil.sparql.template;

import ch.unil.sparql.template.convert.RdfJavaConverter;
import ch.unil.sparql.template.mapping.RdfEntity;
import ch.unil.sparql.template.mapping.RdfMappingContext;
import ch.unil.sparql.template.mapping.RdfProperty;
import ch.unil.sparql.template.query.SparqlQueryService;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.shared.PrefixMapping;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentPropertyAccessor;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

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

    public SparqlTemplate(String endpoint, Map<String, String> prefixMap) {
        this(new SparqlQueryService(endpoint, true), prefixMap);
    }

    public SparqlTemplate(SparqlQueryService queryService) {
        this(queryService, Collections.emptyMap());
    }

    public SparqlTemplate(SparqlQueryService queryService, Map<String, String> prefixMap) {
        this.queryService = queryService;
        conversionService = new DefaultConversionService();
        ((DefaultConversionService) conversionService).addConverter(new RdfJavaConverter());
        mappingContext = new RdfMappingContext(Utils.defaultPrefixMap().setNsPrefixes(prefixMap));
    }

    public <T> T load(String iri, Class<T> type) {
        RdfEntity<?> entity = mappingContext.getPersistentEntity(type);
        return createDynamicProxy(iri, type, entity);
    }

    <T> PersistentPropertyAccessor loadProperties(String iri, T bean) {
        final RdfEntity<?> entity = mappingContext.getPersistentEntity(bean.getClass());
        final PersistentPropertyAccessor propertyAccessor = entity.getPropertyAccessor(bean);

        // query SPARQL endpoint for the set of all triples matching the subject IRI
        final Collection<Triple> triples = queryService.query(iri, entity.getPrefixMap());

        // load all simple properties
        entity.doWithProperties((RdfProperty rdfProperty) -> {
            if (rdfProperty.isSimpleProperty()) {
                loadSimpleProperty(iri, triples, entity, rdfProperty, propertyAccessor);
            }
        });

        // process associations
        entity.doWithAssociations((Association<RdfProperty> association) -> {

            if (!association.getInverse().isCollectionLike()) {
                loadAssociation(iri, triples, entity, association, propertyAccessor);
            }

        });

        return propertyAccessor;
    }

    private void loadSimpleProperty(String iri, Collection<Triple> triples, RdfEntity<?> entity, RdfProperty rdfProperty,
                                    PersistentPropertyAccessor propertyAccessor) {
        // get all triples where the predicate matches the property
        final Collection<Triple> matchingTriples = filterForProperty(triples, rdfProperty, entity.getPrefixMap());

        // there must be exactly one triple with matching predicate
        if (matchingTriples.size() != 1) {
            throw new IllegalStateException("Expecting exactly one RDF predicate for IRI " +
                    iri + " and property " + rdfProperty.getName() + " with prefix " + rdfProperty.getPrefix() +
                    ". But found " + matchingTriples.size());
        }

        final Triple triple = matchingTriples.iterator().next();
        final Node objectNode = triple.getObject();

        // object must be a literal Node
        if (!objectNode.isLiteral()) {
            throw new UnsupportedOperationException("Expecting a literal RDF node to be assigned to property " + rdfProperty +
                    ". But was " + objectNode);
        }

        // convert to Java and assign to the property
        propertyAccessor.setProperty(rdfProperty, conversionService.convert(objectNode, Object.class));
    }

    private void loadAssociation(String iri, Collection<Triple> triples, RdfEntity<?> entity, Association<RdfProperty> association,
                                 PersistentPropertyAccessor propertyAccessor) {

        final RdfProperty inverseProperty = association.getInverse();

        // get all triples where the predicate matches the property
        final Collection<Triple> matchingTriples = filterForProperty(triples, inverseProperty, entity.getPrefixMap());

        // there must be exactly one triple with matching predicate
        if (matchingTriples.size() != 1) {
            throw new IllegalStateException("Expecting exactly one RDF predicate for IRI " +
                    iri + " and property " + inverseProperty.getName() + " with prefix " + inverseProperty.getPrefix() +
                    ". But found " + matchingTriples.size());
        }

        final Triple triple = matchingTriples.iterator().next();
        final Node objectNode = triple.getObject();

        if (!objectNode.isURI()) {
            throw new IllegalStateException("Expecting object node to be an URI node for association (inverse) property " + inverseProperty.getName() +
                    ". But was " + objectNode);
        }

        propertyAccessor.setProperty(inverseProperty, load(objectNode.getURI(), inverseProperty.getType()));
    }

    private <T> T createDynamicProxy(String iri, Class<T> beanType, RdfEntity<?> entity) {
        try {
            return new ByteBuddy()
                    .subclass(beanType)
                    .implement(DynamicBeanProxy.class)
                    .method(ElementMatchers.isDeclaredBy(DynamicBeanProxy.class)
                            .or(ElementMatchers.isGetter()))
                    .intercept(MethodDelegation.to(new DynamicBeanProxyInterceptor<>(iri, beanType, entity, this)))
                    .make()
                    .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                    .getLoaded()
                    .newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Cannot create dynamic proxy for bean of type " + beanType.getSimpleName(), e);
        }
    }

    private Collection<Triple> filterForProperty(Collection<Triple> triples, RdfProperty rdfProperty, final PrefixMapping prefixMap) {
        final String predicateUri = prefixMap.expandPrefix(rdfProperty.getPrefix() + ":" + rdfProperty.getName());
        return CollectionUtils.select(triples, triple -> prefixMap.expandPrefix(triple.getPredicate().getURI())
                .equals(predicateUri));
    }

}
