package club.p6e.coat.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
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
     * Slot Number
     */
    private static int SLOTS_NUM = 15;

    /**
     * Thread Pool Object
     */
    private static ScheduledThreadPoolExecutor EXECUTOR = null;

    /**
     * Init
     *
     * @param num Thread Pool Length
     */
    public synchronized static void init(int num) {
        num = num < 0 ? 15 : num;
        if (EXECUTOR != null && SLOTS_NUM != num) {
            EXECUTOR.shutdown();
            EXECUTOR = null;
        }
        if (EXECUTOR == null) {
            EXECUTOR = new ScheduledThreadPoolExecutor(num, r ->
                    new Thread(r, "P6E-WS-SESSION-MANAGER-THREAD-" + r.hashCode()));
            SLOTS_NUM = num;
        }
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
     * Register Session Object
     *
     * @param id      Session ID
     * @param session Session Object
     */
    public static void register(String id, Session session) {
        synchronized (SessionManager.class) {
            SESSIONS.put(id, session);
            final String name = session.getChannelName();
            final String index = String.valueOf(Math.abs(id.hashCode() % SLOTS_NUM));
            SLOTS.computeIfAbsent(index, k -> new ConcurrentHashMap<>()).put(id, session);
            CHANNELS.computeIfAbsent(name, k -> new ConcurrentHashMap<>()).put(id, session);
            LOGGER.info("[ SESSION MANAGER ] REGISTER => {}%{}={} >>> {}", id, SLOTS_NUM, index, SLOTS.get(index));
        }
    }

    /**
     * Unregister Session Object
     *
     * @param id Session ID
     */
    public static void unregister(String id) {
        synchronized (SessionManager.class) {
            final Session session = SESSIONS.get(id);
            if (session != null) {
                SESSIONS.remove(id);
                final String index = String.valueOf(Math.abs(id.hashCode() % SLOTS_NUM));
                final Map<String, Session> slot = SLOTS.get(index);
                if (slot != null) {
                    slot.remove(id);
                }
                final Map<String, Session> channel = CHANNELS.get(session.getChannelName());
                if (channel != null) {
                    channel.remove(id);
                }
                LOGGER.info("[ SESSION MANAGER ] UNREGISTER => {} % {} = {} >>> {}", id, SLOTS_NUM, index, SLOTS.get(index));
            }
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
     * Get Slot List Object
     *
     * @return Slot List Object
     */
    private static List<List<Session>> getSlotList() {
        final List<List<Session>> list = new ArrayList<>();
        SLOTS.forEach((k, v) -> {
            if (!v.isEmpty()) {
                list.add(new ArrayList<>(v.values()));
            }
        });
        return list;
    }

    /**
     * Get Channel List Object
     *
     * @return Channel List Object
     */
    @SuppressWarnings("ALL")
    public static List<Session> getChannelList(String name) {
        if (CHANNELS.get(name) == null) {
            return new ArrayList<>();
        } else {
            return new ArrayList<>(CHANNELS.get(name).values());
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
        final List<List<Session>> list = getSlotList();
        for (final List<Session> slot : list) {
            if (slot != null && !slot.isEmpty()) {
                LOGGER.info("[ PUSH BINARY SESSION CHANNEL ] {} >>> {}", name, Collections.singletonList(bytes));
                submit(slot, filter, name, bytes);
            }
        }
    }

    /**
     * Push Text Message
     *
     * @param filter  Filter Object
     * @param name    Channel Name
     * @param content Message Content
     */
    public static void pushText(Function<User, Boolean> filter, String name, String content) {
        final List<List<Session>> list = getSlotList();
        for (final List<Session> slot : list) {
            if (slot != null && !slot.isEmpty()) {
                LOGGER.info("[ PUSH TEXT SESSION CHANNEL ] {} >>> {}", name, content);
                submit(slot, filter, name, content);
            }
        }
    }

    /**
     * Submit Push Message Task
     *
     * @param slot    Slot Object
     * @param filter  Filter Object
     * @param name    Channel Name
     * @param content Content Data
     */
    private static void submit(List<Session> slot, Function<User, Boolean> filter, String name, Object content) {
        EXECUTOR.submit(() -> {
            for (final Session session : slot) {
                LOGGER.info("[ SUBMIT TASK EXECUTE ] >>> NAME CHECK >>> CHANNEL NAME: {}/SESSION CHANNEL NAME: {} ? {}", name, session.getChannelName(), name.equalsIgnoreCase(session.getChannelName()));
                if (filter != null && name.equalsIgnoreCase(session.getChannelName())) {
                    final Boolean result = filter.apply(session.getUser());
                    LOGGER.info("[ SUBMIT TASK EXECUTE ] >>> FILTER RESULT >>> {}", result);
                    if (result != null && result) {
                        session.push(content);
                    }
                }
            }
        });
    }

}
