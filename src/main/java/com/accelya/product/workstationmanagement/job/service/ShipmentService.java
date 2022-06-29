package com.accelya.product.workstationmanagement.job.service;

import com.accelya.product.workstationmanagement.Constants;
import com.accelya.product.workstationmanagement.job.mapper.JobMapper;
import com.accelya.product.workstationmanagement.job.model.JobParameters;
import com.accelya.product.workstationmanagement.job.model.Shipment;
import com.accelya.product.workstationmanagement.job.repository.ShipmentRepository;
import com.accelya.product.workstationmanagement.job.transferobjects.ShipmentDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ShipmentService {

    final private ShipmentRepository shipmentRepository;
    final private JobMapper jobMapper;

    /**
     * @param requestShipment
     * @param newShipments
     * @param jobParameters
     * @param status
     */
    public void processShipments(ShipmentDTO requestShipment, List<Shipment> newShipments, JobParameters jobParameters, String status) {

        String newShipmentId = requestShipment.getDocumentType() + requestShipment.getDocumentPrefix() + requestShipment.getDocumentNumber();
        List<Shipment> existingShipments = jobParameters.getShipments();
        boolean match = existingShipments.stream()
                .anyMatch(s -> newShipmentId.equalsIgnoreCase(s.getDocumentType() + s.getDocumentPrefix() + s.getDocumentNumber()));

        log.debug("matched {}", match);
        Shipment shipment = jobMapper.dtoToEntity(requestShipment);
        shipment.setJobParameters(jobParameters);

        if (!match) {
            processNewShipments(newShipments, status, shipment);
        } else {
            processExistingShipments(newShipments, status, existingShipments, shipment);
        }
    }

    /**
     * @param newShipments
     * @param status
     * @param existingShipments
     * @param shipment
     */
    private void processExistingShipments(List<Shipment> newShipments, String status,
                                          List<Shipment> existingShipments, Shipment shipment) {
        String newShipmentId = shipment.getDocumentType() + shipment.getDocumentPrefix() + shipment.getDocumentNumber();
        existingShipments.stream()
                .filter(s -> newShipmentId.equalsIgnoreCase(s.getDocumentType() + s.getDocumentPrefix() + s.getDocumentNumber()))
                .findFirst().ifPresent(s -> {
                    if (!Constants.FINISHED.equals(status)) {
                        shipment.setId(s.getId());
                        shipment.setModifiedBy("ADMIN");
                        shipment.setModifiedDate(ZonedDateTime.now());
                        // update shipment
                        saveShipment(shipment);
                    } else {

                        shipment.setId(null);// it should 'null' already
                        shipment.setCreatedBy("ADMIN");
                        shipment.setCreatedDate(ZonedDateTime.now());
                        
                        // assumption is that new shipment pieces are always greater than old shipment pieces
                        if (shipment.getPiece() > s.getPiece()) {
                            shipment.setPiece(shipment.getPiece() - s.getPiece());
                        }
                        if (shipment.getWeight() > s.getWeight()) {
                            shipment.setWeight(shipment.getWeight() - s.getWeight());
                        }
                        if (shipment.getVolume() > s.getVolume()) {
                            shipment.setVolume(shipment.getVolume() - s.getVolume());
                        }
                        // add to the new shipment list for creating a new job
                        newShipments.add(shipment);
                    }
                });
    }

    /**
     * @param newShipments
     * @param status
     * @param shipment
     */
    private void processNewShipments(List<Shipment> newShipments, String status, Shipment shipment) {
        log.debug("shipment not exists in the previous jobs. add shipment to existing job");
        shipment.setCreatedBy("ADMIN");
        shipment.setCreatedDate(ZonedDateTime.now());
        if (!Constants.FINISHED.equals(status)) {
            // save shipment
            saveShipment(shipment);
        } else {
            newShipments.add(shipment);
        }
    }

    private Shipment processShipment(Shipment existingShipment, String status, Shipment shipment) {
        log.debug("existing shipment id: {}", existingShipment.getId());
        shipment.setId(existingShipment.getId());

        // if job is in finished status, create a new job for additional pieces
        if (Constants.FINISHED.equalsIgnoreCase(status)) {
            shipment.setId(null);
        }
        return shipment;
    }

    /**
     * create new shipments
     *
     * @param jobParameters
     * @param shipment
     * @param jobStatus
     * @param newShipments
     */
    public void createShipments(JobParameters jobParameters, Shipment shipment, String jobStatus, List<Shipment> newShipments) {
        // this is new shipment, add to the existing jobs.
        if (Constants.CREATED.equalsIgnoreCase(jobStatus)) {
            // updated the uld with new shipment
            // incoming ULD as additional shipments
            // save shipment to database table
            saveShipment(shipment, jobParameters);
        } else if (Constants.RUNNING.equalsIgnoreCase(jobStatus)) {
            // updated the uld with new shipment details and send notification
            // save shipment to database table. send notification
            saveShipment(shipment, jobParameters);
            // send notification
            log.debug("sending notification to user who started the job");
        } else if (Constants.FINISHED.equalsIgnoreCase(jobStatus)) {
            // collect all new shipments
            if (shipment.getId() == null) {
                newShipments.add(shipment);
            }
            // send notification
            log.debug("sending notification to user who started the job");
        }
    }

    /**
     * find and delete the shipments
     *
     * @param requestShipments
     * @param shipmentList
     * @param status
     */
    public void findAndDeleteShipments(List<ShipmentDTO> requestShipments, List<Shipment> shipmentList, String status) {
        // find and remove shipments from uld
        Set<Shipment> shipmentsToDeleted = shipmentList.stream()
                .filter(i -> !requestShipments.stream().anyMatch(j -> (i.getDocumentType() + i.getDocumentPrefix() + i.getDocumentNumber())
                        .equalsIgnoreCase(j.getDocumentType() + j.getDocumentPrefix() + j.getDocumentNumber())))
                .collect(Collectors.toSet());

        log.debug("shipments to be deleted : {}", shipmentsToDeleted);

        if (shipmentsToDeleted != null && !shipmentsToDeleted.isEmpty()) {
            deleteShipments(shipmentsToDeleted);
        }
        if (Constants.RUNNING.equalsIgnoreCase(status)) {
            log.debug("send notification....");
        }
    }

    public Shipment saveShipment(Shipment shipment, JobParameters jobParameters) {
        shipment.setJobParameters(jobParameters);
        return this.shipmentRepository.save(shipment);
    }

    public void deleteShipments(Set<Shipment> shipmentsToDeleted) {
        List<Integer> list = shipmentsToDeleted.stream().map(Shipment::getId).collect(Collectors.toList());
        log.debug("shipments to be deleted: {}", list);
        this.shipmentRepository.deleteAllByIdInBatch(list);
    }

    public void deleteShipment(Shipment shipment) {
        this.shipmentRepository.deleteById(shipment.getId());
    }

    public void saveShipment(Shipment shipment) {
        this.shipmentRepository.save(shipment);
    }
}
