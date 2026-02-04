package club.p6e.coat.common;

import club.p6e.coat.common.controller.WebExecuteErrorConfig;
import club.p6e.coat.common.utils.SnowflakeIdUtil;
import club.p6e.coat.common.utils.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Spring Boot 初始化之后钩子函数
 *
 * @author lidashuang
 * @version 1.0
 */
@Order(0)
@Component(value = "club.p6e.coat.common.ApplicationRunner")
public class ApplicationRunner implements CommandLineRunner {

    /**
     * 注入日志对象
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(ApplicationRunner.class);

    /**
     * ApplicationContext object
     */
    private final ApplicationContext context;

    /**
     * 构造方法初始化
     *
     * @param context ApplicationContext object
     */
    public ApplicationRunner(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void run(String... args) {
        SpringUtil.init(context);
        WebExecuteErrorConfig.init();
        final Properties properties = SpringUtil.getBean(Properties.class);
        for (final String name : properties.getSnowflake().keySet()) {
            final Properties.Snowflake snowflake = properties.getSnowflake().get(name);
            SnowflakeIdUtil.register(name, snowflake.getWorkerId(), snowflake.getDataCenterId());
            LOGGER.info("P6E INIT SNOWFLAKE [ WORKER ID: {}, DATACENTER ID: {} ] ==> {}", snowflake.getWorkerId(), snowflake.getDataCenterId(), name);
        }
    }

}
