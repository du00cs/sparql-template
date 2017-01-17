package ch.unil.sparql.template.mapping;

import ch.unil.sparql.template.annotation.Predicate;
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.model.AnnotationBasedPersistentProperty;
import org.springframework.data.mapping.model.SimpleTypeHolder;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

/**
 * @author gushakov
 */
public class RdfProperty extends AnnotationBasedPersistentProperty<RdfProperty> {

    private Predicate predicateAnnot;

    private SimpleTypeHolder simpleTypeHolder;

    public RdfProperty(Field field, PropertyDescriptor propertyDescriptor, PersistentEntity<?, RdfProperty> owner, SimpleTypeHolder simpleTypeHolder) {
        super(field, propertyDescriptor, owner, simpleTypeHolder);
        this.predicateAnnot = findAnnotation(Predicate.class);
        this.simpleTypeHolder = simpleTypeHolder;
    }

    @Override
    protected Association<RdfProperty> createAssociation() {
        return new Association<>(this, null);
    }

    public String getPrefix() {
        return predicateAnnot.value();
    }

    public boolean isSimpleProperty() {
        return !isTransient() && !isVersionProperty() && !isEntity() && !isAssociation() && !isCollectionLike() && !isArray() && !isMap();
    }
}
