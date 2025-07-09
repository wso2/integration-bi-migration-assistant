%dw 2.0
output application/json
---
{
  b: "admin123" replace /(\d+)/ with "ID"
}
