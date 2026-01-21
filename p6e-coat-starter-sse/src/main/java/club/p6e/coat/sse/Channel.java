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
import java.util.Map;

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
     * Session ID
     */
    private final String id;

    /**
     * Auth Service Object
     */
    private final AuthService auth;

    /**
     * Properties Channel Object
     */
    private final Properties.Channel psc;

    /**
     * Constructor Initialization
     *
     * @param psc  Properties Channel Object
     * @param auth Auth Service Object
     */
    public Channel(Properties.Channel psc, AuthService auth) {
        this.psc = psc;
        this.auth = auth;
        this.id = GeneratorUtil.uuid() + GeneratorUtil.random();
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object o) {
        if (o instanceof final FullHttpRequest request) {
            final Properties.Channel.CrossDomain pcc = psc.getCrossDomain();
            final FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            if (pcc.getEnable()) {
                final String origin = request.headers().get(HttpHeaderNames.ORIGIN);
                for (final String item : pcc.getOrigin()) {
                    if ("*".equals(item)) {
                        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, origin == null ? "*" : origin);
                        break;
                    } else if (item.equals(origin)) {
                        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
                        break;
                    }
                }
                response.headers().set(HttpHeaderNames.ACCESS_CONTROL_MAX_AGE, String.valueOf(pcc.getMaxAge()));
                response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, String.join(",", pcc.getHeaders()));
                response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, String.join(",", pcc.getMethods()));
            }
            if (HttpMethod.OPTIONS.equals(request.method())) {
                context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                final User user = this.auth.validate(this.psc.getName(), request.uri());
                if (user == null) {
                    response.content().writeBytes(context.alloc().buffer().writeBytes(JsonUtil.toJson(
                            ResultContext.build(500, "AUTH_ERROR", "AUTH_ERROR")
                    ).getBytes(StandardCharsets.UTF_8)));
                    context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    response.headers().set(HttpHeaderNames.EXPIRES, "0");
                    response.headers().set(HttpHeaderNames.PRAGMA, "no-cache");
                    response.headers().set(HttpHeaderNames.CONNECTION, "keep-alive");
                    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/event-stream; charset=UTF-8");
                    response.headers().set(HttpHeaderNames.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
                    final Session session = new Session(this.psc.getName(), user, context);
                    context.channel().attr(SESSION_ID).set(this.id);
                    SessionManager.register(this.id, session);
                    context.writeAndFlush(response);
                    session.push("LOGIN", JsonUtil.toJson(Map.of("type", "LOGIN", "data", "SUCCESS")));
                }
            }
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext context) {
        try {
            final Session session = SessionManager.get(this.id);
            if (session != null) {
                session.push("LOGOUT", JsonUtil.toJson(Map.of("type", "LOGOUT", "data", "SUCCESS")));
            }
        } finally {
            SessionManager.unregister(this.id);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable e) {
        LOGGER.error("[ CHANNEL ERROR ] {} => {}", this.id, e.getMessage(), e);
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

}
