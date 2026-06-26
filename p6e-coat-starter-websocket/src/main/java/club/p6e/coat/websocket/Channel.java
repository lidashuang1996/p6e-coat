package club.p6e.coat.websocket;

import club.p6e.coat.common.utils.GeneratorUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Handler
 *
 * @author lidashuang
 * @version 1.0
 */
@Slf4j
public class Channel extends ChannelInboundHandlerAdapter {

    /**
     * LOGIN SUCCESS CONTENT TEXT
     */
    private static final String LOGIN_CONTENT_TEXT = "{\"type\":\"login\"}";

    /**
     * LOGIN SUCCESS CONTENT BYTES
     */
    private static final byte[] LOGIN_CONTENT_BYTES = new byte[]{
            0, 0, 0, 16, 0, 16, 0, 1, 0, 0, 0, 0, 0, 0, 0, 2
    };

    /**
     * Session ID Attribute Key
     */
    private static final AttributeKey<String> SESSION_ID = AttributeKey.valueOf("id");

    /**
     * Session ID
     */
    private final String id;

    /**
     * Channel Name
     */
    private final String name;

    /**
     * Channel Type
     */
    private final String type;

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
     * @param name      Channel Name
     * @param type      Channel Type
     * @param auth      Auth Service Object
     * @param callbacks Callback List Object
     */
    public Channel(String name, String type, AuthService auth, List<Callback> callbacks) {
        this.id = GeneratorUtil.uuid() + GeneratorUtil.random();
        this.name = name;
        this.type = type;
        this.auth = auth;
        this.callbacks = callbacks;
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object o) {
        final Session session = SessionManager.get(context.channel().attr(SESSION_ID).get());
        if (o instanceof final TextWebSocketFrame textWebSocketFrame) {
            executeCallbackMessage(session, textWebSocketFrame.text());
        } else if (o instanceof final BinaryWebSocketFrame binaryWebSocketFrame) {
            final ByteBuf byteBuf = binaryWebSocketFrame.content();
            try {
                final int readableBytesLength = byteBuf.readableBytes();
                final byte[] readableByteArray = new byte[readableBytesLength];
                byteBuf.readBytes(readableByteArray);
                executeCallbackMessage(session, readableByteArray);
            } finally {
                byteBuf.release();
            }
        } else if (o instanceof PingWebSocketFrame) {
            context.writeAndFlush(new PongWebSocketFrame());
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext context, Object o) {
        if (o instanceof final WebSocketServerProtocolHandler.HandshakeComplete complete) {
            final User user = this.auth.validate(this.name, complete.requestUri());
            if (user == null) {
                context.close();
            } else {
                final Session session = new Session(this.name, this.type, user, context);
                context.channel().attr(SESSION_ID).set(this.id);
                SessionManager.register(this.id, session);
                executeCallbackOpen(session);
                if (DataType.TEXT.name().equals(this.type)) {
                    context.writeAndFlush(new TextWebSocketFrame(LOGIN_CONTENT_TEXT));
                } else if (DataType.BINARY.name().equals(this.type)) {
                    context.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(LOGIN_CONTENT_BYTES)));
                }
            }
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext context) {
        try {
            final Session session = SessionManager.get(this.id);
            if (session != null) {
                executeCallbackClose(session);
            }
        } finally {
            if (this.id != null) {
                SessionManager.unregister(this.id);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable e) {
        final Session session = SessionManager.get(this.id);
        if (session != null) {
            executeCallbackError(session, e);
        }
        context.close();
    }

    /**
     * Execute Callback Open
     *
     * @param session Session Object
     */
    private void executeCallbackOpen(Session session) {
        if (session != null) {
            for (Callback callback : this.callbacks) {
                try {
                    callback.onOpen(session);
                } catch (Exception e) {
                    log.error("[ WEB SOCKET CALLBACK ERROR ] OPEN => {}", e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Execute Callback Close
     *
     * @param session Session Object
     */
    private void executeCallbackClose(Session session) {
        if (session != null) {
            for (final Callback callback : this.callbacks) {
                try {
                    callback.onClose(session);
                } catch (Exception e) {
                    log.error("[ WEB SOCKET CALLBACK ERROR ] CLOSE => {}", e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Execute Callback Error
     *
     * @param session   Session Object
     * @param throwable Throwable Object
     */
    private void executeCallbackError(Session session, Throwable throwable) {
        if (session != null) {
            for (final Callback callback : this.callbacks) {
                try {
                    callback.onError(session, throwable);
                } catch (Exception e) {
                    log.error("[ WEB SOCKET CALLBACK ERROR ] ERROR => {}", e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Execute Callback Message
     *
     * @param session Session Object
     * @param text    String Object
     */
    private void executeCallbackMessage(Session session, String text) {
        if (session != null) {
            for (final Callback callback : this.callbacks) {
                try {
                    callback.onMessage(session, text);
                } catch (Exception e) {
                    log.error("[ WEB SOCKET CALLBACK ERROR ] MESSAGE TEXT => {}", e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Execute Callback Message
     *
     * @param session Session Object
     * @param bytes   Bytes Object
     */
    private void executeCallbackMessage(Session session, byte[] bytes) {
        if (session != null) {
            for (final Callback callback : this.callbacks) {
                try {
                    callback.onMessage(session, bytes);
                } catch (Exception e) {
                    log.error("[ WEB SOCKET CALLBACK ERROR ] MESSAGE BYTES => {}", e.getMessage(), e);
                }
            }
        }
    }

}
