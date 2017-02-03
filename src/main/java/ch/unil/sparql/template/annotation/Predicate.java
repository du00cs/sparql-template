package ch.unil.sparql.template.annotation;

import org.springframework.data.annotation.Persistent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author gushakov
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Persistent
public @interface Predicate {
    String DEFAULT_NAMESPACE = "";
    String DEFAULT_LOCAL_NAME = "";
    String DEFAULT_LANGUAGE = "";
    String value() default DEFAULT_NAMESPACE;
    String localName() default DEFAULT_LOCAL_NAME;
    String language() default DEFAULT_LANGUAGE;
}
