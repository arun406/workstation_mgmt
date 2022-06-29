package com.accelya.product.workstationmanagement.job.repository;

import com.accelya.product.workstationmanagement.job.model.Job;
import com.accelya.product.workstationmanagement.job.model.JobParameters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobParametersRepository extends JpaRepository<JobParameters, Integer> {

    Optional<JobParameters> findById(Integer id);

   /* @Query("select jp from JobParameters jp where jp.flight.flightNumber = :flightNumber " +
            "and jp.flight.carrier = :flightCarrier " +
            "and jp.flight.flightDate = :flightDate " +
            "and jp.flight.extensionNumber = :flightExtension " +
            "and jp.uld.carrierCode = :uldCarrier " +
            "and jp.uld.uldType = :uldType " +
            "and jp.uld.uldSerialNumber = :uldSerialNumber " +
            "and jp.loadPlanVersion = :loadPlanVersion")
    List<JobParameters> findByULDAndFlightAndLoadPlanVersion(@Param("uldCarrier") String uldCarrier,
                                                             @Param("uldType") String uldType,
                                                             @Param("uldSerialNumber") String uldNumber,
                                                             @Param("flightCarrier") String flightCarrier,
                                                             @Param("flightNumber") String flightNumber,
                                                             @Param("flightExtension") String flightExtension,
                                                             @Param("flightDate") LocalDate flightDate,
                                                             @Param("loadPlanVersion") Integer loadPlanVersion);*/

    /*    @Query("select jp.job from JobParameters jp where jp.flight.flightNumber = :flightNumber " +
                "and jp.flight.carrier = :flightCarrier " +
                "and jp.flight.flightDate = :flightDate " +
                "and jp.flight.extensionNumber = :flightExtension " +
                "and jp.uld.carrierCode = :uldCarrier " +
                "and jp.uld.uldType = :uldType " +
                "and jp.uld.uldSerialNumber = :uldSerialNumber ")*/
    @Query("select DISTINCT(jp) from JobParameters jp JOIN FETCH " +
            "jp.ulds uld " +
            "WHERE " +
            "jp.flight.flightNumber = :flightNumber " +
            "and jp.flight.carrier = :flightCarrier " +
            "and jp.flight.flightDate = :flightDate " +
            "and jp.flight.extensionNumber = :flightExtension " +
            "and uld.carrierCode = :uldCarrier " +
            "and uld.uldType = :uldType " +
            "and uld.uldSerialNumber = :uldSerialNumber ")
    List<JobParameters> findByULDAndFlight(@Param("uldCarrier") String uldCarrier,
                                           @Param("uldType") String uldType,
                                           @Param("uldSerialNumber") String uldNumber,
                                           @Param("flightCarrier") String flightCarrier,
                                           @Param("flightNumber") String flightNumber,
                                           @Param("flightExtension") String flightExtension,
                                           @Param("flightDate") LocalDate flightDate);
}
