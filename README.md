# Mule to Ballerina Migration Assistant

## Table of Contents
- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Building the Project](#building-the-project)
- [Running the Migration Tool](#running-the-migration-tool)
- [Supported Mule Components](#supported-mule-components)
- [License](#license)

## Overview
The Mule to Ballerina Migration Assistant is a comprehensive tool designed to convert Mule XML configuration files into Ballerina code. It facilitates the migration of Mule applications to Ballerina by automating the conversion process. The tool reads Mule XML configuration files and generates equivalent Ballerina code, ensuring a seamless transition with minimal manual effort.

## Prerequisites
- Java 21
- Gradle 8.11 or later

## Building the Project
To build the project, follow these steps:

1. Clone the repository:
    ```sh
    git clone https://github.com/lochana-chathura/mule-to-bi-migration-assistant.git
    cd mule-to-ballerina-migration-assistant
    ```

2. Build the project using Gradle:
    ```sh
    ./gradlew build
    ```

## Running the Migration Tool
To run the migration tool, use the following command:

```sh
java -jar build/libs/mule-to-ballerina-migration-assistant.jar <mule-xml-configuration-file>
```

The generated Ballerina file will have the same name as the input XML file, but with a `.bal` extension, and will be located in the same directory as the input file.

## Supported Mule Components
The migration tool currently supports the following Mule components:

- HTTP Listener
- HTTP Request
- Logger
- Set Payload
- Set Variable
- Choice
- Flow Reference
- Sub Flow
