package club.p6e.coat.common.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.repository.init.ResourceReader;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Blocking Version Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@RequestMapping("/__version__")
@Component(value = "club.p6e.coat.common.controller.BlockingVersionController")
@ConditionalOnClass(name = "org.springframework.web.servlet.package-info")
public class BlockingVersionController {

    @RequestMapping("")
    public void def1(HttpServletResponse response) {
        def2(response);
    }

    @RequestMapping("/")
    public void def2(HttpServletResponse response) {
        try {
            response.addHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=UTF-8");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write(version());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String version() {
        final StringBuilder content = new StringBuilder();
        try (final InputStream inputStream = ResourceReader.class.getClassLoader().getResourceAsStream("version")) {
            if (inputStream == null) {
                content.append("UNKNOWN");
            } else {
                try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        content.append(line);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return content.toString();
    }

}
