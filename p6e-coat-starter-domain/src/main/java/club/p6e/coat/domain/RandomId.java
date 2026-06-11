package club.p6e.coat.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.UUID;

/**
 * Random ID
 *
 * @author lidashuang
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class RandomId implements Identifier, Serializable {

    /**
     * ID
     */
    private String id;

    /**
     * Constructor Initialization
     */
    public RandomId() {
        this.id = UUID.randomUUID().toString().replace("-", "");
    }

}
