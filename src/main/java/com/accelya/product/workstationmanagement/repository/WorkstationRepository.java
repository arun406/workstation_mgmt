package com.accelya.product.workstationmanagement.repository;

import com.accelya.product.workstationmanagement.model.Workstation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkstationRepository extends JpaRepository<Workstation, Integer> {


    @Override
    Page<Workstation> findAll(Pageable pageable);
}
