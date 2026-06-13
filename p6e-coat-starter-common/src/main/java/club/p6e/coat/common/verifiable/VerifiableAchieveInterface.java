package club.p6e.coat.common.verifiable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Verifiable Achieve Interface
 *
 * @author lidashuang
 * @version 1.0
 */
public interface VerifiableAchieveInterface {

    /**
     * Execute Verifiable Achieve
     *
     * @param annotation Annotation Object
     * @param field      Field Object
     * @param data       Data Object
     * @return Execute Verifiable Achieve Result Object
     */
    boolean execute(Annotation annotation, Field field, Object data);

}
