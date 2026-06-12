package club.p6e.coat.common.verifiable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Verifiable Between
 *
 * @author lidashuang
 * @version 1.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface VerifiableBetween {

    /**
     * Verifiable Between Min Length
     *
     * @return Verifiable Between Min Length Value
     */
    double min();

    /**
     * Verifiable Between Max Length
     *
     * @return Verifiable Between Max Length Value
     */
    double max();

}
