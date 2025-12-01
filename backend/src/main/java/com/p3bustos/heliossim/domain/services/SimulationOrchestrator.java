package com.p3bustos.heliossim.domain.services;

import com.p3bustos.heliossim.config.SimulatorProperties;
import com.p3bustos.heliossim.domain.models.EnergyBalance;
import com.p3bustos.heliossim.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimulationOrchestrator {
    
    private final SolarSimulator solarSimulator;
    private final BatterySimulator batterySimulator;
    private final LoadSimulator loadSimulator;
    private final EnergyFlowManager energyFlowManager;
    private final SimulatorProperties properties;
    
    private WeatherData currentWeather;
    
    /**
     * Main simulation loop - runs at configured rate
     */
    @Scheduled(fixedDelayString = "${simulator.publishing.rate-ms}")
    public void simulateTick() {
        try {
            // Get current time in configured timezone
            ZonedDateTime currentTime = ZonedDateTime.now(
                ZoneId.of(properties.getLocation().getTimezone())
            );
            
            // Time delta in seconds (for battery charge/discharge calculations)
            double deltaTimeSeconds = properties.getPublishing().getRateMs() / 1000.0;
            
            // Simulate solar production
            double solarProductionKw = solarSimulator.simulateProduction(currentTime, currentWeather);
            
            // Simulate load consumption
            double loadConsumptionKw = loadSimulator.simulateLoad(currentTime);
            
            // Calculate energy balance and flows
            EnergyBalance energyBalance = energyFlowManager.calculateEnergyFlow(
                solarProductionKw,
                loadConsumptionKw,
                deltaTimeSeconds
            );
            
            // Build telemetry
            PowerTelemetry telemetry = buildTelemetry(currentTime.toInstant(), energyBalance);
            
            // Publish telemetry (will implement MQTT publisher next)
            publishTelemetry(telemetry);
            
            log.debug("Telemetry: Solar={} kW, Load={} kW, Battery={}% ({} kW), Grid={} kW",
                String.format("%.2f", solarProductionKw),
                String.format("%.2f", loadConsumptionKw),
                String.format("%.1f", batterySimulator.getBatterySystem().getSocPercent()),
                String.format("%.2f", batterySimulator.getBatterySystem().getCurrentPowerKw()),
                String.format("%.2f", energyBalance.getGridPowerKw())
            );
            
        } catch (Exception e) {
            log.error("Error in simulation tick", e);
        }
    }
    
    /**
     * Build complete power telemetry message
     */
    private PowerTelemetry buildTelemetry(Instant timestamp, EnergyBalance energyBalance) {
        var solarSystem = solarSimulator.getSolarSystem();
        var batterySystem = batterySimulator.getBatterySystem();
        var loadProfile = loadSimulator.getLoadProfile();
        
        return PowerTelemetry.builder()
                .timestamp(timestamp)
                .siteId("home-001")
                .solar(SolarData.builder()
                        .productionKw(solarSystem.getCurrentProductionKw())
                        .irradianceWM2(solarSystem.getCurrentIrradianceWM2())
                        .panelTempC(solarSystem.getCurrentPanelTempC())
                        .build())
                .battery(BatteryData.builder()
                        .socPercent(batterySystem.getSocPercent())
                        .powerKw(batterySystem.getCurrentPowerKw())
                        .voltageV(batterySystem.getVoltage())
                        .currentA(batterySystem.getCurrent())
                        .build())
                .load(LoadData.builder()
                        .consumptionKw(loadProfile.getCurrentConsumptionKw())
                        .build())
                .grid(GridData.builder()
                        .powerKw(energyBalance.getGridPowerKw())
                        .build())
                .energyFlow(energyBalance.toEnergyFlowData())
                .build();
    }
    
    /**
     * Publish telemetry to MQTT (placeholder for now)
     */
    private void publishTelemetry(PowerTelemetry telemetry) {
        // Will implement MQTT publisher in next step
        // For now, just log every 10 seconds to avoid spam
        if (System.currentTimeMillis() % 10000 < properties.getPublishing().getRateMs()) {
            log.info("Telemetry snapshot: Solar={} kW, Battery={}%, Load={} kW",
                String.format("%.2f", telemetry.getSolar().getProductionKw()),
                String.format("%.1f", telemetry.getBattery().getSocPercent()),
                String.format("%.2f", telemetry.getLoad().getConsumptionKw())
            );
        }
    }
    
    /**
     * Update weather data (will be called by weather service)
     */
    public void updateWeather(WeatherData weather) {
        this.currentWeather = weather;
        log.info("Weather updated: {}°C, {}% clouds", 
            String.format("%.1f", weather.getTemperatureC()),
            String.format("%.0f", weather.getCloudCoverPercent())
        );
    }
}