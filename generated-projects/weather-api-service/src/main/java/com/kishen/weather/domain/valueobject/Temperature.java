package com.kishen.weather.domain.valueobject;

import java.math.BigDecimal;

/**
 * Temperature Value Object
 *
 * Represents a temperature measurement with its unit.
 * Value objects are immutable and define equality by their attributes.
 *
 * @param value The temperature value
 * @param unit  The temperature unit (CELSIUS, FAHRENHEIT, KELVIN)
 * @author Kishen Sivalingam
 */
public record Temperature(BigDecimal value, TemperatureUnit unit) {

    public Temperature {
        if (value == null) {
            throw new IllegalArgumentException("Temperature value cannot be null");
        }
        if (unit == null) {
            throw new IllegalArgumentException("Temperature unit cannot be null");
        }
        validateTemperature(value, unit);
    }

    private void validateTemperature(BigDecimal value, TemperatureUnit unit) {
        BigDecimal absoluteZero = switch (unit) {
            case CELSIUS -> BigDecimal.valueOf(-273.15);
            case FAHRENHEIT -> BigDecimal.valueOf(-459.67);
            case KELVIN -> BigDecimal.ZERO;
        };

        if (value.compareTo(absoluteZero) < 0) {
            throw new IllegalArgumentException(
                    String.format("Temperature cannot be below absolute zero (%s %s)",
                            absoluteZero, unit));
        }
    }

    public Temperature convertTo(TemperatureUnit targetUnit) {
        if (unit == targetUnit) {
            return this;
        }

        BigDecimal celsius = toCelsius();
        BigDecimal convertedValue = switch (targetUnit) {
            case CELSIUS -> celsius;
            case FAHRENHEIT -> celsius.multiply(BigDecimal.valueOf(9.0 / 5.0)).add(BigDecimal.valueOf(32));
            case KELVIN -> celsius.add(BigDecimal.valueOf(273.15));
        };

        return new Temperature(convertedValue, targetUnit);
    }

    private BigDecimal toCelsius() {
        return switch (unit) {
            case CELSIUS -> value;
            case FAHRENHEIT -> value.subtract(BigDecimal.valueOf(32)).multiply(BigDecimal.valueOf(5.0 / 9.0));
            case KELVIN -> value.subtract(BigDecimal.valueOf(273.15));
        };
    }

    public enum TemperatureUnit {
        CELSIUS, FAHRENHEIT, KELVIN
    }
}
