# Sas code test


## Prerequisites
Java 21 and docker

## How to run
Start the docker compose, run the application and send a GET request to http://localhost:8080/customer-data/{customerId}
Filling out customerId with 1 will return a full user, 2 will return a partial content response with missing account information and any other id will return 404.

## Tests
Integration tests are located in the CustomerDataControllerTest class, they cover the cases mentioned above.

## Tracking
To track the underlying soap system, look in the logs for the docker compose. I recommend running docker compose in Intellij for an easy overview.
To track the api look in the application logs, I reduced the log level to debug for easier visibility(I would not normally do this).
