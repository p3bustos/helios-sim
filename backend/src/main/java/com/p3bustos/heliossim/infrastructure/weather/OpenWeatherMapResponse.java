package com.p3bustos.heliossim.infrastructure.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenWeatherMapResponse {
    private Main main;
    private List<Weather> weather;
    private Clouds clouds;
    private Wind wind;
    private Sys sys;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Main {
        private double temp;
        private double humidity;
        private double pressure;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weather {
        private String main;
        private String description;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Clouds {
        private double all;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Wind {
        private double speed;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sys {
        private long sunrise;
        private long sunset;
    }
}