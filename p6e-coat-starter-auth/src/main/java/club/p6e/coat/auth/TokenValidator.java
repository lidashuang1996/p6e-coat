package club.p6e.coat.auth;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface TokenValidator<C, R> {

    R execute(C context);

}
