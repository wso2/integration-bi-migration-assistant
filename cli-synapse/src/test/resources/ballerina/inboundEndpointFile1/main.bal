import ballerina/file;

configurable string fileInboundPath = "/data/in";

public listener file:Listener fileInboundListener = new (
    path = fileInboundPath,
    recursive = false
);

// Synapse VFS inbound endpoints process each discovered file exactly once. file:Listener only reports files created after it starts, so pre-existing files are instead handled by a one-time directory scan in this project's init() function. A file created in the brief window between that scan and this listener actually starting, or one still being written when detected, may be missed or read prematurely - file:Listener has no equivalent to Synapse's file-locking/stability checks.
service on fileInboundListener {
    remote function onCreate(file:FileEvent event) returns error? {
        return fileInboundProcessFile(event.name);
    }
}
