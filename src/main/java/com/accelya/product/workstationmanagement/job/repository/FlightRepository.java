package com.accelya.product.workstationmanagement.job.repository;

import com.accelya.product.workstationmanagement.job.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Integer> {

    @Query("select flight from Flight flight where flight.carrier = :carrier and flight.flightNumber = :flightNumber and " +
            "(:extension is null or flight.extensionNumber = :extension) and " +
            "flight.flightDate = :flightDate and " +
            "flight.boardPoint = :boardPoint and " +
            "flight.offPoint = :offPoint")
    public List<Flight> findByFlightNumberAndCarrierAndFlightDateAndOptionalExtension(@Param("carrier") String carrier,
                                                                                      @Param("flightNumber") String flightNumber,
                                                                                      @Param("extension") String extension,
                                                                                      @Param("flightDate") LocalDate flightDate,
                                                                                      @Param("boardPoint") String boardPoint,
                                                                                      @Param("offPoint") String offPoint);
}
