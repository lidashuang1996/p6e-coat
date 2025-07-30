package club.p6e.coat.sse;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller
 *
 * @author lidashuang
 * @version 1.0
 */
public class Controller {

    /**
     * Voucher Param Name 1
     */
    private static final String V_PARAM_NAME = "v";

    /**
     * Voucher Param Name 2
     */
    private static final String VOUCHER_PARAM_NAME = "voucher";

    /**
     * Get Uri Param Object
     *
     * @param uri Request Uri
     * @return Uri Param Object
     */
    public static Map<String, List<String>> getParams(String uri) {
        final int index = uri.indexOf("?");
        final Map<String, List<String>> result = new HashMap<>(16);
        if (index > 0) {
            int pi = 0;
            final String param = uri.substring(index + 1);
            for (int i = 0; i < param.length(); i++) {
                final char ch = param.charAt(i);
                if (ch == '&' || ch == '?' || i == param.length() - 1) {
                    final String kv = param.substring(pi, i == param.length() - 1 ? i + 1 : i);
                    final String[] kvs = kv.split("=");
                    if (kvs.length == 2) {
                        result.computeIfAbsent(URLDecoder.decode(kvs[0], StandardCharsets.UTF_8), k -> new ArrayList<>()).add(URLDecoder.decode(kvs[1], StandardCharsets.UTF_8));
                    }
                    pi = i + 1;
                }
            }
        }
        return result;
    }

    /**
     * Get Uri Voucher
     *
     * @param uri Request Uri
     * @return Voucher
     */
    @SuppressWarnings("ALL")
    public static String getVoucher(String uri) {
        String voucher;
        final Map<String, List<String>> params = getParams(uri);
        if (params.get(V_PARAM_NAME) == null || params.get(V_PARAM_NAME).isEmpty()) {
            if (params.get(VOUCHER_PARAM_NAME) == null || params.get(VOUCHER_PARAM_NAME).isEmpty()) {
                return null;
            } else {
                voucher = params.get(VOUCHER_PARAM_NAME).get(0);
            }
        } else {
            voucher = params.get(V_PARAM_NAME).get(0);
        }
        return voucher;
    }

    /**
     * Controller Push Param
     */
    @Data
    @Accessors(chain = true)
    public static class PushParam implements Serializable {
        private String name;
        private String content;
        private List<String> users;
    }

}
