package club.p6e.coat.auth.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Reactive Push Verification Code Event
 *
 * @author lidashuang
 * @version 1.0
 */
@Getter
public class ReactivePushVerificationCodeEvent extends ApplicationEvent implements Serializable {

    /**
     * Recipient List Object
     */
    private final List<String> recipients;

    /**
     * Type Content
     */
    private final String type;

    /**
     * Language Content
     */
    private final String language;

    /**
     * Data Object
     */
    private final Map<String, Object> data;

    /**
     * Callback Object
     */
    @Setter
    @SuppressWarnings("ALL")
    private Callback callback;

    /**
     * Constructor Initialization
     *
     * @param source     Source Object
     * @param recipients Recipient List Object
     * @param type       Type
     * @param language   Language
     * @param data       Data Object
     */
    public ReactivePushVerificationCodeEvent(Object source, List<String> recipients, String type, String language, Map<String, Object> data) {
        super(source);
        this.type = type;
        this.data = data;
        this.language = language;
        this.recipients = recipients;
    }

    /**
     * Callback Object
     */
    public interface Callback {

        /**
         * Execute Callback
         *
         * @return Result
         */
        Mono<String> execute();

    }

}
