# Mule to Ballerina Migration Assistant

## Overview
The Mule to Ballerina Migration Assistant is a tool designed to convert Mule XML configuration files into Ballerina code. It facilitates the migration of Mule projects to Ballerina by automating the conversion process.

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

## Supported Mule Versions

The migration tool supports both Mule 3.x and 4.x projects.

## Supported Mule 3.x Components
(This section is AUTO-GENERATED by the test suite)

The migration tool currently supports the following Mule components:

- [Async](docs/palette-item-mappings-v3.md#async)
- [Catch Exception Strategy](docs/palette-item-mappings-v3.md#catch-exception-strategy)
- [Choice](docs/palette-item-mappings-v3.md#choice)
- [Choice Exception Strategy](docs/palette-item-mappings-v3.md#choice-exception-strategy)
- [Database Connector](docs/palette-item-mappings-v3.md#database-connector)
- [Expression Component](docs/palette-item-mappings-v3.md#expression-component)
- [Flow](docs/palette-item-mappings-v3.md#flow)
- [Http Listener](docs/palette-item-mappings-v3.md#http-listener)
- [Http Request](docs/palette-item-mappings-v3.md#http-request)
- [Logger](docs/palette-item-mappings-v3.md#logger)
- [Message Enricher](docs/palette-item-mappings-v3.md#message-enricher)
- [Misc](docs/palette-item-mappings-v3.md#misc)
- [Object To Json](docs/palette-item-mappings-v3.md#object-to-json)
- [Object To String](docs/palette-item-mappings-v3.md#object-to-string)
- [Reference Exception Strategy](docs/palette-item-mappings-v3.md#reference-exception-strategy)
- [Session Variable](docs/palette-item-mappings-v3.md#session-variable)
- [Set Payload](docs/palette-item-mappings-v3.md#set-payload)
- [Sub Flow](docs/palette-item-mappings-v3.md#sub-flow)
- [Transform Message](docs/palette-item-mappings-v3.md#transform-message)
- [Variable](docs/palette-item-mappings-v3.md#variable)
- [Vm Connector](docs/palette-item-mappings-v3.md#vm-connector)

## Supported DataWeave Transformations (DataWeave 1.0)
(This section is AUTO-GENERATED by the test suite)

The migration tool currently supports the following DataWeave transformations and their corresponding Ballerina implementations:

- [Concat Array Expression](docs/dataweave-mappings-v3.md#concat-array-expression)
- [Concat Object Expression](docs/dataweave-mappings-v3.md#concat-object-expression)
- [Concat String Expression](docs/dataweave-mappings-v3.md#concat-string-expression)
- [Date Type Expression](docs/dataweave-mappings-v3.md#date-type-expression)
- [Filter Value Identifier Expression](docs/dataweave-mappings-v3.md#filter-value-identifier-expression)
- [Lower Expression](docs/dataweave-mappings-v3.md#lower-expression)
- [Map Combination Expression](docs/dataweave-mappings-v3.md#map-combination-expression)
- [Map Index Identifier Expression](docs/dataweave-mappings-v3.md#map-index-identifier-expression)
- [Map Index Identifier Only Expression](docs/dataweave-mappings-v3.md#map-index-identifier-only-expression)
- [Map Value Identifier Expression](docs/dataweave-mappings-v3.md#map-value-identifier-expression)
- [Map With Parameters Expression](docs/dataweave-mappings-v3.md#map-with-parameters-expression)
- [Replace With Expression](docs/dataweave-mappings-v3.md#replace-with-expression)
- [Single Selector Expression](docs/dataweave-mappings-v3.md#single-selector-expression)
- [Sizeof Expression](docs/dataweave-mappings-v3.md#sizeof-expression)
- [String Return Expression](docs/dataweave-mappings-v3.md#string-return-expression)
- [Type Coercion Date To Number Expression](docs/dataweave-mappings-v3.md#type-coercion-date-to-number-expression)
- [Type Coercion Format Expression](docs/dataweave-mappings-v3.md#type-coercion-format-expression)
- [Type Coercion Number Expression](docs/dataweave-mappings-v3.md#type-coercion-number-expression)
- [Type Coercion String Expression](docs/dataweave-mappings-v3.md#type-coercion-string-expression)
- [Type Coercion To Date Expression](docs/dataweave-mappings-v3.md#type-coercion-to-date-expression)
- [Upper Expression](docs/dataweave-mappings-v3.md#upper-expression)
- [When Otherwise Expression](docs/dataweave-mappings-v3.md#when-otherwise-expression)
- [When Otherwise Nested Expression](docs/dataweave-mappings-v3.md#when-otherwise-nested-expression)

## Supported Mule 4.x Components
(This section is AUTO-GENERATED by the test suite)

The migration tool currently supports the following Mule 4.x components:

- [Async](docs/palette-item-mappings-v4.md#async)
- [Choice](docs/palette-item-mappings-v4.md#choice)
- [Database Connector](docs/palette-item-mappings-v4.md#database-connector)
- [Error Handler](docs/palette-item-mappings-v4.md#error-handler)
- [Expression Component](docs/palette-item-mappings-v4.md#expression-component)
- [Flow](docs/palette-item-mappings-v4.md#flow)
- [Http Listener](docs/palette-item-mappings-v4.md#http-listener)
- [Http Request](docs/palette-item-mappings-v4.md#http-request)
- [Logger](docs/palette-item-mappings-v4.md#logger)
- [Message Enricher](docs/palette-item-mappings-v4.md#message-enricher)
- [Object To Json](docs/palette-item-mappings-v4.md#object-to-json)
- [Object To String](docs/palette-item-mappings-v4.md#object-to-string)
- [On Error Continue](docs/palette-item-mappings-v4.md#on-error-continue)
- [On Error Propagate](docs/palette-item-mappings-v4.md#on-error-propagate)
- [Property Access](docs/palette-item-mappings-v4.md#property-access)
- [Session Variable](docs/palette-item-mappings-v4.md#session-variable)
- [Set Payload](docs/palette-item-mappings-v4.md#set-payload)
- [Sub Flow](docs/palette-item-mappings-v4.md#sub-flow)
- [Transform Message](docs/palette-item-mappings-v4.md#transform-message)
- [Variable](docs/palette-item-mappings-v4.md#variable)
- [Vm Connector](docs/palette-item-mappings-v4.md#vm-connector)

## Supported DataWeave Transformations (DataWeave 2.0)
(This section is AUTO-GENERATED by the test suite)

The migration tool currently supports the following DataWeave transformations from Mule 4.x and their corresponding Ballerina implementations:

- [Concat Array Expression](docs/dataweave-mappings-v4.md#concat-array-expression)
- [Concat Object Expression](docs/dataweave-mappings-v4.md#concat-object-expression)
- [Concat String Expression](docs/dataweave-mappings-v4.md#concat-string-expression)
- [Date Type Expression](docs/dataweave-mappings-v4.md#date-type-expression)
- [Filter Value Identifier Expression](docs/dataweave-mappings-v4.md#filter-value-identifier-expression)
- [Lower Expression](docs/dataweave-mappings-v4.md#lower-expression)
- [Map Combination Expression](docs/dataweave-mappings-v4.md#map-combination-expression)
- [Map Index Identifier Expression](docs/dataweave-mappings-v4.md#map-index-identifier-expression)
- [Map Index Identifier Only Expression](docs/dataweave-mappings-v4.md#map-index-identifier-only-expression)
- [Map Value Identifier Expression](docs/dataweave-mappings-v4.md#map-value-identifier-expression)
- [Map With Parameters Expression](docs/dataweave-mappings-v4.md#map-with-parameters-expression)
- [Replace With Expression](docs/dataweave-mappings-v4.md#replace-with-expression)
- [Single Selector Expression](docs/dataweave-mappings-v4.md#single-selector-expression)
- [Sizeof Expression](docs/dataweave-mappings-v4.md#sizeof-expression)
- [String Return Expression](docs/dataweave-mappings-v4.md#string-return-expression)
- [Type Coercion Date To Number Expression](docs/dataweave-mappings-v4.md#type-coercion-date-to-number-expression)
- [Type Coercion Format Expression](docs/dataweave-mappings-v4.md#type-coercion-format-expression)
- [Type Coercion Number Expression](docs/dataweave-mappings-v4.md#type-coercion-number-expression)
- [Type Coercion String Expression](docs/dataweave-mappings-v4.md#type-coercion-string-expression)
- [Type Coercion To Date Expression](docs/dataweave-mappings-v4.md#type-coercion-to-date-expression)
- [Upper Expression](docs/dataweave-mappings-v4.md#upper-expression)
- [When Otherwise Expression](docs/dataweave-mappings-v4.md#when-otherwise-expression)
- [When Otherwise Nested Expression](docs/dataweave-mappings-v4.md#when-otherwise-nested-expression)

## Sample Input and Output

For side by side view of conversion examples, please refer to:
- [Mule Component Samples](docs/palette-item-mappings.md)
- [DataWeave Transformation Samples](docs/dataweave-mappings.md)
