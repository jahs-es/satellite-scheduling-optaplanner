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

package org.example.satellitescheduling.rest;

import io.quarkus.test.junit.QuarkusTest;
import org.example.satellitescheduling.domain.Task;
import org.example.satellitescheduling.domain.TaskSchedule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.optaplanner.core.api.solver.SolverStatus;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class TaskScheduleResourceTest {

    @Inject
    TaskScheduleResource taskScheduleResource;

    @Test
    @Timeout(600_000)
    public void solveDemoDataUntilFeasible() throws InterruptedException {
        taskScheduleResource.solve();
        TaskSchedule taskSchedule = taskScheduleResource.getSchedule();
        while (taskSchedule.getSolverStatus() != SolverStatus.NOT_SOLVING) {
            // Quick polling (not a Test Thread Sleep anti-pattern)
            // Test is still fast on fast machines and doesn't randomly fail on slow machines.
            Thread.sleep(20L);
            taskSchedule = taskScheduleResource.getSchedule();
        }
        assertFalse(taskSchedule.getTaskList().isEmpty());
        for (Task task : taskSchedule.getTaskList()) {
            assertNotNull(task.getStartingTimeGrain());
            assertNotNull(task.getVisibility());
        }
        assertTrue(taskSchedule.getScore().isFeasible());
    }
}
