%dw 2.0
output application/json
---
{
    currency: if (payload.country == "USA") "USD"
              else if (payload.country == "UK") "GBP"
              else "EUR"
}
