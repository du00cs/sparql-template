package ch.unil.sparql.template.mapping;

import ch.unil.sparql.template.Utils;
import ch.unil.sparql.template.annotation.PrefixMap;
import org.apache.commons.collections4.MapUtils;
import org.apache.jena.shared.PrefixMapping;
import org.springframework.data.mapping.model.BasicPersistentEntity;
import org.springframework.data.util.TypeInformation;

import java.util.HashMap;

/**
 * @author gushakov
 */
public class RdfEntity<T> extends BasicPersistentEntity<T, RdfProperty> {
    private PrefixMapping prefixMap;

    private PrefixMap prefixMapAnnot;

    public RdfEntity(TypeInformation<T> information) {
        super(information);
        this.prefixMapAnnot = findAnnotation(PrefixMap.class);
    }

    @Override
    public void verify() {
        super.verify();

        // construct value map from provided annotations
        prefixMap = Utils.defaultPrefixMap();
        if (prefixMapAnnot != null) {
            prefixMap.setNsPrefixes(MapUtils.putAll(new HashMap<>(), prefixMapAnnot.value()));
        }
    }

    public PrefixMapping getPrefixMap() {
        return prefixMap;
    }
}
