package club.p6e.coat.sse;

import club.p6e.coat.common.utils.GeneratorUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * Channel Name
     */
    private final String name;

    /**
     * Auth Service Object
     */
    private final AuthService auth;


    /**
     * Constructor Initialization
     *
     * @param name Channel Name
     */
    public Channel(String name, AuthService auth) {
        this.id = GeneratorUtil.uuid() + GeneratorUtil.random();
        this.name = name;
        this.auth = auth;
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object o) {
        if (o instanceof final FullHttpRequest  textWebSocketFrame) {
            executeCallbackMessage(club.p6e.coat.websocket.SessionManager.get(context.channel().attr(SESSION_ID).get()), textWebSocketFrame.text());
        } else if (o instanceof final BinaryWebSocketFrame binaryWebSocketFrame) {
            final ByteBuf byteBuf = binaryWebSocketFrame.content();
            try {
                final int readableBytesLength = byteBuf.readableBytes();
                final byte[] readableByteArray = new byte[readableBytesLength];
                byteBuf.readBytes(readableByteArray);
                executeCallbackMessage(club.p6e.coat.websocket.SessionManager.get(context.channel().attr(SESSION_ID).get()), readableByteArray);
            } finally {
                byteBuf.release();
            }
        } else if (o instanceof PingWebSocketFrame) {
            context.writeAndFlush(new PongWebSocketFrame());
        } else if (o instanceof PongWebSocketFrame) {
            context.writeAndFlush(new PingWebSocketFrame());
        }
    }



    @Override
    public void handlerRemoved(ChannelHandlerContext context) {
        if (DataType.TEXT.name().equalsIgnoreCase(this.type)) {
            context.writeAndFlush(new TextWebSocketFrame(LOGOUT_CONTENT_TEXT));
        } else if (DataType.BINARY.name().equalsIgnoreCase(this.type)) {
            context.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(LOGOUT_CONTENT_BYTES)));
        }
        executeCallbackClose(club.p6e.coat.websocket.SessionManager.get(context.channel().attr(SESSION_ID).get()));
        club.p6e.coat.websocket.SessionManager.unregister(this.id);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable e) {
        LOGGER.error("[ CHANNEL ERROR ] {} => {}", this.id, e.getMessage(), e);
        executeCallbackError(SessionManager.get(context.channel().attr(SESSION_ID).get()), e);
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

    private void executeCallbackOpen(club.p6e.coat.websocket.Session session) {
        for (Callback callback : this.callbacks) {
            try {
                callback.onOpen(session);
            } catch (Exception e) {
                LOGGER.error("[ CALLBACK ERROR ] OPEN => {}", e.getMessage(), e);
            }
        }
    }

    private void executeCallbackClose(club.p6e.coat.websocket.Session session) {
        for (Callback callback : this.callbacks) {
            try {
                callback.onClose(session);
            } catch (Exception e) {
                LOGGER.error("[ CALLBACK ERROR ] CLOSE => {}", e.getMessage(), e);
            }
        }
    }

    private void executeCallbackError(club.p6e.coat.websocket.Session session, Throwable throwable) {
        for (Callback callback : this.callbacks) {
            try {
                callback.onError(session, throwable);
            } catch (Exception e) {
                LOGGER.error("[ CALLBACK ERROR ] ERROR => {}", e.getMessage(), e);
            }
        }
    }

    private void executeCallbackMessage(club.p6e.coat.websocket.Session session, String text) {
        for (Callback callback : this.callbacks) {
            try {
                callback.onMessage(session, text);
            } catch (Exception e) {
                LOGGER.error("[ CALLBACK ERROR ] MESSAGE TEXT => {}", e.getMessage(), e);
            }
        }
    }

    private void executeCallbackMessage(Session session, byte[] bytes) {
        for (Callback callback : this.callbacks) {
            try {
                callback.onMessage(session, bytes);
            } catch (Exception e) {
                LOGGER.error("[ CALLBACK ERROR ] MESSAGE BYTES => {}", e.getMessage(), e);
            }
        }
    }

}
