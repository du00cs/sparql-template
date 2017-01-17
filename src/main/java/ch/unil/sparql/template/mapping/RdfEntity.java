package ch.unil.sparql.template.mapping;

import ch.unil.sparql.template.annotation.PrefixMap;
import org.apache.commons.collections4.MapUtils;
import org.apache.jena.shared.PrefixMapping;
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.SimpleAssociationHandler;
import org.springframework.data.mapping.SimplePropertyHandler;
import org.springframework.data.mapping.model.BasicPersistentEntity;
import org.springframework.data.util.TypeInformation;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

/**
 * @author gushakov
 */
public class RdfEntity<T> extends BasicPersistentEntity<T, RdfProperty> {
    private PrefixMapping prefixMap;

    private PrefixMap prefixMapAnnot;

    public RdfEntity(TypeInformation<T> information, PrefixMapping prefixMap) {
        super(information);
        this.prefixMapAnnot = findAnnotation(PrefixMap.class);
        this.prefixMap = prefixMap;
    }

    @Override
    public void verify() {
        super.verify();

        // record prefix map if specified for this entity
        if (prefixMapAnnot != null) {
            prefixMap.setNsPrefixes(MapUtils.putAll(new HashMap<>(), prefixMapAnnot.value()));
        }
    }

    public PrefixMapping getPrefixMap() {
        return prefixMap;
    }

    public Optional<PersistentProperty<?>> findPropertyForGetter(Method method) {
        final MatchingGetterCollectingPropertiesHandler handler = new MatchingGetterCollectingPropertiesHandler(method);
        doWithProperties(handler);
        final Collection<PersistentProperty<?>> properties = handler.getProperties();
        return !properties.isEmpty() ? Optional.of(properties.iterator().next()) : Optional.empty();
    }

    public Optional<Association<?>> findAssociationForGetter(Method method) {
        final MatchingGetterCollectingAssociationsHandler handler = new MatchingGetterCollectingAssociationsHandler(method);
        doWithAssociations(handler);
        final Collection<Association<?>> associations = handler.getAssociations();
        return !associations.isEmpty() ? Optional.of(associations.iterator().next()) : Optional.empty();
    }

    class MatchingGetterCollectingPropertiesHandler implements SimplePropertyHandler {

        private Collection<PersistentProperty<?>> properties;

        private Method getter;

        public MatchingGetterCollectingPropertiesHandler(Method getter) {
            this.getter = getter;
            this.properties = new HashSet<>();
        }

        public Collection<PersistentProperty<?>> getProperties() {
            return properties;
        }

        @Override
        public void doWithPersistentProperty(PersistentProperty<?> property) {
            if (property.getGetter() != null && property.getGetter().equals(getter)) {
                properties.add(property);
            }
        }
    }

    class MatchingGetterCollectingAssociationsHandler implements SimpleAssociationHandler {
        private Collection<Association<?>> associations;

        private Method getter;

        public MatchingGetterCollectingAssociationsHandler(Method getter) {
            this.getter = getter;
            this.associations = new HashSet<>();
        }

        public Collection<Association<?>> getAssociations() {
            return associations;
        }

        @Override
        public void doWithAssociation(Association<? extends PersistentProperty<?>> association) {
            if (association.getInverse() != null && association.getInverse().getGetter() != null && association.getInverse().getGetter().equals(getter)) {
                associations.add(association);
            }
        }
    }
}
