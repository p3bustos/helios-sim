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
public class PowerTelemetry {
    
    private Instant timestamp;
    
    @JsonProperty("site_id")
    private String siteId;
    
    private SolarData solar;
    private BatteryData battery;
    private LoadData load;
    private GridData grid;
    
    @JsonProperty("energy_flow")
    private EnergyFlowData energyFlow;
}