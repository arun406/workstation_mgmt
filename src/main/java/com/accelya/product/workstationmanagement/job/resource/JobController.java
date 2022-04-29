package com.accelya.product.workstationmanagement.job.resource;

import com.accelya.product.workstationmanagement.job.service.JobService;
import com.accelya.product.workstationmanagement.job.transferobjects.JobDTO;
import com.accelya.product.workstationmanagement.transferobjects.GenericResponse;
import com.accelya.product.workstationmanagement.transferobjects.PagedData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cargo/job-mgmt/v1/jobs")
@RequiredArgsConstructor
public class JobController {

    final private JobService jobService;

    @GetMapping("/{id}")
    public JobDTO get(@PathVariable("id") Integer jobId) {
        return jobService.get(jobId);
    }


    @GetMapping
    public GenericResponse<PagedData<JobDTO>> list(@RequestParam(defaultValue = "0") int page,
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
        final PagedData<JobDTO> pagedData = jobService.list(paging);
        return GenericResponse.<PagedData<JobDTO>>builder()
                .status("success")
                .data(pagedData)
                .build();
    }


    @PostMapping
    public GenericResponse<JobDTO> save(@RequestBody JobDTO job) {
        final JobDTO result = jobService.save(job);

        return GenericResponse.<JobDTO>builder()
                .status("success")
                .data(result)
                .build();
    }
}
