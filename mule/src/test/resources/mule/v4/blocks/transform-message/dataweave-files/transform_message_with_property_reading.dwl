%dw 2.0
output application/json
---
{
  someKey: Mule::p('secure::xref.example.someApiName'),
  anotherKey: Mule::p('secure::xref.example.someDomainName')
}
