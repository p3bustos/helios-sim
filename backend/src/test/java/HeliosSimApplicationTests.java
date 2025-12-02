package com.p3bustos.heliossim;

import com.p3bustos.heliossim.config.SimulatorProperties;
import com.p3bustos.heliossim.domain.services.BatterySimulator;
import com.p3bustos.heliossim.domain.services.SolarSimulator;
import com.p3bustos.heliossim.domain.services.LoadSimulator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HeliosSimApplicationTests {

    @Autowired
    private SimulatorProperties properties;

    @Autowired
    private BatterySimulator batterySimulator;

    @Autowired
    private SolarSimulator solarSimulator;

    @Autowired
    private LoadSimulator loadSimulator;

    @Test
    void contextLoads() {
        // Verifies that the Spring context loads successfully
        assertNotNull(properties);
    }

    @Test
    void batterySimulatorInitializes() {
        assertNotNull(batterySimulator);
        var battery = batterySimulator.getBatterySystem();
        assertNotNull(battery);
        assertEquals(50.0, battery.getSocPercent(), 0.1);
    }

    @Test
    void solarSimulatorInitializes() {
        assertNotNull(solarSimulator);
        var solar = solarSimulator.getSolarSystem();
        assertNotNull(solar);
        assertEquals(10.0, solar.getCapacityKw(), 0.1);
    }

    @Test
    void loadSimulatorInitializes() {
        assertNotNull(loadSimulator);
        var load = loadSimulator.getLoadProfile();
        assertNotNull(load);
        assertEquals(2.0, load.getBaselineKw(), 0.1);
    }

    @Test
    void batteryChargeAndDischarge() {
        var battery = batterySimulator.getBatterySystem();
        double initialSoc = battery.getSocPercent();
        
        // Charge battery
        double charged = batterySimulator.charge(5.0, 1.0);
        assertTrue(charged > 0, "Battery should accept charge");
        assertTrue(battery.getSocPercent() > initialSoc, "SOC should increase");
        
        // Discharge battery
        double currentSoc = battery.getSocPercent();
        double discharged = batterySimulator.discharge(2.0, 1.0);
        assertTrue(discharged > 0, "Battery should discharge");
        assertTrue(battery.getSocPercent() < currentSoc, "SOC should decrease");
    }
}