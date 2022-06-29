package com.accelya.product.workstationmanagement;

import com.accelya.product.workstationmanagement.appointment.model.Appointment;
import com.accelya.product.workstationmanagement.workstation.model.Workstation;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class AppointmentQuerySpecification implements Specification<Appointment> {

    private SearchCriteria criteria;

    public AppointmentQuerySpecification(SearchCriteria searchCriteria) {
        this.criteria = searchCriteria;
    }

    @Override
    public Predicate toPredicate(Root<Appointment> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

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
            } else {
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        }
        return null;
    }
}
