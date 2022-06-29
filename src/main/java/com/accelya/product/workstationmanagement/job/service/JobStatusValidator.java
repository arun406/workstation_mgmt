package com.accelya.product.workstationmanagement.job.service;

import com.accelya.product.workstationmanagement.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

@Service
public class JobStatusValidator {

    /**
     * @param status
     * @param action
     * @return
     */
    public boolean validate(String status, String action) {
        if (Constants.JOB_ACTION_START.equalsIgnoreCase(action) && (!Constants.ASSIGNED.equalsIgnoreCase(status) &&
                !Constants.PAUSED.equalsIgnoreCase(status))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Job is neither in assigned nor paused status.");
        }
        if (Constants.JOB_ACTION_END.equalsIgnoreCase(action) && !Constants.RUNNING.equalsIgnoreCase(status)
                && !Constants.PAUSED.equalsIgnoreCase(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Job is neither running nor paused.");
        }
        if (Constants.JOB_ACTION_PAUSE.equalsIgnoreCase(action) && !Arrays.asList(Constants.RUNNING).contains(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Job is not running.");
        }
        if (Constants.JOB_ACTION_RESUME.equalsIgnoreCase(action) && !Constants.PAUSED.equalsIgnoreCase(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Job is not paused status.");
        }
        if (Constants.JOB_ACTION_DELETE.equalsIgnoreCase(action) && Constants.RUNNING.equalsIgnoreCase(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A running job cannot be deleted.");
        }
        if (Constants.JOB_ACTION_RESTART.equalsIgnoreCase(action) && !Constants.ENDED.equalsIgnoreCase(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Job is not in ended status.");
        }
        return true;
    }
}
