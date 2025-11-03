%dw 2.0
output application/json
---
{
	name: payload.name default "Unknown"
}

