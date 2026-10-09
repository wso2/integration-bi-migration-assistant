# Synapse to Ballerina migration report

1 Synapse construct could not be automatically converted and was left as TODOs in the generated code. Each entry shows the source file and the original Synapse code; review and migrate them manually.

## Unsupported inbound endpoint attribute (1)

### `<suspend>` — inboundEndpoint.xml

Inbound endpoint 'SuspendedHttpInbound' declares suspend="true", so Synapse would deploy it inactive; the generated Ballerina listener has no equivalent and starts accepting traffic immediately. Manual conversion required.

```xml
<inboundEndpoint xmlns="http://ws.apache.org/ns/synapse" name="SuspendedHttpInbound" onError="handleError" protocol="http" sequence="foo" suspend="true">
    <parameters>
        <parameter name="inbound.http.port">8085</parameter>
    </parameters>
</inboundEndpoint>
```
