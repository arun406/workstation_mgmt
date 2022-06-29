package com.accelya.product.workstationmanagement.workstation.service;

import com.accelya.product.workstationmanagement.QuerySpecificationBuilder;
import com.accelya.product.workstationmanagement.SearchCriteria;
import com.accelya.product.workstationmanagement.workstation.mapper.WorkstationMapper;
import com.accelya.product.workstationmanagement.workstation.model.Workstation;
import com.accelya.product.workstationmanagement.workstation.repository.WorkstationRepository;
import com.accelya.product.workstationmanagement.workstation.transferobjects.PageInfo;
import com.accelya.product.workstationmanagement.workstation.transferobjects.PagedData;
import com.accelya.product.workstationmanagement.workstation.transferobjects.WorkstationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkstationService {

    final private WorkstationRepository repository;
    final private WorkstationMapper mapper;

    /**
     * get workstation by id
     *
     * @param id
     * @return
     */
    public WorkstationDTO get(Integer id) {
        final Workstation workstation = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "workstation not found"));
        return mapper.entityToDto(workstation);
    }

    /**
     * Search for workstations based on the criteria
     *
     * @param params
     * @param pageable
     * @return
     */
    public PagedData<WorkstationDTO> search(Map<String, String> params, Pageable pageable) {

        final List<SearchCriteria> searchCriteria = params.entrySet().stream()
                .filter(entry -> !(Arrays.asList("page", "size", "sortBy").contains(entry.getKey())))
                .map(entry -> new SearchCriteria(entry.getKey(), ":", entry.getValue()))
                .map(sc -> {
                    if (Arrays.asList("open", "serviceable", "multipleULDAllowed", "fixed", "active")
                            .contains(sc.getKey())) {
                        sc.setValue(Boolean.parseBoolean((String) sc.getValue()));
                    }
                    return sc;
                })
                .map(sc -> {
                    if (Arrays.asList("shc").contains(sc.getKey()) && StringUtils.hasText((String) sc.getValue())) {
                        sc.setValue(Arrays.asList(((String) sc.getValue()).split(",")));
                    }
                    return sc;
                })
                .collect(Collectors.toList());

        final Specification<Workstation> workstationSpecification
                = new QuerySpecificationBuilder<Workstation>(searchCriteria)
                .build();

        final Page<Workstation> workstationsPage = repository.findAll(workstationSpecification, pageable);
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

    /**
     * saves the workstation
     *
     * @param dto
     * @return
     */
    public Workstation save(WorkstationDTO dto) {
        final Workstation workstation = mapper.dtoToEntity(dto);
        workstation.setActive(Boolean.TRUE);
        return repository.save(workstation);
    }

    /**
     * Update the workstation with new data
     *
     * @param id
     * @param dto request information
     * @return updated workstation
     */
    public WorkstationDTO update(Integer id, WorkstationDTO dto) {
        final Workstation workstation = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "workstation not found"));
        workstation.setId(id);
        workstation.setAirport(dto.getAirport());
        workstation.setCode(dto.getCode());
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
        workstation.setWarehouse(dto.getWarehouse());
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
        // perform validation on jobs allocated to workstation.
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
        // perform validation on jobs allocated to workstation.
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
