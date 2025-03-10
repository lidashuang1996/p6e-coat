package club.p6e.coat.common.sortable;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lidashuang
 * @version 1.0
 */
public class JpaSortableConverter {

    public static Sort toSort(SortableContext context) {
        return toSort(context, null);
    }

    public static Sort toSort(SortableContext context, Sort sort) {
        return execute(context, sort);
    }

    public static void injectSort(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder builder, SortableContext context) {
        injectSort(root, query, builder, context, null);
    }

    public static void injectSort(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder builder, SortableContext context, Sort sort) {
        execute(root, query, builder, context, sort);
    }

    private static Sort execute(SortableContext context, Sort sort) {
        if (context == null) {
            return sort;
        }
        final List<Sort.Order> orders = new ArrayList<>();
        for (final SortableAbstract.Option option : context) {
            if (SortableAbstract.ASC.equals(option.getCondition())) {
                orders.add(Sort.Order.asc(option.getContent()));
            }
            if (SortableAbstract.DESC.equals(option.getCondition())) {
                orders.add(Sort.Order.desc(option.getContent()));
            }
        }
        if (orders.isEmpty()) {
            return sort;
        } else {
            return Sort.by(orders);
        }
    }

    private static void execute(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder builder, SortableContext context, Sort sort) {
        final List<Order> orders = new ArrayList<>();
        if (context != null) {
            for (final SortableAbstract.Option option : context) {
                if (SortableAbstract.ASC.equals(option.getCondition())) {
                    orders.add(builder.asc(root.get(option.getContent())));
                }
                if (SortableAbstract.DESC.equals(option.getCondition())) {
                    orders.add(builder.desc(root.get(option.getContent())));
                }
            }
        }
        if (orders.isEmpty() && sort != null) {
            for (final Sort.Order order : sort) {
                if (order.isAscending()) {
                    orders.add(builder.asc(root.get(order.getProperty())));
                } else {
                    orders.add(builder.desc(root.get(order.getProperty())));
                }
            }
        }
        query.orderBy(orders);
    }

}
