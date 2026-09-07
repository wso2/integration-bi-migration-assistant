# Synapse to Ballerina migration report

2 Synapse constructs could not be automatically converted and were left as TODOs in the generated code. Each entry shows the source file and the original Synapse code; review and migrate them manually.

## Unsupported inbound endpoint parameter (2)

### `<parameter name="sequential">` — inbound.xml

Inbound endpoint 'DispatchInbound' parameter 'sequential' is not mapped to any Ballerina construct; manual conversion required.

```xml
<parameter name="sequential">true</parameter>
```

### `<parameter name="coordination">` — inbound.xml

Inbound endpoint 'DispatchInbound' parameter 'coordination' is not mapped to any Ballerina construct; manual conversion required.

```xml
<parameter name="coordination">true</parameter>
```
