package com.p3bustos.heliossim.domain.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BatterySystem {
    private double capacityKwh;
    private double maxChargeRateKw;
    private double maxDischargeRateKw;
    private double minSocPercent;
    private double maxSocPercent;
    private double roundTripEfficiency;
    
    // Current state
    private double socPercent;
    private double currentPowerKw;  // negative = charging, positive = discharging
    
    /**
     * Attempt to charge the battery
     * 
     * @param powerKw Power available for charging
     * @param deltaTimeSeconds Time interval
     * @return Actual power consumed for charging
     */
    public double charge(double powerKw, double deltaTimeSeconds) {
        if (socPercent >= maxSocPercent) {
            currentPowerKw = 0;
            return 0;
        }
        
        // Limit by max charge rate
        double actualChargePower = Math.min(powerKw, maxChargeRateKw);
        
        // Calculate energy that would be stored (accounting for efficiency)
        double energyKwh = actualChargePower * (deltaTimeSeconds / 3600.0) * roundTripEfficiency;
        
        // Calculate SOC increase
        double socIncrease = (energyKwh / capacityKwh) * 100.0;
        
        // Don't exceed max SOC
        if (socPercent + socIncrease > maxSocPercent) {
            socIncrease = maxSocPercent - socPercent;
            energyKwh = (socIncrease / 100.0) * capacityKwh;
            actualChargePower = energyKwh / (deltaTimeSeconds / 3600.0) / roundTripEfficiency;
        }
        
        socPercent += socIncrease;
        currentPowerKw = -actualChargePower;  // negative indicates charging
        
        return actualChargePower;
    }
    
    /**
     * Attempt to discharge the battery
     * 
     * @param powerKw Power requested from battery
     * @param deltaTimeSeconds Time interval
     * @return Actual power delivered
     */
    public double discharge(double powerKw, double deltaTimeSeconds) {
        if (socPercent <= minSocPercent) {
            currentPowerKw = 0;
            return 0;
        }
        
        // Limit by max discharge rate
        double actualDischargePower = Math.min(powerKw, maxDischargeRateKw);
        
        // Calculate energy that would be removed
        double energyKwh = actualDischargePower * (deltaTimeSeconds / 3600.0);
        
        // Calculate SOC decrease
        double socDecrease = (energyKwh / capacityKwh) * 100.0;
        
        // Don't go below min SOC
        if (socPercent - socDecrease < minSocPercent) {
            socDecrease = socPercent - minSocPercent;
            energyKwh = (socDecrease / 100.0) * capacityKwh;
            actualDischargePower = energyKwh / (deltaTimeSeconds / 3600.0);
        }
        
        socPercent -= socDecrease;
        currentPowerKw = actualDischargePower;  // positive indicates discharging
        
        return actualDischargePower;
    }
    
    /**
     * Get battery voltage based on SOC (simplified linear model)
     */
    public double getVoltage() {
        // Simplified: voltage varies from 48V (min) to 54V (max)
        return 48.0 + (socPercent / 100.0) * 6.0;
    }
    
    /**
     * Get battery current based on power and voltage
     */
    public double getCurrent() {
        double voltage = getVoltage();
        return voltage > 0 ? currentPowerKw * 1000.0 / voltage : 0;
    }
}