package com.kishen.weather.application.usecase;

import java.math.BigDecimal;

/**
 * Input DTO for RecordWeatherDataUseCase
 *
 * This is an immutable data transfer object for the use case input.
 *
 * @author Kishen Sivalingam
 */
public record RecordWeatherDataInput(
        String city,
        String country,
        Double latitude,
        Double longitude,
        BigDecimal temperatureValue,
        String temperatureUnit,
        Integer humidity,
        String description,
        Double windSpeed
) {
}
