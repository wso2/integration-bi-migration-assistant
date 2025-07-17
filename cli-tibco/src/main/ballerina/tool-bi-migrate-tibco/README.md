## Tool Overview

The `migrate-tibco` tool helps you migrate existing TIBCO BusinessWorks integrations to Ballerina. It accepts a TIBCO BusinessWorks project directory or a single process file as input and converts it to equivalent Ballerina code.

## Installation

Execute the command below to pull the `migrate-tibco` tool from Ballerina Central
```bash
$ bal tool pull migrate-tibco
```

## Usage

### Command Syntax

```bash
$ bal migrate-tibco <source-project-directory-or-file> [-o|--out <output-directory>] [-k|--keep-structure] [-v|--verbose] [-d|--dry-run] [-m|--multi-root] [-g|--org-name <organization-name>] [-p|--project-name <project-name>]
```

### Parameters

- **source-project-directory-or-file** - Required. The TIBCO BusinessWorks project directory or process file to migrate.
- **-o or --out** - *Optional*. The directory where the new Ballerina package will be created. If the directory does not exist tool will create it for you. If not provided,
  - If source-project-directory-or-file is a directory it will create new directory named ${source-project-directory-or-file}_converted in the root of source-project-directory-or-file
  - if source-project-directory-or-file is a file it will create a new directory named ${root}_converted in the parent of the root directory where root is the directory containing the given file.
- **-k or --keep-structure** - *Optional*. If specified, preserves the original process structure during migration. By default, this option is disabled.
- **-v or --verbose** - *Optional*. Enable verbose output during conversion.
- **-d or --dry-run** - *Optional*. Run the parsing and analysis phases and generate the `report.html` file without generating the Ballerina package.
- **-m or --multi-root** - *Optional*. Treat each child directory as a separate project and convert all of them. The source must be a directory containing multiple TIBCO projects.
- **-g or --org** - *Optional*. Organization name for the generated Ballerina package. If not provided, defaults to `converter`.
- **-p or --project-name** - *Optional*. Project name for the generated Ballerina package. If not provided, defaults to the input directory or file name.

## Examples

### Convert a TIBCO BusinessWorks project with the default output path

```bash
$ bal migrate-tibco path/to/tibco-project
```

This will create a new Ballerina package inside the root of `path/to/tibco-project`

### Convert a TIBCO BusinessWorks project with a custom output path

```bash
$ bal migrate-tibco path/to/tibco-project --out path/to/output-dir
```

This will create a new Ballerina package inside `path/to/output-dir`. If `path/to/output-dir` doesn't exist tool will create it for you. If the output path already exists tool will simply overwrite any file as needed without purging the directory.

### Convert a standalone process file

```bash
$ bal migrate-tibco path/to/process-file
```

This will create a new Ballerina package in the root directory of directory containing the file.

### Convert a standalone process file with a custom output path

```bash
$ bal migrate-tibco path/to/process-file --out path/to/output-dir
```

This will create a new Ballerina package at `path/to/bal-file`. If the output path already exists tool will simply overwrite any file as needed without
purging the directory.

### Preserve process structure during conversion

```bash
$ bal migrate-tibco path/to/tibco-project --keep-structure
```
or

```bash
$ bal migrate-tibco path/to/tibco-project -k
```

By default, the TIBCO project is converted using the standard Ballerina Integration (BI) file structure. However, if the `--keep-structure` or `-k` flag is used, each TIBCO process will be converted into a separate `.bal` file named after the process, maintaining the original process structure instead of following the standard BI layout.

### Convert a TIBCO BusinessWorks project with verbose output

```bash
$ bal migrate-tibco path/to/tibco-project --verbose
```

or

```bash
$ bal migrate-tibco path/to/tibco-project -v
```

This will convert the project with detailed logging during the conversion process.

### Convert a TIBCO BusinessWorks project in dry-run mode

```bash
$ bal migrate-tibco path/to/tibco-project --dry-run
```

or

```bash
$ bal migrate-tibco path/to/tibco-project -d
```

This will run the parsing and analysis phases and generate the `report.html` file without actually performing Ballerina package generation.

### Convert multiple TIBCO BusinessWorks projects with multi-root mode

```bash
$ bal migrate-tibco path/to/projects-directory --multi-root
```

or

```bash
$ bal migrate-tibco path/to/projects-directory -m
```

For analysis only (dry-run):

```bash
$ bal migrate-tibco path/to/projects-directory --multi-root --dry-run
```

or

```bash
$ bal migrate-tibco path/to/projects-directory -m -d
```

This will treat each child directory within `path/to/projects-directory` as a separate TIBCO project and convert all of them. When used with `--dry-run`, it generates analysis reports for each project found.

### Convert a TIBCO BusinessWorks project with custom organization and project name

```bash
$ bal migrate-tibco path/to/tibco-project --org myorg --project-name myproject
```

or

```bash
$ bal migrate-tibco path/to/tibco-project -g myorg -p myproject
```

This will create a Ballerina package with organization name `myorg` and project name `myproject` instead of the default values.

## Output

- Creates a new Ballerina package with `_converted` suffix in the parent directory.

### Migration summary

- When you run the tool it will generate a `report.html` file in the output directory with migration summary.

### Unhandled activities

- If the tool encounters any activity which it does not know how to convert it will generate a placeholder "unhandled" function with a comment containing the relevant part of the process file.
  ```ballerina
  function unhandled(map<xml> context) returns xml|error {
      //FIXME: [ParseError] : Unknown activity
      //<bpws:empty name="OnMessageStart" xmlns:tibex="http://www.tibco.com/bpel/2007/extensions" tibex:constructor="onMessageStart" tibex:xpdlId="c266c167-7a80-40cc-9db2-60739386deeb" xmlns:bpws="http://docs.oasis-open.org/wsbpel/2.0/process/executable"/>

      //<bpws:empty name="OnMessageStart" xmlns:tibex="http://www.tibco.com/bpel/2007/extensions" tibex:constructor="onMessageStart" tibex:xpdlId="c266c167-7a80-40cc-9db2-60739386deeb" xmlns:bpws="http://docs.oasis-open.org/wsbpel/2.0/process/executable"/>
      return xml `<root></root>`;
  }
  ```

### Partially supported activities
- In case of activities that are only partially supported you will see a log message with the activity name.
  ```
  WARNING: Partially supported activity: JMS Send
  ```

- They will also be listed in the report under heading "Activities that need manual validation". For most typical use cases, you can use the converted source as is, but we highly encourage users to check the converted code. There will be comments explaining any limitations/assumptions the tool has made.
  ```ballerina
      // WARNING: using default destination configuration
      jms:MessageProducer var4 = check var3.createProducer(destination = {
          'type: jms:TOPIC,
          name: "TOPIC"
      });
  ```

### Supported TIBCO BusinessWorks activities

- `invoke`
- `pick`
- `empty`
- `reply`
- `throw`
- `assign`
- `forEach`
- `extensionActivity`
  - `receiveEvent`
  - `activityExtension`
    - `bw.internal.end`
    - `bw.http.sendHTTPRequest`
    - `bw.restjson.JsonRender`
    - `bw.restjson.JsonParser`
    - `bw.http.sendHTTPResponse`
    - `bw.file.write`
    - `bw.generalactivities.log`
    - `bw.xml.renderxml`
    - `bw.generalactivities.mapper`
    - `bw.internal.accumulateend`
  - `extActivity`
- `com.tibco.plugin.mapper.MapperActivity`
- `com.tibco.plugin.http.HTTPEventSource`
- `com.tibco.pe.core.AssignActivity`
- `com.tibco.plugin.http.HTTPResponseActivity`
- `com.tibco.plugin.xml.XMLRendererActivity`
- `com.tibco.plugin.xml.XMLParseActivity`
- `com.tibco.pe.core.LoopGroup`
- `com.tibco.pe.core.WriteToLogActivity`
- `com.tibco.pe.core.CatchActivity`
- `com.tibco.plugin.file.FileReadActivity`
- `com.tibco.plugin.file.FileWriteActivity`
- `com.tibco.plugin.jdbc.JDBCGeneralActivity`
- `com.tibco.plugin.json.activities.RestActivity`
- `com.tibco.pe.core.CallProcessActivity`
- `com.tibco.plugin.soap.SOAPSendReceiveActivity`
- `com.tibco.plugin.json.activities.JSONParserActivity`
- `com.tibco.plugin.json.activities.JSONRenderActivity`
- `com.tibco.plugin.soap.SOAPSendReplyActivity`
- `com.tibco.plugin.jms.JMSQueueEventSource`
- `com.tibco.plugin.jms.JMSQueueSendActivity`
- `com.tibco.plugin.jms.JMSQueueGetMessageActivity`
- `com.tibco.plugin.jms.JMSTopicPublishActivity`
- `com.tibco.pe.core.GenerateErrorActivity`
- `com.tibco.plugin.timer.NullActivity`
- `com.tibco.plugin.timer.SleepActivity`
- `com.tibco.pe.core.GetSharedVariableActivity`
- `com.tibco.pe.core.SetSharedVariableActivity`
- `com.tibco.plugin.file.FileEventSource`
- `com.tibco.pe.core.OnStartupEventSource`
- `com.tibco.plugin.file.ListFilesActivity`
- `com.tibco.plugin.xml.XMLTransformActivity`
