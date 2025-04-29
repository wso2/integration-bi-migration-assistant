%dw 1.0
%output application/json
---
b: "admin123" replace /(\d+)/ with "ID"
