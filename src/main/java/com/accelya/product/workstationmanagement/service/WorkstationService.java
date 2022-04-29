package com.accelya.product.workstationmanagement.service;

import com.accelya.product.workstationmanagement.mapper.WorkstationMapper;
import com.accelya.product.workstationmanagement.model.Workstation;
import com.accelya.product.workstationmanagement.repository.WorkstationRepository;
import com.accelya.product.workstationmanagement.transferobjects.PageInfo;
import com.accelya.product.workstationmanagement.transferobjects.PagedData;
import com.accelya.product.workstationmanagement.transferobjects.WorkstationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkstationService {

    final private WorkstationRepository repository;
    final private WorkstationMapper mapper;

    public WorkstationDTO get(Integer id) {
        final Workstation workstation = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "workstation not found"));
        return mapper.entityToDto(workstation);
    }

    public PagedData<WorkstationDTO> list(Pageable pageable) {
        final Page<Workstation> workstationsPage = repository.findAll(pageable);
        final List<Workstation> workstations = workstationsPage.getContent();

        final List<WorkstationDTO> workstationDTOS = mapper.entityListToDtoList(workstations);
        PageInfo pageInfo = PageInfo.builder()
                .listSize(workstationsPage.getTotalElements())
                .pageNumber(workstationsPage.getNumber())
                .pageSize(workstationsPage.getSize())
                .totalPages(workstationsPage.getTotalPages())
                .build();
        return PagedData.<WorkstationDTO>builder()
                .pageInfo(pageInfo)
                .list(workstationDTOS)
                .build();

    }

    public Workstation save(WorkstationDTO dto) {
        final Workstation workstation = mapper.dtoToEntity(dto);
        return repository.save(workstation);
    }

    /**
     * Update the workstation with new data
     *
     * @param dto request information
     * @return updated workstation
     */
    public WorkstationDTO update(WorkstationDTO dto) {
        final Workstation workstation = repository.findById(dto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "workstation not found"));

        workstation.setAirportCode(dto.getAirportCode());
        workstation.setBreakTimeEnd(dto.getBreakTimeEnd());
        workstation.setBreakTimeStart(dto.getBreakTimeStart());
        workstation.setFixed(dto.getFixed());
        workstation.setCompatibleTypes(dto.getCompatibleTypes());
        workstation.setName(dto.getName());
        workstation.setServiceable(dto.getServiceable());
        workstation.setMultipleULDAllowed(dto.getMultipleULDAllowed());
        workstation.setNotificationTime(dto.getNotificationTime());
        workstation.setOpen(dto.getOpen());
        workstation.setProductType(dto.getProductType());
        workstation.setType(dto.getType());
        workstation.setShc(dto.getShc());
        workstation.setSection(dto.getSection());
        workstation.setWarehouseCode(dto.getWarehouseCode());
        workstation.setSize(dto.getSize());
        final Workstation save = repository.save(workstation);
        return mapper.entityToDto(save);
    }

    /**
     * delete a workstation. Throws 404 if workstation not found
     *
     * @param id workstation id
     * @return true of false based on the delete operation
     */
    public boolean delete(Integer id) {
        repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "workstation not found"));
        repository.deleteById(id);
        return true;
    }

    /**
     * Activate a workstation. Throws 404 if workstation not found
     *
     * @param id workstation id
     * @return workstation
     */
    public WorkstationDTO activate(Integer id) {
        final Workstation workstation = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "workstation not found"));
        workstation.setActive(Boolean.TRUE);
        // update the database
        final Workstation savedEntity = repository.save(workstation);
        return mapper.entityToDto(savedEntity);
    }

    /**
     * Deactivate a workstation. Throws 404 if workstation not found
     *
     * @param id workstation id
     * @return workstation
     */
    public WorkstationDTO deactivate(Integer id) {
        final Workstation workstation = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "workstation not found"));
        workstation.setActive(Boolean.FALSE);
        // update the database
        final Workstation savedEntity = repository.save(workstation);
        return mapper.entityToDto(savedEntity);
    }

    /**
     * activate multiple workstations
     *
     * @param ids
     */
    public void bulkActivate(List<Integer> ids) {
        if (ids != null && !ids.isEmpty()) {
            ids.forEach(this::activate);
        }
    }

    /**
     * deactivate multiple workstations
     *
     * @param ids
     */
    public void bulkDeactivate(List<Integer> ids) {
        if (ids != null && !ids.isEmpty()) {
            ids.forEach(this::deactivate);
        }
    }

    /**
     * delete multiple workstations
     *
     * @param ids
     */
    public void bulkDelete(List<Integer> ids) {
        if (ids != null && !ids.isEmpty()) {
            try {
                ids.forEach(this::delete);
            } catch (ResponseStatusException rse) {
                log.debug(rse.getMessage(), rse);
            }
        }
    }
}
