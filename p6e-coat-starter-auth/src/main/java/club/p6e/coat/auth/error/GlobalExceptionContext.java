package club.p6e.coat.auth.error;

import club.p6e.coat.common.error.ParameterException;

/**
 * Global Exception Context
 *
 * @author lidashuang
 * @version 1.0
 */
public final class GlobalExceptionContext {

    public static CodecException executeRasException(Class<?> sc, String error, String content) {
        return new CodecException(sc, error, content);
    }

    public static AuthException exceptionAuthException(Class<?> sc, String error, String content) {
        return new AuthException(sc, error, content);
    }

    public static BeanException exceptionBeanException(Class<?> sc, String error, String content) {
        return new BeanException(sc, error, content);
    }

    public static CacheException executeCacheException(Class<?> sc, String error, String content) {
        return new CacheException(sc, error, content);
    }

    public static VoucherException executeVoucherException(Class<?> sc, String error, String content) {
        return new VoucherException(sc, error, content);
    }

    public static ParameterException executeParameterException(Class<?> sc, String error, String content) {
        return new ParameterException(sc, error, content);
    }

    public static QrCodeDataNullException executeQrCodeDataNullException(Class<?> sc, String error, String content) {
        return new QrCodeDataNullException(sc, error, content);
    }

    public static UserException executeUserNoExistException(Class<?> sc, String error, String content) {
        return new UserException(sc, error, content);
    }

    public static ServiceNoEnabledException exceptionServiceNoEnabledException(Class<?> sc, String error, String content) {
        return new ServiceNoEnabledException(sc, error, content);
    }

    public static AccountPasswordLoginTransmissionException exceptionAccountPasswordLoginTransmissionException(Class<?> sc, String error, String content) {
        return new AccountPasswordLoginTransmissionException(sc, error, content);
    }

    public static AccountPasswordLoginAccountOrPasswordException exceptionAccountPasswordLoginAccountOrPasswordException(Class<?> sc, String error, String content) {
        return new AccountPasswordLoginAccountOrPasswordException(sc, error, content);
    }

    public static AccountException exceptionAccountException(Class<?> sc, String error, String content) {
        return new AccountException(sc, error, content);
    }

    public static AccountException exceptionAccountExistException(Class<?> sc, String error, String content) {
        return new AccountException(sc, error, content);
    }

    public static AccountException exceptionAccountNoExistException(Class<?> sc, String error, String content) {
        return new AccountException(sc, error, content);
    }

    public static CodecException exceptionPasswordEncryptorException(Class<?> sc, String error, String content) {
        return new CodecException(sc, error, content);
    }

    public static DataBaseException exceptionDataBaseException(Class<?> sc, String error, String content) {
        return new DataBaseException(sc, error, content);
    }

}
