package com.accelya.product.workstationmanagement.job.service;

import com.accelya.product.workstationmanagement.job.mapper.JobMapper;
import com.accelya.product.workstationmanagement.job.model.Job;
import com.accelya.product.workstationmanagement.job.repository.JobRepository;
import com.accelya.product.workstationmanagement.job.transferobjects.JobDTO;
import com.accelya.product.workstationmanagement.model.Workstation;
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
@Slf4j
@RequiredArgsConstructor
public class JobService {
    final private JobRepository jobRepository;
    final private JobMapper jobMapper;

    public JobDTO get(Integer id) {
        final Job job = jobRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "job not found"));
        return jobMapper.entityToDto(job);
    }

    public PagedData<JobDTO> list(Pageable pageable) {
        final Page<Job> page = jobRepository.findAll(pageable);
        final List<Job> jobs = page.getContent();

        final List<JobDTO> jobDTOS = jobMapper.entityListToDtoList(jobs);
        PageInfo pageInfo = PageInfo.builder()
                .listSize(page.getTotalElements())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .build();
        return PagedData.<JobDTO>builder()
                .pageInfo(pageInfo)
                .list(jobDTOS)
                .build();
    }

    public JobDTO save(JobDTO job) {
        final Job entity = jobMapper.dtoToEntity(job);
        entity.setStatus("C");
        final Job savedJob = this.jobRepository.save(entity);
        job.setId(savedJob.getId());
        return job;
    }
}
