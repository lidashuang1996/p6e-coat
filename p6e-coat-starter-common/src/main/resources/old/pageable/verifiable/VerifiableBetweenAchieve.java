package club.p6e.coat.common.old.pageable.verifiable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author lidashuang
 * @version 1.0
 */
public class VerifiableBetweenAchieve implements VerifiableAchieveInterface {

    @Override
    public boolean execute(Annotation annotation, Field field, Object data) {
        if (annotation instanceof VerifiableBetween between) {
            try {
                field.setAccessible(true);
                final Object value = field.get(data);
                final double min = between.min();
                final double max = between.max();
                if (value instanceof Number number
                        && number.doubleValue() >= min
                        && number.doubleValue() <= max) {
                    return true;
                }
            } catch (Exception ignored) {
                // ignored exception
            }
        }
        return false;
    }

}
