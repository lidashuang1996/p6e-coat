package club.p6e.coat.common.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Snowflake ID Util
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public final class SnowflakeIdUtil {

    /**
     * Definition
     */
    public abstract static class Definition {

        /**
         * Worker ID
         */
        protected final long workerId;

        /**
         * Data Center ID
         */
        protected final long datacenterId;

        /**
         * Constructor Initialization
         *
         * @param workerId     Worker ID
         * @param datacenterId Data Center ID
         */
        public Definition(long workerId, long datacenterId) {
            this.workerId = workerId;
            this.datacenterId = datacenterId;
        }

    }

    /**
     * Implementation
     */
    public static class Implementation extends Definition {

        /**
         * Starting Timestamp
         * 2022/01/01 00:00:00
         */
        private final static long STARTED = 1640966400000L;

        /**
         * BITS
         */
        private final static long WORKER_ID_BITS = 5L;
        private final static long DATACENTER_ID_BITS = 5L;
        private final static long SEQUENCE_BITS = 10L;

        /**
         * MAX
         */
        private final static long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
        private final static long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
        private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

        /**
         * SHIFT
         */
        private final static long WORKER_ID_SHIFT = SEQUENCE_BITS;
        private final static long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
        private final static long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

        /**
         * Sequence
         */
        private long sequence = 0L;

        /**
         * Last Timestamp
         */
        private long lastTimestamp = -1L;

        /**
         * Constructor Initialization
         *
         * @param workerId     Worker ID
         * @param datacenterId Data Center ID
         */
        public Implementation(long workerId, long datacenterId) {
            super(workerId, datacenterId);
            if (workerId > MAX_WORKER_ID || workerId < 0) {
                throw new IllegalArgumentException(
                        String.format("worker Id can't be greater than %d or less than 0", MAX_WORKER_ID));
            }
            if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
                throw new IllegalArgumentException(
                        String.format("datacenter Id can't be greater than %d or less than 0", MAX_DATACENTER_ID));
            }
        }

        /**
         * Next ID
         *
         * @return ID
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
                sequence = 0L;
            }
            lastTimestamp = timestamp;
            sequence = sequence << 2 | (ThreadLocalRandom.current().nextLong(0, 4));
            return (timestamp - STARTED) << TIMESTAMP_SHIFT
                    | datacenterId << DATACENTER_ID_SHIFT
                    | workerId << WORKER_ID_SHIFT
                    | sequence;
        }

        /**
         * Til Next Millis
         *
         * @return Til Next Millis
         */
        private long tilNextMillis() {
            long timestamp = timeGen();
            while (timestamp <= lastTimestamp) {
                timestamp = timeGen();
            }
            return timestamp;
        }

        /**
         * Time Gen
         *
         * @return Timestamp
         */
        private long timeGen() {
            return System.currentTimeMillis();
        }

    }

    /**
     * Definition
     * Worker ID - 0
     * Data Center ID - 0
     */
    private static Definition DEFINITION = new Implementation(0, 0);

    /**
     * Cache Snowflake ID Util Object
     */
    private static final Map<String, Definition> DEFINITION_CACHE = new ConcurrentHashMap<>();

    /**
     * Set Definition Implementation Object
     *
     * @param implementation Definition Implementation Object
     */
    public static void set(Definition implementation) {
        DEFINITION = implementation;
    }

    /**
     * Register
     *
     * @param name           Name
     * @param implementation Definition Implementation Object
     */
    public static void register(String name, Definition implementation) {
        DEFINITION_CACHE.put(name, implementation);
    }

    /**
     * Get Definition
     *
     * @return Definition Object
     */
    public static Definition get() {
        return DEFINITION;
    }

    /**
     * Get Cache Register Definition
     *
     * @param name Cache Name
     * @return Definition Object
     */
    public static Definition get(String name) {
        return DEFINITION_CACHE.get(name);
    }

}
