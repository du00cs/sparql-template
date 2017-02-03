package ch.unil.sparql.template.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.mapping.context.AbstractMappingContext;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.util.TypeInformation;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author gushakov
 */
public class RdfMappingContext extends AbstractMappingContext<RdfEntity<?>, RdfProperty> {
    private static final Logger logger = LoggerFactory.getLogger(RdfMappingContext.class);

    public RdfMappingContext(Class<?>... customTypes){
        this(new HashSet<>(Arrays.asList(customTypes)));
    }

    public RdfMappingContext(Set<Class<?>> customTypes) {
        if (customTypes != null) {
            setSimpleTypeHolder(new SimpleTypeHolder(customTypes, true));
        }
    }

    @Override
    protected <T> RdfEntity<?> createPersistentEntity(TypeInformation<T> typeInformation) {
        final RdfEntity<T> entity = new RdfEntity<>(typeInformation);
        logger.debug("Created RDF entity for type {}", typeInformation.getType().getSimpleName());
        return entity;
    }

    @Override
    protected RdfProperty createPersistentProperty(Field field, PropertyDescriptor descriptor, RdfEntity<?> owner, SimpleTypeHolder simpleTypeHolder) {
        final RdfProperty property = new RdfProperty(field, descriptor, owner, simpleTypeHolder);
        logger.debug("Created RDF {} property for field {} of type {}", property.isTransient() ? "transient" : "persistent", field.getName(), owner.getType().getSimpleName());
        return property;
    }

    @Override
    protected boolean shouldCreatePersistentEntityFor(TypeInformation<?> type) {
        return super.shouldCreatePersistentEntityFor(type) && AnnotationUtils.findAnnotation(type.getType(), Persistent.class) != null;
    }
}
