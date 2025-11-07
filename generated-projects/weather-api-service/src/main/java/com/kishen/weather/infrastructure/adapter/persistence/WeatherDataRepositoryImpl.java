package com.kishen.weather.infrastructure.adapter.persistence;

import com.kishen.weather.domain.entity.WeatherData;
import com.kishen.weather.domain.port.WeatherDataRepository;
import com.kishen.weather.domain.valueobject.Location;
import com.kishen.weather.domain.valueobject.Temperature;
import com.kishen.weather.infrastructure.adapter.persistence.entity.WeatherDataJpaEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * WeatherDataRepository Implementation
 *
 * This adapter implements the domain repository interface using Spring Data JPA.
 * It translates between domain entities and JPA entities.
 *
 * This follows the Adapter pattern:
 * - Domain defines the interface (port)
 * - Infrastructure provides the implementation (adapter)
 *
 * @author Kishen Sivalingam
 */
@Repository
public class WeatherDataRepositoryImpl implements WeatherDataRepository {

    private final WeatherDataJpaRepository jpaRepository;

    public WeatherDataRepositoryImpl(WeatherDataJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public WeatherData save(WeatherData weatherData) {
        WeatherDataJpaEntity jpaEntity = toJpaEntity(weatherData);
        WeatherDataJpaEntity saved = jpaRepository.save(jpaEntity);
        return toDomainEntity(saved);
    }

    @Override
    public Optional<WeatherData> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(this::toDomainEntity);
    }

    @Override
    public List<WeatherData> findByCity(String city) {
        return jpaRepository.findByCity(city).stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<WeatherData> findLatestByCity(String city) {
        return jpaRepository.findLatestByCity(city)
                .map(this::toDomainEntity);
    }

    @Override
    public List<WeatherData> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByCity(String city) {
        return jpaRepository.existsByCity(city);
    }

    // Mapping: Domain Entity -> JPA Entity
    private WeatherDataJpaEntity toJpaEntity(WeatherData domain) {
        WeatherDataJpaEntity jpa = new WeatherDataJpaEntity();
        jpa.setId(domain.getId());
        jpa.setCity(domain.getLocation().city());
        jpa.setCountry(domain.getLocation().country());
        jpa.setLatitude(domain.getLocation().latitude());
        jpa.setLongitude(domain.getLocation().longitude());
        jpa.setTemperatureValue(domain.getTemperature().value());
        jpa.setTemperatureUnit(mapTemperatureUnit(domain.getTemperature().unit()));
        jpa.setHumidity(domain.getHumidity());
        jpa.setDescription(domain.getDescription());
        jpa.setWindSpeed(domain.getWindSpeed());
        jpa.setRecordedAt(domain.getRecordedAt());
        jpa.setUpdatedAt(domain.getUpdatedAt());
        return jpa;
    }

    // Mapping: JPA Entity -> Domain Entity
    private WeatherData toDomainEntity(WeatherDataJpaEntity jpa) {
        Location location = new Location(
                jpa.getCity(),
                jpa.getCountry(),
                jpa.getLatitude(),
                jpa.getLongitude()
        );

        Temperature temperature = new Temperature(
                jpa.getTemperatureValue(),
                mapTemperatureUnit(jpa.getTemperatureUnit())
        );

        WeatherData domain = new WeatherData(
                location,
                temperature,
                jpa.getHumidity(),
                jpa.getDescription(),
                jpa.getWindSpeed()
        );

        domain.setId(jpa.getId());
        domain.setRecordedAt(jpa.getRecordedAt());
        domain.setUpdatedAt(jpa.getUpdatedAt());

        return domain;
    }

    private WeatherDataJpaEntity.TemperatureUnit mapTemperatureUnit(Temperature.TemperatureUnit domainUnit) {
        return WeatherDataJpaEntity.TemperatureUnit.valueOf(domainUnit.name());
    }

    private Temperature.TemperatureUnit mapTemperatureUnit(WeatherDataJpaEntity.TemperatureUnit jpaUnit) {
        return Temperature.TemperatureUnit.valueOf(jpaUnit.name());
    }
}
