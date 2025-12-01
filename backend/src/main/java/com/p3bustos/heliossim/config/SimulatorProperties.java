package com.p3bustos.heliossim.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "simulator")
public class SimulatorProperties {

    private Location location = new Location();
    private System system = new System();
    private Mqtt mqtt = new Mqtt();
    private Publishing publishing = new Publishing();
    private Weather weather = new Weather();

    @Data
    public static class Location {
        private double latitude;
        private double longitude;
        private String timezone;
    }

    @Data
    public static class System {
        private Solar solar = new Solar();
        private Battery battery = new Battery();
        private Load load = new Load();

        @Data
        public static class Solar {
            private double capacityKw;
            private double panelEfficiency;
            private double inverterEfficiency;
            private double temperatureCoefficient;
        }

        @Data
        public static class Battery {
            private double capacityKwh;
            private double maxChargeRateKw;
            private double maxDischargeRateKw;
            private double initialSocPercent;
            private double minSocPercent;
            private double maxSocPercent;
            private double roundTripEfficiency;
        }

        @Data
        public static class Load {
            private double baselineKw;
            private double peakMorningKw;
            private double peakEveningKw;
            private int morningPeakHour;
            private int eveningPeakHour;
        }
    }

    @Data
    public static class Mqtt {
        private String brokerUrl;
        private String topic;
        private String clientId;
        private int qos;
        private boolean retain;
        private String username;
        private String password;
    }

    @Data
    public static class Publishing {
        private long rateMs;
    }

    @Data
    public static class Weather {
        private String provider;
        private String apiKey;
        private String baseUrl;
        private int cacheTtlMinutes;
    }
}