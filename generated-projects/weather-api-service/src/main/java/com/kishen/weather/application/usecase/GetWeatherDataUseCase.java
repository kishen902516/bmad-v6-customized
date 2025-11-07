package com.kishen.weather.application.usecase;

import com.kishen.weather.domain.entity.WeatherData;

import java.util.Optional;
import java.util.UUID;

/**
 * GetWeatherDataUseCase Interface
 *
 * This use case defines the contract for retrieving weather data.
 *
 * @author Kishen Sivalingam
 */
public interface GetWeatherDataUseCase {

    /**
     * Get weather data by ID
     *
     * @param id The weather data ID
     * @return Optional containing the weather data if found
     */
    Optional<WeatherData> getById(UUID id);

    /**
     * Get the latest weather data for a city
     *
     * @param city The city name
     * @return Optional containing the latest weather data if found
     */
    Optional<WeatherData> getLatestByCity(String city);
}
