package club.p6e.coat.sse;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Application
 *
 * @author lidashuang
 * @version 1.0
 */
@EnableConfigurationProperties(Properties.class)
public class Application implements ApplicationRunner {

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
     * Properties Object
     */
    private Properties properties;

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
     * @param authServices Auth Service List Object
     */
    public Application(Properties properties, List<AuthService> authServices) {
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
    }


    @Override
    public void run(ApplicationArguments args) {
        reset();
    }

    /**
     * Reset
     */
    public synchronized void reset() {
        LOGGER.info("[ SSE SERVICE ] RESET PROPERTIES >>> {}", this.properties);
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
        this.boss = new NioEventLoopGroup(this.properties.getBossThreads());
        this.work = new NioEventLoopGroup(this.properties.getWorkerThreads());
        for (final Properties.Channel channel : this.properties.getChannels()) {
            LOGGER.info("[ SSE SERVICE ] RESET CHANNEL >>> {}", channel);
            AuthService auth = null;
            for (final AuthService item : this.authServices) {
                if (channel.getAuth().equalsIgnoreCase(item.getClass().getName())) {
                    auth = item;
                    break;
                }
            }
            if (auth == null) {
                throw new NullPointerException("[ SSE SERVICE ] (" + channel.getAuth() + ") AUTH SERVICE NOT FOUND");
            }
            run(channel, auth);
        }
    }

    /**
     * Reset
     *
     * @param properties Properties Object
     */
    @SuppressWarnings("ALL")
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
     * @param psc  Properties Channel Object
     * @param auth Auth Service Object
     */
    private void run(Properties.Channel psc, AuthService auth) {
        try {
            final int port = psc.getPort();
            final int frame = psc.getFrame();
            final String name = psc.getName();
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
                    // CHANNEL
                    channel.pipeline().addLast(new Channel(psc, auth));
                }
            });
            this.channels.add(bootstrap.bind(port).sync().channel());
            LOGGER.info("[ SSE SERVICE ] ({}) ==> START SUCCESSFULLY... BIND ( {} )", name, port);
        } catch (Exception e) {
            LOGGER.error("[ SSE SERVICE ]", e);
        }
    }

}
