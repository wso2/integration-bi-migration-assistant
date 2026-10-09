# Synapse to Ballerina migration report

11 Synapse constructs could not be automatically converted and were left as TODOs in the generated code. Each entry shows the source file and the original Synapse code; review and migrate them manually.

## Unsupported mediator (4)

### `<log>` — JMSErrorSeq.xml

Mediator not supported; manual conversion required.

```xml
<log category="ERROR" xmlns="http://ws.apache.org/ns/synapse">
        <message>JMS error: ${properties.synapse.ERROR_CODE} - ${properties.synapse.ERROR_MESSAGE}</message>
    </log>
```

### `<drop>` — JMSErrorSeq.xml

Mediator not supported; manual conversion required.

```xml
<drop xmlns="http://ws.apache.org/ns/synapse"/>
```

### `<log>` — JMSInjectingSeq.xml

Mediator not supported; manual conversion required.

```xml
<log category="INFO" logFullPayload="true" xmlns="http://ws.apache.org/ns/synapse">
        <message>JMS message received: ${payload}</message>
    </log>
```

### `<drop>` — JMSInjectingSeq.xml

Mediator not supported; manual conversion required.

```xml
<drop xmlns="http://ws.apache.org/ns/synapse"/>
```

## Unsupported inbound endpoint parameter (7)

### `<parameter name="interval">` — inboundEndpoint.xml

Inbound endpoint 'JMSInboundEndpoint' parameter 'interval' is not mapped to any Ballerina construct; manual conversion required.

```xml
<parameter name="interval">1000</parameter>
```

### `<parameter name="sequential">` — inboundEndpoint.xml

Inbound endpoint 'JMSInboundEndpoint' parameter 'sequential' is not mapped to any Ballerina construct; manual conversion required.

```xml
<parameter name="sequential">true</parameter>
```

### `<parameter name="coordination">` — inboundEndpoint.xml

Inbound endpoint 'JMSInboundEndpoint' parameter 'coordination' is not mapped to any Ballerina construct; manual conversion required.

```xml
<parameter name="coordination">true</parameter>
```

### `<parameter name="transport.jms.ConnectionFactoryJNDIName">` — inboundEndpoint.xml

Inbound endpoint 'JMSInboundEndpoint' parameter 'transport.jms.ConnectionFactoryJNDIName' is not mapped to any Ballerina construct; manual conversion required.

```xml
<parameter name="transport.jms.ConnectionFactoryJNDIName">QueueConnectionFactory</parameter>
```

### `<parameter name="transport.jms.SessionTransacted">` — inboundEndpoint.xml

Inbound endpoint 'JMSInboundEndpoint' parameter 'transport.jms.SessionTransacted' is not mapped to any Ballerina construct; manual conversion required.

```xml
<parameter name="transport.jms.SessionTransacted">false</parameter>
```

### `<parameter name="transport.jms.SessionAcknowledgement">` — inboundEndpoint.xml

Inbound endpoint 'JMSInboundEndpoint' parameter 'transport.jms.SessionAcknowledgement' is not mapped to any Ballerina construct; manual conversion required.

```xml
<parameter name="transport.jms.SessionAcknowledgement">AUTO_ACKNOWLEDGE</parameter>
```

### `<parameter name="transport.jms.ContentType">` — inboundEndpoint.xml

Inbound endpoint 'JMSInboundEndpoint' parameter 'transport.jms.ContentType' is not mapped to any Ballerina construct; manual conversion required.

```xml
<parameter name="transport.jms.ContentType">application/json</parameter>
```
