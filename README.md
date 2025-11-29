# Helios Sim

A real-time site power telemetry simulator that models residential energy systems with solar panels, battery storage, and grid connectivity. Uses real weather data for realistic solar production simulation.

## Features

- 🌞 **Solar Production Modeling** - Weather-based PV generation using real-time irradiance data
- 🔋 **Battery Simulation** - SOC tracking with charge/discharge dynamics
- ⚡ **Load Profiling** - Configurable consumption patterns
- 📊 **Real-time Dashboard** - Live telemetry visualization with Material-UI
- 🔌 **MQTT Publishing** - Industry-standard IoT messaging protocol

## Architecture
```
Backend (Spring Boot + Java)  →  MQTT Broker
                              →  WebSocket → Frontend (React + TypeScript)
```

## Tech Stack

### Backend
- Java
- Spring Boot 3.x
- Spring WebFlux
- Eclipse Paho MQTT
- PostgreSQL/TimescaleDB

### Frontend
- React 18
- TypeScript
- Material-UI v7
- Recharts
- TanStack Query
- STOMP/SockJS

### Infrastructure
Coming soon...

## Getting Started

Coming soon...

## Configuration

Coming soon...

## 📝 License

**All Rights Reserved - No License**
Copyright © 2025 Patricio B.

This project is provided for **demonstration and portfolio purposes only**.

⚠️ **USE AT YOUR OWN RISK**
- This software is provided "as is" without warranty of any kind
- The author is not liable for any damages or issues arising from use
- No support or maintenance is guaranteed

🚫 **DO NOT COPY**
- This code may NOT be copied, modified, or distributed
- This code may NOT be used in other projects without explicit written permission
- Viewing for educational purposes only

For inquiries about using this code, please contact the author.
