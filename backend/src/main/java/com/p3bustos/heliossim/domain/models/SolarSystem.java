package com.p3bustos.heliossim.domain.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SolarSystem {
    private double capacityKw;
    private double panelEfficiency;
    private double inverterEfficiency;
    private double temperatureCoefficient;
    
    // Current state
    private double currentProductionKw;
    private double currentIrradianceWM2;
    private double currentPanelTempC;
    
    /**
     * Calculate solar production based on irradiance and temperature
     * 
     * @param irradianceWM2 Solar irradiance in W/m²
     * @param ambientTempC Ambient temperature in Celsius
     * @return Production in kW
     */
    public double calculateProduction(double irradianceWM2, double ambientTempC) {
        // Standard test conditions: 1000 W/m² at 25°C
        final double STC_IRRADIANCE = 1000.0;
        final double STC_TEMPERATURE = 25.0;
        
        // Calculate panel temperature (simplified model)
        // Panel temp is typically 20-30°C higher than ambient under load
        double panelTempC = ambientTempC + (irradianceWM2 / 1000.0) * 25.0;
        
        // Calculate temperature deration
        double tempDelta = panelTempC - STC_TEMPERATURE;
        double tempDeration = 1.0 + (temperatureCoefficient * tempDelta);
        
        // Calculate production
        double production = capacityKw *
                            (irradianceWM2 / STC_IRRADIANCE) *
                            panelEfficiency *
                            inverterEfficiency *
                            tempDeration;
        
        // Update current state
        this.currentProductionKw = Math.max(0, production);
        this.currentIrradianceWM2 = irradianceWM2;
        this.currentPanelTempC = panelTempC;
        
        return this.currentProductionKw;
    }
}