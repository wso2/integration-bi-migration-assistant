%dw 2.0
output application/json
---
{
    a: payload.x map $$,
    b: payload.y map $$
}
