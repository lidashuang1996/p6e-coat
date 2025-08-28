package club.p6e.coat.auth.web;

import club.p6e.coat.common.utils.SpringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Request Parameter Validator
 *
 * @author lidashuang
 * @version 1.0
 */
public interface RequestParameterValidator {

    /**
     * Execute Validator
     *
     * @param request  Http Servlet Request
     * @param response Http Servlet Response
     * @param param    T Param Object
     * @return T Result Object
     */
    static <T> T run(HttpServletRequest request, HttpServletResponse response, T param) {
        if (param == null) {
            return null;
        } else {
            final Map<String, RequestParameterValidator> data = SpringUtil.getBeans(RequestParameterValidator.class);
            final List<RequestParameterValidator> list = new ArrayList<>();
            for (RequestParameterValidator value : data.values()) {
                if (value.type().equals(param.getClass())) {
                    list.add(value);
                }
            }
            final AtomicReference<T> result = new AtomicReference<>(param);
            if (!list.isEmpty()) {
                list.sort(Comparator.comparingInt(RequestParameterValidator::order));
                list.forEach(v -> result.set(v.execute(request, response, param)));
            }
            return result.get();
        }
    }

    /**
     * Order
     *
     * @return Order Object
     */
    int order();

    /**
     * Class Type
     *
     * @return Class Object
     */
    Class<?> type();

    /**
     * Execute Validator
     *
     * @param param T Param Object
     * @return T Result Object
     */
    <T> T execute(HttpServletRequest request, HttpServletResponse response, T param);

}
