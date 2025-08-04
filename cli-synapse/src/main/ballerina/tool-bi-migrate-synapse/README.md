# Synapse Migration Tool

This tool migrates Synapse configuration files to Ballerina code.

## Usage

```bash
bal tool migrate-synapse <source-synapse-config> [options]
```

## Options

- `-o, --out`: Output directory path
- `-k, --keep-structure`: Keep process structure
- `-v, --verbose`: Enable verbose output
- `-d, --dry-run`: Simulate conversion without generating files
- `-m, --multi-root`: Treat each child directory as a separate project
- `-g, --org-name`: Organization name for the generated package
- `-p, --project-name`: Project name for the generated package

## Examples

```bash
bal tool migrate-synapse /path/to/synapse-config.xml
bal tool migrate-synapse /path/to/synapse-config.xml --out /path/to/output
bal tool migrate-synapse /path/to/synapse-config.xml --verbose --dry-run
```