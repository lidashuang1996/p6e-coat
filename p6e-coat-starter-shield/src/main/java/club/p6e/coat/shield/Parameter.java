package club.p6e.coat.shield;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
public class Parameter implements Serializable {

    private String body;
    private String clientId;
    private String clientSecret;
    private Map<String, String> query = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();

//    final StringBuilder body = new StringBuilder();
//        try (
//    final BufferedReader reader = new BufferedReader(
//            new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8))
//        ) {
//        String line;
//        while ((line = reader.readLine()) != null) {
//            body.append(line);
//        }
//    } catch (Exception e) {
//        // ignore exception
//    }
}
