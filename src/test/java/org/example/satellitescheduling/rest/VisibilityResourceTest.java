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

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class VisibilityResourceTest {

//    @Test
//    public void getAll() {
//        List<Visibility> crewList = given()
//                .when().get("/crews")
//                .then()
//                .statusCode(200)
//                .extract().body().jsonPath().getList(".", Visibility.class);
//        assertFalse(crewList.isEmpty());
//        Visibility firstCrew = crewList.get(0);
//        assertNotNull(firstCrew.getCrewName());
//    }

//    @Test
//    public void addAndRemove() {
//        Visibility crew = given()
//                .when()
//                .contentType(ContentType.JSON)
//                .body(new Visibility("Test crew"))
//                .post("/crews")
//                .then()
//                .statusCode(201)
//                .extract().as(Visibility.class);
//
//        given()
//                .when()
//                .delete("/crews/{id}", crew.getId())
//                .then()
//                .statusCode(204);
//    }
}
