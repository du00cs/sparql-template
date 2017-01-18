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
@Target(ElementType.TYPE)
@Persistent
public @interface Rdf {
    String[] value() default {};
}
