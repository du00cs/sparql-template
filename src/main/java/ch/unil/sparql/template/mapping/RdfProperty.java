package ch.unil.sparql.template.mapping;

import ch.unil.sparql.template.annotation.Predicate;
import ch.unil.sparql.template.annotation.Relation;
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

    private Relation relationAnnot;

    private SimpleTypeHolder simpleTypeHolder;

    private boolean isTransient;

    public RdfProperty(Field field, PropertyDescriptor propertyDescriptor, PersistentEntity<?, RdfProperty> owner, SimpleTypeHolder simpleTypeHolder) {
        super(field, propertyDescriptor, owner, simpleTypeHolder);
        this.predicateAnnot = findAnnotation(Predicate.class);
        this.relationAnnot = findAnnotation(Relation.class);
        this.simpleTypeHolder = simpleTypeHolder;
        this.isTransient = super.isTransient() || !isAnnotationPresent(Predicate.class);
    }

    @Override
    protected Association<RdfProperty> createAssociation() {
        return new Association<>(this, null);
    }

    @Override
    public Association<RdfProperty> getAssociation() {
        return super.getAssociation();
    }

    public String getNamespace() {
        return predicateAnnot.value();
    }

    public String getQName() {
        return predicateAnnot.value() + (predicateAnnot.localName().equals(Predicate.DEFAULT_LOCAL_NAME) ? getName() : predicateAnnot.localName());
    }

    public boolean isSimpleProperty() {
        return !isTransient() && !isVersionProperty() && !isEntity() && !isAssociation() && !isCollectionLike() && !isArray() && !isMap();
    }

    public boolean isCollectionOfSimple() {
        return isCollectionLike() && simpleTypeHolder.isSimpleType(getActualType());
    }

    public boolean isCollectionOfEntities() {
        return isCollectionLike() && isEntity();
    }

    @Override
    public boolean isTransient() {
        return isTransient;
    }

    public boolean isRelation() {
        return relationAnnot != null;
    }

    public String getLanguage() {
        if (!predicateAnnot.language().equals(Predicate.DEFAULT_LANGUAGE)) {
            return predicateAnnot.language();
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return getOwner().getType().getSimpleName() + "." + getName();
    }

}
