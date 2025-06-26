%dw 1.0
%output application/json
%input payload application/json
---
[1, 2, 3, 4] filter $ > 2
