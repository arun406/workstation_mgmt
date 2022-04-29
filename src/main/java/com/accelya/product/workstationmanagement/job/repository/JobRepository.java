package com.accelya.product.workstationmanagement.job.repository;

import com.accelya.product.workstationmanagement.job.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, Integer> {
    @Override
    Page<Job> findAll(Pageable pageable);
}
