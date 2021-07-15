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

package org.example.satellitescheduling.bootstrap;

import io.quarkus.runtime.StartupEvent;
import org.example.satellitescheduling.domain.*;
import org.example.satellitescheduling.persistence.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class DemoDataGenerator {

    @ConfigProperty(name = "schedule.demoData", defaultValue = "SMALL")
    public DemoData demoData;

    public enum DemoData {
        NONE,
        SMALL,
        LARGE
    }

    @Inject
    TaskRepository taskRepository;
    @Inject
    TaskTypeRepository taskTypeRepository;
    @Inject
    VisibilityRepository visibilityRepository;
    @Inject
    StationSatelliteRepository stationSatelliteRepository;
    @Inject
    TimeGrainRepository timeGrainRepository;

    @Transactional
    public void generateDemoData(@Observes StartupEvent startupEvent) {
        if (demoData == DemoData.NONE) {
            return;
        }
        TaskType routine = new TaskType("R");
        TaskType NoRoutine = new TaskType("NR");

        taskTypeRepository.persist(routine);
        taskTypeRepository.persist(NoRoutine);

        List<Task> taskList = new ArrayList<>();
        taskList.add(new Task(null, null, NoRoutine, 0,10,0,1,null,null));
        taskList.add(new Task(null, null, routine, 0,10,0,2,null,null));
        taskList.add(new Task(null, null, NoRoutine, 0,10,0,2,null,null));
        taskList.add(new Task(null, null, routine, 0,10,0,1,null,null));

        taskList.add(new Task(null, null, routine,12,40,0,1,null,null));
        taskList.add(new Task(null, null, NoRoutine,12,40,0,5,null,null));
        taskList.add(new Task(null, null, routine,12,40,0,2,null,null));

        taskList.add(new Task("S2", null, NoRoutine,15,40,0,1,null,null));
        taskRepository.persist(taskList);

        List<StationSatellite> stationSatelliteList = new ArrayList<>();
        List<Visibility> visibilityList = new ArrayList<>();

        StationSatellite stationSatellite1 = new StationSatellite("S1","E1");
        Visibility visibility1_1 = new Visibility(2,10, stationSatellite1);

        stationSatelliteList.add(stationSatellite1);
        visibilityList.add(visibility1_1);

        StationSatellite stationSatellite2 = new StationSatellite("S1","E2");
        Visibility visibility2_1 = new Visibility(11,20, stationSatellite2);
        stationSatelliteList.add(stationSatellite2);
        visibilityList.add(visibility2_1);

        StationSatellite stationSatellite3 = new StationSatellite("S2","E3");
        Visibility visibility3_1 = new Visibility(21,22, stationSatellite3);
        stationSatelliteList.add(stationSatellite3);
        visibilityList.add(visibility3_1);

        visibilityRepository.persist(visibilityList);
        stationSatelliteRepository.persist(stationSatelliteList);

        List<TimeGrain> timeGrainList = new ArrayList<>();
        for (int i = 0; i <= 48; i++) {
            timeGrainList.add(new TimeGrain(i));
        }
        if (demoData == DemoData.LARGE) {
            for (int i = 49; i <= 96; i++) {
                timeGrainList.add(new TimeGrain(i));
            }
        }
        timeGrainRepository.persist(timeGrainList);
    }
}
