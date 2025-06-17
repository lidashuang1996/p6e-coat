package club.p6e.coat.websocket;

import club.p6e.coat.common.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
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
            final String index = String.valueOf(Math.abs(id.hashCode() % SLOTS_NUM));
            SLOTS.computeIfAbsent(index, k -> new ConcurrentHashMap<>()).put(id, session);
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
            SESSIONS.remove(id);
            final String index = String.valueOf(Math.abs(id.hashCode() % SLOTS_NUM));
            final Map<String, Session> data = SLOTS.get(index);
            if (data != null) {
                data.remove(id);
            }
            LOGGER.info("[ SESSION MANAGER ] UNREGISTER => {}%{}={} >>> {}", id, SLOTS_NUM, index, SLOTS.get(index));
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
                final List<Session> sessions = new ArrayList<>(v.values());
                list.add(sessions);
                LOGGER.info("[ SESSION CHANNEL ] {} >>> {}", k, sessions);
            }
        });
        return list;
    }

    /**
     * 推送消息
     *
     * @param filter 过滤器对象
     * @param name   服务名称
     * @param bytes  消息内容
     */
    public static void pushBinary(Function<User, Boolean> filter, String name, byte[] bytes) {
        final List<List<Session>> list = getChannel();
        for (final List<Session> channels : list) {
            if (channels != null && !channels.isEmpty()) {
                LOGGER.info("[ PUSH BINARY SESSION CHANNEL ] {} >>> {}", name, Collections.singletonList(bytes));
                submit(channels, filter, name, bytes);
            }
        }
    }

    /**
     * 推送消息
     *
     * @param filter  过滤器对象
     * @param name    服务名称
     * @param content 消息内容
     */
    public static void pushText(Function<User, Boolean> filter, String name, String content) {
        final List<List<Session>> list = getChannel();
        final Map<String, String> data = new HashMap<>();
        data.put("id", id);
        data.put("type", type);
        data.put("content", content);
        final String wc = JsonUtil.toJson(data);
        for (final List<Session> channels : list) {
            if (channels != null && !channels.isEmpty()) {
                LOGGER.info("[ PUSH TEXT SESSION CHANNEL ] {} >>> {}", name, wc);
                submit(channels, filter, name, wc);
            }
        }
    }

    /**
     * Submit
     *
     * @param channels 频道对象
     * @param filter   过滤器对象
     * @param name     服务名称
     * @param content  消息内容
     */
    private static void submit(List<Session> channels, Function<User, Boolean> filter, String name, Object content) {
        EXECUTOR.submit(() -> {
            for (final Session session : channels) {
                LOGGER.info("[ SUBMIT TASK EXECUTE ] >>> NAME CHECK >>> N:{}/SN:{} ? {}", name, session.getName(), name.equalsIgnoreCase(session.getName()));
                if (name.equalsIgnoreCase(session.getName())) {
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
