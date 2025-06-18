package club.p6e.coat.sse;

import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.JsonUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Handler
 *
 * @author lidashuang
 * @version 1.0
 */
public class Channel implements ChannelInboundHandler {

    /**
     * Inject Log Object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Channel.class);

    /**
     * Session ID Attribute Key
     */
    private static final AttributeKey<String> SESSION_ID = AttributeKey.valueOf("id");

    /**
     * Channel ID
     */
    private final String id;

    /**
     * Auth Service Object
     */
    private final AuthService auth;

    /**
     * Callback List Object
     */
    private final List<Callback> callbacks;

    /**
     * Constructor Initialization
     *
     * @param auth      Auth Service Object
     * @param callbacks Callback List Object
     */
    public Channel(AuthService auth, List<Callback> callbacks) {
        this.id = GeneratorUtil.uuid() + GeneratorUtil.random();
        this.auth = auth;
        this.callbacks = callbacks;
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object o) {
        if (o instanceof final FullHttpRequest request) {
            final FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            final String origin = request.headers().get(HttpHeaderNames.ORIGIN);
            response.headers().set(HttpHeaderNames.ACCESS_CONTROL_MAX_AGE, "3600");
            response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type");
            response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, origin == null ? "*" : origin);
            response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,DELETE,PUT,OPTIONS");
            if (HttpMethod.OPTIONS.equals(request.method())) {
                context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                final User user = this.auth.validate(null, Controller.getVoucher(request.uri()));
                if (user == null) {
                    response.content().writeBytes(context.alloc().buffer().writeBytes(JsonUtil.toJson(
                            ResultContext.build(500, "AUTH_ERROR", "AUTH_ERROR")
                    ).getBytes(StandardCharsets.UTF_8)));
                } else {
                    response.headers().set(HttpHeaderNames.EXPIRES, "0");
                    response.headers().set(HttpHeaderNames.PRAGMA, "no-cache");
                    response.headers().set(HttpHeaderNames.CONNECTION, "keep-alive");
                    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/event-stream; charset=UTF-8");
                    response.headers().set(HttpHeaderNames.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
                    final String id = GeneratorUtil.uuid() + GeneratorUtil.random();
                    final Session session = new Session(user, context);
                    context.writeAndFlush(response);
                    SessionManager.register(id, session);
                    executeCallbackOpen(session);
                }
            }
        }
    }


    @Override
    public void handlerRemoved(ChannelHandlerContext context) {
        final String id = context.channel().attr(SESSION_ID).get();
        executeCallbackClose(SessionManager.get(id));
        SessionManager.unregister(id);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable e) {
        LOGGER.error("[ CHANNEL ERROR ] {} => {}", this.id, e.getMessage(), e);
        final String id = context.channel().attr(SESSION_ID).get();
        executeCallbackError(SessionManager.get(id), e);
        context.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext context) {
    }

    @Override
    public void channelActive(ChannelHandlerContext context) {
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) {
    }

    @Override
    public void channelRegistered(ChannelHandlerContext context) {
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext context) {
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) {
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext context) {
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext context, Object o) {
    }

    private void executeCallbackOpen(Session session) {
        for (Callback callback : this.callbacks) {
            try {
                callback.onOpen(session);
            } catch (Exception e) {
                LOGGER.error("[ CALLBACK ERROR ] OPEN => {}", e.getMessage(), e);
            }
        }
    }

    private void executeCallbackClose(Session session) {
        for (Callback callback : this.callbacks) {
            try {
                callback.onClose(session);
            } catch (Exception e) {
                LOGGER.error("[ CALLBACK ERROR ] CLOSE => {}", e.getMessage(), e);
            }
        }
    }

    private void executeCallbackError(Session session, Throwable throwable) {
        for (Callback callback : this.callbacks) {
            try {
                callback.onError(session, throwable);
            } catch (Exception e) {
                LOGGER.error("[ CALLBACK ERROR ] ERROR => {}", e.getMessage(), e);
            }
        }
    }

}
