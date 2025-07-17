%dw 2.0
output application/json
input payload application/json
---
if (payload.country == "USA")
    { currency: "USD" }
else
    { currency: "EUR" }
