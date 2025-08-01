package club.p6e.coat.message.center;

import club.p6e.coat.common.utils.SnowflakeIdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * Message Center Snowflake
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = MessageCenterSnowflake.class,
        ignored = MessageCenterSnowflake.class
)
public class MessageCenterSnowflake {

    /**
     * Snowflake Name
     */
    public static final String SNOWFLAKE_NAME = "MESSAGE_CENTER_LOG_SNOWFLAKE";

    /**
     * Inject Log Object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageCenterSnowflake.class);

    /**
     * Construct Initialization
     */
    public MessageCenterSnowflake() {
        SnowflakeIdUtil.register(SNOWFLAKE_NAME, 0, 0);
        LOGGER.info("[ MESSAGE CENTER ] SNOWFLAKE >>> ( {} ) WORKER ID: {}, DATACENTER ID: {}", SNOWFLAKE_NAME, 0, 0);
    }

    /**
     * Get Next ID
     *
     * @return Snowflake ID
     */
    public long getNextId(String name) {
        return SnowflakeIdUtil.getInstance(name).nextId();
    }

}
