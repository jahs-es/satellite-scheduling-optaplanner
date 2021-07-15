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

package org.example.satellitescheduling.domain;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import static org.example.satellitescheduling.domain.ConstraintCode.*;

@ConstraintConfiguration
public class TaskSchedulingConstraintConfiguration {

    @ConstraintWeight(SHOULD_START_AFTER_EARLIEST_TASK_START)
    public HardSoftScore taskShouldStartAfterEarliestStartTimeGrain = HardSoftScore.ofSoft(1);
    @ConstraintWeight(SHOULD_START_BEFORE_LATEST_TASK_START)
    public HardSoftScore taskShouldStopBeforeLatestStartTimeGrain = HardSoftScore.ofSoft(1);
    @ConstraintWeight(MUST_FIT_VISIBILITY_ZONE)
    public HardSoftScore taskMustBeScheludable = HardSoftScore.ofHard(1);
    @ConstraintWeight(NOT_OVERLAPPING_IN_SAME_VISIBILITY)
    public HardSoftScore tasksNotOverlappedSameVisibility = HardSoftScore.ofHard(1);
    @ConstraintWeight(NOT_ASSIGNED_TO_NOT_REQUIRED_SATELLITE)
    public HardSoftScore taskNotAssignedToNoRequiredSatellite = HardSoftScore.ofHard(1);
    @ConstraintWeight(AT_LEAST_ONE_ROUTINE_EVERY_ORBIT)
    public HardSoftScore atLeastOneRoutineEveryOrbit = HardSoftScore.ofHard(1);
    @ConstraintWeight(ALTERNATE_TYPE_BETWEEN_TASKS)
    public HardSoftScore alternateType = HardSoftScore.ofHard(1);
    @ConstraintWeight(MAXIMIZE_DISTANCE_BETWEEN_ROUTINE_TASKS)
    public HardSoftScore maximizeDistanceBetweenTasks = HardSoftScore.ofSoft(1);
}
