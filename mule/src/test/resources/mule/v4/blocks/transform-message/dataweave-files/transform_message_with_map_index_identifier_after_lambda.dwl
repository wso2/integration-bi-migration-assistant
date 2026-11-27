%dw 2.0
output application/json
---
payload.items map {
    doubled: [1, 2] map ($ + 1),
    value: $,
    position: $$
}
