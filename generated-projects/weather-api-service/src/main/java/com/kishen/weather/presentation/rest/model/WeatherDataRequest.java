package com.kishen.weather.presentation.rest.model;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * WeatherDataRequest DTO
 *
 * Request DTO for creating new weather data.
 * Contains validation annotations to ensure data integrity.
 *
 * @author Kishen Sivalingam
 */
public record WeatherDataRequest(
        @NotBlank(message = "City cannot be blank")
        @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters")
        String city,

        @NotBlank(message = "Country cannot be blank")
        @Size(min = 2, max = 2, message = "Country must be a 2-character ISO code")
        String country,

        @NotNull(message = "Latitude cannot be null")
        @Min(value = -90, message = "Latitude must be between -90 and 90")
        @Max(value = 90, message = "Latitude must be between -90 and 90")
        Double latitude,

        @NotNull(message = "Longitude cannot be null")
        @Min(value = -180, message = "Longitude must be between -180 and 180")
        @Max(value = 180, message = "Longitude must be between -180 and 180")
        Double longitude,

        @NotNull(message = "Temperature value cannot be null")
        BigDecimal temperatureValue,

        @NotBlank(message = "Temperature unit cannot be blank")
        @Pattern(regexp = "CELSIUS|FAHRENHEIT|KELVIN", message = "Temperature unit must be CELSIUS, FAHRENHEIT, or KELVIN")
        String temperatureUnit,

        @NotNull(message = "Humidity cannot be null")
        @Min(value = 0, message = "Humidity must be between 0 and 100")
        @Max(value = 100, message = "Humidity must be between 0 and 100")
        Integer humidity,

        String description,

        @NotNull(message = "Wind speed cannot be null")
        @Min(value = 0, message = "Wind speed cannot be negative")
        Double windSpeed
) {
}
