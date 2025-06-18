package club.p6e.coat.common.utils;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 转换帮助类
 *
 * @author lidashuang
 * @version 1.0
 */
public final class TransformationUtil {

    public static Integer objectToInteger(Object o) {
        if (o == null) {
            return null;
        } else {
            try {
                return o instanceof Integer ? (Integer) o : Double.valueOf(String.valueOf(o)).intValue();
            } catch (Exception e) {
                return null;
            }
        }
    }

    public static Integer objectToInteger(Object o, int def) {
        final Integer result = objectToInteger(o);
        return result == null ? def : result;
    }

    public static Long objectToLong(Object o) {
        if (o == null) {
            return null;
        } else {
            try {
                return o instanceof Long ? (Long) o : Double.valueOf(String.valueOf(o)).longValue();
            } catch (Exception e) {
                return null;
            }
        }
    }

    public static Long objectToLong(Object o, long def) {
        final Long result = objectToLong(o);
        return result == null ? def : result;
    }

    public static Double objectToDouble(Object o) {
        if (o == null) {
            return null;
        } else {
            try {
                return o instanceof Double ? (Double) o : Double.valueOf(String.valueOf(o));
            } catch (Exception e) {
                return null;
            }
        }
    }

    public static Double objectToDouble(Object o, double def) {
        final Double result = objectToDouble(o);
        return result == null ? def : result;
    }

    public static Float objectToFloat(Object o) {
        if (o == null) {
            return null;
        } else {
            try {
                return o instanceof Float ? (Float) o : Double.valueOf(String.valueOf(o)).floatValue();
            } catch (Exception e) {
                return null;
            }
        }
    }

    public static Float objectToFloat(Object o, float def) {
        final Float result = objectToFloat(o);
        return result == null ? def : result;
    }

    public static String objectToString(Object o) {
        if (o == null) {
            return null;
        } else {
            try {
                return o instanceof String ? (String) o : String.valueOf(o);
            } catch (Exception e) {
                return null;
            }
        }
    }

    public static String objectToString(Object o, String def) {
        final String result = objectToString(o);
        return result == null ? def : result;
    }

    public static LocalDateTime objectToLocalDateTime(Object o) {
        if (o instanceof Timestamp) {
            return ((Timestamp) o).toLocalDateTime();
        } else if (o instanceof LocalDateTime) {
            return (LocalDateTime) o;
        } else {
            return null;
        }
    }

    public static LocalDateTime objectToLocalDateTime(Object o, LocalDateTime def) {
        final LocalDateTime result = objectToLocalDateTime(o);
        return result == null ? def : result;
    }

    public static LocalDate objectToLocalDate(Object o) {
        if (o instanceof Date) {
            return ((Date) o).toLocalDate();
        } else if (o instanceof LocalDate) {
            return (LocalDate) o;
        } else {
            return null;
        }
    }

    public static LocalDate objectToLocalDate(Object o, LocalDate def) {
        final LocalDate result = objectToLocalDate(o);
        return result == null ? def : result;
    }


    public static Boolean objectToBoolean(Object o) {
        if (o == null) {
            return null;
        } else {
            try {
                return "true".equalsIgnoreCase(String.valueOf(o));
            } catch (Exception e) {
                return null;
            }
        }
    }

    public static Boolean objectToBoolean(Object o, Boolean def) {
        final Boolean result = objectToBoolean(o);
        return result == null ? def : result;
    }

    @SuppressWarnings("ALL")
    public static Map<String, Object> objectToMap(Object o) {
        if (o == null) {
            return null;
        } else {
            return (Map<String, Object>) o;
        }
    }

    @SuppressWarnings("ALL")
    public static List<Object> objectToList(Object o) {
        if (o == null) {
            return null;
        } else {
            return (List<Object>) o;
        }
    }


    public static LocalDate dateToLocalDate(Date date) {
        if (date == null) {
            return null;
        } else {
            return date.toLocalDate();
        }
    }

    public static LocalDate dateToLocalDate(Date date, LocalDate def) {
        final LocalDate result = dateToLocalDate(date);
        return result == null ? def : result;
    }

    public static LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        } else {
            return timestamp.toLocalDateTime();
        }
    }

    public static LocalDateTime timestampToLocalDateTime(Timestamp timestamp, LocalDateTime def) {
        final LocalDateTime result = timestampToLocalDateTime(timestamp);
        return result == null ? def : result;
    }

}