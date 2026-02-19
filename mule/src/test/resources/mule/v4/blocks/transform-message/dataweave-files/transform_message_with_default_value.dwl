%dw 2.0
output application/json
---
{
  hasHomeDelivery: payload.hasHomeDelivery default "",
  isCompleted: payload.isCompleted default ""
}
