package club.p6e.coat.websocket;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session
 *
 * @author lidashuang
 * @version 1.0
 */
@Getter
public final class Session {

    /**
     * Inject Log Object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Session.class);

    /**
     * User Object
     */
    private final User user;

    /**
     * Channel Name
     */
    private final String channelName;

    /**
     * Channel Type
     */
    private final String channelType;

    /**
     * Channel Handler Context Object
     */
    private final ChannelHandlerContext context;

    /**
     * Date Object
     */
    private volatile long date;

    /**
     * 构造方法初始化
     *
     * @param name    服务名称
     * @param type    消息类型
     * @param user    用户对象
     * @param context 上下文对象
     */
    public Session(String name, String type, User user, ChannelHandlerContext context) {
        this.user = user;
        this.context = context;
        this.channelName = name;
        this.channelType = type;
        this.date = System.currentTimeMillis();
    }

    /**
     * Refresh Session
     */
    @SuppressWarnings("ALL")
    public void refresh() {
        this.date = System.currentTimeMillis();
    }

    /**
     * Close Session
     */
    @SuppressWarnings("ALL")
    public void close() {
        if (context != null && !context.isRemoved()) {
            context.close();
        }
    }

    /**
     * Push Message
     *
     * @param data Message Content
     */
    public void push(Object data) {
        if (context != null && !context.isRemoved()) {
            if (DataType.TEXT.name().equalsIgnoreCase(this.channelType) && data instanceof String content) {
                context.writeAndFlush(new TextWebSocketFrame(content));
            }
            if (DataType.BINARY.name().equalsIgnoreCase(this.channelType) && data instanceof byte[] bytes) {
                context.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(bytes)));
            }
        }
    }

}
