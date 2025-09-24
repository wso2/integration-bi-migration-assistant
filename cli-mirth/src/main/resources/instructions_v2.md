# Mirth Connect to Ballerina Migration Guide

## Introduction

This migration guide is designed to help developers convert Mirth Connect channels to equivalent implementations using Ballerina's Pipeline module and healthcare libraries. It provides a comprehensive approach to recreate the integration patterns, transformations, and message routing capabilities of Mirth Connect within Ballerina's modern, type-safe programming environment.

This guide can be fed into an LLM along with a Mirth channel XML file to generate a complete Ballerina project that accomplishes the same integration tasks as the original Mirth channel.

## Ballerina Language Equivalents for Mirth Connect Components

| Mirth Connect Component | Ballerina Equivalent                     | Description |
|-------------------------|------------------------------------------|-------------|
| **Channel** | `pipeline:HandlerChain`                  | A complete message processing unit that handles the flow of messages through various components |
| **Source Connector** | HTTP Service, TCP Listener, File Watcher | Entry point for messages into the pipeline |
| **Filter** | `@pipeline:FilterConfig` function              | Functions that determine whether messages should continue processing |
| **Transformer** | `@pipeline:TransformerConfig` function         | Functions that modify message content or structure |
| **Preprocessor** | `@pipeline:ProcessorConfig` function     | Generic processing steps that can be applied before main transformation |
| **Destination Connector** | `@pipeline:DestinationConfig` function         | Functions that send messages to external systems |
| **Response Transformer** | Response handling in destination function | Logic to handle and transform responses from destination systems |
| **Channel Scripts** | Ballerina functions                      | Custom code for specialized logic |
| **Code Templates** | Ballerina functions, modules             | Reusable code components |
| **Variable Maps** | `MessageContext` properties              | Key-value store for sharing data between processors |
| **Message Storage** | `rabbitmq:MessageStore`        | Storage for messages that fail processing |
| **Attachment Handler** | Custom processors using byte[] handling  | Handling binary attachments in messages |
| **Queuing** | Retry configuration in destinations      | Automatic retries for failed message deliveries |
| **Batch Processing** | Array/collection processing in processors | Breaking down large messages into individual messages |
| **Error Handling** | Error return types, try-catch blocks     | Managing errors during message processing |

### Mirth Connect Protocol Connectors to Ballerina Equivalents

| Mirth Connector | Ballerina Implementation |
|-----------------|--------------------------|
| **HTTP Listener** | `ballerina/http` listener service |
| **HTTP Sender** | `ballerina/http` client |
| **File Reader** | `ballerina/file` watcher + io operations |
| **File Writer** | `ballerina/io` file operations |
| **Database Reader** | `ballerina/sql` + DB driver modules |
| **Database Writer** | `ballerina/sql` + DB driver modules |
| **TCP Listener** | `ballerina/tcp` listener |
| **TCP Sender** | `ballerina/tcp` client |
| **DICOM Listener/Sender** | `ballerina/tcp` listener |
| **JMS Listener/Sender** | `ballerinax/rabbitmq` or messaging clients |
| **Web Service Listener/Sender** | `ballerina/http` with SOAP handling |
| **SMTP Sender** | `ballerina/email` client |

### Mirth Connect Data Types to Ballerina Types

| Mirth Data Type | Ballerina Data Type |
|-----------------|---------------------|
| **HL7 v2.x** | `ballerinax/health.hl7v2` records |
| **XML** | `xml` native type |
| **JSON** | `json` native type |
| **EDI/X12** | Custom records or `wso2healthcare/health.x12.v005010x217.v278a3` |
| **DICOM** | `byte[]` or `string` |
| **Raw** | `byte[]` or `string` |
| **Delimited Text** | `string` with parsing functions |

## Migration Guidelines

### 1. Analyze Your Mirth Connect Channel

1. **Identify channel components**:
    - Source connector type and configuration
    - Filters and transformers in the processing chain
    - Destination connectors and their configurations
    - Custom scripts and code templates

2. **Map data flow**:
    - Document how messages flow through the channel
    - Identify variable maps and how they're used
    - Note error handling and response processing logic

### 2. Design Ballerina Pipeline Structure

1. **Define message types**:
    - Create Ballerina record types to represent your messages
    - Use constraint validations to enforce data integrity

2. **Create processing functions**:
    - Design filter functions to replace Mirth filters
    - Create transformer functions to replace Mirth transformers
    - Develop destination functions for message delivery

3. **Configure the handler chain**:
    - Arrange processors in the correct sequence
    - Set up failure handling and retry logic

### 3. Implement Message Sources

1. **HTTP Source**:
   ```ballerina
   service /api/v1 on new http:Listener(8080) {
       resource function post messages(http:Request request) returns http:Response|error {
           string payload = check request.getTextPayload();
           _ = start handlerChain.execute(payload);
           return http:ACCEPTED;
       }
   }
   ```

2. **File Source**:
   ```ballerina
   service "fileWatcher" on new file:Listener({ path: "/input", recursive: false }) {
       remote function onModify(file:FileEvent event) returns error? {
           string content = check io:fileReadString(event.name);
           _ = check handlerChain.execute(content);
       }
   }
   ```

3. **TCP/HL7 Source**:
   ```ballerina
   service on new tcp:Listener(port) {
       remote function onConnect(tcp:Caller caller) returns tcp:ConnectionService {
           return new HL7MessageService(handlerChain);
       }
   }
   
   class HL7MessageService {
       *tcp:ConnectionService;
       pipeline:HandlerChain handlerChain;
       
       function init(pipeline:HandlerChain handlerChain) {
           self.handlerChain = handlerChain;
       }
       
       remote function onBytes(tcp:Caller caller, readonly & byte[] data) returns tcp:Error? {
           string hl7Message = check string:fromBytes(data);
           // Extract message from MLLP if needed
           _ = start self.handlerChain.execute(hl7Message);
       }
   }
   ```

### 4. Implement Processing Functions

1. **Filter Function** (equivalent to Mirth Filter):
   ```ballerina
   @pipeline:FilterConfig {id: "patientFilter"}
   isolated function filterByPatientType(pipeline:MessageContext context) returns boolean|error {
       json message = check context.getContentWithType();
       string patientType = check message.patientType;
       return patientType == "INPATIENT" || patientType == "EMERGENCY";
   }
   ```

2. **Transformer Function** (equivalent to Mirth Transformer):
   ```ballerina
   @pipeline:TransformerConfig {id: "enrichPatientData"}
   isolated function enrichPatient(pipeline:MessageContext context) returns json|error {
       json patient = check context.getContentWithType();
       // Store data in the context (like Mirth's channel map)
       context.setProperty("patientId", check patient.id);
       
       // Call external system for additional data
       http:Client demographics = check new("http://demographics-service");
       json additionalInfo = check demographics->/patients/[check patient.id.toString()];
       
       // Merge and return data (transform)
       return {
           ...patient,
           ...additionalInfo
       };
   }
   ```

3. **Generic Processor** (equivalent to Mirth Preprocessor/Postprocessor):
   ```ballerina
   @pipeline:ProcessorConfig {id: "logMessage"}
   isolated function logMessageDetails(pipeline:MessageContext context) returns error? {
       string messageId = context.getId();
       log:printInfo("Processing message", id = messageId);
   }
   ```

### 5. Implement Destination Functions

1. **HTTP Destination** (equivalent to HTTP Sender):
   ```ballerina
   @pipeline:DestinationConfig {
       id: "forwardToEMR",
       retryConfig: {
           maxRetries: 3,
           retryInterval: 2
       }
   }
   isolated function sendToEMR(pipeline:MessageContext context) returns json|error {
       json payload = check context.getContentWithType();
       http:Client emrSystem = check new("https://emr-system/api");
       return emrSystem->/patients.post(payload);
   }
   ```

2. **File Destination** (equivalent to File Writer):
   ```ballerina
   @pipeline:DestinationConfig {id: "archiveMessage"}
   isolated function archiveToFile(pipeline:MessageContext context) returns error? {
       string content = check context.getContentWithType();
       string messageId = context.getId();
       check io:fileWriteString(string `./archive/${messageId}.txt`, content);
       return;
   }
   ```

3. **Multiple Destinations** (equivalent to Mirth multiple destinations):
   ```ballerina
   final pipeline:HandlerChain handlerChain = check new (
       name = "patientProcessingPipeline",
       processors = [
           validateMessage,
           filterByPatientType,
           enrichPatient
       ],
       destinations = [
           sendToEMR,
           archiveToFile,
           sendNotification
       ],
       failureStore = failureStore
   );
   ```

### 6. Handle Errors and Responses

1. **Failure Store** (equivalent to Mirth error handling):
   ```ballerina
   final rabbitmq:MessageStore failureStore = check new("message-failure-store");
   final rabbitmq:MessageStore deadLetterStore = check new("message-dead-letter-store");
   ```

2. **Response Processing** (equivalent to Mirth response transformer):
   ```ballerina
   @pipeline:DestinationConfig {id: "emrDestination"}
   isolated function sendToEMR(pipeline:MessageContext context) returns json|error {
       json payload = check context.getContentWithType();
       http:Client emrSystem = check new("https://emr-system/api");
       json response = check emrSystem->/patients.post(payload);
       
       // Process response like a Mirth response transformer
       string status = check response.status;
       if status != "SUCCESS" {
           return error("EMR rejected message: " + check response.reason.toString());
       }
       
       // Store response data in context for other destinations to use
       context.setProperty("emrResponseId", check response.id);
       return response;
   }
   ```

## Action Plan

### 1. Convert Message Types

1. Analyze Mirth message structures (HL7, XML, JSON, etc.)
2. Create equivalent Ballerina record types with proper constraints
3. Implement parsing/serialization functions if needed
4. State any assumptions only if data is missing in XML (keep this minimal and explicit).

### 2. Implement Processing Pipeline

1. Create processors that match Mirth filters and transformers
2. Implement destination functions for each Mirth destination connector
3. Configure the handler chain to match the Mirth channel flow
4. Use configurable variables for endpoints, credentials, ports, and timeouts. 
5. Demonstrate sample configuration (e.g., Config.toml keys) and how the code uses them. 
6. No secrets hard-coded in source.


### 3. Set Up Source Listeners

1. Implement service that listens on the same protocol as Mirth source
2. Configure appropriate message parsing based on data type
3. Pass incoming messages to the handler chain

### 4. Configure Error Handling and Monitoring

1. Set up failure store and dead letter queues
2. Implement replay mechanism for failed messages
3. Add logging and monitoring

### 5. Test

1. Test pipeline with sample messages matching Mirth test cases
2. Verify transformations and routing logic

## Example Migration Pattern (HL7 Processing)

For a typical HL7 processing channel:

1. Replace Mirth HL7 listener with Ballerina TCP listener with MLLP handling
2. Create record types representing HL7 message structures
3. Implement filters using `@pipeline:Filter` functions
4. Create transformers using `@pipeline:Transformer` functions
5. Configure destinations for each target system
6. Set up failure handling with appropriate retry logic

## Assumptions and clarifications policy:
- Do not invent behavior. 
- Prefer parameterization and documentation over conjecture. 
- If an essential element is truly missing in the XML and cannot be defaulted safely, list it under “Assumptions & Gaps” and proceed with a conservative, configurable default. 
- Keep the “Assumptions & Gaps” list short, precise, and tied to specific XML elements.
