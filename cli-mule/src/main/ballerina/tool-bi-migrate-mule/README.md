## Tool Overview
The `migrate-mule` tool enables the conversion of [MuleSoft](https://www.mulesoft.com) applications into WSO2 Ballerina Integrator
applications by generating the corresponding Ballerina packages. It accepts either a MuleSoft project directory or a
standalone Mule XML file as input and produces an equivalent Ballerina Integrator application.

## Installation

To pull the `migrate-mule` tool from Ballerina Central, run the following command:
```bash
$ bal tool pull migrate-mule
```

## Usage

### Command Syntax

```bash
$ bal migrate-mule <source-project-directory-or-file> [-o|--out <output-directory>]
```

### Parameters

- **source-project-directory-or-file** - *Required*. The path to the MuleSoft project directory or a standalone Mule
  XML file to be migrated.
- **-o or --out** - *Optional*. The directory where the new Ballerina package will be created. If not provided,
    - For a project directory input, the new Ballerina package is created inside the source project directory.
    - For a standalone XML file, the new Ballerina package is created in the same directory as the source file.

### Project Structure Requirements

The migration tool expects a standard MuleSoft project hierarchy with configuration XML files located under:
```muleProjectPath/src/main/app```.

When using a MuleSoft project directory as input, the tool will look for XML files in this location by default.
Make sure your project follows this structure.

### Examples

#### Convert a MuleSoft Project with Default Output Location

```bash
$ bal migrate-mule /path/to/mule-project
```

This will create a Ballerina package inside `/path/to/mule-project` directory.

#### Convert a MuleSoft Project with a Custom Output Location

```bash
$ bal migrate-mule /path/to/mule-project --out /path/to/output-dir
```

This will create a Ballerina package at `/path/to/output-dir`.

#### Convert a Standalone Mule XML File

```bash
$ bal migrate-mule /path/to/mule-flow.xml
```

This will create a Ballerina package in the same directory as the input XML file.

#### Convert a Standalone Mule XML File with a Custom Output Location

```bash
$ bal migrate-mule /path/to/mule-flow.xml --out /path/to/output-dir
```

This will create a Ballerina package at `/path/to/output-dir`.

## Output
- For a mule project directory input: A new Ballerina package is created with the same name as the input project
  directory, appended with a `-ballerina` suffix.
    - For example,  if the input project directory is `my-project`, the
      output ballerina package name will be `my-project-ballerina`.

- Each `.xml` file within `src/main/app` is converted to a corresponding `.bal` file with the same name. When you have
  directories within  `src/main/app` the directory structure is reflected in the corresponding `.bal` file name.
    - For example, if you have a file `src/main/app/common/my-flow.xml`, the corresponding `.bal` file will be
      `my-project-ballerina/common.my-flow.bal`.

- For a Standalone XML file input: A new Ballerina package is created with the same name as the XML file, appended
  with a `-ballerina` suffix. Inside that, a new `.bal` file will be created with the same name as the input file but
  with a `.bal` extension.


### Migration Summary

- When you run `bal migrate-mule <path>`, the tool displays the migration progress in two stages:
    1. **DataWeave Conversion Percentage** – Indicates the conversion success rate of all DataWeave scripts within the
project.
    2. **Overall Project Conversion Percentage** – Represents the combined conversion rate based on both component-level
and DataWeave conversions.

- A detailed report is generated as `migration_summary.html` in the root of the newly created Ballerina package. This
  report provides a breakdown of the conversion percentage.

- Each XML element tag in the Mule configuration is assigned a weight based on its type and frequency of occurrence.
  The overall conversion percentage is then calculated based on the successful conversion of these weighted elements.

### TODO: Comments

During the conversion process, if the tool encounters unsupported Mule XML tags, they are included in the generated
Ballerina code as TODO comments. Each unsupported XML block is wrapped in a Ballerina comment block to indicate
manual intervention is required.

Below is an example:

```ballerina
public function endpoint(Context ctx) returns http:Response|error {

    // TODO: UNSUPPORTED MULE BLOCK ENCOUNTERED. MANUAL CONVERSION REQUIRED.
    // ------------------------------------------------------------------------
    // <db:select-unsupported config-ref="MySQL_Configuration" xmlns:doc="http://www.mulesoft.org/schema/mule/documentation" doc:name="Database" xmlns:db="http://www.mulesoft.org/schema/mule/db">
    //             <db:parameterized-query><![CDATA[SELECT * from users;]]></db:parameterized-query>
    //         </db:select-unsupported>
    // ------------------------------------------------------------------------

    // TODO: UNSUPPORTED MULE BLOCK ENCOUNTERED. MANUAL CONVERSION REQUIRED.
    // ------------------------------------------------------------------------
    // <json:object-to-json-transformer-unsupported xmlns:doc="http://www.mulesoft.org/schema/mule/documentation" doc:name="Object to JSON" xmlns:json="http://www.mulesoft.org/schema/mule/json"/>
    // ------------------------------------------------------------------------

    log:printInfo(string `Users details: ${ctx.payload.toString()}`);

    ctx.inboundProperties.response.setPayload(ctx.payload);
    return ctx.inboundProperties.response;
}

// TODO: UNSUPPORTED MULE BLOCK ENCOUNTERED. MANUAL CONVERSION REQUIRED.
// ------------------------------------------------------------------------
// <db:mysql-config-unsupported database="test_db" xmlns:doc="http://www.mulesoft.org/schema/mule/documentation" doc:name="MySQL Configuration" host="localhost" name="MySQL_Configuration" password="admin123" port="3306" user="root" xmlns:db="http://www.mulesoft.org/schema/mule/db"/>
// ------------------------------------------------------------------------
```

## Supported Features

### Mule Components
The migration tool currently supports converting the following MuleSoft components:

- Async
- Catch Exception Strategy
- Choice
- Choice Exception Strategy
- Database Connector
- Expression Component
- Flow
- Http Listener
- Http Request
- Logger
- Message Enricher
- Object To Json
- Object To String
- Reference Exception Strategy
- Session Variable
- Set Payload
- Sub Flow
- Transform Message
- Variable
- Vm Connector

### DataWeave Transformations

The migration tool currently supports the following DataWeave expressions:

- Concat Array Expression
- Concat Object Expression
- Concat String Expression
- Date Type Expression
- Filter Value Identifier Expression
- Lower Expression
- Map Combination Expression
- Map Index Identifier Expression
- Map Index Identifier Only Expression
- Map Value Identifier Expression
- Map With Parameters Expression
- Replace With Expression
- Single Selector Expression
- Sizeof Expression
- String Return Expression
- Type Coercion Date To Number Expression
- Type Coercion Format Expression
- Type Coercion Number Expression
- Type Coercion String Expression
- Type Coercion To Date Expression
- Upper Expression
- When Otherwise Expression
- When Otherwise Nested Expression

## Limitations
- Currently supports Mule **3.x only**. Support for Mule **4.x** is planned for future releases.
- Some moderate to advanced MuleSoft features may require manual adjustments after migration.
