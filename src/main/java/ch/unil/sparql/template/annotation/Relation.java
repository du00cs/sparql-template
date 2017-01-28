package ch.unil.sparql.template.annotation;

import org.springframework.data.annotation.Reference;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author gushakov
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Reference
public @interface Relation {
}
