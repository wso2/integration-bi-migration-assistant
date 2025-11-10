## Tool Overview
The `migrate-mirth` tool enables the conversion of Mirth Connect Channel into Ballerina packages compatible with the [WSO2 Ballerina Integrator](https://wso2.com/integrator/ballerina-integrator).
It accepts a Mirth Channel XML file as input and produces an equivalent Ballerina Integrator application.

## Installation

To pull the `migrate-mirth` tool from Ballerina Central, run the following command:
```bash
$ bal tool pull migrate-mirth
```

## Usage

### Command Syntax

```bash
$ bal migrate-mirth <source-project-file-path> [-o|--out <output-directory>] [-v|--verbose]
```

### Parameters

- **source-project-file-path** - *Required*. The path to the directory which contains multiple Logic App JSON files 
  or a single Logic App JSON file to be migrated.
- **-o or --out** - *Optional*. The directory where the new Ballerina package will be created. If not provided, the new Ballerina package is created in the same directory as the source file.
- **-v or --verbose** - *Optional*. Enable verbose output during conversion.

### Examples

#### Convert a Mirth Connect Channel XML file

```bash
$ bal migrate-mirth /path/to/mirth-channel.xml
```

This will create a Ballerina package in the same directory as the input JSON file.

#### Convert a Mirth Connect Channel XML file with a Custom Output Location

```bash
$ bal migrate-mirth /path/to/mirth-channel.xml --out /path/to/output-dir
```

This will create a Ballerina package at `/path/to/output-dir`.

#### Convert a Mirth Connect Channel XML file with Verbose Output

```bash
$ bal migrate-mirth /path/to/mirth-channel.xml --verbose
```

This will convert the Logic App with detailed logging during the conversion process.

## Output

- For a Mirth Channel XML file input: A new Ballerina package is created with the same name as the XML file, appended
  with a `_ballerina` suffix.

## Supported Features

The migration tool supports the following Mirth Connect Channel features:

### Core Workflow Components
- **Sources**: HTTP requests, scheduled triggers, event-based triggers, TCP listeners and File watchers
- **Connectors**: List of 3rd party integration connectors, FHIR/HL7 connectors
- **Variables**: Workflow variables and their transformations
- **Custom Code Templates**: Custom javascript functions can be converted to Ballerina functions

### Control Flow
- **Conditional Logic**: If-else conditions and switch statements
- **Loops**: For-each loops and until loops
- **Parallel Branches**: Concurrent execution paths
- **Scopes**: Grouping actions and error handling

### Data Operations
- **Data Transformation**: XML/JSON parsing, composition, and manipulation
- **Filtering**: Filter massages based on conditions
- **Variable Operations**: Initialize, set, increment, and append operations
- **Array Operations**: Filtering, mapping, and aggregation
- **String Operations**: Concatenation, substring, and formatting

### Error Handling
- **Try-Catch Blocks**: Exception handling and error propagation
- **Retry Policies**: Configurable retry mechanisms
- **Timeout Settings**: Action timeout configurations
- **Dead Letter Queue**: Configurable queue for failed messages

### Integration Patterns
- **REST API Calls**: HTTP client operations with authentication
- **Message Routing**: Content-based routing and message transformation
- **Protocol Translation**: Converting between different message formats

## Limitations

While the migration tool provides comprehensive conversion capabilities, there are some limitations to be aware of:

### Platform-Specific Features
- **Mirth-specific Components**: Some Mirth-native connectors may not have direct Ballerina equivalents
- **Mirth Connect Runtime Features**: Some runtime-specific features may need manual implementation

### Advanced Scenarios
- **Complex Custom Connectors**: Custom connectors with complex authentication flows may require manual adaptation
- **Stateful Workflows**: Long-running stateful workflows may need additional consideration
- **Large-scale Parallel Processing**: Extremely high-concurrency scenarios may require performance tuning

### AI-Generated Code Considerations
- **Code Review Required**: Generated code should be reviewed and tested before production use
- **Performance Optimization**: Generated code may require optimization for specific use cases
- **Security Validation**: Security configurations and credentials should be validated manually

### Post-Migration Requirements
- **Testing**: Comprehensive testing of converted workflows is recommended
- **Configuration**: Environment-specific configurations need to be set up manually
- **Monitoring**: Logging and monitoring setup may require additional configuration
