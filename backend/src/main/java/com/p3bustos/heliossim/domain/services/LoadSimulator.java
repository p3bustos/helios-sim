package com.p3bustos.heliossim.domain.services;

import com.p3bustos.heliossim.config.SimulatorProperties;
import com.p3bustos.heliossim.domain.models.LoadProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Slf4j
@Service
public class LoadSimulator {
    
    private final LoadProfile loadProfile;
    
    public LoadSimulator(SimulatorProperties properties) {
        var loadConfig = properties.getSystem().getLoad();
        this.loadProfile = LoadProfile.builder()
                .baselineKw(loadConfig.getBaselineKw())
                .peakMorningKw(loadConfig.getPeakMorningKw())
                .peakEveningKw(loadConfig.getPeakEveningKw())
                .morningPeakHour(loadConfig.getMorningPeakHour())
                .eveningPeakHour(loadConfig.getEveningPeakHour())
                .build();
    }
    
    /**
     * Calculate current load consumption based on time of day
     */
    public double simulateLoad(ZonedDateTime currentTime) {
        return loadProfile.calculateLoad(currentTime.toLocalTime());
    }
    
    public LoadProfile getLoadProfile() {
        return loadProfile;
    }
}