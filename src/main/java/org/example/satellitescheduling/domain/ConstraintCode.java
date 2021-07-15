package org.example.satellitescheduling.domain;

public class ConstraintCode {
    public static final String SHOULD_START_AFTER_EARLIEST_TASK_START = "SHOULD_START_AFTER_EARLIEST_TASK_START";
    public static final String SHOULD_START_BEFORE_LATEST_TASK_START = "SHOULD_START_BEFORE_LATEST_TASK_START";
    public static final String MUST_FIT_VISIBILITY_ZONE = "MUST_FIT_VISIBILITY_ZONE";
    public static final String NOT_OVERLAPPING_IN_SAME_VISIBILITY = "NOT_OVERLAPPING_IN_SAME_VISIBILITY";
    public static final String NOT_ASSIGNED_TO_NOT_REQUIRED_SATELLITE = "NOT_ASSIGNED_TO_NOT_REQUIRED_SATELLITE";
    public static final String AT_LEAST_ONE_ROUTINE_EVERY_ORBIT = "AT_LEAST_ONE_ROUTINE_EVERY_ORBIT";
    public static final String ALTERNATE_TYPE_BETWEEN_TASKS = "ALTERNATE_TYPE_BETWEEN_TASKS";
    public static final String MAXIMIZE_DISTANCE_BETWEEN_ROUTINE_TASKS = "MAXIMIZE_DISTANCE_BETWEEN_ROUTINE_TASKS";
}
