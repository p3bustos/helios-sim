package com.p3bustos.heliossim.domain.services;

import com.p3bustos.heliossim.config.SimulatorProperties;
import com.p3bustos.heliossim.domain.models.BatterySystem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BatterySimulator {
    
    private final BatterySystem batterySystem;
    
    public BatterySimulator(SimulatorProperties properties) {
        var batteryConfig = properties.getSystem().getBattery();
        this.batterySystem = BatterySystem.builder()
                .capacityKwh(batteryConfig.getCapacityKwh())
                .maxChargeRateKw(batteryConfig.getMaxChargeRateKw())
                .maxDischargeRateKw(batteryConfig.getMaxDischargeRateKw())
                .minSocPercent(batteryConfig.getMinSocPercent())
                .maxSocPercent(batteryConfig.getMaxSocPercent())
                .roundTripEfficiency(batteryConfig.getRoundTripEfficiency())
                .socPercent(batteryConfig.getInitialSocPercent())
                .currentPowerKw(0.0)
                .build();
        
        log.info("Battery initialized at {}% SOC", batteryConfig.getInitialSocPercent());
    }
    
    /**
     * Attempt to charge the battery with available power
     * 
     * @param availablePowerKw Power available for charging
     * @param deltaTimeSeconds Time interval in seconds
     * @return Actual power consumed for charging
     */
    public double charge(double availablePowerKw, double deltaTimeSeconds) {
        return batterySystem.charge(availablePowerKw, deltaTimeSeconds);
    }
    
    /**
     * Attempt to discharge the battery to meet demand
     * 
     * @param demandPowerKw Power demanded from battery
     * @param deltaTimeSeconds Time interval in seconds
     * @return Actual power delivered
     */
    public double discharge(double demandPowerKw, double deltaTimeSeconds) {
        return batterySystem.discharge(demandPowerKw, deltaTimeSeconds);
    }
    
    public BatterySystem getBatterySystem() {
        return batterySystem;
    }
}