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
public class BatteryData {
    @JsonProperty("soc_percent")
    private double socPercent;
    
    @JsonProperty("power_kw")
    private double powerKw;
    
    @JsonProperty("voltage_v")
    private double voltageV;
    
    @JsonProperty("current_a")
    private double currentA;
}

