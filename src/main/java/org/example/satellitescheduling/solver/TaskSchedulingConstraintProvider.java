/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.example.satellitescheduling.solver;

import org.example.satellitescheduling.domain.Task;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

import static org.example.satellitescheduling.domain.ConstraintCode.*;
import static org.optaplanner.core.api.score.stream.Joiners.equal;
import static org.optaplanner.core.api.score.stream.Joiners.filtering;

public class TaskSchedulingConstraintProvider implements ConstraintProvider {
    final int ORBIT_DISTANCE = 14;
    final String ROUTINE_TASK = "R";

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                // Hard constraints
                taskMustFitInVisibilityZone(constraintFactory),
                noOverlappingInVisibility(constraintFactory),
                taskNotAssignedToRequiredSatellite(constraintFactory),
                atLeastRoutineEveryOrbitInVisibility(constraintFactory),
                alternateType(constraintFactory),
                // Soft constraints
                taskShouldStartAfterEarliestTime(constraintFactory),
                taskShouldStartBeforeLatestTime(constraintFactory),
                maximizeDistanceBetweenTasks(constraintFactory)
        };
    }

    // ************************************************************************
    // Hard constraints
    // ************************************************************************

    public Constraint taskMustFitInVisibilityZone(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Task.class)
                .filter(task -> !task.getVisibility().canFitTask(task.getStartingTimeGrain().getGrainIndex(), task.getDuration()))
                .penalizeConfigurable(MUST_FIT_VISIBILITY_ZONE);
    }

    public Constraint noOverlappingInVisibility(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Task.class)
                .join(Task.class,
                        equal(Task::getVisibility),
                        filtering((task, otherTaskAssign) ->
                                !task.getId().equals(otherTaskAssign.getId()) && task.overlappedUnits(otherTaskAssign) > 0)
                ).penalizeConfigurable(NOT_OVERLAPPING_IN_SAME_VISIBILITY);
    }

    public Constraint taskNotAssignedToRequiredSatellite(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Task.class)
                .filter(task -> task.getRequiredSatellite() != null
                        && !task.getRequiredSatellite().equals(task.getVisibility().getStationSatellite().getSatellite()))
                .penalizeConfigurable(NOT_ASSIGNED_TO_NOT_REQUIRED_SATELLITE);
    }

    public Constraint atLeastRoutineEveryOrbitInVisibility(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Task.class)
                .filter(task -> task.getType().getName().equals(ROUTINE_TASK))
                .ifNotExists(Task.class,
                        filtering((task, otherTask) ->
                                otherTask.getStartingTimeGrain() != null &&
                                otherTask.getType().getName().equals(ROUTINE_TASK) &&
                                !task.getId().equals(otherTask.getId()) &&
                                task.getVisibility().equals(otherTask.getVisibility()) &&
                                task.distanceUnits(otherTask) < ORBIT_DISTANCE)
                )
                .penalizeConfigurable(AT_LEAST_ONE_ROUTINE_EVERY_ORBIT);
    }

    public Constraint alternateType(ConstraintFactory constraintFactory) {
        return constraintFactory.fromUniquePair(Task.class)
                .filter((task1, task2) -> task1.getType().equals(task2.getType()))
                .ifNotExists(Task.class,
                        filtering((task1, task2, task3) ->
                                task3.getStartingTimeGrain() != null &&
                                        !task3.getId().equals(task1.getId())
                                        && !task3.getId().equals(task2.getId())
                                        && task3.between(task1, task2)))
                .penalizeConfigurable(ALTERNATE_TYPE_BETWEEN_TASKS);

    }

    // ************************************************************************
    // Soft constraints
    // ************************************************************************

    public Constraint maximizeDistanceBetweenTasks(ConstraintFactory constraintFactory) {
        return constraintFactory.fromUniquePair(Task.class,equal(Task::getVisibility))
                .filter((task1, task2) -> task1.getType().getName().equals(ROUTINE_TASK) && task2.getType().getName().equals(ROUTINE_TASK))
                .penalizeConfigurable(MAXIMIZE_DISTANCE_BETWEEN_ROUTINE_TASKS, (task1, task2) -> task1.distanceUnits(task2));
    }

    public Constraint taskShouldStartAfterEarliestTime(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Task.class)
                .filter(task -> task.getStartingTimeGrain().getGrainIndex() < task.getEarlierStartTime())
                .penalizeConfigurable(SHOULD_START_AFTER_EARLIEST_TASK_START,
                        task -> task.getEarlierStartTime()
                                - task.getStartingTimeGrain().getGrainIndex());
    }

    public Constraint taskShouldStartBeforeLatestTime(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Task.class)
                .filter(task -> task.getStartingTimeGrain().getGrainIndex() > task.getLatestStartTime())
                .penalizeConfigurable(SHOULD_START_BEFORE_LATEST_TASK_START,
                        task -> task.getStartingTimeGrain().getGrainIndex() - task.getLatestStartTime());
    }
}
