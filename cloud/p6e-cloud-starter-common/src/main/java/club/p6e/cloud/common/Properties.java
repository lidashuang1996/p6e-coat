package club.p6e.cloud.common;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;

import java.io.Serializable;

/**
 * Properties
 *
 * @author lidashuang
 * @version 1.0
 */
@Data
@Primary
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "p6e.cloud.common")
public class Properties extends club.p6e.coat.common.Properties implements Serializable {
}