package club.p6e.coat.shield.slider;

import club.p6e.coat.common.utils.FileUtil;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.shield.Generator;
import club.p6e.coat.shield.Parameter;
import club.p6e.coat.shield.Properties;
import club.p6e.coat.shield.cache.SliderCache;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.awt.image.DataBuffer;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class SliderGenerator implements Generator {


    protected final SliderCache cache;

    protected final Properties properties;

    protected final SliderCore core = new SliderCore();

    public SliderGenerator(Properties properties, SliderCache cache) {
        this.cache = cache;
        this.properties = properties;
    }

    @Override
    public String name() {
        return "SLIDER";
    }

    @Override
    public OutputStream execute(ServerWebExchange exchange, Parameter parameter) {
        final String value = getValue();
        final String resource = getResource();
        final String client = parameter.getClientId();
        final String shape = parameter.getQuery().getOrDefault("shape", "0");
        final String image = FileUtil.composePath(properties.getBaseResourcePath(), resource);
        final String token = GeneratorUtil.uuid() + GeneratorUtil.random(10, true, false);
        cache.set(client, token, resource, shape, value);
        try {
            final OutputStream os = getResult(Map.of("token", token), execute(image, shape, value));
            exchange.getResponse();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Execute
     *
     * @param image Image Path
     * @param shape Shape Type
     * @param value Value Data
     * @return Bytes Object
     */
    protected byte[] execute(String image, String shape, String value) {
        return core.execute(image, shape, value);
    }

    /**
     * Get Value
     *
     * @return Value Data
     */
    protected String getValue() {
        final int y = ThreadLocalRandom.current().nextInt(50, 120);
        final int x = ThreadLocalRandom.current().nextInt(100, 250);
        return x + "," + y;
    }

    /**
     * Get Resource
     *
     * @return Resource Data
     */
    protected String getResource() {
        final int wil = cache.warehouse();
        final int wii = ThreadLocalRandom.current().nextInt(0, wil);
        final int wcl = cache.warehouse(wii);
        final int wci = ThreadLocalRandom.current().nextInt(0, wcl);
        return cache.warehouse(wii, wci);
    }

    /**
     * Get Result
     *
     * @param data  Map Object
     * @param bytes Bytes Object
     */
    protected Flux<DataBuffer> getResult(Map<String, String> data, byte[] bytes) {
        return DataBufferUtils.jaadsoin(bytes);
    }

}
