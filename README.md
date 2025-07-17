# integration-bi-migration-assistant

## Table of Contents
- [Overview](#overview)
- [Setting Up the Prerequisites](#setting-up-the-prerequisites)
- [Building the Source](#building-the-source)
- [Running the Migration Tool](#running-the-migration-tool)
- [Sending Changes](#sending-changes)

## Overview
The integrator-bi-migration-assistant is a comprehensive tool designed to convert integration platform configurations into [Ballerina](https://ballerina.io) code. This multi-package project includes migration assistants for different integration platforms:

- **Mule**: Located in the `mule/` directory - converts [MuleSoft](https://www.mulesoft.com) configurations to Ballerina
- **Tibco**: Located in the `tibco/` directory - converts [TIBCO BusinessWorks](https://docs.tibco.com/products/tibco-activematrix-businessworks) configurations to Ballerina

Each package contains its own documentation, samples, and migration tools specific to the respective integration platform.

## Setting Up the Prerequisites

1. Download and install Java SE Development Kit (JDK) version 21 (from one of the following locations).
   * [Oracle](https://www.oracle.com/java/technologies/downloads/)
   * [OpenJDK](http://openjdk.java.net/install/index.html)

2. Generate a GitHub access token with read package permissions, then set the following `env` variables:

    ```shell
   export packageUser=<Your GitHub Username>
   export packagePAT=<GitHub Personal Access Token>
   ```

## Building the Source

Execute the commands below to build from source.

1. To build the package:

   ```bash
   ./gradlew clean build
   ```

2. To run the tests:

   ```bash
   ./gradlew clean test
   ```

3. To build without the tests:

   ```bash
   ./gradlew clean build -x test
   ```

## Running the Migration Tool

First, build the project to create the `build/libs/mule-to-ballerina-migration-assistant.jar` file.

To run the migration tool, use the following command:

```sh
java -jar build/libs/mule-to-ballerina-migration-assistant.jar <mule-xml-config-file-or-project-directory>
```

**Parameters:**
- `<mule-xml-config-file-or-project-directory>`: Path to the Mule XML configuration file or the Mule project directory to be converted.

**Output:**
- **Mule XML Config File**: A standalone `.bal` file is generated with the same name as the input file but with a `.bal` extension. This file is located in the same directory as the input file.
- **Mule Project Directory**: A new Ballerina package is created with the same name as the input project directory, appended with a `-ballerina` suffix. This new package is located inside the given project directory path, and a `.bal` file is created for each Mule XML file within the project.

## Sending Changes
Before sending changes, ensure you have Python 3 installed.
Then, run the following commands to automatically generate mule-specific documentation in the mule package:

```sh
python3 scripts/generate_mule_docs_v3.py

python3 scripts/generate_mule_docs_v4.py
```

This will generate:
- [mule/docs/palette-item-mappings.md](mule/docs/palette-item-mappings.md)
- [mule/docs/dataweave-mappings.md](mule/docs/dataweave-mappings.md)
- Update the relevant sections in [mule/README.md](mule/README.md)

## Package-Specific Documentation

For detailed information about each migration assistant, please refer to:
- [Mule Migration Assistant](mule/README.md)
- [Tibco Migration Assistant](tibco/README.md)
