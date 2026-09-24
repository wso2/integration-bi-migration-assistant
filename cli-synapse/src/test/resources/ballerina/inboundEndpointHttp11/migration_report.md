# Synapse to Ballerina migration report

1 Synapse construct could not be automatically converted and was left as TODOs in the generated code. Each entry shows the source file and the original Synapse code; review and migrate them manually.

## Unsupported inbound endpoint parameter (1)

### `<parameter>` — inbound.xml

Inbound endpoint 'ComplexPatternInbound' parameter 'dispatch.filter.pattern' took too long to evaluate against this project's <api> resources (the expression may be pathologically complex); no <api> can be matched against it, so this endpoint's listener is left with no service at all. Manual conversion required.

```xml
<parameter name="dispatch.filter.pattern">/(((((a*)*)*)*)*)b</parameter>
```
