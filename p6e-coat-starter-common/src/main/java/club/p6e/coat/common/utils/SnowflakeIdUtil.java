package club.p6e.coat.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 雪花算法的帮助类
 *
 * @author lidashuang
 * @version 1.0
 */
public final class SnowflakeIdUtil {

    /**
     * 单例
     * 机器ID
     * 数据中心ID
     */
    private static final SnowflakeIdUtil INSTANCE = new SnowflakeIdUtil(0, 0);

    /**
     * 实例的缓存
     */
    private static final Map<String, SnowflakeIdUtil> INSTANCE_CACHE = new HashMap<>(16);

    /**
     * 起始的时间戳
     * 2022/01/01 00:00:00
     */
    private final static long STARTED = 1640966400000L;

    /**
     * 每一部分占用的位数
     */
    private final static long WORKER_ID_BITS = 5L;
    private final static long DATACENTER_ID_BITS = 5L;
    private final static long SEQUENCE_BITS = 12L;

    /**
     * 每一部分的最大值
     */
    private final static long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private final static long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    /**
     * 每一部分向左的位移
     */
    private final static long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private final static long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private final static long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    /**
     * 机器ID
     */
    private final long workerId;
    /**
     * 数据中心ID
     */
    private final long datacenterId;
    /**
     * 序列号
     */
    private long sequence = 0L;
    /**
     * 上一次时间戳
     */
    private long lastTimestamp = -1L;

    /**
     * 获取类
     */
    public static SnowflakeIdUtil getInstance() {
        return INSTANCE;
    }

    /**
     * 获取注册的类的实例
     *
     * @param name 名称
     * @return 类的实例
     */
    public static SnowflakeIdUtil getInstance(String name) {
        return INSTANCE_CACHE.get(name);
    }

    /**
     * 注册
     *
     * @param name         名称
     * @param workerId     机器ID
     * @param datacenterId 数据中心ID
     */
    public static void register(String name, long workerId, long datacenterId) {
        INSTANCE_CACHE.put(name, new SnowflakeIdUtil(workerId, datacenterId));
    }

    /**
     * 私有化构造方法
     *
     * @param workerId     机器ID
     * @param datacenterId 数据中心ID
     */
    public SnowflakeIdUtil(long workerId, long datacenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("worker Id can't be greater than %d or less than 0", MAX_WORKER_ID));
        }
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException(
                    String.format("datacenter Id can't be greater than %d or less than 0", MAX_DATACENTER_ID));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    /**
     * 获取 ID
     *
     * @return 生成的 ID
     */
    public synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format(
                    "Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0L) {
                timestamp = tilNextMillis();
            }
        } else {
            sequence = (sequence % 2 == 0 ? sequence + 1 : sequence);
        }
        lastTimestamp = timestamp;

        return
                // 时间戳部分
                (timestamp - STARTED) << TIMESTAMP_SHIFT
                        // 数据中心部分
                        | datacenterId << DATACENTER_ID_SHIFT
                        // 机器标识部分
                        | workerId << WORKER_ID_SHIFT
                        // 序列号部分
                        | sequence;
    }

    /**
     * 等带获取下一个时间戳
     *
     * @return 下一个时间戳时间
     */
    private long tilNextMillis() {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 获取当前时间戳
     *
     * @return 获取时间戳时间
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }
}
