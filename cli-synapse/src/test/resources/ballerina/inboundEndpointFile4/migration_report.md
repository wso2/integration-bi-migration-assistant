# Synapse to Ballerina migration report

14 Synapse constructs could not be automatically converted and were left as TODOs in the generated code. Each entry shows the source file and the original Synapse code; review and migrate them manually.

## Unsupported mediator (3)

### `<log>` — FileErrorSequence.xml

Mediator not supported; manual conversion required.

```xml
<log category="ERROR" xmlns="http://ws.apache.org/ns/synapse">
        <message>File processing failed: ${properties.synapse.ERROR_MESSAGE}</message>
    </log>
```

### `<log>` — FileProcessSequence.xml

Mediator not supported; manual conversion required.

```xml
<log category="INFO" logFullPayload="true" xmlns="http://ws.apache.org/ns/synapse">
        <message>File received and processing complete</message>
    </log>
```

### `<drop>` — FileProcessSequence.xml

Mediator not supported; manual conversion required.

```xml
<drop xmlns="http://ws.apache.org/ns/synapse"/>
```

## Unsupported inbound endpoint parameter (11)

### `<parameter>` — inboundEndpoint.xml

Inbound endpoint 'FileInboundEndpoint' parameter 'scheduleType' is not mapped to any Ballerina construct; manual conversion required.

```xml
<parameter name="scheduleType">Polling</parameter>
```

### `<parameter>` — inboundEndpoint.xml

Inbound endpoint 'FileInboundEndpoint' parameter 'interval' is not mapped to any Ballerina construct; manual conversion required.

```xml
<parameter name="interval">5000</parameter>
```

### `<parameter>` — inboundEndpoint.xml

Inbound endpoint 'FileInboundEndpoint' parameter 'cronExpression' is not mapped to any Ballerina construct; manual conversion required.

```xml
<parameter name="cronExpression"></parameter>
```

### `<parameter>` — inboundEndpoint.xml

Inbound endpoint 'FileInboundEndpoint' parameter 'fileThrottlingType' is not mapped to any Ballerina construct; manual conversion required.

```xml
<parameter name="fileThrottlingType">Count</parameter>
```

### `<parameter>` — inboundEndpoint.xml

Inbound endpoint 'FileInboundEndpoint' parameter 'transport.vfs.FileProcessCount' is not mapped to any Ballerina construct; manual conversion required.

```xml
<parameter name="transport.vfs.FileProcessCount">10</parameter>
```

### `<parameter>` — inboundEndpoint.xml

Inbound endpoint 'FileInboundEndpoint' parameter 'transport.vfs.FileProcessInterval' is not mapped to any Ballerina construct; manual conversion required.

```xml
<parameter name="transport.vfs.FileProcessInterval">0</parameter>
```

### `<parameter>` — inboundEndpoint.xml

Inbound endpoint 'FileInboundEndpoint' parameter 'sequential' is not mapped to any Ballerina construct; manual conversion required.

```xml
<parameter name="sequential">true</parameter>
```

### `<parameter>` — inboundEndpoint.xml

Inbound endpoint 'FileInboundEndpoint' parameter 'coordination' is not mapped to any Ballerina construct; manual conversion required.

```xml
<parameter name="coordination">true</parameter>
```

### `<parameter>` — inboundEndpoint.xml

Inbound endpoint 'FileInboundEndpoint' parameter 'transport.vfs.FileNamePattern' is not mapped to any Ballerina construct; manual conversion required.

```xml
<parameter name="transport.vfs.FileNamePattern">.*\.txt</parameter>
```

### `<parameter>` — inboundEndpoint.xml

Inbound endpoint 'FileInboundEndpoint' parameter 'transport.vfs.ActionAfterFailure' is not mapped to any Ballerina construct; manual conversion required.

```xml
<parameter name="transport.vfs.ActionAfterFailure">MOVE</parameter>
```

### `<parameter>` — inboundEndpoint.xml

Inbound endpoint 'FileInboundEndpoint' parameter 'transport.vfs.MoveAfterFailure' is not mapped to any Ballerina construct; manual conversion required.

```xml
<parameter name="transport.vfs.MoveAfterFailure">file:///C:/projects/Test/inbound/failed</parameter>
```
