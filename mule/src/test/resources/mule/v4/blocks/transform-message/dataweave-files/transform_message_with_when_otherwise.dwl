%dw 1.0
%output application/json
%input payload application/json
---
{
  currency: "USD"
} when payload.country == "USA"
otherwise
{
      currency: "EUR"
}
