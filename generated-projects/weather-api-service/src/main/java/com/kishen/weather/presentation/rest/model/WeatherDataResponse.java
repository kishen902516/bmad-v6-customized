package com.kishen.weather.presentation.rest.model;

import com.kishen.weather.domain.entity.WeatherData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * WeatherDataResponse DTO
 *
 * Response DTO for weather data.
 * Maps from domain entity to presentation layer.
 *
 * @author Kishen Sivalingam
 */
public record WeatherDataResponse(
        UUID id,
        String city,
        String country,
        Double latitude,
        Double longitude,
        BigDecimal temperatureValue,
        String temperatureUnit,
        Integer humidity,
        String description,
        Double windSpeed,
        String severity,
        boolean stale,
        LocalDateTime recordedAt,
        LocalDateTime updatedAt
) {
    public static WeatherDataResponse fromDomain(WeatherData weatherData) {
        return new WeatherDataResponse(
                weatherData.getId(),
                weatherData.getLocation().city(),
                weatherData.getLocation().country(),
                weatherData.getLocation().latitude(),
                weatherData.getLocation().longitude(),
                weatherData.getTemperature().value(),
                weatherData.getTemperature().unit().name(),
                weatherData.getHumidity(),
                weatherData.getDescription(),
                weatherData.getWindSpeed(),
                weatherData.getSeverity().name(),
                weatherData.isStale(),
                weatherData.getRecordedAt(),
                weatherData.getUpdatedAt()
        );
    }
}
