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

import io.quarkus.panache.common.Sort;
import org.example.satellitescheduling.domain.Task;
import org.example.satellitescheduling.domain.TaskSchedule;
import org.example.satellitescheduling.persistence.TaskRepository;
import org.example.satellitescheduling.persistence.VisibilityRepository;
import org.example.satellitescheduling.persistence.TimeGrainRepository;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.api.solver.SolverStatus;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/schedule")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TaskScheduleResource {

    public static final Long SINGLETON_SCHEDULE_ID = 1L;

    @Inject
    TaskRepository taskRepository;
    @Inject
    VisibilityRepository visibilityRepository;
    @Inject
    TimeGrainRepository timeGrainRepository;

    @Inject
    SolverManager<TaskSchedule, Long> solverManager;
    @Inject
    ScoreManager<TaskSchedule, HardSoftScore> scoreManager;

    // To try, open http://localhost:8080/schedule
    @GET
    public TaskSchedule getSchedule() {
        // Get the solver status before loading the solution
        // to avoid the race condition that the solver terminates between them
        SolverStatus solverStatus = getSolverStatus();
        TaskSchedule solution = findById(SINGLETON_SCHEDULE_ID);
        scoreManager.updateScore(solution); // Sets the score
        solution.setSolverStatus(solverStatus);
        return solution;
    }

    public SolverStatus getSolverStatus() {
        return solverManager.getSolverStatus(SINGLETON_SCHEDULE_ID);
    }

    @POST
    @Path("solve")
    public void solve() {
        solverManager.solveAndListen(SINGLETON_SCHEDULE_ID,
                this::findById,
                this::save);
    }

    @POST
    @Path("stopSolving")
    public void stopSolving() {
        solverManager.terminateEarly(SINGLETON_SCHEDULE_ID);
    }

    @Transactional
    protected TaskSchedule findById(Long id) {
        if (!SINGLETON_SCHEDULE_ID.equals(id)) {
            throw new IllegalStateException("There is no schedule with id (" + id + ").");
        }

        return new TaskSchedule(
                taskRepository.listAll(Sort.by("id")),
                visibilityRepository.listAll(Sort.by("id")),
                timeGrainRepository.listAll(Sort.by("grainIndex").and("id"))
        );
    }

    @Transactional
    protected void save(TaskSchedule schedule) {
        for (Task task : schedule.getTaskList()) {
            // TODO this is awfully naive: optimistic locking causes issues if called by the SolverManager
            Task persistedTask = taskRepository.findById(task.getId());
            persistedTask.setStartingTimeGrain(task.getStartingTimeGrain());
            persistedTask.setVisibility(task.getVisibility());
        }
    }
}
