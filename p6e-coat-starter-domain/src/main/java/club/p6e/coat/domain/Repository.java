package club.p6e.coat.domain;

import club.p6e.coat.environment.EnvironmentContext;
import club.p6e.coat.pageable.PageableContext;
import club.p6e.coat.searchable.SearchableContext;
import club.p6e.coat.sortable.SortableContext;

/**
 * Repository
 *
 * @author lidashuang
 * @version 1.0
 */
public interface Repository<T extends Entity<ID>, ID extends Identifier> {

    /**
     * Save
     *
     * @param environment Environment Context Object
     * @param t           Entity Object
     * @return Entity Object
     */
    T save(EnvironmentContext environment, T t);

    /**
     * Delete
     *
     * @param id Entity Object
     * @return Entity Object
     */
    T delete(EnvironmentContext environment, ID id);

    /**
     * Get
     *
     * @param id Entity Object
     * @return Entity Object
     */
    T findById(EnvironmentContext environment, ID id);

    /**
     * List
     *
     * @param searchable Searchable Context Object
     * @param sortable   Sortable Context Object
     * @param pageable   Pageable Context Object
     * @return List Aggregate Object
     */
    ListAggregate<T> list(EnvironmentContext environment, SearchableContext searchable, SortableContext sortable, PageableContext pageable);

}
