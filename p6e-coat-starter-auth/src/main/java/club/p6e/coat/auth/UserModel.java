package club.p6e.coat.auth;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class UserModel implements User, Serializable {


    @Override
    public String id() {
        return "";
    }

    @Override
    public String password() {
        return "";
    }

    @Override
    public String serialize() {
        return "";
    }

    @Override
    public Map<String, Object> toMap() {
        return Map.of();
    }

}
