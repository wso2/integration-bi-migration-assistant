%dw 1.0
%output application/json
---
{
  mydate1: |2005-06-02T15:10:16Z| as :number {unit: "seconds"},
  mydate2: |2005-06-02T15:10:16Z| as :number {unit: "milliseconds"},
  mydate3: |2005-06-02T15:10:16Z| as :number
}
