package club.p6e.coat.common;

import club.p6e.coat.common.controller.WebExceptionExecuteConfig;
import club.p6e.coat.common.utils.SnowflakeIdUtil;
import club.p6e.coat.common.utils.SpringUtil;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Order(0)
@Component(value = "club.p6e.coat.common.ApplicationRunner")
public class ApplicationRunner implements CommandLineRunner {

    /**
     * Inject Log Object
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(ApplicationRunner.class);

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
        SpringUtil.init(context);
        WebExceptionExecuteConfig.init();
        final Properties properties = SpringUtil.getBean(Properties.class);
        for (final String name : properties.getSnowflake().keySet()) {
            final Properties.Snowflake snowflake = properties.getSnowflake().get(name);
            SnowflakeIdUtil.register(name, snowflake.getWorkerId(), snowflake.getDataCenterId());
            LOGGER.info("P6E COMMON INIT SNOWFLAKE [ WORKER ID: {}, DATACENTER ID: {} ] ==> {}", snowflake.getWorkerId(), snowflake.getDataCenterId(), name);
        }
    }

}
