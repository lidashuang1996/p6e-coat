package club.p6e.coat.common.utils;

import club.p6e.coat.environment.EnvironmentContext;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

public final class RepositoryUtil {

    /**
     * OID
     */
    private static final String OID = "oid";

    /**
     * PID
     */
    private static final String PID = "pid";

    /**
     * Create
     */
    private static final String CREATOR = "creator";

    /**
     * Modifier
     */
    private static final String MODIFIER = "modifier";

    /**
     * Creation Date Time
     */
    private static final String CREATION_DATE_TIME1 = "creationDateTime";

    /**
     * Creation Date Time
     */
    private static final String CREATION_DATE_TIME2 = "creation_date_time";

    /**
     * Modification Date Time
     */
    private static final String MODIFICATION_DATE_TIME1 = "modificationDateTime";

    /**
     * Modification Date Time
     */
    private static final String MODIFICATION_DATE_TIME2 = "modification_date_time";

    /**
     * Version
     */
    private static final String VERSION = "version";

    /**
     * Is Deleted
     */
    private static final String IS_DELETED = "isDeleted";

    public static void injectEnvironmentToCreateModel(EnvironmentContext environment, Object model) {
        final Field[] fields = model.getClass().getDeclaredFields();
        for (final Field field : fields) {
            try {
                final String name = field.getName();
                switch (name) {
                    case PID -> {
                        field.setAccessible(true);
                        if (field.get(model) == null) {
                            field.set(model, environment.getProject().id());
                        }
                    }
                    case OID -> {
                        field.setAccessible(true);
                        if (field.get(model) == null) {
                            field.set(model, environment.getOrganization().id());
                        }
                    }
                    case CREATOR, MODIFIER -> {
                        field.setAccessible(true);
                        if (field.get(model) == null) {
                            final int uid = environment.getUser() == null ? 0 : environment.getUser().id() == null ? 0 : environment.getUser().id();
                            field.set(model, (uid == 0) ? "sys" : String.valueOf(uid));
                        }
                    }
                    case VERSION, IS_DELETED -> {
                        field.setAccessible(true);
                        if (field.get(model) == null) {
                            field.set(model, 0);
                        }
                    }
                    case CREATION_DATE_TIME1,
                         CREATION_DATE_TIME2,
                         MODIFICATION_DATE_TIME1,
                         MODIFICATION_DATE_TIME2 -> {
                        field.setAccessible(true);
                        if (field.get(model) == null) {
                            field.set(model, LocalDateTime.now());
                        }
                    }
                    default -> {
                    }
                }
            } catch (Exception _) {
                // ignore exception
            }
        }
    }

    public static void injectEnvironmentToUpdateModel(EnvironmentContext environment, Object model) {
        final Field[] fields = model.getClass().getDeclaredFields();
        for (final Field field : fields) {
            try {
                final String name = field.getName();
                switch (name) {
                    case MODIFIER -> {
                        field.setAccessible(true);
                        if (field.get(model) == null) {
                            final int uid = environment.getUser() == null ? 0 : environment.getUser().id() == null ? 0 : environment.getUser().id();
                            field.set(model, (uid == 0) ? "sys" : String.valueOf(uid));
                        }
                    }
                    case MODIFICATION_DATE_TIME1,
                         MODIFICATION_DATE_TIME2 -> {
                        field.setAccessible(true);
                        if (field.get(model) == null) {
                            field.set(model, LocalDateTime.now());
                        }
                    }
                    default -> {
                    }
                }
            } catch (Exception _) {
                // ignore exception
            }
        }
    }

}
