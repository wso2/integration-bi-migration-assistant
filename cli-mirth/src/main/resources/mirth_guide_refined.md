User Guide for Mirth® Connect by
NextGen Healthcare, 4.5

Introduction to Mirth Connect

Mirth® Connect is an open source standards-based healthcare integration engine that enables
interoperability between two systems by accepting incoming information packets, called messages, and
processing them according to rules you provide. It speeds message routing, filtering, and transformation
between health-info systems over various messaging protocols (e.g., HL7, X12, EDI, DICOM, XML).

This user guide provides you with an overview of Mirth® Connect and instructions on how to install the
software and use it to transfer or share healthcare data with security and ease.

About Mirth Connect

Like an interpreter who translates foreign languages into a language you understand, Mirth® Connect
translates message standards into a language your system understands. Whenever a "foreign" system
sends you a message, Mirth® Connect with its integration capabilities expedite the following:

•  Filtering: Mirth® Connect reads message parameters and either passes the message on or stops

it on its way to the transformation stage.

•  Transformation: Mirth® Connect converts the incoming message standard to another standard,
such as from Health Level Seven (HL7) to XML.

•  Extraction: Mirth® Connect can pull data from and push data to a database.

•  Routing: Mirth® Connect ensures that messages arrive at their assigned destinations.


The server's maximum available memory (Java max heap size). By default this is
256 MB, but for large production instances you will typically want to increase this
value.

Main Log Level Database Log
Level Channel Log Level

These fields enables you to select the applicable log level from the drop-down
menus. Available options include:

•  ERROR

•  WARN

•  INFO

•  DEBUG

•  TRACE

Depending on the log level, messages of the selected level or lower will pass into
that level's log when the system logs a certain-level message.



The Fundamentals of Mirth Connect

As explained in the Introduction to Mirth Connect (on page 20) section, Mirth® Connect is an
integration engine that can receive data from a variety of sources and take powerful actions on that data,
including sending the data out to multiple external systems. It can also transform data from one format
to another, or extract pieces of the data that you can act on or send outbound. The interfaces you
configure that perform these jobs are called channels.

About Channels and Connectors

A Mirth® Connect channel consists of multiple connectors. A connector is a piece of a channel that
does the job of getting data into Mirth® Connect (a source connector) , or sending data out to an
external system (a destination connector) . Every channel has exactly one source connector, and at least
one destination connector. Because a channel may have multiple destination connectors, it can be
thought of as a process that obtains data from a particular source, and sends it out to many different
places. For example you may receive data over HTTP, then write the data out to a file somewhere, and
also insert pieces of the data into your custom database.

Channel Components

•  General Channel Properties (on page 61)

•  Source Connector (on page 61)

•  Destination Connectors (on page 62)

Page 60 of 619

Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

•  Channel Scripts (on page 62)

General Channel Properties

General Channel properties are configured on the Summary Tab (on page 204) within the Edit Channel
View (on page 203) and include:

•  Unique ID, name, and description.

•  Links to Code Template Libraries (on page 213): These let the channel know which custom

functions are available in specific JavaScript contexts.

•  Links to Library Resources (on page 214): These let the channel know which custom Java

classes are available in specific connectors and/or JavaScript contexts.

•  Deploy/Start Dependencies (on page 215): These determine which channels are dependent on
this one, and also which channels are dependencies for this one. This way you can have some
channels deploy and start before others.

•  Attachment Handler (on page 219): This allows you to extract pieces of any incoming message
and store them separately. As a message processes through a channel, multiple copies of it will
be held in memory at once (for the raw / transformed / encoded versions of a message).
Attachments are stored only once, so by using them you can greatly reduce your channels'
memory footprint.

•  Message Storage Settings (on page 227): These determine how much message data to store /

retain and whether to encrypt content. These settings affect the performance of the channel and
also determine whether you can enable persistent queuing on your connectors.

•  Message Pruning Settings (on page 229): These determine how long to keep message data
around before automatically removing it with the Data Pruner (on page 190). You can also
decide to archive data out to a file somewhere before pruning it.

•  Custom Metadata Columns (on page 230): These allow you to extract pieces of data from your
message and store them in dedicated columns in the internal Mirth® Connect database. You can
then view and search on them in the Message Browser (on page 103).

Source Connector

Every channel has exactly one source connector which gets data into Mirth® Connect from an external
system. The source connector is configured on the Source Tab (on page 232) within the Edit Channel
View (on page 203). In addition to the standard Connector Components, source connectors include:


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute.

Page 61 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

•  A Source Queue that can be enabled or disabled. When enabled, the channel acts as a store-

and-forward service that can receive messages and send acknowledgements immediately to the
originating system, without having to wait for the message to process through the entire
channel.

•  A Batch Processor that can be enabled or disabled. When enabled, the channel takes any
incoming data and splits it into multiple messages that each proceed discretely through the
channel. See Batch Processing (on page 337) for additional information.

•  A Response Selector that determines what response to send back to the originating system, if
applicable. You can choose to auto-generate a response based on the inbound data type of the
source transformer. You can also return the response from a specific destination, or a completely
custom response.

•  A Max Processing Threadsoption. By default this is set to 1, meaning that only one message
can be processed through a channel at any given time. This \does not include asynchronous
processes like the destination queue. Increasing this setting can greatly improve channel
performance / throughput, at the cost of message order preservation.

Source Connector Properties

This section refers to the actual connector-specific settings. Here is a list of source connectors supported
by Mirth® Connect

•  Source Connectors (on page 340)


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 245 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

•  Channel Reader (on page 340)

•  DICOM Listener (on page 341)

•  Database Reader (on page 346)

•  File Reader (on page 351)

•  HTTP Listener (on page 362)

•  JMS Listener (on page 366)

•  JavaScript Reader (on page 371)

•  TCP Listener (on page 372)

•  Web Service Listener (on page 381)

Additional source connectors are made available as commercial extensions (on page 582):

•  Email Reader (on page 588)

•  Serial Connector (on page 597)

•  FHIR Connector (on page 590)

•  Interoperability Connector Suite (on page 592)

Destinations Tab

The Destinations tab is where destination connectors are configured. This includes the destination
connector properties, the destination filter / transformer scripts, and the response transformer scripts.
From this tab you can rename / reorder / enable / disable / clone destinations, and decide which ones
belong in separate destination chains.

Page 246 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Configuration of destination connectors is separated into the following sections:

Destination Table

This table shows you all currently configured destinations for your channel. You can see at a glance the
type of each destination, whether it is enabled, what chain it belongs to, and more.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 247 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Column

Status

Destination

Description

Indicates whether the destination is enabled or disabled. Only enabled destinations
process messages. A channel must have at least one destination enabled.

Double-click this cell to edit the name of the destination. Note that while a destination
may be renamed, its metadata ID will remain the same.

Id

The metadata ID that uniquely identifies this destination within the current channel.

Connector Type

The type of destination connector. To change this, select the destination from the table
and choose a new type from the drop-down menu directly below the table.

Chain

The chain this destination connector belongs to. The first destination in the table is
automatically placed into chain #1. To start a new chain, select a subsequent
destination from the table, and uncheck the Wait for previous destination check box
directly below the table.

Destination Tasks

In addition to the general Edit Channel Tasks (on page 256), several context-specific tasks are unique to
the Destinations Tab (on page 246):

Page 248 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Task Icon

Task Name

Description

New Destination

Creates a new destination and adds it to the table
above.

Delete Destination

Deletes the currently selected destination and removes
it from the table above. Note that a channel must have
at least one enabled destination.

Clone Destination

Copies the currently selected destination and adds it to
the table above.

Enable Destination

Marks this destination as ready to process messages
at deploy time.

Disable Destination

Marks this destination as not ready to process
messages at deploy time. Note that a channel must
have at least one enabled destination.

Move Destination Up

Moves the currently selected destination one row
higher in the table above.

Move Destination Down

Moves the currently selected destination one row lower
in the table above.

Edit Response

Enters the Edit Transformer View (on page 257) for the
currently selected destination's response transformer.

Destination Settings

These are general settings that apply to all destination connectors. They include configuring the
destination queue, whether to validate responses, and whether to re-attach attachments on outbound
messages.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 249 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

A

Name

Description

Queue Messages

•  Never: Disable the destination queue.

•  On Failure: Attempt to send the message first before queuing

it. This will allow subsequent destinations and the
Postprocessor to use the response from this destination if it
successfully sends before queuing.

•  Always: Immediately queue the message. Subsequent
destinations and the Postprocessor will always see this
destination's response as QUEUED.

B

C

D

Advanced Queue
Settings (on page 251)

Configure how often to re-attempt queued messages, increase queue
threads, and more.

Validate Response

Select Yes to validate the response. Responses can only be validated if
the response transformer's inbound properties contains a Response
Validation section. If validation fails, the message will be marked as
queued or errored. For additional information, see Data Types (on page
323).

Reattach Attachments

•

•

If enabled, replacement tokens using the ${ATTACH:...} syntax
are automatically replaced with the associated attachment
content before the message is sent.

If disabled, the tokens are expanded to the full
${ATTACH:channelId:messageId:attachmentId} syntax which
can then be reattached in downstream channels.

For additional information, see Attachment Handlers (on page 219).

Page 250 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

Advanced Queue Settings

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Description

A

B

C

D

E

F

Retry Count Before Queue/
Error

The maximum number of times the connector attempts to send
the message before queuing or erroring.

Retry Interval (ms)

Rotate Queue

Regenerate Template

The amount of time (in milliseconds) that should elapse between
retry attempts to send messages. This interval applies to both
the queue and initial retry attempts.

If enabled, when any message fails to be sent from the queue,
the connector will place the message at the end of the queue
and attempt to send the next message. This will prevent a single
message from holding up the entire queue. If the order of
messages processed is important, this should be disabled.

Regenerate the template and other connector properties by
replacing variables each time the connector attempts to send the
message from the queue. If this is disabled, the original variable
replacement is used for each attempt.

Include Filter/Transformer

If enabled, the filter and transformer is re-executed before every
queue send attempt. This is only available when the Regenerate
Template setting is enabled.

Queue Threads

The number of threads that will read from the queue and
dispatch messages simultaneously. Message order is NOT
guaranteed if this value is greater than one, unless an
assignment variable is used below.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 251 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

G

Name

Description

Thread Assignment Variable  When using multiple queue threads, this map variable

H

Queue Buffer Size

determines how to assign messages to specific threads. If
rotation is disabled, messages with the same thread assignment
value will always be processed in order.

The buffer size for the destination queue. Up to this many
connector messages may be held in memory at once when
queuing.


Source Connectors

This section refers to the actual connector-specific settings for the source connector. The section is
labeled according to the connector type, e.g. "HTTP Listener", "JavaScript Reader". For additional
information on connectors in general, go here: About Channels and Connectors (on page 60)

Here is a list of source connectors supported by Mirth® Connect by NextGen Healthcare

•  Channel Reader (on page 340)

•  DICOM Listener (on page 341)

•  Database Reader (on page 346)

•  File Reader (on page 351)

•  HTTP Listener (on page 362)

•  JMS Listener (on page 366)

•  JavaScript Reader (on page 371)

•  TCP Listener (on page 372)

•  Web Service Listener (on page 381)

Additional source connectors are available as commercial extensions (on page 582):

•  Email Reader (on page 588)

•  Serial Connector (on page 597)

•  FHIR Connector (on page 590)

•  Interoperability Connector Suite (on page 592)

Channel Reader

The Channel Reader is a connector that does nothing but wait for other channels / processes to send it

Page 340 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

messages. This can be useful if you split your message workflow into multiple channels, where one
sends to another. Note that you do not need to use a Channel Reader source for the channel to be able to
receive messages from other internal channels / processes. Channels using other source connector types
can still receive messages from a Channel Writer (on page 384) or from a "router.routeMessage" call.

Supported property groups:

•  Source Settings (on page 238)

Source Map Variables

If this connector receives a message from a Channel Writer (on page 384), the following source map (on
page 449) variables will be available:

Key

sourceChannelId

Description

The unique ID of the channel that dispatched a message to the current
channel.

sourceMessageId

The ID of the message from which the current message dispatch originated.

sourceChannelIds

sourceMessageIds

If there are more than two channels in a Channel Writer -> Reader chain, this
will be a List containing the IDs of all channels in the chain.

If there are more than two channels in a Channel Writer -> Reader chain, this
will be a List containing the IDs of all messages in the chain.

DICOM Listener

This source connector works in conjunction with the DICOM Attachment Handler (on page 224) and
the DICOM Data Type (on page 327) to allow Mirth® Connect to receive and consume DICOM data.
This connector supports the C-STORE operation as a Service Class Provider (SCP). Additional options
are available with the SSL Manager (on page 598) extension.

Supported property groups:

•  Listener Settings (on page 233)


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 341 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

•  Source Settings (on page 238)

Item

Name

Default
Value

Description

A

B

C

D

E

F

Application Entity

If specified, only requests with a matching Application Entity
title will be accepted.

Max Async
operations

0

Maximum number of outstanding operations performed
asynchronously, unlimited by default.

Pack PDV

No

Send only one PDV in one P-Data-TF PDU, pack command
and data PDF in one P-DATA-TF PDU by default.

DIMSE-RSP
interval period (s)

P-DATA-TF PDUs
max length sent
(KB)

10

16

A-RELEASE-RP
timeout (s)

5

Period to check for outstanding DIMSE-RSP, 10 seconds by
default.

Maximal length in KB of sent P-DATA-TF PDUs, 16 KB by
default.

Timeout for receiving A-RELEASE-RP, 5 seconds by default.

Page 342 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default
Value

Description

G

H

I

J

K

L

M

N

O

P

Q

R

S

T

P-DATA-TF PDUs
max length
received (KB)

Socket Close Delay
After A-ABORT
(ms)

Send Socket Buffer
Size (KB)

ASSOCIATE-RQ
timeout (ms)

Receive Socket
Buffer Size (KB)

16

50

0

5

0

DIMSE-RQ timeout
(ms)

60

Transcoder Buffer
Size (KB)

DIMSE-RSP delay
(ms)

1

0

Accept Explicit VR
Big Endian

No

Only Accept Default
Transfer Syntax

No

Maximal length in KB of received P-DATA-TF PDUs, 16 KB by
default.

Delay in ms for Socket close after sending A-ABORT, 50 ms by
default.

Send socket buffer size in KB

Timeout in ms for receiving ASSOCIATE-RQ, 5 seconds by
default.

Receive socket buffer size in KB

Timeout in ms for receiving DIMSE-RQ, 60 ms by default.

Minimal buffer size to write received object to file, 1 KB by
default.

Delay in ms for DIMSE-RSP; useful for testing asynchronous
mode.

Accept explicit value representation Big Endian transfer syntax.

Accept only the default transfer syntax.

Only
Uncompressed
Pixel Data

No

Accept only transfer syntax with uncompressed pixel data.

TCP Delay

Yes

Set TCP_NODELAY socket option to false, true by default.

Store Received
Objects in Directory

Store received objects into files in specified directory.

TLS

No TLS

Determines whether to receive data over an implicit SSL/TLS
socket. The following options are available:

•  3DES: TLS will be used, with the

SSL_RSA_WITH_3DES_EDE_CBC_SHA cipher
suite.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 343 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default
Value

Description

•  AES: TLS will be used, with the following cipher

suites:

•  TLS_RSA_WITH_AES_128_CBC_SHA

•  SSL_RSA_WITH_3DES_EDE_CBC_SHA

•  Without: TLS will be used without symmetric

encryption, with the cipher suite
SSL_RSA_WITH_NULL_SHA. DICOM messages
will be received unencrypted.

•  No TLS: No TLS will be used. DICOM messages will

be received over a regular unencrypted socket.

U

V

W

X

Y

Z

Client
Authentication TLS

Yes

Enable client authentication for TLS. Only applicable if the TLS
option is not set to No TLS.

Accept ssl v2 TLS
handshake

Yes

Enable acceptance of the SSLv2Hello protocol in the TLS
handshake.

Keystore

File path or URL of P12 or JKS keystore to use for the local
server certificate keypair.

Keystore Password

Password for the configured Keystore.

Trust Store

Trust Store
Password

File path or URL of JKS truststore, used to trust remote client
certificates.

Password for the configured Truststore.

AA

Key Password

Password for accessing the key in the Keystore.

Source Map Variables

Key

localApplicationEntityTitle

Description

The Application Entity Title of the local Service Class Provider
(SCP).

remoteApplicationEntityTitle

The Application Entity Title of the remote Service Class User (SCU).

localAddress

The IP address that the TCP socket is locally bound to.

Page 344 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Key

localPort

remoteAddress

remotePort

Description

The port that that TCP socket is locally bound to.

The IP address of the remote connecting client.

The TCP port of the remote connecting client.

associateACProtocolVersion

The associate protocol version of the local SCP.

associateACImplClassUID

The associate implementation class unique identifier of the local
SCP.

associateACImplVersionName

The associate implementation version name of the local SCP.

associateACApplicationContext

The associate application context of the local SCP.

associateACPresentationContexts

A map containing all supported presentation contexts of the local
SCP.

associateRQProtocolVersion

The associate protocol version of the remote SCU.

associateRQImplClassUID

The associate implementation class unique identifier of the remote
SCU.

associateRQImplVersionName

The associate implementation version name of the remote SCU.

associateRQApplicationContext

The associate application context of the remote SCU.

associateRQPresentationContexts

A map containing all supported presentation contexts of the remote
SCU.

username

passcode

userIdentityType

The username presented by the remote SCU, if available.

The passcode presented by the remote SCU, if available.

The type of user identity presented by the remote SCU, if available.
Will be one of the following values:

•  USERNAME

•  USERNAME_PASSCODE

•  KERBEROS

•  SAML


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 345 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Database Reader

This Database Reader connects to an external database, performs a query, and reads selected rows into
messages that get dispatched to the channel. This can be done using a SQL statement, or by using
JavaScript mode to perform the query manually. The database connection will automatically be kept
open across multiple polling windows, unless otherwise specified. This connector also supports a Post-
Process section where an update statement can be performed after each row is read in, for example to set
a processed flag in the source table. The values selected from the query will be automatically converted
into an XML document where each column will be a separate node. That XML document is what
actually gets dispatched to the channel as a message.

Supported property groups:

•  Polling Settings (on page 234)

•  Source Settings (on page 238)

Item

A

Name

Driver

Default Value

Description

Specifies the type of database driver to use
to connect to the database. The following
values are supported by default:

Page 346 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

•  Sun JDBC-ODBC Bridge

•  MySQL

•  Oracle

•  PostgreSQL

•  SQL Server / Sybase

•  SQLite

Additional drivers can be added by editing
dbdrivers.xml File (on page 480).

The JDBC URL to connect to the database
with. This is not used when Yes for Use
JavaScript is checked. However, it is used
when the Generate Connection / Select
feature is used to generate code. Use the
Insert URL Template button above to
populate the URL field with a starting
template.

The username to connect to the database
with. This is not used when Yes for Use
JavaScript is checked. However, it is used
when the Generate Connection / Select
feature is used to generate code.

The password to connect to the database
with. This is not used when Yes for Use
JavaScript is checked. However, it is used
when the Generate Connection / Select
feature is used to generate code.

If enabled, the below JavaScript scripts will
be used to select messages and run a post-
process update. If disabled, SQL code (either
standard or database-specific) may be used,
and the connection will be handled
automatically.

Re-use the same database connection each
time the select query is executed. If disabled,
the connection will be closed after all
selected messages have finished processing.

If enabled, all rows returned in the query will
be aggregated into a single XML message.
Note that all rows will be read into memory at

B

URL

C

D

E

F

G

Username

Password

Use JavaScript

No

Keep Connection
Open

Yes

Aggregate Results

No


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 347 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

H

I

J

K

L

Cache Results

Yes

Fetch Size

1000

# of Retries on Error

3

Retry Interval

10000

Encoding

Default

M

Generate

N

SQL / JavaScript

O

Run Post-Process
SQL / JavaScript

Never

once, so use this with caution.

Cache the entire result set in memory prior to
processing messages.

The JDBC ResultSet fetch size to be used
when fetching results from the current cursor
position.

The number of times to retry executing the
statement or script if an error occurs.

The amount of time that should elapse
between retry attempts.

Select the character set encoding used to
convert binary data into message strings, or
select Default to use the default character set
encoding for the JVM Mirth® Connect is
running on.

•  Connection: This button is

enabled when Use JavaScript is
enabled. When selected, it inserts
boilerplate Connection construction
code into the JavaScript pane at
the current caret position.

•  Select: Opens a window to assist
in building a select query to select
records from the database
specified in the URL above.

The actual SQL or JavaScript code to
execute for each polling window. When
JavaScript mode is used, the return value of
the script is expected to be a ResultSet or a
List<Map<String, Object>> (a list of maps,
where each entry in each map has a String
key and any object value).

Determines whether the post-process update
script is active, and if so whether to execute it
after each message or only once after all
messages in the ResultSet have completed.If
Aggregate Results is disabled:

Page 348 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

•  Never: Do not run the post-process

statement/script.

•  After each message: Run the

post-process statement/script after
each message finishes processing.

•  Once after all messages: Run the
post-process statement/script only
after all messages have finished
processing.

If Aggregate Results is enabled:

•  Never: Do not run the post-process

statement/script.

•  For each row: Run the post-

process statement/script for each
row in the result set.

•  Once for all rows: Run the post-

process statement/script only once.
If JavaScript mode is used, a List of
Maps representing all rows in the
result set will be available as the
variable "results".

•  Connection: This button is

enabled when Use JavaScript is
enabled and a post-process script
is being used. When selected, it
inserts boilerplate Connection
construction code into the
JavaScript pane at the current
caret position.

•  Update: Opens a window to assist
in building an update statement to
update records in the database
specified in the URL above. Only
enabled if a post-process
statement/script is being used.

P

Generate (post-
process)

Q

SQL / JavaScript
(post-process)

The actual SQL or JavaScript code to
execute after each row/message or after all
rows/messages have completed.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 349 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

R

Name

Result Map

Default Value

Description

When using the After each message / For
each row post-process option, values
originally selected using the query above will
be available in the SQL or JavaScript context.
Drag the entries from this section into the
post-process script to use them in your
update statement. For example if you
selected a unique ID column in your initial
query, you may want to use that same value
to update the table and set a processed flag.

Editing Database Drivers

Select on the wrench icon next to the Driver text field. A new dialog will appear:

From this dialog you can add/modify/remove the default drivers that will show up in the drop-down
menu in Database Reader/Writer connectors. The following properties are used:

Required

Description

Column

Name

Driver Class

JDBC URL Template

Yes

Yes

Yes

Select with Limit Query

No

Legacy Driver Classes

No

The name of the driver entry. This will appear in the
drop-down menu for the Database Reader/Writer
connectors.

The fully-qualified Java class name for the JDBC driver.

The template for the JDBC connection URL that can be
auto-populated from the Database Reader/Writer
settings.

A select query (with limit 1) that can be used to retrieve
column metadata. If empty the driver-specific generic
query will be used, which could be slow.

A comma-separated list of alternate or legacy JDBC
driver class names. Any Database Reader/Writer

Page 350 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Column

Required

Description

connector using one of these driver classes will have
the corresponding entry selected in the Driver drop-
down menu. The driver will be updated to the primary
value if you select the entry from the drop-down menu
again.

File Reader

This source connector reads files from a local or remote directory on a specified interval/time schedule.
Several protocols are supported, including regular local file mode, FTP, SFTP, SMB, WebDAV, and
Amazon S3. Files may be read in and converted to Base64, or converted to a message string using a
specific character set encoding. After reading in files, the connector has options to either delete the
original files, rename them, or move them to a separate directory. Additional options (like FTPS) are
available with the SSL Manager (on page 598) extension.

Supported property groups:

•  Polling Settings (on page 234)

•  Source Settings (on page 238)


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 351 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

A

Name

Method

Default Value

Description

file

The basic method used to access files to
be read in. Options include File (local
filesystem or NFS / mapped share), FTP,
SFTP, SMB, WebDAV, or Amazon S3.
Once all necessary connection/directory
information has been filled in before, use
the Test Read button to attempt to
actually connect and test the ability to
read from the directory.

Page 352 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

Item

B

C

D

E

F

G

H

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Name

Default Value

Description

Advanced Options

Directory

URL

Filename Filter
Pattern

*

Include All
Subdirectories

No

Ignore . files

Yes

Anonymous

Yes

If the file method supports advanced
options, this button will be enabled.
Advanced options are summarized in the
Advanced Options label. For additional
information, see:

•  Advanced FTP Options (on

page 357)

•  Advanced SFTP Options (on

page 357)

•  Advanced SMB Options (on

page 359)

•  Advanced Amazon S3 Options

(on page 359)

Only applicable to the File method. The
directory (folder) in which the files to be
read can be found.

Applicable to all methods except File.
The domain name or IP address of the
host (computer) on which the files to be
read can be found. If this setting is
enabled, the second text field specifies
the directory (folder) to read from. When
using the Amazon S3 method, the first
text field will be the bucket name, and the
second text field can be used for a
directory prefix.

Files with names that do not match this
pattern will be ignored. If Regular
Expression is disabled, regular wildcard
(*) matching is supported.

Select Yes to traverse directories
recursively and search for files in each
one.

Select Yes to ignore all files starting with
a period.

Only applicable to the FTP / WebDAV /
Amazon S3 methods. If enabled,
connects to the remote server
anonymously instead of using a
username and password.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 353 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

I

J

K

L

M

N

O

P

Username

anonymous

Password

Timeout (ms)

10000

Secure Mode

Yes

Passive Mode

Yes

Validate Connection  Yes

After Processing
Action

None

Move-to Directory

Q

Move-to File Name

Applicable to all methods except File.
The username used to connect to the
remote server with. When using the
Amazon S3 mode, this will be your AWS
Access Key ID.

Applicable to all methods except File.
The password used to connect to the
remote server with. When using the
Amazon S3 mode, this will be your AWS
Secret Access Key.

Applicable to the FTP / SFTP / SMB /
Amazon S3 methods. The socket timeout
(in ms) to use when connecting to the
remote server.

Only applicable to the WebDAV method.
If enabled, HTTPS will be used instead of
HTTP.

Only applicable to the FTP method. If
enabled, the server decides what port the
client should connect to for the data
channel. Passive mode sometimes allows
a connection through a firewall that
normal mode does not, because the client
is initiating the data connection rather
than the server.

Only applicable to the FTP method. If
enabled, the connection will be tested for
validity before each operation.

Select Move to move and/or rename the
file after successful processing. Select
Delete to delete the file after successful
processing.

If successfully processed files should be
moved to a different directory (folder),
enter that directory here. The directory
name specified may include template
substitutions from the list to the right. If
this field is left empty, successfully
processed files will not be moved to a
different directory.

If successfully processed files should be
renamed, enter the new name here. The
filename specified may include template
substitutions from the list to the right. If

Page 354 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

R

S

T

U

Error Reading Action  None

Error in Response
Action

After Processing Action

Error Move-to
Directory

Error Move-to File
Name

V

Move-to Variables

this field is left empty, successfully
processed files will not be renamed.

Select Move to move and/or rename files
that have failed to be read in (for
example, if an out-of-memory error
occurs, or the network connection drops).
Select Delete to delete files that have
failed to be read in.

Select Move to move and/or rename the
file if an ERROR response is returned.
This action is triggered when the
Response selected in the Source Settings
(on page 238) has a status of ERROR. If
After Processing Action is selected, the
After Processing Action will apply. This
action is only available if Process Batch is
disabled in the Source Settings (on page
238).

If files which cause processing errors
should be moved to a different directory
(folder), enter that directory here. This
action is triggered when the Response
selected in the Source Settings (on page
238) has a status of ERROR. The
     directory name specified may include
     template substitutions from the list to the
     right. If this field is left empty, files which
     cause processing errors will not be
     moved to a different directory.

If files which cause processing errors
should be renamed, enter that directory
here. This action is triggered when the
Response selected in the Source Settings
(on page 238) has a status of ERROR.
The filename specified may include
template substitutions from the list to the
right. If this field is left empty, files which
cause processing errors will not be
renamed.

The variables listed here can be dragged-
and-dropped into the Move-to fields to the
left.

•  channelName: The name of

the current channel.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 355 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

•  channelId: The unique ID of

the current channel.

•  DATE: The current date,

formatted as a human-readable
string.

•  COUNT: A numeric count that
increases for each file read in,
from the point when the
channel was last deployed.

•  UUID: An auto-generated

universally unique identifier.

•  SYSTIME: The current epoch

time in milliseconds.

•  originalFilename: The name

of the file that was read in. Use
this to easily add on an extra
file extension to the original
name.

Select Yes to skip files that are created
within the specified age below.

If Check File Age is enabled, only the
files with creation dates older than the
specified value in milliseconds will be
processed.

The minimum and maximum size (in
bytes) of files to be accepted. If Ignore
Maximum is checked, the file size will
only be bound by the minimum value.

Selects the order in which files should be
processed, if there are multiple files
available. Files can be processed by Date
(oldest last-modification date first), Size
(smallest first), or Name (a before z, etc.).

Select Binary if files contain binary data;
the contents will be Base64 encoded
before processing. Select Text if files
contain textual data; the contents will be
encoded using the specified character set
encoding.

W

X

Y

Z

Check File Age

Yes

File Age (ms)

1000

File Size (bytes)

0, Ignore Maximum

Sort Files By

Date

AA

File Type

Text

Page 356 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

BB

Name

Encoding

Default Value

Description

Default

If Text is chosen for the File Type, select
the character set encoding ( ASCII,
UTF-8, etc.) to be used in reading the
contents of each file.

Advanced FTP Options

When the FTP file method is selected, these additional advanced options may be set:

Name

Initial Commands

Default Value

Description

A comma-separated list of custom
commands to run upon initializing an
FTP connection. For example when
connecting to an AS/400 FTP server
you may have to change the list
format using the "NAMEFMT"
command.

Advanced SFTP Options

When the SFTP file method is selected, these additional advanced options may be set:


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 357 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

A

B

C

D

E

F

Name

Default Value

Description

Authentication

Password

Public/Private Key Files

Passphrase

Host Key Checking

Ask

Known Hosts File

Configuration Options

Determines how to
authenticate to the SFTP
server. Options include
Password, Public Key, or
Both.

The absolute file path of the
public/private keypair used
to gain access to the
remote server.

The passphrase associated
with the public/private
keypair.

Select Yes to validate the
server's host key within the
provided Known Hosts file
(or the system default).
Otherwise the host key will
always be automatically
trusted.

The path to the local Known
Hosts file used to trust
remote host keys.

Custom JSch configuration
options used when

Page 358 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

connecting to the remote
server. For example, these
can be used to enabled
Kerberos authentication.

Advanced SMB Options

When the SMB file method is selected, these additional advanced options may be set:

Item

Default Value

Description

SMB Minimum Version

2.0.2

The minimum version of the SMB protocol to support. Note
that if you are upgrading from an earlier version of Connect,
this may still be set to v1. You should consider changing this
to at least v2 for best security practices

SMB Maximum Version

3.1.1

The maximum version of the SMB protocol to support.

Advanced Amazon S3 Options

When the Amazon S3 file method is selected, these additional advanced options may be set:


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 359 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

A

Name

Default Value

Description

Use Default
Credential Provider
Chain

Yes

If enabled and no explicit credentials are
provided, the default provider chain looks for
credentials in this order:

•  Environment variables:

AWS_ACCESS_KEY_ID and
AWS_SECRET_ACCESS_KEY

•  Java system properties:
aws.accessKeyId and
aws.secretKey

•  Default credentials profile file:

Typically located at ~/.aws/
credentials (location can very per
platform)

•  ECS container credentials:

Loaded from an Amazon ECS
environment variable.

•

Instance profile credentials:
Loaded from the EC2 metadata
service.

Note that if your File Reader has Anonymous
enabled, this option will not be enabled.

B

Use Temporary

No

If enabled, the given credentials will be used

Page 360 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

Item

Name

Credentials

C

D

E

Duration (seconds)

7200

Region

us-west-2

Custom HTTP
Headers

pollId

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Default Value

Description

to request a set of temporary credentials
from the Amazon Security Token Service
(STS). Those temporary credentials will then
be used for all S3 operations.

The duration that the temporary credentials
are valid. Must be between 900 seconds (15
minutes) and 129,600 seconds (36 hours).

The AWS region that your S3 bucket is
located in. Select a specific region from the
drop-down menu, or enter one into the text
field. You can also use Velocity Variable
Replacement (on page 469) here.

These headers will be used on any S3 PUT
operation. They are not used for GET
operations.To add user-defined metadata
tags to the S3 object, include a custom
header that starts with "x-amz-meta-".For
more information, check out the official
Amazon S3 documentation.

Source Map Variables

Key

Description

originalFilename

The name of the file that was read in.

fileDirectory

fileSize

The absolute path of the directory in which the file resides.

The size of the file in bytes.

fileLastModified

The last modified date of the file, as an epoch time in milliseconds.

pollId

pollSequenceId

pollComplete

A unique nanosecond timestamp that uniquely identifies the current polling
window. If your File Reader polls 5 files, the messages for each file will have
the same pollId.

An integer that starts at 1 and increments for every subsequent file in the
current polling window. If your File Reader polls 5 files, the message(s) for
the first file will have a pollSequenceId of 1, the message(s) for the second
file will have a pollSequenceId of 2, and so on.

This is only present for the last file in the current polling window. The value of
this entry is always equal to true. Use this to determine programmatically
whether you are currently working with the last file in a poll. Note that if you
have Batch Processing (on page 337) enabled, you will want to look at both


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 361 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Key

Description

the pollComplete and batchComplete variables to determine whether the
current message is truly the "last" one. If both are true, then you know that
you are on the last file in the polling window, and also on the last message in
that file.

Variables only applicable to the Amazon S3 mode

Key

Description

s3BucketName

The name of the S3 bucket that the file belongs to.

s3ETag

s3Key

s3Owner

s3StorageClass

s3Metadata

The hex encoded 128-bit MD5 hash of the file contents as computed by
Amazon S3.

The key under which the file is stored in S3.

The owner of the S3 object, if present.

The storage class used for this file in S3 (e.g. STANDARD,
STANDARD_IA).

A MessageHeaders object representing the map of metadata/headers for
the S3 object.

HTTP Listener

This source connector acts as an HTTP server, listening for requests from one or more remote clients.
The messages sent to the channel can be the raw payload, or an XML document allowing multipart
payloads to be parsed in a consistent and easy-to-use way. The HTTP payload can be either Base64
encoded or converted using a charset, depending on the Content-Type. Responses that go back to each
client can be fully configured, including custom response headers. Finally, static resources or directories
can be automatically hosted, to allow the connector to act as a simple web server that serves specific
content. Additional options are available with the SSL Manager (on page 598) extension.

Supported property groups:

•  Listener Settings (on page 233)

•  Source Settings (on page 238)

•  HTTP Authentication Settings (on page 239)

Page 362 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

A

B

C

Name

Default Value

Description

Base Context Path

The context path for the HTTP Listener
URL.

Note: If this is specified, any
requests made at this base

context path must have a trailing slash
in the request URI.

Receive Timeout (ms)

30000

The maximum idle time in milliseconds
for a connection.

Message Content

Plain Body

D

Parse Multipart

Yes

•  Plain Body: The request
body will be sent to the
channel as a raw string.

•  XML Body: The request body
will be sent to the channel as
serialized XML.

Applies only to Message Content XML
Body.

•  Select Yes to automatically

parse multipart requests into
separate XML nodes.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 363 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

E

Include Metadata

No

•  Select No to always keep the
request body as a single XML
node.

Applies only to Message Content XML
Body.Select Yes to include request
metadata (method, context path,
headers, query parameters) in the XML
content.

Note: Regardless of this setting,
the same metadata is always

available in the source map.

F

Binary MIME Types

application/.*
(?<!json|xml)$|
image/.*|video /.*|audio/.*

When a response comes in with a
Content-Type header that matches one
of these entries, the content will be
encoded into a Base64 string.

G

HTTP URL

<auto-generated>

•

•

If Regular Expression is
unchecked, specify multiple
entries with commas.

If Regular Expression is
checked,, enter a valid
regular expression to match
MIME types against.

Displays the generated HTTP URL for
the HTTP Listener. This is not an actual
configurable setting, but is instead
displayed for copy/paste convenience.

Note: The host in the URL is the
same as the host you used to

connect to the Administrator. The actual
host that connecting clients use may be
different due to differing networking
environments.

H

Response Content Type

text/plain

The MIME type to be used for the
response.

Page 364 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

I

Name

Default Value

Description

Response Data Type

Text

J

K

L

Charset Encoding

UTF-8

Response Status Code

Response Headers Map
Variable

Use Table

•

•

If Binary is selected,
responses is decoded from
Base64 into raw byte
streams.

If Text is selected, responses
is encoded with the specified
character set encoding.

Select the character set encoding to be
used for the response to the sending
system. Set to Default to assume the
default character set encoding for the
JVM Mirth® Connect by NextGen
Healthcare is running on.

Enter the status code for the HTTP
response.

•

•

If this field is left blank, a
default status code of 200 is
returned for a successful
message, and 500 is returned
for an errored message.

If a Response is chosen in
the Source Settings (on page
238), the status of that
response is used to
determine a successful or
errored response.

•  Use Table: The table below
will be used to populate
response headers.

•  Use Map: The Java map
specified by the following
variable will be used to
populate response headers.
The map must have String
keys and either String or
List<String> values.

M

Response Headers

When using the Use Table option
above, enter custom headers to send


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 365 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

N

Static Resources

back to the originating client

Values in this table are automatically
sent back to any request with the
matching context path. There are three
resource types:

•  File: The value field specifies
the path of the file to return.

•  Directory: Any file within the
directory given by the value
field may be requested, but
subdirectories are not
included.

•  Custom: The value field itself
is returned as the response.

JMS Listener

This source connector connects to an external JMS provider and reads messages from a queue or topic.
It supports both JNDI and specifying a specific connection factory, as well as fine-tuned queries through
a configurable selector. Once this connector is started, it will attempt to keep a persistent open
connection to the JMS provider. If for any reason the connection is dropped, the connector will
automatically reconnect without any intervention needed. The properties view also includes a
mechanism to save configuration templates for common provider types, so that creating a new JMS
Listener is as quick and easy as possible.

Note: Depending on the JMS connection provider you are using, you may need to include some
external libraries (JARs) as a Library Resource (on page 186) and include them using the Set

Dependencies (on page 214) dialog on the Channel Summary tab.

For example, to use connection templates included for ActiveMQ and JBoss, you should download the
libraries from their official site in order to use them with the JMS connectors.

Supported property groups:

•  Source Settings (on page 238)

Page 366 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

A

Name

Use JNDI

B

C

D

E

F

Provider URL

Initial Context Factory

Connection Factory Name

Connection Factory Class

Connection Properties

Default Value

Description

No

Select Yes to use JNDI to
look up a connection factory
to connect to the queue or
topic. Select No to specify a
connection factory class
without using JNDI.

If using JNDI, enter the
URL of the JNDI provider
here.

If using JNDI, enter the
fully-qualified Java class
name of the JNDI Initial
Context Factory class here.

If using JNDI, enter the
JNDI name of the
connection factory here.

If using the generic JMS
provider and not using
JNDI, enter the fully-
qualified Java class name
of the JMS connection
factory here.

This table allows you to
enter custom connection
factory settings. The
Property column is the key,
while the Value column is


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 367 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

G

H

I

J

K

L

M

N

Username

Password

Destination Type

Queue

Destination Name

Client ID

Reconnect Interval (ms)

10000

Selector

Connection Templates

the actual value for the
setting. The specific
properties used here will
vary depending on what
connection factory class /
provider you are using.

The username for
accessing the queue or
topic.

The password for accessing
the queue or topic.

Specify whether the
destination is a queue or
topic. When connecting to a
topic, you can check the
Durable checkbox so that
all messages published to
the topic will be read,
regardless of whether or not
a connection to the broker
is active. If unchecked, only
messages published while
a connection is active will
be read.

The name of the queue or
topic.

The JMS client ID to use
when connecting to the
JMS broker.

The number of milliseconds
between reconnect
attempts in the case that a
connection error occurs.

Enter a selector expression
to select specific messages
from the queue/topic. Leave
blank to read all messages.

This section allows you to
save the current state of
your JMS Listener
properties into a template,
which may then be restored
later if you make changes,
or may also be applied to

Page 368 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

other JMS Listener
connectors. For additional
information, see JMS
Connection Templates (on
page 369).

JMS Connection Templates

This section allows you to save the current state of your JMS Listener properties into a template, which
may then be restored later if you make changes, or may also be applied to other JMS Listener
connectors. Custom templates can be updated and deleted.

Note: Updating or deleting an existing template does not affect any connectors currently using that
template.

Note: For convenience, the JMS Listener comes with two reserved templates,"ActiveMQ"
and"JBoss Messaging / MQ". These cannot be updated or deleted, however you can load the

template, update the configuration as needed, and then save it as a new template.

Loading Templates

Select a template in the Connection Templates list, then select the Load button. You will be prompted to
overwrite your current JMS Listener settings:

Creating New Templates

Configure your JMS Listener settings to the state you want to save, then select the Save button in the
Connection Templates section. You will be prompted to give the template a name:


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 369 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

After selecting OK, the new template will appear in the Connection Templates list:

Updating Templates

To update a current template, follow the directions for creating a new template, then enter the same
name as the template you wish to update. You will be prompted to overwrite:

Deleting Templates

Select a template in the Connection Templates list, then select the Delete button to delete a template.
You will be prompted to confirm the action:

Page 370 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

JavaScript Reader

This source connector executes a custom user-defined JavaScript script on a specified schedule. This can
be used in a wide variety of ways, such as calling out to external Java libraries or invoking a local OS
shell script. You can return a message (or list of messages) to dispatch to the channel, or simply use the
script as a scheduled job that doesn't necessarily produce messages. For example, you can use tools like
ChannelUtil (on page 459) to programmatically start/stop/deploy channels from within the script.

Supported property groups:

•  Polling Settings (on page 234)

•  Source Settings (on page 238)

JavaScript Reader Return Values

If you are using the JavaScript Reader to produce messages for the channel, all you need to do is return
those messages from the script. The following return values are accepted:

•  String: Any non-empty string returned will be sent to the channel as a message.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 371 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

•  RawMessage: This is a special object that contains not only the string message data, but also
information about which destinations to dispatch to, and any source map variables you wish to
inject. For additional information, see The User API (Javadoc) (on page 459).

•  List: If a Java List is returned, all values in the list will be sent to the channel as discrete
messages. The list may contain a mix of Strings, RawMessage objects, or other objects.

•  Empty String / null / undefined: Returning any of these (including just a "return;" statement or

no return statement at all) will cause no messages to dispatch to the channel.

•  Any Object: Any other object returned will be converted to a String via the toString() method,

and that String representation will be sent to the channel as a message.

TCP Listener

This source connector listens for messages coming in over a TCP connection. It can either listen on a
TCP interface/port and wait for clients to connect, or connect to an external server. There are options to
decide when to keep connections open, and how many clients can connect at once. Configurable
transmission modes (on page 376) allow you to decide how to receive inbound messages and send
responses. When sending responses, you can choose to send the data back on the same connection, or on
a new connection.

Supported property groups:

•  Listener Settings (on page 233)

•  Source Settings (on page 238)

Page 372 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

A

B

C

Name

Default Value

Description

Transmission Mode

MLLP

Sample Frame

<VT> <Message Data>
<FS> <CR>

Mode

Server

The transmission mode determines how to
receive message data from the incoming
byte stream, and how to send responses
out. For additional information, see TCP
Transmission Modes (on page 376).

An example of a valid incoming message.
This is dependent on the Transmission
Mode.

•  Select Server to listen for
connections from clients.

•  Select Client to connect to a

TCP Server. In Client mode, the
Listener Settings (on page 233)
is only used if Override Local
Binding is enabled.

D

Remote Address

The domain name or IP address on which


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 373 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

E

F

G

H

I

J

K

Remote Port

Override Local Binding

No

Reconnect Interval (ms)  5000

Max Connections

10

Receive Timeout (ms)

0

Buffer Size (bytes)

65536

Keep Connection Open  Yes

to connect. Only applicable for Client
mode.

The port on which to connect. Only
applicable for Client mode.

•  Select Yes to override the local
address and port that the client
socket will be bound to.

•  Select No to use the default

values of 0.0.0.0:0. A local port
of zero (0) indicates that the OS
should assign an ephemeral port
automatically.

Only applicable for Client mode.

Note: If a specific (non-zero) local
port is chosen, after a socket is
closed it is up to the underlying OS to
release the port before the next socket
creation, otherwise the bind attempt will
fail.

Enter the time (in milliseconds) to wait
between disconnecting from the TCP
server and connecting to it again. Only
applicable for Client mode.

The maximum number of client
connections to accept. After this number
has been reached, subsequent socket
requests will be rejected. Only applicable
for Server mode.

The amount of time, in milliseconds, to
wait without receiving a message before
closing a connection.

Useful when you expect to receive large
messages. Generally, the default value is
fine.

•  Select Yes to keep the socket
open until the sending system

Page 374 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

L

Data Type

Text

M

Encoding

Default

N

Respond on New
Connection

No

closes it. When Yes is selected,
the message will only be
processed if data is received
and either the receive timeout is
reached, the remote system
closes the socket, or an end-of-
message byte sequence has
been detected from the
Transmission Mode.

•  Select No to close the socket
after a received message has
finished processing.

•  Select Binary if the inbound

messages are raw byte streams;
the payload will be Base64
encoded.

•  Select Text if the inbound
messages are textual; the
payload will be encoded with the
specified character set
encoding.

Select the character set encoding to use
when decoding bytes from the TCP
stream, or select Default to use the default
character set encoding for the JVM Mirth®
Connect by NextGen Healthcare is running
on.

•  Select No to send responses

only using the same connection
the inbound message was
received on.

•  Select Yes to always send

responses on a new connection
(during normal processing as
well as recovery).

•  Select Message Recovery to
only send responses on a new
connection during message


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 375 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

recovery. Connections will be
bound locally on the same
interface chosen in the Listener
Settings (on page 233) with an
ephemeral port.

O

P

Response Address

The domain name or IP address to send
message responses to.

Response Port

The port to send message responses to.

Source Map Variables

Key

Description

localAddress

The IP address that the TCP socket is locally bound to.

localPort

The port that that TCP socket is locally bound to.

remoteAddress

The IP address of the remote system.

remotePort

The TCP port of the remote system.

TCP Transmission Modes

The transmission mode determines how to receive message data from the incoming byte stream, and
how to send responses out. The following transmission modes are supported on the TCP Listener (on
page 372) / TCP Sender (on page 416) (and Serial Listener / Sender commercial extension):

•  Basic TCP Transmission Mode (on page 377)

•  MLLP Transmission Mode (on page 378)

An additional transmission mode is made available via a commercial extension:

•  ASTM E1381 Transmission Mode (on page 585)

Page 376 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Basic TCP Transmission Mode

This transmission mode allows you to specify basic TCP frame data (beginning and ending byte
sequences). This allows the source connector to know when a message has been fully received.
Destination connectors also use these sequences when sending data outbound.

Item

A

Start of Message Bytes

Name

Default Value

Description

B

End of Message Bytes

The bytes before the
beginning of the actual
message. Only valid
hexadecimal characters
(0-9, A-F) are allowed.

The bytes after the end of
the actual message. Only
valid hexadecimal
characters (0-9, A-F) are
allowed. If this is not
specified, the only way a
connector knows whether a
message has been
received is if the socket
timeout is reached or if the
remote side closes the
socket.

Byte Abbreviations

The s section to the right of the transmission mode dialog allows you to easily drag-and-drop bytes into
the components to the left, without having to remember the actual hexadecimal values. These also show
up as labels next to the byte fields:


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 377 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

MLLP Transmission Mode

This transmission mode implements the Minimal Lower Layer Protocol (MLLP) specified by HL7, and
is often used when transmitting HL7 v2.x (on page 329) messages. There are two versions of MLLP, v1
and v2. The first version is similar to the Basic TCP Transmission Mode (on page 377) in that it only
specifies sequences for the start/end message bytes.

The second version builds on the first with "reliable delivery assurance", by having each system send a
protocol-level ACK or NAK immediately after every received frame.

Page 378 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

By default only MLLPv1 is enabled, as it is the most common use-case.

Item

A

Name

Default Value

Description

Start of Message Bytes

0x0B <VT>)

The MLLP Start Block bytes
before the beginning of the
actual message. Only valid


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 379 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

B

C

D

E

F

End of Message Bytes

0x1C0D (<FS><CR>)

Use MLLPv2

No

Commit ACK Bytes

0x06 (<ACK>)

Commit NACK Bytes

0x15 (<NAK>)

Max Retry Count

2

hexadecimal characters
(0-9, A-F) are allowed.

The MLLP End Data/Block
bytes after the end of the
actual message. Only valid
hexadecimal characters
(0-9, A-F) are allowed.

Select Yes to use the
MLLPv2 bi-directional
transport layer, which
includes reliable delivery
assurance as per the HL7
specifications.

The MLLPv2 Affirmative
Commit Acknowledgement
bytes to expect after
successfully sending a
message, and to send after
successfully receiving a
message. Only valid
hexadecimal characters
(0-9, A-F) are allowed.

The MLLPv2 Negative
Commit Acknowledgement
bytes to expect after
unsuccessfully sending a
message, and to send after
unsuccessfully receiving a
message. Only valid
hexadecimal characters
(0-9, A-F) are allowed.

The maximum number of
time to retry unsuccessful
dispatches before giving up
and logging an error.

Byte Abbreviations

This section is the same as in the Basic TCP Transmission Mode (on page 377).

Page 380 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Web Service Listener

This source connector publishes a SOAP endpoint via JAX-WS. By default it uses a simple service with
one operation that takes in a message string and sends back a response string. The SOAP XML envelope
is automatically handled, so that the actual data the channel receives is the content within the operation
argument node. You also have the option to provide your own custom service, with custom operations.

Supported property groups:

•  Listener Settings (on page 233)

•  Source Settings (on page 238)

•  HTTP Authentication Settings (on page 239)

Item

A

Name

Default Value

Web Service

Default service

Description

If Custom is
selected, provide
the fully-qualified
class name of the
Endpoint service
you wish to
publish.

B

C

Service Class
Name

com.mirth.connect.connectors.ws.DefaultAcceptMessage  The fully-qualified
class name of the
Endpoint service
to publish.

Service Name

Mirth

The name of the
service, used to
populate the URL
context path.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 381 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

D

Name

Binding

Default Value

Default

E

WSDL URL

<Auto-generated>

F

Method

acceptMessage

Description

The selected
binding version
defines the
structure of the
generated
envelope.
Selecting Default
will publish this
endpoint with the
value from the
annotation in the
Web Service
class. If no
annotation is
found, the SOAP
1.1 binding will be
used.

Displays the auto-
generated WSDL
URL for the web
service. This is
not an actual
configurable
setting, but is
instead displayed
for copy/paste
convenience.
Note that the host
in the URL will be
the same as the
host you used to
connect to the
Administrator. The
actual host that
connecting clients
use may be
different dur to
differing
networking
environments.

If the default
service is used,
this will show the
method
"acceptMessage",
which simply
takes in a String
and returns a
String. For custom
web services, this
will display

Page 382 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

"<Custom Web
Service
Methods>".


Destination Connectors

Every channel has at least one destination connector that sends data out to an external system.
Destination connectors are configured on the Destinations Tab (on page 246) within the Edit Channel
View (on page 203). In addition to the standard Connector Components, destination connectors include:

•  An Enabled flag that determines whether the destination is currently being used. A channel

must have at least one destination enabled at any given time.

•  A Wait for previous destination setting that determines what chain a destination connector

belongs to. (For additional information, see Destination Chains (on page 68). )

•  A Response Transformer. This is like a regular transformer, except it has its own response
inbound data type and response outbound data type, and does the job of modifying the
response that an external system returned to a destination connector. It also allows you to decide
when to queue / force-error a message. See Response Transformers (on page 76) for additional
information.

Destination Connector Properties

This section refers to the actual connector-specific settings. The section is labeled according to the
connector type, e.g. "HTTP Sender Settings", "JavaScript Writer Settings". Here is a list of destination
connectors supported by Mirth® Connect:

•  Destination Connectors (on page 384)
•  Channel Writer (on page 384)

•  DICOM Sender (on page 386)

•  Database Writer (on page 390)

•  Document Writer (on page 393)

•  File Writer (on page 395)

•  HTTP Sender (on page 400)

•  JMS Sender (on page 407)

•  JavaScript Writer (on page 410)

•  SMTP Sender (on page 411)

•  TCP Sender (on page 416)

•  Web Service Sender (on page 422)

Additional destination connectors are made available as commercial extensions (on page 582):

•  Serial Connector (on page 597)

•  NextGen Results CDR Connector (on page 600)

Page 252 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

•  FHIR Connector (on page 590)

•  Interoperability Connector Suite (on page 592)

Destination Mappings

This section is to the right of the destination connector properties, and allows you to easily drag-and-
drop common variables/templates into fields of the connector properties. Standard variables/templates
are available across all destination connectors. Custom mapper variables come from the Mapper Steps
(on page 281) you have added in the current destination or in any previous destinations. For additional
information on Velocity replacement, see Velocity Variable Replacement (on page 469).

Standard Variables/Templates

Name

Channel ID

Description

The unique ID of the current channel.

Channel Name

The name of the current channel.

Message ID

The unique ID of the current message.

Raw Data

The raw content of the destination connector message (equal to the encoded content


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 253 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Name

Description

of the source connector message).

Transformed Data

The serialized internal representation of the post-transformer message data.

Encoded Data

The state of the message data as it exists in the transformer.

Message Source

Message Type

Message Version

Message Hash

Depends on the inbound data type (on page 323) for the connector. For HL7 v2.x (on
page 329) messages this will usually be the Sending Facility value in MSH.4.1.

Depends on the inbound data type (on page 323) for the connector. For HL7 v2.x (on
page 329) messages this will usually be the Type and Trigger values in MSH.9.1 and
MSH.9.2.

Depends on the inbound data type (on page 323) for the connector. For HL7 v2.x (on
page 329) messages this will usually be the Version value in MSH.12.1.

The SHA-256 hash of the encoded data of the destination connector message. If no
encoded data exists, the raw content is hashed instead.

Date

The current date and time, printed using a standard format.

Formatted Date

The current date and time, printed using a custom user-specified format.

Timestamp

Unique ID

The current epoch time represented in milliseconds.

An auto-generated universally unique identifier string.

Original File Name

Only applicable when the source connector is a File Reader. The name of the file
currently being processed.

Count

A number that automatically starts at 1 when the channel is deployed, and increments
for each message, or for each time ${COUNT} is used.

XML Entity Encoder

Automatically encodes any special XML characters (like "&;") into entities (like
"&amp;"). Useful when your message template is XML and you want to inject a custom
variable into the inner text of a node.

XML Pretty Printer

Automatically indents and normalizes whitespace for the given XML string.

Escape JSON String

Automatically escapes any special JSON characters (like "{") with backslashes (like
"\{"). Useful when your message template is JSON and you want to inject a custom
variable into a string property.

JSON Pretty Printer

Automatically indents and normalizes whitespace for the given JSON string.

CDATA Tag

Inserts a CDATA tag, inside which you can place custom data without having to
encode entities.

DICOM Message Raw Data

A special replacement token telling the destination connector to merge the destination
connector message with any DICOM pixel data attachments and dispatch the fully

Page 254 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Name

Description

merged bytes to the outbound endpoint. Typically used by the DICOM Sender (on
page 386) destination.

Destinations

Select the destinations to exclude or not exclude, depending on the behavior above. If
the destination is renamed later, these selections will still be correct since the metadata
ID is used.

Field

The message field or expression to test.

Condition

Determines how to test the Field set above. The following conditions are supported:

•  Exists: Returns true if the length of the field is greater than 0.

•  Not Exist: Returns true if the length of the field is 0.

•  Equals: If the Values table is empty, returns true if the field is equal to an

empty string. If the Values table is not empty, returns true if the field matches
any of the values in the Values table below.

•  Not Equal: If the Values table is empty, returns true if the field is not equal to

an empty string. If the Values table is not empty, returns true if the field
matches none of the values in the Values table below.

•  Contains: Returns true if the field contains any of the values in the Values

table below.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 285 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item Name

Description

•  Not Contain: Returns true if the field contains none of the values in the Values

table below.

Values

A table of expressions that may be used in conjunction with the Condition to test the
given field and decide whether or not to filter the selected destinations.

Iterator Transformer Step

This is a special type of step that allows you to perform extract / transform operations while iterating
through an array or list of XML nodes. For additional information, see Working With Iterators (on page
288) .

Item Name

Description

Iterate On

The element to iterate on. This may be a list of E4X XML nodes, or a Java / JavaScript
array.

Index Variable

The index variable to use for each iteration.

Drag-and-Drop Substitutions  When drag-and-dropping values into the children underneath this Iterator, the index

variable (e.g. "[i]") will be injected after any of these prefixes. For example if your index
variable is i and you have msg['PID'] in the Drag-and-Drop Substitutions table, when
you drag the value msg['PID']['PID.3']['PID.3.1'].toString() from the Message Trees
Tab (on page 265) into a child step, it will automatically be replaced with
msg['PID'][i]['PID.3']['PID.3.1'].toString().

Response Transformers

The response transformer is a special type of transformer only editable for destination connectors on the
Destinations Tab (on page 246). It works the same as a regular transformer, except that the data being
transformed is not the message flowing through the channel, but instead the response payload that the
destination connector received from the external system (if applicable). For additional information about
transformers, see About Transformers (on page 76).

Page 286 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

A destination response is comprised not only of the response data, but also the status (e.g. SENT,
ERROR), status message, and error message. Response transformers can be used to modify these latter
pieces as well. For example if a message gets set to ERROR by the destination connector, in the
response transformer you can choose to override that and set the status to SENT instead based on some
custom logic.

Note: Response transformers will only execute if there is an actual response payload to transform.
For example if you are using an HTTP Sender (on page 400) destination and it fails to connect to

the remote server, then obviously there is no response payload. The one exception to this rule is if the
response inbound data type is set to Raw. In that case, because the Raw data type doesn't need to
perform any serialization, the response transformer will always execute even if there is no response
payload.

Modifying the Response

Modifying the actual response data is done by using the normal features and steps available to a
transformer. The internal representation of the response data is msg, while the internal representation of
the outbound template (if set) is tmp. When the response transformer finishes processing, it will use the
value of tmp (or msg if no outbound template is set) to create the Processed Response content.

There are three other pieces of the response that you can modify in the response transformer:

•  responseStatus: This is the status that will be used to update the message after the response

message finishes. You may set the status to SENT, QUEUED, or ERROR. If the status is set to
QUEUED and queuing is not enabled for the destination connector, it will automatically be
changed to ERROR.

•  responseStatusMessage: This is a brief one-line message that displays alongside the status in

the message browser. It is typically used to give a reason for the status.

•  responseErrorMessage: This is the full error message associated with a response. Typically

this is used to display large stacktrace messages.

In addition to the above variables, you have access to response, which is an ImmutableResponse object.
For additional information, see the User API (on page 459).

Common Scenarios

Re-queue a message if the HL7 ACK has an AE code

if (msg['MSA']['MSA.1']['MSA.1.1'].toString() == 'AE') {


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 287 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

    responseStatus = QUEUED;
    responseStatusMessage = 'Application Error NACK received.';
    responseErrorMessage = msg['MSA']['MSA.3']['MSA.3.1'].toString();

Force a queuing message to error if the number of send attempts exceeds some
threshold

if (responseStatus == QUEUED && connectorMessage.getSendAttempts() >=
5) {
   responseStatus = ERROR;
   responseStatusMessage = 'Maximum send attempts exceeded.';
   }

Route the response data to a downstream channel

if (responseStatus == SENT) {
router.routeMessageByChannelId('channel ID here', response.getMes
sage());
}

Working With Iterators

An Iterator is a special type of step that allows you to loop (iterate) through an array or list of XML
nodes. For each array element or XML node (each "iteration"), you can execute multiple filter rules or
transformer steps (the "children").

For example, let us say you are mapping inbound HL7 v2.x messages to an outbound HL7 v2.x
template, and you want to copy OBR.16 .1 (ordering provider) component to a the PV1.9.1 (consulting
doctor) component in the outbound template.

Page 288 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Typically you would do this with a Message Builder Transformer Step (on page 282):

This works so far when there is only one OBR segment and one OBR.16 field. But what if you want to
handle multiple segments or repeating fields? This is where Iterators come into play.

Creating Iterators From Drag-and-Drop

As explained in the Message Trees Tab (on page 265) section, new rules and steps can be created by
right-clicking the node in the message tree, by dragging a node into the filter/transformer table, or by
dragging a node from the inbound tree and dropping it onto a node in the outbound tree. In all of these
cases, you will be presented with a prompt asking whether you want to create an Iterator automatically.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 289 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

If Yes is chosen, the Iterator Wizard dialog is shown.

Page 290 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

In this wizard dialog you can select your iteration target (what to iterate on). If you have dragged from
inbound to the outbound template, you will also have the option to select which part of the outbound
(tmp) expression corresponds to the inbound (msg) target being iterated on. These options correspond
directly to the drag-and-drop substitutions shown at the bottom of the dialog. You are essentially telling
the wizard "where the i goes"

Once you select OK, the Iterator and subsequent rule/step will be created.

So far it is only iterating at one level though. In the example for this section, we wanted to iterate not
only through each OBR segment, but also through each OBR.16 field repetition. So we want to take the
current selected step and assign it to an additional, nested Iterator.

The Assign To Iterator Task

This task takes the currently selected rule/step, and either moves it to an existing Iterator, or creates an
entirely new Iterator and puts the rule/step within it. When selecting the task, a dialog appears.

The Choose Existing Iterator option allows you to take the currently selected rule/step and move it


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 291 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

underneath a specific pre-existing Iterator. If your rule/step already belongs to an Iterator, by default this
option is selected and the current parent is selected in the drop-down menu.

The Create New Iterator option is the same dialog shown before, where you have the option of
choosing what to iterate on.

Note that in this case, the variable j was automatically chosen, because the wizard detected that the
currently selected step is part of a parent Iterator that is already using the i index variable. We can then
choose to iterate through each OBR.16 field instead of on each OBR segment:.

Page 292 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

After selecting OK, the step will now be placed inside a new nested Iterator. You can see from the
following example that OBR.16.1 is being mapped into PV1.9.1, but now it is being done for each OBR
segment, and in turn for each OBR.16 field repetition.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 293 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Remove From Iterators

If you decide later that a rule or step should not be part of an Iterator, it is easy to undo those changes.
Select the rule or step, then select the Remove From Iterator task. The rule/step is moved one level
higher in the tree. So if the rule/step is currently nested under multiple Iterators, select the task multiple
times until it is at the depth you want.

View Generated Script

For all rule / step types, the properties panel shown in the bottom half of the screen is split into two tabs:
Rule/Step, and Generated Script. Selecting the Generated Script tab shows the equivalent JavaScript
that will execute when your channel is deployed and a message is sent through:

The script pane is not editable, but you can still select code and expand/collapse code folds. As shown in
the screenshot above, selecting an Iterator rule/step shows you the script for the Iterator and all of its

Page 294 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

children at once.

Note: External Script rules/steps are an exception and will not show the actual script that resides
on the server:

Filter Tasks

The following context-specific tasks are available throughout the Edit Filter View (on page 257):

Task Icon

Task Name

Description

Add New Rule

Delete Rule

Adds a new filter rule to the table. If an Iterator rule or
any rule that is a child of an Iterator is currently
selected, the new rule is placed at the end of the
children of the most immediate parent Iterator.
Otherwise, the new rule is placed at the very end of the
list at the bottom of the table.

Removes the currently selected rule from the table. If
an Iterator rule is deleted, all of its children are also
deleted.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 295 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Task Icon

Task Name

Description

Assign To Iterator

Remove From Iterator

Import Filter

Adds the selected rule to a new or existing Iterator. For
additional information, see Working With Iterators (on
page 288).

Removes the selected rule from its current Iterator. For
additional information, see Working With Iterators (on
page 288).

Imports a filter from an XML file. You can choose to
completely replace the current filter or simply append
the rules to the current table. For additional
information, see Import Filter (on page 296)

Export Filter

Exports the current filter (all rules) to an XML file.

Validate Filter

Validate Rule

Move Rule Up

Move Rule Down

Validates the entire filter and all rules. This includes
property validation and script syntax validation.

Validates the currently selected rule. This includes
property validation and script syntax validation.

Moves the currently selected rule one slot higher in the
table. If the rule is inside of an Iterator and is currently
the first rule in the Iterator's children, this task will move
the rule up and out of the Iterator, similar to the
Remove From Iterator task. For additional
information, see Working With Iterators (on page 288).

Moves the currently selected rule one slot lower in the
table. If the rule is inside of an Iterator and is currently
the last rule in the Iterator's children, this task will move
the rule down and out of the Iterator, similar to the
Remove From Iterator task. For additional
information, see Working With Iterators (on page 288).

Import Filter

When this task is selected, you will be presented with a prompt.

Page 296 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

If Yes is chosen, the rules from the filter is added at the end of the current list at the bottom of the table.
If No is chosen, all rules currently in the table are deleted and replaced with the rules from the imported
filter.

Transformer Tasks

The following context-specific tasks are available throughout the Edit Transformer View (on page 257):

Task Icon

Task Name

Add New Step

Delete Step

Assign To Iterator

Remove From Iterator

Import Transformer

Description

Adds a new transformer step to the table. If an
Iterator step or any step that is a child of an Iterator
is currently selected, the new step is placed at the
end of the children of the most immediate parent
Iterator. Otherwise, the new step will be placed at
the very end of the list at the bottom of the table.

Removes the currently selected step from the table.
If an Iterator step is deleted, all of its children are
also deleted.

Adds the selected step to a new or existing Iterator.
For additional information, see Working With
Iterators (on page 288).

Removes the selected step from its current Iterator.
For additional information: Working With Iterators
(on page 288)

Imports a transformer from an XML file. You can
choose to completely replace the current
transformer, or simply append the steps to the
current table. For additional information, see Import


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 297 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Task Icon

Task Name

Description

Transformer (on page 298)

Export Transformer

Exports the current transformer (inbound/outbound
data types and all steps) to an XML file.

Validate Transformer

Validate Step

Move Step Up

Move Step Down

Validates the entire transformer and all steps. This
includes property validation and script syntax
validation.

Validates the currently selected step. This includes
property validation and script syntax validation.

Moves the currently selected step one slot higher in
the table. If the step is inside of an Iterator and is
currently the first step in the Iterator's children, this
task will move the step up and out of the Iterator,
similar to the Remove From Iterator task. For
additional information, see Working With Iterators
(on page 288).

Moves the currently selected step one slot lower in
the table. If the step is inside of an Iterator and is
currently the last step in the Iterator's children, this
task will move the step down and out of the Iterator,
similar to the Remove From Iterator task. For
additional information, see Working With Iterators



Channel Scripts

Channel Scripts can be configured on the Scripts Tab (on page 255) within the Edit Channel View (on
page 203). There are four special scripts associated with a channel:

Page 62 of 619

Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

•  Deploy Script: This runs once right before a channel is deployed.

•  Preprocessor Script: This runs once for every message, after the source connector sends a
message to the channel and after the Attachment Handler Properties (on page 221) has
optionally extracted data, but before the message has reached the source filter/transformer. The
job of the preprocessor is to modify the incoming message.

•  Post Processor Script: This runs once for every message, after the source connector and all

destinations have completed (excluding asynchronous processes like the destination queue), but
before the source connector sends a response back to the originating system. The postprocessor
script has access to responses from all executed destination, and can return custom response that
the source connector can use.

•  Undeploy Script: This runs once right before a channel is undeployed.

Scripts Tab

The Scripts tab is where channel-level scripts are configured. Select a script type from the drop-down
list and edit the script in the text area.Select a script type from the drop-down and edit the script in the
text area below. If a number appears next to "Scripts" in the tab, that number represents how many
scripts have been edited from the default values. There is also a Reference List to the right for easy drag-
and-drop of common helper methods / code templates.

The following channel-level script types can be edited:

•  Deploy Script: This script executes once when the channel is deployed. You have access to the
global / global channel / configuration maps here. Typically this script is used to perform a one-
time operation for the given channel, such as loading a properties file from disk, or instantiating
a database connection.

•  Undeploy Script: This script executes once when the channel is undeployed. You have access
to the global / global channel / configuration maps here. Typically this script is used to cleanup
any data created from the deploy script, such as closing a database connection.

•  Preprocessor Script; This script executes once for every message, after the attachment handler

has run but before the message reaches the source filter/transformer. You have access to
"message" a string variable containing the incoming data. Whatever you return from the
preprocessor script is stored as the Processed Raw content and used to feed into the source filter/
transformer.

•  Post Processor Script: This script executes once for every message, after all destinations have


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 255 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

completed processing (not including queued messages which are processed asynchronously).
You have access to "message" which is an ImmutableMessage object containing information
about the state of all connector messages. This script may be used as a general tool to perform a
custom cleanup script. It can also be used to return a custom response that may be sent back to
the originating system.

Destination Connectors

This section refers to the actual connector-specific settings for destinations. The section is labeled
according to the connector type, e.g. "HTTP Sender", "JavaScript Writer". For additional information on
connectors in general, see About Channels and Connectors (on page 60).

Here is a list of destination connectors supported by Mirth® Connect

•  Channel Writer (on page 384)

•  DICOM Sender (on page 386)

•  Database Writer (on page 390)

•  Document Writer (on page 393)

•  File Writer (on page 395)

•  HTTP Sender (on page 400)

•  JMS Sender (on page 407)

•  JavaScript Writer (on page 410)

•  SMTP Sender (on page 411)

•  TCP Sender (on page 416)

•  Web Service Sender (on page 422)

Additional destination connectors are made available as commercial extensions (on page 582):

•  Serial Connector (on page 597)

•  NextGen Results CDR Connector (on page 600)

Channel Writer

The Channel Writer is a connector that simply dispatches messages to other internal channels. This can

Page 384 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

be useful if you split your message workflow into multiple channels, where one sends to another. If no
target channel is specified, the connector acts as a "sink" where no message dispatching is done. Note
that a channel does not need to use a Channel Reader (on page 340) source for a Channel Writer to be
able to send messages to it. The connector also has options to inject source map variables into the
downstream message of the target channel.

Supported property groups:

•  Destination Settings (on page 249)

Item

A

Name

Channel Id

B

Message Metadata

Default Value

Description

<None>

The unique ID of the target
channel to send messages
to. This may be a hard-
coded ID, or may be a
Velocity Variable
Replacement (on page
469). Use the drop-down
menu to the right to quickly
select a particular channel.
If <None> is selected, the
destination will act as a
"sink" where messages are
not dispatched anywhere.

The map variables entered
here will be included in the
source map of the
destination channel's
message.

Note: This table
expects only
variable names, not Velocity
replacement tokens. For
example, do not use
${varName}, instead use


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 385 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

C

Template

${message.encodedData}

varName. The value will be
extracted from all available
Variable Maps (on page
449).

The actual payload to send
to the target channel. By
default the encoded data of
this destination will be used.
Velocity Variable
Replacement (on page 469)
is supported here.

Source Map Variables

When this connector sends a message to another channel, the following source map (on page 449)
variables will be available on the downstream message:

Key

sourceChannelId

sourceMessageId

sourceChannelIds

sourceMessageIds

Description

The unique ID of the channel that dispatched a message
to the current channel.

The ID of the message from which the current message
dispatch originated.

If there are more than two channels in a Channel Writer ->
Reader chain, this will be a List containing the IDs of all
channels in the chain.

If there are more than two channels in a Channel Writer ->
Reader chain, this will be a List containing the IDs of all
messages in the chain.

DICOM Sender

This destination connector works in conjunction with the DICOM Attachment Handler Properties (on
page 224) and the DICOM Data Type (on page 327) to allow Mirth® Connect to send DICOM data.
This connector supports the C-STORE operation as a Service Class User (SCU). Additional options are
available with the SSL Manager (on page 598) extension.

Page 386 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Supported property groups:

•  Destination Settings (on page 249)

Item

Name

Default Value  Description

A

B

C

D

E

Remote Host  127.0.0.1

The remote IP to send to.

Local Host

The local address that the client socket will be bound to.

Remote Port

104

The remote port to send to.

Local Port

Remote
Application

The local port that the client socket will be bound to.

The Application Entity title to sent to.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 387 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value  Description

Entity

Local
Application
Entity

The Application Entity title to identify the local client with.

Max Async
operations

0

Maximum number of outstanding operations performed
asynchronously. Enter 0 for unlimited.

Priority

Medium

Priority of the C-STORE operation.

Request
Storage
Commitment

No

User Name

Pass Code

No

Request
Positive User
Identity
Response

Pack PDV

No

10

16

5

16

DIMSE-RSP
interval period
(s)

P-DATA-TF
PDUs max
length sent
(KB)

A-RELEASE-
RP timeout
(s)

P-DATA-TF
PDUs max
length
received (KB)

DIMSE-RSP
timeout (s)

60

Request storage committment of (successfully) sent objects
afterwards.

Enable User Identity Negotiation with specified username and
optional passcode.

Optional passcode for User Identity Negotiation, only effective
when a username is set.

Request positive User Identity Negotiation response, only
effective when a username is set.

Send only one PDV in one P-Data-TF PDU, pack command
and data PDF in one P-DATA-TF PDU by default.

Period to check for outstanding DIMSE-RSP, 10 seconds by
default.

Maximal length in KB of sent P-DATA-TF PDUs, 16 KB by
default.

Timeout for receiving A-RELEASE-RP.

Maximal length in KB of received P-DATA-TF PDUs.

Timeout in milliseconds for receiving DIMSE-RSP.

Send Socket

0

Send socket buffer size in KB

F

G

H

I

J

K

L

M

N

O

P

Q

R

S

Page 388 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value  Description

T

U

V

W

X

Y

Z

AA

Buffer Size
(KB)

Shutdown
delay (ms)

1000

Delay in milliseconds for closing the listening socket.

Receive
Socket Buffer
Size (KB)

0

Receive socket buffer size in KB.

Socket Close
Delay After A-
ABORT (ms)

Transcoder
Buffer Size
(KB)

Timeout A-
ASSOCIATE-
AC (ms)

50

1

Delay in ms for Socket close after sending A-ABORT.

Transcoder buffer size in KB.

5000

Timeout in milliseconds for receiving A-ASSOCIATE-AC.

TCP
Connection
Timeout (ms)

0

Timeout in milliseconds for TCP connection. Enter 0 for no
timeout.

TCP Delay

Yes

Set TCP_NODELAY socket option to false.

Default
Presentation
Syntax

No

Offer Default Transfer Syntax in separate Presentation
Context. By default offered with Explicit VR Little Endian TS in
one PC.

BB

TLS

No TLS

Determines whether to receive data over an implicit SSL/TLS
socket. The following options are available:

•  3DES: TLS will be used, with the

SSL_RSA_WITH_3DES_EDE_CBC_SHA cipher
suite.

•  AES: TLS will be used, with the following cipher

suites:

•  TLS_RSA_WITH_AES_128_CBC_SHA

•  SSL_RSA_WITH_3DES_EDE_CBC_SHA

•  Without: TLS will be used without symmetric

encryption, with the cipher suite
SSL_RSA_WITH_NULL_SHA. DICOM messages
will be received unencrypted.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 389 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value  Description

CC

DD

EE

FF

GG

HH

II

JJ

•  No TLS: No TLS will be used. DICOM messages

will be received over a regular unencrypted socket.

Enable client authentication for TLS. Only applicable if the TLS
option is not set to No TLS.

Enable acceptance of the SSLv2Hello protocol in the TLS
handshake.

File path or URL of P12 or JKS keystore to use for the local
server certificate keypair.

Password for the configured Keystore.

File path or URL of JKS truststore, used to trust remote client
certificates.

Password for the configured Truststore.

Password for accessing the key in the Keystore.

Client
Authentication
TLS

Yes

Accept ssl v2
TLS
handshake

Yes

Keystore

Keystore
Password

Trust Store

Trust Store
Password

Key
Password

Template

${DICOMMESSAGE}  The actual payload to send to the target channel. By default

the encoded data of this destination (with all DICOM pixel data
attachments reattached) will be used. Velocity Variable
Replacement (on page 469) is supported here.

Database Writer

This destination connector connects to an external database and performs an INSERT/UPDATE
statement (or any other statement, like calling a stored procedure). This can be done using a SQL
statement, or by using JavaScript mode to execute the statement manually. The database connection will
automatically be kept open across multiple dispatches.

Supported property groups:

•  Destination Settings (on page 249)

Page 390 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Default Value

Description

Specifies the type of
database driver to use to
connect to the database.
The following values are
supported by default:

•  Sun JDBC-ODBC

Bridge

•  MySQL

•  Oracle

•  PostgreSQL

•  SQL Server /

Sybase

•  SQLite

Additional drivers can be
added by editing the
dbdrivers.xml File (on page
480).

The JDBC URL to connect
to the database with. This is
not used when "Use
JavaScript" is checked.
However, it is used when
the Generate Connection /
Insert feature is used to
generate code. Use the
Insert URL Template
button to populate the URL
field with a starting
template.

The username to connect to
the database with. This is
not used when "Use

Item

A

Name

Driver

B

URL

C

Username


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 391 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

D

E

F

Password

Use JavaScript

No

Generate

JavaScript" is checked.
However, it is used when
the Generate Connection /
Insert feature is used to
generate code.

The password to connect to
the database with. This is
not used when "Use
JavaScript" is checked.
However, it is used when
the Generate Connection /
Insert feature is used to
generate code.

If enabled, the JavaScript
scripts will be used to run
the insert/update statement.
If disabled, SQL code
(either standard or
database-specific) may be
used, and the connection
will be handled
automatically.

•  Connection: This
button is enabled
when Use
JavaScript is
enabled. When
selected, it inserts
boilerplate
Connection
construction code
into the
JavaScript pane
at the current
caret position.

•

Insert: Opens a
window to assist
in building an
insert statement
to insert records
into the database
specified in the
URL above.

G

SQL / Template

The actual SQL or
JavaScript code to execute.

Page 392 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Document Writer

This destination connector takes an HTML template and converts it into either a PDF or RTF document.
Custom embedded stylesheets are supported. That document can then be written out to a file and/or
stored as a message attachment. The page size can be specified, and for PDFs you can also encrypt the
document with a password.

Supported property groups:

•  Destination Settings (on page 249)

Item

A

Name

Output

Default Value

Description

File

Choose how to output the
document.

•  File: Write the

contents to a file.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 393 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

•  Attachment:

Write the contents
to an attachment.
The destination's
response
message will
contain the
attachment Id and
can be used in
subsequent
connectors to
include the
attachment.

•  Both: Write the

contents to both a
file and an
attachment.

The directory (folder) where
the generated file should be
written. Use the Test Write
button to confirm that files
can be written to the folder.

The file name to give to the
generated file.

The type of document to be
created for each message.

If the document type is
PDF, generated documents
can optionally be encrypted.

If encryption is enabled,
enter the password that
must be used to view the
document after encryption.

The width and height of the
document pages. The units
for each are determined by
the drop-down menu to the
right. When rendering
PDFs, a minimum of 26mm
is enforced. Use the far-
right drop-down menu to
quickly select a page size
among common US and UK

B

C

D

E

F

G

Directory

File Name

Document Type

PDF

Encrypted

No

Password

Page Size

8.5" x 11" (Letter)

Page 394 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

H

HTML Template

File Writer

formats.

This template is expected to
be an HTML document,
determining how to layout
the PDF/RTF document.
Custom embedded
stylesheets are supported.

This destination connector writes files out to the local filesystem, or to a remote directory. Several
protocols are supported, including regular local file mode, FTP, SFTP, SMB, WebDAV, and Amazon S3.
Files may be converted from Base64 and written out in raw binary format, or converted to bytes using a
specific character set encoding. Additional options (like FTPS) are available with the SSL Manager (on
page 598) extension.

Supported property groups:

•  Destination Settings (on page 249)


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 395 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

A

Name

Method

B

Advanced Options

Default Value

Description

file

The basic method used to
access the directory to write
files to. Options include File
(local filesystem or NFS /
mapped share), FTP,
SFTP, SMB, WebDAV, or
Amazon S3. Once all
necessary connection/
directory information has
been filled in before, use
the Test Write button to
attempt to actually connect
and test the ability to write
to the directory.

If the file method supports
advanced options, this
button will be enabled. Any
advanced options set will
be summarized in the
Advanced Options label
below this. For additional

Page 396 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

C

D

E

F

G

H

I

Directory

URL

File Name

Anonymous

Yes

Username

anonymous

Password

Timeout (ms)

10000

information, see File
Reader (on page 351).

Only applicable to the File
method. The directory
(folder) to write the files to.

Applicable to all methods
except File. The domain
name or IP address of the
host (computer) to connect
to. If this setting is enabled,
the second text field
specifies the directory
(folder) to write to. When
using the Amazon S3
method, the first text field
will be the bucket name,
and the second text field
can be used for a directory
prefix.

The name to write the file
out as.

Only applicable to the FTP /
WebDAV / Amazon S3
methods. If enabled,
connects to the remote
server anonymously instead
of using a username and
password.

Applicable to all methods
except File. The username
used to connect to the
remote server with. When
using the Amazon S3
mode, this will be your AWS
Access Key ID.

Applicable to all methods
except File. The password
used to connect to the
remote server with. When
using the Amazon S3
mode, this will be your AWS
Secret Access Key.

Applicable to the FTP /
SFTP / SMB / Amazon S3
methods. The socket


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 397 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

J

K

L

M

N

O

Keep Connection Open

Yes

Max Idle Time (ms)

0

Secure Mode

Yes

Passive Mode

Yes

Validate Connection

Yes

File Exists

Append

timeout (in ms) to use when
connecting to the remote
server.

Select Yes to keep the
connection to the file
system open after writing to
it.

The maximum amount of
time that a connection can
be idle/unused before it
gets closed. A timeout
value of zero is interpreted
as an infinite timeout,
meaning that connections
will only be closed when the
connector is stopped.

Only applicable to the
WebDAV method. If
enabled, HTTPS will be
used instead of HTTP.

Only applicable to the FTP
method. If enabled, the
server decides what port
the client should connect to
for the data channel.
Passive mode sometimes
allows a connection through
a firewall that normal mode
does not, because the client
is initiating the data
connection rather than the
server.

Only applicable to the FTP
method. If enabled, the
connection will be tested for
validity before each
operation.

Determines what to do
when the file to be written
already exists on the
filesystem / remote server.

•  Append:

Messages will be
appended to the

Page 398 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

end of the current
file.

•  Overwrite: The

current file will be
completely
overwritten with
the current
message content.

•  Error: The
connector
message will
error out and the
current file will
remain
unchanged.

If enabled, the file contents
will first be written to a temp
file and then renamed to the
specified file name. If
disabled, the file contents
will be written directly to the
destination file. This option
is not available if the file is
being appended to (option
M).

Select Binary if the
Template contains Base64
data; the contents will be
decoded into raw bytes.
Select Text if the Template
contains textual data; the
contents will be decoded to
bytes using the specified
character set encoding.

If Text is chosen for the File
Type, select the character
set encoding (ASCII,
UTF-8, etc.) to be used in
writing out the contents of
each file.

The actual payload to send
to the target channel. By
default the encoded data of
this destination will be used.

P

Create Temp File

No

Q

R

S

File Type

Text

Encoding

Default

Template

${message.encodedData}


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 399 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

Velocity Variable
Replacement (on page 469)
is supported here.

Connector Map Variables

When the Amazon S3 mode is used, these variables will be available in the connector map.

Key

s3ETag

Description

The hex encoded 128-bit MD5 hash of the file contents as
computed by Amazon S3.

s3ExpirationTime

The expiration time for this object, only present if not null.

s3ExpirationTimeRuleId

s3SSEAlgorithm

s3SSECustomerAlgorithm

s3SSECustomerKeyMd5

s3VersionId

s3Metadata

The BucketLifecycleConfiguration rule ID for this object's
expiration, only present if not null.

The server-side encryption algorithm, only present if the
object is encrypted using AWS-managed keys.

The server-side encryption algorithm, only present if the
object is encrypted using customer-provided keys.

The base64-encoded MD5 digest of the encryption key for
server-side encryption, only present if the object is
encrypted using customer-provided keys.

The version ID of the newly uploaded object, only present
if not null.

A MessageHeaders object representing the map of
metadata/headers for the S3 object.

HTTP Sender

This destination connector sends an HTTP request to an external web server. The method, parameters,
and headers can all be fully customized. Both Basic and Digest authentication (preemptive or reactive)
are supported. Sending requests through a proxy server is also supported, even when using HTTPS. The
HTTP payload can be written out as raw bytes, or converted using a specified charset. Responses can be
automatically converted to XML, allowing multipart payloads to be parsed in a consistent and easy-to-
use way. Additional options are available with the SSL Manager (on page 598) extension.

Page 400 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Supported property groups:

•  Destination Settings (on page 249)

Item

A

B

Name

URL

Default Value

Description

Use Proxy Server

No

The URL of the HTTP
server to send each
message to.

If enabled, requests are
forwarded to the proxy


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 401 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

C

D

E

F

G

H

Proxy Address

Proxy Port

Method

POST

Multipart

No

Send Timeout (ms)

30000

Response Content

Plain Body

I

Parse Multipart

Yes

server specified in the
address/port fields below.

The domain name or IP
address of the proxy server
to connect to.

The port on which to
connect to the proxy server.

The HTTP operation (POST
/ GET / PUT / DELETE /
PATCH) to send for each
message.

If enabled, the content is
first written to a local temp
file and is then wrapped in a
single file part inside a
multipart/form-data payload.

Sets the socket timeout
(SO_TIMEOUT) in
milliseconds to be used
executing the method. A
timeout value of zero is
interpreted as an infinite
timeout.

•  Plain Body: The
response body
will be stored as a
raw string.

•  XML Body: The
response body
will be stored as
serialized XML.

Applies only to Response
Content XML Body.

•  Select Yes to
automatically
parse multipart
responses into
separate XML
nodes.

Page 402 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

J

Include Metadata

No

K

Binary MIME Types

application/.*
(?<!json|xml)$|
image/.*|video/.*| audio/.*

•  Select No to

always keep the
response body as
a single XML
node.

Applies only to Response
Content XML Body.

•  Select Yes to

include response
metadata
(response code,
headers) in the
XML content.

Note: Regardless of
this setting, the

same metadata is always
available in the connector
map.

When a response comes in
with a Content-Type header
that matches one of these
entries, the content is
encoded into a Base64
string.

•

•

If Regular
Expression is
unchecked,
specify multiple
entries with
commas.

If Regular
Expression is
checked, enter a
valid regular
expression to
match MIME
types against.

L

Authentication

No

If enabled, a Basic or
Digest Authorization


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 403 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

M

Authentication Type

Basic

N

O

P

Username

Password

Query Parameters

Use Table

header is automatically
added to the request.

Select either Basic or
Digest authentication type.

•

•

If the Preemptive
option is checked,
the Authorization
header is sent to
the server with
the initial request.

If the Preemptive
option is
unchecked, the
Authorization
header is only
sent when the
server requests it.

Note: When Digest
authentication is

selected, an Authorization
header containing the
realm/nonce/algorithm/qop
values must be included in
the Headers table.

The username to use to
authenticate to the HTTP
server.

The password to use to
authenticate to the HTTP
server.

•  Use Table: The
table below will
be used to
populate query
parameters.

•  Use Map: The
Java map
specified by the
following variable
will be used to

Page 404 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

Q

Headers

Use Table

populate query
parameters. The
map must have
String keys and
either String or
List<String>
values.

When using the Use Table
option above, entries in this
table will automatically be
added to the request URI
as query parameters.
Multiple parameters with
the same name are
supported.

Tip: If the
"application/x-www-
form-urlencoded" Content
Type is used, this table will
be used to populate the
form data and will be sent in
the entity payload rather
than in the request URI.

•  Use Table: The
table below will
be used to
populate headers.

•  Use Map: The
Java map
specified by the
following variable
will be used to
populate headers.
The map must
have String keys
and either String
or List<String>
values.

When using the Use Table
option above, entries in this
table will be added to the
request as HTTP headers.
Multiple headers with the


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 405 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

R

S

Content Type

text/plain

Data Type

Text

T

Charset Encoding

UTF-8

U

Content

same name are supported

The HTTP message body
MIME type to use. If
application/x-www-form-
urlencoded is used, the
query parameters specified
above will be automatically
encoded into the request
body.

•  Select Binary if
the outbound
message is a
Base64 string.
This is decoded
before it is sent
out.

•  Select Text if the

outbound
message is
textual. This is
encoded with the
specified
character set
encoding.

Select the character set
encoding to send with the
Content-Type header, or
Default to use the default
character set encoding for
the JVM Mirth® Connect is
running on.

•

If None is
selected, no
charset will be
included in the
Content-Type
header unless
explicitly specified
in the Content
Type field.

The actual payload to send.
Only applicable for entity-

Page 406 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

enclosing requests (POST /
PUT / PATCH). Velocity
Variable Replacement (on
page 469) is supported
here.

Connector Map Variables

After a request finishes, the connector map will automatically have the following entries available.
These can be used from within the Response Transformer (on page 286).

Key

responseStatusLine

responseHeaders

JMS Sender

Value

This is the full status line of the HTTP response, e.g.
"HTTP/1.1 200 OK". It includes the HTTP version, the
response code, and the response code reason.

A MessageHeaders object containing all headers received
in the response. Look in the User API for additional
information.

This destination connector connects to an external JMS provider and writes messages to a queue or
topic. It supports both JNDI and specifying a specific connection factory. Once this connector dispatches
a message, the connection to the JMS provider will be kept open and cached until the connector is
stopped or an error occurs. The properties view also includes a mechanism to save configuration
templates for common provider types, so that creating a new JMS Sender is as quick and easy as
possible.

Note: Depending on the JMS connection provider you are using, you may need to include some
external libraries (JARs) as a Library Resource (on page 186) and include them using the Set

Dependencies (on page 214) dialog on the Channel Summary tab.

For example, to use connection templates included for ActiveMQ and JBoss, you should download the
libraries from their official site in order to use them with the JMS connectors.

Supported property groups:


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 407 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

•  Destination Settings (on page 249)

Item

A

Name

Use JNDI

Default Value

Description

No

Select Yes to use JNDI to
look up a connection factory
to connect to the queue or
topic. Select No to specify a
connection factory class
without using JNDI.

If using JNDI, enter the
URL of the JNDI provider
here.

If using JNDI, enter the
fully-qualified Java class
name of the JNDI Initial
Context Factory class here.

If using JNDI, enter the
JNDI name of the
connection factory here.

Provider URL

Initial Context Factory

Connection Factory Name

B

C

D

E

Connection Factory Class

If using the generic JMS

Page 408 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

F

Connection Properties

G

H

I

J

K

L

M

Username

Password

Destination Type

Queue

Destination Name

Client ID

Template

${message.encodedData}

Connection Templates

provider and not using
JNDI, enter the fully-
qualified Java class name
of the JMS connection
factory here.

This table allows you to
enter custom connection
factory settings. The
Property column is the key,
while the Value column is
the actual value for the
setting. The specific
properties used here will
vary depending on what
connection factory class /
provider you are using.

The username for
accessing the queue or
topic.

The password for accessing
the queue or topic.

Specify whether the
destination is a queue or
topic.

The name of the queue or
topic.

The JMS client ID to use
when connecting to the
JMS broker.

The actual payload to send
to the JMS broker. By
default the encoded data of
this destination will be used.
Velocity Variable
Replacement (on page 469)
is supported here.

This section allows you to
save the current state of
your JMS Sender properties
into a template, which may
then be restored later if you
make changes, or may also
be applied to other JMS
Sender connectors. More


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 409 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

information here: JMS
Listener (on page 366)

JavaScript Writer

This destination connector executes a custom user-defined JavaScript script. This can be used in a wide
variety of ways, such as calling out to external Java libraries or invoking a local OS shell script. You can
return custom values that determine what response data to store, and what status to put the destination
connector message into. Or simply use the script as a generic job that doesn't necessarily produce
responses. For example, you can use tools like ChannelUtil to programmatically start/stop/deploy
channels from within the script.

Supported property groups:

•  Destination Settings (on page 249)

Page 410 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

JavaScript Writer Return Values

When you return from your script, you can choose to set a custom Response that will be stored for the
destination. The following return values are accepted:

•  String: Any string returned will be stored as the Response content for the destination, with a

status of SENT.

•  Status: An instance of the Status enum (for additional information, see The User API (Javadoc)
(on page 459)) will cause no response content to be stored, but the connector message status
will be updated to SENT, QUEUED, or ERROR depending on the status returned. Note that
the status can only be set to QUEUED if queuing is enabled in the Destination Settings (on
page 249).

•  Response: A Response object contains a status, status message, error message, and the actual
response content. If this object is returned, all of these things will be stored in the Response
content, and the connector message status will be updated accordingly. For additional
information, see The User API (Javadoc) (on page 459).

•  Empty String / null / undefined: Returning any of these (including a "return;" statement or no
return statement at all) will cause no response data to be stored, and the message status will be
updated to SENT.

•  Any Object: Any other object returned will be converted to a String via the toString() method,
and that String representation will be stored as the Response data. The message status will also
be updated to SENT.

SMTP Sender

This destination connector sends an e-mail to a specified address (or list of addresses), through a given
SMTP relay/host. Both implicit and explicit (STARTTLS) encryption modes are supported. The body
can be either text or HTML. Custom headers and attachments can be added to the request as well.

Supported property groups:

•  Destination Settings (on page 249)


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 411 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

A

Name

SMTP Host

Default Value

Description

The domain name or IP
address of the SMTP server
to use to send the e-mail
messages. Note that
sending e-mail to an SMTP
server that is not expecting
it may result in the IP of the
machine running Mirth®
Connect being added to the
server's "denylist".After
filling out the necessary
information below, use the
Send Test Email button to
send a sample e-mail to the
To address, to verify that
everything is working as
intended.

The port number of the
SMTP server to send the e-

B

SMTP Port

25

Page 412 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

C

Override Local Binding

No

D

E

F

G

Local Address

0.0.0.0

Local Port

0

Send Timeout (ms)

5000

Encryption

None

mail messages to.
Generally, the default port
of 25 is used.

Select Yes to override the
local address and port that
the client socket will be
bound to. Select No to use
the default values of
0.0.0.0:0. A local port of
zero (0) indicates that the
OS should assign an
ephemeral port
automatically. Note that if a
specific (non-zero) local
port is chosen, then after a
socket is closed it is up to
the underlying OS to
release the port before the
next socket creation,
otherwise the bind attempt
will fail.

The local address that the
client socket will be bound
to, if Override Local Binding
is enabled.

The local port that the client
socket will be bound to, if
Override Local Binding is
enabled.Note that if a
specific (non-zero) local
port is chosen, then after a
socket is closed it is up to
the underlying OS to
release the port before the
next socket creation,
otherwise the bind attempt
will fail.

The number of milliseconds
for the SMTP socket
connection timeout.

Determines what type of
encryption to use for the
connection.

•  None: No

encryption will be


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 413 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

used. Messages
will be sent over
the connection in
plain text.

•  STARTTLS: The
connection will
begin as
unencrypted, and
then the SMTP
client will
manually upgrade
the connection
through a
STARTTLS
command.

•  SSL: The

connection will be
encrypted from
the very
beginning. Use
this when the
server expects a
TLS handshake
after a client
connects.

Determines whether to use
authentication when
connecting to the SMTP
server.

The username to
authenticate with.

The password to
authenticate with.

The e-mail address to send
to. Multiple addresses can
be specified with commas.

The e-mail address to send
the message from.

The subject line of the e-
mail.

Use Authentication

No

Username

Password

To

From

Subject

Charset Encoding

Default

The character set encoding

H

I

J

K

L

M

N

Page 414 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

O

P

Q

HTML Body

No

Template

Headers

R

Attachments

to use when converting the
body, or Default to use the
default character set
encoding of the JVM Mirth®
Connect by NextGen
Healthcare is running on.

Determines the MIME type
of the message, either text/
plain or text/html. If HTML is
used, richer message
formatting may be used.

The actual body of the e-
mail. Velocity Variable
Replacement (on page 469)
is supported here.

•  Use Table: The
table below will
be used to
populate headers.

•  Use Map: The
Java map
specified by the
following variable
will be used to
populate headers.
The map must
have String keys
and either String
or List<String>
values.

When using the Use Table
option above, entries in this
table will be included as
SMTP headers in the e-mail
dispatch.

•  Use Table: The
table below will
be used to
populate
attachments.

•  Use List: The


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 415 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

Java list specified
by the following
variable will be
used to populate
attachments. The
List must have
AttachmentEntry
values. The
AttachmentEntry
class is available
in JavaScript
scripts and is
documented in
The User API
(Javadoc) (on
page 459).

When using the Use Table
option above, entries in this
table will be added as
attachments with the e-mail.
The following columns are
configurable:

•  Name: The name
of the attachment.

•  Content: The

Base64-encoded
content of the
attachment. You
can also use a
message
attachment
replacement
token here.

•  MIME Type: The
MIME type of the
attachment (e.g.
"image/png").

TCP Sender

This destination connector opens a new TCP client connection and sends messages over it. You can
decide whether to keep a connection open, and if so for how long. Configurable transmission modes (on

Page 416 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

page 372) allow you to decide how to send outbound messages and receive responses.

Supported property groups:

•  Destination Settings (on page 249)

Item

A

Name

Default Value

Description

Transmission Mode

MLLP

The transmission mode
determines how to send
message data out on the
socket byte stream, and
how to receive responses.
For additional information,
see TCP Listener (on page
372)

B

Sample Frame

<VT> <Message Data>

This is dependent on the


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 417 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

<FS><CR>

Transmission Mode and
displays an example of how
an outgoing message frame
is expected to look.

C

Mode

Client

•  Client: The TCP

Sender will
initiate new
connections
outbound to a
remote server,
and then send
messages
outbound on that
connection.

•  Server: The TCP
Sender will open
a server socket
and listen for
incoming
connections. If
queuing is
enabled,
messages will
continue to queue
until at least one
connection has
been established,
at which point the
messages will be
sent to all
currently
connected client
connections.

The domain name or IP
address on which to
connect. Press the Test
Connection button to verify
whether the server is able
to open a TCP connection
as the specified IP/port.
Only applicable when
Client mode is used above.

The port on which to
connect. Only applicable
when Client mode is used
above.

D

E

Remote Address

127.0.0.1

Remote Port

6660

Page 418 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Name

Default Value

Description

Item

F

G

H

Override Local Binding

No

Local Address

0.0.0.0

Local Port

0

Select Yes to override the
local address and port that
the client socket will be
bound to. Select No to use
the default values of
0.0.0.0:0. A local port of
zero (0) indicates that the
OS should assign an
ephemeral port
automatically. Note that if a
specific (non-zero) local
port is chosen, then after a
socket is closed it is up to
the underlying OS to
release the port before the
next socket creation,
otherwise the bind attempt
will fail. Only applicable
when Client mode is used
above.

The local address that the
client socket will be bound
to, if Override Local Binding
is enabled. If Server mode
is used above, this will
determine what interfaces
to listen on for incoming
connections.

The local port that the client
socket will be bound to, if
Override Local Binding is
enabled. Note that if a
specific (non-zero) local
port is chosen, then after a
socket is closed it is up to
the underlying OS to
release the port before the
next socket creation,
otherwise the bind attempt
will fail. If Server mode is
used above, this will
determine what port to
listen on for incoming
connections.

The maximum number of
client connections to accept
at once. After this number
has been reached,
subsequent socket requests
will result in a rejection.

I

Max Connections

10


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 419 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

J

Keep Connection Open

No

K

Check Remote Host

No

L

Send Timeout (ms)

5000

Only applicable when the
Server mode is used above

Select Yes to keep the
connection to the host open
across multiple messages.
Select No to immediately
close the connection to the
host after sending each
message.

Note: When Keep
Connection Open is
enabled, the Send Timeout
is used to determine how
long to keep the connection
open when there are no
messages to send. By
default the Send Timeout is
set to 5 seconds, so even
when Keep Connection
Open is enabled, the
connection may still be
closed if there is a period of
downtime where no
messages are being
dispatched.

Only applicable when
Client mode is used above.

Select Yes to check if the
remote host has closed the
connection before each
message. Select No to
assume the remote host
has not closed the
connection. Checking the
remote host will decrease
throughput but will prevent
the message from erroring
if the remote side closed
the connection and queuing
is disabled. Only applicable
when Client mode is used
above.

The number of milliseconds
to keep the connection to
the host open, if Keep
Connection Open is
enabled. If zero, the

Page 420 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

Buffer Size (bytes)

65536

Response Timeout (ms)

5000

Queue on Response
Timeout

Yes

Data Type

Text

Encoding

Default

connection will be kept
open indefinitely. Only
applicable when Client
mode is used above.

The size, in bytes, of the
buffer to hold messages
waiting to be sent.
Generally, the default value
is fine.

The number of milliseconds
the connector should wait
whenever attempting to
create a new connection or
attempting to read from the
remote socket. If Ignore
Response is checked, the
connector will not wait for a
response at all after
sending a message.

If enabled, the message is
queued when a timeout
occurs while waiting for a
response. Otherwise, the
message is set to ERROR
when a timeout occurs. This
setting has no effect unless
queuing is enabled for the
connector.

Select Binary if the
outbound message is a
Base64 string (will be
decoded before it is sent
out). Select Text if the
outbound message is
textual (will be encoded
with the specified character
set encoding).

Select the character set
encoding used by the
message sender, or select
Default to use the default
character set encoding for
the JVM Mirth® Connect by
NextGen Healthcareis
running on.

Template

${message.encodedData}

The actual payload to send

M

N

O

P

Q

R


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 421 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

to the remote server. By
default the encoded data of
this destination will be used.
Velocity Variable
Replacement (on page 469)
is supported here.

Web Service Sender

This destination connector connects to a SOAP endpoint via JAX-WS and invokes a defined operation.
When configuring this connector you can automatically fetch the WSDL from the remote server, and all
the services / endpoints / operations will be filled out and modifiable from drop-down menus. Automatic
generation of a sample SOAP envelope is supported too. Custom headers and MTOM attachments can
be added to each request as well.

Supported property groups:

•  Destination Settings (on page 249)

Page 422 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

Item

A

Name

WSDL URL

B

C

Service

Port / Endpoint

D

Location URI

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Default Value

Description

The URL to the WSDL
describing the web service
and available operations.
Select Get Operations
after entering the WSDL to
automatically fill out the
Service, Port, Location URI,
and available Operations.

The service name for the
WSDL defined above. This
field is filled in automatically
when the Get Operations
button is selected and does
not usually need to be
changed, unless multiple
services are defined in the
WSDL.

The port / endpoint name
for the service defined
above. This field is filled in
automatically when the Get
Operations button is
selected and does not
usually need to be
changed, unless multiple
endpoints are defined for
the currently selected
service in the WSDL.

The dispatch location for
the port / endpoint defined
above. This field is filled in
automatically when the Get
Operations button is
selected and does not
usually need to be
changed. If left blank, the
default URI defined in the
WSDL will be used.

Sets the connection and
socket timeout
(SO_TIMEOUT) in
milliseconds to be used
when invoking the web
service. A timeout value of
zero is interpreted as an
infinite timeout.

Socket Timeout (ms)

30000

E

F

Authentication

No

Turning on authentication


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 423 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

Username

Password

Invocation Type

Two-Way

Operation

uses a username and
password to get the WSDL,
if necessary, and uses the
username and password
binding provider properties
when calling the web
service.

The username used to get
the WSDL and call the web
service.

The password used to get
the WSDL and call the web
service.

Determines how to invoke
the operation selected
below.

•  Two-Way: Invoke
the operation
using the
standard two-way
invocation
function. This will
wait for some
response or
acknowledgement
to be returned.

•  One-Way: Invoke
the operation
using the one-
way invocation
function. This will
not wait for any
response, and
should only be
used if the
operation is
defined as a one-
way operation.

The web service operation
to be called. This is used to
generate the envelope
along with the Generate
Envelope button.

SOAP Action

The SOAPAction HTTP

G

H

I

J

K

Page 424 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

L

SOAP Envelope

M

Headers

request header field can be
used to indicate the intent
of the SOAP HTTP request.
This field is optional for
most web services, and
may be auto-populated
when you select an
operation.

The actual SOAP envelope
to send to the remote web
service. Use the Generate
Envelope button above to
generate a sample skeleton
XML document that you can
then fill out using Velocity
Variable Replacement (on
page 469). For more
complex schemas, this
button may produce an
incomplete SOAP
envelope. Please refer to
your vendors to ensure
you're sending the correct
SOAP envelopes in your
outgoing messages.

Note: This is an
XML SOAP
document, so any variables
used here may need to be
properly entity-encoded.
You can do this by dragging
the XML Entity Encoder
over from the Destination
Mappings list first, and then
dragging the map variable
inside the
"XmlUtil.encode()" function
call.

•  Use Table: The
table below will
be used to
populate headers.

•  Use Map: The
Java map
specified by the


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 425 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

N

O

Use MTOM

No

Attachments

following variable
will be used to
populate headers.
The map must
have String keys
and either String
or List<String>
values.

When using the User Table
option above, entries in this
table will be added to the
request as HTTP headers.
Multiple headers with the
same name are supported.

Enables MTOM on the
SOAP Binding. If enabled,
attachments can be added
to the table below and
referenced from within the
envelope.

•  Use Table: The
table below will
be used to
populate
attachments.

•  Use List: The

Java list specified
by the following
variable will be
used to populate
attachments. The
List must have
AttachmentEntry
values. The
AttachmentEntry
class is available
in JavaScript
scripts and is
documented in
The User API
(Javadoc) (on
page 459).

When using the Use Table
option above, entries in this
table will be added as

Page 426 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Default Value

Description

MTOM attachments along
with the request. The
following columns are
configurable:

•

ID: A unique ID
for the attachment
which can be
referenced from
within the SOAP
envelope.

•  Content: The

Base64-encoded
content of the
attachment. You
can also use a
message
attachment
replacement
token here.

•  MIME Type: The
MIME type of the
attachment (e.g.
"image/png").



Connector Components

•  General Connector Properties (on page 63)

•  Connector-Specific Properties (on page 63)

•  Filter (on page 65)

•  Transformer (on page 65)

General Connector Properties

Every connector has a name and a "metadata ID." For a source connector, the name is always "Source"
and the metadata ID is always zero (0). For a destination connector, the name is configurable and the
metadata ID is some value greater than zero. The first destination connector in your channel starts at
metadata ID one (1), the next one will be two, and so on. Even if you rename a destination connector,
the metadata ID will remain the same.

Connector-Specific Properties

Every connector has its own custom set of properties. The properties you configure for a TCP Listener
(on page 372) will be different from a Database Writer (on page 390) and so on. Here is a list of source
and destination connectors supported by Mirth® Connect:

•  Source Connectors (on page 340)


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute.

Page 63 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

•  Channel Reader (on page 340)

•  DICOM Listener (on page 341)

•  Database Reader (on page 346)

•  File Reader (on page 351)

•  HTTP Listener (on page 362)

•  JMS Listener (on page 366)

•  JavaScript Reader (on page 371)

•  TCP Listener (on page 372)

•  Web Service Listener (on page 381)

•  Destination Connectors (on page 384)
•  Channel Writer (on page 384)

•  DICOM Sender (on page 386)

•  Database Writer (on page 390)

•  Document Writer (on page 393)

•  File Writer (on page 395)

•  HTTP Sender (on page 400)

•  JMS Sender (on page 407)

•  JavaScript Writer (on page 410)

•  SMTP Sender (on page 411)

•  TCP Sender (on page 416)

•  Web Service Sender (on page 422)

Additional connectors are made available as commercial extensions (on page 582):

•  Email Reader (on page 588)

•  Serial Connector (on page 597)

•  NextGen Results CDR Connector (on page 600)

Page 64 of 619

Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

•  FHIR Connector (on page 590)

•  Interoperability Connector Suite (on page 592)

Filter

A filter is the piece of a connector that decides whether a message should proceed to the next step or not.
It is configured on the Edit Filter View (on page 257) within either the Source Tab (on page 232) or
Destinations Tab (on page 246) within the Edit Channel View (on page 203). See About Filters (on page
74) for additional information.

Transformer

A transformer is the piece of a connector that modifies a message, converts a message from one format
to another, and extracts pieces of the message for later use. It is configured on the Edit Transformer
View (on page 257) within either the Source Tab (on page 232) or Destinations Tab (on page 246)
within the Edit Channel View (on page 203). For additional information, see About Transformers (on
page 76).

All transformers have an inbound data type and an outbound data type used to determine how data is
parsed and converted. For additional information on data types, see About Data Types (on page 72)



About Message Data

In Mirth® Connect, a message refers to a single overall dispatch of data through the source connector
and destination connectors within a channel. Messages are further separated into connector messages
which are part of the message that flow through a single connector. For example if your channel has two
destinations, then a single message will have three connector messages associated with it as it is
processed through the channel: One source connector, and two destination connectors. These connector
messages correspond to the rows of data you see in the Metadata Table (on page 106) within the
Message Browser (on page 103).

Note that messages do not always correspond 1-to-1 with a file that you read in, or a particular stream of
data. As explained in the About Channels and Connectors (on page 60) section, the source connector
may contain a batch processor which takes a raw inbound stream of data and splits it into multiple
messages. So if you have a single file containing 100 HL7 v2.x messages, your channel could read that
in and process 1 message or 100 messages, depending on how the source connector is configured.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute.

Page 65 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Message Metadata

Metadata typically refers to important information about the message, but not the actual message
content. Message Metadata includes:

•  Message ID: Every message for any given channel has a unique integer ID, the Message ID.

The Message ID is used to organize data in the message browser and join connector messages
together.

•  Connector Metadata ID: Each connector has its own ID, referred to as the connector
metadata ID. The source connector always has a metadata ID of zero (0). Destination
connector metadata IDs start at one (1) and increment for each new destination you add to the
channel.

•  Status: Each connector message has a status, which tells the channel the current processing
state. For example, the status could be RECEIVED, which means the raw data has been
committed to the database, but the message is in the middle of being processed. It could be
QUEUED, which may mean it is sitting in a queue waiting to be processed, or one or more
attempts have been made to process the message, but it has not yet been successfully processed.

•  Timestamps: Timestamps are used to analyze and diagnose issues. Every connector message
stores a received date, which is the time at which its data was committed to the database. For
destination connectors, the send date and response date let you know the time a message was
dispatched outbound, and the time a response was received from the external system. The
differences between these timestamps can give insights into your channel performance.

•  Custom Metadata Columns: Custom metadata columns are configurable columns located on
the Summary Tab (on page 204) within the Edit Channel View (on page 203). They allow you
create your own columns that show up in the Metadata Table (on page 106) and are searchable
in the Message Browser (on page 103).

All of this information and more is visible in the Metadata Table (on page 106) within the Message
Browser (on page 103).

Page 66 of 619

Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Message Content

Message content is the actual data that gets processed. As a message flows through your channel,
different versions of the data are stored for each connector, depending on the modifications your channel
needs to make.

Source Connector

•  Raw - The state of the message as it enters the connector.

•  Processed Raw - The state of the message after passing through the preprocessor script.

•  Transformed - The serialized internal representation of the message, which exists only if a

connector has a filter or transformer configured.

•  Encoded - The state of the message as it exits the transformer (includes changes made to the

transformed data).

•  Response - The message sent back to the originating system (at the very end, after all

destinations finish).

Destination Connector

•  Raw - The state of the message as it enters the connector. For a destination connector, this is the

same as the source encoded data.

•  Transformed - The serialized internal representation of the message, which exists only if a

connector has a filter or transformer configured.

•  Encoded - The state of the message as it exits the transformer (includes changes made to the

transformed data).

•  Sent - The message/connector properties used by the destination connector to send messages to

the outbound system.

•  Response - The message received from the outbound system after the destination sends the

message.

•  Response Transformed - The serialized internal representation of the response, which exists

only if a destination connector has a response transformer configured.

•  Processed Response - The state of the response as it exits the response transformer (includes

changes made to the transformed data).


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute.

Page 67 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

These pieces of content are specific to each individual connector message. So a source connector will
have Raw / Transformed / Encoded data, and each destination connector will have its own Raw /
Transformed / Encoded data.

Note: When a message flows from the source connector to the destination connectors, the
Encoded Data from the source becomes the Raw Data for each destination. However, Raw Data is

not "daisy-chained" from destination to destination. If you have three destinations, the Raw Data for
each and every destination will be identical to the Encoded Data from the source connector message.

For additional information on the various content types, see Message Content Types (on page 111),
Variable Maps (on page 449), Error Content Types (on page 116).

Message Attachments

An attachment is a piece of data extracted from the raw incoming message and stored separately.
Attachments are not associated with connector messages, but instead with the overall message. The
extraction happens at the very beginning of the message lifecycle, even before the preprocessor script
runs. When a destination connector dispatches data outbound, any attachments associated with the
message will be automatically re-inserted into the outgoing data. In this way, attachment data is only
stored once, and multiple copies of it for each connector and for each content type (e.g. Raw /
Transformed / Encoded) will not be stored. Using attachments can greatly improve the memory footprint
of your channels.

More additional information on attachments and how they are extracted, see Attachment Handlers (on
page 219).

The Message Processing Lifecycle

As explained in the About Message Data (on page 65) section, each source and destination connector
has various versions of the message data as it flows through the channel. The raw inbound message
enters Mirth® Connect, is received by the source connector, is filtered and transformed, then encoded
and sent to the destination connector. From the source connector, the raw inbound message can be
passed through multiple destination connectors where it can again be influenced by filters and
transformers before it is processed, encoded, and sent on.

Page 68 of 619

Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Source Processing Steps

1.  A message or stream of data is received by the source connector.

2.  The batch processor decides whether to split the message into multiple messages. If so, the

below steps are repeated for each message returned by the batch processor.

3.  The attachment handler extracts and/or stores all attachment data.

4.  The post-attachment-handler content is stored as raw data.

5.  The content runs through the preprocessor script and is stored as processed raw data.

6.  The content is serialized (converted) to the internal representation of the inbound data type

(e.g. XML).

7.  The content runs through the filter, and the message is either accepted or filtered. If the message

gets filtered, flow stops here and jumps down to the Final Processing Steps (on page 70).

8.  The content runs through the transformer, where it may be modified.

9.  The post-transformer content is stored as the transformed data.

10.  The content is deserialized (converted) from the outbound data type's internal representation

(e.g. XML) into the actual outbound format (HL7, EDI, etc.).

11.  The content is stored as encoded data.

Note: If no filter or transformer are configured, there will not be any transformed or
encoded data. In that case, the content used from here on will only be the raw data (or

processed raw data if a preprocessor modified it).

12.  The resulting content is passed on to the first destination connector of each destination chain (on

page 71).


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute.

Page 69 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Destination Processing Steps

Note: These steps are repeated for each destination connector that a message flows through.

1.  The encoded content from the source connector is used by each destination connector as its raw

data.

2.  The content is serialized (converted) to the internal representation of the inbound data type

(e.g. XML).

3.  The content runs through the filter, and the message is either accepted or filtered. If the message
    gets filtered, flow stops for the current destination here. If there are additional destinations in the
    current chain, these steps are repeated for the next destination. Otherwise, assuming all other
    destination chains have finished, flow jumps down to the Final Processing Steps (on page 70).

4.  The content runs through the transformer, where it may be modified.

5.  The post-transformer content is stored as the transformed data.

6.  The content is deserialized (converted) from the outbound data type's internal representation

(e.g. XML) into the actual outbound format (HL7, EDI, etc.).

7.  The content is stored as encoded data.

Note: If no filter or transformer is configured, there will not be any transformed or
encoded data. In that case, the content used from here on will only be the raw data.

8.  The destination connector builds a message from all available previous content, stores it as sent

data, and sends it to the outbound system.

9.  A response is received by the destination connector and stored as the response data.

10.  If a response transformer is configured, the response content is serialized (converted) to the

internal representation of the response inbound data type (e.g. XML).

11.  The response content runs through the response transformer, where it may be modified.

12.  The post-response-transformer content is stored as the response transformed data.

13.  The response content is deserialized (converted) from the response outbound data type's
     internal representation (e.g. XML) into the actual outbound format (HL7, EDI, etc.).

14.  The response content is stored as processed response data.

Final Processing Steps

Note: This point is reached when all destination chains have finished processing the message. If

Page 70 of 619

Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

the message was filtered or errored out on the source connector, then flow will immediately jump down
to this step.

1.  The post processor script is executed. It can return a response that the source connector may use.

2.  The source connector decides what response to send back to the originating system, if any. This
    may be an auto-generated value, the response payload from a destination connector dispatch, a
    response returned from the post processor script, or a completely custom value residing in the
    response map.

3.  The selected response is stored as the source connector's response data and is sent back to the

originating system, if needed.

Destination Chains

A channel's destinations are grouped into one or more destination chains. Each destination chain
processes simultaneously with respect to each other, however in any particular chain, a message will
flow through each destination in order. It looks something like this:


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute.

Page 71 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

In the above example, there are 5 total destinations. However Destination 3 does not wait on
Destination 2, so it marks the beginning of a new chain. If each destination takes 1 second to process,
then the overall time it takes the message to process through the channel will not be 5 seconds, but
rather 3 seconds. When flowing through the destination connectors, a message will take only as long as
the longest destination chain.


About Data Types

A data type tells a filter / transformer how to parse a certain format. The inbound data type serializes
the incoming raw data into its internal representation. Then the filter / transformer executes, possibly
modifying this internal representation, or even completely overwriting it with a different internal
representation. Finally, the outbound data type takes this transformed data and deserializes it into the
proper outbound format (the encoded data) .

For example, the HL7 v2.x data type (on page 329) serializes ER7 data from this:

into this:

Page 72 of 619

Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

In this case, the internal representation is XML, because, for HL7 v2.x, the message object you use or
manipulate in a filter / transformer is an E4X XML object. However not all data types use XML for their
internal representation. The JSON data type uses JSON as you might expect, and the object you use in a
filter / transformer is a regular JavaScript Object. The Raw data type does no serialization or
deserialization, so its internal representation is identical to the inbound message, and the object used in
the filter / transformer is a Java String.

For additional information, see Data Types (on page 323). Mirth® Connect supports the following data
types:

•  Delimited Text Data Type (on page 324)

•  DICOM Data Type (on page 327)

•  EDI / X12 Data Type (on page 328)

•  HL7 v2.x Data Type (on page 329)

•  HL7 v3.x Data Type (on page 332)


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute.

Page 73 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

•  JSON Data Type (on page 333)

•  NCPDP Data Type (on page 334)

•  Raw Data Type (on page 335)

•  XML Data Type (on page 336)

•  Batch Processing (on page 337)

•  JavaScript Batch Script (on page 338)

The following additional data types are made available as commercial extensions:

•  ASTM E1394 Data Type (on page 586)

•  FHIR Connector (on page 590)

About Filters

The filter is the piece of a connector that decides whether a message should proceed to the next step or
not. It is configured on the Edit Filter View (on page 257) within either the Source Tab (on page 232) or
Destinations Tab (on page 246) within the Edit Channel View (on page 203).

A filter returns true or false. When the filter returns true, the message is said to have been accepted.
When the filter returns false, the message is said to have been filtered.

•  If the source connector filters out a message, it will not flow through the source transformer, and

will not be processed by any of the destination connectors.

•  If a destination connector filters out a message, it will not flow through the destination

transformer, and will not be dispatched outbound by that destination connector. However other
destinations may still process the message.

A filter is comprised of multiple rules. Each rule is joined together with an operator, which can be
AND or OR. For example a filter may look like this:

•  Accept the message if: Rule 1 returns true OR Rule 2 returns true AND Rule 3 returns true

The standard order-of-operations means that AND takes logical precedence over OR, like this:

Page 74 of 619

Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5


Filter Rule Properties

The following Filter Rules are supported by Mirth® Connect

•  Rule Builder Filter Rule (on page 278)

•  JavaScript Filter Rule (on page 279)

•  External Script Filter Rule (on page 279)

•  Iterator Filter Rule (on page 280)

Rule Builder Filter Rule

This rule allows you to build simple accept logic for a specific message field or expression.

Item Name

Description

Behavior

This is always set to Accept, meaning that if the logical expression below evaluates to
true, the message will be accepted.

Field

The message field or expression to test.

Condition

Determines how to test the Field set above. The following conditions are supported:

Page 278 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item Name

Description

•  Exists: Returns true if the length of the field is greater than 0.

•  Not Exist: Returns true if the length of the field is 0.

•  Equals: If the Values table is empty, returns true if the field is equal to an

empty string. If the Values table is not empty, returns true if the field matches
any of the values in the Values table below.

•  Not Equal: If the Values table is empty, returns true if the field is not equal to

an empty string. If the Values table is not empty, returns true if the field
matches none of the values in the Values table below.

•  Contains: Returns true if the field contains any of the values in the Values

table below.

•  Not Contain: Returns true if the field contains none of the values in the

Values table below.

Values

A table of expressions that may be used in conjunction with the Condition to test the
given field and decide whether or not to filter the message.

JavaScript Filter Rule

This rule allows you to write a completely custom script to decide whether to filter the message or not.
For more information about using JavaScript, see Using JavaScript in Mirth Connect (on page 435).

External Script Filter Rule

This rule functions the same way as the JavaScript Filter Rule (on page 279), except that the script is


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 279 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

read from an external file when the channel is deployed. If the given path is not absolute, it will be
relative to the Mirth® Connect installation directory.

Iterator Filter Rule

This is a special type of rule that allows you to decide whether to filter a message or not by iterating
through an array or list of XML nodes. The child rules underneath the Iterator determine the accept/filter
behavior of the overall rule. For additional information, see Working With Iterators (on page 288).

Item Name

Description

Iterate On

The element to iterate on. This may be a list of E4X XML nodes, or a Java /
JavaScript array.

Index Variable

The index variable to use for each iteration.

Accept Message If

Determines how to logically combine each iteration into the overall accept / filter
behavior.

•  At Least One: If the logical combination of the child rules returns true for at
least one of the iterations, the overall Iterator behavior will be to accept the
message.

•  All: If the logical combination of the child rules returns true for all of the
iterations, the overall Iterator behavior will be to accept the message.


The "msg" Object

In order to decide whether a message needs to be filtered or not, you will typically need to test pieces of
the incoming message. As mentioned in the About Data Types (on page 72) section, when the message
enters a filter / transformer, it gets serialized into an internal representation. This is the variable msg,
which may be an E4X XML Object, a JavaScript Object, or a Java String, depending on the data type
implementation.

Filter Rule Types

Mirth® Connect supports the following filter rule types:

•  Rule Builder Filter Rule (on page 278)


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute.

Page 75 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

•  JavaScript Filter Rule (on page 279)

•  External Script Filter Rule (on page 279)

•  Iterator Filter Rule (on page 280)

About Transformers

A transformer is the piece of a connector that modifies a message, converts a message from one format
to another, and extracts pieces of the message for later use. It is configured on the Edit Transformer
View (on page 257) within either the Source Tab (on page 232) or Destinations Tab (on page 246)
within the Edit Channel View (on page 203).

A transformer has an inbound data type, and an outbound data type. These may be the same (e.g.
HL7 v2.x to HL7 v2.x), or they could be different (e.g. HL7 v2.x to JSON). For additional information
on data types, see About Data Types (on page 72).

A transformer is also comprised of multiple steps. Each transformer step modifies the message, extracts
a piece of the message, or performs some other general task.

The "msg" Object

This is the same as in a filter. For more information, see About Filters (on page 74).

The "tmp" Object

This is similar to msg, except that it is the internal representation of the outbound template (on page
260) configured in your transformer settings (on page 257). It will only be available in the transformer
     when you have an outbound template configured.

The tmp variable is used when you want to convert a message from one format to a completely different
format (e.g. HL7 v2.x to JSON). Or, it can be used to selectively include pieces of the incoming message
and map them into the outbound message.

Response Transformers

The Response Transformer is a special type of transformer specific to destination connectors. It works
the same as a regular transformer, except that the data being transformed is not the message flowing

Page 76 of 619

Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

through the channel, but instead the response payload that the destination connector received from the
external system (if applicable). For additional information, see Response Transformers (on page 286)

A destination response is comprised not only of the response data, but also the status (e.g. SENT,
ERROR), status message, and error message. Response transformers can be used to modify these latter
pieces as well. For example if a message gets set to ERROR by the destination connector, in the
response transformer you can choose to override that and set the status to SENT instead based on some
custom logic.

Note: Response transformers will only execute if there is an actual response payload to transform.
For example if you are using an HTTP Sender (on page 400) destination and it fails to connect to

the remote server, then obviously there is no response payload. The one exception to this rule is if the
response inbound data type is set to Raw (on page 335). In that case, because the Raw data type does not
need to perform any serialization, the response transformer will always execute even if there is no
response payload.

Transformer Step Types

Mirth® Connect supports the following transformer step types:

•  Mapper Transformer Step (on page 281)

•  Message Builder Transformer Step (on page 282)

•  JavaScript Transformer Step (on page 283)

•  External Script Transformer Step (on page 283)

•  XSLT Transformer Step (on page 283)

•  Destination Set Filter Transformer Step (on page 284)

•  Iterator Transformer Step (on page 286)

Transformer Step Properties

The following Transformer Steps are supported by Mirth® Connect:

•  Mapper Transformer Step (on page 281)

•  Message Builder Transformer Step (on page 282)

•  JavaScript Transformer Step (on page 283)

•  External Script Transformer Step (on page 283)

•  XSLT Transformer Step (on page 283)

•  Destination Set Filter Transformer Step (on page 284)

•  Iterator Transformer Step (on page 286)

Mapper Transformer Step

This step extracts data from a field in the message (or an expression) and places it into one of the
available Variable Maps (on page 449). Depending on the scope of the map, this variable will be
available in subsequent steps, in the destination connector properties, or even in subsequent connectors.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 281 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item Name

Description

Variable

Mapping

The variable name / key to use when inserting into the map. The Add to drop-down
menu to the right determines which map to place the variable in. For additional
information, see Variable Maps (on page 449).

The value to place into the map. This may be a field from the message, or any
JavaScript expression.

Default Value

If the Mapping is not found or evaluates to an empty string, this value / expression
will be used instead.

String Replacement

This table allows you to perform replacements on the value before it gets inserted
into the map.

•  Regular Expression: A Java-style regular expression to test against the

value. This will implicitly set the global regex flag.

•  Replace With: The value to replace any matched regions with.

Message Builder Transformer Step

This step extracts data from a field in the message (or an expression) and maps it into a specific field in
the inbound or outbound message. This can be used to simply modify a field in the inbound message,
copy a field from one place to another, or map data from the inbound message to the outbound message.

Item Name

Description

Message Segment

The field/location in the inbound or outbound message to place the value into.

Mapping

Default Value

The value to place into the given message segment. This may be a field from the
message, or any JavaScript expression.

If the Mapping is not found or evaluates to an empty string, this value / expression is
used instead.

String Replacement

This table allows you to perform replacements on the value before it gets inserted.

Page 282 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item Name

Description

•  Regular Expression: A Java-style regular expression to test against the

value. This will implicitly set the global regex flag.

•  Replace With: The value to replace any matched regions with.

JavaScript Transformer Step

This step allows you to write a completely custom script to extract / transform data, or to perform almost
any intermediate action you need to. For additional information about using JavaScript, see Using
JavaScript in Mirth Connect (on page 435).

External Script Transformer Step

This step functions the same way as the JavaScript Transformer Step (on page 283), except that the
script is read from an external file when the channel is deployed. If the given path is not absolute, it will
be relative to the Mirth® Connect installation directory.

XSLT Transformer Step

This step allows you to apply an XSLT (eXtensible Stylesheet Language Transformations) stylesheet to
a given XML document. This may be msg/tmp (the internal XML representation of your message data),
or some other variable containing an XML string. The result of the transformation will be stored in the


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 283 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

channel map.

Item Name

Description

Source XML String

The XML string to transform.

Result

The key to use when storing the result into the channel map.

Transformer Factory

Select default to use the Java platform default TransformerFactory implementation
class. Select custom to provide a custom TransformerFactory implementation
class.

XSLT Template

The XSLT stylesheet to use to transform the source XML string.

Destination Set Filter Transformer Step

Destination Set Filtering is a powerful feature of the source transformer that allows you to decide in
advance which destinations to exclude from message processing. Using each individual destination's
filter to control where a message goes is still a valid workflow, but when you have many destinations all
with mutually exclusive filters, the performance of the channel can be affected because message data
will be stored to the database for each destination connector. Also filtered connector messages can
clutter up the message browser (on page 103), making it harder to find what you are looking for. The
advantage to using Destination Set Filtering in this case is that filtered destinations will not have any
message data stored, and will not show up in the message browser. This can greatly increase message
throughput.

Destination Set Filtering can be done manually with JavaScript (look at DestinationSet in the User API
(on page 459)) . However this step allows easier access to the feature without having to write any
JavaScript.

Page 284 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item Name

Description

Behavior

Identifies which destinations will be removed (not processed) from the destination set.
Available options are:

•  the following: The identified destination(s) are filtered if the conditions

indicated are true.

•  all except the following: All destinations except those selected are filtered if
the conditions indicated are true. This includes new destinations added after
this step was created.

•  all: All destinations are filtered if the conditions indicated are true. This

includes new destinations added after this step was created.




Attachment Handlers

An Attachment Handler allows you to extract pieces of any incoming message and store them
separately. As a message processes through a channel, multiple copies of it will be held in memory at
once (for the raw / transformed / encoded versions of a message, etc.). Attachments are stored only once,
so by using them you can greatly reduce your channels' memory footprint. These are configured in the
Channel Properties (on page 205) section of the Summary Tab (on page 204) within the Edit Channel
View (on page 203).

Note: See Attachment JavaScript Functions (on page 455) for more information.

By default the attachment handler is set to None, meaning no attachments will be extracted. To extract
attachments choose an attachment handler type from the drop-down menu, and select the Properties
button to configure the handler.

Note: If Store Attachments next to the Attachment menu is unchecked, then attachments will be
extracted from the incoming message data, but not actually stored anywhere.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 219 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Extraction

When an attachment handler extracts data from a message, it leaves behind an attachment replacement
token like this:

This value tells the destination connector where in your message the attachment should be re-inserted
right before dispatching to an outbound system. If multiple attachments were extracted for a message,
then there will be multiple replacement tokens in the raw data.

Reattachment

Right before a destination connector dispatches a message to the external system, it scans the outbound
message for attachment replacement tokens and automatically re-inserts the actual attachment data. You
can prevent a destination from reattaching data by disabling the Reattach Attachments option in the
Destination Settings (on page 249).

Expanded Replacement Tokens

The standard attachment replacement token only includes the attachment ID, and is implicitly assumed
to be tied to the current message / channel. However if you disable Reattach Attachments in the
Destination Settings (on page 249), the destination will replace the token not with the actual attachment
data, but instead with an expanded token:

The expanded token contains the channel ID, message ID, and attachment ID, so that you can uniquely

Page 220 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

identify an attachment even from a completely different channel or message. Because of this, you can
use this replacement token in downstream channels and reattach attachments from earlier, upstream
channels.

Attachment MIME Types

As explained in the Attachments Tab (on page 118) section, there are four types of attachment viewers
in the Message Browser (on page 103): Text, Image, DICOM, and PDF. The type s of attachment
corresponding with these viewers are:

•  text/*: Plain textual data.

•  image/*: Image data (JPGs, PNGs, etc.).

•  DICOM: A special (not strictly MIME) type reserved for DICOM attachment data.

•  application/pdf: PDF data.

The * is a wildcard, signifying that anything can be present there. For example, if you are reading in
RTF data, the appropriate MIME type would be text/rtf, which matches the text/* type when the
message browser is searching for an attachment viewer.

Note that when extracting / creating attachments, you can use any type you want. It has no effect on how
the data is stored or reattached (except for the DICOM special case), only how it is displayed in the
message browser.

Attachment Handler Properties

The following attachment handlers are supported:

•  Entire Message Attachment Handler Properties (on page 221)

•  Regex Attachment Handler Properties (on page 222)

•  DICOM Attachment Handler Properties (on page 224)

•  JavaScript Attachment Handler Properties (on page 224)

•  Custom Attachment Handler Properties (on page 226)

Entire Message Attachment Handler Properties

This attachment handler takes the entire incoming message data and stores it as a single attachment. The


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 221 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Raw message data afterwards is the attachment token.

The handler has a single property, MIME Type, which specifies what type of attachment data you
expect to receive. You can use a specific value like "text/plain", or you can use Velocity replacement (on
page 469) to inject source map variables. For example if you are using an HTTP Listener (on page 362),
you can use the MIME type coming in the Content-Type header:

Regex Attachment Handler Properties

This attachment handler extracts data from the incoming message using regular expressions. You can
specify multiple expressions, each with their own MIME type. There are also options to replace certain
values on the extracted attachment data before storing it in the database, and replace values in the
attachment data right before reinserting it into the message for outbound dispatching.

Page 222 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Regular Expressions Table

The table at the top of the Regex Attachment Handler dialog shows the current regular expressions
you have configured. The first capture group is used to determine what data to extract, so if you have
other groups in the expression, make sure to include "?:" to make then non-capturing. There is an
example that shows how to extract data from the OBX.5.5 component in an HL7 v2.x message:

•  (?:OBX\|(?:[^|]*\|){4}(?:[^|^]*\^){4})([^|^\r\n]*)(?:[|^\r\n]|$)

Select the New / Delete buttons to add or remove regular expressions from the table.

For each regular expression you can also specify a MIME type. This supports Velocity Variable
Replacement (on page 469), so you can use source map variables here.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 223 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

String Replacement Tables

The bottom section of the dialog has two tables, for Inbound Replacements and Outbound
Replacements. The inbound table determines what replacements will be made on the attachment data
after it is extracted from the message, but before it gets stored in the database. The outbound table
determines what replacements will be made on the attachment data right before it gets reinserted into the
message when a destination connector is about to dispatch data to an external system.

Note: Standard Java String escape characters apply here. For example to replace a backslash, you
will actually want to use two backslashes ("\\") in the Replace All column.

DICOM Attachment Handler Properties

The DICOM Attachment Handler does not have properties to configure. When used, the handler
automatically takes the incoming DICOM data and extract all pixel data into one or more attachments.
The resulting raw data will not have an attachment replacement token, but instead will be the Base64
encoded representation of the DICOM message without the pixel data. If the DICOM Data Type (on
page 327) is used in a filter / transformer, it will automatically serialize this Base64 data into an XML
message containing all header / tag data.

Because there is no attachment replacement token, to reattach DICOM messages on the destination
connector side, a special token is used:

•  ${DICOMMESSAGE}

This indicates to the destination connector that the encoded data should be merged with any pixel data
attachments into a final binary representation before being dispatched to the external system.

JavaScript Attachment Handler Properties

This attachment handler allows you to write a custom JavaScript script to handle extracting attachments.
For additional information on how to work with JavaScript in Mirth® Connect by NextGen Healthcare,
see Mirth Connect and JavaScript (on page 428) and Attachment JavaScript Functions (on page 455).



Scope Variables

In addition to the standard global scope variables, the following are available from within the JavaScript
attachment handler script:

•  message: This is the raw inbound message string. If the data was passed in as a raw byte array,

this variable will be the Base64 encoded string representation of the data.

•  binary: This is a boolean that indicates whether the inbound data was passed in as a raw byte

array.

•  sourceMap: You have access to any variables in the source map. For additional information, see

Variable Maps (on page 449).

Extract Attachments

Use the following method to extract and store attachments from the attachment script:

The resulting Attachment object contains the ID you need to inject back into the message. For more
information, look at the User API (on page 459). The return value for the JavaScript attachment script


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 225 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

should be the final message string, with any attachments extracted out and replaced with attachment
replacement tokens.

addAttachment(data, type)

Creates and stores an attachment for the current message.

Name

data

Table 1. Parameters

Type

Description

String or Byte array

The actual attachment data to insert. Man be either a
String or a byte array. If a string is used, it is assumed
to be a Base64 encoded representation of the actual
attachment data.

The MIME type of the attachment. For additional
information see Attachment Handlers (on page 219).

type

String

<Return Value>

Attachment

The inserted Attachment object.

The resulting Attachment object contains the ID you need to inject back into the message. For more
information, look at the The User API (Javadoc) (on page 459). The Return Value for the JavaScript
attachment script should be the final message string, with any attachments extracted out and replaced
with attachment replacement tokens.

Custom Attachment Handler Properties

This attachment handler gives you full control over the attachment extraction process by allowing you to
provide a custom Java implementation of MirthAttachmentHandlerProvider. In the properties you
specify the class name, and any properties you want to pass in.

Page 226 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Properties

This is a map of string keys / string values that gets passed into your custom attachment handler
implementation. When receiving a message, the entire RawMessage object including source map data
will be available, but it will be up to your custom implementation to actually replace source map
variables.

Message Storage Settings

This section of the Summary Tab (on page 204) enables you to determine how much message data your
channel will store, whether to encrypt content, and whether to automatically delete content after
messages are finished processing. Changing these settings may affect the availability of certain features,
like queuing.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 227 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

A

Name

Storage Slider

Description

Use this slider bar to change how much data to store as
messages process through the channel. The options are:

•  Development: All data will be stored.

•  Production : Everything except Processed Raw,

Transformed, Response Transformed, and Processed
Response content will be stored.

•  Raw: Only Raw / Source Map content and attachments
will be stored. Destination queuing is not supported in
this mode, but source queuing is still supported.

•  Metadata: No message content or attachments will be
stored, only metadata (for additional information, see
About Message Data (on page 65)) . Source and
destination queuing are not supported in this mode.

•  Disabled: No message metadata, content, or

attachments will be stored. Source and destination
queuing are not supported in this mode.

B

C

D

Content

Metadata

Shows what message content will be stored for the currently
selected storage settings.

Shows what message metadata will be stored for the currently
selected storage settings. Includes custom metadata columns.

Durable Message Delivery  Shows whether Durable Message Delivery is currently enabled

based on the selected message storage settings.

•

If enabled, unfinished messages will automatically be
recovered and processed if the channel is halted, or if
the server suddenly goes down for any reason.

Page 228 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Item

Name

Description

Performance

Encrypt

•

If set to Reprocess only, unfinished messages will not
be automatically reprocessed, but you still have the
option of manually reprocessing them from the
message browser.

Shows a relative estimation of performance for each storage
option. When storage is Disabled, performance is highest, at the
cost of not having durable message delivery, or the ability to view
messages in the message browser.

•  Message content: If enabled, content stored in the

database will be encrypted. Messages that are stored
while this option is enabled will still be viewable in the
message browser, but the content will not be
searchable.

•  Attachments: Encrypt message attachments that are
stored in the database. Attachments that are stored
while this option is enabled will still be viewable in the
message browser.

•  Custom metadata: Encrypt custom metadata columns

that are stored in the database. Custom metadata
values that are stored while this option is enabled will
still be viewable in the message browser, but the
metadata will not be searchable. This will only apply to
STRING type custom metadata columns.

Remove content on
completion

Removes message content after the message has completed
processing. Not applicable for messages that are errored or
queued. If Filtered only is also checked, only content for filtered
connector messages will be removed.

Remove attachments on
completion

Removes message attachments after the message has
completed processing. Not applicable for messages that are
errored or queued.



HTTP Authentication Settings

The HTTP Authentication Settings provide automatic user authentication with a variety of supported
mechanisms. for HTTP-based source connectors.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 239 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Choose an Authentication Type

Select an option from the drop-down window.

Once an HTTP Authentication type is selected, fields associated with the type appear. For additional
information on each HTTP Authentication type, select a links below.

•  Basic HTTP Authentication (on page 240)

•  Digest HTTP Authentication (on page 241)

•  JavaScript HTTP Authentication (on page 243)

•  Custom Java Class HTTP Authentication (on page 244)

•  OAuth 2.0 Token Verification (on page 245)

JavaScript HTTP Authentication

Allows you to authenticate users with a custom JavaScript script. With this script you have access to
source map variables, and can choose whether to send a challenged or failure response back to the client.

The default script simply allows all requests to pass.

•  The Script field will show <Default Script Set> if the default script is currently being used.

•  If you have made any modification to the script, the Script field will show <Custom Script

Set>.

Select the Script field to edit the JavaScript:

This script expects either a boolean ( true to accept the request, false to send back a failure response) or


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 243 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

an AuthenticationResult object to be returned (for additional information, see User API (on page 459))
. There are three types of results you can return:

•  AuthenticationResult.Success(): The request will be accepted and processed through the

channel.

•  AuthenticationResult.Challenged(authenticateHeader): The request will not be processed

through the channel. A 401 response will be sent back to the client, with a given WWW-
Authenticate header value.

•  AuthenticationResult.Failure(): The request will not be processed through the channel. A 401

response will be sent back to the client, without any WWW-Authenticate header or any
additional information.

If a Challenged/Failure result is returned, the AuthenticationResult object also allows you to add
custom headers to include on the HTTP response sent back to the client.

For more information on using JavaScript within Mirth® Connect by NextGen Healthcare, see Mirth
Connect and JavaScript (on page 428).

Custom Java Class HTTP Authentication

This authentication method gives you full control by allowing you to specify your own custom-
developed Authenticator implementation.


Access Token Location

Determines where the access token is located in client
requests.

•  Request Header: The field to the right

specifies the HTTP header to pull the access
token from. This same header will be sent in
the request to the verification URL.

•  Query Parameter: The field to the right
specifies the query parameter to pull the
access token from. This same parameter will
be sent in the request to the verification URL.

B

Verification URL

The HTTP URL to perform a GET request to for access
token verification. If the response code is >= 400, the
authentication attempt is rejected by the server, and
the request will not be processed through the channel.

Note: This token verification feature does not constitute a fully-functioning OAuth 2.0 server. It
does not authenticate or authorize users directly, but simply delegates this to the actual OAuth

server the Verification URL points to.


Code Template Contexts

information, see Code Template Contexts (on page 307).

Generates / updates a JSDoc at the beginning of your code, with
parameter / return annotations as needed. For additional
information, see Use JSDoc in Code Templates (on page 308).

Similar to how a code template library (on page 304) can be included only on specific channels, code
templates can further be isolated to specific scripts. This can be helpful for ensuring that there are no
conflicts between function names, and also for better memory usage, since not all code templates need to
be compiled in with all scripts across your entire server. The script contexts are organized into groups,
allowing you to easily include a code template in, for example, all global scripts, in a single select. The
following groups are displayed:

•  Global Scripts: The global deploy/undeploy/preprocessor/postprocessor scripts, not specific to
any particular channel. For additional information, see Edit Global Scripts View (on page 299).

•  Channel Scripts: The channel-level deploy/undeploy/preprocessor/postprocessor scripts (more

info here (on page 255)) , as well as the JavaScript Attachment Handler (on page 224) and
JavaScript Batch Adapter (for additional information, see Data Types (on page 323)) .


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 307 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

•  Source Connector: The source filter/transformer script (on page 257), and any script associated

with the source connector. Examples of this include the JavaScript Reader (on page 371),
Database Reader (on page 346) (in JavaScript mode), and the JavaScript HTTP Authentication
script (on page 243).

•  Destination Connector: The destination filter/transformer script (on page 257), the response
transformer (on page 286), and any script associated with the destination connector. Examples
of this include the JavaScript Writer (on page 410) and Database Writer (on page 390) (in
JavaScript mode).

Use JSDoc in Code Templates

A JSDoc is a type of comment block used to annotate JavaScript scripts. For code templates in Mirth®
Connect, this is used not only for good documentation, but also for the code template description and the
information that shows up in the auto-completion dialog in the JavaScript Editor (on page 445).

When you create a new code template, a sample JSDoc is created for you:

The first portion of the comment block is used for the description of the code template, and may contain
multiple lines and blank lines. Following that, you can include any JSDoc annotations, your own custom
annotations, or whatever you want, as long as it follows correct JavaScript syntax. Only the following
annotations are recognized by Mirth Connect for the purpose of populating the auto-completion dialog
in the JavaScript Editor (on page 445):

Page 308 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

•  @param: Documents the input arguments for your function, in the order that they appear. If

you have multiple function arguments, you will want to add multiple @param annotations. The
format is:

•  @param {Type} Name - Description

Note that the type doesn't have to be an actual JavaScript type. It can be anything you want as
long as it doesn't contain the "{}" characters, like "MyObject", "String/Number", etc.

•  @return: Documents the return value for your function, if applicable. The format is:

•  @return Description

If you change the names or number of arguments in your function, you can use the Update JSDoc
button at the bottom of the editor. This will automatically inject or update @param annotations as
needed, after which you can change the Type and Description as needed.

Once you have filled out your JSDoc appropriately, the function appears in the auto-completion dialog
accessible from the JavaScript Editor (on page 445):


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 309 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Code Template Tasks

The following context-specific tasks are available throughout the Edit Code Templates View (on page
301):

Task Icon

Task Name

Description

Refresh

Save Changes

Refreshes the list of code templates / libraries. If there
are unsaved changes, you are prompted to save first
before refreshing.

Saves all changes made to all code templates / libraries.
Changes to a code template will cause its revision to
increment. Changes to a library, or adding/removing code
templates to/from the library, will cause its revision to
increment.

Page 310 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Task Icon

Task Name

Description

New Code Template

New Library

Creates a new code template in the currently selected
library. If there are currently no libraries configured for
your server, you must first create a new library before this
task becomes available.

Creates a new code template library and adds it to the
table. By default the library is not included in any
channels. Use the Channels table in the Edit Library
Panel (on page 304) to link the library to specific
channels.

Import Code Templates (on
page 307)

Import a single code template or a list of code templates
from an XML file.

Import Libraries (on page 311)

Import a single code template library or a list of libraries
(including any code templates within) from an XML file.

Export Code Template

Exports the currently selected code template to an XML
file.

Export Library

Exports the currently selected code template library to an
XML file.

Export All Libraries

Exports all libraries in the table to separate XML files.

Delete Code Template

Deletes the currently selected code template, removing it
from the table.

Delete Library

Validate Script

Deletes the currently selected code template library, and
all code templates belonging to the library.

Validates the currently selected code template, ensuring
that all properties are valid, and that the actual code has
proper syntax.

Import Code Templates/Libraries

When importing code templates/libraries, either in the Edit Code Templates View (on page 301) or when
importing a channel (on page 154) containing libraries, you are presented with a confirmation dialog.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 311 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

All libraries and code templates contained in the import file/channel appear in the dialog. From here you
can choose to import everything, or individually select specific entries to import. Once you have made
your selections, select the Import button to actually perform the import operation.

•  If a code template or library already exists in your Mirth® Connect server, the Overwrite

column displays a checkbox. If checked, the entry will overwrite the current code template /
library rather than creating a new one. If there are multiple entries with such conflicts, you can
easily choose to overwrite all or none of them with the All / None links at the top-right of the
dialog.

•  If the library or code template has a name that conflicts with another entry in your current table,
the Conflicts column displays a red error icon. In this case, you must choose to either overwrite
the current entry, or update its name by double-clicking on the Name column.

•  If a code template already exists in a different library than the one you are trying to import into,

you will see a yellow warning icon. In this case, you can choose to overwrite the current
template, ignore the warning and continue, or simply cancel the operation.

When importing only code templates by themselves, you must first select a library to import them into.



Data Types

This section describes the various common properties configurable across data types, and specific
properties for each data type. For an introduction to data types in general, see About Data Types (on
page 72).

Whether a data type is used as inbound or outbound, and whether it is tied to a source connector,
destination connector, or destination response, affects what properties it needs. The following groups of
properties may be displayed in the Set Data Types Window (on page 207) depending on the data type
and context:

Inbound Properties

•  Serialization Properties: Determines how to convert data from the raw inbound format to the

internal representation (e.g. XML). If a data type doesn't have serialization properties present, it
either doesn't need any (DICOM, JSON), or it doesn't actually do any serialization (Raw).

•  Batch Properties : Determines how to split an incoming message into multiple messages. Only
supported when Process Batch is enabled in the Source Settings (on page 238). This will only
be displayed for source connectors. Not all data types support batch processing (DICOM).

•  Response Generation Properties: When an auto-generation option is chosen for the response
on the Source Settings (on page 238), these properties determine how to generate an automatic
response. This will only be displayed for source connectors. Not all data types support
automatic response generation.

•  Response Validation Properties: Determines how to validate responses received by a

destination after dispatching a message. Only supported when Validate Response is enabled in
the Destination Settings (on page 249). This will only be displayed for destination responses.
Not all data types support automatic response generation.

Outbound Properties

•  Deserialization Properties: After a transformer finishes, these properties determine how to


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 323 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

convert data from the final internal representation (e.g. XML) into the outbound data format. If
a data type doesn't have deserialization properties present, it either doesn't need any (DICOM,
EDI/X12, XML, JSON) or it doesn't actually do any deserialization (Raw).

•  Template Serialization: If an outbound template is specified for the transformer, these

properties determine how to convert that template to its corresponding internal representation
(e.g. XML).

Mirth® Connect supports the following data types:

•  Delimited Text Data Type (on page 324)

•  DICOM Data Type (on page 327)

•  EDI / X12 Data Type (on page 328)

•  HL7 v2.x Data Type (on page 329)

•  HL7 v3.x Data Type (on page 332)

•  JSON Data Type (on page 333)

•  NCPDP Data Type (on page 334)

•  Raw Data Type (on page 335)

•  XML Data Type (on page 336)

An additional data type is made available as a commercial extension:

•  ASTM E1394 Data Type (on page 586)

Also see:

•  Batch Processing (on page 337)

Delimited Text Data Type

The Delimited Text data type is a very powerful and flexible data type that can satisfy many common
formats like CSV, but also many proprietary formats that are dependent on custom delimiters, fixed-
width columns, or other nuances. The following properties are configurable:

Page 324 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

Serialization / Template Serialization / Deserialization Properties

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Default Value

Description

,

\n

Name

Column Delimiter

Record Delimiter

Column Widths

If column values are delimited, enter the
characters that separate columns. For
example, this is a comma in a CSV file.

Enter the characters that separate each
record (a message may contain multiple
records). For example, this is a newline (\n) in
a CSV file.

If the column values are fixed width, enter a
comma separated list of fixed column widths.
By default, column values are assumed to be
delimited.

Enter the quote characters that are used to
bracket delimit column values containing
embedded special characters like column
delimiters, record delimiters, quote characters
and/or message delimiters. For example, this
is a double quote (") in a CSV file.

By default, two consecutive quote tokens
within a quoted value are treated as an
embedded quote token. Uncheck to enable
escaped quote token processing (and specify
the Escape Tokens).

Enter the characters used to escape
embedded quote tokens. By default, this is a
back slash. This option has no effect unless
Double Quote Escaping is unchecked.

To override the default column names
(column1, ..., columnN), enter a comma
separated list of column names.

Check to number each row in the XML
representation of the message.

Ignores carriage return (\r) characters. These
are read over and skipped without processing
them.

Quote Token

"

Double Quote Escaping

Enabled

Escape Token

\

Column Names

Numbered Rows

Disabled

Ignore Carriage Returns

Enabled

Batch Properties


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 325 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Name

Split Batch By

Record

Default Value

Description

Select the method for splitting the
batch message. This option has no
effect unless Process Batch is
enabled in the Source Settings (on
page 238). The following options are
available:

•  Record: Treat each record
as a message. Records are
separated by the record
delimiter from the
serialization properties.

•  Delimiter: Use the Batch
Delimiter to separate
messages.

•  Grouping Column: Use a
column to group multiple
records into a single
message. When the
specified column value
changes, this signifies the
boundary between
messages.

•  JavaScript: Use JavaScript

to split messages. For
additional information, see
JavaScript Batch Script (on
page 338).

The number of header records to
skip. By default, no header records
are skipped.

The delimiter that separates
messages. The batch delimiter may
be a sequence of characters.

Check to include the batch delimiter in
the message returned by the batch
processor. By default, batch delimiters
are consumed.

The name of the column used to
group multiple records into a single
message. When the specified column
value changes, this signifies the
boundary between messages.

Number of Header Records

0

Batch Delimiter

Include Batch Delimiter

Disabled

Grouping Column

Page 326 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

Name

JavaScript

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Default Value

Description

Enter JavaScript that splits the batch,
and returns the next message. This
script has access to 'reader', a Java
BufferedReader, to read the incoming
data stream. The script must return a
string containing the next message, or
a null/empty string to indicate end of
input. For additional information, see
JavaScript Batch Script (on page
338).

DICOM Data Type

This data type works in conjunction with the DICOM Listener (on page 341) / DICOM Sender (on page
386) and the DICOM Attachment Handler (on page 224) to consume and transformer DICOM
     messages. It has no configurable data type properties, but will automatically convert binary DICOM data
     to and from an XML format specified by the dcm4che parser library.

Example XML snippet:

<dicom>
    <tag00020000 len="4" tag="00020000" vr="UL">212</tag00020000>
    <tag00020001 len="2" tag="00020001" vr="OB">00\01</tag00020001>
    <tag00020002 len="26" tag="00020002" vr="UI">1.2.840.1000
8.5.1.4.1.1.4</tag00020002>
    <tag00020003 len="60" tag="00020003" vr="UI">1.3.46.670589.11.3
0.9.1062531302827752870602.13.1.1.1.0.0.1</tag00020003>
    <tag00020010 len="18" tag="00020010" vr="UI">1.2.840.10008.1.2</t
ag00020010>
    <tag00020012 len="18" tag="00020012" vr="UI">1.3.46.670589.1
7.1</tag00020012>
    <tag00020013 len="14" tag="00020013" vr="SH">ARCVTS04NOV99</tag00
020013>
    <tag00020016 len="14" tag="00020016" vr="AE">VTS_DCM_STORE</tag00
020016>
    <tag00080005 len="10" tag="00080005" vr="CS">ISO_IR 100</tag00080
005>
    <tag00080008 len="26" tag="00080008" vr="CS">ORIGINAL\PRIMARY\M_S
E\M\SE</tag00080008>

Each node in the XML document contains the attribute length, tag code, value representation, and actual


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 327 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

value. These can be used or modified within transformers, and the DICOM data type will automatically
convert the finished XML to the native DICOM binary format.

EDI / X12 Data Type

This data type handles both UN/EDIFACT and ASC X12 data formats, as well as custom formats
similar to EDI / X12 that use a segment / element / subelement delimiter.

Serialization / Template Serialization / Deserialization Properties

Default Value

Description

Name

Segment Delimiter

Element Delimiter

Subelement Delimiter

~

*

:

Infer X12 Delimiters

Enabled

Characters that delimit the segments in the
message.

Characters that delimit the elements in the
message.

Characters that delimit the subelements in the
message.

This property only applies to X12 messages. If
checked, the delimiters are inferred from the
incoming message and the delimiter
properties will not be used.

Batch Properties

Name

Split Batch By

JavaScript

Default Value

Description

JavaScript

Select the method for splitting the
batch message. This option has no
effect unless Process Batch is
enabled in the Source Settings (on
page 238). The following options are
available:

•  JavaScript: Use JavaScript

to split messages. For
additional information, see
JavaScript Batch Script (on
page 338).

Enter JavaScript that splits the batch,
and returns the next message. This
script has access to 'reader', a Java

Page 328 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Name

Default Value

Description

BufferedReader, to read the incoming
data stream. The script must return a
string containing the next message, or
a null/empty string to indicate end of
input. For additional information, see
JavaScript Batch Script (on page
338).

HL7 v2.x Data Type

This data type enables powerful and flexible parsing and manipulation of HL7 v2.x messages. It features
two modes, strict and non-strict. The strict mode parses messages into and from XML according to the
official XSD, and enables automatic validation against the HL7 specifications. The non-strict mode
parses messages into a simple, consistent XML structure consisting of the segment/field/component/
subcomponent hierarchy, and enables quick and easy transformation in most use-cases.

The data type also features an automatic response (ACK) generator, and a response validator that can
mark messages as failed when a negative acknowledgement is received or when the message control IDs
do not match. It also has a batch adapter that can split messages based on the MSH segment, even while
streaming over a TCP connection.

Serialization / Template Serialization / Deserialization Properties

Name

Default Value

Description

Parse Field Repetitions

Enabled

Parse Subcomponents

Enabled

Use Strict Parser

Disabled

Validate in Strict Parser

Disabled

Strip Namespaces

Enabled

Segment Delimiter

\r

Parse field repetitions (applies to
Non-Strict Parser only).

Parse subcomponents (applies to
Non-Strict Parser only).

Parse messages based on strict HL7
specifications.

Validate messages using HL7
specifications (applies to Strict Parser
only).

Strips namespace definitions from the
transformed XML message (applies to
Strict Parser only).

This is the input delimiter characters
expected to occur after each


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 329 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Name

Default Value

Convert Line Breaks

Enabled

Description

segment.

Convert all styles of line breaks
(CRLF, CR, LF) in the raw message
to the segment delimiter.

Batch Properties

Name

Default Value

Description

Split Batch By

MSH Segment

JavaScript

Select the method for splitting the batch message.
This option has no effect unless Process Batch is
enabled in the Source Settings (on page 238). The
following options are available:

•  MSH Segment: Each MSH Segment

indicates the start of a new message in
the batch.

•  JavaScript: Use JavaScript to split

messages. For additional information, see
JavaScript Batch Script (on page 338).

Enter JavaScript that splits the batch, and returns
the next message. This script has access to
'reader', a Java BufferedReader, to read the
incoming data stream. The script must return a
string containing the next message, or a null/empty
string to indicate end of input. For additional
information, see JavaScript Batch Script (on page
338).

Response Generation Properties

Name

Default Value

Description

Segment Delimiter

\r

Successful ACK Code

AA

These are the delimiter character(s) that will be
used after each segment. This option has no
effect unless an "Auto-generate" item has been
selected in the response settings.

The ACK code to respond with when the
message processes successfully. This value
supports Velocity Variable Replacement (on
page 469) with values from the current
connector message.

Page 330 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Name

Default Value

Description

Successful ACK Message

Error ACK Code

AE

Error ACK Message

An Error Occurred Processing
Message.

Rejected ACK Code

AR

Rejected ACK Message

Message Rejected.

MSH-15 ACK Accept

Disabled

Date Format

yyyyMMddHHmmss.SSS

The ACK message to respond with when the
message processes successfully. This value
supports Velocity Variable Replacement (on
page 469) with values from the current
connector message.

The ACK code to respond with when an error
occurs during message processing. This value
supports Velocity Variable Replacement (on
page 469) with values from the current
connector message.

The ACK message to respond with when an
error occurs during message processing. This
value supports Velocity Variable Replacement
(on page 469) with values from the current
connector message.

The ACK code to respond with when the
message is filtered. This value supports Velocity
Variable Replacement (on page 469) with
values from the current connector message.

The ACK message to respond with when the
message is filtered. This value supports Velocity
Variable Replacement (on page 469) with
values from the current connector message.

This setting determines if Mirth should check the
MSH-15 field of an incoming message to control
the acknowledgment conditions. The MSH-15
field specifies if a message should be always
acknowledged, never acknowledged, or only
acknowledged on error.

This setting determines the date format used for
the timestamp in the generated ACK. The
default value is "yyyyMMddHHmmss.SSS".

Response Validation Properties

Name

Default Value

Description

Successful ACK Codes

AA,CA

The ACK code(s) to expect when the
message is accepted by the
downstream system. By default, the
message status will be set to SENT.
Specify multiple codes with a list of
comma separated values.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 331 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Name

Default Value

Description

Error ACK Codes

AE,CE

Rejected ACK Codes

AR,CR

Validate Message Control Id

Enabled

Original Message Control Id

Destination Encoded

Original Id Map Variable

The ACK code(s) to expect when an
error occurs on the downstream
system. By default, the message
status will be set to ERROR. Specify
multiple codes with a list of comma
separated values.

The ACK code(s) to expect when the
message is rejected by the
downstream system. By default, the
message status will be set to
ERROR. Specify multiple codes with
a list of comma separated values.

Select this option to validate the
Message Control Id (MSA-2) returned
from the response.

Select the source of the original
Message Control Id used to validate
the response. If Destination Encoded
is selected, the Id will be extracted
from the MSH-10 field of the
destination's encoded content. If Map
Variable is selected, the Id will be
retrieved from the destination's
connector map or the channel map.

This field must be populated if the
Original Message Control Id is set to
Map Variable. The Id will be read from
this variable in the destination's
connector map or the channel map.

HL7 v3.x Data Type

This data type handles HL7 v3.x messages. No actual serialization or deserialization is needed because
the data format is the same as the internal representation format (XML), but it still has options to strip
namespaces if needed.

Serialization / Template Serialization Properties

Name

Default Value

Description

Strip Namespaces

Enabled

Strips namespace definitions from the
transformed XML message. Will not remove
namespace prefixes. If you do not strip

Page 332 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Name

Default Value

Description

namespaces your default xml namespace will
be set to the incoming data namespace. If
your outbound template namespace is
different, you will have to set "default xml
namespace = 'namespace';" via JavaScript
before template mappings.

Batch Properties

Name

Split Batch By

JavaScript

Default Value

Description

JavaScript

Select the method for splitting the
batch message. This option has no
effect unless Process Batch is
enabled in the Source Settings (on
page 238). The following options are
available:

•  JavaScript: Use JavaScript

to split messages. For
additional information, see
JavaScript Batch Script (on
page 338).

Enter JavaScript that splits the batch,
and returns the next message. This
script has access to 'reader', a Java
BufferedReader, to read the incoming
data stream. The script must return a
string containing the next message, or
a null/empty string to indicate end of
input. For additional information, see
JavaScript Batch Script (on page
338).

JSON Data Type

This data type allows seamless integration between JSON messages and the filter/transformer scripts
which are JavaScript-based. When using the JSON data type, the msg / tmp variables will be a standard
JavaScript object, as opposed to an E4X XML object which some other data types use. Since JSON is a
very lightweight data format, and the transition from String to JavaScript Object is all handled
automatically, no serialization properties are needed.

Batch Properties


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 333 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Name

Split Batch By

JavaScript

Default Value

Description

JavaScript

Select the method for splitting the batch
message. This option has no effect unless
Process Batch is enabled in the Source
Settings (on page 238). The following
options are available:

•  JavaScript: Use JavaScript to
split messages. For additional
information, see JavaScript Batch
Script (on page 338).

Enter JavaScript that splits the batch, and
returns the next message. This script has
access to 'reader', a Java BufferedReader, to
read the incoming data stream. The script
must return a string containing the next
message, or a null/empty string to indicate
end of input. For additional information, see
JavaScript Batch Script (on page 338).

NCPDP Data Type

This data type handles the flat file format for National Council for Prescription Drug Programs
(NCPDP) pharmacy data.

Serialization / Template Serialization / Deserialization Properties

Default Value

Description

Name

Field Delimiter

Group Delimiter

Segment Delimiter

0x1C

0x1D

0x1E

Use Strict Validation

Disabled

Batch Properties

Characters that delimit the fields in the
message.

Characters that delimit the groups in the
message.

Characters that delimit the segments in the
message.

Validates the NCPDP message against the
appropriate schema. Only applicable for the
Deserialization properties.

Page 334 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

Name

Split Batch By

JavaScript

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Default Value

Description

JavaScript

Select the method for splitting the
batch message. This option has no
effect unless Process Batch is
enabled in the Source Settings (on
page 238). The following options are
available:

•  JavaScript: Use JavaScript

to split messages. For
additional information, see
JavaScript Batch Script (on
page 338).

Enter JavaScript that splits the batch,
and returns the next message. This
script has access to 'reader', a Java
BufferedReader, to read the incoming
data stream. The script must return a
string containing the next message, or
a null/empty string to indicate end of
input. For additional information, see
JavaScript Batch Script (on page
338).

Raw Data Type

This data type allows a channel / connector to process any custom data not handled by any of the other
data types. When using it, the msg / tmp variable accessible in the filter/transformer will be a String
rather than an E4X XML object which some other data types use. This data type also has the special
property that when used as the Response Inbound data type for a destination, the response transformer
will always be executed, even if no actual response data was received. No serialization properties are
needed since there is no conversion done.

Batch Properties

Name

Split Batch By

Default Value

Description

JavaScript

Select the method for splitting the
batch message. This option has no
effect unless Process Batch is
enabled in the Source Settings (on
page 238). The following options are
available:


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 335 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Name

Default Value

Description

JavaScript

•  JavaScript: Use JavaScript

to split messages. For
additional information, see
JavaScript Batch Script (on
page 338)

Enter JavaScript that splits the batch,
and returns the next message. This
script has access to 'reader', a Java
BufferedReader, to read the incoming
data stream. The script must return a
string containing the next message, or
a null/empty string to indicate end of
input. For additional information, see
JavaScript Batch Script (on page 338)

XML Data Type

This data type handles XML messages. No actual serialization or deserialization is needed because the
data format is the same as the internal representation format (XML), but it still has options to strip
namespaces if needed.

Serialization / Template Serialization Properties

Name

Default Value

Description

Strip Namespaces

Disabled

Identifies the strips namespace definitions from
the transformed XML message. Will not remove
namespace prefixes. If you do not strip
namespaces your default xml namespace will
be set to the incoming data namespace. If your
outbound template namespace is different, you
will have to set "default xml namespace =
'namespace';" via JavaScript before template
mappings.

Batch Properties

Name

Default Value

Description

Split Batch By

Element Name

Select the method for splitting the batch
message. This option has no effect unless

Page 336 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Name

Default Value

Description

Process Batch is enabled in the Source
Settings. The following options are available:

•  Element Name: Use the element
name to split messages. Does not
work with namespaces.

•  Level: Use the element level to split

messages. Default is 1.

•  XPath Query: Use a custom XPath

Query to split messages.

•  JavaScript: Use JavaScript to split

messages. For additional
information, see JavaScript Batch
Script.

Enter JavaScript that splits the batch and
returns the next message. This script has
access to 'reader', a Java BufferedReader, to
read the incoming data stream. The script
must return a string containing the next
message, or a null/empty string to indicate
end of input. For additional information, see
JavaScript Batch Script.

JavaScript

––-

Batch Processing

Batch Processing allows a channel to receive a single message, but split it into multiple messages that
each get processed through the channel. When using this along with a source connector that supports
streaming (File Reader, TCP Listener in MLLP mode), batch processing has the added benefit of not
having to read the entire file into memory all at once, but instead only one message at a time. For
example with the File Reader and batch processing you can read in files that are gigabytes in size,
without causing any memory issues for your Mirth Connect server. Data types that support batch
processing will have a Batch Properties section in the source inbound data type properties.

To enable batch processing, set Process Batch to Yes in the Source Settings (on page 238). To change
how an incoming message is split into multiple messages, look at the Batch Properties section of the
source inbound data type you are using. For example, the Delimited Text Data Type (on page 324) has
options to split by record delimiter, a specific hard-coded delimiter, a grouping column, or a custom
JavaScript script.

When batch messages are processing through a channel, some extra Source Map variables (on page 449)


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 337 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

are available:

Key

batchId

batchSequenceId

batchComplete

Description

This is a unique ID that identifies the current overall batch
file/message. It will be equal to the message ID of the first
message that gets processed for the current batch. For
example if you read in a single file and your batch
processor splits it into 5 messages, all 5 of those
messages will have the same batchId value in the source
map.

This is an integer that starts at 1 and increments for each
subsequent message in the batch.

This is a boolean value, equal to either true or false. It
indicates whether the currently processing message is the
last one in the current batch. Since this value is returned
from a map, it will be a java.lang.Boolean object. An
example of how you could use this is: if
($('batchComplete').booleanValue()) { //
Code here will execute when batchComplete
is true }

JavaScript Batch Script

The JavaScript batch adapter is an option common to almost all data types. When Process Batch is
enabled on the Source Settings (on page 238), you can set this script from the Set Data Types Window
(on page 207) to programmatically decide how to split the incoming data into multiple messages.

Within the batch script you have access to a variable called "reader", which is a Java BufferedReader
object. Use this variable to consume from the underlying character stream and return a String for each
message you want to process through the channel. When you decide that no more messages should be
processed, or you reach the end of the stream, return null or an empty string.

Example 1

This script will simply send a message for every line in the input.

return reader.readLine();

Example 2

This script will split the input into multiple messages by assuming that each new message starts with a
line break and the characters "MSH". Note that this is already a feature supported by the , but is shown

Page 338 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

here to illustrate how the batch script can be used.

var message = new java.lang.StringBuilder();

var line;
while ((line = reader.readLine()) != null) {
message.append(line).append('\r');

    // Mark the stream for 3 characters while we check for MSH
    reader.mark(3);
    // Check for the code points corresponding to MSH
    if (reader.read() == 77 && reader.read() == 83 && reader.read()
== 72) {
reader.reset();
break;
}
reader.reset();
}

return message.toString();


Mirth Connect and JavaScript

JavaScript is a scripting language that can be used in a wide variety of places throughout Mirth®
Connect to perform advanced routing and transformation.

About JavaScript

This section provides a basic explanation of how the language works:

•  Variables (on page 428)

•  Comments (on page 429)

•  Arrays (on page 429)

•  Operators (on page 430)

•  Conditional Statements (on page 432)

•  Loops and Iterations (on page 433)

•  Exception Handling (on page 435)

Variables

Unassigned variables have the value undefined by default; string literals can use single or double quotes;
braces { } create a block of statements that can be used for loops, conditionals, and statements. These
are some examples of variable declarations:

var x;
var y = r;
z = "abc"

Page 428 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Comments

You can start a single-line comment with two forward slashes, or a multi-line comment with forward
slashes and asterisks:

// This is a single-line comment

/*
This is a
multi-line
comment
*/

Arrays

Arrays are native objects indexed with bracket notation that can contain other objects, arrays, or
primitive types. Arrays can be initialized using Java-like constructors or bracket notation and do not
need to be sized upon construction:

var myArray1 = new Array(10);
var myArray2 = new Array(5, "some string", new Array());
var myArray3 = [];
var myArray4 = [ "one", "two", "three" ];

Uninitialized elements in arrays are undefined, so use the length property (var size = myArray.length;)
to get a size. Use the delete operator to remove the index value, which sets the element as undefined.
There are several built-in methods for arrays: concat(), reverse(), replace(), sort(), indexOf(). Arrays
in JavaScript are zero-based indexed, so use myArray[0] to access the first element. Other important
native objects in JavaScript include:

•  String

•  Date

•  Boolean

•  RegExp (regular expressions)

•  Math

•  XML


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 429 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Operators

The most common JavaScript operators can be put into the following categories:

•  Arithmetic Operators (on page 430)

•  Assignment Operators (on page 431)

•  Comparison Operators (on page 431)

•  Logical Operators (on page 432)

Arithmetic Operators

These operators are used to perform arithmetic between variables and/or values. In this table, the
y-variable has a value of 5 to explain the JavaScript Arithmetic operators:

Operator  Description

Example

Result of
x

Result of
y

+

-

*

/

%

++

Addition

x=y+2 (5+2)

Subtraction

x=y-2 (5-2)

Multiplication

x=y*2 (5*2)

Division

x=y/2 (5/2)

Modulus (division
remainder)

x=y%2 (remainder is 1 for the equation
5/2)

Increment

x=++y (++6)

--

Decrement

x=y++ (6++)

x=--y (--4)

x=y-- (4--)

7

3

10

2.5

1

6 (a)

5 (c)

4 (d)

5 (f)

5

5

5

5

5

6 (b)

6 (b)

4 (e)

4 (e)

(a) The increment occurs before the variable, so x is the incremented value of y, which is 6; (b) The value is 6 because
y, which is 5, is increased by 1 for this operator; (c) The increment occurs after the variable, so x is the original value of
y, which is 5; (d) The decrement occurs before the variable, so x is the decremented value of y, which is 4; (e) The
value is 4 because y, which is 5, is decreased by 1 for this operator; (f) The decrement occurs after the variable, so x
is the original value of y, which is 5.

Page 430 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Assignment Operators

These operators are used to assign values to JavaScript variables. In this table, the x-variable has a value
of 10, and the y-variable has a value of 5 to explain the JavaScript Assignment operators:

Operator

Description

Example

Same As

Result

=

+=

-=

*=

/=

%=

assign

x=y

x=y (5)

add and assign

x+=y

x=x+y (10+5)

subtract and assign

x-=y

x=x-y (10-5)

multiply and assign

x*=y

x=x*y (10*5)

divide and assign

x/=y

x=x/y (10/5)

modulus (division
remainder) and
assign

x%=y

x=x%y (remainder is
0 for the equation 10/
2)

x=5

x=15

x=5

x=50

x=2

x=0

Comparison Operators

Comparison operators are used in logical statements to determine equality or difference between
variables or values. In this table, the x-variable has a value of 5 to explain the JavaScript Comparison
operators:

Operator  Description

Comparing  Returns

==

is equal to

x==8

x==5

x!=8

x>8

x<8

is not equal to

is greater than

is less than

is greater than or equal to  x>=8

is less than or equal to

x<=8

false

true

true

false

true

false

true

!=

>

<

>=

<=


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 431 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Logical Operators

Logical operators are used to determine the logic between variables or values. In this table, the
x-variable has a value of 6, and the y-variable has a value of 3 to explain the JavaScript Logical
operators:

Operator

Description

Example

&&

||

!

and

or

not

(x < 10 && y > 1) is true

(x==5 || y==5) is false

!(x==y) is true

Conditional Statements

This example shows the basic syntax structure for conditional statements in JavaScript:

if (condition1) {
// Code to execute
} else if (condition2) {
// Code to execute
} else {
// Code to execute
}

Functions

This example shows the basic syntax structure for the creation of functions in JavaScript:

function functionName (p1, p2, …, pN) {
// Code to execute
return someValue;
}

There are various ways to call a function [e.g., var x = myFunction ("ABC", 100, myVar);].
Parameters are optional and unlimited. The return statement is also optional.

Page 432 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Loops and Iterations

A loop is a type of programming-language statement that lets code be executed repeatedly; that is, a loop
is a series of iterations. In programming language, there are four types of loops:

•  for loops (on page 433)

•  for each…in loops (on page 433)

•  while loops (on page 434)

•  do…while loops (on page 434)

An iteration is a single execution of the inner loop process. If you loop from 1 to 10, the code inside the
loop will be executed for 10 iterations.

Loops can be unconditionally exited with a break statement: break;. The continue statement: continue;
unconditionally skips to the next iteration of the loop.

for loops

These loops are often distinguished by an explicit loop counter (variable), which lets the body of the for
loop (the code that is being repeatedly executed) know about the sequencing of each iteration. for loops
are typically used when the number of iterations is known before the loop is entered.

Syntax (for loops)

Example

for (index=startValue; endCondition; incInd
ex) {
// Code to execute
}

for (var i=0; i<10; i++) {
logger.info("i = " + i);
}

for each…in loops

These loops are part of the E4X standard. Unlike other for loop constructs, for each…in loops usually
have no explicit counter; they essentially say "do this to everything in this set" rather than "do this X
times." This avoids possible off-by-one errors and makes code easier to read. These loops are used to
iterate through elements in an array (or collection) or in the property values of an object.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 433 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Syntax (for each...in loops)  Example

for each (var in object) {
// Code to execute
}

var sources = new Array ();
sources [0] = "Customer 1";
sources [1] = "Customer 2";
sources [2] = "Customer 3";
for each (src in sources) {
logger.info(src);
}

while loops

These loops are control-flow statements that let code execute repeatedly based on a given Boolean (true/
false) condition. A while loop can be thought of as a repeating "if" statement and consists of a block of
code and a condition. Upon evaluation, if the condition is true, the code in the block is executed,
repeating until the condition becomes false. A >while loop checks the condition before the block is
executed, in contrast to a do…while loop, which tests the condition after the block is executed.

Syntax (while loops)

Example

while (condition) {
// Code to execute
}

var index = 0;
var found = false;
while (!found) {
if (myArray[index++] == "ABC") {
found = true;
}
}

do…while loops

These loops let code execute at least once based on a Boolean (true/false) condition. A do...while loop
consists of a process symbol and a condition. The code in the block executes, and the condition is
evaluated. If the condition is true, the code in the block is executed again, repeating until the condition
becomes false. A do…whileloop checks the condition after the block is executed, in contrast to the
while loop, which tests the condition before the block is executed. It is possible—and sometimes
desirable—for the condition to always evaluate as true, which creates an infinite loop. When such a loop
is created purposely, there is usually another control structure, such as a break statement, that terminates
the loop.

Page 434 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Syntax (do...while loops)

Example

do {
// Code to execute
} while (conditional);

var index = -1;
do {
var val = getValue(++index);
} while (val != "ABC");

Exception Handling

The variable in a catch statement is of the Error type or one of its subclasses. You can raise exceptions
with a throw statement:

•  throw "This is my exception message!";

•  throw new RangeError("Var x is not between 1 and 100");

Uncaught exceptions inside a connector result in an error status for the processed message.

Syntax (Exception Handling)  Example

try {
// Code to execute
} catch (exception) {
// Exception handling
// code to execute
} finally {
// Code to always execute
}

try {
var x = undefinedVarName;
} catch (e) {
logger.error("Error: "+e);
}

Using JavaScript in Mirth Connect

This section shows you how to use JavaScript to perform various messaging operations within Mirth®
Connect.

About E4X

ECMAScript for XML (E4X), introduced in JavaScript 1.6, is a JavaScript extension that provides
native XML support to ECMAScript:


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 435 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

var person1 = new XML("<person></person>");
var person2 = <person></person>;

E4X supplies a simpler alternative to Document Object Model (DOM) interfaces for accessing XML
documents. E4X also offers a new way to make XML visible. Prior to E4X, XML had to be accessed at
an object level. E4X regards XML as primitive level, which suggests quicker access, improved support,
and acknowledgment as a component (data structure) of a program. Provided below are several useful
XML object methods:

•  appendChild()—appends a child element

•  name()—gets the name of an element

•  attribute()—gets an attribute of an element

•  children()—gets a list of all of an element's child elements

•  length()—gets the count of an element's child elements.

Use DOM-like syntax to access XML elements and use @ for element attributes:

<person>
      <name>
            <first/>
            <last/>
      </name>
<address type="home"/>
</person>
person.name.first = "Joe"
person['name']['first'] = "Joe"
…
person.address.@type = "work";
person['address']['@type'] = "work";

Accessing Message Data with E4X

<?xml version="1.0" encoding="UTF-8"?>
<HL7Message>
      <MSH>
            <MSH.1>|</MSH.1>
            <MSH.2>|^~\&amp;</MSH.2>
            <MSH.3>

Page 436 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

                  <MSH.3.1>SENDAPP</MSH.3.1>
            </MSH.3>
            <MSH.4>
                  <MSH.4.1>General Hospital</MSH.4.1>
            </MSH.4>
            <MSH.5>
                  <MSH.5.1>RECAPP</MSH.5.1>
            </MSH.5>
            <MSH.6/>
…
</MSH>
…
</HL7Message>

The XML variables msg and tmp represent root-level elements. Use JavaScript bracket notation for
each element level in the document below the root (this works with any message data, not only HL7):

var sendingFacility = msg ['MSH']['MSH.4'][MSH.4.1'].toString();

Most values in an HL7 message go three levels deep. The first level is a segment; the second level is a
field within the segment; the third level is a component within a segment field. If a field or component
does not exist, it is created automatically. Examples for repeating segments and fields include using
bracket notation to index:

var obx = msg['OBX'];
var obx5 = obx['OBX.5'];
var obx5_1 = obx5['OBX.5.1'].toString();

All E4X methods available on the msg and tmp variables can be accessed through the auto-completion
dialog in the JavaScript Editor (on page 445).

Adding Segments to a Message

Segments are a group of fields that can contain varying types of data. Each segment exists independently
and can be used in multiple messages.

To add a segment to a message, create a new XML object with a segment code, then add it after the
segment it should follow:

var seg = new XML("<ZZZ><ZZZ.1><ZZZ.1.1>My Value</ZZZ.1.1></ZZZ.1></Z
ZZ>");
msg['QRF'] += seg;


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 437 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Insert the += operator into a message after a particular segment or at the end of the message. Mirth®
Connect has four global functions by which you can create segments for messages:

Function

Function Description

Example

Create Segment (individual)  Creates an XML object for the segment that

has not been inserted into a message.

Create Segment (in
message)

Creates an XML object for the segment at the
end of a specified message.

var newSeg = createSegmen
t('ZYX');
newSeg ['ZYX']['ZYX.1']['ZY
X.1.1'] = "My Value";
msg += newSeg;

createSegment ('ZYX', msg);
msg ['ZYX']['ZYX.1']['ZY
X.1.1'] = "My Value";

Create Segment (in
message, index)

Creates an XML object for the segment in a
specified message (msg or tmp) in a specified
index and is issued for repeating segments; if
a segment is already in the index, the new
segment overwrites it.

createSegment ('OBX', msg,
4);
msg ['OBX'][4]['OBX.3']['OB
X.3.2'] = "Glucose";

Create Segment After
Segment

Creates an XML object for the segment and
adds it after the target segment.

createSegmentAfter("ZZZ", ms
g ["QRF"]);
msg ["ZZZ"]["ZZZ.1"]["ZZ
Z.1.1") = "My Value";

which function call is equiv
alent to:

msg ["QRF"] += createSegmen
t("ZZZ");

To access these functions, navigate to an Edit Channel page > Channel Tasks panel > Edit Filter or
Edit Transformer. On the Reference tab, select the Category bar > Message Functions, among which
you will find the Create Segment functions. For additional information, see Reference List (on page
274).

Page 438 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Deleting a Segment

To delete a segment, use the JavaScript delete keyword: delete msg['ZZZ'];

In repeating segments, the above code deletes all instances of that segment. When you delete a segment,
all subsequent segments move up.

Iterating Over Message Segments

To iterate over all segments, follow this example:

for each (segment in msg.children()) {
if (segment.name().toString() == "ORC") {
// Do something…
}
}

To iterate through specifically named segments, use this formula:

for each (segment in msg..OBX) {
// Do something…
}

Note that although JavaScript can be used to iterate through message segments, you may find it easier to
use the Iterator Rule / Step (on page 288) instead.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 439 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Iterating Over Repeating Fields

To iterate over repeating fields, check the Handle Repetitions property. First, use the XML object’s
length() method to get the number of repetitions to iterate over: var reps = msg ['PV1]['PV1.7'].length();

Then, use a for each loop to iterate over repetitions:

for each (attendingDr in msg['PV1']['PV1.7']) {
lastName = attendingDr['PV1.7.2'].toString();
}

Note that although JavaScript can be used to iterate through repeating fields, you may find it easier to
use the Iterator Rule / Step instead.

Adding a New Repeating Field

To add a new repeating field, you need to create an XML object at the segment's field level. Populate the
fields, then link to the message at the repeating field level:

var newDr = new XML ("<PV1.7/>");
newDr ["PV1.7.1"] = "C3333";
newDr ["PV1.7.2"] = "Jones";
msg ['PV1']['PV1.7'] += newDr;

Message Variables

There are a wide variety of different JavaScript contexts throughout Mirth® Connect, and each have
various variables automatically available from the local scope. Here are some common examples:

•  message: A string that represents a raw inbound message in native format.

•  msg: An XML object that represents a transformed version of the inbound message.

•  tmp: An XML object that represents an outbound message template; available only if an

outbound template is defined.

•  connectorMessage: An instance of the ImmutableConnectorMessage Java class; an internal

representation of the message and its attributes.

Page 440 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Built-In Code Templates

These templates are used for function calls and code snippets for common JavaScript tasks. The
templates are available in all JavaScript contexts on the Reference tab using the drag-and-drop function
and can be extended with custom code templates. For additional information, see Reference List (on
page 274).

Using Java Classes

You can access any Java class in any JavaScript context:

var map = new Packages.java.util.LinkedHashMap();

The "Packages." at the beginning may be omitted for common top-level package domains, like "com",
"net", "org", and "java". To avoid having to type out the fully-qualified class name every time, you can
import the package:

importPackage(org.apache.commons.io);

FileUtils.getUserDirectory();

For classes in custom or non-standard libraries, create a resource (see Resources Settings Tab (on page
186)) containing your .jar file. Then include it on the channel in the Library Resources (on page 214)
dependencies tab.

Regular Expressions

These are used to search, match, or manipulate text strings based on patterns. In Mirth Connect regular
expressions may be used for:

•  String replacement in the Mapper and Message Builder transformer steps

•  File-reader filename filter patterns

•  Error-condition matching in Alerts

•  String methods in JavaScript contexts.

Regular expressions have their own set of rules.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 441 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Character

Character Name

Usage

Example

Vertical bar/pipe

Separates alternatives

this|that – matches “this” or “that"

|

( )

^

$

?

*

+

Parentheses

Caret

Dollar sign

Question mark

Asterisk/star

Plus

[ ]

Brackets

Groups characters
together

Matches characters only
at the start position

Matches characters only
at the end position

Indicates 0 instances or 1
instance of the previous
character

Indicates 0 instances or
multiple instances of the
previous character

Indicates 1 instance or
multiple instances of the
previous character

Denotes a set of
characters that match

(A|a)bc – matches “Abc” or “abc”

^ab – matches “abc” but not "lab"

Ab$ - matches “lab” but not “abc”

Ab?cd – matches “acd” or “abcd”

ab*c – matches “ac,” “abc,” “abbc,”
“abbbc,” etc.

ab+c – matches “abc,” “abbc,”
“abbbc,” etc.

[abc] – matches “a,” “b,” or “c”
[^abc] – matches any characters
except “a,” “b,” or “c” [a-d] –
matches “a,” b,” “c,” or “d”

When you use regular expressions in JavaScript, define them with the regular expression object or with
this syntax: /pattern/attributes. Regular expression objects take two strings: pattern and attributes.

Attributes include “g” (global) and “i” (case insensitive): var exp = new RegExp("abc", "gi");

The test() method matches an expression to a given string: found = exp.test("I know my abc's");

JavaScript string object methods that use regular expressions include: match(), replace(), search(),
split()

Example:

var myString = "Line1\nLine2\nLine3\n";
myString = myString.replace(/\n/g, "\r");

Page 442 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Logging with JavaScript

Use logger.info() and logger.error() to log information in JavaScript contexts: logger.info("The value
of x =" + x);

You can view the output of this method in the Mirth® Connect Dashboard's Server Log. The default
logging level, ERROR, is the quickest, having the least amount of overhead. INFO and DEBUG levels
give more details but have more overhead, so are somewhat slower. You can change the logging level
via the Mirth® Connect Server Manager for users who run the latest version of Mirth® Connect on a
Mac ( Applications > Mirth Connect > mcmanager) or PC:

Note: Mirth Appliance users can change the logging level from the Mirth® Connect Settings page
via Applications > Mirth Connect > Manage from the Appliance UI.

Generating a Hash with JavaScript

Use HashUtil.generate() to create a hex hash of a message passing through a channel in JavaScript
contexts: var hash = HashUtil.generate(message);

We provide 3 overloaded versions:


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 443 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

•  HashUtil.generate(Object)

•  Takes in any object and returns a hex hash of the object as a String. This defaults to
using the platform's default character encoding and the SHA-256 hashing algorithm.

•  Example

•  HashUtil.generate("Hello, World!")

•  HashUtil.generate(String, String, String)

•  Takes in data as a string, a character encoding as a string, and a hashing algorithm as a

string and returns a hex hash of the data as a string.

•  Example

•  HashUtil.generate("Hello, World!", "UTF-8", "SHA-25

6")

•  HashUtil.generate(byte[], String)

•  Takes in data as a byte[] and a hashing algorithm as a string and returns a hex hash of

the data as a string.

•  Example

•  HashUtil.generate("Hello, World!".getBytes(), "SHA-25

6")

Supported Character Encodings (for more information, please see documentation on Charset):

•  US-ASCII

•  ISO-8859-1

•  UTF-8

•  UTF-16BE

•  UTF-16LE

Page 444 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

•  UTF-16

Supported Algorithms (for more information, please see documentation on MessageDigest and
MessageDigest Algorithms):

•  MD2

•  MD5

•  SHA-1

•  SHA-224

•  SHA-256

•  SHA-384

•  SHA-512

•  SHA-512/224

•  SHA-512/256

Using the JavaScript Editor

Mirth® Connect has a rich JavaScript editor that includes automatic code completion, code folding,
multi-line tabs/comment toggles, auto-indentation, bracket matching, macros, and much more.

•  Using the Context Menu in the JavaScript Editor (on page 445)

•  Remapping Editor Shortcut Keys (on page 449)

•  Using the Auto-Completion Popup in the JavaScript Editor (on page 448)

Using the Context Menu in the JavaScript Editor

The Context menu contains items that let you Undo/Redo actions; Select/Copy/Cut/Paste/Delete code;
Find/Replace code; collapse/expand sections of code; and characterizes tabs/whitespace/line endings.

•  Finding/Replacing Code in the JavaScript Editor (on page 446)

•  Folding in the JavaScript Editor (on page 447)


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 445 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Finding/Replacing Code in the JavaScript Editor

You can use the Context menu to find code, to find and replace certain found code, or to find and replace
all found code.

1.  Navigate to the page that has the desired JavaScript Editor, and right-click / control+click in

the Editor.

2.  On the Context menu, select Find/Replace.

3.  On the Find/Replace dialog > Find text field, enter the code string you want to replace (in this

case, $co) .

Note: If you are merely searching for all incidents of &quot;$co&quot; in the code, select
the Find/Replace dialog's Find button > Close button. In the JS Editor, all incidents of

the entered string are highlighted.

4.  In the Replace with field, enter your replacement string (in this case, $c) .

5.  Configure other search filters as desired, and, depending on the situation, select the Replace

button (to replace only the first incident of the string) or the Replace All button (to replace all
incidents of the string).

Page 446 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Note: Mirth® Connect performs the selected action and highlights the strings in the
Editor.

6.  Select the Close button.

Folding in the JavaScript Editor

Folding, which refers to collapsing/expanding portions of code in the JavaScript Editor, can be
accomplished manually by selecting the + / - icon in the left margin of the JS Editor or from the Editor's
Context menu (which offers more ways to collapse/expand folds faster than you can do manually). Use
the following steps to collapse/expand a fold from the Context menu. (The procedures to perform the
other folding actions are similar.)

1.  Navigate to the page that has the desired JavaScript Editor, and select the first line of the desired

fold.

Note: You need to select the first line in the desired fold. which lines are distinguished by
a +/- icon in the grey margin of the JS Editor (previous graphic). If you do not select one

of these lines, the Editor does not collapse/expand the fold.

You can see how many lines of code are in a fold by moving the pointer into the grey margin
below a +/- icon, which action reveals a bracket that extends from the icon down to the last line
of the fold.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 447 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

2.  Right-click or control+click in the Editor.

3.  On the context menu, move the pointer over Folding, and select an option on the sub-menu (in

this case, Collapse Current Fold) .

Note: The Collapse Current Fold action is performed.

Using the Auto-Completion Popup in the
JavaScript Editor

The Auto-Completion popup simplifies channel coding, eliminating the need to go back and forth
between the JavaScript Editor and the User API (on page 459) or even the Message Template (on page
260) list on the same page as the Editor by putting all coding variables (e.g., code templates, classes,
     functions, variables) in one popup within the Editor itself. Accompanying the Auto-Completion popup is
     a descriptor window that displays, depending on the selected item, its name and various traits. To
     display the Auto-Completion popup, select on the beginning/end of a line in the JavaScript Editor, then
     hold down the ctrl / control button, and press the space bar. On the list, double-click the desired item to
     add it to the code in the Editor.

Page 448 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Note: When adding classes in the JavaScript Editor, double-click the desired class on the Auto-
Completion popup, and type a period after the class to reveal a list of its methods (that perform

specific actions), then double-click the desired method to add it to the Editor.

Remapping Editor Shortcut Keys

All editor shortcut key mappings can be changed from the Administrator Settings Tab (on page 174) in
the Settings View (on page 166). You only have to do this once, even if you log into multiple, separate
Mirth® Connect instances.

Variable Maps

Throughout the Message Processing Lifecycle (on page 68), your channels and messages have access to
various maps. Depending on the scope, the map may only be available in the current channel/connector,
or may be globally available across your entire system. These variable maps allow you to store a piece


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 449 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

of information that can be used later (in a downstream channel, connector, or somewhere else). A
common use for these variables is to provide easy drag-and-drop for connector properties. The
Destination Mappings (on page 253) list will display all available connector/channel map variables for
example. They are also used in other ways, such as populating Custom Metadata Columns (on page
230).

The following variable maps exist throughout Mirth® Connect:

Name

JavaScript Variable

Get/Put Shortcut Variable

Response Map (on page 452)

responseMap

Connector Map (on page 450)

connectorMap

Channel Map (on page 451)

channelMap

Source Map (on page 451)

sourceMap

Global Channel Map (on page 452)  globalChannelMap

Global Map (on page 453)

globalMap

Configuration Map (on page 453)

configurationMap

$r

$co

$c

$s

$gc

$g

$cfg

The table above also shows the precedence of these maps when referencing them in Velocity (on page
469) or when using the generic lookup function. For additional information, see The Variable Map
     Lookup Sequence (on page 454).

Connector Map

This map is isolated to the current message, and the current connector the message is processing
through. For example, if you store a connector map variable in Destination 1, you will not be able to
access that value in Destination 2. This is useful to avoid conflicts among common variable names, and
to reduce message storage.

•  Get connector map value:

•  var value = connectorMap.get('key');

•  var value = $co('key');

•  Put connector map value:

•  connectorMap.put('key', 'value');

Page 450 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

•  $co('key', 'value');

Channel Map

This map is isolated to the current message as it processes through a channel. If you store a connector
map variable in the source connector, you will have access to that value in all subsequent destinations.
However when the current message finishes and the next one begins, that next message will not have
access to the value you stored for the previous message.

The channel map is useful for anything that needs to be shared among multiple destinations, or the
source connector and all destinations. For example, you might have one HTTP Sender (on page 400)
destination that makes a request to a remote service, and then in the Response Transformer (on page
286) you store a particular response HTTP header in the channel map. As long as the next destination
     connector is in the same chain (on page 68), it will have access to that channel map variable, and can do
     something else with it, like include it on a subsequent HTTP request.

•  Get channel map value:

•  var value = channelMap.get('key');

•  var value = $c('key');

•  Put channel map value:

•  channelMap.put('key', 'value');

•  $c('key', 'value');

Source Map

This map is isolated to the current message as it processes through a channel. Unlike the channel map
however, this one is read only. The Source Connector (on page 340) or an upstream process can inject
source map variables. For example, the File Reader (on page 351) will automatically inject the
"originalFilename" variable

•  Get source map value:

•  var value = sourceMap.get('key');

•  var value = $s('key');


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 451 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Response Map

This map is isolated to the current message as it processes through a channel. Unlike the channel map,
this one is specifically used for storing Response objects. When a destination finishes processing, its
Response will automatically be stored in the response map. Subsequent destinations and the
Postprocessor Script (on page 255) will have access to these values. The source connector can also use
values stored in the response map to send responses back to the originating system (set in the Source
Settings (on page 238)) .

•  Get response map value:

•  var value = responseMap.get('key');

•  var value = $r('key');

•  Put response map value:

•  responseMap.put('key', 'value');

•  $r('key', 'value');

Global Channel Map

This map is isolated to a specific channel, but across multiple messages. That means you can store a
value during a message processing lifecycle, and it will be available during the lifecycle of the next
message. You can also store global channel map values in the channel scripts (on page 255).

This map is useful for storing stateful, non-serializable objects like a database Connection. It is in-
memory only, meaning that if Mirth® Connect is restarted, the entries in this map are not preserved
anywhere. It is also a concurrent map, which means that "null" values cannot be stored in it.

•  Get global channel map value:

•  var value = globalChannelMap.get('key');

•  var value = $gc('key');

•  Put global channel map value:

•  globalChannelMap.put('key', 'value');

•  $gc('key', 'value');

Page 452 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Note: By default the "Clear global channel map on deploy" option is enabled on the Summary Tab
(on page 204). You may want to uncheck this if you want the global channel map to remain

unchanged when you redeploy the channel.

Global Map

This map is available across your entire server, across all channels and all messages. That means you
can store a value during message processing in one channel, and use that value from a different channel,
or somewhere else like an Alert (on page 313). You can also store global map values in the global scripts
(on page 299).

Like the global channel map, this map is useful for storing stateful, non-serializable objects like a
database Connection. It is in-memory only, meaning that if Mirth® Connect is restarted, the entries in
this map are not preserved anywhere. It is also a concurrent map, which means that "null" values cannot
be stored in it.

•  Get global map value:

•  var value = globalMap.get('key');

•  var value = $g('key');

•  Put global map value:

•  globalMap.put('key', 'value');

•  $g('key', 'value');

Note: By default the "Clear global map on redeploy" option is enabled on the Server Settings Tab
(on page 167). You may want to disable this if you want the global map to remain unchanged

when you redeploy all channels.

Configuration Map

This map is also available across your entire server, across all channels and all messages. Like the
global map, that means you can use the values from the configuration map in any channel, or
somewhere else like an Alert (on page 313). Unlike the global map however, this map is editable only
from the Configuration Map Settings Tab (on page 182), and is read-only from the perspective of
channels / messages. The values are also String key/values only.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 453 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

This map is useful for global, static settings you want to persist across restarts of Mirth Connect. For
example, you could store a variable like "clientAdtPort" and then use Velocity (on page 469) to
reference that variable ("${clientAdtPort}") in a TCP Listener (on page 372). That way you can export
the channel on one Mirth® Connect installation, import it into a completely different installation, and
then you would not have to edit anything in the channel settings as long as the configuration map is set
on both instances.

•  Get configuration map value:

•  var value = configurationMap.get('key');

•  var value = $cfg('key');

The Variable Map Lookup Sequence

In many cases when referencing map variables in Mirth® Connect, you do not call out to a specific map,
but instead use the generic lookup function:

•  var value = $('variableName');

Or, you might reference a variable using Velocity Variable Replacement (on page 469):

•  ${variableName}

When you do this, Mirth® Connect will automatically look that key up in all available maps. That may
only be the configuration/global map (in the case of the global scripts (on page 299)) , or it may be all
maps (in the case of a filter / transformer script (on page 257)) . This sequence is followed:

•  Response Map

•  Connector Map

•  Channel Map

•  Source Map

•  Global Channel Map

•  Global Map

•  Configuration Map

Page 454 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

For example, if you have stored a variable called "dataSource" in both the Connector Map and the
Global Channel Map, the one from the Connector Map will be used. If you want the value specifically
from the Global Channel Map instead, you can use the map-specific get function shown in the Variable
Maps (on page 449) section:

•  var dataSource = globalChannelMap.get('dataSource');

Attachment JavaScript Functions

Attachment Handlers (on page 219) are used to extract a portion of a message (or the entire message)
and store it separately as an attachment. The portion of the message that was extracted is replaced with
an attachment replacement token. When you use a destination connector to send a message downstream,
Mirth® Connect will automatically take the message and replace any attachment tokens with the actual
attachment data (unless you have Reattach Attachments turned off in the Destination Settings (on page
249)) .

Throughout the message lifecycle you can retrieve, modify, and add new attachments with built-in
helper functions.

•  Built-In Attachment Functions (on page 455)

•  The AttachmentUtil Class (on page 457)

•  The Attachment Object (on page 458)

•  Examples (on page 459)

Built-In Attachment Functions

•  getAttachmentIds()

•  Get a List containing the IDs of all Attachments associated with this message. Uses the

current connectorMessage variable.

•  getAttachmentIds(channelId, messageId)

•  Get a List containing the IDs of all Attachments associated with any channel / message.

channelId

The ID of the channel associated with the
attachments.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 455 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

messageId

The ID of the message associated with the
attachments.

•  getAttachments(base64Decode)

•  Get List of Attachments associated with this message. This will get all attachments that

have been added in the source and destination(s).

base64Decode

If true, the content of each attachment will first
be Base64 decoded for convenience.

•  getAttachment(attachmentId, base64Decode)

•  Get a specific Attachment associated with this message. Uses the current

connectorMessage variable.

attachmentId

•

base64Decode

The ID of the attachment to retrieve.

If true, the content of the attachment will first be
Base64 decoded for convenience.

•  getAttachment(channelId, messageId, attachmentId, base64Decode)

•  Get a specific Attachment associated with any channel / message. You can use this to

retrieve an attachment from a completely different channel.

channelId

messageId

attachmentId

base64Decode

The ID of the channel to retrieve the attachment
from.

The ID of the message to retrieve the
attachment from.

The ID of the attachment to retrieve.

If true, the content of the attachment will first be
Base64 decoded for convenience.

•  addAttachment(data, type, base64Encode)

•  Add attachment (String or byte[]) to the current message.

data

The data to insert as an attachment. May be a
string or byte array.

Page 456 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

type

base64Encode

The MIME type of the attachment.

If true, the content will be Base64 encoded for
convenience. If the content you are passing in is
not already Base64 encoded, you should pass
in true for this argument.

•  updateAttachment(attachment, base64Encode)

•  updateAttachment(attachmentId, data, type, base64Encode)

•  updateAttachment(channelId, messageId, attachment, base64Encode)

•  updateAttachment(channelId, messageId, attachmentId, data, type, base64Encode)

•  Updates an attachment associated with the current connector message, or with any

message from any channel.

attachment

attachmentId

channelId

messageId

data

type

base64Encode

The The Attachment Object (on page 458) to
update.

The unique ID of the attachment to update.

The ID of the channel the attachment is
associated with.

The ID of the message the attachment is
associated with.

The attachment content (must be a string or
byte array).

The MIME type of the attachment.

If true, the content will be Base64 encoded for
convenience. If the content you are passing in is
not already Base64 encoded, you should pass
in true for this argument.

The AttachmentUtil Class

All of the Built-In Attachment Functions (on page 455) are also available from the AttachmentUtil
utility class available from the User API (on page 459). In addition to this, AttachmentUtil has some
extra methods available that you can use to re-attach attachment data into a message string.

For more information, check out the AttachmentUtil class in your locally hosted User API: The User


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 457 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

API (Javadoc) (on page 459)

You can also view the documentation for this class here: http://javadocs.mirthcorp.com/connect/3.6.0/
user-api/com/mirth/connect/server/userutil/AttachmentUtil.html

The Attachment Object

When using these methods, you will likely be working with a special class called Attachment. This class
is documented in the User API (on page 459). A few of the available methods are documented here:

Create a new Attachment

•  new Attachment()

•  new Attachment(id, content, type)

•  new Attachment(id, content, charset, type)

id

content

charset

The unique ID of the attachment.

The content (String or byte array) to store for the attachment.

If the content passed in is a string, this is the charset encoding to convert the
string to bytes with. If the content is a String and a charset is not used, UTF-8 will
be used as the charset.

type

The MIME type of the attachment.

Retrieve Attachment Content

Note: When you retrieve an attachment using the Built-In Attachment Functions (on page 455) or
The AttachmentUtil Class (on page 457), you may want to pass in true for the base64Decode

argument so that the attachment content is already decoded.

•  getContent()

•  Retrieves the raw byte array content of the attachment. Note that this may be the actual
raw bytes of the attachment, or it may be the Base64 byte array representation. See the
note above.

•  getContentString()

•  getContentString(charset)

Page 458 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

•  Returns the content of the attachment as a string, using the specified charset encoding.
If not specified, UTF-8 will be used. Note that this may be the raw string value of the
attachment, or it may be the Base64 string value. See the note above.

Examples

Modify a previously attached CCD attachment:

var ccd = new XML(getAttachment(attachmentId, true).getContentStrin
g());

ccd.id.@root = 'testing';

updateAttachment(attachmentId, ccd.toString(), 'text/xml', true);

Read in a PDF from the filesystem and add it as an attachment:

var fileBytes = FileUtil.readBytes('/path/to/file.pdf');

// Pass in true for Base64Encode, since the content isn't already Bas
e64 encoded
addAttachment(fileBytes, 'application/pdf', true);

The User API (Javadoc)

The user API (application programming interface) is a collection of Java classes and methods that helps
you interact with channels and the message that is being processed. It also provides helper methods for
common tasks such as date formatting.

Wherever you are editing JavaScript code in Mirth® Connect, you can view the API by selecting the
View User API function in the Other panel, which is available on all Mirth® Connect Administrator
views.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 459 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

You can also right-click/command+click in the text area of any JavaScript Editor, and on the drop-
down menu, select View User API.

The Javadoc appears in your default web browser, in which you can select classes to view their method
signatures and descriptions.

Page 460 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Note: You can also view the API via this link: http://javadocs.mirthcorp.com/connect/3.6.0/user-
api/

Mirth Connect Debugger

The Mirth Connect Debugger tool allows you to troubleshoot JavaScript errors throughout the Mirth®
Connect application. The Debugger can be used for the following scripts and connectors:

•  Deploy, Undeploy, Preprocessor, and Post Processor scripts

•  Attachment/Batch scripts

•  Source Connectors:

•  Filters/Transformers

•  Database Reader and JavaScript Reader types


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 461 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

•  Destination Connectors:

•  Filters/Transformers.

•  Database Writer and JavaScript Writer types

•  Response transformers

Before You Begin

If your Java application does not interact directly with a user, use the following steps to change the
Headless mode to false. Setting this mode to false means that your Java application:

•  Displays windows or dialog boxes.

•  Accepts keyboard or mouse input.

•  Uses any heavyweight AWT (Abstract Windowing Toolkit) components.

To Edit the mcserver.vmoptions file

1.  Access the folder in which Mirth® Connect is installed.

Note: The mcserver.vmoptions file can typically be found in the following location:

•  Windows: C:\Program Files\Mirth® Connect\mcserver.vmoptions

•  MacOS: /Applications/Mirth® Connect/mcserver.vmoptions

2.  Open the mcserver.vmoptions file.

3.  Set -Djava.awt.headless=false.

4.  Save the updated file.

Page 462 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Use the Debugger

Note:

•  mcservice.exe is currently not supported for Debug mode.

•  Debug mode is not recommended for Clustering environment (Basic or Advanced Clustering).

1.  Within Mirth® Connect, select Channels.
2.  In the Channels list, select the channel you would like to debug.

Note: You can currently only debug one channel at a time.

3.  In Channel Tasks, select Debug Channel.

4.  On the Debug Channel window, select the channel script(s) you would like to use to debug the

selected channel.

5.  Select OK. A separate debugger window opens for each selected script, and for each destination

connector.

6.  Process the selected channel (For Example: send a message). The Debugger window(s) will

pop-up based on the options you selected.

7.  To exit the debug mode, close the debugger window(s) or undeploy the channel.


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 463 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Note: The channel will not stop or undeploy properly if the debugger is waiting for user
operation, such as selecting “Go”.

Debugger Window

The following context-specific tasks are available:

Number

1

Name

Description

Title And Menu Bar

The Title identifies the Chanel ID and the Event
Name or Connector Identifier for the selected
channel.

The Menu Bar allows you to perform actions on the

Page 464 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Number

Name

Description

Debugger window.

2

3

4

5

Coding Area

Local Variables Area

Expressions Area

The Coding area displays Mirth® Connect code and
custom JavaScript associated with the channel
debugger

The Local Variables area shows the values and
values and the current state of local variables
associated with the debugger results.

The Expressions area allows you to evaluate an
expression to determine what a value is. You can
also evaluate a snippet of code in this area.

Thread Information Bar

The Thread Information bar identifies the following
information:

•  Connector type (Ex: JavaScript Writer)

•  Channel Name (Ex: “Debugger Channel”)

•  Channel ID

•  Location of JavaScript (Ex: destination
“Physician (2)” where “Physician” is the
name of a destination connector and “2” is
the destination number)

Debugger Menus

File Menu

Name

Open

Description

Opens a dialog which allows you to select JavaScript from the system which


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 465 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Name

Description

is pulled into a new window in the debugger workspace.

Allows you to select JavaScript from the system which is pulled into the
debugger window for execution. The new test interruption point will be the
next executable line.

Finishes the current test and closes the debugger window.

Run

Exit

Edit Menu

Name

Cut

Copy

Paste

Description

Copies the highlighted text to the system clipboard and removes the text
from the debugger window.

Copies the highlighted text to the system clipboard.

Places the system clipboard text into the current location of the cursor in the
debugger window.

Go to function...

Moves the cursor to a specific named function within the script.

Go to line...

Moves the cursor to a specific line within the script.

Page 466 of 619 Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. March 10, 2025

Debug Menu

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Name

Break

Go

Step Into

Step Over

Description

Pauses to interrupt the running code in the test.

Continues running the test until the end or to the next breakpoint.

Increments the execution by one line. If the next line is inside a function,
then the interruption will step into the function allowing the view of the
context within.

If the current function has been called by another function, then Step Over
returns to the previous function. Otherwise it continues running the test to
the end or to the next breakpoint (like the Go function).

Step Out

Progresses the execution to the next line outside of the current function.

Break on Exceptions

Continues running the test script until an exception occurs.

Break on Function Enter

Continues and then breaks once you reach a new function.

Break on Function Return

Continues and then breaks once you reach the end of the current function.

Window Menu


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 467 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

Name

Cascade

Tile

Console

Coding Area

Description

Stacks all windows with the debugger work space.

Places all windows side-by-side in the debugger work space.

Brings focus to the Console Window. The Console Window allows users to
perform small scripts, test the state of variables, and display logs.

Number

1

2

3

Name

Title

Internal Code

Description

The Title identifies the Chanel ID and the Event Name
or Connector Identifier for the selected channel.

This is the thread interruption point which appears
where the internal code of the debug option starts (i.e.,
the “importClass – function() ”. line). As you test your
script, you can see the state of various objects and
variables in the lines prior to the interruption line.

Custom JavaScript

Any custom JavaScript displays normally within the
“doScript() ” boundary.


Velocity Variable Replacement

The Apache Velocity template engine is used throughout Mirth® Connect to allow dynamic variables to
be injected into property fields. For example, you can use a transformer to programmatically select a
TCP address/port to send to and store those values in the channel map (on page 449), and then use a
Velocity template to inject those variables into the TCP Sender (on page 416) settings.

•  Basic Syntax (on page 469)

•  Conditional Statements (on page 470)

•  For Loops (on page 470)

Basic Syntax

Basic syntax for a Velocity reference is as follows:

${variableName}

The brackets may be omitted if the identifier starts with a letter and contains only letters, numbers,
hyphens, or underscores:

$variableName

The variable will be looked up in all available maps, according to The Variable Map Lookup Sequence
(on page 454). The string representation of the value will then replace the Velocity reference. You can
also access properties and methods from context variables:

${myArray.length}
${myList.size()}
${myObject.customMethod('param')}

If a property has a corresponding getter method (like getValue()), the engine will automatically find that
method when you attempt to access the property. Therefore these may be equivalent:

${myObject.value}


Confidential - Proprietary Information - For Use By Authorized Company Clients Only. Do Not Distribute. Page 469 of 619

User Guide for Mirth® Connect by NextGen Healthcare, 4.5

${myObject.getValue()}

If the context variable doesn't exist, or if the value returned by the reference evaluation is null, no
replacement will be done, so the final template will still have your "${varName}" string within. In these
cases you can put an exclamation mark after the dollar sign to tell the engine to replace null values with
an empty string instead:

$!{thisValueIsNull}

Conditional Statements

Velocity supports if..else statements, with the #if / #elseif / #else directives:

There #if($list.size()==1)is#{else}are#end ${list.size()} total valu
e#if($list.size()!=1)s#end

The curly brackets ("{}") are only needed if the if/else/end might be confused with template data
immediately before or after.

For Loops

Velocity supports iterating through Lists / Collections / Arrays with the #foreach directive:

<table>
#foreach ($item in $list)
    <tr>
        <td>${item.name}</td>
    <tr>
#end
</table>







