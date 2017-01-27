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
import org.apache.commons.collections4.Transformer;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Literal;
import org.apache.jena.graph.Triple;
import org.apache.jena.shared.PrefixMapping;
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentPropertyAccessor;

import java.util.*;

/**
 * @author gushakov
 */
public class SparqlTemplate {

    private SparqlQueryService queryService;
    private RdfJavaConverter rdfJavaConverter;
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
        this.rdfJavaConverter = new RdfJavaConverter();
        mappingContext = new RdfMappingContext(Utils.defaultPrefixMap().setNsPrefixes(prefixMap));
    }

    public <T> T load(String iri, Class<T> type) {
       return load(null, null, iri, type);
    }

    private <S, T> T load(S fromBean, RdfProperty fromProperty, String iri, Class<T> type){
        RdfEntity<?> entity = mappingContext.getPersistentEntity(type);
        return createDynamicProxy(fromBean, fromProperty, iri, type, entity);
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
            }
            else {
                // single entity relation
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
        if (matchingTriples.size() == 0) {
            return;
        }

        final Triple triple = matchingTriples.iterator().next();
        final Node objectNode = triple.getObject();

        // convert to Java and assign to the property
        propertyAccessor.setProperty(rdfProperty, rdfJavaConverter.convert((Node_Literal) objectNode, rdfProperty));
    }

    private void loadAssociation(String iri, Collection<Triple> triples, RdfEntity<?> entity, Association<RdfProperty> association,
                                 PersistentPropertyAccessor propertyAccessor) {

        final RdfProperty inverseProperty = association.getInverse();

        // get all triples where the predicate matches the property
        final Collection<Triple> matchingTriples = filterForProperty(triples, inverseProperty, entity.getPrefixMap());

        // there must be exactly one triple with matching predicate
        if (matchingTriples.size() != 1) {
            return;
        }

        final Triple triple = matchingTriples.iterator().next();
        final Node objectNode = triple.getObject();

        propertyAccessor.setProperty(inverseProperty, load(this, inverseProperty, objectNode.getURI(), inverseProperty.getType()));
    }

    private void loadCollectionOfSimpleProperties(String iri, Collection<Triple> triples, RdfEntity<?> entity, RdfProperty rdfProperty,
                                                  PersistentPropertyAccessor propertyAccessor) {

        final Collection<Triple> matchingTriples = filterForProperty(triples, rdfProperty, entity.getPrefixMap());

        final Set<Object> valueSet = new HashSet<>();

        for (final Triple triple : matchingTriples) {

            final Node objectNode = triple.getObject();

            // convert to Java and store in the value set
            valueSet.add(rdfJavaConverter.convert((Node_Literal) objectNode, rdfProperty));

        }

        // cast the collection to the required type
        CollectionUtils.transform(valueSet, new Transformer<Object, Object>() {
            @Override
            public Object transform(Object input) {
                return rdfProperty.getActualType().cast(input);
            }
        });


        propertyAccessor.setProperty(rdfProperty, valueSet);
    }

    private void loadCollectionOfEntities(String iri, Collection<Triple> triples, RdfEntity<?> entity, Association<RdfProperty> association,
                                          PersistentPropertyAccessor propertyAccessor){

        final RdfProperty inverseProperty = association.getInverse();

        // get all triples where the predicate matches the property
        final Collection<Triple> matchingTriples = filterForProperty(triples, inverseProperty, entity.getPrefixMap());

        final Set<DynamicBeanProxy> proxies = new HashSet<>();

        for (final Triple triple : matchingTriples){
            proxies.add((DynamicBeanProxy)load(this, inverseProperty, triple.getObject().getURI(), inverseProperty.getActualType()));
        }

        propertyAccessor.setProperty(inverseProperty, proxies);
    }

    private <S, T> T createDynamicProxy(S fromBean, RdfProperty fromProperty, String iri, Class<T> beanType, RdfEntity<?> entity) {
        try {
            return new ByteBuddy()
                    .subclass(beanType)
                    .implement(DynamicBeanProxy.class)
                    .method(ElementMatchers.isDeclaredBy(DynamicBeanProxy.class)
                            .or(ElementMatchers.isGetter()))
                    .intercept(MethodDelegation.to(new DynamicBeanProxyInterceptor<>(fromBean, fromProperty , iri, beanType, entity, this)))
                    .make()
                    .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                    .getLoaded()
                    .newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Cannot create dynamic proxy for bean of type " + beanType.getSimpleName(), e);
        }
    }

    private Collection<Triple> filterForProperty(Collection<Triple> triples, RdfProperty rdfProperty, final PrefixMapping prefixMap) {
        return CollectionUtils.select(triples, triple -> {
                    final String predicateUri = prefixMap.expandPrefix(triple.getPredicate().getURI());

                    // match qualified name of property to predicate URI
                    if (predicateUri.equals(rdfProperty.getQName())) {

                        // for relation
                        if (rdfProperty.isEntity() || rdfProperty.isCollectionOfEntities()) {
                            // check that the object is a URI node
                            return triple.getObject().isURI();
                        }

                        // for simple property or collection of simple properties
                        if (rdfProperty.isSimpleProperty() || rdfProperty.isCollectionOfSimple()) {
                            // check that object is a literal node and that it can be converted to the value for the property
                            return triple.getObject().isLiteral()
                                    && rdfJavaConverter.canConvert((Node_Literal) triple.getObject(), rdfProperty);
                        }

                    }

                    return false;
                }
        );
    }

}
