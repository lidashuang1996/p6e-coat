package club.p6e.coat.common.verifiable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Verifiable
 *
 * @author lidashuang
 * @version 1.0
 */
public class Verifiable {

    /**
     * Verifiable Achieves Object
     */
    private static final Map<Class<? extends Annotation>, VerifiableAchieveInterface> ACHIEVES = new ConcurrentHashMap<>();

    /*
     * Init Verifiable Achieves Object
     */
    static {
        ACHIEVES.put(VerifiableJson.class, new VerifiableJsonAchieve());
        ACHIEVES.put(VerifiableRange.class, new VerifiableRangeAchieve());
        ACHIEVES.put(VerifiableLength.class, new VerifiableLengthAchieve());
        ACHIEVES.put(VerifiableNotNull.class, new VerifiableNotNullAchieve());
        ACHIEVES.put(VerifiableRegular.class, new VerifiableRegularAchieve());
        ACHIEVES.put(VerifiableBetween.class, new VerifiableBetweenAchieve());
    }

    /**
     * Execute Verifiable
     *
     * @param data Verifiable Data Object
     * @return Execute Verifiable Result Object
     */
    public static boolean execute(Object data) {
        if (data == null) {
            return false;
        } else {
            for (final Field field : data.getClass().getDeclaredFields()) {
                if (!execute(field, data)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Execute Verifiable Field
     *
     * @param field Verifiable Field Data Object
     * @param data  Verifiable Data Object
     * @return Execute Verifiable Result Object
     */
    private static boolean execute(Field field, Object data) {
        if (field != null) {
            for (final Class<? extends Annotation> annotation : ACHIEVES.keySet()) {
                if (field.isAnnotationPresent(annotation)
                        && !ACHIEVES.get(annotation).execute(field.getAnnotation(annotation), field, data)) {
                    return false;
                }
            }
        }
        return true;
    }

}
