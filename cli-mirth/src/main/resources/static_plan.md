# Mirth Connect to Ballerina Migration Plan: Hl7 Conversion Channel

## Channel Overview

**Channel Name:** Hl7 Conversion  
**Channel ID:** 45fb1e66-c429-4fa3-8c32-5eb17e0fea06  
**Description:** This channel processes HL7 messages via TCP listener, extracts patient data, validates it, and stores it to MySQL database.

## Message Flow Analysis

### Source Connector
- **Type:** TCP Listener (MLLP)
- **Host:** 0.0.0.0
- **Port:** 6662
- **Protocol:** MLLP (Minimum Lower Layer Protocol)
- **Data Type:** HL7v2

### Destination Connectors
1. **Fields Validation** (JavaScript Writer) - metaDataId: 1
2. **MySQL insert query** (Database Writer) - metaDataId: 3 (currently disabled)

## Processing Phases

### Phase 1: Inbound Processing
| Action/Component | Type | Execution | Description |
|------------------|------|-----------|-------------|
| TCP Listener | Standard | Sequential | Receives HL7 messages via MLLP on port 6662 |
| HL7 Message Parsing | Standard | Sequential | Parses incoming HL7v2 messages |

### Phase 2: Data Transformation
| Action/Component | Type | Execution | Description |
|------------------|------|-----------|-------------|
| Message Code Extraction | Standard (Mapper) | Sequential | Extracts MSH.9.1 (message type) |
| Message Trigger Event Extraction | Standard (Mapper) | Sequential | Extracts MSH.9.2 (trigger event) |
| First Name Extraction | Standard (Mapper) | Sequential | Extracts PID.5.2 (patient first name) |
| Last Name Extraction | Standard (Mapper) | Sequential | Extracts PID.5.1 (patient last name) |
| Date of Birth Extraction | Standard (Mapper) | Sequential | Extracts PID.7.1 (patient DOB) |

### Phase 3: Data Processing & JSON Conversion
| Action/Component | Type | Execution | Description |
|------------------|------|-----------|-------------|
| HL7 to JSON Conversion | Custom (JavaScript) | Sequential | Converts extracted HL7 data to JSON object |
| Date Format Transformation | Custom (JavaScript) | Sequential | Converts YYYYMMDD to YYYY-MM-DD using moment.js |
| Channel Map Storage | Custom (JavaScript) | Sequential | Stores JSON object in channel map as 'hl7_json_object' |

### Phase 4: Data Validation
| Action/Component | Type | Execution | Description |
|------------------|------|-----------|-------------|
| Full Name Validation | Custom (JavaScript) | Sequential | Validates full name using regex pattern |
| Date of Birth Validation | Custom (JavaScript) | Sequential | Validates DOB format using regex pattern |
| JSON Object Logging | Custom (JavaScript) | Sequential | Logs the converted JSON object |

### Phase 5: Database Storage (Currently Disabled)
| Action/Component | Type | Execution | Description |
|------------------|------|-----------|-------------|
| MySQL Insert Query | Standard (Database) | Sequential | Inserts patient data into MySQL database |

## Technical Components Analysis

### Source Transformer Components
1. **Variable Mapping Steps (Standard):**
    - Message code: `msg['MSH']['MSH.9']['MSH.9.1'].toString().trim()`
    - Message Trigger Event: `msg['MSH']['MSH.9']['MSH.9.2'].toString().trim()`
    - First Name: `msg['PID']['PID.5']['PID.5.2'].toString().trim()`
    - Last Name: `msg['PID']['PID.5']['PID.5.1'].toString().trim()`
    - Date Of Birth: `msg['PID']['PID.7']['PID.7.1'].toString().trim()`

2. **JavaScript Conversion Step (Custom):**
   ```javascript
   var hl7JsonObject = {};
   hl7JsonObject.first_name = msg['PID']['PID.5']['PID.5.2'].toString();
   hl7JsonObject.last_name = msg['PID']['PID.5']['PID.5.1'].toString();
   hl7JsonObject.date_of_birth = moment(msg['PID']['PID.7']['PID.7.1'].toString(), 'YYYYMMDD').format('YYYY-MM-DD');
   channelMap.put('hl7_json_object', hl7JsonObject);
   ```

### Destination 1: Fields Validation Components
1. **JavaScript Validation Script (Custom):**
   ```javascript
   const fullName = $('Last Name') + " " + $('First Name');
   const dateOfBirth = $('Date Of Birth');
   
   const fullNamePattern = /^([a-zA-Z]{2,}\s[a-zA-Z]{1,}'?-?[a-zA-Z]{2,}\s?([a-zA-Z]{1,})?)/g;
   const dateOfBirthPattern = /([12]\d{3}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01]))/g;
   
   const isFullNameValid = fullName.match(fullNamePattern);
   const isDateOfBirthValid = dateOfBirth.match(dateOfBirthPattern);
   
   function isNotNull (value) {
       return value !== null && value.length === 1;
   }
   
   logger.info("Our JSON object in plain text: " + JSON.stringify($('hl7_json_object')));
   ```

### Destination 2: MySQL Insert Components
1. **Database Configuration:**
    - Driver: `com.mysql.cj.jdbc.Driver`
    - URL: `jdbc:mysql://localhost:3306/mydatabase`
    - Username: `root`
    - Password: `root`

2. **SQL Query:**
   ```sql
   INSERT INTO patients (firstname, lastname, dateofbirth)
   VALUES (${maps.get('First Name')}, ${maps.get('Last Name')}, ${maps.get('Date Of Birth')})
   ```

## Dependencies and Libraries

### External Libraries Used
1. **Moment.js** - Used for date formatting and manipulation
    - Version: 2.29.1
    - Purpose: Converting YYYYMMDD format to YYYY-MM-DD

## Configuration Requirements

### Network Configuration
- **Inbound Port:** 6662
- **Protocol:** MLLP over TCP
- **Host Binding:** 0.0.0.0 (all interfaces)

### Database Configuration
- **Database Type:** MySQL
- **Host:** localhost
- **Port:** 3306
- **Database Name:** mydatabase
- **Table:** patients
- **Required Columns:** firstname, lastname, dateofbirth

### Message Processing Settings
- **Message Storage Mode:** PRODUCTION
- **Queue Buffer Size:** 1000
- **Processing Threads:** 1
- **Retry Settings:** 0 retries with 10 second intervals

## Data Flow Summary

```
HL7 Message (MLLP/TCP) 
    ↓
Parse HL7v2 Message
    ↓
Extract Patient Data (MSH, PID segments)
    ↓
Convert to JSON Object
    ↓
Validate Data (Name & DOB patterns)
    ↓
Log Processing Results
    ↓
[Optional] Store to MySQL Database
```

## Migration Considerations for Ballerina

1. **HL7 Processing:** Need Ballerina HL7 library for message parsing
2. **MLLP Protocol:** Implement MLLP framing for TCP communication
3. **Date Processing:** Replace moment.js with Ballerina time library
4. **Validation Logic:** Convert JavaScript regex validation to Ballerina
5. **Database Integration:** Use Ballerina MySQL connector
6. **Logging:** Implement structured logging with Ballerina log module
7. **Error Handling:** Implement proper error handling and retry mechanisms
8. **Configuration Management:** Use Ballerina configurable variables for environment-specific settings

## Key Integration Points

1. **Input:** TCP listener on port 6662 with MLLP protocol support
2. **Processing:** HL7 message parsing, data extraction, and validation
3. **Output:** JSON object creation and optional database storage
4. **Monitoring:** Logging of validation results and processed data