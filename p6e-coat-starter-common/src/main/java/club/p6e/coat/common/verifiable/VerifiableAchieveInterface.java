package club.p6e.coat.common.verifiable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface VerifiableAchieveInterface {

    boolean execute(Annotation annotation, Field field, Object data);

}
