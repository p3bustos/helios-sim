package com.p3bustos.heliossim.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherData {
    
    private Instant timestamp;
    
    @JsonProperty("temperature_c")
    private double temperatureC;
    
    @JsonProperty("cloud_cover_percent")
    private double cloudCoverPercent;
    
    @JsonProperty("humidity_percent")
    private double humidityPercent;
    
    @JsonProperty("wind_speed_ms")
    private double windSpeedMs;
    
    private String description;
    
    @JsonProperty("sunrise")
    private Instant sunrise;
    
    @JsonProperty("sunset")
    private Instant sunset;
}