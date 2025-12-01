package com.p3bustos.heliossim.domain.services;

import com.p3bustos.heliossim.config.SimulatorProperties;
import com.p3bustos.heliossim.domain.models.SolarSystem;
import com.p3bustos.heliossim.dto.WeatherData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.ZonedDateTime;

@Slf4j
@Service
public class SolarSimulator {
    
    private final SolarSystem solarSystem;
    
    public SolarSimulator(SimulatorProperties properties) {
        var solarConfig = properties.getSystem().getSolar();
        this.solarSystem = SolarSystem.builder()
                .capacityKw(solarConfig.getCapacityKw())
                .panelEfficiency(solarConfig.getPanelEfficiency())
                .inverterEfficiency(solarConfig.getInverterEfficiency())
                .temperatureCoefficient(solarConfig.getTemperatureCoefficient())
                .build();
    }
    
    /**
     * Simulate solar production based on time of day and weather
     */
    public double simulateProduction(ZonedDateTime currentTime, WeatherData weather) {
        LocalTime time = currentTime.toLocalTime();
        
        // Calculate theoretical solar irradiance based on time of day
        double theoreticalIrradiance = calculateTheoreticalIrradiance(
            time, 
            weather != null ? weather.getSunrise() : null,
            weather != null ? weather.getSunset() : null
        );
        
        // Apply cloud cover reduction if weather data available
        double cloudFactor = 1.0;
        if (weather != null) {
            cloudFactor = 1.0 - (weather.getCloudCoverPercent() / 100.0 * 0.75);
        }
        
        double actualIrradiance = theoreticalIrradiance * cloudFactor;
        
        // Get temperature
        double temperature = weather != null ? weather.getTemperatureC() : 25.0;
        
        // Calculate production
        return solarSystem.calculateProduction(actualIrradiance, temperature);
    }
    
    /**
     * Calculate theoretical solar irradiance based on time of day
     * Simplified model using a sine curve between sunrise and sunset
     */
    private double calculateTheoreticalIrradiance(LocalTime time, 
                                                   java.time.Instant sunrise, 
                                                   java.time.Instant sunset) {
        // Default sunrise/sunset if not provided
        int sunriseHour = 6;
        int sunsetHour = 18;
        
        if (sunrise != null && sunset != null) {
            sunriseHour = ZonedDateTime.ofInstant(sunrise, java.time.ZoneId.systemDefault()).getHour();
            sunsetHour = ZonedDateTime.ofInstant(sunset, java.time.ZoneId.systemDefault()).getHour();
        }
        
        int currentHour = time.getHour();
        int currentMinute = time.getMinute();
        double currentTimeDecimal = currentHour + (currentMinute / 60.0);
        
        // No sun before sunrise or after sunset
        if (currentTimeDecimal < sunriseHour || currentTimeDecimal > sunsetHour) {
            return 0.0;
        }
        
        // Calculate position in the day (0 to 1, where 0.5 is solar noon)
        double dayLength = sunsetHour - sunriseHour;
        double timeIntoDay = currentTimeDecimal - sunriseHour;
        double dayPosition = timeIntoDay / dayLength;
        
        // Use sine curve for solar irradiance (peaks at solar noon)
        // Max irradiance is ~1000 W/m² on a clear day
        double maxIrradiance = 1000.0;
        return maxIrradiance * Math.sin(dayPosition * Math.PI);
    }
    
    public SolarSystem getSolarSystem() {
        return solarSystem;
    }
}