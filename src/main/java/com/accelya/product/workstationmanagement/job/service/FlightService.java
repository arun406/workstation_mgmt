package com.accelya.product.workstationmanagement.job.service;

import com.accelya.product.workstationmanagement.job.model.Flight;
import com.accelya.product.workstationmanagement.job.repository.FlightRepository;
import com.accelya.product.workstationmanagement.job.transferobjects.FlightDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightService {
    final private FlightRepository flightRepository;

    public List<Flight> findFlights(FlightDTO flight) {
        return this.flightRepository.findByFlightNumberAndCarrierAndFlightDateAndOptionalExtension(flight.getTransportInfo().getCarrier(),
                flight.getTransportInfo().getNumber(), flight.getTransportInfo().getExtensionNumber(),
                flight.getTransportInfo().getFlightDate(), flight.getBoardPoint(), flight.getOffPoint());

    }
}
