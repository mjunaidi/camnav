CamNav
======

CamNav is a back-end appllication developed for another mobile app which works as its front-end. It can be used to store location data such as latitude, longitude and their associate information.

Requirements
============

* JDK1.7 or Java 7
* Maven 3.x.x

Usage
=====

Run `mvn clean install` to compile the project.

Run `mvn jetty:run` to start the project.

Default port number is `8080`.

Access the project at http://localhost:8080/

API test at http://localhost:8080/location/api/test