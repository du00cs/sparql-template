package ch.unil.sparql.template.mapping;

import ch.unil.sparql.template.annotation.Predicate;
import org.apache.jena.shared.PrefixMapping;
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

    private PrefixMapping prefixMap;

    private boolean isTransient;

    public RdfProperty(Field field, PropertyDescriptor propertyDescriptor, PersistentEntity<?, RdfProperty> owner, SimpleTypeHolder simpleTypeHolder) {
        super(field, propertyDescriptor, owner, simpleTypeHolder);
        this.predicateAnnot = findAnnotation(Predicate.class);
        this.simpleTypeHolder = simpleTypeHolder;
        this.prefixMap = ((RdfEntity<?>)owner).getPrefixMap();
        this.isTransient = super.isTransient() || !isAnnotationPresent(Predicate.class);
    }

    @Override
    protected Association<RdfProperty> createAssociation() {
        return new Association<>(this, null);
    }

    public String getPrefix() {
        return predicateAnnot.value();
    }

    public String getQName(){
        return prefixMap.expandPrefix(getPrefix() + ":" + getName());
    }

    public boolean isSimpleProperty() {
        return !isTransient() && !isVersionProperty() && !isEntity() && !isAssociation() && !isCollectionLike() && !isArray() && !isMap();
    }

    public boolean isCollectionOfSimple(){
        return isCollectionLike() && simpleTypeHolder.isSimpleType(getActualType());
    }

    public boolean isCollectionOfEntities(){
       return isCollectionLike() && isEntity();
    }

    @Override
    public boolean isTransient() {
        return isTransient;
    }

    @Override
    public String toString() {
        return getOwner().getType().getSimpleName() + "." + getName();
    }

}
