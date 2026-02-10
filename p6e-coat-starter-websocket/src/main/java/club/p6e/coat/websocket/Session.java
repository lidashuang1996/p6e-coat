package club.p6e.coat.websocket;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.Getter;

/**
 * Session
 *
 * @author lidashuang
 * @version 1.0
 */
@Getter
public class Session {

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
     * Constructor Initialization
     *
     * @param name    Channel Name
     * @param type    Channel Type
     * @param user    User Object
     * @param context Channel Handler Context Object
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
    public void refresh() {
        this.date = System.currentTimeMillis();
    }

    /**
     * Close Session
     */
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
    @SuppressWarnings("ALL")
    public void push(Object data) {
        if (context != null && !context.isRemoved()) {
            if (DataType.TEXT.name().equalsIgnoreCase(this.channelType) && data instanceof String content) {
                final io.netty.channel.Channel channel = context.channel();
                if (channel != null) {
                    final EventLoop el = channel.eventLoop();
                    if (el != null) {
                        el.execute(() -> channel.writeAndFlush(new TextWebSocketFrame(content)));
                    }
                }
            }
            if (DataType.BINARY.name().equalsIgnoreCase(this.channelType) && data instanceof byte[] bytes) {
                final io.netty.channel.Channel channel = context.channel();
                if (channel != null && channel.isActive() && channel.isWritable()) {
                    final EventLoop el = channel.eventLoop();
                    if (el != null) {
                        el.execute(() -> channel.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(bytes))));
                    }
                }
            }
        }
    }

}
