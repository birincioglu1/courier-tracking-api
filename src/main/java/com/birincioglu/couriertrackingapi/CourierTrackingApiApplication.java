package com.birincioglu.couriertrackingapi;

import com.birincioglu.couriertrackingapi.infrastructure.config.AppConfig;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@EnableJpaRepositories
@EnableJpaAuditing
@EnableConfigurationProperties(AppConfig.class)
@SpringBootApplication
@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server URL")})
public class CourierTrackingApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourierTrackingApiApplication.class, args);
    }

}
