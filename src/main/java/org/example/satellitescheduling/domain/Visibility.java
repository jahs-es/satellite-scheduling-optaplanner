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

import org.optaplanner.core.api.domain.lookup.PlanningId;

import javax.persistence.*;

@Entity
public class Visibility {
    @PlanningId
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int startGrainIndex;
    private int stopGrainIndex;

    @ManyToOne
    private StationSatellite stationSatellite;

    public Visibility() {
    }

    public Visibility(int startGrainIndex, int stopGrainIndex, StationSatellite stationSatellite) {
        this.startGrainIndex = startGrainIndex;
        this.stopGrainIndex = stopGrainIndex;
        this.stationSatellite = stationSatellite;
    }

    @Override
    public String toString() {
        return "Visibility {" +
                "id=" + id +
                '}';
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************
    public boolean canFitTask(int taskStartGrainIndex, int taskDuration) {
        return (taskStartGrainIndex >= this.startGrainIndex) && ((taskStartGrainIndex + taskDuration) <= this.stopGrainIndex);
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StationSatellite getStationSatellite() {
        return stationSatellite;
    }

    public void setStationSatellite(StationSatellite stationSatellite) {
        this.stationSatellite = stationSatellite;
    }

    public int getStartGrainIndex() {
        return startGrainIndex;
    }

    public void setStartGrainIndex(int startGrainIndex) {
        this.startGrainIndex = startGrainIndex;
    }

    public int getStopGrainIndex() {
        return stopGrainIndex;
    }

    public void setStopGrainIndex(int stopGrainIndex) {
        this.stopGrainIndex = stopGrainIndex;
    }
}
