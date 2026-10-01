import ballerina/http;

http:Client Notify_HttpClientResource = checkpanic new (string `https://${Resources_Notify_HttpClient_Host}:${Resources_Notify_HttpClient_Port}`);
http:Client Secure_HttpClientResource = checkpanic new (string `${Resources_Secure_HttpClient_UseSSL ? "https" : "http"}://sessions.example.com:80`);
http:Client Status_HttpClientResource = checkpanic new (string `http://status.example.com:8080`);
