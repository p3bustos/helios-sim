package com.p3bustos.heliossim.infrastructure.weather;

import com.p3bustos.heliossim.config.SimulatorProperties;
import com.p3bustos.heliossim.domain.services.SimulationOrchestrator;
import com.p3bustos.heliossim.dto.WeatherData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {
    
    private final SimulatorProperties properties;
    private final RestTemplate restTemplate;
    private final SimulationOrchestrator orchestrator;
    
    /**
     * Fetch weather data every 15 minutes
     */
    @Scheduled(fixedRate = 900000) // 15 minutes in milliseconds
    public void updateWeather() {
        try {
            WeatherData weather = fetchCurrentWeather();
            if (weather != null) {
                orchestrator.updateWeather(weather);
            }
        } catch (Exception e) {
            log.error("Failed to fetch weather data", e);
        }
    }
    
    /**
     * Fetch current weather from OpenWeatherMap API
     */
    @Cacheable(value = "weather", unless = "#result == null")
    public WeatherData fetchCurrentWeather() {
        var location = properties.getLocation();
        var weatherConfig = properties.getWeather();
        
        if ("demo".equals(weatherConfig.getApiKey())) {
            log.warn("Using demo weather data - set WEATHER_API_KEY environment variable for real data");
            return createDemoWeatherData();
        }
        
        String url = String.format(
            "%s/weather?lat=%f&lon=%f&appid=%s&units=metric",
            weatherConfig.getBaseUrl(),
            location.getLatitude(),
            location.getLongitude(),
            weatherConfig.getApiKey()
        );
        
        try {
            OpenWeatherMapResponse response = restTemplate.getForObject(url, OpenWeatherMapResponse.class);
            return convertToWeatherData(response);
        } catch (Exception e) {
            log.error("Error fetching weather from OpenWeatherMap", e);
            return createDemoWeatherData();
        }
    }
    
    /**
     * Convert OpenWeatherMap response to our WeatherData DTO
     */
    private WeatherData convertToWeatherData(OpenWeatherMapResponse response) {
        if (response == null) {
            return null;
        }
        
        return WeatherData.builder()
                .timestamp(Instant.now())
                .temperatureC(response.getMain().getTemp())
                .cloudCoverPercent(response.getClouds().getAll())
                .humidityPercent(response.getMain().getHumidity())
                .windSpeedMs(response.getWind().getSpeed())
                .description(response.getWeather().get(0).getDescription())
                .sunrise(Instant.ofEpochSecond(response.getSys().getSunrise()))
                .sunset(Instant.ofEpochSecond(response.getSys().getSunset()))
                .build();
    }
    
    /**
     * Create demo weather data for testing without API key
     */
    private WeatherData createDemoWeatherData() {
        // Simulate realistic weather based on time of day
        var now = Instant.now();
        int hour = java.time.ZonedDateTime.ofInstant(now, java.time.ZoneId.systemDefault()).getHour();
        
        // Temperature varies throughout the day
        double baseTemp = 20.0;
        double tempVariation = 5.0 * Math.sin((hour - 6) * Math.PI / 12.0);
        double temperature = baseTemp + tempVariation;
        
        // Some cloud cover
        double cloudCover = 25.0;
        
        // Default sunrise/sunset (6 AM / 6 PM)
        var zonedNow = java.time.ZonedDateTime.ofInstant(now, java.time.ZoneId.systemDefault());
        var sunrise = zonedNow.withHour(6).withMinute(0).withSecond(0).toInstant();
        var sunset = zonedNow.withHour(18).withMinute(0).withSecond(0).toInstant();
        
        return WeatherData.builder()
                .timestamp(now)
                .temperatureC(temperature)
                .cloudCoverPercent(cloudCover)
                .humidityPercent(60.0)
                .windSpeedMs(3.5)
                .description("Demo weather data")
                .sunrise(sunrise)
                .sunset(sunset)
                .build();
    }
}