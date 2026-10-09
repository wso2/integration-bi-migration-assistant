# Synapse to Ballerina migration report

5 Synapse constructs could not be automatically converted and were left as TODOs in the generated code. Each entry shows the source file and the original Synapse code; review and migrate them manually.

## Unsupported inbound endpoint parameter (3)

### `<parameter name="truststore">` — inboundEndpoint.xml

Inbound endpoint 'SecureInboundEndpoint' parameter 'truststore' is not mapped to any Ballerina construct; manual conversion required.

```xml
<parameter name="truststore">repository/resources/security/client-truststore.jks
                JKS
                wso2carbon</parameter>
```

### `<parameter name="sequential">` — inboundEndpoint.xml

Inbound endpoint 'SecureInboundEndpoint' parameter 'sequential' is not mapped to any Ballerina construct; manual conversion required.

```xml
<parameter name="sequential">true</parameter>
```

### `<parameter name="coordination">` — inboundEndpoint.xml

Inbound endpoint 'SecureInboundEndpoint' parameter 'coordination' is not mapped to any Ballerina construct; manual conversion required.

```xml
<parameter name="coordination">true</parameter>
```

## Unresolved sequence (1)

### `<sequence>` — inboundEndpoint.xml

Referenced sequence 'FileProcessSequence' was not found among the converted artifacts; manual conversion required.

```xml
<sequence key="FileProcessSequence"/>
```

## Unresolved fault sequence (1)

### `<onError>` — inboundEndpoint.xml

Referenced fault sequence 'fault' was not found among the converted artifacts; falling back to the default error handler.

```xml
onError="fault"
```
