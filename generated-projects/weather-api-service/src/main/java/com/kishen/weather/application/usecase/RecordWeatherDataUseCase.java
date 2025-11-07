package com.kishen.weather.application.usecase;

import com.kishen.weather.domain.entity.WeatherData;

/**
 * RecordWeatherDataUseCase Interface
 *
 * This use case defines the contract for recording new weather data.
 * Use cases represent the application's business operations.
 *
 * @author Kishen Sivalingam
 */
public interface RecordWeatherDataUseCase {

    /**
     * Execute the use case to record new weather data
     *
     * @param input The input containing weather data details
     * @return The recorded weather data
     */
    WeatherData execute(RecordWeatherDataInput input);
}
