package club.p6e.coat.common.old.pageable.verifiable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author lidashuang
 * @version 1.0
 */
public class VerifiableRangeAchieve implements VerifiableAchieveInterface {

    @Override
    public boolean execute(Annotation annotation, Field field, Object data) {
        if (annotation instanceof VerifiableRange range) {
            try {
                field.setAccessible(true);
                final String[] list = range.value();
                final Object value = field.get(data);
                if (value instanceof String string) {
                    for (final String item : list) {
                        if (item.equals(string)) {
                            return true;
                        }
                    }
                } else if (value instanceof Number number) {
                    for (final String item : list) {
                        if (String.valueOf(Double.valueOf(item))
                                .equals(String.valueOf(number.doubleValue()))) {
                            return true;
                        }
                    }
                }
            } catch (Exception ignored) {
                // ignored exception
            }
        }
        return false;
    }

}