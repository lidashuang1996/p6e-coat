package club.p6e.coat.sse;

import club.p6e.coat.common.utils.SpringUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * Config Reset
     */
    @SuppressWarnings("ALL")
    public synchronized void reset(Config config) {
        SessionManager.init(config.getManagerThreadPoolLength());
        for (final Config.Channel channel : config.getChannels()) {
            AuthService auth = null;
            final Map<String, AuthService> aBeans = SpringUtil.getBeans(AuthService.class);
            for (final AuthService item : aBeans.values()) {
                if (channel.getAuth().equalsIgnoreCase(item.getClass().getName())) {
                    auth = item;
                    break;
                }
            }
            run(channel.getPort(), channel.getName(), channel.getType(), auth);
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
    @SuppressWarnings("ALL")
    public void push(Function<User, Boolean> filter, String name, String content) {
        SessionManager.pushText(filter, name, content);
    }

    /**
     * Netty Web Socket Server
     *
     * @param port Channel Port
     * @param name Channel Name
     * @param type Channel Type
     * @param auth Auth Service Object
     */
    private void run(int port, String name, String type, AuthService auth) {
        final EventLoopGroup boss = new NioEventLoopGroup();
        final EventLoopGroup work = new NioEventLoopGroup();
        try {
            final ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, work);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<>() {
                @Override
                protected void initChannel(io.netty.channel.Channel channel) {
                    // HTTP
                    channel.pipeline().addLast(new HttpServerCodec());
                    // HTTP OBJECT AGGREGATOR
                    channel.pipeline().addLast(new HttpObjectAggregator(65536));
                    // CHANNEL
                    channel.pipeline().addLast(new Channel(name, auth));
                }
            });
            final io.netty.channel.Channel channel = bootstrap.bind(port).sync().channel();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                boss.shutdownGracefully();
                work.shutdownGracefully();
            }));
            LOGGER.info("[ WEBSOCKET SERVICE ] ({} : {}) ==> START SUCCESSFULLY... BIND ( {} )", name, type, port);
            channel.closeFuture();
        } catch (Exception e) {
            LOGGER.error("[ WEBSOCKET SERVICE ]", e);
        }
    }

}
