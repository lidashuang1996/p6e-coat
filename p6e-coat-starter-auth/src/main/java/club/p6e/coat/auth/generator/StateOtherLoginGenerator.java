package club.p6e.coat.auth.generator;

/**
 * 第三方登录的 STATE 生成器
 *
 * @author lidashuang
 * @version 1.0
 */
public interface StateOtherLoginGenerator {

    /**
     * 生成 STATE
     *
     * @return STATE 内容
     */
    String execute();

}
