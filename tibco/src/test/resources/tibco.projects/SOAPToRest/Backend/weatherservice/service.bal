import ballerina/http;

service / on new http:Listener(8080) {
    resource function post weather(@http:Payload WeatherRequest weatherRequest) returns WeatherResponse {
        return {
            temperature: 25.5,
            windSpeed: 10.2,
            humidity: 65.0
        };
    }
}