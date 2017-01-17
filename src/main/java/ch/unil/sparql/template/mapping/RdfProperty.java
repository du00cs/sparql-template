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

    public RdfProperty(Field field, PropertyDescriptor propertyDescriptor, PersistentEntity<?, RdfProperty> owner, SimpleTypeHolder simpleTypeHolder) {
        super(field, propertyDescriptor, owner, simpleTypeHolder);
        predicateAnnot = findAnnotation(Predicate.class);
    }

    @Override
    protected Association<RdfProperty> createAssociation() {
        return null;
    }

    public String getPrefix() {
        return predicateAnnot.value();
    }
}
