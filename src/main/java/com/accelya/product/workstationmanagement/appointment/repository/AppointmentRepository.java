package com.accelya.product.workstationmanagement.appointment.repository;

import com.accelya.product.workstationmanagement.appointment.model.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer>, JpaSpecificationExecutor<Appointment> {

    /*@Query("select appointment from Appointment appointment where (:workstationCode is null or appointment.workstation.code = :workstationCode) " +
            "and (:appointmentDate is null or appointment.date = :appointmentDate) " +
            "and (:jobCode is null or appointment.job.code = :jobCode) ")
    Page<Appointment> findByWorkstationCodeAndAppointmentDateAndJobCode(@Param("workstationCode") String workstationCode,
                                                                        @Param("jobCode") String jobCode,
                                                                        @Param("appointmentDate") LocalDate appointmentDate,
                                                                        Pageable pageable);

    @Query("select appointment from Appointment appointment where (:workstationCode is null or appointment.workstation.code = :workstationCode) " +
            "and (:appointmentDate is null or appointment.date = :appointmentDate) ")
    List<Appointment> findByWorkstationCodeAndAppointmentDateAndJobCode(@Param("workstationCode") String workstationCode,
                                                                        @Param("appointmentDate") LocalDate appointmentDate);*/

    @Query("select appointment from Appointment appointment where appointment.workstation.id = :workstationId")
    Page<Appointment> findByWorkstationId(@Param("workstationId") Integer workstationId, Pageable pageable);

    @Query("select appointment from Appointment appointment where appointment.job.id = :jobId")
    Page<Appointment> findByJobId(@Param("jobId") Integer jobId, Pageable pageable);

    @Query("select appointment from Appointment appointment where appointment.job.id = :jobId")
    Appointment findByJobId(@Param("jobId") Integer jobId);
}
