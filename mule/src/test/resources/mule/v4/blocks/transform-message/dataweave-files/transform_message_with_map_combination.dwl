%dw 2.0
output application/json
---
{
  users: ["john", "peter", "matt"] map upper($)
}
