package com.accelya.product.workstationmanagement.resource;

import com.accelya.product.workstationmanagement.model.Workstation;
import com.accelya.product.workstationmanagement.service.WorkstationService;
import com.accelya.product.workstationmanagement.transferobjects.GenericResponse;
import com.accelya.product.workstationmanagement.transferobjects.PagedData;
import com.accelya.product.workstationmanagement.transferobjects.WorkstationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cargo/reference-data/v1/warehouse/locations")
@RequiredArgsConstructor
public class WorkstationController {

    final private WorkstationService workstationService;

    @GetMapping(path = "/{id}")
    public GenericResponse<WorkstationDTO> get(@PathVariable("id") Integer id) {
        final WorkstationDTO workstationDTO = workstationService.get(id);
        return GenericResponse.<WorkstationDTO>builder()
                .status("success")
                .data(workstationDTO)
                .build();
    }

    @GetMapping
    public GenericResponse<PagedData<WorkstationDTO>> list(@RequestParam(defaultValue = "0") int page,
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
        final PagedData<WorkstationDTO> pagedData = workstationService.list(paging);
        return GenericResponse.<PagedData<WorkstationDTO>>builder()
                .status("success")
                .data(pagedData)
                .build();
    }

    @PostMapping
    public GenericResponse<String> save(@RequestBody WorkstationDTO request) {
        final Workstation save = workstationService.save(request);
        return GenericResponse.<String>builder()
                .status("success")
                .data("workstation created successfully")
                .build();
    }

    @PutMapping
    public GenericResponse<String> update(@RequestBody WorkstationDTO request) {
        final WorkstationDTO update = workstationService.update(request);
        return GenericResponse.<String>builder()
                .status("success")
                .data("workstation updated successfully")
                .build();
    }

    @PutMapping(path = "/{id}/actions/activate")
    public GenericResponse<String> activate(@PathVariable("id") Integer id) {
        final WorkstationDTO activatedWorkstation = workstationService.activate(id);
        return GenericResponse.<String>builder()
                .status("success")
                .data("workstation is activated successfully")
                .build();
    }

    @PutMapping(path = "/{id}/actions/deactivate")
    public GenericResponse<String> deactivate(@PathVariable("id") Integer id) {
        final WorkstationDTO activatedWorkstation = workstationService.deactivate(id);
        return GenericResponse.<String>builder()
                .status("success")
                .data("workstation deactivated successfully")
                .build();
    }

    @DeleteMapping(path = "/{id}")
    public GenericResponse<String> delete(@PathVariable("id") Integer id) {
        final boolean delete = workstationService.delete(id);
        return GenericResponse.<String>builder()
                .status("success")
                .data("workstation deleted successfully")
                .build();
    }

    // action multiple

    @PutMapping(path = "/actions/activate")
    public GenericResponse<String> bulkActivate(@RequestBody List<Integer> ids) {
        workstationService.bulkActivate(ids);
        return GenericResponse.<String>builder()
                .status("success")
                .data(String.format("workstations : %s  are activated successfully", ids))
                .build();
    }

    @PutMapping(path = "/actions/deactivate")
    public GenericResponse<String> bulkDeactivate(@RequestBody List<Integer> ids) {
        workstationService.bulkDeactivate(ids);
        return GenericResponse.<String>builder()
                .status("success")
                .data(String.format("workstations : %s  are deactivated successfully", ids))
                .build();
    }

    @DeleteMapping(path = "/actions/delete")
    public GenericResponse<String> bulkDelete(@RequestBody List<Integer> ids) {
        workstationService.bulkDelete(ids);
        return GenericResponse.<String>builder()
                .status("success")
                .data(String.format("workstations : %s  are deleted successfully", ids))
                .build();
    }
}
