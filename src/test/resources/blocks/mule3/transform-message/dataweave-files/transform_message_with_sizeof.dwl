%dw 1.0
%output application/json
%input payload application/json
---
{
	hail1: sizeOf [1,2,3,4]
}
