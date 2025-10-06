# Studio Booking Service

A Spring Boot REST API service for managing studio bookings similar to Playo for booking badminton courts.

## Features

- List available studios and courts
- Filter studios by city
- Filter courts by studio
- Create, update, and delete studios and courts (Admin functionality)
- Database integration with PostgreSQL
- Liquibase for database schema management

## Tech Stack

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- PostgreSQL
- Liquibase
- Maven

## Database Schema

The application uses the following database tables:

1. **studios** - Stores information about studios
2. **courts** - Stores information about courts within studios
3. **bookings** - Stores booking information for courts

## API Endpoints

### Studios

- `GET /api/studios` - Get all studios
- `GET /api/studios/active` - Get all active studios
- `GET /api/studios/city/{city}` - Get studios by city
- `GET /api/studios/{id}` - Get studio by ID
- `POST /api/studios` - Create a new studio
- `PUT /api/studios/{id}` - Update an existing studio
- `DELETE /api/studios/{id}` - Delete a studio

### Courts

- `GET /api/courts` - Get all courts
- `GET /api/courts/studio/{studioId}` - Get courts by studio ID
- `GET /api/courts/studio/{studioId}/available` - Get available courts by studio ID
- `GET /api/courts/{id}` - Get court by ID
- `POST /api/courts` - Create a new court
- `PUT /api/courts/{id}` - Update an existing court
- `DELETE /api/courts/{id}` - Delete a court

## Setup and Running

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 12+

### Database Setup

1. Create a PostgreSQL database named `studiobooking`:
   ```sql
   CREATE DATABASE studiobooking;
   ```

2. Update database configuration in `application.properties` if needed.

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run: `mvn spring-boot:run`

The application will start on port 8080 by default, and Liquibase will automatically create the schema and insert sample data.

## Sample Data

The application comes with sample data for testing:
- 3 studios with different locations
- 5 courts distributed across these studios

## Database Insertion Queries

For manual database insertion, you can use the following queries:

```sql
-- Insert a new studio
INSERT INTO studios (name, address, city, state, postal_code, contact_phone, contact_email, rating, opening_time, closing_time, is_active)
VALUES ('Studio Name', 'Studio Address', 'City', 'State', 'Postal Code', 'Contact Phone', 'email@example.com', 4.5, '06:00:00', '22:00:00', true);

-- Insert a new court
INSERT INTO courts (studio_id, name, court_type, hourly_rate, is_available, capacity, amenities)
VALUES (1, 'Court Name', 'BADMINTON', 500.00, true, 4, 'Air Conditioned, Wooden Flooring');

-- Insert a new booking
INSERT INTO bookings (court_id, user_id, booking_date, start_time, end_time, status, amount_paid, payment_status)
VALUES (1, 1, '2025-10-07', '10:00:00', '11:00:00', 'CONFIRMED', 500.00, 'PAID');
```