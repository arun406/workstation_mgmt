package com.accelya.product.workstationmanagement.workstation.mapper;

import com.accelya.product.workstationmanagement.job.model.Flight;
import com.accelya.product.workstationmanagement.job.model.JobParameters;
import com.accelya.product.workstationmanagement.job.model.Shipment;
import com.accelya.product.workstationmanagement.job.model.ULD;
import com.accelya.product.workstationmanagement.job.transferobjects.FlightDTO;
import com.accelya.product.workstationmanagement.job.transferobjects.JobParametersDTO;
import com.accelya.product.workstationmanagement.job.transferobjects.ShipmentDTO;
import com.accelya.product.workstationmanagement.job.transferobjects.UldDTO;
import com.accelya.product.workstationmanagement.workstation.model.Workstation;
import com.accelya.product.workstationmanagement.workstation.transferobjects.WorkstationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WorkstationMapper {

    Workstation dtoToEntity(WorkstationDTO dto);

    WorkstationDTO entityToDto(Workstation entity);

    List<WorkstationDTO> entityListToDtoList(List<Workstation> entities);

    List<Workstation> dtoListToEntityList(List<WorkstationDTO> dtos);


    @Mappings({
            @Mapping(target = "tareWeight", source = "tareWeight.value"),
            @Mapping(target = "maximumWeight", source = "maximumWeight.value"),
            @Mapping(target = "actualWeight", source = "actualWeight.value"),
            @Mapping(target = "maximumVolume", source = "maximumVolume.value"),
            @Mapping(target = "actualVolume", source = "actualVolume.value")
    })
    ULD dtoToEntity(UldDTO dto);

    @Mappings({
            @Mapping(target = "tareWeight.value", source = "tareWeight"),
            @Mapping(target = "maximumWeight.value", source = "maximumWeight"),
            @Mapping(target = "actualWeight.value", source = "actualWeight"),
            @Mapping(target = "maximumVolume.value", source = "maximumVolume"),
            @Mapping(target = "actualVolume.value", source = "actualVolume")
    })
    UldDTO entityToDto(ULD entity);

    @Mappings({
            @Mapping(target = "origin", source = "origin.code"),
            @Mapping(target = "destination", source = "destination.code"),
            @Mapping(target = "piece", source = "quantity.piece"),
            @Mapping(target = "weight", source = "quantity.weight.value"),
            @Mapping(target = "volume", source = "quantity.volume.value"),
            @Mapping(target = "weightUnit", source = "quantity.weight.unit.code"),
            @Mapping(target = "volumeUnit", source = "quantity.volume.unit.code")
    })
    Shipment dtoToEntity(ShipmentDTO dto);

    @Mappings({
            @Mapping(target = "origin.code", source = "origin"),
            @Mapping(target = "destination.code", source = "destination"),
            @Mapping(source = "piece", target = "quantity.piece"),
            @Mapping(source = "weight", target = "quantity.weight.value"),
            @Mapping(source = "volume", target = "quantity.volume.value"),
            @Mapping(source = "weightUnit", target = "quantity.weight.unit.code"),
            @Mapping(source = "volumeUnit", target = "quantity.volume.unit.code")
    })
    ShipmentDTO entityToDto(Shipment shipment);

    @Mappings({
            @Mapping(target = "flight", source = "flight"),
            @Mapping(target = "uldGroupName", source = "uldGroupName"),
            @Mapping(target = "uldGroupCode", source = "uldGroupCode")
    })
    JobParametersDTO entityToDto(JobParameters entity);


    @Mappings({
            @Mapping(source = "flight", target = "flight"),
            @Mapping(source = "uldGroupName", target = "uldGroupName"),
            @Mapping(source = "uldGroupCode", target = "uldGroupCode")
    })
    JobParameters dtoToEntity(JobParametersDTO dto);

    @Mappings({
            @Mapping(source = "aircraftRegistration", target = "aircraftRegistration"),
            @Mapping(source = "transportInfo.carrier", target = "carrier"),
            @Mapping(source = "transportInfo.number", target = "flightNumber"),
            @Mapping(source = "transportInfo.extensionNumber", target = "extensionNumber"),
            @Mapping(source = "departureDateTimeLocal.schedule", target = "std"),
            @Mapping(source = "departureDateTimeLocal.estimated", target = "etd"),
            @Mapping(source = "departureDateTimeLocal.actual", target = "atd"),
            @Mapping(source = "arrivalDateTimeLocal.schedule", target = "sta"),
            @Mapping(source = "arrivalDateTimeLocal.estimated", target = "eta"),
            @Mapping(source = "arrivalDateTimeLocal.actual", target = "ata")
    })
    Flight dtoToEntity(FlightDTO dto);

    @Mappings({
            @Mapping(target = "aircraftRegistration", source = "aircraftRegistration"),
            @Mapping(target = "transportInfo.carrier", source = "carrier"),
            @Mapping(target = "transportInfo.number", source = "flightNumber"),
            @Mapping(target = "transportInfo.extensionNumber", source = "extensionNumber"),
            @Mapping(target = "departureDateTimeLocal.schedule", source = "std"),
            @Mapping(target = "departureDateTimeLocal.estimated", source = "etd"),
            @Mapping(target = "departureDateTimeLocal.actual", source = "atd"),
            @Mapping(target = "arrivalDateTimeLocal.schedule", source = "sta"),
            @Mapping(target = "arrivalDateTimeLocal.estimated", source = "eta"),
            @Mapping(target = "arrivalDateTimeLocal.actual", source = "ata")
    })
    FlightDTO entityToDto(Flight entity);
}
