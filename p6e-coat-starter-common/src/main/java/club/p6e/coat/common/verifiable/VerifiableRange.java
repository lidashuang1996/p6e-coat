package club.p6e.coat.common.verifiable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Verifiable Range
 *
 * @author lidashuang
 * @version 1.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface VerifiableRange {

    /**
     * Verifiable Range
     *
     * @return Verifiable Range Range Value
     */
    String[] value() default {};

}
