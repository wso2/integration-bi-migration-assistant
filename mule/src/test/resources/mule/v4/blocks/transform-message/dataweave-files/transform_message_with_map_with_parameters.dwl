%dw 2.0
output application/json
---
["john", "peter", "matt"] map ((firstName, position) -> position ++ ":" ++ upper(firstName))
