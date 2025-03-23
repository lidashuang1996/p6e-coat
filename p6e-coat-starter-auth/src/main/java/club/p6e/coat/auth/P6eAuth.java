package club.p6e.coat.auth;

import club.p6e.coat.auth.web.reactive.ServerConfig;

/**
 * @author lidashuang
 * @version 1.0
 */
public final class P6eAuth {

    private static final Properties PROPERTIES = Properties.getInstance();

    public Properties getProperties() {
        return PROPERTIES;
    }

    public static void init() {
        ServerConfig.init();
    }

}
