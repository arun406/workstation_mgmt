package com.accelya.product.workstationmanagement;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class SpecificationUtil {

    public static <T> Specification<T> bySearchQuery(SearchQuery searchQuery, Class<T> clazz) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // Add Predicates for tables to be joined
            List<JoinColumnProps> joinColumnProps = searchQuery.getJoinColumnProps();
            if (joinColumnProps != null && !joinColumnProps.isEmpty()) {
                for (JoinColumnProps joinColumnProp : joinColumnProps) {
                    addJoinColumnProps(predicates, joinColumnProp, criteriaBuilder, root);
                }
            }
            List<SearchCriteria> searchFilters = searchQuery.getSearchCriteria();
            if (searchFilters != null && !searchFilters.isEmpty()) {
                for (final SearchCriteria searchFilter : searchFilters) {
                    addPredicates(predicates, searchFilter, criteriaBuilder, root);
                }
            }
            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static <T> void addJoinColumnProps(List<Predicate> predicates, JoinColumnProps joinColumnProp,
                                               CriteriaBuilder criteriaBuilder, Root<T> root) {
        SearchCriteria searchFilter = joinColumnProp.getSearchCriteria();
        Join<Object, Object> joinParent = root.join(joinColumnProp.getJoinColumnName());

        String property = searchFilter.getKey();
        Path expression = joinParent.get(property);

        addPredicate(predicates, searchFilter, criteriaBuilder, expression);
    }

    private static <T> void addPredicates(List<Predicate> predicates, SearchCriteria searchFilter,
                                          CriteriaBuilder criteriaBuilder, Root<T> root) {
        String property = searchFilter.getKey();
        Path expression = root.get(property);
        addPredicate(predicates, searchFilter, criteriaBuilder, expression);
    }

    private static void addPredicate(List<Predicate> predicates, SearchCriteria searchFilter,
                                     CriteriaBuilder criteriaBuilder, Path expression) {
        switch (searchFilter.getOperation()) {
            case "=":
                predicates.add(criteriaBuilder.equal(expression, searchFilter.getValue()));
                break;
            case "LIKE":
                predicates.add(criteriaBuilder.like(expression, "%" + searchFilter.getValue() + "%"));
                break;
            case "IN":
                predicates.add(criteriaBuilder.in(expression).value(searchFilter.getValue()));
                break;
            case ">":
                predicates.add(criteriaBuilder.greaterThan(expression, (Comparable) searchFilter.getValue()));
                break;
            case "<":
                predicates.add(criteriaBuilder.lessThan(expression, (Comparable) searchFilter.getValue()));
                break;
            case ">=":
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(expression, (Comparable) searchFilter.getValue()));
                break;
            case "<=":
                predicates.add(criteriaBuilder.lessThanOrEqualTo(expression, (Comparable) searchFilter.getValue()));
                break;
            case "!":
                predicates.add(criteriaBuilder.notEqual(expression, searchFilter.getValue()));
                break;
            case "IsNull":
                predicates.add(criteriaBuilder.isNull(expression));
                break;
            case "NotNull":
                predicates.add(criteriaBuilder.isNotNull(expression));
                break;
            default:
                System.out.println("Predicate is not matched");
                throw new IllegalArgumentException(searchFilter.getOperation() + " is not a valid predicate");
        }
    }
}
