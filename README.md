# WSCBS
## How to run it?
The auth service will listen on ::8080
The url service will listen on :8000
### UrlSerice


There is a jar file called urlservice-0.0.1-SNAPSHOT.jar in urlservice/target

Run `java -jar urlservice-0.0.1-SNAPSHOT.jar --spring.config.location=<config file>`
you can find the config file in urlservice/src/main/resources/application.properties

### AuthService

The auth service's jar file is in authservice/target

you can find the config file in authservice/src/main/resources/application.properties

use similar command to start this service


