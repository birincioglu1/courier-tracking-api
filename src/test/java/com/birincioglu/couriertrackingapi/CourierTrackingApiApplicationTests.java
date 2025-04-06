package com.birincioglu.couriertrackingapi;

import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest

class CourierTrackingApiApplicationTests {

    void contextLoads() {
        assertThat(CourierTrackingApiApplicationTests.class).isNotNull();
    }
}
