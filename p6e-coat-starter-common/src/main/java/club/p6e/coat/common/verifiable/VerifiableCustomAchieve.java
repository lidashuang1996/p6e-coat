package club.p6e.coat.common.verifiable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author lidashuang
 * @version 1.0
 */
public abstract class VerifiableCustomAchieve implements VerifiableAchieveInterface {

    @Override
    public boolean execute(Annotation annotation, Field field, Object data) {
        if (annotation instanceof VerifiableCustom custom) {
            try {
                field.setAccessible(true);
                return execute(custom.value(), field.get(data));
            } catch (Exception ignored) {
                // ignored exception
            }
        }
        return false;
    }

    public boolean execute(String content, Object data) {
        return false;
    }

}
