package club.p6e.coat.sse;

import club.p6e.coat.websocket.DataType;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
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
     * @param user    User Object
     * @param context Channel Handler Context Object
     */
    public Session(User user, ChannelHandlerContext context) {
        this.user = user;
        this.context = context;
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
            context.writeAndFlush(new TextWebSocketFrame(content));
        }
    }

}
