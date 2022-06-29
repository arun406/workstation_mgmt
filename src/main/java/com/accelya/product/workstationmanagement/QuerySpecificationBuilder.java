package com.accelya.product.workstationmanagement;

import com.accelya.product.workstationmanagement.job.model.Flight;
import com.accelya.product.workstationmanagement.job.model.Job;
import com.accelya.product.workstationmanagement.job.model.JobParameters;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class QuerySpecificationBuilder<T> {
    private final List<SearchCriteria> params;

    /**
     * @return
     */
    public Specification<T> build() {
        if (params.size() == 0) {
            return null;
        }
        List<Specification> specs = params.stream()
                .map(criteria -> (Specification<T>) (root, query, builder) -> {
                    if (criteria.getOperation().equalsIgnoreCase(">")) {
                        return builder.greaterThanOrEqualTo(
                                root.<String>get(criteria.getKey()), criteria.getValue().toString());
                    } else if (criteria.getOperation().equalsIgnoreCase("<")) {
                        return builder.lessThanOrEqualTo(
                                root.<String>get(criteria.getKey()), criteria.getValue().toString());
                    } else if (criteria.getOperation().equalsIgnoreCase(":")) {
                        if (root.get(criteria.getKey()).getJavaType() == String.class) {
                            return builder.like(
                                    root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
                        } else if (root.get(criteria.getKey()).getJavaType() == List.class) {
                            return builder.like(root.get("shc").as(String.class), "%" + criteria.getValue() + "%");
                        }
                    }
                    return null;
                })
                .collect(Collectors.toList());
        Specification<T> result = specs.get(0);
        for (int i = 1; i < params.size(); i++) {
            result = Specification.where(result).and(specs.get(i));
        }
        return result;
    }
}
