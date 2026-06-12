package club.p6e.coat.common.verifiable;

import club.p6e.coat.common.utils.JsonUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Verifiable Json Achieve
 *
 * @author lidashuang
 * @version 1.0
 */
public class VerifiableJsonAchieve implements VerifiableAchieveInterface {

    @Override
    public boolean execute(Annotation annotation, Field field, Object data) {
        if (annotation instanceof VerifiableJson) {
            try {
                field.setAccessible(true);
                final Object value = field.get(data);
                if (value instanceof String vs) {
                    return JsonUtil.fromJsonToMap(vs, Object.class, Object.class) != null;
                }
            } catch (Exception ignored) {
                // ignored exception
            }
        }
        return false;
    }

}
