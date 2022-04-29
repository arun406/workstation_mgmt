package com.accelya.product.workstationmanagement.loadplan.repository;

import com.accelya.product.workstationmanagement.loadplan.model.FlightLoadPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightLoadPlanRepository extends JpaRepository<FlightLoadPlan, Integer> {
}
