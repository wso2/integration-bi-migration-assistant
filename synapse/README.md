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

### Artifacts

| Tag | Converted to |
|-----|--------------|
| `<api>` | HTTP service |
| `<resource>` | resource function |
| `<inSequence>` | resource function body |
| `<inboundEndpoint>` (`protocol="http"`/`"https"`) | dedicated `http:Listener` (port from the `inbound.http.port` parameter) plus a wildcard service forwarding every request to the referenced `sequence` |
| `<inboundEndpoint>` (`protocol="jms"`) | dedicated `jms:Listener` (from `java.naming.factory.initial`, `java.naming.provider.url`, `transport.jms.Destination`, `transport.jms.UserName`/`Password`) plus a service with a single `onMessage` remote function forwarding every message to the referenced `sequence` |
| `<inboundEndpoint>` (`protocol="file"`) | dedicated `file:Listener` (from `transport.vfs.FileURI`) plus a service with a single `onCreate` remote function reading each newly-created file into the payload (as text, or as bytes when `transport.vfs.ContentType` is not a `text/…` type) and forwarding it to the referenced `sequence`; files already present when the service starts are handled once by a one-time directory scan in the project's `init()` function; `transport.vfs.ActionAfterProcess` `MOVE`/`DELETE` (with `MoveAfterProcess` for the target directory) is applied after a file is mediated successfully. There is no equivalent to Synapse's own poll interval, file locking/stability check, or `ActionAfterFailure`/`MoveAfterFailure` — see [Known limitations](#known-limitations). |

### Mediators

| Tag | Converted to |
|-----|--------------|
| `<payloadFactory>` | response payload |
| `<respond>` | response return |
| `<property>` (static name only) | response header, status code, or local variable |
| `<faultSequence>` (inline or resolved `key="…"` reference to a project-level sequence) | `on fail` clause of a `do` block wrapping the resource body |

## Unsupported constructs (TODOs)

The migration never aborts on an unsupported construct. Instead, every construct with no Ballerina
translation is surfaced as a TODO so the generated package still builds around the supported parts:

- **Unsupported mediators** (e.g. `<log>`, `<filter>`, `<switch>`, `<call>`) become a `// TODO` comment
  in the generated function body, carrying the original Synapse XML and its source file. For a
  control-flow wrapper (`<filter>`, `<switch>`, `<foreach>`, `<iterate>`, `<aggregate>`, `<clone>`), the supported
  mediators nested in its branches are still converted best-effort (the wrapper's control flow is not
  applied — the TODO flags that it needs manual restructuring).
- **Unsupported top-level artifacts** (e.g. `<proxy>`, `<endpoint>`) are reported in
  `migration_report.md` (they have no Ballerina construct to host an inline comment).
- **`<inboundEndpoint>` protocols other than `http`, `https`, `jms`, `file`** (e.g. `ws`, `kafka`,
  `mqtt`, a `class=…` custom Java endpoint) have no generated listener equivalent yet and are reported
  in `migration_report.md` the same way an unsupported top-level artifact is. An `inboundEndpoint`
  parameter not mapped by its protocol (e.g. `transport.jms.ConnectionFactoryJNDIName`, `interval`,
  `transport.vfs.FileNamePattern`) is likewise reported rather than silently ignored.
- **A `<respond/>` mediator reached while converting a `jms`/`file` inbound endpoint's sequence** (there
  is no reply transport on these protocols) becomes an inline `// TODO` comment and is recorded in the
  report, instead of being converted; the default/implicit fault handler for these protocols only logs
  the error rather than also responding.
- **Unsupported `<property>` scopes / `remove` actions** and **unresolved `<sequence key="…"/>`
  references** become inline `// TODO` comments and are recorded in the report.

Every unsupported case is also aggregated into a `migration_report.md` at the package root (source file
+ original Synapse code per entry). The report is written only when there is at least one unsupported
case; under `--dry-run` it is printed instead of written.

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

- `<proxy>` services, `<log>`, `<filter>` and other mediators/artifacts are not converted, but they no
  longer fail the migration: they are surfaced as TODOs (see [Unsupported constructs](#unsupported-constructs-todos)).
- `<outSequence>` (the out flow) is not yet migrated. `<faultSequence>` (the error flow) is now
  supported — an unresolved `key="…"` reference or a resource with no fault sequence at all falls back
  to the project-level default and is reported if unresolved.
- The response payload is set with a generic setter rather than media-type-specific ones (e.g. JSON/text/XML setters).
- The shared HTTP listener every `<api>` service binds to is fixed (port `8080`) and is not derived
  from the source artifact. An `<inboundEndpoint>` is the exception: it gets its own dedicated
  listener, e.g. an `http`/`https` one with the port read from its `inbound.http.port` parameter.
- `https` inbound endpoints model server-authentication TLS only: a `keystore` parameter becomes the
  listener's `secureSocket.key`. Mutual TLS (`truststore`/`SSLVerifyClient`) and every other TLS-related
  parameter (`HttpsProtocols`, `SSLProtocol`, `CertificateRevocationVerifier`) are reported as unsupported,
  and an `https` endpoint with no `keystore` at all falls back to a plain, unencrypted `http:Listener`.
- A `jms` inbound endpoint's generated `jms:Listener` always binds to a queue; a
  `transport.jms.DestinationType="topic"` parameter is reported as unsupported and a queue listener is
  still generated best-effort.
- A named `<sequence>` shared between an `<api>` (or an `http`/`https` inbound endpoint) and a
  `jms`/`file` inbound endpoint is converted once, independent of caller. A `<respond/>` inside such a
  shared sequence's main body (as opposed to its fault path) is not caught by the jms/file
  unsupported-respond check described above — verify manually if a sequence is reused this way.
- A `file` inbound endpoint's pre-existing-file backfill (a one-time `file:readDir` scan in the
  project's `init()`) and its `file:Listener` (which only reports files created after it starts) are
  not atomic with each other: a file created in the brief window between the two may be processed
  twice or missed entirely. Neither the backfill nor `onCreate` waits for a file to finish being
  written before reading it — `file:Listener` has no equivalent to Synapse's own file-locking/stability
  checks, so a file read while still being written may be read incomplete. There is also no equivalent
  to Synapse's configurable poll interval (`file:Listener` is event-driven, not interval-polling), and
  `transport.vfs.ActionAfterFailure`/`MoveAfterFailure` (the failure-path counterparts of
  `ActionAfterProcess`/`MoveAfterProcess`) are not implemented — a failed file is left in place.
