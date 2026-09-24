# Synapse to Ballerina migration report

1 Synapse construct could not be automatically converted and was left as TODOs in the generated code. Each entry shows the source file and the original Synapse code; review and migrate them manually.

## Unresolved fault sequence (1)

### `<onError>` — inboundEndpoint.xml

Referenced fault sequence 'missingFaultSeq' was not found among the converted artifacts; falling back to the default error handler.

```xml
onError="missingFaultSeq"
```
