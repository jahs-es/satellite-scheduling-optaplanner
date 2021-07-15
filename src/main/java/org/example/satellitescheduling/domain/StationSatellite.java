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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class StationSatellite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String satellite;
    private String station;

    public StationSatellite() {
    }

    public StationSatellite(String satellite, String station) {
        this.satellite = satellite;
        this.station = station;
    }

    @Override
    public String toString() {
        return "StationSatellite {" +
                "id=" + id +
                '}';
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************
//    public boolean canAffordTask(Task task) {
//        List<Visibility> visibilityFound = this.vibilities.stream()
//                .filter(it -> it.canFitTask(task.getStartingTimeGrain().getGrainIndex(), task.getDuration()))
//                .collect(Collectors.toList());
//
//        return (visibilityFound.size() > 0);
//    }
//
//    public void addToVisibilities(Visibility visibility) {
//        this.vibilities.add(visibility);
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSatellite() {
        return satellite;
    }

    public void setSatellite(String satellite) {
        this.satellite = satellite;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }
}
