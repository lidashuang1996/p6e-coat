package club.p6e.coat.sse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
    private static int SLOT_NUM = 15;

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
        synchronized (SessionManager.class) {
            SESSIONS.put(id, session);
            final String name = session.getChannelName();
            final String index = String.valueOf(Math.abs(id.hashCode() % SLOT_NUM));
            SLOTS.computeIfAbsent(index, _ -> new ConcurrentHashMap<>()).put(id, session);
            CHANNELS.computeIfAbsent(name, _ -> new ConcurrentHashMap<>()).put(id, session);
            LOGGER.info("[ SESSION MANAGER ] REGISTER => {}({})%{}={} >>> {}", id, id.hashCode(), SLOT_NUM, index, SLOTS.get(index));
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
    private static Map<String, List<Session>> getSlotList() {
        final Map<String, List<Session>> result = new HashMap<>();
        SLOTS.forEach((k, v) -> {
            if (!v.isEmpty()) {
                result.put(k, List.copyOf(v.values()));
            }
        });
        return result;
    }

    /**
     * Get Channel List Object
     *
     * @return Channel List Object
     */
    @SuppressWarnings("ALL")
    public static List<Session> getChannelList(String name) {
        return new ArrayList<>(CHANNELS.get(name).values());
    }

    /**
     * Push Binary Message
     *
     * @param filter Filter Object
     * @param name   Channel Name
     * @param bytes  Message Content
     */
    public static void pushBinary(Function<User, Boolean> filter, String name, byte[] bytes) {
        final Map<String, List<Session>> data = getSlotList();
        data.forEach((_, sessions) -> {
            LOGGER.info("[ PUSH BINARY SESSION CHANNEL ] {} >>> {}", name, Collections.singletonList(bytes));
            submit(sessions, filter, name, bytes);
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
        final Map<String, List<Session>> data = getSlotList();
        data.forEach((_, sessions) -> {
            LOGGER.info("[ PUSH TEXT SESSION CHANNEL ] {} >>> {}", name, content);
            submit(sessions, filter, name, content);
        });
    }

    /**
     * Submit Push Message Task
     *
     * @param sessions Session List Object
     * @param filter   Filter Object
     * @param name     Channel Name
     * @param content  Content Data
     */
    private static void submit(List<Session> sessions, Function<User, Boolean> filter, String name, Object content) {
        Thread.startVirtualThread(() -> {
            for (final Session session : sessions) {
                if (filter != null && name.equalsIgnoreCase(session.getChannelName())) {
                    final Boolean result = filter.apply(session.getUser());
                    if (result != null && result) {
                        session.push(content);
                    }
                }
            }
        }).start();
    }

}
