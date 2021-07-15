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

import io.quarkus.test.junit.QuarkusTest;
import org.example.satellitescheduling.domain.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

import javax.inject.Inject;

@QuarkusTest
public class TaskSchedulingConstraintProviderTest {
    static TaskType routine;
    static TaskType NoRoutine;

    @Inject
    ConstraintVerifier<TaskSchedulingConstraintProvider, TaskSchedule> constraintVerifier;

    @BeforeAll
    public static void init() {
        routine = new TaskType("R");
        NoRoutine = new TaskType("NR");
    }

    @Test
    public void taskShouldStartAfterEarliestTimeUnpenalized() {
        Task task = new Task(null, null, NoRoutine,5,40,0,1,null,null);
        TimeGrain startingTimeGrain = new TimeGrain(0);
        task.setStartingTimeGrain(startingTimeGrain);

        StationSatellite stationSatellite = new StationSatellite("S1","E2");
        Visibility visibility = new Visibility(5,7, stationSatellite);
        task.setVisibility(visibility);

        constraintVerifier.verifyThat(TaskSchedulingConstraintProvider::taskShouldStartAfterEarliestTime)
                .given(task)
                .penalizesBy(5);
    }

    @Test
    public void taskShouldStartAfterEarliestTimePenalized() {
        Task task = new Task(null, null, NoRoutine,5,40,0,1, null,null);
        TimeGrain startingTimeGrain = new TimeGrain(4);
        task.setStartingTimeGrain(startingTimeGrain);

        StationSatellite stationSatellite = new StationSatellite("S1","E2");
        Visibility visibility = new Visibility(5,7, stationSatellite);
        task.setVisibility(visibility);

        constraintVerifier.verifyThat(TaskSchedulingConstraintProvider::taskShouldStartAfterEarliestTime)
                .given(task)
                .penalizesBy(1);
    }

    @Test
    public void taskShouldStartBeforeLatestTimePenalized() {
        Task task = new Task(null, null, NoRoutine,5,40,0,1,null,null);
        TimeGrain startingTimeGrain = new TimeGrain(42);
        task.setStartingTimeGrain(startingTimeGrain);

        StationSatellite stationSatellite = new StationSatellite("S1","E2");
        Visibility visibility = new Visibility(5,7, stationSatellite);
        task.setVisibility(visibility);

        constraintVerifier.verifyThat(TaskSchedulingConstraintProvider::taskShouldStartBeforeLatestTime)
                .given(task)
                .penalizesBy(2);
    }

    @Test
    public void taskShouldStartBeforeLatestTimeUnpenalized() {
        Task task = new Task(null, null, NoRoutine,5,40,0,1, null,null);
        TimeGrain startingTimeGrain = new TimeGrain(39);
        task.setStartingTimeGrain(startingTimeGrain);

        constraintVerifier.verifyThat(TaskSchedulingConstraintProvider::taskShouldStartBeforeLatestTime)
                .given(task)
                .penalizesBy(0);
    }

    @Test
    public void taskMustFitInVisibilityZoneUnpenalized() {
        Task task = new Task(null, null, NoRoutine,5,40,6,2,null,null);
        TimeGrain startingTimeGrain = new TimeGrain(5);
        task.setStartingTimeGrain(startingTimeGrain);

        StationSatellite stationSatellite = new StationSatellite("S1","E2");
        Visibility visibility = new Visibility(5,7, stationSatellite);
        task.setVisibility(visibility);

        constraintVerifier.verifyThat(TaskSchedulingConstraintProvider::taskMustFitInVisibilityZone)
                .given(task)
                .penalizesBy(0);
    }

    @Test
    public void taskMustFitInVisibilityZonePenalized() {
        Task task = new Task(null, null, NoRoutine,5,40,6,2,null,null);
        TimeGrain startingTimeGrain = new TimeGrain(3);
        task.setStartingTimeGrain(startingTimeGrain);

        StationSatellite stationSatellite = new StationSatellite("S1","E2");
        Visibility visibility = new Visibility(5,7, stationSatellite);
        task.setVisibility(visibility);

        constraintVerifier.verifyThat(TaskSchedulingConstraintProvider::taskMustFitInVisibilityZone)
                .given(task)
                .penalizesBy(1);
    }

    @Test
    public void noOverlappingInVisibilityUnpenalized() {
        Task task = new Task(null, null, NoRoutine,0,8,0,2,null,null);
        TimeGrain startingTimeGrain = new TimeGrain(0);
        task.setStartingTimeGrain(startingTimeGrain);

        StationSatellite stationSatellite1 = new StationSatellite("S1","E2");
        Visibility visibility1_1 = new Visibility(0,10, stationSatellite1);
        task.setVisibility(visibility1_1);
        task.setId(0L);

        Task otherTask = new Task(null, null, NoRoutine,5,8,5,1,null,null);
        TimeGrain startingTimeGrainOtherTask = new TimeGrain(5);
        otherTask.setStartingTimeGrain(startingTimeGrainOtherTask);

        otherTask.setVisibility(visibility1_1);
        otherTask.setId(1L);

        constraintVerifier.verifyThat(TaskSchedulingConstraintProvider::noOverlappingInVisibility)
                .given(task, otherTask)
                .penalizesBy(0);
    }

    @Test
    public void noOverlappingInVisibilityPenalized() {
        Task task = new Task(null, null, NoRoutine,0,8,0,2,null,null);
        TimeGrain startingTimeGrain = new TimeGrain(5);
        task.setStartingTimeGrain(startingTimeGrain);

        StationSatellite stationSatellite1 = new StationSatellite("S1","E2");
        Visibility visibility1_1 = new Visibility(0,10, stationSatellite1);
        task.setVisibility(visibility1_1);
        task.setId(0L);

        Task otherTask = new Task(null, null, NoRoutine,5,8,5,1,null,null);
        TimeGrain startingTimeGrainOtherTask = new TimeGrain(5);
        otherTask.setStartingTimeGrain(startingTimeGrainOtherTask);

        otherTask.setVisibility(visibility1_1);
        otherTask.setId(1L);

        constraintVerifier.verifyThat(TaskSchedulingConstraintProvider::noOverlappingInVisibility)
                .given(task, otherTask)
                .penalizesBy(2);
    }

    @Test
    public void taskNotAssignedToRequiredSatelliteUnpenalized() {
        Task task = new Task("S1", null, NoRoutine,5,40,6,2,null,null);
        TimeGrain startingTimeGrain = new TimeGrain(5);
        task.setStartingTimeGrain(startingTimeGrain);

        StationSatellite stationSatellite = new StationSatellite("S1","E2");
        Visibility visibility = new Visibility(5,7, stationSatellite);
        task.setVisibility(visibility);

        constraintVerifier.verifyThat(TaskSchedulingConstraintProvider::taskNotAssignedToRequiredSatellite)
                .given(task)
                .penalizesBy(0);
    }

    @Test
    public void taskNotAssignedToRequiredSatellitePenalized() {
        Task task = new Task("S1", null, NoRoutine,5,40,6,2,null,null);
        TimeGrain startingTimeGrain = new TimeGrain(5);
        task.setStartingTimeGrain(startingTimeGrain);

        StationSatellite stationSatellite = new StationSatellite("S2","E2");
        Visibility visibility = new Visibility(5,7, stationSatellite);
        task.setVisibility(visibility);

        constraintVerifier.verifyThat(TaskSchedulingConstraintProvider::taskNotAssignedToRequiredSatellite)
                .given(task)
                .penalizesBy(1);
    }

    @Test
    public void atLeastRoutineEveryOrbitInVisibilityUnpenalized() {
        Task task = new Task(null, null, routine,0,8,0,2,null,null);
        TimeGrain startingTimeGrain = new TimeGrain(0);
        task.setStartingTimeGrain(startingTimeGrain);

        StationSatellite stationSatellite1 = new StationSatellite("S1","E2");
        Visibility visibility1_1 = new Visibility(0,10, stationSatellite1);
        task.setVisibility(visibility1_1);
        task.setId(0L);

        Task otherTask = new Task(null, null, routine,5,8,5,1,null,null);
        TimeGrain startingTimeGrainOtherTask = new TimeGrain(5);
        otherTask.setStartingTimeGrain(startingTimeGrainOtherTask);

        otherTask.setVisibility(visibility1_1);
        otherTask.setId(1L);

        constraintVerifier.verifyThat(TaskSchedulingConstraintProvider::atLeastRoutineEveryOrbitInVisibility)
                .given(task, otherTask)
                .penalizesBy(0);
    }

    @Test
    public void atLeastRoutineEveryOrbitInVisibilityPenalizedByType() {
        Task task = new Task(null, null, NoRoutine,0,8,0,2,null,null);
        TimeGrain startingTimeGrain = new TimeGrain(0);
        task.setStartingTimeGrain(startingTimeGrain);

        StationSatellite stationSatellite1 = new StationSatellite("S1","E2");
        Visibility visibility1_1 = new Visibility(0,10, stationSatellite1);
        task.setVisibility(visibility1_1);
        task.setId(0L);

        Task otherTask = new Task(null, null, routine,5,8,5,1,null,null);
        TimeGrain startingTimeGrainOtherTask = new TimeGrain(5);
        otherTask.setStartingTimeGrain(startingTimeGrainOtherTask);

        otherTask.setVisibility(visibility1_1);
        otherTask.setId(1L);

        constraintVerifier.verifyThat(TaskSchedulingConstraintProvider::atLeastRoutineEveryOrbitInVisibility)
                .given(task, otherTask)
                .penalizesBy(1);
    }

    @Test
    public void atLeastRoutineEveryOrbitInVisibilityPenalizedByTime() {
        Task task = new Task(null, null, routine,0,8,0,2,null,null);
        TimeGrain startingTimeGrain = new TimeGrain(0);
        task.setStartingTimeGrain(startingTimeGrain);

        StationSatellite stationSatellite1 = new StationSatellite("S1","E2");
        Visibility visibility1_1 = new Visibility(0,20, stationSatellite1);
        task.setVisibility(visibility1_1);
        task.setId(0L);

        Task otherTask = new Task(null, null, routine,5,20,5,1,null,null);
        TimeGrain startingTimeGrainOtherTask = new TimeGrain(15);
        otherTask.setStartingTimeGrain(startingTimeGrainOtherTask);

        otherTask.setVisibility(visibility1_1);
        otherTask.setId(1L);

        constraintVerifier.verifyThat(TaskSchedulingConstraintProvider::atLeastRoutineEveryOrbitInVisibility)
                .given(task, otherTask)
                .penalizesBy(2);
    }

    @Test
    public void alternateTypePenalized() {
        StationSatellite stationSatellite = new StationSatellite("S1","E2");

        Visibility visibility2_1 = new Visibility(6,12, stationSatellite);

        Task firstTask = new Task(null, null, routine,5,40,0,1,null,null);
        TimeGrain startingTimeGrainFirstTask = new TimeGrain(5);
        firstTask.setStartingTimeGrain(startingTimeGrainFirstTask);
        firstTask.setVisibility(visibility2_1);
        firstTask.setId(0L);

        Task secondTask = new Task(null, null, routine,5,40,0,1,null,null);
        TimeGrain startingTimeGrainSecondTask = new TimeGrain(6);
        secondTask.setStartingTimeGrain(startingTimeGrainSecondTask);
        secondTask.setVisibility(visibility2_1);
        secondTask.setId(1L);

        Task thirdTask = new Task(null, null, NoRoutine,5,40,0,1,null,null);
        TimeGrain startingTimeGrainThirdTask = new TimeGrain(7);
        thirdTask.setStartingTimeGrain(startingTimeGrainThirdTask);
        thirdTask.setVisibility(visibility2_1);
        thirdTask.setId(2L);

        constraintVerifier.verifyThat(TaskSchedulingConstraintProvider::alternateType)
                .given(firstTask, secondTask, thirdTask)
                .penalizesBy(1);
    }

    @Test
    public void alternateTypeUnpenalized() {
        StationSatellite stationSatellite = new StationSatellite("S1","E2");

        Visibility visibility2_1 = new Visibility(6,12, stationSatellite);

        Task firstTask = new Task(null, null, routine,5,40,0,1,null,null);
        TimeGrain startingTimeGrainFirstTask = new TimeGrain(5);
        firstTask.setStartingTimeGrain(startingTimeGrainFirstTask);
        firstTask.setVisibility(visibility2_1);
        firstTask.setId(0L);

        Task secondTask = new Task(null, null, NoRoutine,5,40,0,1,null,null);
        TimeGrain startingTimeGrainSecondTask = new TimeGrain(6);
        secondTask.setStartingTimeGrain(startingTimeGrainSecondTask);
        secondTask.setVisibility(visibility2_1);
        secondTask.setId(1L);

        Task thirdTask = new Task(null, null, routine,5,40,0,1,null,null);
        TimeGrain startingTimeGrainThirdTask = new TimeGrain(7);
        thirdTask.setStartingTimeGrain(startingTimeGrainThirdTask);
        thirdTask.setVisibility(visibility2_1);
        thirdTask.setId(2L);

        constraintVerifier.verifyThat(TaskSchedulingConstraintProvider::alternateType)
                .given(firstTask, secondTask, thirdTask)
                .penalizesBy(0);
    }

    @Test
    public void maximizeDistanceBetweenTasks() {
        StationSatellite stationSatellite = new StationSatellite("S1","E2");

        Visibility visibility2_1 = new Visibility(6,25, stationSatellite);

        Task firstTask = new Task(null, null, routine,5,40,0,1,null,null);
        TimeGrain startingTimeGrainFirstTask = new TimeGrain(6);
        firstTask.setStartingTimeGrain(startingTimeGrainFirstTask);
        firstTask.setVisibility(visibility2_1);
        firstTask.setId(0L);

        Task secondTask = new Task(null, null, NoRoutine,5,40,0,1,null,null);
        TimeGrain startingTimeGrainSecondTask = new TimeGrain(8);
        secondTask.setStartingTimeGrain(startingTimeGrainSecondTask);
        secondTask.setVisibility(visibility2_1);
        secondTask.setId(1L);

        Task thirdTask = new Task(null, null, routine,5,40,0,1,null,null);
        TimeGrain startingTimeGrainThirdTask = new TimeGrain(20);
        thirdTask.setStartingTimeGrain(startingTimeGrainThirdTask);
        thirdTask.setVisibility(visibility2_1);
        thirdTask.setId(2L);

        constraintVerifier.verifyThat(TaskSchedulingConstraintProvider::maximizeDistanceBetweenTasks)
                .given(firstTask, secondTask, thirdTask)
                .penalizesBy(14);
    }
}
