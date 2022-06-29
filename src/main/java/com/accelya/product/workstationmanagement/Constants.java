package com.accelya.product.workstationmanagement;

public interface Constants {
    String CREATED = "C";   // created
    String ASSIGNED = "A"; // Assigned, Pending as per SDS
    String RUNNING = "R";   // running
    String FINISHED = "F";// completed
    String PAUSED = "P"; // Paused
    String STOPPED = "S"; // stopped
    String ENDED = "E"; // Ended
    String INCOMPLETE = "I";    // incomplete
    String DELETED = "X"; // DELETED
    String QRT_ULD = "QRT";
    String DEFAULT_GROUP = "DEFAULT_GROUP";
    String DEFAULT_GROUP_NAME = "Default Group";
    Integer DEFAULT_PRIORITY = 0;
    String BREAKDOWN_JOB_TYPE = "BREAKDOWN";
    String BUILDUP_JOB_TYPE = "BUILDUP";

    String APPOINTMENT_CREATED = "C";
    String APPOINTMENT_CANCELLED = "X";
    String APPOINTMENT_FINISHED = "F";
    String DEFAULT_ULD_GROUP = "DEFAULT_ULD_GROUP";
    String DEFAULT_ULD_GROUP_NAME = "DEFAULT_ULD_GROUP_NAME";

    String JOB_ACTION_START = "START";
    String JOB_ACTION_END = "END";
    String JOB_ACTION_PAUSE = "PAUSE";
    String JOB_ACTION_RESUME = "RESUME";
    String JOB_ACTION_RESTART = "RESTART";
    String JOB_ACTION_DELETE = "DELETE";
    String APPOINTMENT_STARTED = "S";
}
