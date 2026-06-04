import ballerina/http;
import ballerina/log;

// Converted from Synapse REST API `HealthCheckApi` (context: /healthcheck).
service /healthcheck on new http:Listener(8290) {

    resource function get status() returns json {
        log:printInfo("Health check requested");
        json response = {"status": "UP"};
        return response;
    }
}
