%dw 2.0
output application/json
---
(payload.ChangeLog.ModTypes.QuantityDetail map {
    "entity": Mule::p('secure::xref.orderFiltering.entityApiName'),
    "value": lower(Mule::p('secure::xref.orderFiltering.entityApiName') ++ "|" ++ vars.sourceApi ++ "|" ++ ($ default "")) default "",
    "domain": Mule::p('secure::xref.orderFiltering.domianNameOrder'),
    "defaultValue": true
}
) default []
