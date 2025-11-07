package com.kishen.weather.infrastructure.adapter.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * WeatherData JPA Entity
 *
 * This is the persistence model for weather data.
 * It maps to the database table and contains JPA annotations.
 *
 * This is separate from the domain entity to maintain clean architecture:
 * - Domain entities are independent of persistence frameworks
 * - JPA entities handle database mapping
 *
 * @author Kishen Sivalingam
 */
@Entity
@Table(name = "weather_data", indexes = {
        @Index(name = "idx_city", columnList = "city"),
        @Index(name = "idx_recorded_at", columnList = "recorded_at")
})
public class WeatherDataJpaEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "country", nullable = false, length = 2)
    private String country;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "temperature_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal temperatureValue;

    @Column(name = "temperature_unit", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TemperatureUnit temperatureUnit;

    @Column(name = "humidity", nullable = false)
    private Integer humidity;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "wind_speed", nullable = false)
    private Double windSpeed;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Default constructor for JPA
    public WeatherDataJpaEntity() {
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getTemperatureValue() {
        return temperatureValue;
    }

    public void setTemperatureValue(BigDecimal temperatureValue) {
        this.temperatureValue = temperatureValue;
    }

    public TemperatureUnit getTemperatureUnit() {
        return temperatureUnit;
    }

    public void setTemperatureUnit(TemperatureUnit temperatureUnit) {
        this.temperatureUnit = temperatureUnit;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public enum TemperatureUnit {
        CELSIUS, FAHRENHEIT, KELVIN
    }
}
