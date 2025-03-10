package club.p6e.coat.common.verifiable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author lidashuang
 * @version 1.0
 */
public class VerifiableNotNullAchieve implements VerifiableAchieveInterface {

    @Override
    public boolean execute(Annotation annotation, Field field, Object data) {
        if (annotation instanceof VerifiableNotNull) {
            try {
                field.setAccessible(true);
                return field.get(data) != null;
            } catch (Exception ignored) {
                // ignored exception
            }
        }
        return false;
    }

}
