package com.p3bustos.heliossim.domain.services;

import com.p3bustos.heliossim.domain.models.EnergyBalance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnergyFlowManager {
    
    private final BatterySimulator batterySimulator;
    
    /**
     * Calculate energy balance and flow based on current solar production and load
     * 
     * Priority logic:
     * 1. Solar powers load first
     * 2. Excess solar charges battery
     * 3. If battery full, export to grid
     * 4. If solar insufficient, discharge battery
     * 5. If battery depleted, import from grid
     */
    public EnergyBalance calculateEnergyFlow(double solarProductionKw, 
                                            double loadConsumptionKw,
                                            double deltaTimeSeconds) {
        
        EnergyBalance.EnergyBalanceBuilder balance = EnergyBalance.builder()
                .solarProductionKw(solarProductionKw)
                .loadConsumptionKw(loadConsumptionKw);
        
        double solarToLoadKw = 0;
        double solarToBatteryKw = 0;
        double solarToGridKw = 0;
        double batteryToLoadKw = 0;
        double gridToLoadKw = 0;
        double batteryPowerKw = 0;
        double gridPowerKw = 0;
        
        if (solarProductionKw >= loadConsumptionKw) {
            // Solar can meet or exceed load
            solarToLoadKw = loadConsumptionKw;
            double excessSolar = solarProductionKw - loadConsumptionKw;
            
            if (excessSolar > 0) {
                // Try to charge battery with excess solar
                double powerConsumedByBattery = batterySimulator.charge(excessSolar, deltaTimeSeconds);
                solarToBatteryKw = powerConsumedByBattery;
                batteryPowerKw = -powerConsumedByBattery; // negative = charging
                
                // Remaining excess goes to grid
                double remainingExcess = excessSolar - powerConsumedByBattery;
                if (remainingExcess > 0.01) { // Small threshold to avoid floating point issues
                    solarToGridKw = remainingExcess;
                    gridPowerKw = -remainingExcess; // negative = exporting
                }
            }
        } else {
            // Solar cannot meet load - need additional power
            solarToLoadKw = solarProductionKw;
            double deficit = loadConsumptionKw - solarProductionKw;
            
            // Try to discharge battery to cover deficit
            double powerFromBattery = batterySimulator.discharge(deficit, deltaTimeSeconds);
            batteryToLoadKw = powerFromBattery;
            batteryPowerKw = powerFromBattery; // positive = discharging
            
            // Remaining deficit comes from grid
            double remainingDeficit = deficit - powerFromBattery;
            if (remainingDeficit > 0.01) { // Small threshold
                gridToLoadKw = remainingDeficit;
                gridPowerKw = remainingDeficit; // positive = importing
            }
        }
        
        return balance
                .batteryPowerKw(batteryPowerKw)
                .gridPowerKw(gridPowerKw)
                .solarToLoadKw(solarToLoadKw)
                .solarToBatteryKw(solarToBatteryKw)
                .solarToGridKw(solarToGridKw)
                .batteryToLoadKw(batteryToLoadKw)
                .gridToLoadKw(gridToLoadKw)
                .build();
    }
}