%dw 1.0
%output application/json
%input payload application/json
---
{
	hail1: payload.resultSet1
}
