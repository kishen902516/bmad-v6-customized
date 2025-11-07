package com.kishen.weather.domain.entity;

import com.kishen.weather.domain.valueobject.Location;
import com.kishen.weather.domain.valueobject.Temperature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for WeatherData domain entity.
 *
 * Tests business logic and validations.
 *
 * @author Kishen Sivalingam
 */
@DisplayName("WeatherData Domain Entity Tests")
class WeatherDataTest {

    @Test
    @DisplayName("Should create valid weather data")
    void shouldCreateValidWeatherData() {
        // Arrange
        Location location = new Location("London", "GB", 51.5074, -0.1278);
        Temperature temperature = new Temperature(BigDecimal.valueOf(20), Temperature.TemperatureUnit.CELSIUS);

        // Act
        WeatherData weatherData = new WeatherData(location, temperature, 75, "Partly cloudy", 15.5);

        // Assert
        assertNotNull(weatherData.getId());
        assertEquals(location, weatherData.getLocation());
        assertEquals(temperature, weatherData.getTemperature());
        assertEquals(75, weatherData.getHumidity());
        assertEquals("Partly cloudy", weatherData.getDescription());
        assertEquals(15.5, weatherData.getWindSpeed());
        assertNotNull(weatherData.getRecordedAt());
        assertNotNull(weatherData.getUpdatedAt());
    }

    @Test
    @DisplayName("Should reject null location")
    void shouldRejectNullLocation() {
        // Arrange
        Temperature temperature = new Temperature(BigDecimal.valueOf(20), Temperature.TemperatureUnit.CELSIUS);

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                new WeatherData(null, temperature, 75, "Partly cloudy", 15.5)
        );
    }

    @Test
    @DisplayName("Should reject null temperature")
    void shouldRejectNullTemperature() {
        // Arrange
        Location location = new Location("London", "GB", 51.5074, -0.1278);

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                new WeatherData(location, null, 75, "Partly cloudy", 15.5)
        );
    }

    @Test
    @DisplayName("Should reject invalid humidity - null")
    void shouldRejectNullHumidity() {
        // Arrange
        Location location = new Location("London", "GB", 51.5074, -0.1278);
        Temperature temperature = new Temperature(BigDecimal.valueOf(20), Temperature.TemperatureUnit.CELSIUS);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                new WeatherData(location, temperature, null, "Partly cloudy", 15.5)
        );
    }

    @Test
    @DisplayName("Should reject invalid humidity - out of range")
    void shouldRejectInvalidHumidity() {
        // Arrange
        Location location = new Location("London", "GB", 51.5074, -0.1278);
        Temperature temperature = new Temperature(BigDecimal.valueOf(20), Temperature.TemperatureUnit.CELSIUS);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                new WeatherData(location, temperature, 150, "Partly cloudy", 15.5)
        );
    }

    @Test
    @DisplayName("Should reject negative wind speed")
    void shouldRejectNegativeWindSpeed() {
        // Arrange
        Location location = new Location("London", "GB", 51.5074, -0.1278);
        Temperature temperature = new Temperature(BigDecimal.valueOf(20), Temperature.TemperatureUnit.CELSIUS);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                new WeatherData(location, temperature, 75, "Partly cloudy", -5.0)
        );
    }

    @Test
    @DisplayName("Should update weather data correctly")
    void shouldUpdateWeatherDataCorrectly() {
        // Arrange
        Location location = new Location("London", "GB", 51.5074, -0.1278);
        Temperature initialTemp = new Temperature(BigDecimal.valueOf(20), Temperature.TemperatureUnit.CELSIUS);
        WeatherData weatherData = new WeatherData(location, initialTemp, 75, "Partly cloudy", 15.5);

        LocalDateTime beforeUpdate = LocalDateTime.now();

        // Act
        Temperature newTemp = new Temperature(BigDecimal.valueOf(25), Temperature.TemperatureUnit.CELSIUS);
        weatherData.updateWeatherData(newTemp, 80, "Sunny", 10.0);

        // Assert
        assertEquals(newTemp, weatherData.getTemperature());
        assertEquals(80, weatherData.getHumidity());
        assertEquals("Sunny", weatherData.getDescription());
        assertEquals(10.0, weatherData.getWindSpeed());
        assertTrue(weatherData.getUpdatedAt().isAfter(beforeUpdate) ||
                weatherData.getUpdatedAt().isEqual(beforeUpdate));
    }

    @Test
    @DisplayName("Should detect stale data correctly")
    void shouldDetectStaleData() {
        // Arrange
        Location location = new Location("London", "GB", 51.5074, -0.1278);
        Temperature temperature = new Temperature(BigDecimal.valueOf(20), Temperature.TemperatureUnit.CELSIUS);
        WeatherData weatherData = new WeatherData(location, temperature, 75, "Partly cloudy", 15.5);

        // Act - set recorded time to 2 hours ago
        weatherData.setRecordedAt(LocalDateTime.now().minusHours(2));

        // Assert
        assertTrue(weatherData.isStale());
    }

    @Test
    @DisplayName("Should detect fresh data correctly")
    void shouldDetectFreshData() {
        // Arrange
        Location location = new Location("London", "GB", 51.5074, -0.1278);
        Temperature temperature = new Temperature(BigDecimal.valueOf(20), Temperature.TemperatureUnit.CELSIUS);

        // Act
        WeatherData weatherData = new WeatherData(location, temperature, 75, "Partly cloudy", 15.5);

        // Assert
        assertFalse(weatherData.isStale());
    }

    @Test
    @DisplayName("Should determine weather severity - NORMAL")
    void shouldDetermineSeverityNormal() {
        // Arrange
        Location location = new Location("London", "GB", 51.5074, -0.1278);
        Temperature temperature = new Temperature(BigDecimal.valueOf(20), Temperature.TemperatureUnit.CELSIUS);
        WeatherData weatherData = new WeatherData(location, temperature, 75, "Partly cloudy", 15.5);

        // Assert
        assertEquals(WeatherData.WeatherSeverity.NORMAL, weatherData.getSeverity());
    }

    @Test
    @DisplayName("Should determine weather severity - MODERATE")
    void shouldDetermineSeverityModerate() {
        // Arrange
        Location location = new Location("London", "GB", 51.5074, -0.1278);
        Temperature temperature = new Temperature(BigDecimal.valueOf(20), Temperature.TemperatureUnit.CELSIUS);
        WeatherData weatherData = new WeatherData(location, temperature, 75, "Windy", 35.0);

        // Assert
        assertEquals(WeatherData.WeatherSeverity.MODERATE, weatherData.getSeverity());
    }

    @Test
    @DisplayName("Should determine weather severity - SEVERE")
    void shouldDetermineSeveritySevere() {
        // Arrange
        Location location = new Location("London", "GB", 51.5074, -0.1278);
        Temperature temperature = new Temperature(BigDecimal.valueOf(20), Temperature.TemperatureUnit.CELSIUS);
        WeatherData weatherData = new WeatherData(location, temperature, 75, "Storm", 55.0);

        // Assert
        assertEquals(WeatherData.WeatherSeverity.SEVERE, weatherData.getSeverity());
    }
}
