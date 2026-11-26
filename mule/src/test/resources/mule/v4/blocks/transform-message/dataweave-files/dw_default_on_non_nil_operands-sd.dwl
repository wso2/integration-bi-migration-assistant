%dw 2.0
output application/json
---
{
    "filtered": (payload.items filter $.active) default [],
    "joined": (payload.a ++ "|" ++ payload.b) default "",
    "nested": (payload.c default "") default "z",
    "nullFallback": (payload.d default null) default "y"
}
