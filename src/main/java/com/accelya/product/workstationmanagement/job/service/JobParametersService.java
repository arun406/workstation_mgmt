package com.accelya.product.workstationmanagement.job.service;

import com.accelya.product.workstationmanagement.Constants;
import com.accelya.product.workstationmanagement.job.model.*;
import com.accelya.product.workstationmanagement.job.repository.FlightRepository;
import com.accelya.product.workstationmanagement.job.repository.JobParametersRepository;
import com.accelya.product.workstationmanagement.job.repository.JobRepository;
import com.accelya.product.workstationmanagement.job.transferobjects.FlightDTO;
import com.accelya.product.workstationmanagement.job.transferobjects.JobParametersDTO;
import com.accelya.product.workstationmanagement.job.transferobjects.ShipmentDTO;
import com.accelya.product.workstationmanagement.job.transferobjects.UldDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class JobParametersService {
    final private JobRepository jobRepository;
    final private JobParametersRepository jobParametersRepository;
    final private FlightRepository flightRepository;
    final private ShipmentService shipmentService;
    final private FlightService flightService;

    /**
     * Check and return jobs for the same flight load plan
     *
     * @param jobParameters
     * @return
     */
    public Job processJobRequest(JobParametersDTO jobParameters) {
        List<Shipment> newShipments = new ArrayList<>();
        FlightDTO flight = jobParameters.getFlight();
        // get the all jobs for request flight
        List<Flight> flights = this.flightService.findFlights(flight);
        if (flights.isEmpty()) {
            return null; //no previous load plan exists for a flight
        }
        // check for load plan version and get the latest version. Throw exception if requested load plan version is not latest
        Optional<Job> jobOptional = flights.stream()
                .map(f -> f.getJobParameters())
                .sorted(Comparator.comparing(JobParameters::getLoadPlanVersion).reversed())
                .map(JobParameters::getJob)
                .findFirst();
        if (!jobOptional.isPresent()) {
            return null; //no previous load plan exists
        }
        Job existingLatestJob = jobOptional.get();
        // update existing job's load plan version to latest
        JobParameters latestJobJobParameters = existingLatestJob.getJobParameters();

        // check job has same uld/uld group
        boolean uldOrUldGroupExists = isUldOrUldGroupExists(jobParameters, existingLatestJob, latestJobJobParameters);
        if (uldOrUldGroupExists) {
            //System links the Job to the new Flight load plan version
            if (jobParameters.getLoadPlanVersion() > latestJobJobParameters.getLoadPlanVersion()) {
                latestJobJobParameters.setLoadPlanVersion(jobParameters.getLoadPlanVersion()); // update the load plan version to requested
            } else {
                return existingLatestJob;
            }
            // compare shipment details
            List<ShipmentDTO> requestShipments = jobParameters.getShipments();
            for (ShipmentDTO requestShipment : requestShipments) {
                shipmentService.processShipments(requestShipment, newShipments, existingLatestJob.getJobParameters(), existingLatestJob.getStatus());
            }
            shipmentService.findAndDeleteShipments(requestShipments, latestJobJobParameters.getShipments(), existingLatestJob.getStatus());
            // create a new job with the new shipments if the job is already in Finished status
            if (!newShipments.isEmpty()) {
                return createNewJob(existingLatestJob, jobParameters, newShipments, latestJobJobParameters);
            }
            return existingLatestJob;
        }
        return null;
    }

    private boolean isUldOrUldGroupExists(JobParametersDTO jobParameters, Job existingLatestJob, JobParameters latestJobJobParameters) {
        List<ULD> existingULDs = existingLatestJob.getJobParameters().getUlds();
        boolean uldOrUldGroupExists = false;
        // if request is for a single uld
        if (!jobParameters.getGrouped()) {
            UldDTO uldDTO = jobParameters.getUlds().get(0);
            uldOrUldGroupExists = existingULDs.stream()
                    .anyMatch(uld -> uld.getUldType().equalsIgnoreCase(uldDTO.getUldType()));
        } else {
            // grouped uld, check uld group already exists
            String uldGroupCode = jobParameters.getUldGroupCode();
            if (latestJobJobParameters.getUldGroupCode().equalsIgnoreCase(uldGroupCode)) {
                uldOrUldGroupExists = true;
            }
        }
        return uldOrUldGroupExists;
    }


    /**
     * create new job
     *
     * @param existingLatestJob
     * @param jobParameters
     * @param newShipments
     * @param latestJobJobParameters
     * @return
     */
    private Job createNewJob(Job existingLatestJob, JobParametersDTO jobParameters, List<Shipment> newShipments,
                             JobParameters latestJobJobParameters) {
        return processJobRequest(existingLatestJob, jobParameters, newShipments, latestJobJobParameters);
    }

    /**
     * Build Job
     *
     * @param existingLatestJob
     * @param jobParameters
     * @param newShipments
     * @param latestJobJobParameters
     * @return
     */
    private Job processJobRequest(Job existingLatestJob, JobParametersDTO jobParameters, List<Shipment> newShipments,
                                  JobParameters latestJobJobParameters) {
        List<ULD> existingJobULDs = latestJobJobParameters.getUlds();
        Flight flight = latestJobJobParameters.getFlight();
        List<ULD> newULDs = new ArrayList<>();
        for (ULD existingJobULD : existingJobULDs) {
            ULD newULD = ULD.builder()
                    .uldSerialNumber(existingJobULD.getUldSerialNumber())
                    .uldType(existingJobULD.getUldType())
                    .name(existingJobULD.getName())
                    .carrierCode(existingJobULD.getCarrierCode())
                    .transferHandlingCode(existingJobULD.getTransferHandlingCode())
                    .tareWeight(existingJobULD.getTareWeight())
                    .volumeUnit(existingJobULD.getVolumeUnit())
                    .weightUnit(existingJobULD.getWeightUnit())
                    .rateType(existingLatestJob.getType())
                    .shc(existingJobULD.getShc())
                    .priorityCode(existingJobULD.getPriorityCode())
                    .maximumWeight(existingJobULD.getMaximumWeight())
                    .maximumVolume(existingJobULD.getMaximumVolume())
                    .loadingCode(existingJobULD.getLoadingCode())
                    .contourCode(existingJobULD.getContourCode())
                    .actualWeight(existingJobULD.getActualWeight())
                    .actualVolume(existingJobULD.getActualVolume())
                    .status(existingJobULD.getStatus())
                    .build();
            newULDs.add(newULD);
        }

        Flight newFlight = Flight.builder()
                .ata(flight.getAta())
                .eta(flight.getEta())
                .sta(flight.getSta())
                .aircraftCategory(flight.getAircraftCategory())
                .iataAircraftType(flight.getIataAircraftType())
                .aircraftRegistration(flight.getAircraftRegistration())
                .offPoint(flight.getOffPoint())
                .boardPoint(flight.getBoardPoint())
                .carrier(flight.getCarrier())
                .extensionNumber(flight.getExtensionNumber())
                .flightDate(flight.getFlightDate())
                .flightNumber(flight.getFlightNumber())
                .atd(flight.getAtd())
                .etd(flight.getEtd())
                .std(flight.getStd())
                .build();

        JobParameters newJobParameter = JobParameters.builder()
                .shipments(newShipments)
                .ulds(newULDs)
                .loadPlanVersion(jobParameters.getLoadPlanVersion())// new load plan version
                .grouped(false)
                .flight(newFlight)
                .uldCount(1)
                .shipmentCount(newShipments.size())
                .build();

        newShipments.forEach(shipment -> shipment.setJobParameters(newJobParameter));
        newULDs.forEach(newULD ->
                newULD.setJobParameters(newJobParameter));
        newFlight.setJobParameters(newJobParameter);
        // create new Job
        Job newJob = Job.builder()
                .code(existingLatestJob.getCode())// keep the job  same as existing one
                .type(existingLatestJob.getType())
                .groupCode(existingLatestJob.getGroupCode())
                .groupName(existingLatestJob.getGroupName())
                .jobParameters(newJobParameter)
                .parent(existingLatestJob)
                .notes(existingLatestJob.getNotes())
                .remarks(existingLatestJob.getRemarks())
                .priority(existingLatestJob.getPriority())
                .status(Constants.CREATED)
                .updatable(existingLatestJob.getUpdatable())
                .build();
        newJobParameter.setJob(newJob);
        return newJob;
    }
}
