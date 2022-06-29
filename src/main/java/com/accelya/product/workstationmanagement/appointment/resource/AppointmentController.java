package com.accelya.product.workstationmanagement.appointment.resource;

import com.accelya.product.workstationmanagement.appointment.service.AppointmentService;
import com.accelya.product.workstationmanagement.appointment.transferobjects.AppointmentDTO;
import com.accelya.product.workstationmanagement.appointment.transferobjects.AppointmentRequestDTO;
import com.accelya.product.workstationmanagement.appointment.transferobjects.AppointmentResponse;
import com.accelya.product.workstationmanagement.job.transferobjects.JobDTO;
import com.accelya.product.workstationmanagement.job.transferobjects.JobResponse;
import com.accelya.product.workstationmanagement.job.transferobjects.PagedJobResponse;
import com.accelya.product.workstationmanagement.job.transferobjects.StringResponse;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cargo/job-mgmt/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    final AppointmentService appointmentService;

    @Operation(summary = "Get a appointment by its id", tags = "appointments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the Appointment",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid appointment id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Appointment not found",
                    content = @Content)
    })
    @GetMapping("/{appointmentId}")
    public GenericResponse<AppointmentDTO> findById(@Parameter(description = "id of the appointment to be searched", example = "1")
                                                    @NotNull(message = "appointment id cannot be null")
                                                    @PathVariable("appointmentId") Integer id) {
        AppointmentDTO appointment = this.appointmentService.getAppointment(id);
        return GenericResponse.<AppointmentDTO>builder()
                .status("success")
                .data(appointment)
                .build();
    }

    @Operation(summary = "Add a new appointment", tags = {"appointments"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Appointment is created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Appointment is already exists",
                    content = @Content)
    })
    @PostMapping
    public GenericResponse<AppointmentDTO> create(@Parameter(description = "Appointment to be created. Cannot null or empty.",
            required = true, schema = @Schema(implementation = AppointmentRequestDTO.class))
                                                  @Valid @RequestBody AppointmentRequestDTO request) {
        AppointmentDTO appointment = this.appointmentService.createAppointment(request);
        return GenericResponse.<AppointmentDTO>builder()
                .status("success")
                .data(appointment)
                .build();
    }

    @Operation(summary = "Delete a Appointment", tags = {"appointments"}, parameters = {
            @Parameter(in = ParameterIn.QUERY, name = "deleteJob", description = "to delete the jobs when appointment is deleted", example = "true")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment is deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StringResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid Appointment id", content = @Content),
            @ApiResponse(responseCode = "404", description = "Appointment not found", content = @Content)
    })
    @DeleteMapping("/{appointmentId}")
    public GenericResponse<String> remove(@Parameter(description = "Id of the appointment to be deleted. Cannot be blank.", example = "1", required = true)
                                          @NotNull(message = "appointment id cannot be null")
                                          @PathVariable("appointmentId") Integer id,
                                          @RequestParam(value = "deleteJob", required = false) boolean deleteJob) {
        this.appointmentService.deleteAppointmentAndJob(id, deleteJob);
        return GenericResponse.<String>builder()
                .status("success")
                .data(String.format("appointment %s is cancelled and uld job is deleted successfully", id))
                .build();
    }

    @Operation(summary = "find all appointments by criteria", tags = "appointments", parameters = {
            @Parameter(name = "flightNumber", description = "flight number for which appointments are available", example = "0566", in = ParameterIn.QUERY),
            @Parameter(name = "flightDate", description = "flight date for which appointments are available. format yyyy-MM-dd", example = "2022-05-13", in = ParameterIn.QUERY),
            @Parameter(name = "boardPoint", description = "board point of a flight which appointments are available.", example = "JFK", in = ParameterIn.QUERY),
            @Parameter(name = "offPoint", description = "off point of a flight which appointments are available.", example = "BKK", in = ParameterIn.QUERY),
            @Parameter(name = "carrier", description = "airline code of the flight for which appointments are available.", example = "XX", in = ParameterIn.QUERY),
            @Parameter(name = "extensionNumber", description = "flight extension number for which appointments are available. format yyyy-MM-dd", example = "B", in = ParameterIn.QUERY),
            @Parameter(name = "from", description = "fo datetime for which appointments should be searched, format: yyyy-MM-dd'T'HH:mmZ", example = "2022-05-13T10:00+0400", in = ParameterIn.QUERY),
            @Parameter(name = "to", description = "to datetime for which appointments should be searched,format: yyyy-MM-dd'T'HH:mmZ", example = "2022-05-13T10:00+0400", in = ParameterIn.QUERY),
            @Parameter(name = "tag", description = "appointment tag to be search", example = "2022-05-13", in = ParameterIn.QUERY),
            @Parameter(name = "status", description = "appointment status to be searched", example = "C", in = ParameterIn.QUERY),
            @Parameter(name = "origin", description = "origin of the shipment for which appointments are available", example = "DXB", in = ParameterIn.QUERY),
            @Parameter(name = "destination", description = "destination of the shipment for which appointments are available", example = "BKK", in = ParameterIn.QUERY),
            @Parameter(name = "documentType", description = "type of the shipment for which appointments are available", example = "AWB", in = ParameterIn.QUERY),
            @Parameter(name = "documentPrefix", description = "prefix of the shipment for which appointments are available", example = "888", in = ParameterIn.QUERY),
            @Parameter(name = "documentNumber", description = "number of the shipment for which appointments are available", example = "10111011", in = ParameterIn.QUERY),
            @Parameter(name = "uldCarrierCode", description = "carrier code of ULD", example = "UA", in = ParameterIn.QUERY),
            @Parameter(name = "uldSerialNumber", description = "uld serial number", example = "12110", in = ParameterIn.QUERY),
            @Parameter(name = "uldType", description = "type of uld", example = "AKE", in = ParameterIn.QUERY),
            @Parameter(name = "contourCode", description = "contour code", in = ParameterIn.QUERY),
            @Parameter(name = "transferHandlingCode", description = "transfer handling code of uld", example = "QRT", in = ParameterIn.QUERY),
            @Parameter(name = "uldPriorityCode", description = "priority code of uld", example = "MGO", in = ParameterIn.QUERY),
            @Parameter(name = "jobCode", description = "Job code", example = "JOB1", in = ParameterIn.QUERY),
            @Parameter(name = "jobType", description = "Job type", example = "BREAKDOWN", in = ParameterIn.QUERY),
            @Parameter(name = "jobGroupCode", description = "job group code", example = "DEFAULT_JOB_GROUP", in = ParameterIn.QUERY),
            @Parameter(name = "jobGroupName", description = "job group name", example = "Default Job Group", in = ParameterIn.QUERY),
            @Parameter(name = "updatableJob", description = "updatable job or not", example = "true", in = ParameterIn.QUERY),
            @Parameter(name = "jobStatus", description = "job status", example = "A", in = ParameterIn.QUERY),
            @Parameter(name = "jobPriority", description = "job priority", example = "0", in = ParameterIn.QUERY),
            @Parameter(name = "workstationCode", description = "workstation code", example = "WORKSTATION1", in = ParameterIn.QUERY)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the appointments",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PagedJobResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Did not find any appointments",
                    content = @Content)
    })
    @GetMapping
    public GenericResponse<PagedData<AppointmentDTO>> search(@Parameter(hidden = true) @RequestParam Map<String, String> params,
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
        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));

        PagedData<AppointmentDTO> pagedData = this.appointmentService.search(params, pageable);

        return GenericResponse.<PagedData<AppointmentDTO>>builder()
                .status("success")
                .data(pagedData)
                .build();
    }

    @Operation(summary = "Update an existing appointment", tags = {"appointments"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment is updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Appointment not exists",
                    content = @Content)
    })
    @PutMapping("/{appointmentId}")
    public GenericResponse<AppointmentDTO> update(@Parameter(description = "Id of the appointment to be updated. Cannot be blank.", example = "1", required = true)
                                                  @NotNull(message = "appointment id cannot be null")
                                                  @PathVariable("appointmentId") Integer appointmentId,
                                                  @Parameter(description = "Appointment to be updated. Cannot null or empty.",
                                                          required = true, schema = @Schema(implementation = AppointmentRequestDTO.class)) @Valid @RequestBody AppointmentRequestDTO request) {
        AppointmentDTO appointmentDTO = this.appointmentService.updateAppointment(appointmentId, request);
        return GenericResponse.<AppointmentDTO>builder()
                .status("success")
                .data(appointmentDTO)
                .build();
    }
}
