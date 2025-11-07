package com.kishen.weather.application.service;

import com.kishen.weather.application.usecase.RecordWeatherDataInput;
import com.kishen.weather.application.usecase.RecordWeatherDataUseCase;
import com.kishen.weather.domain.entity.WeatherData;
import com.kishen.weather.domain.port.WeatherDataRepository;
import com.kishen.weather.domain.valueobject.Location;
import com.kishen.weather.domain.valueobject.Temperature;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * RecordWeatherDataService
 *
 * This service implements the RecordWeatherDataUseCase.
 * It orchestrates the domain logic and repository operations.
 *
 * Application services:
 * - Implement use cases
 * - Orchestrate domain objects
 * - Manage transactions
 * - Do not contain business logic (that's in domain entities)
 *
 * @author Kishen Sivalingam
 */
@Service
public class RecordWeatherDataService implements RecordWeatherDataUseCase {

    private final WeatherDataRepository repository;

    public RecordWeatherDataService(WeatherDataRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public WeatherData execute(RecordWeatherDataInput input) {
        // Create value objects
        Location location = new Location(
                input.city(),
                input.country(),
                input.latitude(),
                input.longitude()
        );

        Temperature temperature = new Temperature(
                input.temperatureValue(),
                Temperature.TemperatureUnit.valueOf(input.temperatureUnit())
        );

        // Create domain entity
        WeatherData weatherData = new WeatherData(
                location,
                temperature,
                input.humidity(),
                input.description(),
                input.windSpeed()
        );

        // Persist
        return repository.save(weatherData);
    }
}
