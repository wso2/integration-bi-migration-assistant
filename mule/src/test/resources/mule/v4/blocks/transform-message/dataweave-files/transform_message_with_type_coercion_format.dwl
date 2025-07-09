%dw 2.0
output application/json
---
{
    a: 1 as String {format: "##,#"},
    b: now() as String {format: "yyyy-MM-dd"},
    c: true as String
}
