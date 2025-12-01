package com.p3bustos.heliossim.domain.models;

import com.p3bustos.heliossim.dto.EnergyFlowData;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EnergyBalance {
    private double solarProductionKw;
    private double loadConsumptionKw;
    private double batteryPowerKw;  // negative = charging, positive = discharging
    private double gridPowerKw;     // negative = exporting, positive = importing
    
    // Energy flow breakdown
    private double solarToLoadKw;
    private double solarToBatteryKw;
    private double solarToGridKw;
    private double batteryToLoadKw;
    private double gridToLoadKw;
    
    /**
     * Convert to telemetry energy flow data
     */
    public EnergyFlowData toEnergyFlowData() {
        return EnergyFlowData.builder()
                .solarToLoadKw(solarToLoadKw)
                .solarToBatteryKw(solarToBatteryKw)
                .solarToGridKw(solarToGridKw)
                .batteryToLoadKw(batteryToLoadKw)
                .gridToLoadKw(gridToLoadKw)
                .build();
    }
}