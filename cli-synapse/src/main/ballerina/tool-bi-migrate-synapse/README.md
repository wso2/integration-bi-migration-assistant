## Tool Overview

The `migrate-synapse` tool helps you migrate existing WSO2 Synapse (ESB / Micro Integrator) integrations to Ballerina.
It accepts a Synapse project directory or a single artifact file (proxy service, REST API, sequence, endpoint, etc.) as
input and converts it to equivalent Ballerina code.

## Installation

Execute the command below to pull the `migrate-synapse` tool from Ballerina Central

```bash
$ bal tool pull migrate-synapse
```

## Usage

### Command Syntax

```bash
$ bal migrate-synapse <source-project-directory-or-file> [-o|--out <output-directory>] [-k|--keep-structure] [-v|--verbose] [-d|--dry-run] [-m|--multi-root] [-g|--org-name <organization-name>] [-p|--project-name <project-name>]
```

### Parameters

- **source-project-directory-or-file** - Required. The WSO2 Synapse project directory or artifact file to migrate.
- **-o or --out** - *Optional*. The directory where the new Ballerina package will be created. If the directory does not
  exist the tool will create it for you. If not provided,
    - If source-project-directory-or-file is a directory it will create a new directory named $
      {source-project-directory-or-file}_converted in the root of source-project-directory-or-file
    - If source-project-directory-or-file is a file it will create a new directory named ${root}_converted in the parent
      of the root directory where root is the directory containing the given file.
- **-k or --keep-structure** - *Optional*. If specified, preserves the original artifact structure during migration. By
  default, this option is disabled.
- **-v or --verbose** - *Optional*. Enable verbose output during conversion.
- **-d or --dry-run** - *Optional*. Run the parsing and analysis phases and generate the `report.html` file without
  generating the Ballerina package.
- **-m or --multi-root** - *Optional*. Treat each child directory as a separate project and convert all of them. The
  source must be a directory containing multiple Synapse projects.
- **-g or --org-name** - *Optional*. Organization name for the generated Ballerina package. If not provided, defaults to
  `converter`.
- **-p or --project-name** - *Optional*. Project name for the generated Ballerina package. If not provided, defaults to
  the input directory or file name.

## Examples

### Convert a WSO2 Synapse project with the default output path

```bash
$ bal migrate-synapse path/to/synapse-project
```

This will create a new Ballerina package inside the root of `path/to/synapse-project`

### Convert a WSO2 Synapse project with a custom output path

```bash
$ bal migrate-synapse path/to/synapse-project --out path/to/output-dir
```

This will create a new Ballerina package inside `path/to/output-dir`. If `path/to/output-dir` doesn't exist the tool
will create it for you. If the output path already exists the tool will simply overwrite any file as needed without
purging the directory.

### Convert a standalone artifact file

```bash
$ bal migrate-synapse path/to/proxy-service.xml
```

This will create a new Ballerina package in the root directory of the directory containing the file.

### Preserve artifact structure during conversion

```bash
$ bal migrate-synapse path/to/synapse-project --keep-structure
```

or

```bash
$ bal migrate-synapse path/to/synapse-project -k
```

By default, the Synapse project is converted using the standard Ballerina Integration (BI) file structure. However, if
the `--keep-structure` or `-k` flag is used, each Synapse artifact will be converted into a separate `.bal` file named
after the artifact, maintaining the original structure instead of following the standard BI layout.

### Convert a WSO2 Synapse project with verbose output

```bash
$ bal migrate-synapse path/to/synapse-project --verbose
```

or

```bash
$ bal migrate-synapse path/to/synapse-project -v
```

This will convert the project with detailed logging during the conversion process.

### Convert a WSO2 Synapse project in dry-run mode

```bash
$ bal migrate-synapse path/to/synapse-project --dry-run
```

or

```bash
$ bal migrate-synapse path/to/synapse-project -d
```

This will run the parsing and analysis phases and generate the `report.html` file without actually performing Ballerina
package generation.

### Convert multiple WSO2 Synapse projects with multi-root mode

```bash
$ bal migrate-synapse path/to/projects-directory --multi-root
```

or

```bash
$ bal migrate-synapse path/to/projects-directory -m
```

This will treat each child directory within `path/to/projects-directory` as a separate Synapse project and convert all
of them.

## Output

- Creates a new Ballerina package with `_converted` suffix in the parent directory.

### Migration summary

- When you run the tool it will generate a `report.html` file in the output directory with a migration summary.

### Unhandled mediators

- If the tool encounters a mediator which it does not know how to convert it will generate a placeholder "unhandled"
  function with a comment containing the relevant part of the artifact file.

### Partially supported mediators

- In case of mediators that are only partially supported you will see a log message with the mediator name. They will
  also be listed in the report under heading "Mediators that need manual validation". For most typical use cases, you
  can use the converted source as is, but we highly encourage users to check the converted code. There will be comments
  explaining any limitations/assumptions the tool has made.

### Supported Synapse artifacts and mediators

> NOTE: This list grows as the converter evolves. The items below are the initial targeted set.

- Artifacts
    - `proxy` (proxy service)
    - `api` (REST API) with `resource`
    - `sequence`
    - `endpoint`
- Mediators
    - `log`
    - `respond`
    - `send`
    - `payloadFactory`
    - `property`
    - `sequence` (call)
    - `call` / `callout`
    - `filter`
    - `switch`
    - `iterate`
    - `foreach`
