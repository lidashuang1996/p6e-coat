package club.p6e.coat.websocket;

import club.p6e.coat.common.utils.SpringUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Application
 *
 * @author lidashuang
 * @version 1.0
 */
public class Application {

    /**
     * Inject Log Object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    /**
     * Event Loop Group Boss
     */
    private EventLoopGroup boss;

    /**
     * Event Loop Group Worker
     */
    private EventLoopGroup work;

    /**
     * Constructor Initialization
     */
    public Application() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (this.boss != null) {
                this.boss.shutdownGracefully();
            }
            if (this.work != null) {
                this.work.shutdownGracefully();
            }
        }));
    }

    /**
     * Config Reset
     */
    @SuppressWarnings("ALL")
    public synchronized void reset(Config config) {
        LOGGER.info("[ WEBSOCKET SERVICE ] RESET CONFIG >>> {}", config);
        if (this.boss != null || this.work != null) {
            this.boss.shutdownGracefully();
            this.work.shutdownGracefully();
            this.boss = null;
            this.work = null;
        }
        SessionManager.init(config.getManagerThreadPoolLength());
        this.boss = new NioEventLoopGroup(config.getBossThreads());
        this.work = new NioEventLoopGroup(config.getBossThreads());
        for (final Config.Channel channel : config.getChannels()) {
            LOGGER.info("[ WEBSOCKET SERVICE ] RESET CHANNEL >>> {}", channel);
            AuthService auth = null;
            final List<Callback> callbacks = new ArrayList<>();
            final Map<String, Callback> cBeans = SpringUtil.getBeans(Callback.class);
            final Map<String, AuthService> aBeans = SpringUtil.getBeans(AuthService.class);
            for (final String bn : channel.getCallbacks()) {
                for (final Callback item : cBeans.values()) {
                    if (bn.equalsIgnoreCase(item.getClass().getName())) {
                        callbacks.add(item);
                    }
                }
            }
            for (final AuthService item : aBeans.values()) {
                if (channel.getAuth().equalsIgnoreCase(item.getClass().getName())) {
                    auth = item;
                    break;
                }
            }
            run(channel.getPort(), channel.getPath(), channel.getName(), channel.getType(), channel.getFrame(), auth, callbacks);
        }
    }

    /**
     * Push Message
     *
     * @param filter Filter Object
     * @param name   Channel Name
     * @param bytes  Message Content
     */
    public void push(Function<User, Boolean> filter, String name, byte[] bytes) {
        SessionManager.pushBinary(filter, name, bytes);
    }

    /**
     * Push Message
     *
     * @param filter  Filter Object
     * @param name    Channel Name
     * @param content Message Content
     */
    public void push(Function<User, Boolean> filter, String name, String content) {
        SessionManager.pushText(filter, name, content);
    }

    /**
     * Netty Web Socket Server
     *
     * @param port      Channel Port
     * @param path      Channel Path
     * @param name      Channel Name
     * @param type      Channel Type
     * @param frame     Channel Frame
     * @param auth      Auth Service Object
     * @param callbacks Callback List Object
     */
    private synchronized void run(int port, String path, String name, String type, int frame, AuthService auth, List<Callback> callbacks) {
        try {
            final ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(this.boss, this.work);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<>() {
                @Override
                protected void initChannel(io.netty.channel.Channel channel) {
                    // HTTP
                    channel.pipeline().addLast(new HttpServerCodec());
                    // HTTP OBJECT AGGREGATOR
                    channel.pipeline().addLast(new HttpObjectAggregator(frame));
                    // WEB SOCKET
                    channel.pipeline().addLast(new WebSocketServerProtocolHandler(
                            path, null, true,
                            frame, false, true
                    ));
                    // CHANNEL
                    channel.pipeline().addLast(new Channel(name, type, auth, callbacks));
                }
            });
            bootstrap.bind(port).sync();
            LOGGER.info("[ WEBSOCKET SERVICE ] ({} : {}) ==> START SUCCESSFULLY... BIND ( {} )", name, type, port);
        } catch (Exception e) {
            LOGGER.error("[ WEBSOCKET SERVICE ]", e);
        }
    }

}
