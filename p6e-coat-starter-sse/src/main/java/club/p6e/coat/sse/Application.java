package club.p6e.coat.sse;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Application
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@EnableConfigurationProperties(Properties.class)
public class Application implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    private EventLoopGroup boss;
    private EventLoopGroup work;
    private Properties properties;
    private final List<AuthService> authServices;
    private final List<io.netty.channel.Channel> channels = new ArrayList<>();

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

    /**
     * [P3] 修复: ApplicationRunner.run() 中调用 reset()，
     *      确保 Spring Boot 启动后自动初始化 SSE 服务
     */
    @Override
    public void run(@NonNull ApplicationArguments args) {
        reset();
    }

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
        this.boss = new MultiThreadIoEventLoopGroup(this.properties.getBossThreads(), NioIoHandler.newFactory());
        this.work = new MultiThreadIoEventLoopGroup(this.properties.getWorkerThreads(), NioIoHandler.newFactory());
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

    @SuppressWarnings("ALL")
    public void reset(Properties properties) {
        this.properties = properties;
        reset();
    }

    public void push(Function<User, Boolean> filter, String name, byte[] bytes) {
        SessionManager.pushBinary(filter, name, bytes);
    }

    public void push(Function<User, Boolean> filter, String name, String content) {
        SessionManager.pushText(filter, name, content);
    }

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
                    channel.pipeline().addLast(new HttpServerCodec());
                    channel.pipeline().addLast(new HttpObjectAggregator(frame));
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