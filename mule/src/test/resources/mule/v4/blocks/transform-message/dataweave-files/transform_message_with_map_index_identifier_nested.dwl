%dw 2.0
output application/json
---
payload.orders map {
    items: $.items map {pos: $$}
}
