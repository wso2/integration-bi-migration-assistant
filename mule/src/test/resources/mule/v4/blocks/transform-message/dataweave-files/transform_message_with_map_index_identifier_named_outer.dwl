%dw 2.0
output application/json
---
payload map ((item, index) -> (item.kids map {outer: index, inner: $$}))
