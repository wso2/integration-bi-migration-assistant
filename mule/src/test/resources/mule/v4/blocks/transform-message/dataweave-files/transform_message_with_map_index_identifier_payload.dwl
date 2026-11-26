%dw 2.0
output application/json
---
payload.items map {
    value: $,
    position: $$
}
