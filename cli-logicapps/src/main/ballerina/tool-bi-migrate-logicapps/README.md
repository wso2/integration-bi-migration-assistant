## Tool Overview
The `migrate-logicapps` tool enables the conversion of [Azure Logic Apps](https://learn.microsoft.com/en-us/azure/logic-apps/logic-apps-overview) applications into Ballerina packages compatible with the [WSO2 Ballerina Integrator](https://wso2.com/integrator/ballerina-integrator).
It accepts either a project directory which contains multiple Logic App JSON files or a single Logic App JSON file as input and produces an equivalent Ballerina Integrator application.

## Installation

To pull the `migrate-logicapps` tool from Ballerina Central, run the following command:
```bash
$ bal tool pull migrate-logicapps
```

## Usage

### Command Syntax

```bash
$ bal migrate-logicapps <source-project-directory-or-file> [-o|--out <output-directory>] [-v|--verbose] [-m|--multi-root]
```

### Parameters

- **source-project-directory-or-file** - *Required*. The path to the directory which contains multiple Logic App JSON files 
  or a single Logic App JSON file to be migrated.
- **-o or --out** - *Optional*. The directory where the new Ballerina package will be created. If not provided,
    - For a project directory input, the new Ballerina package is created inside the source project directory.
    - For a single JSON file, the new Ballerina package is created in the same directory as the source file.
- **-v or --verbose** - *Optional*. Enable verbose output during conversion.
- **-m or --multi-root** - *Optional*. Treat each child directory as a separate project and convert all of them. The source must be a directory containing multiple Logic App JSON files.

### Project Structure Requirements

When using a project directory (which contains multiple Logic App JSON files) as input, the tool will look for JSON files in this location by default.
Make sure your project follows this structure.

* NOTE - Do not include any other JSON files other than the Logic Apps JSON files.

### Examples

#### Convert a Logic App JSON file

```bash
$ bal migrate-logicapps /path/to/logic-app-control-flow.json
```

This will create a Ballerina package in the same directory as the input JSON file.

#### Convert a Logic App JSON file with a Custom Output Location

```bash
$ bal migrate-logicapps /path/to/logic-app-control-flow.json --out /path/to/output-dir
```

This will create a Ballerina package at `/path/to/output-dir`.

#### Convert a Logic App JSON file with Verbose Output

```bash
$ bal migrate-logicapps /path/to/logic-app-control-flow.json --verbose
```

This will convert the Logic App with detailed logging during the conversion process.

#### Convert multiple Logic App files with Default Output Location

```bash
$ bal migrate-logicapps /path/to/logic-apps-file-directory --multi-root
```

This will create multiple Ballerina packages inside `/path/to/logic-apps-file-directory` directory for each Logic App file.

#### Convert multiple Logic App files with a Custom Output Location

```bash
$ bal migrate-logicapps /path/to/logic-apps-file-directory --out /path/to/output-dir --multi-root
```

This will create multiple Ballerina packages at `/path/to/output-dir` for each Logic App file.

## Output
- For a Logic App file directory input: Multiple Ballerina package are created for each Logic App file with the same name 
  as the JSON file, appended with a `_ballerina` suffix.
    - For example,  if the JSON file name is `my_control_flow`, the
      output ballerina package name will be `my_control_flow_ballerina`.

- For a Logic App JSON file input: A new Ballerina package is created with the same name as the JSON file, appended
  with a `_ballerina` suffix.

## Supported Features

The migration tool supports the following Azure Logic Apps features:

### Core Workflow Components
- **Triggers**: HTTP requests, scheduled triggers, and event-based triggers
- **Actions**: HTTP actions, data operations, and control flow actions
- **Connectors**: Common Azure connectors and third-party service integrations
- **Variables**: Workflow variables and their transformations
- **Expressions**: Logic Apps expressions and functions

### Control Flow
- **Conditional Logic**: If-else conditions and switch statements
- **Loops**: For-each loops and until loops
- **Parallel Branches**: Concurrent execution paths
- **Scopes**: Grouping actions and error handling

### Data Operations
- **Data Transformation**: JSON parsing, composition, and manipulation
- **Variable Operations**: Initialize, set, increment, and append operations
- **Array Operations**: Filtering, mapping, and aggregation
- **String Operations**: Concatenation, substring, and formatting

### Error Handling
- **Try-Catch Blocks**: Exception handling and error propagation
- **Retry Policies**: Configurable retry mechanisms
- **Timeout Settings**: Action timeout configurations

### Integration Patterns
- **REST API Calls**: HTTP client operations with authentication
- **Message Routing**: Content-based routing and message transformation
- **Protocol Translation**: Converting between different message formats

## Limitations

While the migration tool provides comprehensive conversion capabilities, there are some limitations to be aware of:

### Platform-Specific Features
- **Azure-specific Connectors**: Some Azure-native connectors may not have direct Ballerina equivalents
- **Logic Apps Runtime Features**: Some runtime-specific features may need manual implementation

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
