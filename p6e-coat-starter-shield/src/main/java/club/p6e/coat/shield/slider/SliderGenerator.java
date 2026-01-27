package club.p6e.coat.shield.slider;

import club.p6e.coat.common.utils.FileUtil;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.shield.Generator;
import club.p6e.coat.shield.Parameter;
import club.p6e.coat.shield.Properties;
import club.p6e.coat.shield.Signature;
import club.p6e.coat.shield.cache.SliderCache;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class SliderGenerator implements Generator {

    protected SliderCore core = new SliderCore();

    protected Properties properties;
    protected SliderCache cache;

    public SliderGenerator(Properties properties, SliderCache cache) {
        this.properties = properties;
        this.cache = cache;
    }

    @Override
    public String name() {
        return "SLIDER";
    }

    @Override
    public OutputStream execute(HttpServletRequest request, HttpServletResponse response, Parameter parameter) {
        final String value = getValue();
        final String resource = getResource();
        final String shape = parameter.getQuery().getOrDefault("shape", "1");
        final String image = FileUtil.composePath(properties.getBaseResourcePath(), resource);
        final String token = GeneratorUtil.uuid() + GeneratorUtil.random(10, true, false);
        final byte[] bytes = execute(image, shape, value);
        cache.set(parameter.getClient(), token, resource, shape, value);
        return getResult(Map.of("token", token), bytes);
    }

    protected byte[] execute(String image, String shape, String value) {
        return core.execute(image, shape, value);
    }

    protected String getValue() {
        final int y = ThreadLocalRandom.current().nextInt(50, 120);
        final int x = ThreadLocalRandom.current().nextInt(100, 250);
        return x + "," + y;
    }

    protected String getResource() {
        final int wil = cache.warehouse();
        final int wii = ThreadLocalRandom.current().nextInt(0, wil);
        final int wcl = cache.warehouse(wii);
        final int wci = ThreadLocalRandom.current().nextInt(0, wcl);
        return cache.warehouse(wii, wci);
    }

    protected OutputStream getResult(Map<String, String> data, byte[] bytes) {
        final int y = ThreadLocalRandom.current().nextInt(50, 120);
        final int x = ThreadLocalRandom.current().nextInt(100, 250);
        return x + "," + y;
    }

}
