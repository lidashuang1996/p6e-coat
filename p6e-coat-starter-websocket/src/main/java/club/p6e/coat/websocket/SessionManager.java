package club.p6e.coat.websocket;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Session Manager
 *
 * @author lidashuang
 * @version 1.0
 */
@Slf4j
public class SessionManager {

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
    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(3, 30, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());

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
     * @param id      Session ID
     * @param session Session Object
     */
    public static void register(String id, Session session) {
        SESSIONS.put(id, session);
        final String name = session.getChannelName();
        final String index = String.valueOf(Math.abs(id.hashCode() % SLOT_NUM));
        SLOTS.computeIfAbsent(index, _ -> new ConcurrentHashMap<>()).put(id, session);
        CHANNELS.computeIfAbsent(name, _ -> new ConcurrentHashMap<>()).put(id, session);
        log.info("[ SESSION MANAGER ] REGISTER => {}({})%{}={}", id, id.hashCode(), SLOT_NUM, index);
    }

    /**
     * Unregister Session Object
     *
     * @param id Session ID
     */
    public static void unregister(String id) {
        final Session session = SESSIONS.get(id);
        if (session != null) {
            SESSIONS.remove(id);
            final String index = String.valueOf(Math.abs(id.hashCode() % SLOT_NUM));
            final Map<String, Session> slot = SLOTS.get(index);
            if (slot != null) {
                slot.remove(id);
            }
            final Map<String, Session> channel = CHANNELS.get(session.getChannelName());
            if (channel != null) {
                channel.remove(id);
            }
            log.info("[ SESSION MANAGER ] UNREGISTER => {}({})%{}={}", id, id.hashCode(), SLOT_NUM, index);
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
    public static List<Session> all() {
        return new ArrayList<>(SESSIONS.values());
    }

    /**
     * Get All Session Object
     *
     * @return Session Keys
     */
    public static Set<String> keys() {
        return SESSIONS.keySet();
    }

    /**
     * For Each Session In Channel
     *
     * @param name     Channel Name
     * @param consumer Consumer Object
     */
    public static void forEachSessionInChannel(String name, Consumer<Session> consumer) {
        final Map<String, Session> channel = CHANNELS.get(name);
        if (channel != null) {
            channel.values().forEach(consumer);
        }
    }

    /**
     * Push Binary Message
     *
     * @param filter Filter Object
     * @param name   Channel Name
     * @param bytes  Message Content
     */
    public static void pushBinary(Function<User, Boolean> filter, String name, byte[] bytes) {
        SLOTS.forEach((_, sessions) -> {
            if (!sessions.isEmpty()) {
                submit(sessions, filter, name, bytes);
            }
        });
    }

    /**
     * Push Text Message
     *
     * @param filter  Filter Object
     * @param name    Channel Name
     * @param content Message Content
     */
    public static void pushText(Function<User, Boolean> filter, String name, String content) {
        SLOTS.forEach((_, sessions) -> {
            if (!sessions.isEmpty()) {
                submit(sessions, filter, name, content);
            }
        });
    }

    /**
     * Submit Push Message Task
     *
     * @param sessions Session Map Object
     * @param filter   Filter Object
     * @param name     Channel Name
     * @param content  Content Data
     */
    private static void submit(Map<String, Session> sessions, Function<User, Boolean> filter, String name, Object content) {
        EXECUTOR.submit(() -> {
            for (final Session session : sessions.values()) {
                if (filter != null && name.equalsIgnoreCase(session.getChannelName())) {
                    final Boolean result = filter.apply(session.getUser());
                    if (result != null && result) {
                        session.push(content);
                    }
                }
            }
        });
    }

}
