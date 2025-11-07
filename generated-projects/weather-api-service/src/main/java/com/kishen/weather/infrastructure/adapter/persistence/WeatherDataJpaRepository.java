package com.kishen.weather.infrastructure.adapter.persistence;

import com.kishen.weather.infrastructure.adapter.persistence.entity.WeatherDataJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for WeatherData
 *
 * This is the Spring Data JPA interface that provides database operations.
 * Spring Data JPA automatically provides implementation at runtime.
 *
 * @author Kishen Sivalingam
 */
public interface WeatherDataJpaRepository extends JpaRepository<WeatherDataJpaEntity, UUID> {

    /**
     * Find all weather data for a specific city
     *
     * @param city The city name
     * @return List of weather data JPA entities
     */
    List<WeatherDataJpaEntity> findByCity(String city);

    /**
     * Find the latest weather data for a specific city
     *
     * @param city The city name
     * @return Optional containing the latest weather data if found
     */
    @Query("SELECT w FROM WeatherDataJpaEntity w WHERE w.city = :city ORDER BY w.recordedAt DESC LIMIT 1")
    Optional<WeatherDataJpaEntity> findLatestByCity(@Param("city") String city);

    /**
     * Check if weather data exists for a city
     *
     * @param city The city name
     * @return true if data exists, false otherwise
     */
    boolean existsByCity(String city);
}
