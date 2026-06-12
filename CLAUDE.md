# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Multi-module Gradle project that converts integration platform configurations (MuleSoft, TIBCO BusinessWorks, Azure Logic Apps, Mirth Connect) into [Ballerina](https://ballerina.io) code. The conversion pipeline uses an AST-based intermediate representation (IR) to generate idiomatic Ballerina from parsed source configs.

## Environment Setup

Before building, set GitHub credentials for fetching packages from GitHub Packages:

```bash
export packageUser=<Your GitHub Username>
export packagePAT=<GitHub Personal Access Token with read:packages scope>
```

## Build & Test Commands

```bash
./gradlew clean build           # Full build with tests
./gradlew clean build -x test   # Build without tests
./gradlew clean test            # Run tests only

# Build specific tool JARs
./gradlew muleJar               # mule-migration-assistant-*.jar
./gradlew tibcoJar              # tibco-migration-assistant-*.jar
./gradlew logicAppsJar
./gradlew mirthChannelJar

# Package as Ballerina tools (requires Ballerina installed)
./gradlew mulePack
./gradlew tibcoPack

# Publish common library locally (needed when changing common/ for local testing)
./gradlew :common:publishToMavenLocal
```

Run tests for a single module:
```bash
./gradlew :mule:test
./gradlew :tibco:test
./gradlew :common:test
```

## Before Submitting Changes

After any mule-related changes, regenerate the docs (requires Python 3):

```bash
python3 scripts/generate_mule_docs_v3.py
python3 scripts/generate_mule_docs_v4.py
```

This updates `mule/docs/palette-item-mappings.md`, `mule/docs/dataweave-mappings.md`, and sections in `mule/README.md`.

## Architecture

### Module Structure

| Module | Purpose |
|--------|---------|
| `common/` | Shared IR (`BallerinaModel`), code generator, base converter interface, report utilities |
| `mule/` | Mule 3.x and 4.x → Ballerina conversion engine |
| `tibco/` | TIBCO BusinessWorks → Ballerina conversion engine |
| `cli-mule/`, `cli-tibco/` | Thin CLI wrappers + Ballerina tool packaging |
| `cli-logicapps/`, `cli-mirth/` | Logic Apps and Mirth Connect CLI tools |
| `build-config/` | Checkstyle configuration |

### Conversion Pipeline

1. **Parse**: Source config files (XML/YAML/JSON) are parsed into platform-specific object models (`mule/model/`, `tibco/model/`)
2. **Convert**: Platform converters (`mule.v3.converter`, `mule.v4.converter`, `tibco.converter`) transform the model into `common.BallerinaModel` IR nodes
3. **Generate**: `common.CodeGenerator` renders the IR into `.bal` source files, formatting via `ballerina-parser` / `formatter-core`
4. **Report**: `common.ReportUtils` + platform-specific report classes produce HTML/Markdown migration summaries

### Mule Module Internals

- `mule.v3` and `mule.v4` are fully separate; `mule.common` is shared by both
- DataWeave transformations (v1.0 for Mule 3, v2.0 for Mule 4) are converted via **ANTLR4 parsers** using the visitor pattern — generated parser code lives in `mule/v{3,4}/dataweave/parser/`
- `MuleMigrator.java` is the main orchestrator (44 KB); entry point from `cli-mule`
- To test a DataWeave snippet: run `mule.v4.dataweave.converter.TestDWConversion.testDWConversion()` (prints equivalent Ballerina for DataWeave 2.0)

### Key Classes in `common/`

- `BallerinaModel` — IR node types representing Ballerina constructs
- `CodeGenerator` — renders `BallerinaModel` IR to `.bal` source
- `BICodeConverter` — base interface all platform converters implement
- `TimeEstimation` / `ProjectSummary` — migration effort estimation and stats
- `ReportUtils` — HTML/Markdown report generation

## Code Style

From `.github/copilot-instructions.md` (enforced by Checkstyle + SpotBugs):

- Max line length: **120 characters**
- Wrap single-line `if` statements in braces
- No doc comments on private methods
- New helper methods must be `private` and placed adjacent to their caller
- No comments describing what code does — rename instead; comments only for non-obvious *why*
- No single-use variables — inline the value at the use site
- Annotate non-null return types with `@NotNull`; prefer `Optional` over returning `null`
- Add assertions for non-null/state preconditions in methods/constructors

## Testing

Tests use **TestNG** (`src/test/resources/testng.xml` per module). Test resources include sample Mule/TIBCO projects under `src/test/resources/` with expected Ballerina outputs for comparison.