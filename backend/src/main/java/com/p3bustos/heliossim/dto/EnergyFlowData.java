package com.p3bustos.heliossim.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnergyFlowData {
    @JsonProperty("solar_to_load_kw")
    private double solarToLoadKw;
    
    @JsonProperty("solar_to_battery_kw")
    private double solarToBatteryKw;
    
    @JsonProperty("solar_to_grid_kw")
    private double solarToGridKw;
    
    @JsonProperty("battery_to_load_kw")
    private double batteryToLoadKw;
    
    @JsonProperty("grid_to_load_kw")
    private double gridToLoadKw;
}