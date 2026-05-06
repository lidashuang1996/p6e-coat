package club.p6e.coat.domain;

import club.p6e.coat.pageable.PageableContext;
import club.p6e.coat.searchable.SearchableContext;
import club.p6e.coat.sortable.SortableContext;

/**
 * Empty Repository
 *
 * @author lidashuang
 * @version 1.0
 */
public interface EmptyRepository<T extends Entity<ID>, ID extends Identifier> extends Repository<T, ID> {

    @Override
    default T save(T t) {
        return null;
    }

    @Override
    default T delete(ID id) {
        return null;
    }

    @Override
    default T findById(ID id) {
        return null;
    }

    @Override
    default ListAggregate<T> list(SearchableContext searchable, SortableContext sortable, PageableContext pagination) {
        return null;
    }

}
