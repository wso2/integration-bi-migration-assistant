%dw 2.0
output application/json
---
(payload.ChangeLog.ModTypes.Order map (item) -> {
  entity: Mule::p('secure::xref.orderFiltering.entityApiName'),
  value: lower(
    Mule::p('secure::xref.orderFiltering.entityApiName')
    ++ "|" ++ vars.sourceApi
    ++ "|" ++ item
    ++ "|" ++ ""
    ++ "|"
  ) default "",
  domain: Mule::p('secure::xref.orderFiltering.domainNameOrder'),
  defaultValue: false
})
default [
  {
    entity: Mule::p('secure::xref.orderFiltering.entityApiName'),
    value: "",
    domain: Mule::p('secure::xref.orderFiltering.domainNameOrder'),
    defaultValue: false
  }
]
