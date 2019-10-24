# EDUC-PEN-API

## Build Setup

``` bash
#Install dependencies and build application
mvn clean install

#Run application with local properties
mvn -Dspring.profiles.active=dev spring-boot:run

#Run application with default properties
mvn spring-boot:run