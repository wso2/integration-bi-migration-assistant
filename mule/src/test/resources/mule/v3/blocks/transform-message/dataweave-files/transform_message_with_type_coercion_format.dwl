%dw 1.0
%output application/json
---
{
    a: 1 as :string {format: "##,#"},
    b: now as :string {format: "yyyy-MM-dd"},
    c: true as :string
}
