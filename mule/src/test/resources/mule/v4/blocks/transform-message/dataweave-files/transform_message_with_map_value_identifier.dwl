%dw 2.0
output application/json
---
[1, 2, 3, 4] map ($ + 1)
