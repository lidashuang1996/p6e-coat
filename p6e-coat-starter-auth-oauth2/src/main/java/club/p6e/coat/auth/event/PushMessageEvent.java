package club.p6e.coat.auth.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Push Message Event
 *
 * @author lidashuang
 * @version 1.0
 */
@Getter
public class PushMessageEvent extends ApplicationEvent implements Serializable {

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
     * Constructor Initialization
     *
     * @param source     Source Object
     * @param recipients Recipient List Object
     * @param type       Type Content
     * @param language   Language Content
     * @param data       Data Object
     */
    public PushMessageEvent(Object source, List<String> recipients, String type, String language, Map<String, Object> data) {
        super(source);
        this.type = type;
        this.data = data;
        this.language = language;
        this.recipients = recipients;
    }

}
