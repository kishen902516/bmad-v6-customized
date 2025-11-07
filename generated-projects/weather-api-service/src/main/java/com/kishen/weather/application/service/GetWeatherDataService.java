package com.kishen.weather.application.service;

import com.kishen.weather.application.usecase.GetWeatherDataUseCase;
import com.kishen.weather.domain.entity.WeatherData;
import com.kishen.weather.domain.port.WeatherDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * GetWeatherDataService
 *
 * This service implements the GetWeatherDataUseCase.
 * It retrieves weather data from the repository.
 *
 * @author Kishen Sivalingam
 */
@Service
@Transactional(readOnly = true)
public class GetWeatherDataService implements GetWeatherDataUseCase {

    private final WeatherDataRepository repository;

    public GetWeatherDataService(WeatherDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<WeatherData> getById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Optional<WeatherData> getLatestByCity(String city) {
        return repository.findLatestByCity(city);
    }
}
