package com.accelya.product.workstationmanagement.appointment.service;

import com.accelya.product.workstationmanagement.Constants;
import com.accelya.product.workstationmanagement.SearchCriteria;
import com.accelya.product.workstationmanagement.appointment.mapper.AppointmentMapper;
import com.accelya.product.workstationmanagement.appointment.model.Appointment;
import com.accelya.product.workstationmanagement.appointment.repository.AppointmentRepository;
import com.accelya.product.workstationmanagement.appointment.transferobjects.AppointmentDTO;
import com.accelya.product.workstationmanagement.appointment.transferobjects.AppointmentRequestDTO;
import com.accelya.product.workstationmanagement.job.model.*;
import com.accelya.product.workstationmanagement.job.repository.JobRepository;
import com.accelya.product.workstationmanagement.workstation.model.Workstation;
import com.accelya.product.workstationmanagement.workstation.repository.WorkstationRepository;
import com.accelya.product.workstationmanagement.workstation.transferobjects.PageInfo;
import com.accelya.product.workstationmanagement.workstation.transferobjects.PagedData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    public static final List<String> SHIPMENT_SEARCH_PROPERTIES = Arrays.asList("origin", "destination", "documentPrefix", "documentNumber", "documentType", "piece", "weight", "volume", "shc");
    public static final List<String> FLIGHT_SEARCH_PROPERTIES = Arrays.asList("carrier", "flightNumber", "boardPoint", "offPoint", "flightDate", "extensionNumber");
    public static final List<String> ULD_SEARCH_PROPERTIES = Arrays.asList("transferHandlingCode", "uldCarrierCode", "uldPriorityCode", "uldSerialNumber", "uldType", "contourCode");
    public static final List<String> JOB_SEARCH_PROPERTIES = Arrays.asList("jobPriority", "jobCode", "jobType", "jobGroupCode", "jobGroupName", "updatableJob", "jobStatus");
    public static final List<String> LOAD_PLAN_SEARCH_PROPERTIES = Arrays.asList("loadPlanVersion", "grouped", "uldGroupCode");
    public static final List<String> WORKSTATION_SEARCH_PROPERTIES = Arrays.asList("workstationCode");
    public static final List<String> APPOINTMENT_SEARCH_PROPERTIES = Arrays.asList("from", "to", "date", "tag", "status");
    public static final List<String> BOOLEAN_SEARCH_PROPERTIES = Arrays.asList("updatable", "open", "serviceable", "multipleULDAllowed", "fixed", "active");
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_DATE;

    final private AppointmentRepository appointmentRepository;
    final private WorkstationRepository workstationRepository;
    final private JobRepository jobRepository;
    final private AppointmentMapper appointmentMapper;

    /**
     * Create Appointment
     *
     * @param request
     * @return
     */
    public AppointmentDTO createAppointment(AppointmentRequestDTO request) {

        Workstation workstation = workstationRepository.findById(request.getWorkstationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "workstation not found"));
        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "job not found"));

        log.debug("validating the appointment request");
        validateAppointmentRequest(request, workstation, job);

        Appointment appointment = getAppointment(request, workstation, job);
        Appointment savedAppointment = this.appointmentRepository.save(appointment);

        // change the job status
        job.setStatus(Constants.ASSIGNED);  // assigned
        jobRepository.save(job);
        return appointmentMapper.entityToDto(savedAppointment);
    }


    /**
     * Cancel the appointment
     *
     * @param appointmentId
     */
    public void deleteAppointmentAndJob(Integer appointmentId, boolean deleteJob) {
        Appointment appointment = this.appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "appointment not found"));

        Job job = appointment.getJob();
        if (Constants.RUNNING.equalsIgnoreCase(job.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Job is running status. Please stop the job in order to cancel the appointment.");
        }
        appointment.setStatus(Constants.APPOINTMENT_CANCELLED);
        appointment.setModifiedBy("ADMIN");
        appointment.setModifiedDate(OffsetDateTime.now());
        this.appointmentRepository.save(appointment);

        log.debug("change the job status back to Created.");
        // change the job status back to Created
        job.setStatus(Constants.CREATED);
        this.jobRepository.save(job);

        if (deleteJob) {
            log.debug("delete the job from job repository");
            // delete the job using job id
            this.jobRepository.deleteById(job.getId());
        }
    }

    /**
     * Update the appointment
     *
     * @param appointmentId
     * @param request
     * @return
     */
    public AppointmentDTO updateAppointment(Integer appointmentId, AppointmentRequestDTO request) {
        Appointment appointment = this.appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "appointment not found"));

        if (request.getWorkstationId() != null) {
            Workstation workstation = workstationRepository.findById(request.getWorkstationId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "workstation not found"));
            appointment.setWorkstation(workstation);
        }
        if (request.getJobId() != null) {
            Job job = jobRepository.findById(request.getJobId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "job not found"));

            if (!(Constants.ASSIGNED.equalsIgnoreCase(job.getStatus()) || (Constants.PAUSED.equalsIgnoreCase(job.getStatus())))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "job status should be either assigned or paused");
            }
            appointment.setJob(job);
        }
        appointment.setFromTime(request.getFromTime());
        appointment.setToTime(request.getToTime());
        appointment.getTags().addAll(request.getTags());
        appointment.setModifiedBy("ADMIN");
        appointment.setModifiedDate(OffsetDateTime.now());
        Appointment updatedAppointment = this.appointmentRepository.save(appointment);
        return appointmentMapper.entityToDto(updatedAppointment);
    }

    /**
     * Returns the appointment
     *
     * @param appointmentId
     * @return
     */
    public AppointmentDTO getAppointment(Integer appointmentId) {
        Appointment appointment = this.appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "appointment not found"));
        return appointmentMapper.entityToDto(appointment);
    }


    /**
     * Fetches the workstation appointments for a given date
     *
     * @param params
     * @param pageable
     * @return
     */
    public PagedData<AppointmentDTO> search(Map<String, String> params, Pageable pageable) {
        Page<Appointment> page = null;
        if (params != null && !params.isEmpty()) {
            Specification<Appointment> specification = getSpecification(params);
            page = this.appointmentRepository.findAll(specification, pageable);
        } else {
            page = this.appointmentRepository.findAll(pageable);
        }

        List<Appointment> content = page.getContent();
        final List<AppointmentDTO> appointments = this.appointmentMapper.entityListToDtoList(content);
        PageInfo pageInfo = PageInfo.builder()
                .listSize(page.getTotalElements())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .build();
        return PagedData.<AppointmentDTO>builder()
                .pageInfo(pageInfo)
                .list(appointments)
                .build();
    }


    /////////////////////////////////////////////// Private Methods ////////////////////////////////////////////////////
    private Specification<Appointment> getSpecification(Map<String, String> params) {
        // validate params
        if (!areValidParameters(params)) {

        }
        List<Predicate> predicates = new ArrayList<>();
        List<Specification> specs = new ArrayList<>();

        // handling page and boolean params
        final List<SearchCriteria> searchCriteria = getSearchCriteria(params);

        Specification<Appointment> specification = (root, query, builder) -> {
            query.distinct(true);

            List<Predicate> appointmentPredicates = getAppointmentPredicates(searchCriteria, root, builder);
            if (!appointmentPredicates.isEmpty()) {
                predicates.addAll(appointmentPredicates);
            }

            List<Predicate> flightPredicates = getFlightPredicates(searchCriteria, root, builder);
            if (flightPredicates != null && !flightPredicates.isEmpty()) {
                predicates.addAll(flightPredicates);
            }

            List<Predicate> workstationPredicates = getWorkstationPredicates(searchCriteria, root, builder);

            if (workstationPredicates != null && !workstationPredicates.isEmpty()) {
                predicates.addAll(workstationPredicates);
            }

            List<Predicate> jobPredicates = getJobPredicates(searchCriteria, root, builder);

            if (jobPredicates != null && !jobPredicates.isEmpty()) {
                predicates.addAll(jobPredicates);
            }

            List<Predicate> shipmentPredicates = getShipmentPredicates(searchCriteria, root, builder);

            if (shipmentPredicates != null && !shipmentPredicates.isEmpty()) {
                predicates.addAll(shipmentPredicates);
            }

            List<Predicate> uldPredicates = getUldPredicates(searchCriteria, root, builder);

            if (uldPredicates != null && !uldPredicates.isEmpty()) {
                predicates.addAll(uldPredicates);
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };

        specs.add(specification);
        return specs.get(0);
    }

    private List<Predicate> getUldPredicates(List<SearchCriteria> searchCriteria, Root<Appointment> root, CriteriaBuilder builder) {
        return searchCriteria.stream()
                .filter(sc -> ULD_SEARCH_PROPERTIES.contains(sc.getKey()))
                .map(sc -> {
                    Join<Appointment, Job> job = root.join("job");
                    Join<Job, JobParameters> jobParameters = job.join("jobParameters");
                    Join<JobParameters, ULD> uld = jobParameters.join("ulds");
                    if ("uldCarrierCode".equalsIgnoreCase(sc.getKey())) {
                        return builder.equal(uld.<String>get("carrierCode"), sc.getValue());
                    } else if ("uldPriorityCode".equalsIgnoreCase(sc.getKey())) {
                        return builder.equal(uld.<String>get("priorityCode"), sc.getValue());
                    }
                    return builder.equal(uld.<String>get(sc.getKey()), sc.getValue());
                })
                .collect(Collectors.toList());
    }

    private List<Predicate> getShipmentPredicates(List<SearchCriteria> searchCriteria, Root<Appointment> root, CriteriaBuilder builder) {
        return searchCriteria.stream()
                .filter(sc -> SHIPMENT_SEARCH_PROPERTIES.contains(sc.getKey()))
                .map(sc -> {
                    Join<Appointment, Job> job = root.join("job");
                    Join<Job, JobParameters> jobParameters = job.join("jobParameters");
                    Join<JobParameters, Shipment> shipments = jobParameters.join("shipments");
                    return builder.equal(shipments.<String>get(sc.getKey()), sc.getValue());
                })
                .collect(Collectors.toList());
    }

    private List<Predicate> getWorkstationPredicates(List<SearchCriteria> searchCriteria, Root<Appointment> root, CriteriaBuilder builder) {
        return searchCriteria.stream()
                .filter(sc -> WORKSTATION_SEARCH_PROPERTIES.contains(sc.getKey()))
                .map(sc -> {
                    Join<Appointment, Workstation> workstation = root.join("workstation");
                    if ("workstationCode".equalsIgnoreCase(sc.getKey())) {
                        return builder.equal(workstation.<String>get("code"), sc.getValue());
                    }
                    return builder.equal(workstation.<String>get(sc.getKey()), sc.getValue());
                })
                .collect(Collectors.toList());
    }

    private List<Predicate> getJobPredicates(List<SearchCriteria> searchCriteria, Root<Appointment> root, CriteriaBuilder builder) {
        return searchCriteria.stream()
                .filter(sc -> JOB_SEARCH_PROPERTIES.contains(sc.getKey()))
                .map(sc -> {
                    Join<Appointment, Job> workstation = root.join("job");
                    if ("jobCode".equalsIgnoreCase(sc.getKey())) {
                        return builder.equal(workstation.<String>get("code"), sc.getValue());
                    } else if ("jobType".equalsIgnoreCase(sc.getKey())) {
                        return builder.equal(workstation.<String>get("type"), sc.getValue());
                    } else if ("jobGroupCode".equalsIgnoreCase(sc.getKey())) {
                        return builder.equal(workstation.<String>get("groupCode"), sc.getValue());
                    } else if ("jobGroupName".equalsIgnoreCase(sc.getKey())) {
                        return builder.equal(workstation.<String>get("groupName"), sc.getValue());
                    } else if ("updatableJob".equalsIgnoreCase(sc.getKey())) {
                        return builder.equal(workstation.<String>get("updatable"), sc.getValue());
                    } else if ("jobStatus".equalsIgnoreCase(sc.getKey())) {
                        return builder.equal(workstation.<String>get("status"), sc.getValue());
                    } else if ("jobPriority".equalsIgnoreCase(sc.getKey())) {
                        return builder.equal(workstation.<String>get("priority"), sc.getValue());
                    }
                    return builder.equal(workstation.<String>get(sc.getKey()), sc.getValue());
                })
                .collect(Collectors.toList());
    }

    private List<Predicate> getFlightPredicates(List<SearchCriteria> searchCriteria, Root<Appointment> root, CriteriaBuilder builder) {
        return searchCriteria.stream()
                .filter(sc -> FLIGHT_SEARCH_PROPERTIES.contains(sc.getKey()))
                .map(sc -> {
                    Join<Appointment, Job> job = root.join("job");
                    Join<Job, JobParameters> jobParameters = job.join("jobParameters");
                    Join<JobParameters, Flight> flight = jobParameters.join("flight");


                    if (!"flightDate".equals(sc.getKey()))
                        return builder.equal(flight.<String>get(sc.getKey()), sc.getValue());
                    else {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        return builder.equal(flight.<LocalDate>get(sc.getKey()), LocalDate.parse((String) sc.getValue(), formatter));
                    }
                })
                .collect(Collectors.toList());
    }

    private List<SearchCriteria> getSearchCriteria(Map<String, String> params) {
        return params.entrySet().stream()
                .filter(entry -> !(Arrays.asList("page", "size", "sortBy").contains(entry.getKey())))
                .map(entry -> new SearchCriteria(entry.getKey(), ":", entry.getValue()))
                .map(sc -> {
                    if (BOOLEAN_SEARCH_PROPERTIES.contains(sc.getKey())) {
                        sc.setValue(Boolean.parseBoolean((String) sc.getValue()));
                    }
                    return sc;
                })
                .map(sc -> {
                    if ("from".equalsIgnoreCase(sc.getKey()) || "to".equalsIgnoreCase(sc.getKey())) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mmZ");
                        sc.setValue(OffsetDateTime.parse((String) sc.getValue(), formatter));
                    }/* else if ("date".equalsIgnoreCase(sc.getKey())) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        sc.setValue(OffsetDateTime.parse((String) sc.getValue(), formatter));
                    }*/
                    return sc;
                })
                .collect(Collectors.toList());
    }

    private List<Predicate> getAppointmentPredicates(List<SearchCriteria> searchCriteria, Root<Appointment> root, CriteriaBuilder builder) {
        return searchCriteria.stream()
                .filter(sc -> APPOINTMENT_SEARCH_PROPERTIES.contains(sc.getKey()))
                .map(sc -> {
                    if ("from".equalsIgnoreCase(sc.getKey())) {
                        return builder.greaterThanOrEqualTo(root.get("fromTime").as(OffsetDateTime.class), (OffsetDateTime) sc.getValue());
                    } else if ("to".equalsIgnoreCase(sc.getKey())) {
                        return builder.lessThanOrEqualTo(root.get("toTime").as(OffsetDateTime.class), (OffsetDateTime) sc.getValue());
                    }
                    return builder.equal(root.get(sc.getKey()), sc.getValue());
                })
                .collect(Collectors.toList());
    }

    private boolean areValidParameters(Map<String, String> params) {
        return true;
    }

    /**
     * Create Appointment object
     *
     * @param request
     * @param workstation
     * @param job
     * @return
     */
    private Appointment getAppointment(AppointmentRequestDTO request, Workstation workstation, Job job) {
        return Appointment.builder()
                .fromTime(request.getFromTime())
                .toTime(request.getToTime())
                .status(Constants.APPOINTMENT_CREATED)
                .tags(request.getTags())
                .workstation(workstation)
                .job(job)
                .createdBy("ADMIN")
                .createdDate(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
    }

    /*private boolean isAvailable(Workstation workstation, ZonedDateTime fromTime, ZonedDateTime toTime, Duration duration) {

        if (toTime.isBefore(fromTime)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Appointment end time %s must not be before appointment start time %s ", toTime, fromTime));
        }

        // get workstation appointments for that day
        List<Appointment> appointments = this.appointmentRepository
                .findByWorkstationCodeAndAppointmentDateAndJobCode(workstation.getCode(), fromTime, toTime)
                .stream()
                .filter(appointment -> !appointment.getStatus().equalsIgnoreCase(Constants.APPOINTMENT_FINISHED))
                .filter(appointment -> !appointment.getStatus().equalsIgnoreCase(Constants.APPOINTMENT_CANCELLED))
                .collect(Collectors.toList());
        if (appointments.size() > 0) {
            for (Appointment appointment : appointments) {
                log.debug("from time {}, to time", appointment.getFromTime(), appointment.getToTime());
                if (toTime.isAfter(appointment.getFromTime()) && appointment.getToTime().isAfter(fromTime)) {
                    log.debug("overlapping appointments found");
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            String.format("Appointment cannot be created between %s to %s due to appointment overlapping with another", toTime, fromTime));
                }
            }
        }
        return true;
    }*/

    private void validateAppointmentRequest(AppointmentRequestDTO request, Workstation workstation, Job job) {
        if (!workstation.getServiceable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "workstation is not serviceable");
        }
        if (!workstation.getActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "workstation is not active");
        }

        if (!job.getStatus().equalsIgnoreCase(Constants.CREATED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ULD Job is not in pending status on the workstation ");
        }
        if (job.getJobParameters().getLoadPlanVersion() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "job is not part of flight load planning");
        }
/*        if (!isAvailable(workstation, request.getFromTime(), request.getToTime(), request.getDuration())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "workstation is not available");
        }*/

        if (!request.isIgnoreULDTypeCheck()) {
            // shc validation
            List<String> workstationShc = workstation.getShc();
            List<String> jobShc = job.getJobParameters().getUlds().stream()
                    .flatMap(uld -> uld.getShc() != null ? uld.getShc().stream() : null)
                    .collect(Collectors.toList());
            log.debug("workstation shc {}, job shc{}", workstationShc, jobShc);
            for (String s : jobShc) {
                if (!workstationShc.contains(s)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ULD's shc in the job is not compatible with the " +
                            "workstation/user group's shc ");
                }
            }
        }

        // uld type validation
        List<String> compatibleTypes = workstation.getCompatibleTypes();
        List<String> uldTypes = job.getJobParameters().getUlds().stream()
                .map(uld -> uld.getUldType())
                .collect(Collectors.toList());
        for (String s : uldTypes) {
            if (!compatibleTypes.contains(s)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ULD in the job type is not compatible with the workstation/user group ");
            }
        }
    }

    /**
     * @param workstationId
     * @param pageable
     * @return
     */
    public PagedData<AppointmentDTO> findAppointmentsByWorkstationId(Integer workstationId, Pageable pageable) {

        Workstation workstation = this.workstationRepository.findById(workstationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "workstation not found"));
        log.debug("workstation {}", workstation);
        Page<Appointment> appointmentPage = this.appointmentRepository.findByWorkstationId(workstationId, pageable);
        List<Appointment> appointments = appointmentPage.getContent();
        final List<AppointmentDTO> appointmentDTOs = appointmentMapper.entityListToDtoList(appointments);
        PageInfo pageInfo = PageInfo.builder()
                .listSize(appointmentPage.getTotalElements())
                .pageNumber(appointmentPage.getNumber())
                .pageSize(appointmentPage.getSize())
                .totalPages(appointmentPage.getTotalPages())
                .build();
        return PagedData.<AppointmentDTO>builder()
                .pageInfo(pageInfo)
                .list(appointmentDTOs)
                .build();
    }

    /**
     * @param jobId
     * @param pageable
     * @return
     */
    public PagedData<AppointmentDTO> findAppointmentsByJobId(Integer jobId, Pageable pageable) {
        Job job = this.jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        log.debug("Job {}", job);
        Page<Appointment> appointmentPage = this.appointmentRepository.findByJobId(jobId, pageable);
        List<Appointment> appointments = appointmentPage.getContent();
        final List<AppointmentDTO> appointmentDTOs = appointmentMapper.entityListToDtoList(appointments);
        PageInfo pageInfo = PageInfo.builder()
                .listSize(appointmentPage.getTotalElements())
                .pageNumber(appointmentPage.getNumber())
                .pageSize(appointmentPage.getSize())
                .totalPages(appointmentPage.getTotalPages())
                .build();
        return PagedData.<AppointmentDTO>builder()
                .pageInfo(pageInfo)
                .list(appointmentDTOs)
                .build();
    }

    public void setAppointmentStatus(Appointment appointment, String status) {
        // find the appointment by jobId Assumption is job will have only one appointment at a time
        if (appointment != null) {
            appointment.setStatus(status);
            this.appointmentRepository.save(appointment);
        }
    }

    public Appointment getAppointmentByJobId(Integer jobId) {
        return this.appointmentRepository.findByJobId(jobId);
    }
}
