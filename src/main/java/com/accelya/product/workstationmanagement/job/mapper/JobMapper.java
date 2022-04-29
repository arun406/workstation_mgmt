package com.accelya.product.workstationmanagement.job.mapper;

import com.accelya.product.workstationmanagement.job.model.Job;
import com.accelya.product.workstationmanagement.job.transferobjects.JobDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface JobMapper {
    Job dtoToEntity(JobDTO dto);

    JobDTO entityToDto(Job entity);

    List<JobDTO> entityListToDtoList(List<Job> entities);
}
