package com.accelya.product.workstationmanagement.workstation.repository;

import com.accelya.product.workstationmanagement.workstation.model.Workstation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkstationRepository extends JpaRepository<Workstation, Integer>, JpaSpecificationExecutor<Workstation> {
    @Override
    Page<Workstation> findAll(Pageable pageable);
}
