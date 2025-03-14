package club.p6e.coat.auth;

import club.p6e.coat.auth.web.reactive.ServerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author lidashuang
 * @version 1.0.0
 */
@SpringBootApplication
public class P6eAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(P6eAuthApplication.class, args);
        ServerConfig.init("66666666666666666");
    }

}
