package club.p6e.coat.domain;

import club.p6e.coat.environment.EnvironmentContext;
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
    default T save(EnvironmentContext environment, T t) {
        throw new UnsupportedOperationException("EmptyRepository does not support the save operation.");
    }

    @Override
    default T delete(EnvironmentContext environment, ID id) {
        throw new UnsupportedOperationException("EmptyRepository does not support the delete operation.");
    }

    @Override
    default T findById(EnvironmentContext environment, ID id) {
        throw new UnsupportedOperationException("EmptyRepository does not support the findById operation.");
    }

    @Override
    default ListAggregate<T> list(EnvironmentContext environment, SearchableContext searchable, SortableContext sortable, PageableContext pagination) {
        throw new UnsupportedOperationException("EmptyRepository does not support the list operation.");
    }

}
