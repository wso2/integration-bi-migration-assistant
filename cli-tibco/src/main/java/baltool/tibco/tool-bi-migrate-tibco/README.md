# Module Overview

The bi-migrate-tibco tool helps you migrate existing TIBCO BusinessWorks integrations to Ballerina. It accepts a TIBCO BusinessWorks project directory or a single `bwp` file as input and converts it to equivalent Ballerina code.

# Usage

## Command Syntax

``` bash
bal name <source-project-directory-or-file> [-o|--out <output-directory>]
```

## Parameters

- **source-project-directory-or-file** - Required. The TIBCO BusinessWorks project directory or `bwp` file to migrate.
- **-o or --out** - *Optional*. The directory where the new Ballerina package will be created. If the directory does not exist tool will create it for you. If not provided,
  - If source-project-directory-or-file is a directory it will create new directory named \${source-project-directory-or-file}<sub>converted</sub> in the root of source-project-directory-or-file
  - if source-project-directory-or-file is a file a new `bal` will be created at the same location

# Examples

## Convert a TIBCO BusinessWorks project with the default output path

``` bash
bal name path/to/tibco-project
```

This will create a new Ballerina package inside the root of `path/to/tibco-project`

## Convert a TIBCO BusinessWorks project with a custom output path

``` bash
bal name path/to/tibco-project --out path/to/output-dir
```

This will create a new Ballerina package inside `path/to/output-dir`. If `path/to/output-dir` doesn't exist tool will create it for you. If the output path already exists tool will simply overwrite any file as needed without purging the directory.

## Convert a standalone `bwp` file

``` bash
bal name path/to/bwp-file
```

This will create a new `bal` file in the same directory as the `bwp` file.

## Convert a standalone `bwp` file with a custom output path

``` bash
bal name path/to/bwp-file --out path/to/bal-file
```

This will create a new `bal` file at `path/to/bal-file`. Note if the file already exists this will overwrite the file.

# Output

- When processing a `bwp` file: Generates a standalone `bal` file with the same name as the input file but with a `bal` extension if no output is given otherwise generate `bal` file at the output path
- When processing a TIBCO BusinessWorks project directory: Creates a new Ballerina package with `_converted` suffix in the parent directory.

## Migration summary

- When you run the tool it will log the number of activities it detected for each process along with the number of activities it failed to convert, if any.

## Unhandled activities

- If the tool encounters any activity which it does not know how to convert it will generate a placeholder "unhandled" function with a comment containing the relevant part of the `bwp` file.

``` bal
function unhandled(map<xml> context) returns xml|error {
    //FIXME: [ParseError] : Unknown activity
    //<bpws:empty name="OnMessageStart" xmlns:tibex="http://www.tibco.com/bpel/2007/extensions" tibex:constructor="onMessageStart" tibex:xpdlId="c266c167-7a80-40cc-9db2-60739386deeb" xmlns:bpws="http://docs.oasis-open.org/wsbpel/2.0/process/executable"/>

    //<bpws:empty name="OnMessageStart" xmlns:tibex="http://www.tibco.com/bpel/2007/extensions" tibex:constructor="onMessageStart" tibex:xpdlId="c266c167-7a80-40cc-9db2-60739386deeb" xmlns:bpws="http://docs.oasis-open.org/wsbpel/2.0/process/executable"/>
    return xml `<root></root>`;
}
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

# Limitations

- Currently supports only TIBCO BusinessWorks 5.
