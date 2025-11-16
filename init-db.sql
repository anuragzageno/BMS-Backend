-- Initialize the studiobooking database
-- This script runs when the PostgreSQL container starts for the first time

-- The database should already be created by POSTGRES_DB, but let's ensure it exists
-- Connect to the default postgres database first
\c postgres;

-- Create user if not exists (though postgres should already exist)
DO
$$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_catalog.pg_user WHERE usename = 'postgres') THEN
      CREATE USER postgres WITH SUPERUSER PASSWORD 'password';
   END IF;
END
$$;

-- Create database if it doesn't exist
SELECT 'CREATE DATABASE studiobooking OWNER postgres'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'studiobooking')\gexec

-- Connect to the studiobooking database
\c studiobooking;

-- Grant all privileges
GRANT ALL PRIVILEGES ON DATABASE studiobooking TO postgres;
GRANT ALL ON SCHEMA public TO postgres;