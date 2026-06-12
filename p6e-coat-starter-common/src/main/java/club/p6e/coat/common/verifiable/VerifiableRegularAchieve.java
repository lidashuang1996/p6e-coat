package club.p6e.coat.common.verifiable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.regex.Pattern;

/**
 * Verifiable Regular Achieve
 *
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
                if (!content.isEmpty() && value instanceof String vs) {
                    return Pattern.compile(content).matcher(vs).matches();
                }
            } catch (Exception ignored) {
                // ignored exception
            }
        }
        return false;
    }

}