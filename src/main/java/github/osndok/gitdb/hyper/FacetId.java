package github.osndok.gitdb.hyper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * UNIMPLEMENTED
 * ----
 * This annotation is intended to allow a developer to specify an exact UUID for a given facet/schema class,
 * which may be useful in cases where a class name or package changes (which would otherwise change the
 * generated UUID and result in data loss).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD })
public
@interface FacetId
{
    /**
     * When applied to a class, this should be a constant that is the UUID associated with this class.
     *
     * When applied to a static string field, this should be left blank/empty, and the field contents
     * will be used.
     */
    String value() default "";
}
