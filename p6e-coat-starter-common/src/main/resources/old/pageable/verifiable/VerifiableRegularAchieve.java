package club.p6e.coat.common.old.pageable.verifiable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.regex.Pattern;

/**
 * @author lidashuang
 * @version 1.0
 */
public class VerifiableRegularAchieve implements VerifiableAchieveInterface {

    @Override
    public boolean execute(Annotation annotation, Field field, Object data) {
        if (annotation instanceof VerifiableRegular regular) {
            try {
                field.setAccessible(true);
                final Object value = field.get(data);
                final String content = regular.value();
                if (value instanceof String string) {
                    return Pattern.compile(content).matcher(string).matches();
                }
            } catch (Exception ignored) {
                // ignored exception
            }
        }
        return false;
    }

}