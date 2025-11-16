# Dance Studio Booking App - Spring Boot Backend

## Project Overview

The Spring Boot backend for a comprehensive dance studio booking platform, similar to Playo. This RESTful API serves as the core backend system handling user authentication, studio management, booking operations, payments, and administrative functions.

## Architecture & Technology Stack

### Backend Technology
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: PostgreSQL with PostGIS (spatial extension)
- **ORM**: Spring Data JPA with Hibernate
- **Security**: Spring Security + JWT
- **Authentication**: JWT + OAuth2 (Google/Facebook)
- **Database Migrations**: Liquibase
- **Caching**: Redis (planned)
- **Message Queue**: RabbitMQ (planned)
- **Storage**: AWS S3 / Google Cloud Storage
- **Payment Gateway**: Razorpay / Stripe

### Current Dependencies
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
    <dependency>
        <groupId>org.liquibase</groupId>
        <artifactId>liquibase-core</artifactId>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
    </dependency>
</dependencies>

<!-- Planned Dependencies -->
<!--
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-java-sdk-s3</artifactId>
</dependency>
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
-->
```

## System Architecture

### Layered Architecture
```
┌─────────────────────────────────────┐
│           Presentation Layer         │
│     (Controllers, DTOs, Mappers)    │
├─────────────────────────────────────┤
│             Service Layer           │
│        (Business Logic)             │
├─────────────────────────────────────┤
│            Repository Layer         │
│       (Data Access Objects)        │
├─────────────────────────────────────┤
│             Data Layer             │
│     (PostgreSQL + PostGIS)         │
└─────────────────────────────────────┘
```

## Data Model & Database Schema

### Core Entities

#### 1. User Management
```sql
-- Users table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER', -- CUSTOMER, OWNER, ADMIN
    is_active BOOLEAN NOT NULL DEFAULT true,
    email_verified BOOLEAN NOT NULL DEFAULT false,
    phone_verified BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User profiles for additional information
CREATE TABLE user_profiles (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id),
    avatar_url VARCHAR(500),
    date_of_birth DATE,
    gender VARCHAR(20),
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    preferences JSONB, -- Store user preferences as JSON
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### 2. Studio Management
```sql
-- Studios table
CREATE TABLE studios (
    id SERIAL PRIMARY KEY,
    owner_id INTEGER NOT NULL REFERENCES users(id),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    -- Location with spatial indexing
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    location GEOGRAPHY(POINT, 4326), -- PostGIS spatial column
    -- Address details
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    country VARCHAR(100) NOT NULL,
    -- Contact information
    contact_phone VARCHAR(20),
    contact_email VARCHAR(255),
    -- Business details
    hourly_rate DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'INR',
    capacity INTEGER NOT NULL,
    area_sqft DECIMAL(8, 2),
    -- Timing
    opening_time TIME NOT NULL,
    closing_time TIME NOT NULL,
    -- Status and ratings
    is_active BOOLEAN NOT NULL DEFAULT true,
    is_verified BOOLEAN NOT NULL DEFAULT false,
    average_rating DECIMAL(3, 2) DEFAULT 0.00,
    total_reviews INTEGER DEFAULT 0,
    total_bookings INTEGER DEFAULT 0,
    -- Metadata
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Studio amenities (many-to-many)
CREATE TABLE amenities (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    icon VARCHAR(100),
    category VARCHAR(50), -- BASIC, AUDIO, SAFETY, etc.
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE studio_amenities (
    studio_id INTEGER NOT NULL REFERENCES studios(id),
    amenity_id INTEGER NOT NULL REFERENCES amenities(id),
    PRIMARY KEY (studio_id, amenity_id)
);

-- Studio images
CREATE TABLE studio_images (
    id SERIAL PRIMARY KEY,
    studio_id INTEGER NOT NULL REFERENCES studios(id),
    image_url VARCHAR(500) NOT NULL,
    caption VARCHAR(255),
    is_primary BOOLEAN DEFAULT false,
    display_order INTEGER DEFAULT 0,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### 3. Booking Management
```sql
-- Bookings table
CREATE TABLE bookings (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id),
    studio_id INTEGER NOT NULL REFERENCES studios(id),
    -- Booking timing
    booking_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    duration_hours DECIMAL(4, 2) NOT NULL,
    -- Pricing
    hourly_rate DECIMAL(10, 2) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    discount_amount DECIMAL(10, 2) DEFAULT 0.00,
    final_amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'INR',
    -- Status tracking
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING', 
    -- PENDING, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW
    cancellation_reason TEXT,
    cancelled_by INTEGER REFERENCES users(id),
    cancelled_at TIMESTAMP,
    -- Special requirements
    special_requirements TEXT,
    guest_count INTEGER DEFAULT 1,
    -- Metadata
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- Constraints
    CHECK (end_time > start_time),
    CHECK (final_amount >= 0)
);

-- Booking status history for audit trail
CREATE TABLE booking_status_history (
    id SERIAL PRIMARY KEY,
    booking_id INTEGER NOT NULL REFERENCES bookings(id),
    from_status VARCHAR(50),
    to_status VARCHAR(50) NOT NULL,
    changed_by INTEGER NOT NULL REFERENCES users(id),
    reason TEXT,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### 4. Payment Management
```sql
-- Payments table
CREATE TABLE payments (
    id SERIAL PRIMARY KEY,
    booking_id INTEGER NOT NULL REFERENCES bookings(id),
    -- Payment details
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'INR',
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    -- PENDING, PROCESSING, SUCCESS, FAILED, CANCELLED, REFUNDED
    payment_method VARCHAR(50), -- CARD, UPI, NETBANKING, WALLET
    -- Gateway integration
    gateway_provider VARCHAR(50), -- RAZORPAY, STRIPE, PAYU
    gateway_payment_id VARCHAR(255),
    gateway_order_id VARCHAR(255),
    gateway_response JSONB,
    -- Timing
    initiated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    failed_at TIMESTAMP,
    -- Refund information
    refund_amount DECIMAL(10, 2) DEFAULT 0.00,
    refunded_at TIMESTAMP,
    refund_gateway_id VARCHAR(255),
    -- Metadata
    failure_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### 5. Reviews & Ratings
```sql
-- Reviews table
CREATE TABLE reviews (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id),
    studio_id INTEGER NOT NULL REFERENCES studios(id),
    booking_id INTEGER REFERENCES bookings(id), -- Optional link to booking
    -- Review content
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    title VARCHAR(255),
    comment TEXT,
    -- Review categories (JSON for flexibility)
    category_ratings JSONB, -- {"cleanliness": 4, "equipment": 5, "ambiance": 3}
    -- Status
    is_verified BOOLEAN DEFAULT false,
    is_approved BOOLEAN DEFAULT true,
    is_featured BOOLEAN DEFAULT false,
    -- Response from studio owner
    owner_response TEXT,
    owner_responded_at TIMESTAMP,
    -- Metadata
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- Ensure one review per user per studio (or per booking)
    UNIQUE(user_id, studio_id, booking_id)
);

-- Review images
CREATE TABLE review_images (
    id SERIAL PRIMARY KEY,
    review_id INTEGER NOT NULL REFERENCES reviews(id),
    image_url VARCHAR(500) NOT NULL,
    caption VARCHAR(255),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Spatial Indexing for Geolocation
```sql
-- Create spatial index for location-based queries
CREATE INDEX idx_studios_location ON studios USING GIST(location);

-- Function to update location column from lat/lng
CREATE OR REPLACE FUNCTION update_studio_location()
RETURNS TRIGGER AS $$
BEGIN
    NEW.location = ST_SetSRID(ST_MakePoint(NEW.longitude, NEW.latitude), 4326);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to automatically update location
CREATE TRIGGER trigger_update_studio_location
    BEFORE INSERT OR UPDATE ON studios
    FOR EACH ROW
    EXECUTE FUNCTION update_studio_location();
```

## Project Structure

```
src/
├── main/
│   ├── java/com/booking/studiobooking/
│   │   ├── StudioBookingApplication.java    # Main application class
│   │   ├── config/                          # Configuration classes
│   │   │   ├── SecurityConfig.java          # Security configuration
│   │   │   ├── DatabaseConfig.java          # Database configuration
│   │   │   ├── RedisConfig.java            # Cache configuration
│   │   │   └── SwaggerConfig.java           # API documentation
│   │   ├── controller/                      # REST controllers
│   │   │   ├── AuthController.java          # Authentication endpoints
│   │   │   ├── UserController.java          # User management
│   │   │   ├── StudioController.java        # Studio operations
│   │   │   ├── BookingController.java       # Booking management
│   │   │   ├── PaymentController.java       # Payment processing
│   │   │   └── ReviewController.java        # Reviews & ratings
│   │   ├── dto/                             # Data Transfer Objects
│   │   │   ├── request/                     # Request DTOs
│   │   │   ├── response/                    # Response DTOs
│   │   │   └── mapper/                      # Entity-DTO mappers
│   │   ├── entity/                          # JPA entities
│   │   │   ├── User.java
│   │   │   ├── Studio.java
│   │   │   ├── Booking.java
│   │   │   ├── Payment.java
│   │   │   └── Review.java
│   │   ├── repository/                      # Data repositories
│   │   │   ├── UserRepository.java
│   │   │   ├── StudioRepository.java
│   │   │   ├── BookingRepository.java
│   │   │   ├── PaymentRepository.java
│   │   │   └── ReviewRepository.java
│   │   ├── service/                         # Business logic
│   │   │   ├── AuthService.java
│   │   │   ├── UserService.java
│   │   │   ├── StudioService.java
│   │   │   ├── BookingService.java
│   │   │   ├── PaymentService.java
│   │   │   ├── ReviewService.java
│   │   │   ├── NotificationService.java
│   │   │   └── EmailService.java
│   │   ├── security/                        # Security components
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   ├── JwtTokenProvider.java
│   │   │   └── CustomUserDetailsService.java
│   │   ├── exception/                       # Exception handling
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── BusinessException.java
│   │   │   └── ResourceNotFoundException.java
│   │   ├── util/                           # Utility classes
│   │   │   ├── DateTimeUtil.java
│   │   │   ├── ValidationUtil.java
│   │   │   └── EncryptionUtil.java
│   │   └── integration/                    # External integrations
│   │       ├── payment/                    # Payment gateway integration
│   │       ├── notification/               # SMS/Email services
│   │       └── storage/                    # File storage services
│   └── resources/
│       ├── application.properties          # Application configuration
│       ├── application-dev.properties      # Development environment
│       ├── application-prod.properties     # Production environment
│       └── db/changelog/                   # Liquibase migrations
│           ├── changelog-master.xml
│           └── changes/
│               ├── 001-create-users-schema.xml
│               ├── 002-create-studios-schema.xml
│               ├── 003-create-bookings-schema.xml
│               ├── 004-create-payments-schema.xml
│               ├── 005-create-reviews-schema.xml
│               └── 006-insert-sample-data.xml
└── test/
    ├── java/com/booking/studiobooking/
    │   ├── controller/                     # Controller tests
    │   ├── service/                        # Service layer tests
    │   ├── repository/                     # Repository tests
    │   └── integration/                    # Integration tests
    └── resources/
        └── application-test.properties     # Test configuration
```

## API Specification

### Base URL & Versioning
- **Base URL**: `http://localhost:8080/api/v1`
- **API Version**: v1
- **Content Type**: `application/json`
- **Authentication**: JWT Bearer Token

### API Endpoints

#### 1. Authentication & User Management

##### Authentication
```http
POST /api/v1/auth/signup
POST /api/v1/auth/login
POST /api/v1/auth/refresh
POST /api/v1/auth/logout
POST /api/v1/auth/forgot-password
POST /api/v1/auth/reset-password
POST /api/v1/auth/verify-email
POST /api/v1/auth/verify-phone

# Social Authentication
POST /api/v1/auth/google
POST /api/v1/auth/facebook
```

##### User Profile
```http
GET    /api/v1/users/profile
PUT    /api/v1/users/profile
DELETE /api/v1/users/profile
POST   /api/v1/users/upload-avatar
```

#### 2. Studio Management

##### Public Studio Access
```http
GET /api/v1/studios                    # Search studios with filters
GET /api/v1/studios/{id}               # Get studio details
GET /api/v1/studios/{id}/availability  # Check availability
GET /api/v1/studios/{id}/reviews       # Get studio reviews
GET /api/v1/studios/nearby             # Location-based search
GET /api/v1/studios/featured           # Get featured studios
```

##### Studio Owner Operations
```http
POST   /api/v1/owners/studios          # Create new studio
PUT    /api/v1/owners/studios/{id}     # Update studio
DELETE /api/v1/owners/studios/{id}     # Delete studio
POST   /api/v1/owners/studios/{id}/images  # Upload images
GET    /api/v1/owners/studios          # Get owner's studios
GET    /api/v1/owners/studios/{id}/bookings # Get studio bookings
PUT    /api/v1/owners/studios/{id}/availability # Update availability
```

#### 3. Booking Management

##### Customer Booking Operations
```http
POST /api/v1/bookings                  # Create new booking
GET  /api/v1/bookings                  # Get user bookings
GET  /api/v1/bookings/{id}            # Get booking details
PUT  /api/v1/bookings/{id}/cancel      # Cancel booking
PUT  /api/v1/bookings/{id}/reschedule  # Reschedule booking
```

##### Studio Owner Booking Management
```http
GET /api/v1/owners/bookings            # Get all owner's bookings
PUT /api/v1/owners/bookings/{id}/confirm   # Confirm booking
PUT /api/v1/owners/bookings/{id}/cancel    # Cancel booking
```

#### 4. Payment Management
```http
POST /api/v1/payments/initiate         # Initiate payment
POST /api/v1/payments/verify           # Verify payment
POST /api/v1/payments/webhook          # Payment gateway webhook
GET  /api/v1/payments/history          # Payment history
POST /api/v1/payments/refund           # Process refund
```

#### 5. Reviews & Ratings
```http
POST /api/v1/reviews                   # Submit review
PUT  /api/v1/reviews/{id}              # Update review
DELETE /api/v1/reviews/{id}            # Delete review
GET  /api/v1/reviews/my-reviews        # Get user's reviews
POST /api/v1/reviews/{id}/helpful      # Mark review as helpful
```

#### 6. Admin Operations
```http
GET    /api/v1/admin/users             # Manage users
GET    /api/v1/admin/studios           # Manage studios
GET    /api/v1/admin/bookings          # View all bookings
GET    /api/v1/admin/payments          # Payment analytics
GET    /api/v1/admin/reviews           # Review moderation
POST   /api/v1/admin/promotions        # Create promotions
GET    /api/v1/admin/analytics         # System analytics
```

## Development Setup

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL 14+
- Redis 6+ (for caching)
- IDE (IntelliJ IDEA / VS Code)

### Installation & Setup

#### 1. Database Setup
```sql
-- Create database
CREATE DATABASE studio_booking_db;

-- Create user (optional)
CREATE USER studio_app WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE studio_booking_db TO studio_app;

-- Enable PostGIS extension
\c studio_booking_db;
CREATE EXTENSION IF NOT EXISTS postgis;
```

#### 2. Project Setup
```bash
# Clone repository
git clone <repository-url>
cd BMS-Backend

# Build project
mvn clean compile

# Run database migrations
mvn liquibase:update

# Run application
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### 3. Environment Variables
```bash
# Create .env file or set environment variables
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
export JWT_SECRET=your-super-secret-jwt-key
export RAZORPAY_KEY_ID=your_razorpay_key
export RAZORPAY_SECRET=your_razorpay_secret
export AWS_ACCESS_KEY_ID=your_aws_access_key
export AWS_SECRET_ACCESS_KEY=your_aws_secret_key
```

### Configuration Files

#### Database Configuration
```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/studio_booking_db
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:password}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# Liquibase Configuration
spring.liquibase.change-log=classpath:db/changelog/changelog-master.xml
```

## Testing Strategy

### Test Structure
```
src/test/java/com/booking/studiobooking/
├── controller/
│   ├── AuthControllerTest.java
│   ├── StudioControllerTest.java
│   └── BookingControllerTest.java
├── service/
│   ├── AuthServiceTest.java
│   ├── StudioServiceTest.java
│   └── BookingServiceTest.java
├── repository/
│   ├── UserRepositoryTest.java
│   └── StudioRepositoryTest.java
└── integration/
    ├── AuthIntegrationTest.java
    └── StudioBookingIntegrationTest.java
```

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AuthServiceTest

# Run integration tests
mvn test -Dtest=*IntegrationTest

# Generate test coverage report
mvn jacoco:report
```

## Deployment & DevOps

### Docker Configuration
```dockerfile
# Dockerfile
FROM openjdk:17-jdk-slim

VOLUME /tmp

ARG JAR_FILE=target/studio-booking-service-*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Docker Compose
```yaml
# docker-compose.yml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DATABASE_URL=jdbc:postgresql://db:5432/studio_booking_db
    depends_on:
      - db
      - redis

  db:
    image: postgis/postgis:14-3.2
    environment:
      - POSTGRES_DB=studio_booking_db
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=password
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

volumes:
  postgres_data:
```

## Roadmap

### Phase 1: Foundation (Current)
- [x] Project setup with Spring Boot
- [x] Database schema design
- [ ] User authentication system
- [ ] Basic CRUD operations

### Phase 2: Core Features
- [ ] Studio management APIs
- [ ] Booking system implementation
- [ ] Payment gateway integration
- [ ] Geolocation search

### Phase 3: Enhanced Features
- [ ] Review & rating system
- [ ] Notification service
- [ ] Admin dashboard APIs
- [ ] Real-time features

### Phase 4: Production Ready
- [ ] Performance optimization
- [ ] Security hardening
- [ ] Comprehensive monitoring
- [ ] Scalability improvements

## Support & Documentation

### Additional Resources
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [PostGIS Documentation](https://postgis.net/docs/)
- [Liquibase Documentation](https://docs.liquibase.com/)

### Team Contacts
- **Backend Lead**: [Your Name]
- **Database Admin**: [DBA Team]
- **DevOps Engineer**: [DevOps Team]

---

*Last Updated: November 16, 2025*
*Version: 1.0.0*