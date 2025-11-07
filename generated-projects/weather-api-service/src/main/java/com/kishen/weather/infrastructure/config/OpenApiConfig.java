package com.kishen.weather.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger Configuration
 *
 * Configures Springdoc OpenAPI for API documentation.
 *
 * @author Kishen Sivalingam
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI weatherApiServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Weather API Service")
                        .description("Manages weather data with Clean Architecture")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Kishen Sivalingam")
                                .email("kishen@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
