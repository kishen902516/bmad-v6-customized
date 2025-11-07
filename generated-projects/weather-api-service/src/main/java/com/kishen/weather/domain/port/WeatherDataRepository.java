package com.kishen.weather.domain.port;

import com.kishen.weather.domain.entity.WeatherData;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * WeatherDataRepository Port (Interface)
 *
 * This is a domain interface that defines the contract for weather data persistence.
 * The implementation resides in the infrastructure layer.
 *
 * This follows the Dependency Inversion Principle:
 * - Domain defines what it needs (port)
 * - Infrastructure provides how it's done (adapter)
 *
 * @author Kishen Sivalingam
 */
public interface WeatherDataRepository {

    /**
     * Save or update weather data
     *
     * @param weatherData The weather data to save
     * @return The saved weather data
     */
    WeatherData save(WeatherData weatherData);

    /**
     * Find weather data by ID
     *
     * @param id The weather data ID
     * @return Optional containing the weather data if found
     */
    Optional<WeatherData> findById(UUID id);

    /**
     * Find all weather data for a specific city
     *
     * @param city The city name
     * @return List of weather data for the city
     */
    List<WeatherData> findByCity(String city);

    /**
     * Find the latest weather data for a specific city
     *
     * @param city The city name
     * @return Optional containing the latest weather data if found
     */
    Optional<WeatherData> findLatestByCity(String city);

    /**
     * Find all weather data
     *
     * @return List of all weather data
     */
    List<WeatherData> findAll();

    /**
     * Delete weather data by ID
     *
     * @param id The weather data ID
     */
    void deleteById(UUID id);

    /**
     * Check if weather data exists for a city
     *
     * @param city The city name
     * @return true if data exists, false otherwise
     */
    boolean existsByCity(String city);
}
