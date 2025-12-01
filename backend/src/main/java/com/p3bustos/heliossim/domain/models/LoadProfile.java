package com.p3bustos.heliossim.domain.models;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class LoadProfile {
    private double baselineKw;
    private double peakMorningKw;
    private double peakEveningKw;
    private int morningPeakHour;
    private int eveningPeakHour;
    
    // Current state
    private double currentConsumptionKw;
    
    /**
     * Calculate load consumption based on time of day
     * Uses a simple profile with morning and evening peaks
     * 
     * @param currentTime Current time of day
     * @return Load consumption in kW
     */
    public double calculateLoad(LocalTime currentTime) {
        int hour = currentTime.getHour();
        
        // Morning peak (Gaussian curve centered on morning peak hour)
        double morningFactor = gaussianCurve(hour, morningPeakHour, 1.5);
        double morningLoad = morningFactor * (peakMorningKw - baselineKw);
        
        // Evening peak (Gaussian curve centered on evening peak hour)
        double eveningFactor = gaussianCurve(hour, eveningPeakHour, 2.0);
        double eveningLoad = eveningFactor * (peakEveningKw - baselineKw);
        
        // Total load is baseline plus peaks
        currentConsumptionKw = baselineKw + morningLoad + eveningLoad;
        
        return currentConsumptionKw;
    }
    
    /**
     * Gaussian curve for smooth peak modeling
     */
    private double gaussianCurve(int hour, int peakHour, double width) {
        double delta = Math.abs(hour - peakHour);
        // Handle wrap-around at midnight
        if (delta > 12) {
            delta = 24 - delta;
        }
        return Math.exp(-Math.pow(delta / width, 2));
    }
}