package com.birincioglu.couriertrackingapi.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("swagger")
public class SwaggerProperties {

    private String apiPath;

    private boolean corsEnabled;

    private Info info;

    @Data
    static class Info {

        private String title;

        private String description;

        private String version;

    }

}