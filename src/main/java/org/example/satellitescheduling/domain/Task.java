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

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import javax.persistence.*;

@Entity
@PlanningEntity
public class Task {

    @Id
    @PlanningId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String requiredSatellite;
    private String requiredStation;

    @ManyToOne
    private TaskType type;

    private int earlierStartTime;
    private int latestStartTime;
    private int preferedStartTime;
    private int duration;

    // TODO: Add configuration option for how long each TimeGrain is
    @PlanningVariable(valueRangeProviderRefs = "timeGrainRange")
    @ManyToOne
    private TimeGrain startingTimeGrain;

    @PlanningVariable(valueRangeProviderRefs = "assignedVisibilityRange")
    @ManyToOne
    private Visibility visibility;

    public TimeGrain getStartingTimeGrain() {
        return startingTimeGrain;
    }

    public void setStartingTimeGrain(TimeGrain startingTimeGrain) {
        this.startingTimeGrain = startingTimeGrain;
    }

//    public int getMyGrain() {
//        return (startingTimeGrain == null ? startingTimeGrain.getGrainIndex() : 0);
//    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public Task() {
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                '}';
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Task(String requiredSatellite, String requiredStation, TaskType type, int earlierStartTime, int latestStartTime, int preferedStartTime, int duration, TimeGrain startingTimeGrain, Visibility visibility) {
        this.requiredSatellite = requiredSatellite;
        this.requiredStation = requiredStation;
        this.type = type;
        this.earlierStartTime = earlierStartTime;
        this.latestStartTime = latestStartTime;
        this.preferedStartTime = preferedStartTime;
        this.duration = duration;
        this.startingTimeGrain = startingTimeGrain;
        this.visibility = visibility;
    }

    public int getEarlierStartTime() {
        return earlierStartTime;
    }

    public int overlappedUnits(Task other) {
        if (startingTimeGrain == null || other.getStartingTimeGrain() == null) {
            return 0;
        }
        int start = startingTimeGrain.getGrainIndex();
        int end = start + this.getDuration();
        int otherStart = other.getStartingTimeGrain().getGrainIndex();
        int otherEnd = otherStart + other.getDuration();

        if (end < otherStart) {
            return 0;
        } else if (otherEnd < start) {
            return 0;
        }
        return Math.min(end, otherEnd) - Math.max(start, otherStart);
    }

    public int distanceUnits(Task other) {
        if (startingTimeGrain == null || other.getStartingTimeGrain() == null) {
            return 0;
        }
        int start = startingTimeGrain.getGrainIndex();
        int otherStart = other.getStartingTimeGrain().getGrainIndex();

        return Math.abs(start - otherStart);
    }

    public boolean between(Task previous, Task next) {
        if ((previous.startingTimeGrain.getGrainIndex() < this.getStartingTimeGrain().getGrainIndex()) &&
           (next.startingTimeGrain.getGrainIndex() > this.getStartingTimeGrain().getGrainIndex())) {
            return true;
        }
        return false;
    }

    public void setEarlierStartTime(int earlierStartTime) {
        this.earlierStartTime = earlierStartTime;
    }

    public int getLatestStartTime() {
        return latestStartTime;
    }

    public void setLatestStartTime(int latestStartTime) {
        this.latestStartTime = latestStartTime;
    }

    public int getPreferedStartTime() {
        return preferedStartTime;
    }

    public void setPreferedStartTime(int preferedStartTime) {
        this.preferedStartTime = preferedStartTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getRequiredSatellite() {
        return requiredSatellite;
    }

    public void setRequiredSatellite(String satellite) {
        this.requiredSatellite = satellite;
    }

    public String getRequiredStation() {
        return requiredStation;
    }

    public void setRequiredStation(String station) {
        this.requiredStation = station;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }
}
