package com.kishen.weather.presentation.rest;

import com.kishen.weather.application.usecase.GetWeatherDataUseCase;
import com.kishen.weather.application.usecase.RecordWeatherDataInput;
import com.kishen.weather.application.usecase.RecordWeatherDataUseCase;
import com.kishen.weather.domain.entity.WeatherData;
import com.kishen.weather.presentation.rest.exception.WeatherDataNotFoundException;
import com.kishen.weather.presentation.rest.model.WeatherDataRequest;
import com.kishen.weather.presentation.rest.model.WeatherDataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * WeatherDataController
 *
 * REST API controller for weather data operations.
 *
 * This is the presentation layer that:
 * - Handles HTTP requests/responses
 * - Validates input
 * - Maps between DTOs and domain objects
 * - Delegates to use cases
 *
 * @author Kishen Sivalingam
 */
@RestController
@RequestMapping("/api/v1/weather")
@Tag(name = "Weather Data", description = "Weather data management API")
public class WeatherDataController {

    private final RecordWeatherDataUseCase recordWeatherDataUseCase;
    private final GetWeatherDataUseCase getWeatherDataUseCase;

    public WeatherDataController(RecordWeatherDataUseCase recordWeatherDataUseCase,
                                 GetWeatherDataUseCase getWeatherDataUseCase) {
        this.recordWeatherDataUseCase = recordWeatherDataUseCase;
        this.getWeatherDataUseCase = getWeatherDataUseCase;
    }

    @PostMapping
    @Operation(summary = "Record new weather data", description = "Creates a new weather data record for a location")
    public ResponseEntity<WeatherDataResponse> recordWeatherData(
            @Valid @RequestBody WeatherDataRequest request) {

        RecordWeatherDataInput input = new RecordWeatherDataInput(
                request.city(),
                request.country(),
                request.latitude(),
                request.longitude(),
                request.temperatureValue(),
                request.temperatureUnit(),
                request.humidity(),
                request.description(),
                request.windSpeed()
        );

        WeatherData weatherData = recordWeatherDataUseCase.execute(input);
        WeatherDataResponse response = WeatherDataResponse.fromDomain(weatherData);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get weather data by ID", description = "Retrieves weather data by its unique identifier")
    public ResponseEntity<WeatherDataResponse> getWeatherDataById(@PathVariable UUID id) {
        WeatherData weatherData = getWeatherDataUseCase.getById(id)
                .orElseThrow(() -> new WeatherDataNotFoundException(id));

        WeatherDataResponse response = WeatherDataResponse.fromDomain(weatherData);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/city/{city}")
    @Operation(summary = "Get latest weather data for a city", description = "Retrieves the most recent weather data for a specific city")
    public ResponseEntity<WeatherDataResponse> getLatestWeatherDataByCity(@PathVariable String city) {
        WeatherData weatherData = getWeatherDataUseCase.getLatestByCity(city)
                .orElseThrow(() -> new WeatherDataNotFoundException(city));

        WeatherDataResponse response = WeatherDataResponse.fromDomain(weatherData);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Simple health check endpoint")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Weather API Service is running");
    }
}
