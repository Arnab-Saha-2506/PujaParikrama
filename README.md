# 🪔 PujaParikrama

### A location-aware backend for discovering Durga Puja pandals across Kolkata.

PujaParikrama is a Spring Boot backend that powers a Durga Puja discovery and navigation platform.

It allows users to discover pandals by area, find nearby pandals and metro stations, calculate distances, search nearby essential places, and create multi-stop Puja routes.

> **পুজোর কলকাতা, আপনার পথে**

---

## ✨ Features

### 🛕 Pandal Discovery

- Browse Durga Puja pandals by area/zone:
  - North Kolkata
  - South Kolkata
  - Central Kolkata
  - East Kolkata
  - Howrah
  - Hooghly
- View detailed pandal information:
  - Description
  - Address
  - Coordinates
  - Best time to visit
  - Images
  - Nearby metro stations

### 📍 Location-Based Discovery

- Find pandals near the user's current location.
- Radius-based nearby search.
- Bounding-box optimization for efficient location filtering.
- Maximum 20 nearby results.
- Distance calculation using the **Haversine formula**.
- Walking distance and time estimation.

### 🚇 Metro Integration

- Explore Kolkata Metro lines and stations.
- Find metro stations connected to individual pandals.
- Calculate distance and walking time between pandals and metro stations.
- Support multiple metro stations for a single pandal.

### 🗺️ Route Planning

- Create multi-waypoint Puja routes.
- Calculate routes using **OSRM**.
- Support multiple pandals within a single route.

### 📍 Nearby Places / POI

Search for useful places around a user's location:

- 🏧 ATM
- 🚔 Police
- 🏥 Hospital
- 💊 Pharmacy
- 🍽️ Restaurant
- ☕ Cafe
- 🚻 Toilet
- ⛽ Fuel
- 🏦 Bank

#### Multi-Provider Architecture

The POI service supports multiple external providers:

```text
                    ┌─────────────────┐
                    │   POI Request   │
                    └────────┬────────┘
                             ↓
                  ┌─────────────────────┐
                  │ Composite POI      │
                  │ Service            │
                  └─────────┬───────────┘
                            ↓
                    ┌───────────────┐
                    │ Google Places │
                    │    Primary    │
                    └───────┬───────┘
                            │
                     Failure / Timeout
                            ↓
                    ┌───────────────┐
                    │   Geoapify    │
                    │   Fallback    │
                    └───────────────┘
```

- Google Places as the primary provider.
- Geoapify as the fallback provider.
- Automatic fallback on provider errors, timeout, quota exhaustion, or key issues.
- 5-second external-service timeout.
- Spring Cache to reduce repeated external API calls.
- Composite cache key based on:
  - Place type
  - Latitude
  - Longitude
  - Radius

---

# 🔄 External Pandal Data Import

PujaParikrama supports importing pandal information from an external JSON dataset containing **900+ pandals**.

The import process validates, deduplicates, and merges incoming data instead of blindly replacing existing records.

```text
             External JSON Dataset
                      │
                      ▼
               Data Validation
                      │
                      ▼
                Deduplication
                      │
                      ▼
                 Data Merge
                      │
                      ▼
                    MySQL
                      │
                      ▼
               REST API Layer
                      │
                      ▼
                   Users
```

## Deduplication Strategy

Records are matched in the following order:

```text
1. external_id + source
             ↓
2. name + area
             ↓
3. coordinate proximity < 50m
             ↓
4. global coordinate proximity < 30m
             ↓
5. create new record
```

### Data Preservation

Existing curated information is preserved wherever possible.

### Updated from External Dataset

- Latitude
- Longitude
- Address, if existing value is null
- External ID
- Source

### Preserved Curated Data

- Name
- Description
- Best time to visit
- Image
- Area
- Metro relationships

### Data Lineage

Imported records maintain:

```text
externalId
source
```

This allows the origin of external data to be tracked.

---

# 🧠 Backend Architecture

```text
                         ┌──────────────────────┐
                         │       Frontend       │
                         └──────────┬───────────┘
                                    │
                                  REST
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │    Controllers       │
                         ├──────────────────────┤
                         │ Pandal               │
                         │ Metro                │
                         │ Route                │
                         │ Nearby Places        │
                         │ Contact              │
                         │ Import               │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │      Services        │
                         ├──────────────────────┤
                         │ Business Logic       │
                         │ POI Fallback         │
                         │ Route Calculation    │
                         │ Data Import          │
                         │ Metro Calculation    │
                         └──────────┬───────────┘
                                    │
                    ┌───────────────┴───────────────┐
                    │                               │
                    ▼                               ▼
          ┌──────────────────┐             ┌──────────────────┐
          │      MySQL       │             │  External APIs   │
          │                  │             │                  │
          │ Pandals          │             │ Geoapify         │
          │ Metro Stations   │             │ Google Places    │
          │ Areas            │             │ OSRM             │
          │ Relationships    │             │                  │
          └──────────────────┘             └──────────────────┘
```

---

# 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 22 |
| Framework | Spring Boot 3.5.6 |
| Build Tool | Gradle |
| Database | MySQL 8 |
| ORM | Spring Data JPA / Hibernate |
| API | REST |
| API Documentation | SpringDoc OpenAPI / Swagger |
| Validation | Jakarta Bean Validation / Hibernate Validator |
| Caching | Spring Cache + Caffeine |
| Mapping | Manual Entity ↔ DTO Mappers |
| Primary POI Provider | Google Places |
| POI Fallback | Geoapify |
| Routing | OSRM |
| Configuration | Environment Variables / dotenv-java |

---

# 📁 Project Structure

```text
src/main/java/com/proj/PujaParikrama/
│
├── controller/
│   ├── AreaController.java
│   ├── ContactController.java
│   ├── MetroController.java
│   ├── PandalController.java
│   ├── PandalImportController.java
│   └── RouteController.java
│
├── service/
│   ├── AreaService.java
│   ├── CompositeNearbyPlaceService.java
│   ├── ContactService.java
│   ├── DataInitializerService.java
│   ├── GooglePlacesServiceImpl.java
│   ├── MetroService.java
│   ├── NearbyPlaceService.java
│   ├── NearbyPlaceServiceImpl.java
│   ├── PandalImportService.java
│   ├── PandalMetroCalculationService.java
│   ├── PandalService.java
│   └── RouteService.java
│
├── repository/
│   ├── AreaRepository.java
│   ├── ContactRepository.java
│   ├── MetroStationRepository.java
│   ├── PandalMetroRepository.java
│   └── PandalRepository.java
│
├── entity/
│   ├── AreaEntity.java
│   ├── BaseEntity.java
│   ├── ContactMeEntity.java
│   ├── MetroStationEntity.java
│   ├── PandalEntity.java
│   └── PandalMetroEntity.java
│
├── dto/
├── mappers/
├── exception/
├── config/
│
└── PujaParikramaApplication.java
```

---

# 🗄️ Data Model

The application uses a relational data model for pandals, areas, and metro connectivity.

```text
                       Area
                         │
                         │ 1 : N
                         ▼
                       Pandal
                         │
                         │ 1 : N
                         ▼
                    PandalMetro
                         │
                         │ N : 1
                         ▼
                   MetroStation
```

## PandalEntity

```text
id
name
area
description
address
bestTimeToVisit
latitude
longitude
imageUrl
externalId
source
```

## MetroStationEntity

```text
id
name
line
latitude
longitude
```

## PandalMetroEntity

```text
pandal
metroStation
distanceKm
walkingTimeMinutes
```

## AreaEntity

```text
id
name
```

The separate `PandalMetroEntity` allows a pandal to have multiple nearby metro stations while storing station-specific distance and walking information.

---

# 📚 API Reference

## 🛕 Pandal APIs

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/v1/areas/{areaId}/pandals` | List pandals by area |
| `GET` | `/api/v1/pandals/{pandalId}` | Get pandal details |
| `GET` | `/api/v1/pandals/{pandalId}/distance` | Calculate distance from user |
| `GET` | `/api/v1/pandals/nearby` | Find nearby pandals |

### Nearby Pandals

```http
GET /api/v1/pandals/nearby?lat=22.5135&lon=88.3510&radiusKm=2
```

---

## 🚇 Metro APIs

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/v1/metro/lines` | Get all metro lines |
| `GET` | `/api/v1/metro/stations` | Get all metro stations |
| `GET` | `/api/v1/metro/stations/line/{lineName}` | Get stations by line |

---

## 📍 Nearby Places API

```http
GET /api/v1/nearby/places
```

Supported types:

```text
atm
police
hospital
pharmacy
restaurant
cafe
toilet
fuel
bank
```

### Example

```http
GET /api/v1/nearby/places?type=atm&lat=22.5135&lon=88.3510&radiusKm=2
```

---

## 🗺️ Route Planning API

```http
POST /api/v1/routes/calculate
```

Calculates routes between multiple waypoints using OSRM.

---

## 🔐 Admin APIs

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/admin/pandals/import/pujos-json` | Import external pandal dataset |
| `POST` | `/api/v1/admin/pandals/metros/calculate-all` | Calculate metro-pandal relationships |

> **Note:** Admin endpoints should be protected with authentication and authorization before production use.

---

## 📩 Contact API

```http
POST /api/v1/message/me
```

Supports:

- User queries
- Appreciation messages
- Request validation
- Required email for queries
- Optional email for appreciation messages

---

## ❤️ Health Check

```http
GET /api/v1/health
```

Used to verify backend availability.

---

# ⚙️ Local Setup

## Prerequisites

- Java 22+
- MySQL 8.0+
- Gradle 8.x

> The Gradle Wrapper is included, so installing Gradle separately is optional.

---

## 1. Clone the Repository

```bash
git clone <your-repository-url>
cd PujaParikrama
```

---

## 2. Configure Environment Variables

Create a `.env` file or configure environment variables:

```env
# Database
DATASOURCE_URL=jdbc:mysql://localhost:XXXX
DATASOURCE_USERNAME=your_user
DATASOURCE_PASSWORD=your_password
DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver
HIBERNATE_DIALECT=org.hibernate.dialect.MySQLDialect

# Server
PORT=2025
ACTIVE_PROFILE=local

# External APIs
GEOAPIFY_API_KEY=your_geoapify_key
GOOGLE_PLACES_API_KEY=your_google_places_key

# External Pandal Dataset
PUJA_JSON_URL=https://raw.githubusercontent.com/.../pujos.json
PUJOS_JSON_SOURCE=PUJOS_JSON
```

> ⚠️ **Never commit API keys, passwords, or `.env` files to Git.**

---

## 3. Create the Database

```sql
CREATE DATABASE pujapath;
```

For initial local development:

```properties
spring.jpa.hibernate.ddl-auto=update
```

For production:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

Ensure the required areas exist:

```text
South Kolkata
North Kolkata
Central Kolkata
East Kolkata
Howrah
Hooghly
```

---

## 4. Run the Application

### Linux / macOS

```bash
./gradlew bootRun
```

### Windows

```bash
gradlew.bat bootRun
```

### Build JAR

```bash
./gradlew clean build
```

Run:

```bash
java -jar build/libs/pujapath-0.0.1-SNAPSHOT.jar
```

The application will run on:

```text
http://localhost:2025
```

---

# 📖 Swagger / OpenAPI

Once the application is running:

### Swagger UI

```text
http://localhost:2025/swagger-ui/index.html
```

### OpenAPI JSON

```text
http://localhost:2025/v3/api-docs
```

---

# 🧪 Testing

Run the test suite:

```bash
./gradlew test
```

Generate JaCoCo coverage:

```bash
./gradlew jacocoTestReport
```

---

# 📝 Logging

Application logging includes:

- External API calls
- Import progress
- Database operations
- API execution timing
- Errors and exceptions

Log file:

```text
logs/pujapath.log
```

The log file uses daily rolling.

Application package logging:

```text
DEBUG → com.proj.PujaParikrama
```

---

# 🚢 Deployment

Build the production JAR:

```bash
./gradlew clean build -x test
```

Generated artifact:

```text
build/libs/pujapath-0.0.1-SNAPSHOT.jar
```

The application uses environment-based configuration and can be deployed to Java-compatible cloud platforms.

---

# 🔭 Future Improvements

- [ ] Authentication and authorization for admin APIs
- [ ] Scheduled external data synchronization
- [ ] Redis-based distributed caching
- [ ] API rate limiting
- [ ] Database query optimization
- [ ] Database indexing for larger datasets
- [ ] Application metrics and monitoring
- [ ] Distributed tracing
- [ ] Docker containerization
- [ ] CI/CD pipeline
- [ ] Automated integration testing
- [ ] Historical pandal data by Puja year
- [ ] Advanced route optimization

---

# 🎯 Engineering Highlights

PujaParikrama goes beyond a basic CRUD application by combining:

```text
                    PujaParikrama
                         │
        ┌────────────────┼────────────────┐
        │                │                │
        ▼                ▼                ▼
   REST APIs        Data Modelling   Geospatial Logic
        │                │                │
        ▼                ▼                ▼
 External APIs       MySQL/JPA       Haversine Search
        │                │                │
        └────────────────┼────────────────┘
                         │
                         ▼
                Resilient Integrations
                         │
              ┌──────────┴──────────┐
              ▼                     ▼
         Caching                Fallback
              │                     │
              └──────────┬──────────┘
                         ▼
                  Route Planning
                         │
                         ▼
                   Real Users
```

The project demonstrates practical backend engineering through:

- Location-based search
- Relational data modelling
- External API integration
- Provider fallback
- Caching
- Data synchronization
- Deduplication
- Route calculation
- API validation
- Exception handling
- Cloud deployment

---

# 🌐 Live Project

**PujaPath — পুজোর কলকাতা, আপনার পথে**

https://pujapath.pages.dev/

---

# 👨‍💻 Author

**Arnab Saha**

Backend Developer | Java | Spring Boot | Microservices
