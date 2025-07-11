%dw 2.0
output application/json
---
{
  currency: "USD"
} when payload.country == "USA"
otherwise
{
      currency: "EUR"
}
