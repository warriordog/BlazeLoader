package net.BlazeLoader.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Documented
@Retention(RetentionPolicy.CLASS)
/**
 * Annotation to mark beta fields, methods, and classes.  Items marked with this annotation may be changed or removed later.
 */
public @interface Beta {

    /**
     * Is the beta item stable?  (is it likely to be soon removed or highly changed?)
     *
     * @return Return true if the item is stable, false if not.
     */
    public boolean stable() default false;
}
