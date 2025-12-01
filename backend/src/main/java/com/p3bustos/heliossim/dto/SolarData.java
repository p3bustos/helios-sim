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
public class SolarData {
    @JsonProperty("production_kw")
    private double productionKw;
    
    @JsonProperty("irradiance_w_m2")
    private double irradianceWM2;
    
    @JsonProperty("panel_temp_c")
    private double panelTempC;
}