package com.accelya.product.workstationmanagement.job.service;

import com.accelya.product.workstationmanagement.Constants;
import com.accelya.product.workstationmanagement.SearchCriteria;
import com.accelya.product.workstationmanagement.appointment.model.Appointment;
import com.accelya.product.workstationmanagement.appointment.service.AppointmentService;
import com.accelya.product.workstationmanagement.appointment.transferobjects.AppointmentNotExists;
import com.accelya.product.workstationmanagement.job.mapper.JobMapper;
import com.accelya.product.workstationmanagement.job.model.*;
import com.accelya.product.workstationmanagement.job.repository.*;
import com.accelya.product.workstationmanagement.job.transferobjects.*;
import com.accelya.product.workstationmanagement.workstation.transferobjects.PageInfo;
import com.accelya.product.workstationmanagement.workstation.transferobjects.PagedData;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobService {

    public static final List<String> SHIPMENT_SEARCH_PROPERTIES = Arrays.asList("origin", "destination", "documentPrefix", "documentNumber", "documentType", "piece", "weight", "volume", "shc");
    public static final List<String> FLIGHT_SEARCH_PROPERTIES = Arrays.asList("carrier", "flightNumber", "boardPoint", "offPoint", "flightDate", "extensionNumber");
    public static final List<String> ULD_SEARCH_PROPERTIES = Arrays.asList("transferHandlingCode", "uldCarrierCode", "uldPriorityCode", "uldSerialNumber", "uldType", "contourCode");
    public static final List<String> JOB_SEARCH_PROPERTIES = Arrays.asList("priority", "type", "groupCode", "groupName", "updatable", "status");
    public static final List<String> LOAD_PLAN_SEARCH_PROPERTIES = Arrays.asList("loadPlanVersion", "grouped", "uldGroupCode");

    final private JobRepository jobRepository;
    final private JobParametersService jobParametersService;
    final private JobMapper jobMapper;
    final private ShipmentService shipmentService;
    final private JobStatusValidator jobStatusValidator;
    final private AppointmentService appointmentService;

    /**
     * get a single job
     *
     * @param id
     * @return
     */
    public JobDTO get(Integer id) {
        final Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "job not found"));
        return jobMapper.entityToDto(job);
    }

    /**
     * list the jobs
     *
     * @param pageable
     * @return
     */
    public PagedData<JobDTO> search(Map<String, String> params, Pageable pageable) {
        Specification<Job> specification = null;
        if (!params.isEmpty()) {
            specification = getJobSpecification(params);
        }
        final Page<Job> page = jobRepository.findAll(specification, pageable);
        final List<Job> jobs = page.getContent();

        final List<JobDTO> jobDTOs = jobMapper.entityListToDtoList(jobs);
        PageInfo pageInfo = PageInfo.builder().listSize(page.getTotalElements()).pageNumber(page.getNumber()).pageSize(page.getSize()).totalPages(page.getTotalPages()).build();
        return PagedData.<JobDTO>builder().pageInfo(pageInfo).list(jobDTOs).build();
    }


    /**
     * update shipment status
     *
     * @param jobId
     * @param shipmentId
     * @param action
     */
    public void updateShipmentStatus(Integer jobId, String shipmentId, String action) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "job not found"));

        List<Shipment> shipments = job.getJobParameters().getShipments().stream()
                .filter(shipment -> ((shipment.getDocumentType() + shipment.getDocumentPrefix() + shipment.getDocumentNumber())
                        .equalsIgnoreCase(shipmentId))
                ).collect(Collectors.toList());

        if (shipments.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "shipment not found");
        }
        shipments.forEach(shipment -> {
            String completed = action.equalsIgnoreCase("completed") ? "C" : "N";
            shipment.setStatus(completed);
            this.shipmentService.saveShipment(shipment);
        });
    }

    /**
     * Delete the by Job id
     *
     * @param jobId
     */
    public void deleteJob(Integer jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "job not found"));

        if (this.jobStatusValidator.validate(job.getStatus(), Constants.JOB_ACTION_DELETE)) {
            job.setStatus(Constants.DELETED);// logical delete
            this.jobRepository.save(job);
        }
    }

    /**
     * Update the Job action by job id
     *
     * @param jobId
     * @param action
     */
    public void updateJobStatus(Integer jobId, String action) throws AppointmentNotExists {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "job not found"));

        Appointment appointment = this.appointmentService.getAppointmentByJobId(jobId);
        if (appointment == null) {
            throw new AppointmentNotExists("Job cannot be started as no appointment is created yet.");
        }
        if (this.jobStatusValidator.validate(job.getStatus(), action)) {
            job.setStatus(getJobStatus(action));
            if (Constants.JOB_ACTION_START.equalsIgnoreCase(action)) {
                // set the appointment status to 'Started'
                // check appointment - Job can be started when appointment is exists for the job
                this.appointmentService.setAppointmentStatus(appointment, Constants.APPOINTMENT_STARTED);
            } else if (Constants.JOB_ACTION_END.equalsIgnoreCase(action)) {
                // set the appointment status to 'Ended'
                this.appointmentService.setAppointmentStatus(appointment, Constants.APPOINTMENT_FINISHED);
                job.setEndTime(OffsetDateTime.now(ZoneOffset.UTC));
            }
            job.setModifiedBy("ADMIN");
            job.setModifiedDate(OffsetDateTime.now(ZoneOffset.UTC));
            this.jobRepository.save(job);
        }
    }

    /**
     * @param action
     * @return
     */
    private String getJobStatus(String action) {
        String status = null;
        switch (action) {
            case "START":
            case "RESUME":
            case "RESTART":
                status = Constants.RUNNING;
                break;
            case "END":
                status = Constants.ENDED;
                break;
            case "PAUSE":
                status = Constants.PAUSED;
                break;
            case "DELETE":
                status = Constants.DELETED;
                break;
        }
        return status;
    }


    /**
     * Update the Job status by job id
     *
     * @param jobId
     * @param returnToPlanner
     */
    public void endJob(Integer jobId, boolean returnToPlanner) throws AppointmentNotExists {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "job not found"));

        Appointment appointment = this.appointmentService.getAppointmentByJobId(jobId);
        if (appointment == null) {
            throw new AppointmentNotExists("Job cannot be ended as no appointment is created yet.");
        }

        if (this.jobStatusValidator.validate(job.getStatus(), Constants.JOB_ACTION_END)) {
            // check for incomplete shipment
            List<Shipment> incompleteShipments = job.getJobParameters().getShipments().stream()
                    .filter(shipment -> !"Y".equalsIgnoreCase(shipment.getStatus())).collect(Collectors.toList());
            if (incompleteShipments.size() > 0 && returnToPlanner) {
                job.setStatus(Constants.INCOMPLETE);
                log.debug("in completed shipment exists in the job.");
            } else {
                job.setStatus(Constants.ENDED);
            }
            this.appointmentService.setAppointmentStatus(appointment, Constants.APPOINTMENT_FINISHED);
            job.setEndTime(OffsetDateTime.now(ZoneOffset.UTC));
            this.jobRepository.save(job);
        }
    }

    /**
     * save a request
     *
     * @param request
     * @return
     */
    public JobDTO save(JobDTO request) {
        if (request.getJobParameters() != null && !request.getJobParameters().getGrouped()) {
            for (Iterator<UldDTO> iterator = request.getJobParameters().getUlds().iterator(); iterator.hasNext(); ) {
                UldDTO uld = iterator.next();
                boolean qrt = isQRTULDs(uld);
                log.debug("is qrt uld : {}", qrt);
                if (qrt) {
                    iterator.remove();
                }
            }
        }
        Job job = this.jobParametersService.processJobRequest(request.getJobParameters());
        if (job == null) {
            job = getJob(request);
        }
        final Job savedJob = this.jobRepository.save(job);
        JobDTO jobDTO = jobMapper.entityToDto(savedJob);
        return jobDTO;
    }

    private boolean isJobAlreadyExists(JobDTO request) {
        // check no previous jobs exist for the same ULD / Group of ULDs
        JobParametersDTO jobParameters = request.getJobParameters();
        FlightDTO flight = jobParameters.getFlight();
        Integer flightLoadPlanVersion = jobParameters.getLoadPlanVersion();
        log.debug("Grouped: {}", jobParameters.getGrouped());
        log.debug("Flight load plan version :{}", flightLoadPlanVersion);
        jobParameters.getUlds()
                .forEach(uldDTO -> log.debug("ULD Serial Number: {}{}{}", uldDTO.getCarrierCode(), uldDTO.getUldType(), uldDTO.getUldSerialNumber()));
        log.debug("Flight Number Number: {}{}{}{}", flight.getTransportInfo().getCarrier(), flight.getTransportInfo().getNumber(),
                flight.getTransportInfo().getExtensionNumber(), flight.getTransportInfo().getFlightDate());
//        List<JobParameters> jobParametersList = jobParametersService.getJob(jobParameters);
//        List<Job> list = jobParametersList.stream().map(JobParameters::getJob).collect(Collectors.toList());
//        if (list != null && !list.isEmpty()) {
//            handleExistingJob(request, list);
//        }
        return true;
    }

    /**
     * @param jobId
     * @param request
     */
    public void addShipment(Integer jobId, ShipmentDTO request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "job not found"));

        if (!Constants.ENDED.equalsIgnoreCase(job.getStatus())) {
            Shipment shipment = jobMapper.dtoToEntity(request);
            Shipment saveShipment = this.shipmentService.saveShipment(shipment, job.getJobParameters());
            job.getJobParameters().getShipments().add(saveShipment);
            //send notification
            return;
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "job is already ended. cannot add shipment.");
    }


    /**
     * @param jobId
     */
    public void removeShipment(Integer jobId, String shipmentId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "job not found"));

        if (!Constants.ENDED.equalsIgnoreCase(job.getStatus())) {
            List<Shipment> shipments = job.getJobParameters().getShipments().stream()
                    .filter(shipment -> ((shipment.getDocumentType() + shipment.getDocumentPrefix() + shipment.getDocumentNumber())
                            .equalsIgnoreCase(shipmentId))
                    ).collect(Collectors.toList());
            if (shipments.size() > 0) {
                this.shipmentService.deleteShipment(shipments.get(0));
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "shipment not found");
            }
            //send notification
            return;
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "job is already ended. cannot remove shipment.");
    }


    /**
     * add uld to an existing job
     *
     * @param jobId
     * @param request
     */
    public void addUld(Integer jobId, UldDTO request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "job not found"));
        if (!Constants.ENDED.equalsIgnoreCase(job.getStatus())) {

        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "job is already ended. cannot add uld.");
    }

    /////////////////////////////////////////////// Private Methods ////////////////////////////////////////////////////

    /**
     * Handle existing job
     *
     * @param requestedJob
     * @param jobs
     * @return
     */
    private JobDTO handleExistingJob(JobDTO requestedJob, List<Job> jobs) {
        Job existingLatestJob = null;
        JobParametersDTO jobParameters = requestedJob.getJobParameters();
        log.debug("Load Plan Version: {}", jobParameters.getLoadPlanVersion());
        List<Shipment> newShipments = new ArrayList<>();
        boolean present = jobs.stream()
                .anyMatch(in -> in.getJobParameters().getLoadPlanVersion() == jobParameters.getLoadPlanVersion());
        if (present) {
            //if load plan version is already exists check the shipments and ulds.
        } else {
            // get Job for the latest version of load plan
            // sort by load plan version
            existingLatestJob = jobs.stream()
                    .sorted(Comparator.comparing(j -> j.getJobParameters().getLoadPlanVersion())).findFirst()
                    .orElseThrow(() -> new RuntimeException(String.format("Job not found with the load plan version %s", jobParameters.getLoadPlanVersion())));

            //System links the Job to the new Flight load plan version
            JobParameters latestJobJobParameters = existingLatestJob.getJobParameters();
            latestJobJobParameters.setLoadPlanVersion(jobParameters.getLoadPlanVersion());

            // compare shipment details
            List<ShipmentDTO> requestShipments = jobParameters.getShipments();
            for (ShipmentDTO requestShipment : requestShipments) {
                shipmentService.processShipments(requestShipment, newShipments, existingLatestJob.getJobParameters(), existingLatestJob.getStatus());
            }
            shipmentService.findAndDeleteShipments(requestShipments, latestJobJobParameters.getShipments(), existingLatestJob.getStatus());
            // create a new job with the new shipments if the job is already in Finished status
            if (!newShipments.isEmpty()) {
                createNewJob(existingLatestJob, jobParameters, newShipments, latestJobJobParameters);
            }
        }
        return null;
    }


    /**
     * removes the QRT ULDs from the Job Parameters
     *
     * @param uld
     * @return
     */
    private boolean isQRTULDs(UldDTO uld) {
        if (uld != null) {
            return uld.getTransferHandlingCode().equalsIgnoreCase(Constants.QRT_ULD);
        }
        return false;
    }

    /**
     * @param dto
     * @return
     */
    private Job getJob(JobDTO dto) {
        final JobParameters parameters = getJobParameters(dto.getJobParameters());
        Job job = Job.builder()
                .code(dto.getCode())
                .jobParameters(parameters)
                .groupName(dto.getGroupName() == null ? Constants.DEFAULT_GROUP_NAME : dto.getGroupName())
                .groupCode(dto.getGroupCode() == null ? Constants.DEFAULT_GROUP : dto.getGroupCode())
                .status(Constants.CREATED)  // created
                .updatable(dto.getUpdatable())
                .notes(dto.getNotes())
                .remarks(dto.getRemarks())
                .createdBy("ADMIN")
                .createdDate(OffsetDateTime.now(ZoneOffset.UTC))
                .priority(dto.getPriority() == null ? Constants.DEFAULT_PRIORITY : dto.getPriority())
                .type(dto.getType())
                .build();

        parameters.setJob(job);
        return job;
    }

    /**
     * create new job
     *
     * @param existingLatestJob
     * @param jobParameters
     * @param newShipments
     * @param latestJobJobParameters
     */
    private void createNewJob(Job existingLatestJob, JobParametersDTO jobParameters, List<Shipment> newShipments,
                              JobParameters latestJobJobParameters) {
        Job newJob = getJob(existingLatestJob, jobParameters, newShipments, latestJobJobParameters);
        this.jobRepository.save(newJob);
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
    private Job getJob(Job existingLatestJob, JobParametersDTO jobParameters, List<Shipment> newShipments,
                       JobParameters latestJobJobParameters) {
        List<ULD> existingJobULDs = latestJobJobParameters.getUlds();
        Flight flight = latestJobJobParameters.getFlight();
        List<ULD> newULDs = new ArrayList<>();
        for (ULD existingJobULD : existingJobULDs) {
            ULD newULD = getUld(existingJobULD);
            newULDs.add(newULD);
        }
        Flight newFlight = getFlight(flight);

        JobParameters newJobParameter = JobParameters.builder()
                .shipments(newShipments)
                .ulds(newULDs)
                .loadPlanVersion(jobParameters.getLoadPlanVersion())// new load plan version
                .grouped(false)
                .flight(newFlight)
                .uldCount(1)
                .shipmentCount(newShipments.size())
                .createdBy("ADMIN")
                .createdDate(ZonedDateTime.now())
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
                .createdBy("ADMIN")
                .createdDate(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
        newJobParameter.setJob(newJob);
        return newJob;
    }

    private Flight getFlight(Flight flight) {
        return Flight.builder()
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
                .createdBy("ADMIN")
                .createdDate(ZonedDateTime.now())
                .build();
    }

    private ULD getUld(ULD existingJobULD) {
        return ULD.builder()
                .uldSerialNumber(existingJobULD.getUldSerialNumber())
                .uldType(existingJobULD.getUldType())
                .name(existingJobULD.getName())
                .carrierCode(existingJobULD.getCarrierCode())
                .transferHandlingCode(existingJobULD.getTransferHandlingCode())
                .tareWeight(existingJobULD.getTareWeight())
                .volumeUnit(existingJobULD.getVolumeUnit())
                .weightUnit(existingJobULD.getWeightUnit())
                .rateType(existingJobULD.getRateType())
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
    }

    /**
     * Builds JobParameters
     *
     * @param jobParametersDTO
     * @return
     */
    private JobParameters getJobParameters(JobParametersDTO jobParametersDTO) {

        Flight.FlightBuilder flightBuilder = Flight.builder();
        Flight flight = null;
        JobParameters.JobParametersBuilder builder = JobParameters.builder();
        JobParameters entity = builder
                .grouped(jobParametersDTO.getGrouped())
                .uldGroupName(jobParametersDTO.getUldGroupName() == null ? Constants.DEFAULT_ULD_GROUP_NAME : jobParametersDTO.getUldGroupName())
                .uldGroupCode(jobParametersDTO.getUldGroupCode() == null ? Constants.DEFAULT_ULD_GROUP : jobParametersDTO.getUldGroupCode())
                .shipmentCount(jobParametersDTO.getShipments().size())
                .loadPlanVersion(jobParametersDTO.getLoadPlanVersion())
                .uldCount(jobParametersDTO.getUlds().size())
                .build();
        if (jobParametersDTO.getFlight() != null) {
            flightBuilder.aircraftCategory(jobParametersDTO.getFlight().getAircraftCategory())
                    .aircraftRegistration(jobParametersDTO.getFlight().getAircraftRegistration())
                    .iataAircraftType(jobParametersDTO.getFlight().getIataAircraftType())
                    .boardPoint(jobParametersDTO.getFlight().getBoardPoint())
                    .offPoint(jobParametersDTO.getFlight().getOffPoint());
            if (jobParametersDTO.getFlight().getTransportInfo() != null) {
                flightBuilder.extensionNumber(jobParametersDTO.getFlight().getTransportInfo().getExtensionNumber())
                        .flightNumber(jobParametersDTO.getFlight().getTransportInfo().getNumber())
                        .carrier(jobParametersDTO.getFlight().getTransportInfo().getCarrier())
                        .flightDate(jobParametersDTO.getFlight().getTransportInfo().getFlightDate());
            }
            if (jobParametersDTO.getFlight().getArrivalDateTimeLocal() != null) {
                flightBuilder.ata(jobParametersDTO.getFlight().getArrivalDateTimeLocal().getActual())
                        .eta(jobParametersDTO.getFlight().getArrivalDateTimeLocal().getEstimated())
                        .sta(jobParametersDTO.getFlight().getArrivalDateTimeLocal().getSchedule());
            }
            if (jobParametersDTO.getFlight().getDepartureDateTimeLocal() != null) {
                flightBuilder.std(jobParametersDTO.getFlight().getDepartureDateTimeLocal().getSchedule())
                        .atd(jobParametersDTO.getFlight().getDepartureDateTimeLocal().getActual())
                        .etd(jobParametersDTO.getFlight().getDepartureDateTimeLocal().getEstimated());
            }

            flight = flightBuilder
                    .createdBy("ADMIN")
                    .createdDate(ZonedDateTime.now())
                    .build();
            flight.setJobParameters(entity);
        }

        final List<Shipment> shipmentList = jobParametersDTO.getShipments().stream()
                .map(s -> getShipment(s, entity))
                .collect(Collectors.toList());

        entity.setUlds(getULDs(jobParametersDTO.getUlds(), entity));
        entity.setShipments(shipmentList);
        entity.setFlight(flight);
        entity.setCreatedBy("ADMIN");
        entity.setCreatedDate(ZonedDateTime.now());
        return entity;
    }

    private List<ULD> getULDs(List<UldDTO> ulds, JobParameters parameters) {
        List<ULD> entities = new ArrayList<>();
        for (UldDTO uld : ulds) {
            ULD entity = getULD(uld, parameters);
            entities.add(entity);
        }
        return entities;
    }

    /**
     * Builds ULD
     *
     * @param dto
     * @param parameters
     * @return
     */
    private ULD getULD(UldDTO dto, JobParameters parameters) {
        ULD entity = ULD.builder()
                .actualVolume(dto.getActualVolume().getValue())
                .actualWeight(dto.getActualWeight().getValue())
                .carrierCode(dto.getCarrierCode())
                .uldSerialNumber(dto.getUldSerialNumber())
                .name(dto.getName())
                .contourCode(dto.getContourCode())
                .loadingCode(dto.getLoadingCode())
                .shc(dto.getShc())
                .maximumVolume(dto.getMaximumVolume().getValue())
                .maximumWeight(dto.getMaximumWeight().getValue())
                .priorityCode(dto.getPriorityCode())
                .rateType(dto.getRateType())
                .totalPieces(dto.getTotalPieces())
                .weightUnit(dto.getActualWeight().getUnit().getCode())
                .volumeUnit(dto.getActualVolume().getUnit().getCode())
                .tareWeight(dto.getTareWeight().getValue())
                .totalShipments(dto.getTotalShipments())
                .transferHandlingCode(dto.getTransferHandlingCode())
                .uldType(dto.getUldType())
                .status(dto.getStatus())
                .build();
        entity.setJobParameters(parameters);
        return entity;
    }

    /**
     * Builds Shipment
     *
     * @param dto
     * @param parameters
     * @return
     */
    private Shipment getShipment(ShipmentDTO dto, JobParameters parameters) {
        Shipment entity = Shipment.builder().bookingStatus(dto.getBookingStatus())
                .commodity(dto.getCommodity())
                .description(dto.getDescription())
                .destination(dto.getDestination().getCode())
                .documentNumber(dto.getDocumentNumber())
                .documentPrefix(dto.getDocumentPrefix())
                .documentType(dto.getDocumentType())
                .eAWBIndicator(dto.isEAWBIndicator())
                .origin(dto.getOrigin().getCode())
                .piece(dto.getQuantity().getPiece())
                .weight(dto.getQuantity().getWeight().getValue())
                .weightUnit(dto.getQuantity().getWeight().getUnit().getCode())
                .volume(dto.getQuantity().getVolume().getValue())
                .volumeUnit(dto.getQuantity().getVolume().getUnit().getCode())
                .productCode(dto.getProductCode())
                .shc(dto.getShc())
                .remarks(dto.getRemarks())
                .routing(dto.getRouting())
                .status(dto.getStatus())
                .createdBy("ADMIN")
                .createdDate(ZonedDateTime.now())
                .build();
        entity.setJobParameters(parameters);
        return entity;
    }

    /**
     * @param params
     * @return
     */
    private Specification<Job> getJobSpecification(Map<String, String> params) {
        List<Predicate> predicates = new ArrayList<>();
        List<Specification> specs = new ArrayList<>();

        final List<SearchCriteria> searchCriteria = params.entrySet().stream()
                .filter(entry -> !(Arrays.asList("page", "size", "sortBy").contains(entry.getKey())))
                .map(entry -> new SearchCriteria(entry.getKey(), ":", entry.getValue()))
                .map(sc -> {
                    if (Arrays.asList("updatable").contains(sc.getKey())) {
                        sc.setValue(Boolean.parseBoolean((String) sc.getValue()));
                    }
                    return sc;
                })
                .collect(Collectors.toList());

        Specification<Job> jobSpecification = (root, query, builder) -> {
            query.distinct(true);
            Join<Job, JobParameters> jobParameters = root.join("jobParameters");
            Join<JobParameters, Flight> flight = jobParameters.join("flight");
            Join<JobParameters, Shipment> shipments = jobParameters.join("shipments");

            long jobCriteriaCount = searchCriteria.stream()
                    .filter(sc -> JOB_SEARCH_PROPERTIES.contains(sc.getKey()))
                    .count();
            if (jobCriteriaCount > 0) {
                predicates.addAll(searchCriteria.stream()
                        .filter(sc -> JOB_SEARCH_PROPERTIES.contains(sc.getKey()))
                        .map(sc -> builder.equal(root.get(sc.getKey()), sc.getValue()))
                        .collect(Collectors.toList()));
            }
            if (searchCriteria.stream().filter(sc -> LOAD_PLAN_SEARCH_PROPERTIES.contains(sc.getKey())).count() > 0) {
                predicates.addAll(searchCriteria.stream()
                        .filter(sc -> LOAD_PLAN_SEARCH_PROPERTIES.contains(sc.getKey()))
                        .map(sc -> builder.equal(jobParameters.get(sc.getKey()), sc.getValue())).collect(Collectors.toList()));
            }
            long uldCriteriaCount = searchCriteria.stream()
                    .filter(sc -> ULD_SEARCH_PROPERTIES.contains(sc.getKey()))
                    .count();
            if (uldCriteriaCount > 0) {
                Join<JobParameters, ULD> uld = jobParameters.join("ulds");
                predicates.addAll(searchCriteria.stream()
                        .filter(sc -> ULD_SEARCH_PROPERTIES.contains(sc.getKey()))
                        .map(sc -> {
                            if ("uldCarrierCode".equalsIgnoreCase(sc.getKey())) {
                                return builder.equal(uld.get("carrierCode"), sc.getValue());
                            } else if ("uldPriorityCode".equalsIgnoreCase(sc.getKey())) {
                                return builder.equal(uld.get("priorityCode"), sc.getValue());
                            }
                            return builder.equal(uld.get(sc.getKey()), sc.getValue());
                        }).collect(Collectors.toList()));
            }
            long flightCriteriaCount = searchCriteria.stream()
                    .filter(sc -> FLIGHT_SEARCH_PROPERTIES.contains(sc.getKey()))
                    .count();
            if (flightCriteriaCount > 0) {
                predicates.addAll(searchCriteria.stream()
                        .filter(sc -> FLIGHT_SEARCH_PROPERTIES.contains(sc.getKey()))
                        .map(sc -> {
                            if (!"flightDate".equals(sc.getKey()))
                                return builder.equal(flight.<String>get(sc.getKey()), sc.getValue());
                            else {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                return builder.equal(flight.<LocalDate>get(sc.getKey()), LocalDate.parse((String) sc.getValue(), formatter));
                            }
                        }).collect(Collectors.toList()));
            }

            long shipmentCriteriaCount = searchCriteria.stream()
                    .filter(sc -> SHIPMENT_SEARCH_PROPERTIES.contains(sc.getKey()))
                    .count();
            if (shipmentCriteriaCount > 0) {
                predicates.addAll(searchCriteria.stream()
                        .filter(sc -> SHIPMENT_SEARCH_PROPERTIES.contains(sc.getKey()))
                        .map(sc -> {
                            if (Arrays.asList("piece", "weight", "volume").contains(sc.getKey())) {
                                return builder.greaterThanOrEqualTo(shipments.get(sc.getKey()), sc.getValue().toString());
                            } else if (Arrays.asList("shc").contains(sc.getKey())) {
                                /*return builder.like(builder.function("shc", String.class, shipments.get("shc"),
                                        builder.<String>literal("-")), "%" + sc.getValue().toString().toLowerCase() + "%");*/
                                return builder.like(shipments.get("shc").as(String.class), "%" + sc.getValue() + "%");
                            } else {
                                return builder.equal(shipments.<String>get(sc.getKey()), sc.getValue());
                            }
                        }).collect(Collectors.toList()));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
        specs.add(jobSpecification);

        Specification<Job> specification = specs.get(0);
        return specification;
    }

}
