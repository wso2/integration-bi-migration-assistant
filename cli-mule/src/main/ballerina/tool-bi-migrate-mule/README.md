## Tool Overview
The `migrate-mule` tool enables the conversion of [MuleSoft](https://www.mulesoft.com) applications into Ballerina packages compatible with the [WSO2 Integrator](https://wso2.com/integration-platform/integrator/).
It accepts either a MuleSoft project directory or a standalone Mule XML file as input and produces an equivalent WSO2 Integrator application.

## Supported Mule Versions

The migration tool supports both Mule 3.x and Mule 4.x projects.

## Supported Mule Components

The migration tool currently supports a wide range of Mule components for both Mule 3.x and Mule 4.x. For a full list of supported components and their mappings, see:
- [Mule 3.x Components](https://github.com/wso2/integration-bi-migration-assistant/blob/main/mule/docs/palette-item-mappings-v3.md)
- [Mule 4.x Components](https://github.com/wso2/integration-bi-migration-assistant/blob/main/mule/docs/palette-item-mappings-v4.md)

## Supported DataWeave Transformations

The migration tool supports both DataWeave 1.0 (Mule 3.x) and DataWeave 2.0 (Mule 4.x) transformations. For details and 
conversion samples, see:
- [DataWeave 1.0 Mappings](https://github.com/wso2/integration-bi-migration-assistant/blob/main/mule/docs/dataweave-mappings-v3.md)
- [DataWeave 2.0 Mappings](https://github.com/wso2/integration-bi-migration-assistant/blob/main/mule/docs/dataweave-mappings-v4.md)

## Installation

To pull the `migrate-mule` tool from Ballerina Central, run the following command:
```bash
$ bal tool pull migrate-mule
```

## Usage

### Command Syntax

```bash
$ bal migrate-mule <source-project-directory-or-file> [-o|--out <output-directory>] [-f|--force-version <3|4>] [-k|--keep-structure] [-v|--verbose] [-d|--dry-run] [--check-responses] [-m|--multi-root]
```

### Parameters

- **source-project-directory-or-file** - *Required*. The path to the MuleSoft project directory or a standalone Mule XML file to be migrated.
- **-o or --out** - *Optional*. The directory where the new Ballerina package will be created. If not provided:
    - For a project directory input, the new Ballerina package is created inside the source project directory.
    - For a standalone XML file, the new Ballerina package is created in the same directory as the source file.
- **-f or --force-version** - *Optional*. Force the Mule version to 3 or 4 if automatic detection fails.
- **-k or --keep-structure** - *Optional*. If specified, preserves the original Mule project structure during migration. By default, this option is disabled.
- **-v or --verbose** - *Optional*. Enable verbose output during conversion.
- **-d or --dry-run** - *Optional*. Run the parsing and analysis phases and generate the `migration_report.html` file without generating the Ballerina package.
- **--check-responses** - *Optional*. Make the migrated APIkit services check their success responses against the project's API spec (RAML). When the spec describes a JSON body for the status a flow answers with, a body that does not match it fails with a 500 error instead of being sent. By default, responses are sent as the flow produced them, the same as in Mule.
- **-m or --multi-root** - *Optional*. Treat each child directory as a separate project and convert all of them. The source must be a directory containing multiple MuleSoft projects.

### Project Structure Requirements

The migration tool expects a standard MuleSoft project hierarchy with configuration XML files located under:

- For **Mule 3.x projects**:
  ```
  muleProjectPath/src/main/app
  ```
- For **Mule 4.x projects**:
  ```
  muleProjectPath/src/main/mule
  ```

When using a MuleSoft project directory as input, the tool will look for XML files in these locations by default based on the detected Mule version. Make sure your project follows the appropriate structure.

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

#### Check API Responses Against the Spec

```bash
$ bal migrate-mule /path/to/mule-project --check-responses
```

For an APIkit project, the migrated service then checks each success response whose JSON body the API spec (RAML) of the project describes, and answers with a 500 error when the body does not match it. Other responses, such as a status the spec does not list, are sent as the flow produced them. Without this flag, responses are not checked, the same as in Mule.

#### Force Mule Version During Migration

The migration tool will intelligently detect the Mule version (3.x or 4.x) from your project or XML file. However, if automatic detection fails, you can use the `--force-version` flag to explicitly specify the Mule version for migration.

```bash
$ bal migrate-mule /path/to/mule-project --force-version 3
```
This will force the migration tool to treat the input as a Mule 3.x project.

```bash
$ bal migrate-mule /path/to/mule-project --force-version 4
```
This will force the migration tool to treat the input as a Mule 4.x project.

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

### APIkit Projects with an API Spec

For a Mule 4.x APIkit project, the tool reads the RAML spec that `apikit:config` refers to. It looks in
`src/main/resources/api`, in `exchange_modules`, and in `target/repository`. When the spec comes from Anypoint Exchange
and is not in the project, download it, unzip it into the folder the migration report names, and migrate again.
Without the spec, the APIkit resources are built from the flow names, as before.

With the spec, the migrated service follows it:

- Each resource takes the spec's path, query, header and body parameters with their types. The spec's types become
  records in `types.bal`, with their limits as `@constraint` annotations. A request with a missing or invalid header,
  path parameter or body gets 400, as APIkit answered.
- A spec operation without a flow raises `APIKIT:NOT_IMPLEMENTED`, which APIkit's scaffolded error handlers answer
  with 501.
- When API Manager enforced policies on the API, such as client ID enforcement or a security scheme of the spec, a
  `TODO` on the service names them. The migrated service does not enforce them.
- `tests/api_spec_test.bal` checks the migrated service against the spec: the spec's examples must fit the generated
  records, and the service must answer 400, 501 and 404 as described above. Run them with `bal test --groups api-spec`,
  after setting the configurable values in `tests/Config.toml`.
- The `API Contracts` section of `migration_report.html` lists the implemented operations, the operations without a
  flow, flows that do not match the spec, and the API Manager policies.

Known differences from APIkit: the migrated service answers a method the spec does not declare with 404 instead of
405, and does not answer 406 or 415. It does not check enum or boolean query values, and it rejects an empty value of
a required header, which APIkit accepts.

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

## Limitations

- Some moderate to advanced MuleSoft features may require manual adjustments after migration.
