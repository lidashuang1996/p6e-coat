package club.p6e.coat.sse;

import club.p6e.coat.common.utils.GeneratorUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;

/**
 * Session
 *
 * @author lidashuang
 * @version 1.0
 */
@Getter
public class Session {

    /**
     * Date Time Formatter Object
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * User Object
     */
    private final User user;

    /**
     * Channel Name
     */
    private final String channelName;

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
     * @param user    User Object
     * @param context Channel Handler Context Object
     */
    public Session(String name, User user, ChannelHandlerContext context) {
        this.user = user;
        this.context = context;
        this.channelName = name;
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
     * @param content Message Content
     */
    public void push(Object content) {
        push(null, "MESSAGE", content);
    }

    /**
     * Push Message
     *
     * @param event   Message Event
     * @param content Message Content
     */
    public void push(String event, Object content) {
        push(null, event, content);
    }

    /**
     * Push Message
     *
     * @param id    Message ID
     * @param event Message Event
     * @param data  Message Content
     */
    public void push(String id, String event, Object data) {
        if (context != null && !context.isRemoved()) {
            event = event == null ? "MESSAGE" : event;
            id = id == null ? (LocalDateTime.now().format(DATE_TIME_FORMATTER) + GeneratorUtil.random(8, true, false)) : id;
            if (data == null) {
                data = "";
            } else if (data instanceof final byte[] bytes) {
                data = HexFormat.of().formatHex(bytes);
            } else {
                data = data.toString();
            }
            final FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    io.netty.buffer.Unpooled.copiedBuffer("id: " + id + "\nevent: " + event + "\ndata: " + data + "\n\n", CharsetUtil.UTF_8));
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/event-stream; charset=UTF-8");
            response.headers().set(HttpHeaderNames.CACHE_CONTROL, "no-cache");
            response.headers().set(HttpHeaderNames.CONNECTION, "keep-alive");
            context.writeAndFlush(response);
        }
    }

}
