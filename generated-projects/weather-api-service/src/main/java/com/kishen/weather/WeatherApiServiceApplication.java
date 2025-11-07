package com.kishen.weather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Weather API Service - Spring Boot Application
 *
 * This service manages weather data using Clean Architecture principles.
 *
 * Architectural Layers:
 * - Domain: Pure business logic, entities, value objects, and repository interfaces
 * - Application: Use cases and application services that orchestrate domain logic
 * - Infrastructure: Implementation details (database, external APIs, messaging)
 * - Presentation: REST API controllers and DTOs
 *
 * @author Kishen Sivalingam
 * @version 1.0.0
 */
@SpringBootApplication
public class WeatherApiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherApiServiceApplication.class, args);
    }
}
