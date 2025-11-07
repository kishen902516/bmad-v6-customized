package com.kishen.weather.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Temperature value object.
 *
 * @author Kishen Sivalingam
 */
@DisplayName("Temperature Value Object Tests")
class TemperatureTest {

    @Test
    @DisplayName("Should create valid temperature")
    void shouldCreateValidTemperature() {
        // Act
        Temperature temp = new Temperature(BigDecimal.valueOf(20), Temperature.TemperatureUnit.CELSIUS);

        // Assert
        assertEquals(BigDecimal.valueOf(20), temp.value());
        assertEquals(Temperature.TemperatureUnit.CELSIUS, temp.unit());
    }

    @Test
    @DisplayName("Should reject null value")
    void shouldRejectNullValue() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                new Temperature(null, Temperature.TemperatureUnit.CELSIUS)
        );
    }

    @Test
    @DisplayName("Should reject null unit")
    void shouldRejectNullUnit() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                new Temperature(BigDecimal.valueOf(20), null)
        );
    }

    @Test
    @DisplayName("Should reject temperature below absolute zero - Celsius")
    void shouldRejectBelowAbsoluteZeroCelsius() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                new Temperature(BigDecimal.valueOf(-300), Temperature.TemperatureUnit.CELSIUS)
        );
    }

    @Test
    @DisplayName("Should accept temperature at absolute zero - Kelvin")
    void shouldAcceptAbsoluteZeroKelvin() {
        // Act
        Temperature temp = new Temperature(BigDecimal.ZERO, Temperature.TemperatureUnit.KELVIN);

        // Assert
        assertEquals(BigDecimal.ZERO, temp.value());
    }

    @Test
    @DisplayName("Should convert Celsius to Fahrenheit")
    void shouldConvertCelsiusToFahrenheit() {
        // Arrange
        Temperature celsius = new Temperature(BigDecimal.ZERO, Temperature.TemperatureUnit.CELSIUS);

        // Act
        Temperature fahrenheit = celsius.convertTo(Temperature.TemperatureUnit.FAHRENHEIT);

        // Assert
        assertEquals(Temperature.TemperatureUnit.FAHRENHEIT, fahrenheit.unit());
        assertEquals(0, BigDecimal.valueOf(32).compareTo(fahrenheit.value()));
    }

    @Test
    @DisplayName("Should convert Celsius to Kelvin")
    void shouldConvertCelsiusToKelvin() {
        // Arrange
        Temperature celsius = new Temperature(BigDecimal.ZERO, Temperature.TemperatureUnit.CELSIUS);

        // Act
        Temperature kelvin = celsius.convertTo(Temperature.TemperatureUnit.KELVIN);

        // Assert
        assertEquals(Temperature.TemperatureUnit.KELVIN, kelvin.unit());
        assertTrue(kelvin.value().compareTo(BigDecimal.valueOf(273)) > 0);
    }

    @Test
    @DisplayName("Should return same temperature when converting to same unit")
    void shouldReturnSameWhenConvertingToSameUnit() {
        // Arrange
        Temperature celsius = new Temperature(BigDecimal.valueOf(20), Temperature.TemperatureUnit.CELSIUS);

        // Act
        Temperature converted = celsius.convertTo(Temperature.TemperatureUnit.CELSIUS);

        // Assert
        assertEquals(celsius, converted);
    }
}
