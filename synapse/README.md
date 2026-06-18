# Synapse to Ballerina migration

Converts WSO2 Synapse (ESB / Micro Integrator) artifacts into a Ballerina package.

> Status: early scaffold. A focused subset of the Synapse REST API surface is supported today
> (see [Supported constructs](#supported-synapse-constructs)) and is being grown incrementally.

## What it does

Given a Synapse REST API definition, the tool generates a Ballerina package — a `main.bal`
containing the equivalent HTTP service plus a `Ballerina.toml` manifest. Each `<api>` becomes an
HTTP service, each `<resource>` becomes a resource function, and the mediators inside a resource
are translated into the function body.

## Building the project

Prerequisite: JDK 21.

Build the runnable migration jar using the Gradle wrapper from the repository root:

```sh
./gradlew :cli-synapse:synapseJar
```

This produces `cli-synapse/build/libs/synapse-migration-assistant-<version>.jar`.

## Running the migration tool

Once the jar is built, run the migration tool with the following command:

```sh
java -jar cli-synapse/build/libs/synapse-migration-assistant-<version>.jar <synapse-artifact-file> [-o|--out <output-directory>]
```

**Parameters:**
- `<synapse-artifact-file>`: Path to the Synapse artifact file to be converted.
- `-o`, `--out` `<output-directory>`: Optional. Directory to write the generated Ballerina package into.

**Output:**
- A Ballerina package (a `main.bal` and a `Ballerina.toml`) is generated. By default it is written to a directory named after the input file with a `_converted` suffix; if `-o`/`--out` is given, it is written to that directory instead.

## Supported Synapse constructs

The migration tool currently supports the following Synapse elements:

| Tag | Converted to |
|-----|--------------|
| `<api>` | HTTP service |
| `<resource>` | resource function |
| `<inSequence>` | resource function body |
| `<payloadFactory>` | response payload |
| `<respond>` | response return |
| `<property>` (static name only) | response header, status code, or local variable |

## Example

Input (`HelloWorldService/helloWorld.xml`):

```xml
<api context="/HelloWorld" name="HelloWorld" xmlns="http://ws.apache.org/ns/synapse">
    <resource methods="GET" uri-template="/status/{id}">
        <inSequence>
            <payloadFactory media-type="json">
                <format>{"Hello":"World"}</format>
            </payloadFactory>
            <respond/>
        </inSequence>
    </resource>
</api>
```

Output (`main.bal`):

```ballerina
import ballerina/http;

public listener http:Listener httpListener = new (8080);

service /HelloWorld on httpListener {
    resource function get status/[string id]() returns http:Response {
        http:Response response = new;
        response.setPayload({"Hello": "World"});
        return response;
    }
}
```

## Sample conversion projects

Sample Synapse artifacts live under `cli-synapse/src/test/resources/synapse/<Name>`, paired by name
with the expected Ballerina packages under `cli-synapse/src/test/resources/ballerina/<Name>`. To add
a case, drop `synapse/<Name>/<Name>.xml` and the expected `ballerina/<Name>` package.

## Known limitations

- Only a single Synapse artifact **file** is converted, not a full project directory.
- `<proxy>` services, `<log>`, and `<faultSequence>` are not yet handled; an unsupported mediator causes the conversion to fail.
- `<property>` uses the static `value` attribute only; the dynamic `expression` form is not yet supported.
- The response payload is set with a generic setter rather than media-type-specific ones (e.g. JSON/text/XML setters).
- The HTTP listener is fixed (port `8080`) and is not derived from the source artifact.
