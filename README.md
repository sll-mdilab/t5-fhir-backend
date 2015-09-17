# T5 FHIR Backend

## Introduction

This is a server application which provides a FHIR-based interface for retrieving physiological observation data from the T5 PoC database. It reads observations in the T5 PoC XML-format from an XCC XQuery-endpoint and makes them available as FHIR Observation-resource objects.

## Build

The application is written in Java version 8 and uses Gradle for automatic building and dependency management.
Assuming that Java EE 8 development kit is installed and exist on the PATH environment variable, the project can be built with the following command from the project root folder:

    ./gradlew build

This outputs a .war-file into the `build/lib` directory.

## Deployment

The build process produces a servlet contained in a .war-file which can be deployed on any compatible Java servlet container. It has been tested with Apache Tomcat 8.0.

The application uses the following environment variables:

* `T5_FHIR_DATABASE_HOST` - The IP/hostname of the database.
* `T5_FHIR_DATABASE_PORT` - Port number of XCC endpoint.
* `T5_FHIR_DATABASE_NAME` - Name of the database schema.
* `T5_FHIR_DATABASE_USER` - Database username.
* `T5_FHIR_DATABASE_PASSWORD` - Database password.
