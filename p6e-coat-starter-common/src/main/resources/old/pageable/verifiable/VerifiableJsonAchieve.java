package club.p6e.coat.common.old.pageable.verifiable;

import club.p6e.coat.common.utils.JsonUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
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
                if (value instanceof String string) {
                    JsonUtil.fromJsonToMap(string, Object.class, Object.class);
                    return true;
                }
            } catch (Exception ignored) {
                // ignored exception
            }
        }
        return false;
    }

}
