## Tool Overview
The `migrate-mule` tool enables the conversion of [MuleSoft](https://www.mulesoft.com) applications into Ballerina packages compatible with the [WSO2 Ballerina Integrator](https://wso2.com/integrator/ballerina-integrator).
It accepts either a MuleSoft project directory or a standalone Mule XML file as input and produces an equivalent Ballerina Integrator application.

## Installation

To pull the `migrate-mule` tool from Ballerina Central, run the following command:
```bash
$ bal tool pull migrate-mule
```

## Usage

### Command Syntax

```bash
$ bal migrate-mule <source-project-directory-or-file> [-o|--out <output-directory>] [-k|--keep-structure] [-v|--verbose] [-d|--dry-run] [-m|--multi-root]
```

### Parameters

- **source-project-directory-or-file** - *Required*. The path to the MuleSoft project directory or a standalone Mule
  XML file to be migrated.
- **-o or --out** - *Optional*. The directory where the new Ballerina package will be created. If not provided,
    - For a project directory input, the new Ballerina package is created inside the source project directory.
    - For a standalone XML file, the new Ballerina package is created in the same directory as the source file.
- **-k or --keep-structure** - *Optional*. If specified, preserves the original Mule project structure during migration. By default, this option is disabled.
- **-v or --verbose** - *Optional*. Enable verbose output during conversion.
- **-d or --dry-run** - *Optional*. Run the parsing and analysis phases and generate the `migration_report.html` file without generating the Ballerina package.
- **-m or --multi-root** - *Optional*. Treat each child directory as a separate project and convert all of them. The source must be a directory containing multiple MuleSoft projects.

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

#### Preserve Mule Project Structure During Conversion

```bash
$ bal migrate-mule /path/to/mule-project --keep-structure
```

By default, the Mule project is converted using the standard Ballerina Integration(BI) file structure. However, if 
the `--keep-structure` flag is used, each Mule config xml file will be converted into a separate `.bal` file named 
after the xml file, maintaining the original project structure instead of following the standard BI layout.

#### Convert a Mule Project with Verbose Output

```bash
$ bal migrate-mule /path/to/mule-project --verbose
```

This will convert the project with detailed logging during the conversion process.

#### Convert a Mule project in dry-run mode

```bash
$ bal migrate-mule /path/to/mule-project --dry-run
```

This will run the parsing and analysis phases and generate the `migration_report.html` file without actually 
performing Ballerina package generation. It is useful for assessing migration feasibility and obtaining a time estimation. 
The generated report also lists the sections that will require manual conversion.

#### Convert Multiple Mule projects with multi-root mode

```bash
$ bal migrate-tibco path/to/projects-directory --multi-root
```

`--multi-root` will treat each child directory within `path/to/projects-directory` as a separate Mule project and convert all of them into Ballerina packages. `aggregate_migration_report.html` will be generated in the `projects-directory`, summarizing the migration results for all projects.

```bash
$ bal migrate-tibco path/to/projects-directory --out path/to/reports-directory --multi-root --dry-run
```

Additionally, you can use the `--dry-run` flag to run the parsing and analysis phases without generating Ballerina packages. This will generate individual analysis reports for each project found in the directory and an aggregated report `aggregate_migration_report.html` summarizing the migration results.

## Output
- For a mule project directory input: A new Ballerina package is created with the same name as the input project
  directory, appended with a `_ballerina` suffix.
    - For example,  if the input project directory is `my_project`, the
      output ballerina package name will be `my_project_ballerina`.

- Each `.xml` file within `src/main/app` is converted to a corresponding `.bal` file with the same name. When you have
  directories within  `src/main/app` the directory structure is reflected in the corresponding `.bal` file name.
    - For example, if you have a file `src/main/app/common/my_flow.xml`, the corresponding `.bal` file will be
      `my_project_ballerina/common.my_flow.bal`.

- For a Standalone XML file input: A new Ballerina package is created with the same name as the XML file, appended
  with a `_ballerina` suffix. Inside that, a new `.bal` file will be created with the same name as the input file but
  with a `.bal` extension.

### Migration Summary

- When you run `bal migrate-mule <path>`, the tool displays the migration progress in two stages:
    1. **DataWeave Conversion Percentage** – Indicates the conversion success rate of all DataWeave scripts within the
project.
    2. **Overall Project Conversion Percentage** – Represents the combined conversion rate based on both component-level
and DataWeave conversions.

- A detailed report is generated as `migration_report.html` in the root of the newly created Ballerina package. 
  This report provides the percentage of automated migration coverage and an estimated time for completing the remaining parts.
  Sections that require manual conversion are also highlighted in the report.

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

- [Async](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_COMPONENT_SAMPLES.md#async)
- [Catch Exception Strategy](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_COMPONENT_SAMPLES.md#catch-exception-strategy)
- [Choice](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_COMPONENT_SAMPLES.md#choice)
- [Choice Exception Strategy](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_COMPONENT_SAMPLES.md#choice-exception-strategy)
- [Database Connector](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_COMPONENT_SAMPLES.md#database-connector)
- [Expression Component](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_COMPONENT_SAMPLES.md#expression-component)
- [Flow](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_COMPONENT_SAMPLES.md#flow)
- [Http Listener](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_COMPONENT_SAMPLES.md#http-listener)
- [Http Request](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_COMPONENT_SAMPLES.md#http-request)
- [Logger](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_COMPONENT_SAMPLES.md#logger)
- [Message Enricher](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_COMPONENT_SAMPLES.md#message-enricher)
- [Object To Json](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_COMPONENT_SAMPLES.md#object-to-json)
- [Object To String](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_COMPONENT_SAMPLES.md#object-to-string)
- [Reference Exception Strategy](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_COMPONENT_SAMPLES.md#reference-exception-strategy)
- [Session Variable](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_COMPONENT_SAMPLES.md#session-variable)
- [Set Payload](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_COMPONENT_SAMPLES.md#set-payload)
- [Sub Flow](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_COMPONENT_SAMPLES.md#sub-flow)
- [Transform Message](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_COMPONENT_SAMPLES.md#transform-message)
- [Variable](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_COMPONENT_SAMPLES.md#variable)
- [Vm Connector](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_COMPONENT_SAMPLES.md#vm-connector)

### DataWeave Transformations

The migration tool currently supports the following DataWeave expressions:

- [Concat Array Expression](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_DATAWEAVE_SAMPLES.md#concat-array-expression)
- [Concat Object Expression](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_DATAWEAVE_SAMPLES.md#concat-object-expression)
- [Concat String Expression](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_DATAWEAVE_SAMPLES.md#concat-string-expression)
- [Date Type Expression](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_DATAWEAVE_SAMPLES.md#date-type-expression)
- [Filter Value Identifier Expression](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_DATAWEAVE_SAMPLES.md#filter-value-identifier-expression)
- [Lower Expression](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_DATAWEAVE_SAMPLES.md#lower-expression)
- [Map Combination Expression](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_DATAWEAVE_SAMPLES.md#map-combination-expression)
- [Map Index Identifier Expression](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_DATAWEAVE_SAMPLES.md#map-index-identifier-expression)
- [Map Index Identifier Only Expression](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_DATAWEAVE_SAMPLES.md#map-index-identifier-only-expression)
- [Map Value Identifier Expression](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_DATAWEAVE_SAMPLES.md#map-value-identifier-expression)
- [Map With Parameters Expression](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_DATAWEAVE_SAMPLES.md#map-with-parameters-expression)
- [Replace With Expression](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_DATAWEAVE_SAMPLES.md#replace-with-expression)
- [Single Selector Expression](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_DATAWEAVE_SAMPLES.md#single-selector-expression)
- [Sizeof Expression](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_DATAWEAVE_SAMPLES.md#sizeof-expression)
- [String Return Expression](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_DATAWEAVE_SAMPLES.md#string-return-expression)
- [Type Coercion Date To Number Expression](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_DATAWEAVE_SAMPLES.md#type-coercion-date-to-number-expression)
- [Type Coercion Format Expression](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_DATAWEAVE_SAMPLES.md#type-coercion-format-expression)
- [Type Coercion Number Expression](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_DATAWEAVE_SAMPLES.md#type-coercion-number-expression)
- [Type Coercion String Expression](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_DATAWEAVE_SAMPLES.md#type-coercion-string-expression)
- [Type Coercion To Date Expression](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_DATAWEAVE_SAMPLES.md#type-coercion-to-date-expression)
- [Upper Expression](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_DATAWEAVE_SAMPLES.md#upper-expression)
- [When Otherwise Expression](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_DATAWEAVE_SAMPLES.md#when-otherwise-expression)
- [When Otherwise Nested Expression](https://github.com/wso2/integration-bi-migration-assistant/blob/main/samples/MULE_DATAWEAVE_SAMPLES.md#when-otherwise-nested-expression)

## Limitations
- Currently supports Mule **3.x only**. Support for Mule **4.x** is planned for future releases.
- Some moderate to advanced MuleSoft features may require manual adjustments after migration.
