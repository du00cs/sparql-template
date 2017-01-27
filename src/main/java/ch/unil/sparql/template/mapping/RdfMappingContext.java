package ch.unil.sparql.template.mapping;

import ch.unil.sparql.template.Utils;
import org.apache.jena.shared.PrefixMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.mapping.context.AbstractMappingContext;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.util.TypeInformation;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.time.ZonedDateTime;
import java.util.Collections;

/**
 * @author gushakov
 */
public class RdfMappingContext extends AbstractMappingContext<RdfEntity<?>, RdfProperty> {
    private static final Logger logger = LoggerFactory.getLogger(RdfMappingContext.class);

    private PrefixMapping prefixMap;

    public RdfMappingContext() {
        this(Utils.defaultPrefixMap(), null);
        this.prefixMap = Utils.defaultPrefixMap();
        setSimpleTypeHolder(new SimpleTypeHolder(Collections.singleton(ZonedDateTime.class), true));
    }

    public RdfMappingContext(PrefixMapping prefixMap) {
        this.prefixMap = prefixMap;
        setSimpleTypeHolder(new SimpleTypeHolder(Collections.singleton(ZonedDateTime.class), true));
    }

    public RdfMappingContext(PrefixMapping prefixMap, SimpleTypeHolder simpleTypeHolder) {
        this.prefixMap = prefixMap;
        if (simpleTypeHolder != null) {
            setSimpleTypeHolder(simpleTypeHolder);
        } else {
            setSimpleTypeHolder(new SimpleTypeHolder(Collections.singleton(ZonedDateTime.class), true));
        }
    }

    @Override
    protected <T> RdfEntity<?> createPersistentEntity(TypeInformation<T> typeInformation) {
        final RdfEntity<T> entity = new RdfEntity<>(typeInformation, prefixMap);
        logger.debug("Created RDF entity for type {}", typeInformation.getType().getSimpleName());
        return entity;
    }

    @Override
    protected RdfProperty createPersistentProperty(Field field, PropertyDescriptor descriptor, RdfEntity<?> owner, SimpleTypeHolder simpleTypeHolder) {
        final RdfProperty property = new RdfProperty(field, descriptor, owner, simpleTypeHolder);
        logger.debug("Created RDF {} property for field {} of type {}", property.isTransient() ? "(transient)" : "", field.getName(), owner.getType().getSimpleName());
        return property;
    }

    @Override
    protected boolean shouldCreatePersistentEntityFor(TypeInformation<?> type) {
        return super.shouldCreatePersistentEntityFor(type) && AnnotationUtils.findAnnotation(type.getType(), Persistent.class) != null;
    }
}
