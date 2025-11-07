package com.kishen.weather.domain.valueobject;

/**
 * Location Value Object
 *
 * Represents a geographical location with city, country, and coordinates.
 *
 * @param city      The city name
 * @param country   The country code (ISO 3166-1 alpha-2)
 * @param latitude  The latitude coordinate
 * @param longitude The longitude coordinate
 * @author Kishen Sivalingam
 */
public record Location(String city, String country, Double latitude, Double longitude) {

    public Location {
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City cannot be null or blank");
        }
        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException("Country cannot be null or blank");
        }
        if (latitude == null || latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }
        if (longitude == null || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }
    }

    public String getFullLocation() {
        return String.format("%s, %s", city, country);
    }
}
