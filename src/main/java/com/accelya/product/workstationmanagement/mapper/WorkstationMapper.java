package com.accelya.product.workstationmanagement.mapper;

import com.accelya.product.workstationmanagement.model.Workstation;
import com.accelya.product.workstationmanagement.transferobjects.WorkstationDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WorkstationMapper {

    Workstation dtoToEntity(WorkstationDTO dto);

    WorkstationDTO entityToDto(Workstation entity);

    List<WorkstationDTO> entityListToDtoList(List<Workstation> entities);
}
