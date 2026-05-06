package club.p6e.coat.domain;

import java.io.Serializable;

/**
 * Entity
 *
 * @author lidashuang
 * @version 1.0
 */
public interface Entity<ID extends Identifier> extends Serializable {

    /**
     * ID
     *
     * @return ID
     */
    ID id();

}
