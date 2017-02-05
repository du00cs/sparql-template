package ch.unil.sparql.template;

import ch.unil.sparql.template.convert.ExtendedRdfJavaConverter;
import ch.unil.sparql.template.convert.RdfJavaConverter;
import ch.unil.sparql.template.mapping.RdfEntity;
import ch.unil.sparql.template.mapping.RdfMappingContext;
import ch.unil.sparql.template.mapping.RdfProperty;
import ch.unil.sparql.template.query.SparqlQueryService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentPropertyAccessor;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author gushakov
 */
public class SparqlTemplate {

    private static final Logger logger = LoggerFactory.getLogger(SparqlTemplate.class);

    private SparqlQueryService queryService;
    private RdfJavaConverter rdfJavaConverter;
    private RdfMappingContext mappingContext;

    private LoadingCache<Pair<String, Class<?>>, Object> cache;


    public SparqlTemplate(String endpoint) {
        this(new SparqlQueryService(endpoint, true), new ExtendedRdfJavaConverter());
    }

    public SparqlTemplate(SparqlQueryService queryService) {
        this(queryService, new ExtendedRdfJavaConverter());
    }

    public SparqlTemplate(String endpoint, RdfJavaConverter rdfJavaConverter) {
        this(new SparqlQueryService(endpoint, true), rdfJavaConverter);
    }

    public SparqlTemplate(SparqlQueryService queryService, RdfJavaConverter rdfJavaConverter) {
        this.queryService = queryService;
        this.rdfJavaConverter = rdfJavaConverter;
        mappingContext = new RdfMappingContext(rdfJavaConverter.getCustomTypes());
        cache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build(new CacheLoader<Pair<String, Class<?>>, Object>() {
                    @Override
                    public Object load(Pair<String, Class<?>> pair) throws Exception {
                        return SparqlTemplate.this.createDynamicProxy(pair.getLeft(),
                                pair.getRight(), mappingContext.getPersistentEntity(pair.getRight()));
                    }
                });
    }

    <T> T load(String iri, Class<T> type){
        try {
            return type.cast(cache.get(Pair.of(iri, type)));
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    <T> void loadProperties(String iri, T bean) {
        final RdfEntity<?> entity = mappingContext.getPersistentEntity(bean.getClass());
        final PersistentPropertyAccessor propertyAccessor = entity.getPropertyAccessor(bean);

        // query SPARQL endpoint for the set of all triples matching the subject IRI
        final Collection<Triple> triples = queryService.query(iri);

        logger.debug("SPARQL query returned " + triples.size() + " triples");

        // load all simple properties
        entity.doWithProperties((RdfProperty rdfProperty) -> {
            if (rdfProperty.isSimpleProperty()) {
                loadSimpleProperty(iri, triples, entity, rdfProperty, propertyAccessor);
            } else {
                if (rdfProperty.isCollectionOfSimple()) {
                    loadCollectionOfSimpleProperties(iri, triples, entity, rdfProperty, propertyAccessor);
                }
            }
        });

        // process associations
        entity.doWithAssociations((Association<RdfProperty> association) -> {

            if (association.getInverse().isCollectionOfEntities()) {
                // multiple entities relation
                loadCollectionOfEntities(iri, triples, entity, association, propertyAccessor);
            } else {
                // single entity relation
                loadAssociation(iri, triples, entity, association, propertyAccessor);
            }

        });

    }

    private void loadSimpleProperty(String iri, Collection<Triple> triples, RdfEntity<?> entity, RdfProperty rdfProperty,
                                    PersistentPropertyAccessor propertyAccessor) {
        // get all triples where the predicate matches the property
        final Collection<Triple> matchingTriples = filterForProperty(triples, rdfProperty);

        // there must be exactly one triple with matching predicate
        if (matchingTriples.size() == 0) {
            return;
        }

        final Triple triple = matchingTriples.iterator().next();
        final Node objectNode = triple.getObject();

        // convert to Java and assign to the property
        propertyAccessor.setProperty(rdfProperty, rdfJavaConverter.convert(objectNode, rdfProperty));
    }

    private void loadAssociation(String iri, Collection<Triple> triples, RdfEntity<?> entity, Association<RdfProperty> association,
                                 PersistentPropertyAccessor propertyAccessor) {

        final RdfProperty inverseProperty = association.getInverse();

        // get all triples where the predicate matches the property
        final Collection<Triple> matchingTriples = filterForProperty(triples, inverseProperty);

        // there must be exactly one triple with matching predicate
        if (matchingTriples.size() != 1) {
            return;
        }

        final Triple triple = matchingTriples.iterator().next();
        final Node objectNode = triple.getObject();

        propertyAccessor.setProperty(inverseProperty, load(objectNode.getURI(), inverseProperty.getType()));
    }

    private void loadCollectionOfSimpleProperties(String iri, Collection<Triple> triples, RdfEntity<?> entity, RdfProperty rdfProperty,
                                                  PersistentPropertyAccessor propertyAccessor) {

        final Collection<Triple> matchingTriples = filterForProperty(triples, rdfProperty);

        final List<Object> listOfValues = new ArrayList<>();

        for (final Triple triple : matchingTriples) {

            final Node objectNode = triple.getObject();

            // convert to Java and store in the value list
            listOfValues.add(rdfJavaConverter.convert(objectNode, rdfProperty));

        }

        // cast the collection to the required type
        CollectionUtils.transform(listOfValues, input -> rdfProperty.getActualType().cast(input));

        propertyAccessor.setProperty(rdfProperty, listOfValues);
    }

    private void loadCollectionOfEntities(String iri, Collection<Triple> triples, RdfEntity<?> entity, Association<RdfProperty> association,
                                          PersistentPropertyAccessor propertyAccessor) {

        final RdfProperty inverseProperty = association.getInverse();

        // get all triples where the predicate matches the property
        final Collection<Triple> matchingTriples = filterForProperty(triples, inverseProperty);

        final Set<DynamicBeanProxy> proxies = new HashSet<>();

        for (final Triple triple : matchingTriples) {
            proxies.add((DynamicBeanProxy) load(triple.getObject().getURI(), inverseProperty.getActualType()));
        }

        propertyAccessor.setProperty(inverseProperty, proxies);
    }

    private <S, T> T createDynamicProxy(String iri, Class<T> beanType, RdfEntity<?> entity) {
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

    private Collection<Triple> filterForProperty(Collection<Triple> triples, RdfProperty rdfProperty) {
        return CollectionUtils.select(triples, triple -> {
                    final String predicateUri = triple.getPredicate().getURI();

                    // match qualified name of property to predicate URI
                    if (predicateUri.equals(rdfProperty.getQName())) {

                        // for relation
                        if (rdfProperty.isEntity() || rdfProperty.isCollectionOfEntities()) {
                            // check that the object is a URI node
                            return triple.getObject().isURI();
                        }

                        // for simple property or collection of simple properties
                        if (rdfProperty.isSimpleProperty() || rdfProperty.isCollectionOfSimple()) {
                            // check that the object value can be converted to the type of the property
                            return rdfJavaConverter.canConvert(triple.getObject(), rdfProperty);
                        }

                    }

                    return false;
                }
        );
    }

}
