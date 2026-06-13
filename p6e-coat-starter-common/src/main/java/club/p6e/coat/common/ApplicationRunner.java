package club.p6e.coat.common;

import club.p6e.coat.common.controller.WebExceptionExecuteConfig;
import club.p6e.coat.common.utils.SnowflakeIdUtil;
import club.p6e.coat.common.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Application Runner
 *
 * @author lidashuang
 * @version 1.0
 */
@Slf4j
@Order(0)
@Component(value = "club.p6e.coat.common.ApplicationRunner")
public class ApplicationRunner implements CommandLineRunner {

    /**
     * Application Context Object
     */
    private final ApplicationContext context;

    /**
     * Constructor Initialization
     *
     * @param context Application Context Object
     */
    public ApplicationRunner(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void run(String @NonNull ... args) {
        SpringUtil.init(this.context);
        WebExceptionExecuteConfig.init();
        final Properties properties = SpringUtil.getBean(Properties.class);
        for (final String name : properties.getSnowflake().keySet()) {
            final Properties.Snowflake snowflake = properties.getSnowflake().get(name);
            SnowflakeIdUtil.register(name, new SnowflakeIdUtil.Implementation(snowflake.getWorkerId(), snowflake.getDataCenterId()));
            log.info("SNOWFLAKE ({}) ==> [ WORKER ID: {}, DATACENTER ID: {} ]", name, snowflake.getWorkerId(), snowflake.getDataCenterId());
        }
    }

}
