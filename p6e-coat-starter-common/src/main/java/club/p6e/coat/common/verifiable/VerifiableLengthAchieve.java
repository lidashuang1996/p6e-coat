package club.p6e.coat.common.verifiable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author lidashuang
 * @version 1.0
 */
public class VerifiableLengthAchieve implements VerifiableAchieveInterface {

    @Override
    public boolean execute(Annotation annotation, Field field, Object data) {
        if (annotation instanceof VerifiableLength length) {
            try {
                field.setAccessible(true);
                final Object value = field.get(data);
                if (length.isAllowNull() && value == null) {
                    return true;
                }
                final int min = length.min();
                final int max = length.max();
                if (value instanceof String string && string.length() >= min && string.length() <= max) {
                    return true;
                }
            } catch (Exception ignored) {
                // ignored exception
            }
        }
        return false;
    }

}