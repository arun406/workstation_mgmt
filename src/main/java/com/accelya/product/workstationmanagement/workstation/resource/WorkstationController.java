package com.accelya.product.workstationmanagement.workstation.resource;

import com.accelya.product.workstationmanagement.appointment.service.AppointmentService;
import com.accelya.product.workstationmanagement.appointment.transferobjects.AppointmentDTO;
import com.accelya.product.workstationmanagement.job.transferobjects.JobResponse;
import com.accelya.product.workstationmanagement.workstation.transferobjects.PagedWorkstationResponse;
import com.accelya.product.workstationmanagement.workstation.transferobjects.WorkstationResponse;
import com.accelya.product.workstationmanagement.workstation.mapper.WorkstationMapper;
import com.accelya.product.workstationmanagement.workstation.model.Workstation;
import com.accelya.product.workstationmanagement.workstation.service.WorkstationService;
import com.accelya.product.workstationmanagement.workstation.transferobjects.GenericResponse;
import com.accelya.product.workstationmanagement.workstation.transferobjects.PagedData;
import com.accelya.product.workstationmanagement.workstation.transferobjects.WorkstationDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cargo/reference-data/v1/warehouse/locations")
@RequiredArgsConstructor
public class WorkstationController {

    final private WorkstationService workstationService;
    final private WorkstationMapper workstationMapper;
    final private AppointmentService appointmentService;

    @Operation(summary = "Get a workstation by its id", tags = "workstations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the workstation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = WorkstationResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "workstation not found",
                    content = @Content)
    })
    @GetMapping(path = "/{id}")
    public GenericResponse<WorkstationDTO> get(@Parameter(description = "id of the workstation to be searched", example = "1")
                                               @NotNull(message = "workstation id cannot be null")
                                               @PathVariable("id") Integer id) {
        final WorkstationDTO workstationDTO = workstationService.get(id);
        return GenericResponse.<WorkstationDTO>builder()
                .status("success")
                .data(workstationDTO)
                .build();
    }

    @Operation(summary = "find all workstations by criteria", tags = "workstations", parameters = {
            @Parameter(name = "code", description = "workstation code", example = "WORKSTATION1", in = ParameterIn.QUERY),
            @Parameter(name = "airport", description = "workstation terminal/airport", example = "DXB", in = ParameterIn.QUERY),
            @Parameter(name = "warehouse", description = "warehouse code", example = "WH1", in = ParameterIn.QUERY),
            @Parameter(name = "section", description = "warehouse section code", example = "SEC1", in = ParameterIn.QUERY),
            @Parameter(name = "type", description = "workstation type", example = "USER_GROUP", in = ParameterIn.QUERY),
            @Parameter(name = "name", description = "workstation name", example = "Buildup Workstation Name", in = ParameterIn.QUERY),
            @Parameter(name = "shc", description = "workstation supported shc", example = "HEA", in = ParameterIn.QUERY),
            @Parameter(name = "productType", description = "workstation supported product type", example = "GCR", in = ParameterIn.QUERY),
            @Parameter(name = "open", description = "workstation open status", example = "true", in = ParameterIn.QUERY),
            @Parameter(name = "active", description = "workstation active status", example = "true", in = ParameterIn.QUERY),
            @Parameter(name = "serviceable", description = "workstation active status", example = "true", in = ParameterIn.QUERY),
            @Parameter(name = "multipleULDAllowed", description = "workstation multiple uld allowed status", example = "true", in = ParameterIn.QUERY),
            @Parameter(name = "fixed", description = "workstation fixed status", example = "true", in = ParameterIn.QUERY)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the workstations",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PagedWorkstationResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Did not find any workstations",
                    content = @Content)
    })
    @GetMapping
    public GenericResponse<PagedData<WorkstationDTO>> search(@RequestParam Map<String, String> params,
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

        final PagedData<WorkstationDTO> pagedData = workstationService.search(params, paging);
        return GenericResponse.<PagedData<WorkstationDTO>>builder()
                .status("success")
                .data(pagedData)
                .build();
    }

    @Operation(summary = "Add a new workstation", tags = {"workstations"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "workstation is created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = JobResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "workstation already exists",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<GenericResponse<WorkstationDTO>> create(@Parameter(description = "Workstation to add. Cannot null or empty.",
            required = true, schema = @Schema(implementation = WorkstationDTO.class))
                                                                  @Valid @RequestBody WorkstationDTO request) {
        final Workstation save = workstationService.save(request);
        WorkstationDTO response = workstationMapper.entityToDto(save);

        GenericResponse<WorkstationDTO> genericResponse = GenericResponse.<WorkstationDTO>builder()
                .status("success")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(genericResponse);
    }

    @Operation(summary = "Update an existing workstation", tags = {"workstations"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "workstation is updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = WorkstationResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "workstation not exists",
                    content = @Content)
    })
    @PutMapping(path = "/{id}")
    public GenericResponse<WorkstationDTO> update(
            @Parameter(description = "id of the workstation to be updated", example = "1")
            @NotNull(message = "workstation id cannot be null")
            @PathVariable("id") Integer id,
            @Parameter(description = "Workstation to update. Cannot null or empty.",
                    required = true, schema = @Schema(implementation = WorkstationDTO.class))
            @Valid @RequestBody WorkstationDTO request) {
        final WorkstationDTO update = workstationService.update(id, request);
        return GenericResponse.<WorkstationDTO>builder()
                .status("success")
                .data(update)
                .build();
    }

    @Operation(summary = "Activate an existing workstation", tags = {"workstations"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "workstation is activated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = WorkstationResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "workstation not exists",
                    content = @Content)
    })
    @PutMapping(path = "/{id}/actions/activate")
    public GenericResponse<String> activate(@Parameter(description = "id of the workstation to be activated", example = "1")
                                            @NotNull(message = "workstation id cannot be null")
                                            @PathVariable("id") Integer id) {
        final WorkstationDTO activatedWorkstation = workstationService.activate(id);
        return GenericResponse.<String>builder()
                .status("success")
                .data("workstation is activated successfully")
                .build();
    }

    @Operation(summary = "Deactivate an existing workstation", tags = {"workstations"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "workstation is deactivated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = WorkstationResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "workstation not exists",
                    content = @Content)
    })
    @PutMapping(path = "/{id}/actions/deactivate")
    public GenericResponse<String> deactivate(@Parameter(description = "id of the workstation to be deactivated", example = "1")
                                              @NotNull(message = "workstation id cannot be null")
                                              @PathVariable("id") Integer id) {
        final WorkstationDTO activatedWorkstation = workstationService.deactivate(id);
        return GenericResponse.<String>builder()
                .status("success")
                .data("workstation is deactivated successfully")
                .build();
    }

    @Operation(summary = "Delete an existing workstation", tags = {"workstations"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "workstation is deactivated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = WorkstationResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "workstation not exists",
                    content = @Content)
    })
    @DeleteMapping(path = "/{id}")
    public GenericResponse<String> delete(@PathVariable("id") Integer id) {

        final boolean delete = workstationService.delete(id);
        return GenericResponse.<String>builder()
                .status("success")
                .data("workstation deleted successfully")
                .build();
    }

    // action multiple
    @Operation(summary = "Activate multiple workstations", tags = {"workstations"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "workstations are activated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = WorkstationResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "workstation not exists",
                    content = @Content)
    })
    @PutMapping(path = "/actions/activate")
    public GenericResponse<String> bulkActivate(@Valid @RequestBody List<Integer> ids) {
        workstationService.bulkActivate(ids);
        return GenericResponse.<String>builder()
                .status("success")
                .data("workstations are activated successfully")
                .build();
    }

    @Operation(summary = "Deactivate multiple workstations", tags = {"workstations"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "workstations are deactivated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = WorkstationResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "workstation not exists",
                    content = @Content)
    })
    @PutMapping(path = "/actions/deactivate")
    public GenericResponse<String> bulkDeactivate(@RequestBody List<Integer> ids) {
        workstationService.bulkDeactivate(ids);
        return GenericResponse.<String>builder()
                .status("success")
                .data("workstations are deactivated successfully")
                .build();
    }

    @Operation(summary = "Delete multiple workstations", tags = {"workstations"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "workstations are deactivated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = WorkstationResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "workstation not exists",
                    content = @Content)
    })
    @PutMapping(path = "/actions/delete")
    public GenericResponse<String> bulkDelete(@RequestBody List<Integer> ids) {
        workstationService.bulkDelete(ids);
        return GenericResponse.<String>builder()
                .status("success")
                .data("workstations are deleted successfully")
                .build();
    }

    @Operation(summary = "Find appointments created for a workstation", tags = {"appointments"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the workstation appointments",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = WorkstationResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "workstation not exists",
                    content = @Content)
    })
    @GetMapping(path = "/{id}/appointments")
    public GenericResponse<PagedData<AppointmentDTO>> workstationAppointments(
            @Parameter(description = "Id of the workstation for which appointments are fetched. Cannot be blank.", required = true)
            @NotNull(message = "workstation id cannot be null")
            @PathVariable("id") Integer id,
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

        PagedData<AppointmentDTO> appointmentsByWorkstationId = this.appointmentService.findAppointmentsByWorkstationId(id, paging);
        return GenericResponse.<PagedData<AppointmentDTO>>builder()
                .status("success")
                .data(appointmentsByWorkstationId)
                .build();
    }
}
