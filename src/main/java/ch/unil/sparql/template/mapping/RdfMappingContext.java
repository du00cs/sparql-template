package ch.unil.sparql.template.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mapping.context.AbstractMappingContext;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.util.TypeInformation;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

/**
 * @author gushakov
 */
public class RdfMappingContext extends AbstractMappingContext<RdfEntity<?>, RdfProperty> {
    private static final Logger logger = LoggerFactory.getLogger(RdfMappingContext.class);

    @Override
    protected <T> RdfEntity<?> createPersistentEntity(TypeInformation<T> typeInformation) {
        logger.debug("Creating RDF entity for type {}", typeInformation.getType().getSimpleName());
        return new RdfEntity<>(typeInformation);
    }

    @Override
    protected RdfProperty createPersistentProperty(Field field, PropertyDescriptor descriptor, RdfEntity<?> owner, SimpleTypeHolder simpleTypeHolder) {
        logger.debug("Creating RDF property for field {} of type {}", field.getName(), owner.getType().getSimpleName());
        return new RdfProperty(field, descriptor, owner, simpleTypeHolder);
    }
}