package com.accelya.product.workstationmanagement.job.resource;

import com.accelya.product.workstationmanagement.Constants;
import com.accelya.product.workstationmanagement.appointment.service.AppointmentService;
import com.accelya.product.workstationmanagement.appointment.transferobjects.AppointmentDTO;
import com.accelya.product.workstationmanagement.appointment.transferobjects.AppointmentNotExists;
import com.accelya.product.workstationmanagement.appointment.transferobjects.PagedAppointmentResponse;
import com.accelya.product.workstationmanagement.job.service.JobService;
import com.accelya.product.workstationmanagement.job.transferobjects.*;
import com.accelya.product.workstationmanagement.workstation.transferobjects.GenericResponse;
import com.accelya.product.workstationmanagement.workstation.transferobjects.PagedData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cargo/job-mgmt/v1/jobs")
@RequiredArgsConstructor
@Slf4j
@Validated
public class JobController {

    final private JobService jobService;
    final private AppointmentService appointmentService;

    @Operation(summary = "Get a job by its id", tags = "jobs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the Job",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = JobResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Job not found",
                    content = @Content)
    })
    @GetMapping("/{jobId}")
    public GenericResponse<JobDTO> findById(@Parameter(description = "id of the job to be searched", example = "1")
                                            @NotNull(message = "job id cannot be null")
                                            @PathVariable("jobId") final Integer jobId) {

        JobDTO jobDTO = jobService.get(jobId);
        return GenericResponse.<JobDTO>builder()
                .status("success")
                .data(jobDTO)
                .build();
    }


    @Operation(summary = "find all jobs by criteria", tags = "jobs", parameters = {
            @Parameter(name = "flightNumber", description = "flight number for which jobs are available", example = "0566", in = ParameterIn.QUERY),
            @Parameter(name = "flightDate", description = "flight date for which jobs are available. format yyyy-MM-dd", example = "2022-05-13", in = ParameterIn.QUERY),
            @Parameter(name = "boardPoint", description = "board point of a flight which jobs are available.", example = "JFK", in = ParameterIn.QUERY),
            @Parameter(name = "offPoint", description = "off point of a flight which jobs are available.", example = "BKK", in = ParameterIn.QUERY),
            @Parameter(name = "carrier", description = "airline code of the flight for which jobs are available.", example = "XX", in = ParameterIn.QUERY),
            @Parameter(name = "extensionNumber", description = "flight extension number for which jobs are available. format yyyy-MM-dd", example = "B", in = ParameterIn.QUERY),
            @Parameter(name = "origin", description = "origin of the shipment for which jobs are available", example = "DXB", in = ParameterIn.QUERY),
            @Parameter(name = "destination", description = "destination of the shipment for which jobs are available", example = "BKK", in = ParameterIn.QUERY),
            @Parameter(name = "documentType", description = "type of the shipment for which jobs are available", example = "AWB", in = ParameterIn.QUERY),
            @Parameter(name = "documentPrefix", description = "prefix of the shipment for which jobs are available", example = "888", in = ParameterIn.QUERY),
            @Parameter(name = "documentNumber", description = "number of the shipment for which jobs are available", example = "10111011", in = ParameterIn.QUERY),
            @Parameter(name = "uldCarrierCode", description = "carrier code of ULD", example = "UA", in = ParameterIn.QUERY),
            @Parameter(name = "uldSerialNumber", description = "uld serial number", example = "12110", in = ParameterIn.QUERY),
            @Parameter(name = "uldType", description = "type of uld", example = "AKE", in = ParameterIn.QUERY),
            @Parameter(name = "contourCode", description = "contour code", in = ParameterIn.QUERY),
            @Parameter(name = "transferHandlingCode", description = "transfer handling code of uld", example = "QRT", in = ParameterIn.QUERY),
            @Parameter(name = "uldPriorityCode", description = "priority code of uld", example = "MGO", in = ParameterIn.QUERY),
            @Parameter(name = "code", description = "Job code", example = "JOB1", in = ParameterIn.QUERY),
            @Parameter(name = "type", description = "Job type", example = "BREAKDOWN", in = ParameterIn.QUERY),
            @Parameter(name = "groupCode", description = "job group code", example = "DEFAULT_JOB_GROUP", in = ParameterIn.QUERY),
            @Parameter(name = "groupName", description = "job group name", example = "Default Job Group", in = ParameterIn.QUERY),
            @Parameter(name = "updatable", description = "updatable job or not", example = "true", in = ParameterIn.QUERY),
            @Parameter(name = "status", description = "job status", example = "A", in = ParameterIn.QUERY),
            @Parameter(name = "loadPlanVersion", description = "load plan version", example = "1", in = ParameterIn.QUERY),
            @Parameter(name = "grouped", description = "uld is group or not", example = "true", in = ParameterIn.QUERY),
            @Parameter(name = "uldGroupCode", description = "uld group code", example = "A", in = ParameterIn.QUERY),
            @Parameter(name = "priority", description = "job priority", example = "0", in = ParameterIn.QUERY)})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the Jobs",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PagedJobResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Did not find any jobs",
                    content = @Content)
    })
    @GetMapping
    public GenericResponse<PagedData<JobDTO>> search(@Parameter(hidden = true) @RequestParam(required = false) Map<String, String> queryParams,
                                                     @Parameter(description = "Page number, default is 0") @RequestParam(defaultValue = "0") final int page,
                                                     @Parameter(description = "Page size, default is 20") @RequestParam(defaultValue = "20") final int size,
                                                     @Parameter(description = "Comma separated sort parameters in <fieldName|<DESC/ASC>> format") @RequestParam(defaultValue = "id|DESC") final String sortBy) {
        List<Sort.Order> orders = new ArrayList<>();

        //TODO validate the format
        if (StringUtils.hasText(sortBy)) {
            for (String s : sortBy.split(",")) {
                final String[] split = s.split("\\|");
                Sort.Order order = new Sort.Order(Sort.Direction.fromString(split[1]), split[0]);
                orders.add(order);
            }
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));

        final PagedData<JobDTO> pagedData = jobService.search(queryParams, pageable);
        log.debug("paged data {}", pagedData.getPageInfo());
        return GenericResponse.<PagedData<JobDTO>>builder()
                .status("success")
                .data(pagedData)
                .build();
    }

    @Operation(summary = "Add a new job", tags = {"jobs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Job is created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = JobResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Job already exists",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<GenericResponse<JobDTO>> save(
            @Parameter(description = "Job to add. Cannot null or empty.",
                    required = true, schema = @Schema(implementation = JobDTO.class))
            @Valid @RequestBody JobDTO job) {
        final JobDTO result = jobService.save(job);

        GenericResponse<JobDTO> response = GenericResponse.<JobDTO>builder()
                .status("success")
                .data(result)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Start a job", tags = {"jobs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job is started",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StringResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid job id", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid job id", content = @Content),
            @ApiResponse(responseCode = "404", description = "Job not found", content = @Content)
    })
    @PutMapping("/{jobId}/actions/start")
    public GenericResponse<String> startJob(
            @Parameter(description = "Id of the job to be started. Cannot be empty.", example = "1", required = true)
            @NotNull(message = "job id cannot be null")
            @PathVariable("jobId") Integer jobId) throws AppointmentNotExists {

        this.jobService.updateJobStatus(jobId, Constants.JOB_ACTION_START); //running
        return GenericResponse.<String>builder()
                .status("success")
                .data("job started successfully")
                .build();
    }

    @Operation(summary = "End a job", tags = {"jobs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job is ended",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StringResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid job id", content = @Content),
            @ApiResponse(responseCode = "404", description = "Job not found", content = @Content)
    })
    @PutMapping("/{jobId}/actions/end")
    public GenericResponse<String> endJob(
            @Parameter(description = "Id of the job to be started. Cannot be blank.", example = "1", required = true)
            @NotNull(message = "job id cannot be null")
            @PathVariable("jobId") Integer jobId,
            @Parameter(description = "Indicator to return the shipment to planner or not.", example = "returnToPlanner=true")
            @RequestParam(value = "returnToPlanner", required = false) boolean returnToPlanner) throws AppointmentNotExists {
        this.jobService.endJob(jobId, returnToPlanner); //stopped
        return GenericResponse.<String>builder()
                .status("success")
                .data("job is ended successfully")
                .build();
    }

    @Operation(summary = "Restart a job", tags = {"jobs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job is restarted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StringResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid job id", content = @Content),
            @ApiResponse(responseCode = "404", description = "Job not found", content = @Content)
    })
    @PutMapping("/{jobId}/actions/restart")
    public GenericResponse<String> restartJob(
            @Parameter(description = "Id of the job to be restarted. Cannot be blank.", example = "1", required = true)
            @NotNull(message = "job id cannot be null")
            @PathVariable("jobId") Integer jobId) throws AppointmentNotExists {
        this.jobService.updateJobStatus(jobId, Constants.JOB_ACTION_RESTART); //stopped
        return GenericResponse.<String>builder()
                .status("success")
                .data("job is restarted successfully")
                .build();
    }

/*
    @PutMapping("/{jobId}/actions/incomplete")
    public GenericResponse<String> incomplete(@PathVariable("jobId") Integer jobId) {
        this.jobService.updateJobStatus(jobId, Constants.INCOMPLETE); //stopped
        return GenericResponse.<String>builder()
                .status("success")
                .data("job is marked as incomplete")
                .build();
    }
*/

    @Operation(summary = "Pause a job", tags = {"jobs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job is paused",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StringResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid job id", content = @Content),
            @ApiResponse(responseCode = "404", description = "Job not found", content = @Content)
    })
    @PutMapping("/{jobId}/actions/pause")
    public GenericResponse<String> pauseJob(
            @Parameter(description = "Id of the job to be paused. Cannot be blank.", example = "1", required = true)
            @NotNull(message = "job id cannot be null")
            @PathVariable("jobId") Integer jobId) throws AppointmentNotExists {
        this.jobService.updateJobStatus(jobId, Constants.PAUSED); // paused
        return GenericResponse.<String>builder()
                .status("success")
                .data("job is paused successfully")
                .build();
    }

    @Operation(summary = "Resume a job", tags = {"jobs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job is resumed",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StringResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid job id", content = @Content),
            @ApiResponse(responseCode = "404", description = "Job not found", content = @Content)
    })
    @PutMapping("/{jobId}/actions/resume")
    public GenericResponse<String> resumeJob(
            @Parameter(description = "Id of the job to be resumed. Cannot be blank.", example = "1", required = true)
            @NotNull(message = "job id cannot be null")
            @PathVariable("jobId") Integer jobId) throws AppointmentNotExists {
        this.jobService.updateJobStatus(jobId, Constants.RUNNING);// running
        return GenericResponse.<String>builder()
                .status("success")
                .data("job is resumed successfully")
                .build();
    }

    @Operation(summary = "Delete a job", tags = {"jobs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job is deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StringResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid job id", content = @Content),
            @ApiResponse(responseCode = "404", description = "Job not found", content = @Content)
    })
    @DeleteMapping("/{jobId}")
    public GenericResponse<String> deleteJob(
            @Parameter(description = "Id of the job to be deleted. Cannot be blank.", example = "1", required = true)
            @NotNull(message = "job id cannot be null")
            @PathVariable("jobId") Integer jobId) {
        this.jobService.deleteJob(jobId);
        return GenericResponse.<String>builder()
                .status("success")
                .data("job is deleted successfully")
                .build();
    }

    @Operation(summary = "Update shipment status of a job", tags = {"jobs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "shipment status is updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StringResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid job id", content = @Content),
            @ApiResponse(responseCode = "404", description = "Job not found", content = @Content)
    })
    @PutMapping("/{jobId}/shipments/{shipmentId}/actions/{action}")
    public GenericResponse<String> updateShipmentStatus(
            @Parameter(description = "Id of the job to be updated. Cannot be blank.", required = true)
            @NotNull(message = "job id cannot be null")
            @PathVariable("jobId") Integer jobId,
            @Parameter(description = "Id of the shipment to be updated. Cannot be blank.", required = true)
            @NotBlank(message = "shipment id cannot be blank")
            @PathVariable("shipmentId") String shipmentId,
            @Parameter(description = "action to be performed on the shipment", required = true, example = "completed")
            @NotBlank(message = "action cannot be blank")
            @PathVariable("action") String action) {
        this.jobService.updateShipmentStatus(jobId, shipmentId, action);
        return GenericResponse.<String>builder()
                .status("success")
                .data(String.format("shipment %s status is marked as %s successfully", shipmentId, action))
                .build();
    }

    @Operation(summary = "Add a shipment to an existing job", tags = {"jobs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "shipment is added to job",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StringResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Job not found", content = @Content)
    })
    @PostMapping("/{jobId}/shipments")
    public GenericResponse<String> addShipment(
            @Parameter(description = "Id of the job to be updated. Cannot be blank.", example = "1", required = true)
            @NotNull(message = "job id cannot be null")
            @PathVariable("jobId") Integer jobId,
            @RequestParam(value = "override", defaultValue = "false") boolean override,
            @Parameter(description = "Shipment to be added to job. Cannot null or empty.",
                    required = true, schema = @Schema(implementation = ShipmentDTO.class))
            @Valid @RequestBody ShipmentDTO request) {
        this.jobService.addShipment(jobId, request);
        return GenericResponse.<String>builder()
                .status("success")
                .data("shipment added to job successfully")
                .build();
    }

    @Operation(summary = "Remove a shipment from an existing job", tags = {"jobs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "shipment is added to job",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StringResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid shipment id or job id", content = @Content),
            @ApiResponse(responseCode = "404", description = "Job not found", content = @Content)
    })
    @DeleteMapping("/{jobId}/shipments/{shipmentId}")
    public GenericResponse<String> removeShipment(
            @Parameter(description = "Id of the job from shipment must be removed. Cannot be blank.", example = "1", required = true)
            @NotNull(message = "job id cannot be null")
            @PathVariable("jobId") Integer jobId,
            @RequestParam(value = "override", defaultValue = "false") boolean override,
            @Parameter(description = "Id of the shipment to be updated. Cannot be blank.", example = "AWB88812345678", required = true)
            @NotBlank(message = "shipment id cannot be blank")
            @PathVariable("shipmentId") String shipmentId) {
        this.jobService.removeShipment(jobId, shipmentId);
        return GenericResponse.<String>builder()
                .status("success")
                .data("shipment removed to job successfully")
                .build();
    }


    @Operation(summary = "Add a uld to an existing job", tags = {"jobs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "uld is added to job",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StringResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid job id", content = @Content),
            @ApiResponse(responseCode = "404", description = "Job not found", content = @Content)
    })
    @PutMapping("/{jobId}/ulds/")
    public GenericResponse<String> addULD(
            @Parameter(description = "Id of the job to which uld must be add. Cannot be blank.", required = true)
            @NotNull(message = "job id cannot be null")
            @PathVariable("jobId") Integer jobId,
            @Parameter(description = "ULD to be added to job. Cannot null or empty.",
                    required = true, schema = @Schema(implementation = UldDTO.class))
            @Valid @RequestBody UldDTO request) {
        this.jobService.addUld(jobId, request);
        return GenericResponse.<String>builder()
                .status("success")
                .data("ULD is added to job successfully")
                .build();
    }


    @Operation(summary = "Find appointments created for a job", tags = {"appointments"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the job appointments",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PagedAppointmentResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "job not exists",
                    content = @Content)
    })
    @GetMapping(path = "/{jobId}/appointments")
    public GenericResponse<PagedData<AppointmentDTO>> jobAppointments(
            @Parameter(description = "Id of the job for which appointments are fetched. Cannot be blank.", required = true)
            @NotNull(message = "Job id cannot be null")
            @PathVariable("jobId") Integer id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id|DESC") String sortBy) {
        List<Sort.Order> orders = new ArrayList<>();

        //TODO validate the format
        if (StringUtils.hasText(sortBy)) {
            for (String s : sortBy.split(",")) {
                final String[] split = s.split("\\|");
                Sort.Order order = new Sort.Order(Sort.Direction.fromString(split[1]), split[0]);
                orders.add(order);
            }
        }
        Pageable paging = PageRequest.of(page, size, Sort.by(orders));

        PagedData<AppointmentDTO> appointmentsByJobId = this.appointmentService.findAppointmentsByJobId(id, paging);
        return GenericResponse.<PagedData<AppointmentDTO>>builder()
                .status("success")
                .data(appointmentsByJobId)
                .build();
    }

}
