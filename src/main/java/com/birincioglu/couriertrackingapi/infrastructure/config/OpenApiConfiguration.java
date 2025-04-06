package com.birincioglu.couriertrackingapi.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        SwaggerProperties.class
})
@RequiredArgsConstructor
public class OpenApiConfiguration {

    private final SwaggerProperties swaggerProperties;

    @Bean
    public OpenAPI openApiInfo() {
        return new OpenAPI()
                .info(info());

    }

    private Info info() {
        return new Info()
                .description(swaggerProperties.getInfo().getDescription())
                .title(swaggerProperties.getInfo().getTitle())
                .version(swaggerProperties.getInfo().getVersion());
    }

}
