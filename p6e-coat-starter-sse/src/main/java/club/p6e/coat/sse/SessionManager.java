package club.p6e.coat.sse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Session Manager
 *
 * @author lidashuang
 * @version 1.0
 */
public class SessionManager {

    /**
     * Inject Log Object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionManager.class);

    /**
     * Session Cache Object
     */
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    /**
     * Slot Cache Object
     */
    private static final Map<String, Map<String, Session>> SLOTS = new ConcurrentHashMap<>();

    /**
     * Channel Name Cache Object
     */
    private static final Map<String, Map<String, Session>> CHANNELS = new ConcurrentHashMap<>();

    /**
     * Executor
     */
    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(3, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());

    /**
     * Slot Number
     */
    private static int SLOT_NUM = 15;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            EXECUTOR.shutdown();
            try {
                if (!EXECUTOR.awaitTermination(10, TimeUnit.SECONDS)) {
                    EXECUTOR.shutdownNow();
                }
            } catch (Exception e) {
                EXECUTOR.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }));
    }

    /**
     * Init
     *
     * @param num Thread Pool Length
     */
    public synchronized static void init(int num) {
        SLOT_NUM = num < 0 ? 15 : num;
        SLOTS.clear();
        SESSIONS.clear();
        CHANNELS.clear();
    }

    /**
     * Register Session Object
     *
     * [P2] 性能优化: 移除类级别 synchronized 锁，
     *      ConcurrentHashMap 的 put/computeIfAbsent 已保证原子性
     *
     * @param id      Session ID
     * @param session Session Object
     */
    public static void register(String id, Session session) {
        SESSIONS.put(id, session);
        final String name = session.getChannelName();
        final String index = String.valueOf(Math.abs(id.hashCode() % SLOT_NUM));
        SLOTS.computeIfAbsent(index, _ -> new ConcurrentHashMap<>()).put(id, session);
        CHANNELS.computeIfAbsent(name, _ -> new ConcurrentHashMap<>()).put(id, session);
        LOGGER.info("[ SESSION MANAGER ] REGISTER => {}({})%{}={} >>> {}", id, id.hashCode(), SLOT_NUM, index, SLOTS.get(index));
    }

    /**
     * Unregister Session Object
     *
     * [P2] 性能优化: 用 SESSIONS.remove() 原子返回替代同步块
     *
     * @param id Session ID
     */
    public static void unregister(String id) {
        final Session session = SESSIONS.remove(id);
        if (session != null) {
            final String index = String.valueOf(Math.abs(id.hashCode() % SLOT_NUM));
            final Map<String, Session> slot = SLOTS.get(index);
            if (slot != null) {
                slot.remove(id);
            }
            final Map<String, Session> channel = CHANNELS.get(session.getChannelName());
            if (channel != null) {
                channel.remove(id);
            }
            LOGGER.info("[ SESSION MANAGER ] UNREGISTER => {}({})%{}={} >>> {}", id, id.hashCode(), SLOT_NUM, index, SLOTS.get(index));
        }
    }

    /**
     * Get Session Object
     *
     * @param id Session ID
     * @return Session Object
     */
    public static Session get(String id) {
        return SESSIONS.get(id);
    }

    /**
     * Get All Session Object
     *
     * @return Session List Object
     */
    @SuppressWarnings("ALL")
    public static List<Session> all() {
        return new ArrayList<>(SESSIONS.values());
    }

    /**
     * Get All Session Object
     *
     * @return Session Keys
     */
    @SuppressWarnings("ALL")
    public static Set<String> keys() {
        return SESSIONS.keySet();
    }

    /**
     * Get Channel List Object
     *
     * @return Channel List Object
     */
    @SuppressWarnings("ALL")
    public static List<Session> getChannelList(String name) {
        final Map<String, Session> channel = CHANNELS.get(name);
        return channel == null ? Collections.emptyList() : new ArrayList<>(channel.values());
    }

    /**
     * Push Binary Message
     *
     * [P2] 性能优化: 单次提交替代逐 slot 提交，避免线程泛滥
     *
     * @param filter Filter Object
     * @param name   Channel Name
     * @param bytes  Message Content
     */
    public static void pushBinary(Function<User, Boolean> filter, String name, byte[] bytes) {
        LOGGER.info("[ PUSH BINARY SESSION CHANNEL ] {} >>> {}", name, Collections.singletonList(bytes));
        EXECUTOR.submit(() -> {
            for (final Session session : SESSIONS.values()) {
                if (name.equalsIgnoreCase(session.getChannelName()) && filter != null) {
                    final Boolean result = filter.apply(session.getUser());
                    if (result != null && result) {
                        session.push(bytes);
                    }
                }
            }
        });
    }

    /**
     * Push Text Message
     *
     * [P2] 性能优化: 单次提交替代逐 slot 提交，避免线程泛滥
     *
     * @param filter  Filter Object
     * @param name    Channel Name
     * @param content Message Content
     */
    public static void pushText(Function<User, Boolean> filter, String name, String content) {
        LOGGER.info("[ PUSH TEXT SESSION CHANNEL ] {} >>> {}", name, content);
        EXECUTOR.submit(() -> {
            for (final Session session : SESSIONS.values()) {
                if (name.equalsIgnoreCase(session.getChannelName()) && filter != null) {
                    final Boolean result = filter.apply(session.getUser());
                    if (result != null && result) {
                        session.push(content);
                    }
                }
            }
        });
    }

    /**
     * Submit Push Message Task
     *
     * @param sessions Session List Object
     * @param filter   Filter Object
     * @param name     Channel Name
     * @param content  Content Data
     *
     * @deprecated [P2] 性能优化: 由 pushText/pushBinary 内联替代
     */
    @Deprecated
    @SuppressWarnings("ALL")
    private static void submit(List<Session> sessions, Function<User, Boolean> filter, String name, Object content) {
    }

}