# Synapse to Ballerina migration report

1 Synapse construct could not be automatically converted and was left as TODOs in the generated code. Each entry shows the source file and the original Synapse code; review and migrate them manually.

## Unsupported inbound endpoint parameter (1)

### `<parameter>` — inboundEndpoint.xml

Inbound endpoint 'PlainHttpInbound' parameter 'keystore' is not mapped to any Ballerina construct; manual conversion required.

```xml
<parameter name="keystore">repository/resources/security/wso2carbon.jks
                JKS
                wso2carbon
                wso2carbon</parameter>
```
