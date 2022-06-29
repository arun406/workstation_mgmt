package com.accelya.product.workstationmanagement.job.repository;

import com.accelya.product.workstationmanagement.job.model.ULD;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ULDRepository extends JpaRepository<ULD, Integer> {
}
