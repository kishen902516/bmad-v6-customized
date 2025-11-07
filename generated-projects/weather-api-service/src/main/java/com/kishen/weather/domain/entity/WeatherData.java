package com.kishen.weather.domain.entity;

import com.kishen.weather.domain.valueobject.Location;
import com.kishen.weather.domain.valueobject.Temperature;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * WeatherData Domain Entity
 *
 * Represents weather data for a specific location and time.
 * This is a domain entity containing business logic and validations.
 *
 * Domain entities are independent of persistence frameworks.
 *
 * @author Kishen Sivalingam
 */
public class WeatherData {

    private UUID id;
    private Location location;
    private Temperature temperature;
    private Integer humidity;
    private String description;
    private Double windSpeed;
    private LocalDateTime recordedAt;
    private LocalDateTime updatedAt;

    // Default constructor for JPA
    protected WeatherData() {
    }

    // Constructor for creating new weather data
    public WeatherData(Location location, Temperature temperature, Integer humidity,
                       String description, Double windSpeed) {
        this.id = UUID.randomUUID();
        this.location = Objects.requireNonNull(location, "Location cannot be null");
        this.temperature = Objects.requireNonNull(temperature, "Temperature cannot be null");
        this.humidity = validateHumidity(humidity);
        this.description = description;
        this.windSpeed = validateWindSpeed(windSpeed);
        this.recordedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Business Logic: Update weather data
    public void updateWeatherData(Temperature temperature, Integer humidity,
                                   String description, Double windSpeed) {
        this.temperature = Objects.requireNonNull(temperature, "Temperature cannot be null");
        this.humidity = validateHumidity(humidity);
        this.description = description;
        this.windSpeed = validateWindSpeed(windSpeed);
        this.updatedAt = LocalDateTime.now();
    }

    // Business Logic: Check if data is stale (older than 1 hour)
    public boolean isStale() {
        return recordedAt.isBefore(LocalDateTime.now().minusHours(1));
    }

    // Business Logic: Determine weather condition severity
    public WeatherSeverity getSeverity() {
        if (windSpeed > 50) return WeatherSeverity.SEVERE;
        if (windSpeed > 30) return WeatherSeverity.MODERATE;
        return WeatherSeverity.NORMAL;
    }

    private Integer validateHumidity(Integer humidity) {
        if (humidity == null) {
            throw new IllegalArgumentException("Humidity cannot be null");
        }
        if (humidity < 0 || humidity > 100) {
            throw new IllegalArgumentException("Humidity must be between 0 and 100");
        }
        return humidity;
    }

    private Double validateWindSpeed(Double windSpeed) {
        if (windSpeed == null) {
            throw new IllegalArgumentException("Wind speed cannot be null");
        }
        if (windSpeed < 0) {
            throw new IllegalArgumentException("Wind speed cannot be negative");
        }
        return windSpeed;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public String getDescription() {
        return description;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // For persistence layer reconstruction
    public void setId(UUID id) {
        this.id = id;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WeatherData that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public enum WeatherSeverity {
        NORMAL, MODERATE, SEVERE
    }
}
