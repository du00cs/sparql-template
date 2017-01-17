package ch.unil.sparql.template;

import ch.unil.sparql.template.mapping.RdfEntity;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyAccessor;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * @author gushakov
 */
public class DynamicBeanProxyInterceptor<T> {
    private static final Logger logger = LoggerFactory.getLogger(DynamicBeanProxyInterceptor.class);

    private String iri;

    private T bean;

    private Class<T> beanType;

    private RdfEntity<?> entity;

    private SparqlTemplate sparqlTemplate;

    private PersistentPropertyAccessor propertyAccessor;

    public DynamicBeanProxyInterceptor(String iri, Class<T> beanType, RdfEntity<?> entity, SparqlTemplate sparqlTemplate) {
        this.iri = iri;
        this.beanType = beanType;
        this.entity = entity;
        this.sparqlTemplate = sparqlTemplate;
    }

    public Object __getBean() {
        return bean;
    }

    @RuntimeType
    public Object interceptGetter(@Origin Method getter) {

        logger.debug("Intercepting getter {} for bean of type {}", getter.getName(), beanType.getSimpleName());

        // check if the bean needs to be loaded
        if (bean == null) {

            // instantiate new bean
            try {
                bean = beanType.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Cannot instantiate bean of type " + beanType, e);
            }

            // load and process properties
            propertyAccessor = sparqlTemplate.loadProperties(iri, bean);
        }

        // find a property corresponding to the getter
        final Optional<PersistentProperty<?>> getterPropertyOptional = entity.findPropertyForGetter(getter);

        // if there is no matching property, try to find an association corresponding to the getter
        if (!getterPropertyOptional.isPresent()) {
            final Optional<Association<?>> getterAssociationOptional = entity.findAssociationForGetter(getter);
            if (!getterAssociationOptional.isPresent()) {
                throw new IllegalStateException("Cannot find a property or an association for getter method " +
                        getter.getName() + " for bean of type " + beanType.getSimpleName());
            } else {
                // access the value of the inverse property of matching association
                return propertyAccessor.getProperty(getterAssociationOptional.get().getInverse());
            }
        } else {
            // access the value of the matching property
            return propertyAccessor.getProperty(getterPropertyOptional.get());
        }

    }


}
