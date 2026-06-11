package club.p6e.coat.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Application
 *
 * @author lidashuang
 * @version 1.0
 */
@Slf4j
public class Application {

    /**
     * Event Loop Group Boss
     */
    private EventLoopGroup boss;

    /**
     * Event Loop Group Worker
     */
    private EventLoopGroup work;

    /**
     * Properties Object
     */
    private Properties properties;

    /**
     * Callback List Object
     */
    private final List<Callback> callbacks;

    /**
     * Auth Service List Object
     */
    private final List<AuthService> authServices;

    /**
     * Server Channels
     */
    private final List<io.netty.channel.Channel> channels = new ArrayList<>();

    /**
     * Constructor Initialization
     *
     * @param properties   Properties Object
     * @param callbacks    Callback List Object
     * @param authServices Auth Service List Object
     */
    public Application(Properties properties, List<Callback> callbacks, List<AuthService> authServices) {
        this.callbacks = callbacks;
        this.properties = properties;
        this.authServices = authServices;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (this.boss != null) {
                this.boss.shutdownGracefully();
            }
            if (this.work != null) {
                this.work.shutdownGracefully();
            }
        }));
        reset();
    }

    /**
     * Reset
     */
    public synchronized void reset() {
        log.info("[ WEBSOCKET SERVICE ] RESET PROPERTIES >>> {}", this.properties);
        for (final io.netty.channel.Channel channel : this.channels) {
            channel.close();
        }
        this.channels.clear();
        if (this.boss != null) {
            this.boss.shutdownGracefully();
            this.boss = null;
        }
        if (this.work != null) {
            this.work.shutdownGracefully();
            this.work = null;
        }
        SessionManager.init(this.properties.getManagerThreadPoolLength());
        this.boss = new MultiThreadIoEventLoopGroup(this.properties.getBossThreads(), NioIoHandler.newFactory());
        this.work = new MultiThreadIoEventLoopGroup(this.properties.getWorkerThreads(), NioIoHandler.newFactory());
        for (final Properties.Channel channel : this.properties.getChannels()) {
            log.info("[ WEBSOCKET SERVICE ] RESET CHANNEL >>> {}", channel);
            AuthService auth = null;
            final List<Callback> callbacks = new ArrayList<>();
            for (final String item : channel.getCallbacks()) {
                for (final Callback callback : this.callbacks) {
                    if (callback.getClass().getName().equalsIgnoreCase(item)) {
                        callbacks.add(callback);
                    }
                }
            }
            for (final AuthService item : this.authServices) {
                if (item.getClass().getName().equalsIgnoreCase(channel.getAuth())) {
                    auth = item;
                    break;
                }
            }
            if (auth == null) {
                throw new NullPointerException("[ WEBSOCKET SERVICE ] (" + channel.getAuth() + ") AUTH SERVICE NOT FOUND");
            }
            run(channel.getPort(), channel.getPath(), channel.getName(), channel.getType(), channel.getFrame(), auth, callbacks);
        }
    }

    /**
     * Reset
     *
     * @param properties Properties Object
     */
    public void reset(Properties properties) {
        this.properties = properties;
        reset();
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
            this.channels.add(bootstrap.bind(port).sync().channel());
            log.info("[ WEBSOCKET SERVICE ] ({} : {}) ==> START SUCCESSFULLY... BIND ( {} : {} )", name, type, port, path);
        } catch (Exception e) {
            log.error("[ WEBSOCKET SERVICE ] >>> {}", e.getMessage(), e);
        }
    }

}
