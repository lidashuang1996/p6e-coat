package club.p6e.coat.common.verifiable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Verifiable Length
 *
 * @author lidashuang
 * @version 1.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface VerifiableLength {

    /**
     * Verifiable Length Min Length
     *
     * @return Verifiable Length Min Length Value
     */
    int min();

    /**
     * Verifiable Length Max Length
     *
     * @return Verifiable Length Max Length Value
     */
    int max();

    /**
     * Verifiable Length Is Allow Null
     *
     * @return Verifiable Length Is Allow Null Value
     */
    boolean isAllowNull() default false;

}
