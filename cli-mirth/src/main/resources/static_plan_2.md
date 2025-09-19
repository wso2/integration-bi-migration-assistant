# Mirth Connect to Ballerina Migration Plan

## Objective
Migrate the referenced Mirth Connect channel (`Hl7 Conversion`) to a Ballerina Integration project, identifying and categorizing the main processing phases, connectors, and custom components.

## Source Connector
- **Type:** TCP Listener (MLLP)
- **Port:** 6662
- **Data Type:** HL7V2
- **Custom JavaScript:**
    - Channel transformer contains a JavaScript step to convert HL7 PID fields to JSON and format date-of-birth using moment.js.

## Destination Connectors
1. **Fields Validation**
    - **Type:** JavaScript Writer
    - **Custom JavaScript:** Validates patient name and date-of-birth fields using regex and logs the transformed JSON object.
2. **MySQL Insert Query**
    - **Type:** Database Writer (MySQL)
    - **Custom Script:** SQL INSERT using mapped HL7 fields.

## Message Flow Outline
- Source connector receives HL7 over TCP/MLLP.
- HL7 message is mapped and transformed (custom JavaScript for JSON conversion).
- Message passes to validation (custom JavaScript for regex checks).
- On success, inserts patient record into MySQL DB (using mapped HL7 fields).

## Processing Phases
| Phase | Connector(s)/Component(s)              | Type         | Actions                                            |
|-------|----------------------------------------|--------------|----------------------------------------------------|
| 1     | Source Connector (TCP Listener, MLLP)  | Standard     | Receive HL7V2 message                              |
| 2     | Channel Transformer (Mapper & JS Step) | Custom       | Map HL7 segments, convert to JSON, format DOB      |
| 3     | Fields Validation (JS Writer)          | Custom       | Validate patient name & DOB, log transformed object |
| 4     | MySQL Insert Query (DB Writer)         | Standard/Custom| Insert patient record into MySQL                   |
| 5     | Postprocessing/Export                  | Standard     | Export/Store message, attachments, metadata         |

## Parallel vs Sequential
- All connectors are executed sequentially according to Mirth's channel message flow.
- No explicit parallel execution.

## Categorization Summary
- **Standard Components:** TCP Listener, MLLP, Database Writer, Export/Storage.
- **Custom Components:** Mapper steps and JavaScript steps for HL7-to-JSON conversion, validation, and logging.

---