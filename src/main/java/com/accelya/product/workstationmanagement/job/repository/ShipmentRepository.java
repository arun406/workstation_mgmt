package com.accelya.product.workstationmanagement.job.repository;

import com.accelya.product.workstationmanagement.job.model.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Integer> {
}
