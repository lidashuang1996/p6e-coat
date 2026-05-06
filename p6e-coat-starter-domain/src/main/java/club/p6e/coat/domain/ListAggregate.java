package club.p6e.coat.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * List Aggregate
 *
 * @author lidashuang
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public abstract class ListAggregate<T extends Entity<?>> implements Aggregate<ListId>, Serializable {

    /**
     * List ID Object
     */
    protected final ListId id;

    /**
     * Page Number
     */
    protected int page;

    /**
     * Page Size
     */
    protected int size;

    /**
     * Total
     */
    protected long total;

    /**
     * List Object
     */
    protected List<T> list;

    /**
     * Constructor Initialization
     *
     * @param page  Page Number
     * @param size  Page Size
     * @param total Total
     * @param list  List Object
     */
    public ListAggregate(int page, int size, long total, List<T> list) {
        this.page = page;
        this.size = size;
        this.total = total;
        this.list = list;
        this.id = new ListId();
    }

    @Override
    public ListId id() {
        return id;
    }

}
