package club.p6e.coat.common.verifiable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Verifiable Regular
 *
 * @author lidashuang
 * @version 1.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface VerifiableRegular {

    /**
     * Regular Expression
     *
     * @return Regular Expression Value
     */
    String value() default "";

}
