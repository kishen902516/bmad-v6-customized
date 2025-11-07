package com.kishen.weather.presentation.rest.exception;

import java.util.UUID;

/**
 * WeatherDataNotFoundException
 *
 * Exception thrown when weather data is not found.
 *
 * @author Kishen Sivalingam
 */
public class WeatherDataNotFoundException extends RuntimeException {

    public WeatherDataNotFoundException(UUID id) {
        super("Weather data not found with ID: " + id);
    }

    public WeatherDataNotFoundException(String city) {
        super("Weather data not found for city: " + city);
    }
}
