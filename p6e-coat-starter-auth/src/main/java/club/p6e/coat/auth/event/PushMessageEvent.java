package club.p6e.coat.auth.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;
import java.time.Clock;
import java.util.List;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
public class PushMessageEvent extends ApplicationEvent implements Serializable {


    @Getter
    private final List<String> recipients;

    @Getter
    private final String type;

    @Getter
    private final String language;

    @Getter
    private final Map<String, Object> data;

    public PushMessageEvent(Object source,  List<String> recipients, String type, String language, Map<String, Object> data) {
        super(source);
        this.type = type;
        this.data = data;
        this.language = language;
        this.recipients = recipients;
    }

}
