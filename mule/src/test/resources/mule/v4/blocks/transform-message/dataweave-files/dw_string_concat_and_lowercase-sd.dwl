%dw 2.0
output application/json
---
lower(
  Mule::p('secure::xref.orderFiltering.entityApiName')
  ++ "|" ++ vars.sourceApi
  ++ "|" ++ "MyOrderType"
  ++ "|" ++ ""
  ++ "|"
)
